package ev.koslov.remote_control.server;

import ev.koslov.data_exchanging.components.Message;
import ev.koslov.data_exchanging.components.RequestBody;
import ev.koslov.data_exchanging.components.ResponseBody;
import ev.koslov.data_exchanging.module.ServerConnection;
import ev.koslov.data_exchanging.module.ServerInterface;
import ev.koslov.remote_control.common.dto.AgentInfo;
import ev.koslov.remote_control.common.taglib.ServerTaglib;
import ev.koslov.remote_control.server.components.AgentConnectionAttachment;

import java.io.IOException;
import java.util.ArrayList;

public class RemoteControlServerInterface extends ServerInterface {

    @Override
    protected void processRequestFromClient(Message request) {
        try {

            RequestBody<ServerTaglib> requestBody = request.getBody();

            switch (requestBody.getCommand()) {

                case INITIALIZE_AGENT: {
                    initializeAgent(request.getHeader(), requestBody);
                    break;
                }

                case GET_AGENT_CONNECTIONS: {
                    sendConnectedAgents(request.getHeader());
                    break;
                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void processMessageFromClientToClient(Message message) {
        try {
            forward(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets data from cient request and attaches client info to connection
     * @param header request header
     * @param requestBody body with data
     */
    private void initializeAgent(Message.Header header, RequestBody requestBody) {
        AgentConnectionAttachment attachment = new AgentConnectionAttachment();

        attachment.setConnectionId(header.getSourceId());
        attachment.setHostName((String) requestBody.getProperty("hostName"));
        attachment.setHostIP((String) requestBody.getProperty("hostIP"));

        getAssociatedEndpoint().getConnection(header.getSourceId()).attach(attachment);
    }

    /**
     * Creates list of connected agents and send it to remote client
     * @param header request header
     * @throws IOException
     */
    private void sendConnectedAgents(Message.Header header) throws IOException {
        ArrayList<AgentInfo> agentInfoList = new ArrayList<AgentInfo>();

        //iterate all of connections and select any connection that has AgentConnectionAttachment as attachment
        for (ServerConnection connection : getAssociatedEndpoint().getConnections()) {
            if (connection.attachment() != null && connection.attachment() instanceof AgentConnectionAttachment) {
                AgentConnectionAttachment attachment = (AgentConnectionAttachment) connection.attachment();
                AgentInfo agentInfo = new AgentInfo();
                agentInfo.setAgentId(attachment.getConnectionId());
                agentInfo.setHostName(attachment.getHostName());
                agentInfoList.add(agentInfo);
            }
        }

        ResponseBody responseBody = new ResponseBody();
        responseBody.setProperty("agentConnections", agentInfoList);

        response(header, responseBody);
    }
}
