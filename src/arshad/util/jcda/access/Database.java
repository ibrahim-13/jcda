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

import arshad.util.jcda.broadcast.interfaces.BroadcastReceiver;
import arshad.util.jcda.dao.DAOHandler;
import arshad.util.jcda.database.DatabaseManager;
import arshad.util.jcda.database.interfaces.QueryResult;
import arshad.util.jcda.database.interfaces.DatabaseAccess;
import arshad.util.jcda.database.interfaces.DatabaseConfig;
import arshad.util.jcda.exceptions.DatabaseThreadException;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the MAIN class that is used by to user to access this whole library
 * 
 * @author Arshad
 */
public class Database implements Runnable {
    private DatabaseAccess dbAccess = null;
    private final ArrayList<BroadcastReceiver> receiverList;
    private final Object receiverControl = new Object();
    
    /**
     * Thread that will execute the queries
     */
    public Thread t = null;
    private final String dbThreadName = "DatabaseAccessThread";
    private final Object threadControl = new Object();
    private boolean threadRun = false;
    
    private Queue<SQLObject> sqlQueue = null;
    private final Object queueControl = new Object();
    
    /**
     * Constructor for the Database class. This class is mother of all class.
     * 
     * @param config Database configuration
     * @param receiver Instance that will receive the result broadcast
     */
    public Database(DatabaseConfig config, BroadcastReceiver receiver) {
        dbAccess = DatabaseManager.getDefault().config(config);
        receiverList = new ArrayList<>();
        receiverList.add(receiver);
        sqlQueue = new PriorityQueue<>();
    }
    
    /**
     * Start database Thread. This Thread will execute the queries and deliver to the braodcast receivers
     */
    public void initDatabase() {
        this.threadRun = true;
        this.t = new Thread(this, dbThreadName);
        t.setDaemon(true);
        t.start();
    }
    
    /**
     * Shutdown database Thread. Use before closing down the main application
     */
    public void shutdown() {
        this.threadRun = false;
        t.interrupt();
        synchronized(threadControl) {
            threadControl.notify();
        }
    }
    
    /**
     * Get DAO (Data Access Object) for the given interface. It will construct an instance
     * implementing the given interface class and return the instance for calling the queries.
     * This interface must apply proper annotations to specify the SQL query and other properties.
     * 
     * @param <T> Custom interface type user's interface
     * @param daoInterface Class of the user's interface
     * @return 
     */
    public <T> T getDAO(Class<T> daoInterface) {
        Class[] classes = { daoInterface };
        return (T) Proxy.newProxyInstance(daoInterface.getClassLoader(), classes, new DAOHandler(this));
    }
    
    /**
     * Add a query to the query processing queue. No need to use this if you are using DAO.
     * 
     * @param token Token number to identify this specific query.
     * @param sql SQL query to be processed
     * @param hasReturnVal Weather this query will return data back through the broadcast, or not
     */
    public void addQuery(int token, String sql, boolean hasReturnVal) {
        if(!threadRun) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, new DatabaseThreadException("Database thread"
                    + " not running. Start thread with init() method"));
            return;
        }
        synchronized(queueControl) {
            sqlQueue.add(new SQLObject(sql, token, hasReturnVal));
        }
        synchronized(threadControl) {
            threadControl.notify();
        }
    }
    
    @Override
    public void run() {
        while(true) {
            synchronized(threadControl) {
                try {
                    threadControl.wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
                    continue;
                }
                if(!threadRun) {
                    break;
                }
                SQLObject obj;
                synchronized(queueControl) {
                    obj = sqlQueue.poll();
                }
                if(obj != null) {
                    if(obj.getToken() <= 0) {
                        synchronized(receiverControl) {
                            receiverList.forEach((recv) -> {
                                recv.onDBResultReceive(obj.getToken(), null, BroadcastReceiver.RESULT_CODE.INVALID_TOKEN);
                            });
                            continue;
                        }
                    }
                    if(!obj.getHasReturnVal()) {
                        dbAccess.execute(obj.getQuery());
                    } else {
                        synchronized(receiverControl) {
                            for(BroadcastReceiver recv : receiverList) {
                                if(recv == null) {
                                    System.out.println("No receiver found ! Add receiver to receive query result");
                                    System.out.println("Query Token : " + obj.getToken());
                                    continue;
                                }
                                QueryResult ret = dbAccess.executeQuery(obj.getQuery());
                                if(ret != null) {
                                    recv.onDBResultReceive(obj.getToken(), ret, BroadcastReceiver.RESULT_CODE.OK);
                                } else {
                                    recv.onDBResultReceive(obj.getToken(), null, BroadcastReceiver.RESULT_CODE.COMMUNICATION_ERROR);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
