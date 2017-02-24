package ev.koslov.remote_control.agent;

import ev.koslov.data_exchanging.components.RequestBody;
import ev.koslov.data_exchanging.module.Client;
import ev.koslov.data_exchanging.module.ClientInterfaceAutoSorting;
import ev.koslov.remote_control.agent.request_processors.RemoteControlRequestProcessor;
import ev.koslov.remote_control.common.taglib.ServerTaglib;

import java.io.IOException;
import java.net.InetAddress;

public class AgentRemoteControlInterface extends ClientInterfaceAutoSorting{

    @Override
    public Client connect(String host, int port) throws IOException {
        Client client = super.connect(host, port);

        RequestBody<ServerTaglib> requestBody = new RequestBody<ServerTaglib>(ServerTaglib.INITIALIZE_AGENT);

        InetAddress inetAddress = InetAddress.getLocalHost();
        requestBody.setProperty("hostName", inetAddress.getHostName());
        requestBody.setProperty("hostIP", inetAddress.getHostAddress());

        clientToServerRequest(requestBody);

        this.addRequestProcessor(new RemoteControlRequestProcessor());

        return client;
    }
}
