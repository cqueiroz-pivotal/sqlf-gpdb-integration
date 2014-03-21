package com.gopivotal.poc.gfxd_gpdb;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;


import com.pivotal.gemfirexd.callbacks.Event;
import com.pivotal.gemfirexd.callbacks.EventCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Code based on Charlie Black demo code.
 * User: cq
 * Date: 11/03/2014
 * Time: 3:38 AM
 */
public class DataDispatcher implements EventCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataDispatcher.class);

    private BoneCP connectionPool;
    private final Properties p = new Properties();

    /** counter to choose next proxy table*/
    private int rr = 1;

    @Override
    public void onEvent(Event event) throws SQLException {


        switch (event.getType()) {
            case AFTER_INSERT:
            case AFTER_UPDATE:
                ResultSet rs = event.getNewRowsAsResultSet();
                ResultSet pkRS = event.getPrimaryKeysAsResultSet();
                int numCols = rs.getMetaData().getColumnCount();
                Object pk = pkRS.getObject(1);
                processRow(rs, pk, numCols);
                break;

            default:
                break;
        }

    }

    /**
     *
     * @param rs
     * @param pk
     * @param numCols
     */
    private void processRow(ResultSet rs, Object pk, int numCols) {


        int proxyLocation;
        String tableName = "";
        PreparedStatement pstm = null;
        Connection conn = null;

        try{

            conn = connectionPool.getConnection();
            StringBuffer sb = new StringBuffer();
            for(int i = 1; i <= numCols ; i++){
                sb.append(rs.getObject(i));
                sb.append('|');
            }

            proxyLocation = ((pk.hashCode() & 0x7fffffff) % Integer.parseInt(p.getProperty("numproxies"))) + 1;
            tableName = p.getProperty("proxyTablePrefix") + "_" + proxyLocation;

            pstm = conn.prepareStatement("update " + tableName + " set value=? where k=1");
            pstm.setString(1, sb.deleteCharAt(sb.length() -1 ).toString());
            pstm.executeUpdate();


        }catch(SQLException e){
            LOGGER.error("Error processing row: updating proxy table: " + tableName ,e);

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
        connectionPool.shutdown();

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

            BoneCPConfig config = new BoneCPConfig();

            config.setJdbcUrl(p.getProperty("connectionURL"));
            config.setUsername(p.getProperty("username"));
            config.setPassword(p.getProperty("password"));
            config.setMinConnectionsPerPartition(32);
            config.setMaxConnectionsPerPartition(64);
            config.setPartitionCount(1);
            connectionPool = new BoneCP(config); // setup the connection pool

            LOGGER.info("Connection pool created");


        } catch (IOException e) {
            LOGGER.error("Error parsing configuration input:", e);
        } catch (SQLException e){
            LOGGER.error("Error starting pool", e);

        }
    }
}
