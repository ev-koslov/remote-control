package ev.koslov.remote_control.client.gui;

import ev.koslov.data_exchanging.components.RequestBody;
import ev.koslov.remote_control.client.ClientApplication;
import ev.koslov.remote_control.client.RemoteControlClientInterface;
import ev.koslov.remote_control.common.actions.RCAction;
import ev.koslov.remote_control.common.bodies.FrameBody;
import ev.koslov.remote_control.common.taglib.RemoteControlTaglib;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


/**
 * Created by voron on 22.02.2017.
 */
public class RemoteControlController {
    private RemoteControlClientInterface anInterface;
    private long remoteAgentId;
    private boolean screenInitialized;

    private LinkedList<RCAction> pendingActions;

    private Future remoteImageReaderTask;

    @FXML
    private ImageView remoteScreen;

    @FXML
    private Pane parent;

    public void init(long remoteAgentId, RemoteControlClientInterface anInterface) {
        this.anInterface = anInterface;
        this.remoteAgentId = remoteAgentId;

        remoteScreen.fitHeightProperty().bind(parent.heightProperty());
        remoteScreen.fitWidthProperty().bind(parent.widthProperty());

        remoteScreen.fitWidthProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                System.out.println(remoteScreen.getBoundsInParent().getHeight());
            }
        });

        parent.getScene().getWindow().setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent windowEvent) {
                closeConnection();
            }
        });

        remoteImageReaderTask = ClientApplication.getExecutorService().scheduleWithFixedDelay(
                new RemoteImageReaderWorker(),
                0,
                10,
                TimeUnit.MILLISECONDS
        );
    }

    private void updateRemoteScreenImage(FrameBody remoteFrameBody) {

    }

    private void initializeScreen(FrameBody remoteFrameBody) {
        screenInitialized = true;
    }

    private void closeConnection() {
        System.out.println("Closed connection: " + remoteAgentId);
        remoteImageReaderTask.cancel(true);
    }

    private class RemoteImageReaderWorker implements Runnable {

        public void run() {
            try {
                RequestBody<RemoteControlTaglib> requestBody = new RequestBody<RemoteControlTaglib>(RemoteControlTaglib.GET_FRAME);
                FrameBody frameBody = (FrameBody) anInterface.clientToClientRequest(remoteAgentId, requestBody, 10000);

                if (screenInitialized) {

                } else {
                    initializeScreen(frameBody);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        }
    }

    private class RemoteControlWorker implements Runnable {
        private LinkedList<RCAction> actionsToSend;

        {
            actionsToSend = new LinkedList<RCAction>();
        }

        public void run() {
            actionsToSend.clear();

            while (pendingActions.size() > 0) {
                if (actionsToSend.size() > 50) {
                    break;
                }
            }
        }
    }
}

