package com.gopivotal.poc.sqlfgpdb;

import com.vmware.sqlfire.callbacks.Event;
import com.vmware.sqlfire.callbacks.EventCallback;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.SQLException;
import java.util.Properties;

/**
 * User: cq
 * Date: 11/03/2014
 * Time: 3:38 AM
 */
public class MyProxyDispatcher implements EventCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyProxyDispatcher.class);

    private HikariDataSource ds;
    private final Properties p = new Properties();

    @Override
    public void onEvent(Event event) throws SQLException {

    }

    @Override
    public void close() throws SQLException {

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
            config.setMinimumPoolSize(32);
            config.setMaximumPoolSize(64);
            config.setDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");
            config.setPoolName("sqlfCP");
            config.addDataSourceProperty("url", p.getProperty("connectionURL"));
            config.addDataSourceProperty("user", p.getProperty("username"));
            config.addDataSourceProperty("password", p.getProperty("password"));


        } catch (IOException e) {
            LOGGER.error("Error parsing configuration input:", e);
        }
    }
}
