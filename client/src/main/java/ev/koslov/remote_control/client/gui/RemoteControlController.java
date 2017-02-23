package ev.koslov.remote_control.client.gui;

import ev.koslov.data_exchanging.components.RequestBody;
import ev.koslov.remote_control.client.ClientApplication;
import ev.koslov.remote_control.client.RemoteControlClientInterface;
import ev.koslov.remote_control.common.actions.RCAction;
import ev.koslov.remote_control.common.bodies.FrameBody;
import ev.koslov.remote_control.common.bodies.RCActionBody;
import ev.koslov.remote_control.common.taglib.RemoteControlTaglib;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.image.*;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.awt.image.BufferedImage;
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

    private LinkedList<InputEvent> pendingEvents;

    private Future remoteImageUpdateWorkerTask, remoteControlWorkerTask;

    private EventHandler mouseEventHandler, keyboardEventHandler, scrollEventHandler;

    @FXML
    private ImageView remoteScreen;

    private Stage stage;

    @FXML
    public void startRemoteControl() {
        remoteScreen.addEventHandler(MouseEvent.MOUSE_MOVED, mouseEventHandler);
        remoteScreen.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEventHandler);
        remoteScreen.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseEventHandler);
        remoteScreen.addEventHandler(ScrollEvent.SCROLL, scrollEventHandler);

        Stage stage = (Stage) remoteScreen.getScene().getWindow();

        stage.addEventFilter(KeyEvent.KEY_PRESSED, keyboardEventHandler);
        stage.addEventFilter(KeyEvent.KEY_RELEASED, keyboardEventHandler);
    }

    @FXML
    public void stopRemoteControl() {
        remoteScreen.removeEventHandler(MouseEvent.MOUSE_MOVED, mouseEventHandler);
        remoteScreen.removeEventHandler(MouseEvent.MOUSE_PRESSED, mouseEventHandler);
        remoteScreen.removeEventHandler(MouseEvent.MOUSE_RELEASED, mouseEventHandler);
        remoteScreen.removeEventHandler(ScrollEvent.SCROLL, scrollEventHandler);

        Stage stage = (Stage) remoteScreen.getScene().getWindow();

        stage.removeEventFilter(KeyEvent.KEY_PRESSED, keyboardEventHandler);
        stage.removeEventFilter(KeyEvent.KEY_RELEASED, keyboardEventHandler);
    }

    public void init(long remoteAgentId, RemoteControlClientInterface anInterface) {
        this.anInterface = anInterface;
        this.remoteAgentId = remoteAgentId;
        this.pendingEvents = new LinkedList<InputEvent>();

        this.mouseEventHandler = new MouseEventHandler();
        this.keyboardEventHandler = new KeyboardEventHandler();
        this.scrollEventHandler = new ScrollEventHandler();

        this.stage = (Stage) remoteScreen.getScene().getWindow();

        stage.widthProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                remoteScreen.setFitWidth(t1.doubleValue());
            }
        });

        stage.heightProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                remoteScreen.setFitHeight(t1.doubleValue());
            }
        });

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent windowEvent) {
                closeConnection();
            }
        });

        remoteImageUpdateWorkerTask = ClientApplication.getExecutorService().scheduleWithFixedDelay(
                new RemoteImageUpdaterWorker(),
                0,
                10,
                TimeUnit.MILLISECONDS
        );

        remoteControlWorkerTask = ClientApplication.getExecutorService().scheduleWithFixedDelay(
                new RemoteControlWorker(),
                100,
                100,
                TimeUnit.MILLISECONDS
        );


    }


    private void closeConnection() {
        remoteImageUpdateWorkerTask.cancel(true);
        remoteControlWorkerTask.cancel(true);
        System.gc();
    }

    private class RemoteImageUpdaterWorker implements Runnable {
        private static final int TRANSPARENT_MASK = (0 << 24) | (0 << 16) | (0 << 8) | 0;
        private int currentScreenW, currentScreenH;
        private WritableImage showingImage, tempImage;
        private int frameCounter;
        private int[] receivedFrameBuffer;

        public void run() {
            try {
                RequestBody<RemoteControlTaglib> requestBody = new RequestBody<RemoteControlTaglib>(RemoteControlTaglib.GET_FRAME);
                FrameBody frameBody = (FrameBody) anInterface.clientToClientRequest(remoteAgentId, requestBody, 10000);

                updateRemoteScreenImage(frameBody);

                frameCounter++;

                if (frameCounter > 100) {
                    frameCounter %= 100;
                    System.gc();
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        private void initializeScreen(FrameBody remoteFrameBody) throws IOException {
            currentScreenW = remoteFrameBody.getFrameW();
            currentScreenH = remoteFrameBody.getFrameH();

            BufferedImage image = remoteFrameBody.getImage(BufferedImage.TYPE_INT_RGB);

            showingImage = SwingFXUtils.toFXImage(image, null);
            tempImage = new WritableImage(currentScreenW, currentScreenH);

            Platform.runLater(new Runnable() {
                public void run() {
                    remoteScreen.setImage(showingImage);
                }
            });
        }

        private void updateRemoteScreenImage(FrameBody remoteFrameBody) throws IOException {

            if (currentScreenW != remoteFrameBody.getFrameW() || currentScreenH != remoteFrameBody.getFrameH()) {
                initializeScreen(remoteFrameBody);
                return;
            }

            receivedFrameBuffer = remoteFrameBody.getFrameBuffer();

            PixelWriter tempPixelWriter = tempImage.getPixelWriter();

            for (int x = 0; x < currentScreenW; x++) {
                for (int y = 0; y < currentScreenH; y++) {

                    int pixel = getPixelFromReceivedBuffer(x, y);

                    if (pixel != TRANSPARENT_MASK) {
                        tempPixelWriter.setArgb(x, y, pixel);
                    }

                }
            }

            showingImage.getPixelWriter().setPixels(0, 0, currentScreenW, currentScreenH, tempImage.getPixelReader(), 0, 0);
        }

        private int getPixelFromReceivedBuffer(int x, int y) {
            return receivedFrameBuffer[currentScreenW * y + x];
        }
    }

    private class RemoteControlWorker implements Runnable {
        private final LinkedList<RCAction> actionsToSend;

        public RemoteControlWorker() {
            actionsToSend = new LinkedList<RCAction>();;
        }

        public void run() {

            actionsToSend.clear();

            while (pendingEvents.size() > 0) {
                pendingEvents.removeFirst();
//                actionsToSend.addLast(pendingEvents.removeFirst());

                if (actionsToSend.size() > 50) {
                    break;
                }

            }

            RCActionBody rcActionBody = new RCActionBody(actionsToSend);

            try {
                anInterface.clientToClientRequest(remoteAgentId, rcActionBody);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private class MouseEventHandler implements EventHandler<MouseEvent> {

        public void handle(MouseEvent mouseEvent) {
            System.out.println(mouseEvent);

            mouseEvent.consume();

            if (!pendingEvents.isEmpty() && pendingEvents.getLast() instanceof MouseEvent) {
                MouseEvent prevMouseEvent = (MouseEvent) pendingEvents.getLast();

                if (prevMouseEvent.getEventType() == MouseEvent.MOUSE_MOVED ||
                        prevMouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                    pendingEvents.removeLast();
                }

            }

            pendingEvents.addLast(mouseEvent);
        }
    }

    private class KeyboardEventHandler implements EventHandler<KeyEvent> {

        public void handle(KeyEvent keyEvent) {
            keyEvent.consume();
            pendingEvents.addLast(keyEvent);
        }
    }

    private class ScrollEventHandler implements EventHandler<ScrollEvent> {

        public void handle(ScrollEvent scrollEvent) {
            scrollEvent.consume();
            pendingEvents.addLast(scrollEvent);
        }
    }
}

