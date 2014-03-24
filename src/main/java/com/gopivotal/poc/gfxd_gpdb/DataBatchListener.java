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
import java.util.List;
import java.util.Properties;


/**
 * Code based on Charlie Black demo code.
 * @author cq
 * modified by @author Wang Yi
 * Date: 11/03/2014
 * Time: 2:38 AM
 */
public class DataBatchListener implements AsyncEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataBatchListener.class);


    private BoneCP connectionPool;
    private final Properties p = new Properties();
//    private BoneCP gfxdConnectionPool;
//    private String delPattern;
//    private String whereClausePostions;

    private boolean integrate = true;

    private BufferedWriter pipeWriter;
//    private static double counter0,counter1, counter, counter2;


//    private void processStats(long processingTime){
//
//        counter++;
//        if(processingTime<=200) {
//            counter2++;
//        }else if(processingTime>200 && processingTime<=400) {
//            counter0++;
//        }else {
//            counter1++;
//        }
//
////        if((counter % 100000) == 0){
//        LOGGER.info("too slow: " + (counter1/counter * 100.0f));
//        LOGGER.info("medium slow: " + (counter0/counter * 100.0f));
//        LOGGER.info("fast: " + (counter2/counter * 100.0f));
////        }
//
//    }


    @Override
    public boolean processEvents(List<Event> events) {

//        long startTime = System.currentTimeMillis();

        if(integrate) startLoadingData();

        try {
            pipeWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(p.getProperty("pipeFileLocation"))));
//            LOGGER.info("pipeWriter started!!");

        }catch(FileNotFoundException e){
            LOGGER.error("Error opening pipe: ",e);
            return false;
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
                            // To clear the main table data

                        } catch (SQLException e) {
                            LOGGER.error("Error doing single Insert/Update: ",e);
                        } catch(IOException e){
                            LOGGER.error("Error writing to pipe: ", e);
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
//            clearMainTable(events);
//            LOGGER.info("EmbeddedGFXDForTests table data be cleared: " + i);

//            long endTime = System.currentTimeMillis();
//            long pt = (endTime - startTime);
//            processStats(pt);

            LOGGER.info("Events flushed to pipe: " + i);

            return true;

        } catch (IOException e) {
            LOGGER.error("Error writing to pipe: ", e);
        }catch (Exception e){
            LOGGER.error("Error",e);
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


    @Override
    public void close() {

        if(integrate) connectionPool.shutdown();
//        gfxdConnectionPool.shutdown();
        try {
            pipeWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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


            integrate = Boolean.parseBoolean(p.getProperty("integrate"));

            if(integrate){
                Class.forName("org.postgresql.Driver");
                BoneCPConfig config = new BoneCPConfig();

                config.setJdbcUrl(p.getProperty("connectionURL"));
                config.setUsername(p.getProperty("username"));
                config.setPassword(p.getProperty("password"));
                config.setMinConnectionsPerPartition(1);
                config.setMaxConnectionsPerPartition(1);
                config.setPartitionCount(1);
                connectionPool = new BoneCP(config); // setup the connection pool

                LOGGER.info("GPDB connectionPool started!!");
            }else{
                LOGGER.info("GPDB connectionPool NOT started!!");
                LOGGER.info("Running on DEBUG mode");
            }


        } catch (IOException e) {
            LOGGER.error("Error parsing configuration input:", e);
        }catch (SQLException e){
            LOGGER.error("Error starting BoneCP pool",e);
        }catch(Exception e){
            LOGGER.error("Error starting pools/parsing configuration",e);
        }


    }

    @Override
    public void start() {



    }
}