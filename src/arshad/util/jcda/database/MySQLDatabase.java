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

import arshad.util.jcda.database.interfaces.QueryResult;
import arshad.util.jcda.database.interfaces.DatabaseAccess;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Database instance to use MySQL database.
 * @author Arshad
 */
public final class MySQLDatabase implements DatabaseAccess {
    
    private String DB_NAME = "ast_device_data";
    private String DB_IP = "localhost";
    private String DB_PORT = "3306";
    private String DB_USERNAME = "root";
    private String DB_PASSWORD = "";
    
    private Connection dbConn;
    private Statement dbStmt;
    private ResultSet dbRs;
    
    private boolean isConnected = false;
    
    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Constructor for MySQLDatabase class. This class handles everything related to MySQL database.
     * 
     * @param dbName Database name
     * @param dbIP IP or address of the host
     * @param dbPort Port to connect to the database. (Ex. 3306)
     * @param user Username for the database access
     * @param pass Password for the database access
     */
    public MySQLDatabase(String dbName, String dbIP, String dbPort, String user, String pass) {
        DB_NAME = dbName;
        DB_IP = dbIP;
        DB_PORT = dbPort;
        DB_USERNAME = user;
        DB_PASSWORD = pass;
    }
    
    @Override
    public boolean checkConnection() {
        if(dbStmt == null) {
            return false;
        }
        try {
            dbStmt.executeQuery("SELECT * FROM `user`;");
            isConnected = true;
            return true;
        } catch (SQLException ex) {
            isConnected = false;
            return false;
        }
    }
    
    @Override
    public void connect() {
        if(!isConnected) {
            try {
                dbConn = (Connection) DriverManager.getConnection("jdbc:mysql://" + DB_IP + ":" + DB_PORT + "/" + DB_NAME, DB_USERNAME, DB_PASSWORD);
                dbStmt = (Statement) dbConn.createStatement();
                isConnected = true;
            } catch (SQLException ex) {
                Logger.getLogger(MySQLDatabase.class.getName()).log(Level.SEVERE, null, ex);
                isConnected = false;
            }
        }
    }

    @Override
    public boolean execute(String sql) {
        if(!checkConnection()) {
            connect();
        }
        
        try {
            dbRs = dbStmt.executeQuery(sql);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(MySQLDatabase.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public <T extends QueryResult> T executeQuery(String sql) {
        if(!checkConnection()) {
            connect();
        }
        
        if(dbStmt == null) {
            return null;
        }
        
        try {
            dbRs = dbStmt.executeQuery(sql);
            ResultSetMetaData metaData = dbRs.getMetaData();
            
            int columns = metaData.getColumnCount();
            String[] columnNames = new String[columns];
            for(int i = 1; i <= columns; i++) {
                columnNames[i - 1] = metaData.getColumnLabel(i);
            }
            QueryResultIMPL retVal = new QueryResultIMPL(columnNames);
            while(dbRs.next()) {
                for(String name : columnNames) {
                    retVal.addByColumnName(name, dbRs.getObject(name));
                }
            }
            retVal.trimToSize();
            
            return (T) retVal;
        } catch (SQLException ex) {
            Logger.getLogger(MySQLDatabase.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
