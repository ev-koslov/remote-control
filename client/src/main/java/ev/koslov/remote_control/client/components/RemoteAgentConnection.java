package ev.koslov.remote_control.client.components;

import ev.koslov.data_exchanging.components.RequestBody;
import ev.koslov.data_exchanging.module.ClientInterface;
import ev.koslov.remote_control.common.actions.RCAction;
import ev.koslov.remote_control.common.bodies.FrameBody;
import ev.koslov.remote_control.common.bodies.RCActionBody;
import ev.koslov.remote_control.common.taglib.RemoteControlTaglib;
import ev.koslov.remote_control.common.taglib.ServerTaglib;

import java.io.IOException;
import java.util.List;


public class RemoteAgentConnection {
    private long agentId;
    private ClientInterface clientInterface;

    public RemoteAgentConnection(long agentId, ClientInterface clientInterface) {
        this.agentId = agentId;
        this.clientInterface = clientInterface;
    }

    public FrameBody getRemoteFrame() throws IOException, InterruptedException {
        RequestBody<RemoteControlTaglib> requestBody = new RequestBody<RemoteControlTaglib>(RemoteControlTaglib.GET_FRAME);
        return (FrameBody) clientInterface.clientToClientRequest(agentId, requestBody, 10000);
    }

    public void sendControlActions(List<RCAction> actions) throws IOException {
        RCActionBody actionBody = new RCActionBody(actions);
        clientInterface.clientToClientRequest(agentId, actionBody);
    }

    public void disconnect() {
        RequestBody<ServerTaglib> requestBody = new RequestBody<ServerTaglib>(ServerTaglib.DISCONNECT);
        requestBody.setProperty("agentId", agentId);
        try {
            clientInterface.clientToServerRequest(requestBody);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
