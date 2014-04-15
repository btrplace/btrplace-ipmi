/**
 * Created by vkherbac on 14/04/14.
 */

import btrplace.executor.ExecutorException;
import btrplace.executor.Actuator;
import btrplace.plan.event.Action;
import btrplace.plan.event.BootNode;

public class PowerUpActuator implements Actuator {

    private String ipAddress;
    private String username;
    private String password;
    private String privilege;
    private int port;
    private BootNode action;
    private IpmiChassisControl ipmiCC;

    public PowerUpActuator(BootNode action, String ipAddress, String username,
                           String password, String privilege, int port) {

        this.action = action;
        this.ipAddress = ipAddress;
        this.username = username;
        this.password = password;
        this.privilege = privilege;
        this.port = port;
    }

    @Override
    public void execute() throws ExecutorException {

        ipmiCC = new IpmiChassisControl(ipAddress, username, password,
                privilege, port);

        try {
            ipmiCC.chassisControlActionPowerUp();
        } catch (Exception e) {
            throw new ExecutorException(this, e.getMessage());
        }
    }

    @Override
    public Action getAction() { return action; }
}