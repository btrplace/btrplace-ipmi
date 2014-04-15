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
import btrplace.plan.event.ShutdownNode;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PowerDownActuatorBuilder implements ActuatorBuilder {

    @Override
    public Class getAssociatedAction() { return ShutdownNode.class; }

    @Override
    public Actuator build(Action action) {

        Properties properties = new Properties();

        try {
            properties.load(new FileInputStream(
                    "src/main/resources/connection.properties"));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        // Create and return the PowerDown actuator
        return new PowerDownActuator((ShutdownNode)action,
                properties.getProperty("ipAddress"),
                properties.getProperty("username"),
                properties.getProperty("password"),
                properties.getProperty("privilege"),
                Integer.getInteger(properties.getProperty("port")));
    }
}