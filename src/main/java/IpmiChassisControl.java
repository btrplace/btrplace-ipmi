/*
 * Copyright (c) 2014 INRIA
 *
 * This file is part of btrplace.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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

/**
 * Control nodes' chassis through IPMI
 *
 * @author Vincent KHERBACHE
 */
public class IpmiChassisControl {

    private static final int STANDARD_CIPHER_SUITE = 3;

    /**
     * Default timeout for an answer in secs.
     */
    private static final int DEFAULT_TIMEOUT = 5;

    private String ipAddress;
    private String username;
    private String password;
    private int port;

    private int timeout;
    private ConnectionManager connectionManager;
    private Connection connection;
    private CipherSuite cs;
    private ConnectionListenerImpl listener;
    private PrivilegeLevel privilege;
    private AuthenticationType authType;
    private IpmiVersion ipmiVersion;

    /**
     * Initiates IpmiChassisControl
     *
     * @param ipAddress     the IP address of the destination node
     * @param username      the username  to authenticate with the BMC
     * @param password      the password to authenticate with the BMC
     * @param privilege     the user privilege level
     * @param authType      the authentication type to use
     * @param ipmiVersion   the version of IPMI messages encoding/decoding
     * @param port          the port that library will bind to (waiting for
     *                      answer)
     */
    public IpmiChassisControl (String ipAddress, String username,
                               String password, PrivilegeLevel privilege,
                               AuthenticationType authType,
                               IpmiVersion ipmiVersion, int port) {

        this.ipAddress = ipAddress;
        this.username = username;
        this.password = password;
        this.privilege = privilege;
        this.authType = authType;
        this.ipmiVersion = ipmiVersion;
        this.port = port;
        timeout = DEFAULT_TIMEOUT;
    }

    /**
     * Initialize the connection to the remote node and establish a session.
     *
     * @throws Exception if an error occurred.
     */
    protected void init() throws Exception {

        connectionManager = new ConnectionManager(port);

        // Create a new connection to the remote node
        int index = connectionManager.createConnection(InetAddress
                .getByName(ipAddress));

        // Get the available Cipher suites and select the third (std)
        List<CipherSuite> cipherSuites = connectionManager
                .getAvailableCipherSuites(index);
        cs = cipherSuites.get(STANDARD_CIPHER_SUITE);

        // Check the remote authentication capabilities
        connectionManager.getChannelAuthenticationCapabilities(index, cs,
                privilege);

        // Establish the session using the right credentials
        connectionManager.startSession(index, cs, privilege, username,
                password, null);

        // Create and register a new listener to the connection
        listener = new ConnectionListenerImpl();
        connectionManager.registerListener(index, listener);

        connection = connectionManager.getConnection(index);

    }

    /**
     * Set the timeout value before considering
     * the request was lost
     *
     * @param t the timeout in second
     */
    public void setTimeout(int t) {
        this.timeout = t;
    }

    /**
     * Get the timeout.
     *
     * @return a value in second
     */
    public int getTimeout() {
        return this.timeout;
    }
    /**
     * Close the connection to the remote node
     *
     * @throws Exception
     */
    protected void close() throws Exception {

        connection.closeSession();
        connection.disconnect();
        connectionManager.close();
    }

    /**
     * Power up the remote node
     *
     * @throws Exception if an error occurred
     */
    public void chassisControlActionPowerUp() throws Exception {

        init();
        sendCommand(new ChassisControl(ipmiVersion, cs, authType,
                PowerCommand.PowerUp));
        close();
    }

    /**
     * Power down the remote node
     *
     * @throws Exception
     */
    public void chassisControlActionPowerDown() throws Exception {

        init();
        sendCommand(new ChassisControl(ipmiVersion, cs, authType,
                PowerCommand.PowerDown));
        close();
    }

    /**
     * Send an IPMI command through the existing connection
     *
     * @param coder The IPMI command to send
     * @throws Exception
     */
    private void sendCommand(IpmiCommandCoder coder) throws Exception {

        int timeout = DEFAULT_TIMEOUT; //5s timeout

        // Send the IPMI command
        connection.sendIpmiCommand(coder);

        // Waiting for the response
        while (!listener.responseArrived && timeout > 0) {
            Thread.sleep(1000); //1s
            timeout--;
        }
        if (timeout < 0) {
            throw new Exception("Response timeout");
        }
        //There is no need to check the response. We just wait
        //to be sure the request has been send successfully
    }

    /**
     * Implement ConnectionListener interface to manage the IPMI response
     * processing
     */
    private class ConnectionListenerImpl implements ConnectionListener {

        private ResponseData responseData;
        private boolean responseArrived;

        /**
         * Getter for the response data
         *
         * @return  the response data
         */
        public ResponseData getResponseData() {
            responseArrived = false;
            return responseData;
        }

        /**
         * Initiates ConnectionListener
         */
        public ConnectionListenerImpl() {
            responseArrived = false;
            responseData = null;
        }

        @Override
        public void notify(ResponseData responseData, int handle, int tag,
                           Exception exception) {
            this.responseData = responseData;
            if (exception != null) {
                // Print the error message into the console
                System.out.println(exception.getMessage());
                this.responseData = null;
            }
            responseArrived = true;
        }
    }
}