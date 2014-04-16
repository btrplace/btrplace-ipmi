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

import btrplace.executor.Actuator;
import btrplace.executor.ActuatorBuilder;
import btrplace.model.Attributes;
import btrplace.model.Model;
import btrplace.model.Node;
import btrplace.model.view.NamingService;
import btrplace.plan.event.BootNode;
import com.veraxsystems.vxipmi.coding.commands.IpmiVersion;
import com.veraxsystems.vxipmi.coding.commands.PrivilegeLevel;
import com.veraxsystems.vxipmi.coding.protocol.AuthenticationType;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * A builder to create a PowerUp actuator from a BootNode action
 *
 * @author Vincent KHERBACHE
 */
public class PowerUpActuatorBuilder implements ActuatorBuilder<BootNode> {

    @Override
    public Class<BootNode> getAssociatedAction() { return BootNode.class; }

    @Override
    public Actuator build(Model model, BootNode action) {

        PrivilegeLevel privilege;
        AuthenticationType authType;
        IpmiVersion ipmiVersion;

        // Get the node attribute (ip address)
        Attributes attrs = model.getAttributes();
        String ipAddress = attrs.getString(action.getNode(), "ip");

        // Get the estimated boot duration
        int bootDuration = (action.getEnd()-action.getStart());

        // Trying to load the config file
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(
                    "src/main/resources/connection.properties"));
        } catch (IOException e) {

            // Print the error message into the console
            // TODO: throw a new exception
            System.out.println(e.getMessage());
        }

        /*
        Select the right privilege level from config file
            default: User
        */
        switch (properties.getProperty("privilege").toLowerCase()) {
            case "administrator":
                privilege = PrivilegeLevel.Administrator;
                break;
            case "operator":
                privilege = PrivilegeLevel.Operator;
                break;
            default:
                privilege = PrivilegeLevel.User;
                break;
        }

        /*
        Select the right authentication type from config file
            default: RMCPPlus
        */
        switch (properties.getProperty("authentication").toLowerCase()) {
            case "none":
                authType = AuthenticationType.None;
                break;
            case "simple":
                authType = AuthenticationType.Simple;
                break;
            case "md2":
                authType = AuthenticationType.Md2;
                break;
            case "md5":
                authType = AuthenticationType.Md5;
                break;
            case "oem":
                authType = AuthenticationType.Oem;
                break;
            default:
                authType = AuthenticationType.RMCPPlus;
                break;
        }

        /*
        Select the right IPMI version
            default: 20
        */
        switch (Integer.getInteger(properties.getProperty("ipmiVersion"))) {
            case 15:
                ipmiVersion = IpmiVersion.V15;
                break;
            default:
                ipmiVersion = IpmiVersion.V20;
                break;
        }

        // Create and return the PowerUp actuator
        return new PowerUpActuator(action,
                ipAddress,
                bootDuration,
                properties.getProperty("username"),
                properties.getProperty("password"),
                privilege,
                authType,
                ipmiVersion,
                Integer.getInteger(properties.getProperty("port")));
    }
}