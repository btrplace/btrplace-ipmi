/**
 * Created by vkherbac on 14/04/14.
 */

import btrplace.executor.Actuator;
import btrplace.executor.ActuatorBuilder;
import btrplace.plan.event.Action;
import btrplace.plan.event.BootNode;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PowerDownActuatorBuilder implements ActuatorBuilder {

    @Override
    public Class getAssociatedAction() { return BootNode.class; }

    @Override
    public Actuator build(Action action) {

        Properties properties = new Properties();

        try {
            properties.load(new FileInputStream(
                    "src/main/resources/connection.properties"));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return new PowerDownActuator((BootNode)action,
                properties.getProperty("ipAddress"),
                properties.getProperty("username"),
                properties.getProperty("password"),
                properties.getProperty("privilege"),
                Integer.getInteger(properties.getProperty("port")));
    }
}