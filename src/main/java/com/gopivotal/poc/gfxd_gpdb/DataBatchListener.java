package com.gopivotal.poc.gfxd_gpdb;

import com.jolbox.bonecp.BoneCP;

import com.jolbox.bonecp.BoneCPConfig;
import com.pivotal.gemfirexd.callbacks.AsyncEventListener;
import com.pivotal.gemfirexd.callbacks.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;


/**
 * Code based on Charlie Black demo code.
 * User: cq
 * Date: 11/03/2014
 * Time: 2:38 AM
 */
public class DataBatchListener implements AsyncEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataBatchListener.class);


    private BoneCP connectionPool;
    private final Properties p = new Properties();
    private BoneCP gfxdConnectionPool;
    private String delPattern;
    private String whereClausePostions;

    private BufferedWriter pipeWriter;

    @Override
    public boolean processEvents(List<Event> events) {

        startLoadingData();

        try {

            pipeWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(p.getProperty("pipeFileLocation"))));
            LOGGER.info("pipeWriter started!!");

        } catch (FileNotFoundException e) {
            LOGGER.error("Error starting pipe:", e);
        }

        try {
            int i = 0;
            for(Event event : events){
                ResultSet rs = event.getNewRowsAsResultSet();
                switch(event.getType()){

                    case AFTER_INSERT:
                    case AFTER_UPDATE:
                        try {
                            String data = rs.getString(1);
                            pipeWriter.write(data);
                            pipeWriter.newLine();
                            i++;
                        } catch (SQLException e) {
                            LOGGER.error("Error doing single Insert/Update: ",e);
                        } catch(IOException e){
                            LOGGER.error("Error writing to pipe: ",e);
                        }finally{
                            try {
                                if(rs!=null)
                                    rs.close();
                            } catch (SQLException e) {
                                LOGGER.error("Error closing RS ",e);
                            }
                        }

                    default:
                        break;
                }
            }

            pipeWriter.flush();
            pipeWriter.close();


            LOGGER.info("Events flushed into pipe: " + i);

            // To clear the main table data
            clearMainTable(events);
            LOGGER.info("Main table data be cleared: " + i);

            return true;

        } catch (IOException e) {
            LOGGER.error("Error writing to pipe: ", e);
        }

        return false;
    }



    /**
     * Starts loading data into GPDB.
     */
    private void startLoadingData() {
        Thread loadData = new Thread(new Runnable() {
            @Override
            public void run() {
                Connection conn = null;
                PreparedStatement pstm = null;
                final String sql = "INSERT  INTO  " + p.getProperty("destTableName") +
                        " SELECT  * from " + p.getProperty("extTableName") + " ;";

                try{

                    conn = connectionPool.getConnection();
                    pstm = conn.prepareStatement(sql);
                    int i = pstm.executeUpdate();
                    LOGGER.debug("if i  > 0 data loaded into GPDB: " + i);

                }catch (SQLException e){
                    LOGGER.error("Error inserting data into GPDB",e);

                }finally{
                    if(conn!=null)
                        try {
                            conn.close();
                        } catch (SQLException e) {
                            LOGGER.error("Error closing connection",e);
                        }
                }

            }
        });
        loadData.start();
    }


    private void clearMainTable(List<Event> events) {
        Connection connection = null;
        Statement sql = null;
        ResultSet rs = null;

        try {
            connection = gfxdConnectionPool.getConnection();
            sql = connection.createStatement();

            for (Event event : events) {
                rs = event.getNewRowsAsResultSet();

                switch (event.getType()) {
                    case AFTER_INSERT:
                    case AFTER_UPDATE:

                        String data = rs.getString(1);
                        String delSQL = prepareSQL(data);
                        if (null != delSQL)
                            sql.addBatch(delSQL);

                    default:
                        break;
                }

                rs.close();
            }

            int[] results = sql.executeBatch();
            int resultSize = results == null ? 0 : results.length;

        } catch (SQLException e1) {

            LOGGER.error("SQLError:" + e1.getMessage());

        } finally {

            try {
                if (null != rs) {
                    rs.close();
                }
                connection.close();
            } catch (SQLException e) {
                LOGGER.error("SQLError:" + e.getMessage());
            }
        }
    }

    private String prepareSQL(String data) {
        String sql = null;

        String[] positions = whereClausePostions.split("\\-");
        String[] dataArr = data.split("\\|");

        LOGGER.info("data in preparSQL:"+data);

        if (null != positions) {
            int iPostion = 0;
            String val = "";
            for (String sPostion : positions) {

                try {
                    iPostion = Integer.valueOf(sPostion);
                    val = dataArr[iPostion].toString();
                    sql = delPattern.replace("{" + sPostion + "}", val);
                } catch (Exception e) {
                    return null;
                }
            }
        }

        LOGGER.info("delSQL=" + sql);

        return sql;
    }

    @Override
    public void close() {

        connectionPool.shutdown();
        gfxdConnectionPool.shutdown();
    }

    @Override
    public void init(String s) {

        try {

            StringBuilder sb = new StringBuilder();
            for(String property : s.split("\\|")){
                sb.append(property);
                sb.append("\n");
            }

            StringReader sr = new StringReader(sb.toString());
            p.load(sr);

            Class.forName("org.postgresql.Driver");
            BoneCPConfig config = new BoneCPConfig();

            config.setJdbcUrl(p.getProperty("connectionURL"));
            config.setUsername(p.getProperty("username"));
            config.setPassword(p.getProperty("password"));
            config.setMinConnectionsPerPartition(1);
            config.setMaxConnectionsPerPartition(1);
            config.setPartitionCount(1);
            connectionPool = new BoneCP(config); // setup the connection pool

            LOGGER.info("connectionPool started!!");

            BoneCPConfig gfxdConfig = new BoneCPConfig();
            gfxdConfig.setJdbcUrl(p.getProperty("gfxdConnectionURL"));//("jdbc:sqlfire:");
            gfxdConfig.setUsername(p.getProperty("gfxdUserName"));//"APP");
            gfxdConfig.setPassword(p.getProperty("gfxdPassword"));
            gfxdConfig.setMinConnectionsPerPartition(1);
            gfxdConfig.setMaxConnectionsPerPartition(1);
            gfxdConfig.setPartitionCount(1);
            gfxdConnectionPool = new BoneCP(gfxdConfig); // setup the gfxd connection// pool
            delPattern = p.getProperty("delPattern");//"delete from app.erd_data where ERD_2='{1}' ";
            whereClausePostions =  p.getProperty("whereClausePostions");//"1";

            LOGGER.info("gfxdConnectionPool started!!");



        } catch (IOException e) {
            LOGGER.error("Error parsing configuration input:", e);
        }catch (ClassNotFoundException e) {
            LOGGER.error("Error loading Driver",e);
        } catch (SQLException e){
            LOGGER.error("Error starting BoneCP pool",e);
        }


    }

    @Override
    public void start() {



    }
}
