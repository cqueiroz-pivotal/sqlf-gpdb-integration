import org.junit.Assert;
import org.junit.Test;

import java.io.StringReader;
import java.util.Properties;

/**
 * User: cq
 * Date: 11/03/2014
 * Time: 3:57 AM
 */
public class MyProxyDispatcherTests {

    @Test
    public void testLoadConfig() throws Exception{
        String s = "connectionURL=jdbc:sqlfire:|numproxies=1|proxyTablePrefix=dataProxy|username=app|password=app|minConn=32|maxConn=64";
        Properties p = new Properties();
        StringBuilder sb = new StringBuilder();
        for(String property : s.split("\\|")){
            sb.append(property);
            sb.append("\n");
        }

        StringReader sr = new StringReader(sb.toString());
        p.load(sr);

        Assert.assertTrue(p.containsKey("connectionURL"));
        Assert.assertTrue(p.containsKey("numproxies"));
        Assert.assertTrue(p.containsKey("proxyTablePrefix"));
        Assert.assertTrue(p.containsKey("username"));
        Assert.assertTrue(p.containsKey("password"));
        Assert.assertTrue(p.containsKey("minConn"));
        Assert.assertTrue(p.containsKey("maxConn"));


        Assert.assertEquals("jdbc:sqlfire:",p.getProperty("connectionURL"));
        Assert.assertEquals("1",p.getProperty("numproxies"));
        Assert.assertEquals("dataProxy",p.getProperty("proxyTablePrefix"));
        Assert.assertEquals("app",p.getProperty("username"));
        Assert.assertEquals("app",p.getProperty("password"));
        Assert.assertEquals("32",p.getProperty("minConn"));
        Assert.assertEquals("64",p.getProperty("maxConn"));
    }
}
