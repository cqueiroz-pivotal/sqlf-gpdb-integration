package com.gopivotal.poc.gfxd_gpdb;

import com.pivotal.gemfirexd.FabricServer;
import com.pivotal.gemfirexd.FabricServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by cq on 21/3/14.
 */
public class EmbeddedGFXDForTests {


    private Logger LOGGER = LoggerFactory.getLogger(EmbeddedGFXDForTests.class);


    private static String ERD_DATA_TABLE = "CREATE TABLE \"APP\".\"ERD_DATA\"\n" +
            "(\n" +
            "  ERD_1 timestamp NOT NULL,\n" +
            "  ERD_2 varchar(100) NOT NULL,\n" +
            "  ERD_3 varchar(20) NOT NULL,\n" +
            "  ERD_4 varchar(20) NOT NULL,\n" +
            "  ERD_5 varchar(20) NOT NULL,\n" +
            "  ERD_6 varchar(20) NOT NULL,\n" +
            "  ERD_7 varchar(20) NOT NULL,\n" +
            "  ERD_8 int NOT NULL,\n" +
            "  ERD_9 int NOT NULL,\n" +
            "  ERD_10 int NOT NULL,\n" +
            "  ERD_11 int NOT NULL,\n" +
            "  ERD_12 int NOT NULL,\n" +
            "  PRIMARY KEY (ERD_2)\n" +
            ") PARTITION BY COLUMN (ERD_2)\n" +
            "  REDUNDANCY 0\n" +
            "  SERVER GROUPS (poc)";

    private static String ERD_LISTENER = "call SYS.ADD_LISTENER('ERD_LISTENER',\n" +
            "                      'APP','ERD_DATA',\n" +
            "                      'com.gopivotal.poc.gfxd_gpdb.DataDispatcher',\n" +
            "                      'connectionURL=jdbc:sqlfire:|numproxies=1|proxyTablePrefix=dataProxy|username=app|password=app|minConn=32|maxConn=64',\n" +
            "                      null);";


    private static String DATA_PROXY_LISTENER = "CREATE ASYNCEVENTLISTENER dataProxy_1\n" +
            "(\n" +
            "LISTENERCLASS 'com.gopivotal.poc.gfxd_gpdb.DataBatchListener'\n" +
            "INITPARAMS 'pipeFileLocation=/tmp/data.pipe|extTableName=app.ext_data|destTableName=app.data|connectionURL=jdbc:postgresql://mdw:5432/fdc|username=gpadmin|password=gpadmin|gfxdConnectionURL=jdbc:sqlfire:|gfxdUserName=app|gfxdPassword=app|delPattern=delete from app.erd_data where ERD_2=''{1}''|whereClausePostions=1|minConn=32|maxConn=64|integrate=false'\n" +
            "MANUALSTART false\n" +
            "ENABLEBATCHCONFLATION false\n" +
            "BATCHSIZE 100000\n" +
            "BATCHTIMEINTERVAL 6000\n" +
            "ENABLEPERSISTENCE false\n" +
            "MAXQUEUEMEMORY 100\n" +
            ")\n" +
            "SERVER GROUPS ( poc )";

    private static String PROXY_TABLE_1 = "create table dataProxy_1\n" +
            "( k integer, value varchar(500))\n" +
            "  ASYNCEVENTLISTENER ( dataProxy_1 ) SERVER GROUPS ( poc )";


    private static String INSERT_PROXY_TABLE_1 = "insert into dataProxy_1 values (1,'2014-03-21 11:33:47.078|ee70ed9b-ead7-40a7-b089-d7b5ec18df01|a1234567891234567890|b1234567891234567890|c123456789|d123456789|e123456789|1967155024|197416237|934958692|527614193|1598464958')";



    private void setupDataSchema() throws SQLException{

        Properties connProps = new Properties();
        connProps.setProperty("user", "app");
        connProps.setProperty("password", "app");

        Connection conn = null;
        PreparedStatement ps;

        try {
            conn = DriverManager.getConnection("jdbc:gemfirexd:", connProps);
            ps = conn.prepareStatement(ERD_DATA_TABLE);
            ps.executeUpdate();
            ps.close();

            ps = conn.prepareStatement(ERD_LISTENER);
            ps.executeUpdate();
            ps.close();

            ps = conn.prepareStatement(DATA_PROXY_LISTENER);
            ps.executeUpdate();
            ps.close();

            ps = conn.prepareStatement(PROXY_TABLE_1);
            ps.executeUpdate();
            ps.close();

            ps = conn.prepareStatement(INSERT_PROXY_TABLE_1);
            int i = ps.executeUpdate();
            ps.close();

            if(LOGGER.isDebugEnabled() && i > 0){
                LOGGER.debug("Row inserted to DataProxy_1 table");
            }


        }finally{
            if(conn!=null) conn.close();
        }


    }

    private FabricServer startServer() throws SQLException{
        final String gfxdDir = "/tmp/egfxd";

        File f = new File(gfxdDir);
        if(!f.exists()){
            if(f.mkdir()) LOGGER.info("Folder created");

        }

        Properties serverProps = new Properties();
        serverProps.setProperty("server-groups", "poc");
        serverProps.setProperty("persist-dd", "false");
        serverProps.setProperty("sys-disk-dir",gfxdDir);
        serverProps.setProperty("server-groups","poc");


        FabricServer server = FabricServiceManager.getFabricServerInstance();
        server.start(serverProps);
        return server;

    }

    public static void main(String[] args) throws Exception{


        EmbeddedGFXDForTests gfxd = new EmbeddedGFXDForTests();
        FabricServer server  = gfxd.startServer();
        gfxd.setupDataSchema();
        server.startNetworkServer(null,-1, null);
        Object lock = new Object();
        synchronized (lock) {
            while (true) {
                lock.wait();
            }
        }


    }
}
