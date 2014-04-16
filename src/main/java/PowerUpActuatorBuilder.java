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
import btrplace.plan.event.BootNode;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * A builder to create a {@link PowerUpActuator} from a {@link btrplace.plan.event.BootNode} action
 * Credentials and IPMI parameters are retrieved from a properties file using {@link IPMIProperties}.
 * The node IP is retrieved from the {@link btrplace.model.Attributes} "ip"
 *
 * @author Vincent KHERBACHE
 */
public class PowerUpActuatorBuilder implements ActuatorBuilder<BootNode> {

    private String path;

    /**
     * Make a new builder.
     *
     * @param p the path to the property file
     */
    public PowerUpActuatorBuilder(String p) {
        path = p;
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
     * @return a path
     */
    public void setProperties(String path) {
        this.path = path;
    }

    @Override
    public Class<BootNode> getAssociatedAction() { return BootNode.class;
    }

    @Override
    public PowerUpActuator build(Model model, BootNode action) throws ExecutorException {

        // Get the node attribute (ip address)
        Attributes attrs = model.getAttributes();
        String ipAddress = attrs.getString(action.getNode(), "ip");
        if (ipAddress == null) {
            throw new ExecutorException(action);
        }
        // Get the estimated boot duration
        int bootDuration = action.getEnd()-action.getStart();

        // Trying to load the config file
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(path));

        // Create and return the PowerUp actuator
        return new PowerUpActuator(action,
                ipAddress,
                bootDuration,
                IPMIProperties.getUsername(properties),
                IPMIProperties.getPassword(properties),
                IPMIProperties.getPrivilegeLevel(properties),
                IPMIProperties.getAuthenticationType(properties),
                IPMIProperties.getIpmiVersion(properties),
                IPMIProperties.getLocalPort(properties));
        } catch (Exception e) {
            //TODO: not accurate at all but no suitable constructor
            throw new ExecutorException(action);
        }
    }
}