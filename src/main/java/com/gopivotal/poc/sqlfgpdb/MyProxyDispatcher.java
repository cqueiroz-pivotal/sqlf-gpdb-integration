package com.gopivotal.poc.sqlfgpdb;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import com.vmware.sqlfire.callbacks.Event;
import com.vmware.sqlfire.callbacks.EventCallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * User: cq
 * Date: 11/03/2014
 * Time: 3:38 AM
 */
public class MyProxyDispatcher implements EventCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyProxyDispatcher.class);

//    private HikariDataSource ds;
    private BoneCP connectionPool;
    private final Properties p = new Properties();

    /** counter to choose next proxy table*/
    private int rr = 1;

    @Override
    public void onEvent(Event event) throws SQLException {

        ResultSet rs = event.getNewRowsAsResultSet();
        ResultSet pkRS = event.getPrimaryKeysAsResultSet();
        int numCols = rs.getMetaData().getColumnCount();

        Connection conn = null;
        PreparedStatement pstm = null;

        try{
            conn = connectionPool.getConnection();

            switch (event.getType()) {
                case AFTER_INSERT:
                case AFTER_UPDATE:
                    processRow(rs, pkRS, numCols, conn);
                    break;

                case BULK_INSERT:{

                    while(rs.next()){
                        processRow(rs, pkRS, numCols, conn);
                    }
                    break;

                }


                case AFTER_DELETE:
                    break;

                default:
                    break;
            }

        }catch (SQLException e){
            LOGGER.error("Error updating table:",e);
            throw new SQLException(e);

        }finally{


            if(conn!=null){
                conn.close();
            }
        }


    }

    private void processRow(ResultSet rs, ResultSet pkRS, int numCols, Connection conn) throws SQLException {
        int proxyLocation;
        String tableName;
        PreparedStatement pstm;
        StringBuffer sb = new StringBuffer();
        for(int i = 1; i <= numCols ; i++){
            sb.append(rs.getObject(i));
            sb.append('|');
        }
        if(pkRS!=null){
            pkRS.next();
            Object pk = pkRS.getObject(1);
            proxyLocation = (pk.hashCode() % Integer.parseInt(p.getProperty("numproxies"))) + 1;
            tableName = p.getProperty("proxyTablePrefix") + "_" + proxyLocation;

        }else{
            proxyLocation = (rr++ % Integer.parseInt(p.getProperty("numproxies"))) + 1;
            tableName = p.getProperty("proxyTablePrefix") + "_" + proxyLocation;
        }

        pstm = conn.prepareStatement("update " + tableName + " set value=? where k=1");
        pstm.setString(1, sb.toString());
        pstm.executeUpdate();
        pstm.close();
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

            Class.forName("com.vmware.sqlfire.jdbc.EmbeddedDriver");
            BoneCPConfig config = new BoneCPConfig();

            config.setJdbcUrl(p.getProperty("connectionURL"));
            config.setUsername(p.getProperty("username"));
            config.setPassword(p.getProperty("password"));
            config.setMinConnectionsPerPartition(32);
            config.setMaxConnectionsPerPartition(64);
            config.setPartitionCount(1);
            connectionPool = new BoneCP(config); // setup the connection pool

//            HikariConfig config = new HikariConfig();
//            config.setMinimumPoolSize(32);
//            config.setMaximumPoolSize(64);
//            config.setDataSourceClassName("com.vmware.sqlfire.internal.jdbc.ClientDataSource");
//            config.setPoolName("sqlfCP");
//            config.addDataSourceProperty("url", p.getProperty("connectionURL"));
//            config.addDataSourceProperty("user", p.getProperty("username"));
//            config.addDataSourceProperty("password", p.getProperty("password"));
//
//            ds = new HikariDataSource(config);


        } catch (IOException e) {
            LOGGER.error("Error parsing configuration input:", e);
        } catch (SQLException e){
            LOGGER.error("Error starting pool", e);

        }catch (ClassNotFoundException e){
            LOGGER.error("Error finding class: com.vmware.sqlfire.jdbc.EmbeddedDriver", e);
        }
    }
}
