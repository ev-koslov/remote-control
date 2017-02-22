package ev.koslov.remote_control.client.workers;

import ev.koslov.remote_control.client.RemoteControlClientInterface;

import java.io.IOException;

/**
 * Created by voron on 22.02.2017.
 */
public class ConnectionKeeper implements Runnable {
    private RemoteControlClientInterface anInterface;
    private String host;
    private int port;

    public ConnectionKeeper(String host, int port, RemoteControlClientInterface anInterface) {
        this.anInterface = anInterface;
        this.host = host;
        this.port = port;
    }

    public void run() {
        try {
            if (!anInterface.isConnected()) {
                anInterface.connect(host, port);
            }
        } catch (IOException e) {
            //do nothing
        }
    }
}
