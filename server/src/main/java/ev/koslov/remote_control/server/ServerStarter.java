package ev.koslov.remote_control.server;

import ev.koslov.data_exchanging.module.Server;

import java.io.IOException;

public class ServerStarter {
    public static void main(String[] args) throws IOException {
        //initialization of server interface and starting server
        RemoteControlServerInterface remoteControlServerInterface = new RemoteControlServerInterface();
        Server server = remoteControlServerInterface.startServer(5555);
    }
}
