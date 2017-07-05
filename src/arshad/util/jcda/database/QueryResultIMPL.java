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
import arshad.util.jcda.exceptions.QueryResultException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is an implementation for QueryResult class.
 * 
 * @author Arshad
 */
public class QueryResultIMPL implements QueryResult {
    private final String[] columnNames;
    private final HashMap<String, ArrayList<Object>> columnData;
    
    private int rowCount;
    private int currentPosition;
    
    /**
     * Constructor for the QueryResult implementation class. Takes an array of column names as parameter.
     * 
     * @param columnNames Names of the columns
     */
    public QueryResultIMPL(String[] columnNames) {
        this.columnNames = columnNames;
        this.rowCount = 0;
        this.currentPosition = -1;
        columnData = new HashMap<>();
        for(String name : columnNames) {
            columnData.put(name, new ArrayList<>());
        }
    }
    
    /**
     * Add data to the end of row. Data is added based on column names.
     * 
     * @param columnName Name of the column
     * @param data Data related to the column name
     */
    public void addByColumnName(String columnName, Object data) {
        columnData.get(columnName).add(data);
        rowCount++;
    }
    
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    @Override
    public String[] getColumnNames() {
        return columnNames;
    }
    
    /**
     * Remove extra space allocation in this object. Used for saving RAM.
     */
    public void trimToSize() {
        for(String name : columnNames) {
            columnData.get(name).trimToSize();
        }
    }

    /**
     * Checks if next row is present or not.
     * 
     * @return Returns true if next row is present, false otherwise
     */
    public boolean hasNext() {
        return currentPosition < rowCount;
    }
    
    @Override
    public boolean next() {
        currentPosition++;
        return hasNext();
    }
    
    @Override
    public void resetRowPosition() {
        currentPosition = 0;
    }

    @Override
    public Object getByColumnName(String columnName){
        if(!hasNext()) {
            return null;
        }
        if(currentPosition < 0) {
            Logger.getLogger(QueryResult.class.getName()).log(Level.SEVERE, null, new QueryResultException("next() "
                + "method must be called once before accessing row data"));
            return null;
        }
        return columnData.get(columnName).get(currentPosition);
    }
}
