import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

/**
 * User: cq
 * Date: 11/03/2014
 * Time: 2:45 AM
 */
public class BatchListenerTests {

    @Test
    public void testLoadConfig() throws IOException{
        String s = "pipeFileLocation=/home/gpadmin/flights.pipe|extTableName=ext_flights|destTableName=app.flights|connectionURL=jdbc:postgresql://mdw:5440/airlines|username=gpadmin|password=gpadmin|gfxdConnectionURL=jdbc:sqlfire:|gfxdUserName=app|gfxdPassword=app|delPattern=delete from app.erd_data where ERD_2='{1}'|stringPos=1|minConn=32|maxConn=64|integrate=false";
        Properties p = new Properties();
        StringBuilder sb = new StringBuilder();
        for(String property : s.split("\\|")){
            sb.append(property);
            sb.append("\n");
        }

        StringReader sr = new StringReader(sb.toString());
        p.load(sr);

        Assert.assertTrue(p.containsKey("pipeFileLocation"));
        Assert.assertTrue(p.containsKey("extTableName"));
        Assert.assertTrue(p.containsKey("destTableName"));
        Assert.assertTrue(p.containsKey("connectionURL"));
        Assert.assertTrue(p.containsKey("username"));
        Assert.assertTrue(p.containsKey("password"));

        Assert.assertTrue(p.containsKey("gfxdConnectionURL"));
        Assert.assertTrue(p.containsKey("gfxdUserName"));
        Assert.assertTrue(p.containsKey("gfxdPassword"));
        Assert.assertTrue(p.containsKey("delPattern"));
        Assert.assertTrue(p.containsKey("stringPos"));

        Assert.assertTrue(p.containsKey("minConn"));
        Assert.assertTrue(p.containsKey("maxConn"));

        Assert.assertTrue(p.containsKey("integrate"));


        Assert.assertEquals("/home/gpadmin/flights.pipe",p.getProperty("pipeFileLocation"));
        Assert.assertEquals("ext_flights",p.getProperty("extTableName"));
        Assert.assertEquals("app.flights",p.getProperty("destTableName"));
        Assert.assertEquals("jdbc:postgresql://mdw:5440/airlines",p.getProperty("connectionURL"));
        Assert.assertEquals("gpadmin",p.getProperty("username"));
        Assert.assertEquals("gpadmin",p.getProperty("password"));


        Assert.assertEquals("jdbc:sqlfire:",p.getProperty("gfxdConnectionURL"));
        Assert.assertEquals("app",p.getProperty("gfxdUserName"));
        Assert.assertEquals("app",p.getProperty("gfxdPassword"));
        Assert.assertEquals("delete from app.erd_data where ERD_2='{1}'",p.getProperty("delPattern"));
        Assert.assertEquals("1",p.getProperty("stringPos"));

        Assert.assertEquals("32",p.getProperty("minConn"));
        Assert.assertEquals("64",p.getProperty("maxConn"));

        Assert.assertEquals("false",p.getProperty("integrate"));





    }
}
