/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hwt.meter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 *
 * @author HWT
 */
public class SQLite {
    public static Logger LOGGER = LogManager.getLogger(SQLite.class);
    private static Connection connection = null;
    
    public static void connect() {
        LOGGER.info("Start: AAA");        
        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:./aa.db";
            connection = DriverManager.getConnection(url);            
            LOGGER.info("Connection to SQLite has been estiblished.");            
        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.error(Arrays.toString(e.getStackTrace()));            
        } 
        LOGGER.info("End: AAA");        
    }
    
    public static void close(){
        try {
            if(connection != null)
                connection.close();
        } catch (SQLException e) {
            LOGGER.error(Arrays.toString(e.getStackTrace()));
        }
    }
    
    public static Object[] query(String sql){
        LOGGER.info("Start: Query->" + sql);
        Statement stmt = null;
        ResultSet rs = null;
        List<Object> data1;
        //Map<String, Object> row;
        List<String> colName;
        List<Object> data;
        try {
            stmt = connection.createStatement();
            rs = stmt.executeQuery(sql);
            ResultSetMetaData meta = rs.getMetaData();
            
            //data = new ArrayList<>();
            //row = new HashMap();
            data = new ArrayList<>();
            colName = new ArrayList<>();
            
            while(rs.next()) {
                List<Object> row = new ArrayList<>();
                for(int i=1; i<=meta.getColumnCount(); i++){
                    if(!colName.contains(meta.getColumnName(i))) {
                        colName.add(meta.getColumnName(i));
                    }                    
                    //row.put(meta.getColumnName(i), rs.getObject(meta.getColumnName(i)));
                    row.add(rs.getObject(meta.getColumnName(i)));
                }
                data.add(row);
            }
            rs.close();
            stmt.close();
            LOGGER.info("End: Query->" + sql);
            return new Object[]{colName, data};
        } catch (SQLException e) {
            e.printStackTrace();
            LOGGER.error(Arrays.toString(e.getStackTrace()));
            return new Object[]{new ArrayList<>(), new ArrayList<>()};
        } finally {
            close();
        }
    }
    
    public static Integer insert(String sql, Map<Integer, String> param) throws SQLException {
        LOGGER.info("Start: Insert Operation->" + sql);
        try {
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(sql);
            for(Map.Entry<Integer, String> p : param.entrySet()){
                stmt.setObject(p.getKey(), p.getValue());
            }
            int result = stmt.executeUpdate();
            stmt.close();
            connection.commit();
            LOGGER.info("End: Insert Operation->" + sql);
            return result;
        } catch (SQLException e) {
            connection.rollback();
            LOGGER.error(Arrays.toString(e.getStackTrace()));
            return 0;
        } finally {
            close();
        }
    }
    
    public static Integer update(String sql, Map<Integer, String> param) throws SQLException {
        LOGGER.info("Start: Insert Operation->" + sql);
        try {
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(sql);
            for(Map.Entry<Integer, String> p : param.entrySet()){
                stmt.setObject(p.getKey(), p.getValue());
            }
            int result = stmt.executeUpdate();
            stmt.close();
            connection.commit();
            LOGGER.info("End: Insert Operation->" + sql);
            return result;
        } catch (SQLException e) {
            connection.rollback();
            LOGGER.error(Arrays.toString(e.getStackTrace()));
            return 0;
        } finally {
            close();
        }
    }
    
    public static Integer delete(String sql) {
        LOGGER.info("Start: Insert Operation->" + sql);
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            Integer result = stmt.executeUpdate(sql);
            stmt.close();
            LOGGER.info("End: Insert Operation->" + sql);
            return result;
        } catch (SQLException e) {
            LOGGER.error(Arrays.toString(e.getStackTrace()));
            return 0;
        }        
    }
}

/*
meter_bill - DDL
CREATE TABLE meter_bill (
    id               INTEGER        PRIMARY KEY AUTOINCREMENT
                                    UNIQUE
                                    NOT NULL,
    start_date       DATE,
    end_date         DATE,
    start_unit       DECIMAL (8, 2) NOT NULL
                                    DEFAULT (0),
    end_unit         DECIMAL (8, 2) NOT NULL
                                    DEFAULT (0),
    total_unit       DECIMAL (8, 2) NOT NULL
                                    DEFAULT (0),
    unit_per_charges DECIMAL (8, 2) NOT NULL,
    created_on       DATETIME       DEFAULT now,
    updated_on       DATETIME       DEFAULT now
);

unit_per_charges DDL
CREATE TABLE unit_per_charges (
    id        INTEGER        PRIMARY KEY AUTOINCREMENT,
    charges   DECIMAL (8, 2),
    type      TEXT,
    update_on DATETIME
);
INSERT INTO unit_per_charges (
                                 update_on,
                                 type,
                                 charges,
                                 id
                             )
                             VALUES (
                                 '2017-08-04 16:20:00',
                                 'electricity',
                                 100,
                                 1
                             ),
                             (
                                 '2017-08-04 16:21:00',
                                 'water',
                                 50,
                                 2
                             );



*/
