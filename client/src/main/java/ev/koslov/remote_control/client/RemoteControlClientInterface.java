package ev.koslov.remote_control.client;

import ev.koslov.data_exchanging.components.RequestBody;
import ev.koslov.data_exchanging.components.ResponseBody;
import ev.koslov.data_exchanging.module.ClientInterfaceAutoSorting;
import ev.koslov.remote_control.common.actions.RCAction;
import ev.koslov.remote_control.common.bodies.FrameBody;
import ev.koslov.remote_control.common.bodies.RCActionBody;
import ev.koslov.remote_control.common.dto.AgentInfo;
import ev.koslov.remote_control.common.taglib.RemoteControlTaglib;
import ev.koslov.remote_control.common.taglib.ServerTaglib;

import java.io.IOException;
import java.util.List;

public class RemoteControlClientInterface extends ClientInterfaceAutoSorting {

    public List<AgentInfo> getAvailableAgents() throws IOException, InterruptedException {
        RequestBody<ServerTaglib> requestBody = new RequestBody<ServerTaglib>(ServerTaglib.GET_AGENT_CONNECTIONS);
        ResponseBody responseBody = clientToServerRequest(requestBody, 10000);
        List<AgentInfo> agentInfos = responseBody.getProperty("agentConnections");
        return agentInfos;
    }

    public FrameBody getRemoteFrame(long agentId) throws IOException, InterruptedException {
        RequestBody<RemoteControlTaglib> requestBody = new RequestBody<RemoteControlTaglib>(RemoteControlTaglib.GET_FRAME);
        FrameBody responseBody = (FrameBody) clientToClientRequest(agentId, requestBody, 10000);
        return responseBody;
    }

    public void sendControlActions(long agentId, List<RCAction> actions) throws IOException {
        RCActionBody actionBody = new RCActionBody(actions);
        clientToClientRequest(agentId, actionBody);
    }
}
