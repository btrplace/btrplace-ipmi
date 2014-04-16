import com.veraxsystems.vxipmi.coding.commands.IpmiVersion;
import com.veraxsystems.vxipmi.coding.commands.PrivilegeLevel;
import com.veraxsystems.vxipmi.coding.protocol.AuthenticationType;

import java.util.Properties;

/**
 * Utility class to load parameters to communicate to a BMC through the IPMI from a properties file.
 *
 * @author Fabien Hermenier
 */
public final class IPMIProperties {

    /**
     * property that provide the password.
     */
    public static final String PASSWORD = "password";

    /**
     * property that provide the username.
     */
    public static final String USERNAME = "username";

    /**
     * property that provide the port.
     */
    public static final String PORT = "port";

    /**
     * property that provide the version of the IPMI protocol.
     */
    public static final String IPMI_VERSION = "ipmiVersion";

    /**
     * property that provide the authentication method.
     */
    public static final String AUTHENTICATION = "authentication";

    /**
     * property that provide the privilege to use.
     */
    public static final String PRIVILEGE = "privilege";

    private IPMIProperties() {

    }

    /**
     * Read the password from the properties.
     * The values is read from the {@link #PASSWORD} property.
     *
     * @param p the properties file
     * @return the password
     * @throws Exception if the password is missing
     */
    public static String getPassword(Properties p) throws Exception {
        String x = p.getProperty(PASSWORD);
        if (x == null) {
            throw new Exception("Missing property '" + PASSWORD + "'");
        }
        return x;
    }

    /**
     * Read the username from the properties.
     * The values is read from the {@link #USERNAME} property.
     *
     * @param p the properties file
     * @return the username
     * @throws Exception if the username is missing
     */
    public static String getUsername(Properties p) throws Exception {
        String x = p.getProperty(USERNAME);
        if (x == null) {
            throw new Exception("Missing property '" + USERNAME + "'");
        }
        return x;
    }

    /**
     * Read the port from the properties.
     * The values is read from the {@link #PORT} property.
     *
     * @param p the properties file
     * @return the port
     * @throws Exception if the port is missing
     */
    public static int getLocalPort(Properties p) throws Exception {
        String po = p.getProperty(PORT);
        if (po == null) {
            throw new Exception("Missing property '" + PORT + "'");
        }
        return Integer.parseInt(po);
    }

    /**
     * Read the IPMI version from the properties.
     * The values is read from the {@link #IPMI_VERSION} property.
     *
     * @param p the properties file
     * @return the IPMI version
     * @throws Exception if the version or unsupported is missing
     */
    public static IpmiVersion getIpmiVersion(Properties p) throws Exception {
        String v = p.getProperty(IPMI_VERSION);
        if (v == null) {
            throw new Exception("Missing property '" + IPMI_VERSION + "'");
        }
        switch (v) {
            case "15":
                return IpmiVersion.V15;
            case "20":
                return IpmiVersion.V20;
        }
        throw new Exception("Unsupported IPMI version '" + v + "'. Must be '15' or '20'");
    }

    /**
     * Read the authentication type from the properties.
     * The values is read from the {@link #AUTHENTICATION} property.
     *
     * @param p the properties file
     * @return the authentication type
     * @throws Exception if the port is missing
     */
    public static AuthenticationType getAuthenticationType(Properties p) throws Exception {
        String type = p.getProperty(AUTHENTICATION);
        if (type == null) {
            throw new Exception("Missing property '" + AUTHENTICATION + "'");
        }

        switch (type.toLowerCase()) {
            case "rmcpplus":
                return AuthenticationType.RMCPPlus;
            case "none":
                return AuthenticationType.None;
            case "simple":
                return AuthenticationType.Simple;
            case "md2":
                return AuthenticationType.Md2;
            case "md5":
                return AuthenticationType.Md5;
            case "oem":
                return AuthenticationType.Oem;
        }
        throw new Exception("Unsupported authentication type '" + type + "'. Supported value: 'rmcpplus', 'none','simple','md2','md5','oem'");
    }

    public static PrivilegeLevel getPrivilegeLevel(Properties p) throws Exception {
        String lvl = p.getProperty(PRIVILEGE);
        if (lvl == null) {
            throw new Exception("Missing property '" + PRIVILEGE + "'");
        }
        switch (lvl.toLowerCase()) {
            case "administrator":
                return PrivilegeLevel.Administrator;
            case "operator":
                return PrivilegeLevel.Operator;
            case "user":
                return PrivilegeLevel.User;
        }
        throw new Exception("Unsupported privilege '" + lvl + "'. Supported value: 'administrator', 'operator', 'user'");
    }
}
