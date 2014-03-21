package com.gopivotal.poc.gfxd_gpdb;

import com.pivotal.gemfirexd.callbacks.Event;
import com.pivotal.gemfirexd.callbacks.EventCallback;

import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by cq on 21/3/14.
 */
public class FilterDispatchWriter implements EventCallback {

    private final Properties p = new Properties();
    private int bucketsSize ;

    @Override
    public void onEvent(Event event) throws SQLException {
            switch(event.getType()){
                case BEFORE_INSERT:{
                    Object pk = event.getPrimaryKeysAsResultSet().getObject(1);
                    int dk = ((pk.hashCode() & 0x7fffffff) % bucketsSize);

                }
                break;
            }
    }

    @Override
    public void close() throws SQLException {

    }

    @Override
    public void init(String s) throws SQLException {
        StringBuilder sb = new StringBuilder();
        for(String property : s.split("\\|")){
            sb.append(property);
            sb.append("\n");
        }

        StringReader sr = new StringReader(sb.toString());
        try {

            p.load(sr);
            bucketsSize = Integer.parseInt(p.getProperty("buckets_size"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
