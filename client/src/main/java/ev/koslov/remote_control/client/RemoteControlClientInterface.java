package ev.koslov.remote_control.client;

import ev.koslov.data_exchanging.components.RequestBody;
import ev.koslov.data_exchanging.components.ResponseBody;
import ev.koslov.data_exchanging.module.ClientInterfaceAutoSorting;
import ev.koslov.remote_control.client.components.RemoteAgentConnection;
import ev.koslov.remote_control.common.dto.AgentInfo;
import ev.koslov.remote_control.common.taglib.ServerTaglib;

import java.io.IOException;
import java.util.List;

public class RemoteControlClientInterface extends ClientInterfaceAutoSorting {

    public List<AgentInfo> getAvailableAgents() throws IOException, InterruptedException {
        RequestBody<ServerTaglib> requestBody = new RequestBody<ServerTaglib>(ServerTaglib.GET_AGENT_CONNECTIONS);
        ResponseBody responseBody = clientToServerRequest(requestBody, 10000);
        return responseBody.getProperty("agentConnections");
    }

    public RemoteAgentConnection initConnectionToAgent(long remoteAgentId) throws IOException, InterruptedException {
        RequestBody<ServerTaglib> requestBody = new RequestBody<ServerTaglib>(ServerTaglib.CONNECT);
        requestBody.setProperty("agentId", remoteAgentId);
        clientToServerRequest(requestBody, 5000);

        return new RemoteAgentConnection(remoteAgentId, this);
    }
}
