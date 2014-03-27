package com.gopivotal.poc.gfxd_gpdb;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;


import com.pivotal.gemfirexd.callbacks.DBSynchronizer;
import com.pivotal.gemfirexd.callbacks.Event;
import com.pivotal.gemfirexd.callbacks.EventCallback;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Code based on Charlie Black demo code.
 * User: cq
 * Date: 11/03/2014
 * Time: 3:38 AM
 */
public class DataDispatcher implements EventCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataDispatcher.class);

    private final Properties p = new Properties();

    private HikariDataSource ds;


    private final List<String> tableNamesList = new ArrayList<String>(20);

    @Override
    public void onEvent(Event event) throws SQLException {



        switch (event.getType()) {
            case AFTER_INSERT:
            case AFTER_UPDATE:
                ResultSet rs = event.getNewRowsAsResultSet();
                int numCols = rs.getMetaData().getColumnCount();
                processRow(rs, numCols);
                break;

            default:
                break;
        }

    }

    /**
     *
     * @param rs
     * @param numCols
     */
    private void processRow(ResultSet rs, int numCols) {

        PreparedStatement pstm = null;
        Connection conn = null;
        final StringBuilder sb = new StringBuilder();
        String updateSQL = null;
        try{

            for(int i = 1; i <= numCols ; i++){
                sb.append(rs.getObject(i));
                sb.append('|');
            }
            sb.deleteCharAt(sb.length() -1);

            conn = ds.getConnection();
            updateSQL = tableNamesList.get((int)System.currentTimeMillis() % tableNamesList.size());
            pstm = conn.prepareStatement(updateSQL);
            pstm.setString(1, sb.toString());
            pstm.executeUpdate();



        }catch(SQLException e){
            LOGGER.error("Error processing row: updating proxy table: " + updateSQL ,e);

        }finally{

            try {

                if(pstm!=null)  pstm.close();
                if(conn!=null)  conn.close();


            } catch (SQLException e) {
                LOGGER.error("Error closing connection/statement ",e);
            }
        }

    }


    @Override
    public void close() throws SQLException {

            ds.shutdown();
    }

    @Override
    public void init(String s) throws SQLException {

        try {

            StringBuilder sb = new StringBuilder();
            for(String property : s.split("\\|")){
                sb.append(property);
                sb.append("\n");
            }

            StringReader sr = new StringReader(sb.toString());
            p.load(sr);


            HikariConfig config = new HikariConfig();
            config.setMaximumPoolSize(Integer.parseInt(p.getProperty("maxConn")));
            config.setMinimumPoolSize(Integer.parseInt(p.getProperty("minConn")));
            config.setDataSourceClassName("com.pivotal.gemfirexd.internal.jdbc.EmbeddedDataSource");
            config.addDataSourceProperty("user", "app");
            config.addDataSourceProperty("password", "app");
            ds = new HikariDataSource(config);

            LOGGER.info("HikariCP Connection pool created");




            String[] tableNames = System.getProperty("proxyTableName").split(",");

            for(String name : tableNames){
                StringBuilder sbs = new StringBuilder();
                tableNamesList.add(sbs.append("update ").append(name).
                        append(" set value=? where k=1").toString());

            }



            LOGGER.info("UpdateSQLs created: " + tableNamesList.size());


        } catch (IOException e) {
            LOGGER.error("Error parsing configuration input:", e);
        }
    }


}
