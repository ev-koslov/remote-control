package ev.koslov.remote_control.server;

import ev.koslov.data_exchanging.module.Server;

import java.io.IOException;

public class ServerStarter {
    public static void main(String[] args) throws IOException {
        if (args.length != 1){
            throw new RuntimeException("Port must be specified.");
        }

        int port = Integer.parseInt(args[0]);

        //initialization of server interface and starting server
        RemoteControlServerInterface remoteControlServerInterface = new RemoteControlServerInterface();
        Server server = remoteControlServerInterface.startServer(port);
    }
}
