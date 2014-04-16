import com.veraxsystems.vxipmi.coding.commands.IpmiVersion;
import com.veraxsystems.vxipmi.coding.commands.PrivilegeLevel;
import com.veraxsystems.vxipmi.coding.protocol.AuthenticationType;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * Unit tests for {@link IPMIProperties}.
 *
 * @author Fabien Hermenier
 */
public class IPMIPropertiesTest {

    @Test
    public void test() throws Exception {
        Properties p = new Properties();
        p.load(new FileInputStream("src/test/resources/connexion.properties"));
        Assert.assertEquals(IPMIProperties.getLocalPort(p), 6666);
        Assert.assertEquals(IPMIProperties.getAuthenticationType(p), AuthenticationType.RMCPPlus);
        Assert.assertEquals(IPMIProperties.getIpmiVersion(p), IpmiVersion.V20);
        Assert.assertEquals(IPMIProperties.getPrivilegeLevel(p), PrivilegeLevel.Administrator);
        Assert.assertEquals(IPMIProperties.getUsername(p), "admin");
        Assert.assertEquals(IPMIProperties.getPassword(p), "mdpbmc");
    }
}
