package ev.koslov.remote_control.common.dto;

import java.io.Serializable;

public class AgentInfo implements Serializable {
    private long agentId;
    private String hostName;

    public AgentInfo() {

    }

    public long getAgentId() {
        return agentId;
    }

    public void setAgentId(long agentId) {
        this.agentId = agentId;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    @Override
    public String toString() {
        return hostName;
    }
}
