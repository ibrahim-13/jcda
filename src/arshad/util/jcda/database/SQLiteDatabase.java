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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import arshad.util.jcda.database.interfaces.DatabaseAccess;
import arshad.util.jcda.database.interfaces.QueryResult;
import java.sql.ResultSetMetaData;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Database instance to use SQLite database.
 * @author Arshad
 */
public class SQLiteDatabase implements DatabaseAccess {
    
    private final String dbFileLocation;
    
    static {
        try {
            Class.forName("org.sqlite.JDBC").newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }
    
    private Connection dbConn;
    private Statement dbStmt;
    private ResultSet dbRs;
    
    private boolean isConnected = false;
    
    /**
     * Constructor for SQLiteDatabase class. This class handles everything related to SQLite database.
     * 
     * @param dbName Database file name or location
     */
    public SQLiteDatabase(String dbName) {
        this.dbFileLocation = dbName;
    }

    @Override
    public boolean checkConnection() {
        if(dbStmt == null) {
            return false;
        }
        try {
            dbStmt.executeQuery("SELECT 1;");
            if(dbConn.isClosed()) {
                isConnected = false;
            }
            isConnected = true;
        } catch (SQLException ex) {
            isConnected = false;
        }
        return isConnected;
    }

    @Override
    public void connect() {
        if(!isConnected) {
            try {
                dbConn = (Connection) DriverManager.getConnection("jdbc:sqlite:" + dbFileLocation);
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
            dbStmt.execute(sql);
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
