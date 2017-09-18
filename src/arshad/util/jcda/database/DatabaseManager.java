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

package arshad.util.jcda.database;

import arshad.util.jcda.database.interfaces.DatabaseAccess;
import arshad.util.jcda.database.interfaces.DatabaseConfig;

/**
 * Manages database instances for different types of databases.
 * @author Arshad
 */
public class DatabaseManager {
    
    private static DatabaseManager dbManager = null;
    
    /**
     * This constructor has been made private this disabling normal object creation. This object can be
     * created by a static method.
     */
    private DatabaseManager() {
    }
    
    /**
     * Returns the default instance of this class. This class creates a single instance and that instance
     * is used by all other counterparts. This class is only needed to create database controller class
     * of different databases.
     * 
     * @return Returns default instance of this class
     */
    public static DatabaseManager getDefault() {
        synchronized(DatabaseManager.class) {
            if(dbManager == null) {
                dbManager = new DatabaseManager();
            }
        }
        return dbManager;
    }
    
    /**
     * Get DatabaseAccess object to communicate with the database. Returns object that is specific
     * for the configuration database type.
     * 
     * @param config Database Configuration class
     * @return Returns DatabaseAccess object
     */
    public synchronized DatabaseAccess config(DatabaseConfig config) {
        switch(config.TYPE()) {
            case MySQL: {
                return new MySQLDatabase(config.NAME(), config.IP(), config.PORT(), config.USERNAME(), config.PASSRORD());
            }
            case SQLite: {
                return new SQLiteDatabase(config.FILENAME());
            }
        }
        return null;              
    }
}
