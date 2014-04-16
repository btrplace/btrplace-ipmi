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

import btrplace.executor.ActuatorBuilder;
import btrplace.executor.ExecutorException;
import btrplace.model.Attributes;
import btrplace.model.Model;
import btrplace.plan.event.ShutdownNode;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * A builder to create a {@link PowerDownActuator} actuator from a {@link btrplace.plan.event.ShutdownNode} action
 * Credentials and IPMI parameters are retrieved from a properties file using {@link IPMIProperties}.
 * The node IP is retrieved from the {@link btrplace.model.Attributes} "ip"
 *
 * @author Vincent KHERBACHE
 */
public class PowerDownActuatorBuilder implements ActuatorBuilder<ShutdownNode> {

    private String path;

    /**
     * Make a new builder
     *
     * @param p the path to the property file
     */
    public PowerDownActuatorBuilder(String p) {
        path = p;
    }


    @Override
    public Class<ShutdownNode> getAssociatedAction() {
        return ShutdownNode.class;
    }

    /**
     * Get the path of the properties file
     *
     * @return a path
     */
    public String getProperties() {
        return path;
    }

    /**
     * Set the path of the properties file
     *
     * * @param path a path
     */
    public void setProperties(String path) {
        this.path = path;
    }

    @Override
    public PowerDownActuator build(Model model, ShutdownNode action) throws ExecutorException {
        // Get the node attribute (ip address)
        Attributes attrs = model.getAttributes();
        String ipAddress = attrs.getString(action.getNode(), "ip");
        if (ipAddress == null) {
            throw new ExecutorException(action);
        }

        // Trying to load the config file
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(path));

            // Create and return the PowerUp actuator
            return new PowerDownActuator(action,
                    ipAddress,
                    IPMIProperties.getUsername(properties),
                    IPMIProperties.getPassword(properties),
                    IPMIProperties.getPrivilegeLevel(properties),
                    IPMIProperties.getAuthenticationType(properties),
                    IPMIProperties.getIpmiVersion(properties),
                    IPMIProperties.getLocalPort(properties));
        } catch (Exception e) {
            throw new ExecutorException("Unable to build the actuator for " + action, e);
        }
    }
}