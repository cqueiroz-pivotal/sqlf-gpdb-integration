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
        String s = "connectionurl=jdbc:sqlfire:|numproxies=3|proxyTablePrefix=flightsProxy|username=app|password=app";
        Properties p = new Properties();
        StringBuilder sb = new StringBuilder();
        for(String property : s.split("\\|")){
            sb.append(property);
            sb.append("\n");
        }

        StringReader sr = new StringReader(sb.toString());
        p.load(sr);

        Assert.assertTrue(p.containsKey("connectionurl"));
        Assert.assertTrue(p.containsKey("numproxies"));
        Assert.assertTrue(p.containsKey("proxyTablePrefix"));
        Assert.assertTrue(p.containsKey("username"));
        Assert.assertTrue(p.containsKey("password"));

        Assert.assertEquals("jdbc:sqlfire:",p.getProperty("connectionurl"));
        Assert.assertEquals("3",p.getProperty("numproxies"));
        Assert.assertEquals("flightsProxy",p.getProperty("proxyTablePrefix"));
        Assert.assertEquals("app",p.getProperty("username"));
        Assert.assertEquals("app",p.getProperty("password"));
    }
}
