import com.veraxsystems.vxipmi.coding.commands.IpmiCommandCoder;
import com.veraxsystems.vxipmi.coding.commands.IpmiVersion;
import com.veraxsystems.vxipmi.coding.commands.PrivilegeLevel;
import com.veraxsystems.vxipmi.coding.commands.ResponseData;
import com.veraxsystems.vxipmi.coding.commands.chassis.ChassisControl;
import com.veraxsystems.vxipmi.coding.commands.chassis.PowerCommand;
import com.veraxsystems.vxipmi.coding.protocol.AuthenticationType;
import com.veraxsystems.vxipmi.coding.security.CipherSuite;
import com.veraxsystems.vxipmi.connection.Connection;
import com.veraxsystems.vxipmi.connection.ConnectionListener;
import com.veraxsystems.vxipmi.connection.ConnectionManager;

import java.net.InetAddress;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Created by vkherbac on 14/04/14.
 */
public class IpmiChassisControl {

    private static final int STANDARD_CIPHER_SUITE = 3;

    private String ipAddress;
    private String username;
    private String password;
    private int port;

    private ConnectionManager connectionManager;
    private Connection connection;
    private CipherSuite cs;
    private ConnectionListenerImpl listener;
    private PrivilegeLevel privilege;

    public IpmiChassisControl (String ipAddress, String username,
                               String password, String privilege, int port) {

        this.ipAddress = ipAddress;
        this.username = username;
        this.password = password;
        this.port = port;

        if (privilege.equals("Administrator")) {
            this.privilege = PrivilegeLevel.Administrator;
        }
        else if (privilege.equals("Operator")) {
            this.privilege = PrivilegeLevel.Operator;
        }
        else {  this.privilege = PrivilegeLevel.User; }
    }

    protected void init() throws Exception {

        connectionManager = new ConnectionManager(port);

        int index = connectionManager.createConnection(InetAddress
                .getByName(ipAddress));
        List<CipherSuite> cipherSuites = connectionManager
                .getAvailableCipherSuites(index);

        cs = cipherSuites.get(STANDARD_CIPHER_SUITE);

        connectionManager.getChannelAuthenticationCapabilities(index, cs,
                privilege);

        connectionManager.startSession(index, cs, privilege, username,
                password, null);

        listener = new ConnectionListenerImpl();

        connectionManager.registerListener(index, listener);

        connection = connectionManager.getConnection(index);
    }

    protected void close() throws Exception {

        connection.closeSession();
        connection.disconnect();
        connectionManager.close();
    }

    public void chassisControlActionPowerUp() throws Exception {

        init();

        sendCommand(new ChassisControl(IpmiVersion.V20, cs,
                AuthenticationType.RMCPPlus,
                PowerCommand.PowerUp));

        close();
    }

    public void chassisControlActionPowerDown() throws Exception {

        init();

        sendCommand(new ChassisControl(IpmiVersion.V20, cs,
                AuthenticationType.RMCPPlus,
                PowerCommand.PowerDown));

        close();
    }

    private void sendCommand(IpmiCommandCoder coder) throws Exception {

        connection.sendIpmiCommand(coder);

        int time = 0;
        int timeout = 5000; //5s

        while (!listener.responseArrived && time < timeout) {
            Thread.sleep(1); //1ms
            time ++;
        }

        if (time < timeout) {
            ResponseData responseData = listener.getResponseData();
            //System.out.println("Response: " + responseData.toString() + "\n");
        }
        else { throw new Exception("Response timeout"); }
    }

    private class ConnectionListenerImpl implements ConnectionListener {

        private ResponseData responseData;
        private boolean responseArrived;

        public ResponseData getResponseData() {
            responseArrived = false;
            return responseData;
        }

        public ConnectionListenerImpl() {
            responseArrived = false;
            responseData = null;
        }

        @Override
        public void notify(ResponseData responseData, int handle, int tag,
                           Exception exception) {
            this.responseData = responseData;
            if (exception != null) {
                //System.out.println(exception.getMessage());
                this.responseData = null;
            }
            responseArrived = true;
        }
    }
}