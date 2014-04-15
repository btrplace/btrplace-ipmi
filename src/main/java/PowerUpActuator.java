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

import btrplace.executor.ExecutorException;
import btrplace.executor.Actuator;
import btrplace.plan.event.Action;
import btrplace.plan.event.BootNode;

/**
 *  An actuator to execute the BootNode action
 *
 * @author Vincent KHERBACHE
 */
public class PowerUpActuator implements Actuator {

    private String ipAddress;
    private String username;
    private String password;
    private String privilege;
    private int port;
    private int bootDuration;
    private BootNode action;

    /**
     * Initiates the PowerUp actuator
     *
     * @param action        the action to execute
     * @param ipAddress     the ip address of the destination node
     * @param username      the username to authenticate with the BMC
     * @param password      the password to authenticate with the BMC
     * @param privilege     the user privilege level
     * @param port          the port that library will bind to (waiting for answer)
     * @param bootDuration  the estimated boot duration of the node
     */
    public PowerUpActuator(BootNode action, String ipAddress, String username,
                           String password, String privilege, int port,
                           int bootDuration) {

        this.action = action;
        this.ipAddress = ipAddress;
        this.username = username;
        this.password = password;
        this.privilege = privilege;
        this.port = port;
        this.bootDuration = bootDuration;
    }

    /**
     * Execute the action by establishing a session to the remote node BMC
     * and sending the PowerUp command
     *
     * @throws ExecutorException
     */
    @Override
    public void execute() throws ExecutorException {

        IpmiChassisControl ipmiCC = new IpmiChassisControl(ipAddress, username,
                password, privilege, port);

        // Boot the node using IPMI and wait for the expected boot duration
        try {
            ipmiCC.chassisControlActionPowerUp();
            Thread.sleep(bootDuration * 1000);
        } catch (Exception e) {
            throw new ExecutorException(this, e);
        }
    }

    @Override
    public Action getAction() { return action; }
}