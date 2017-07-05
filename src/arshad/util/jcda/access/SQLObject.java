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

package arshad.util.jcda.access;

/**
 * This class holds necessary informations to execute a query an take necessary steps
 * 
 * @author Arshad
 */
public class SQLObject {
    private String query = null;
    private int token = 0;
    private boolean hasReturnVal = false;
    
    /**
     * Constructor for SQLObject class. This class creates instance with the data
     * provided in the constructor. These data can not be changed later.
     * 
     * @param query SQL Query
     * @param token Token number to identify the query
     * @param hasReturnVal Weather this query has return value
     */
    public SQLObject(String query, int token, boolean hasReturnVal) {
        this.query = query;
        this.token = token;
        this.hasReturnVal = hasReturnVal;
    }

    /**
     * Get the query of this Object given in the constructor
     * @return Query of this Object
     */
    public String getQuery() {
        return query;
    }

    /**
     * Get the token of this Object given in the constructor
     * @return Token of this Object
     */
    public int getToken() {
        return token;
    }
    
    /**
     * Get weather this query object has return value, or not, given in the constructor
     * @return Returns true if query has return value, otherwise false
     */
    public boolean getHasReturnVal() {
        return hasReturnVal;
    }
}
