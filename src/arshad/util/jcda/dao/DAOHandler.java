//    Copyright (C) 2017 MD. Ibrahim Khan
//
//    Project Name: 
//    Author: MD. Ibrahim Khan
//    Author's Email: ib.arshad777@gmail.com
//
//    Redistribution and use in source and binary forms, with or without modification,
//    are permitted provided that the following conditions are met:
//
//    1. Redistributions of source code must retain the above copyright notice, this
//       list of conditions and the following disclaimer.
//
//    2. Redistributions in binary form must reproduce the above copyright notice, this
//       list of conditions and the following disclaimer in the documentation and/or
//       other materials provided with the distribution.
//
//    3. Neither the name of the copyright holder nor the names of the contributors may
//       be used to endorse or promote products derived from this software without
//       specific prior written permission.
//
//    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
//    ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
//    WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
//    IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
//    INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING
//    BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
//    DATA, OR PROFITS; OR BUSINESS INTERRUPTIONS) HOWEVER CAUSED AND ON ANY THEORY OF
//    LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
//    OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
//    OF THE POSSIBILITY OF SUCH DAMAGE.

package arshad.util.jcda.dao;

import arshad.util.jcda.access.Database;
import arshad.util.jcda.annotations.Bind;
import arshad.util.jcda.annotations.Query;
import arshad.util.jcda.annotations.ReturnsResult;
import arshad.util.jcda.exceptions.BindAnnotationException;
import arshad.util.jcda.exceptions.NoQueryAnnotationException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * This class handles the Data Access Object (DAO) method calls.
 * 
 * @author Arshad
 */
public class DAOHandler implements InvocationHandler {
    
    private final Database database;
    private final Object controlObject = new Object();
    
    /**
     * Constructor for the handler.
     * @param db Database instance of the library
     */
    public DAOHandler(Database db) {
        database = db;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Annotation ann = method.getAnnotation(Query.class);
        if(ann == null) {
            throw new NoQueryAnnotationException("Method must have Query Annotation.");
        }
        String query = ((Query)ann).sql();
        int token = ((Query)ann).token();
        
        if(args.length > 0) {
            Annotation[][] paramAnn = method.getParameterAnnotations();
            
            int count = 0;
            for(Annotation[] params : paramAnn) {
                for(Annotation pAnn : params) {
                    if(params.length > 1) {
                        throw new BindAnnotationException("Using multiple Bind in the same parameter is not permitted");
                    }
                    if(pAnn instanceof Bind) {
                        String parts[] = query.split(":" + ((Bind) pAnn).value() + ":");
                        if(parts.length != 2) {
                            throw new BindAnnotationException("Using same Bind for multiple parameters is not permitted");
                        } else {
                            query = parts[0] + args[count] + parts[1];
                        }
                    }
                }
                count++;
            }
        }
        
        Annotation returnOpt = method.getAnnotation(ReturnsResult.class);
        if(returnOpt != null) {
            synchronized(controlObject) {
                database.addQuery(token, query, true);
            }
        } else {
            synchronized(controlObject) {
                database.addQuery(token, query, false);
            }
        }
        return null;
    }
}
