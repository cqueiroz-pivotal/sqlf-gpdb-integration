package com.gopivotal.poc.sqlfgpdb;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import com.vmware.sqlfire.callbacks.AsyncEventListener;
import com.vmware.sqlfire.callbacks.Event;
//import com.zaxxer.hikari.HikariConfig;
//import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.vmware.sqlfire.callbacks.Event.Type;

/**
 * User: cq
 * Date: 11/03/2014
 * Time: 2:38 AM
 */
public class MyBatchListener implements AsyncEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyBatchListener.class);

//    private HikariDataSource ds;
    private BoneCP connectionPool;
    private final Properties p = new Properties();

    private BufferedWriter pipeWriter;
    private final ExecutorService executorService = Executors.newFixedThreadPool(1);

    @Override
    public boolean processEvents(List<Event> events) {

        executorService.execute(new Runnable() {
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

                    case BULK_INSERT:
                        break;

                    case AFTER_DELETE:
                        break;

                    default:
                        break;
                }
            }

            pipeWriter.flush();
            LOGGER.debug("Events flushed into pipe: " + i);

            return true;
        } catch (IOException e) {
            LOGGER.error("Error writing to pipe: ", e);
        }

        return false;
    }

    @Override
    public void close() {

        try {
            connectionPool.shutdown();
//            ds.shutdown();
            executorService.shutdown();
            executorService.awaitTermination(10L, TimeUnit.SECONDS);
            pipeWriter.close();

        } catch (IOException e) {
            LOGGER.error("Error closing pipeWriter: " + p.getProperty("pipeFileLocation"),e);

        } catch (InterruptedException e){

            LOGGER.error("Error closing executorService: ",e);
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

            pipeWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(p.getProperty("pipeFileLocation"))));


        } catch (IOException e) {
            LOGGER.error("Error parsing configuration input:", e);
        }


    }

    @Override
    public void start() {

        try {
            Class.forName("org.postgresql.Driver");
            BoneCPConfig config = new BoneCPConfig();

            config.setJdbcUrl(p.getProperty("connectionURL"));
            config.setUsername(p.getProperty("username"));
            config.setPassword(p.getProperty("password"));
            config.setMinConnectionsPerPartition(1);
            config.setMaxConnectionsPerPartition(1);
            config.setPartitionCount(1);
            connectionPool = new BoneCP(config); // setup the connection pool

        } catch (ClassNotFoundException e) {
            LOGGER.error("Error loading Driver",e);
        } catch (SQLException e){
            LOGGER.error("Error starting BoneCP pool",e);
        }


//        config.setMaximumPoolSize(1);
//        config.setDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");
//        config.setPoolName("gpdbCP");
//
//        config.addDataSourceProperty("user", p.getProperty("username"));
//        config.addDataSourceProperty("password", p.getProperty("password"));
//
//        ds = new HikariDataSource(config);
    }
}
