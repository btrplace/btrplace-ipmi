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
import btrplace.plan.event.Action;
import btrplace.plan.event.BootNode;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * A builder to create a PowerUp actuator from a BootNode action
 *
 * @author Vincent KHERBACHE
 */
public class PowerUpActuatorBuilder implements ActuatorBuilder {

    @Override
    public Class getAssociatedAction() { return BootNode.class; }

    @Override
    public Actuator build(Action action) {

        Properties properties = new Properties();

        // Trying to load the config file
        try {
            properties.load(new FileInputStream(
                    "src/main/resources/connection.properties"));
        } catch (IOException e) {

            // Print the error message into the console
            // TODO: throw a new exception
            System.out.println(e.getMessage());
        }

        // Create and return the PowerUp actuator
        return new PowerUpActuator((BootNode)action,
                properties.getProperty("ipAddress"),
                properties.getProperty("username"),
                properties.getProperty("password"),
                properties.getProperty("privilege"),
                Integer.getInteger(properties.getProperty("bootDuration")),
                Integer.getInteger(properties.getProperty("port")));
    }
}