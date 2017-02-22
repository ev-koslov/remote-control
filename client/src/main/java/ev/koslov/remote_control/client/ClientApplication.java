package ev.koslov.remote_control.client;

import ev.koslov.remote_control.client.gui.MainWindowController;
import ev.koslov.remote_control.client.workers.ConnectionKeeper;
import ev.koslov.remote_control.common.gui.FXUtils;
import ev.koslov.remote_control.common.gui.SceneControllerPair;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class ClientApplication extends Application {
    private static RemoteControlClientInterface remoteControlClientInterface;
    private static ScheduledExecutorService executorService;

    public static RemoteControlClientInterface getInterface() {
        return remoteControlClientInterface;
    }

    public static ScheduledExecutorService getExecutorService() {
        return executorService;
    }

    @Override
    public void start(Stage stage) throws Exception {
        remoteControlClientInterface = new RemoteControlClientInterface();
        executorService = Executors.newScheduledThreadPool(4);

        SceneControllerPair<MainWindowController> pair = FXUtils.loadPair("/fxml/main.fxml");
        pair.getController().init(remoteControlClientInterface);

        executorService.scheduleWithFixedDelay(
                new ConnectionKeeper("localhost", 5555, remoteControlClientInterface),
                0,
                1,
                TimeUnit.SECONDS
        );

        stage.setScene(pair.getScene());
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        remoteControlClientInterface.disconnect();
        executorService.shutdown();
        super.stop();
    }

    public static void main(String[] args) {
        ClientApplication.launch(args);
    }
}
