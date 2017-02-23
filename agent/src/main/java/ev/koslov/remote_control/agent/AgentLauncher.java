package ev.koslov.remote_control.agent;

import java.io.IOException;

/**
 * Created by voron on 22.02.2017.
 */
public class AgentLauncher {
    public static void main(String[] args) throws IOException {
        AgentRemoteControlInterface agentRemoteControlInterface = new AgentRemoteControlInterface();
        agentRemoteControlInterface.connect("sysadmin", 5555);
    }
}
