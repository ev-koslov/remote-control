package ev.koslov.remote_control.client.gui;

import ev.koslov.remote_control.client.ClientApplication;
import ev.koslov.remote_control.client.components.RemoteAgentConnection;
import ev.koslov.remote_control.common.actions.*;
import ev.koslov.remote_control.common.bodies.FrameBody;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;



public class RemoteControlController {

    private Image remoteCursor;

    private RemoteAgentConnection connection;

    private LinkedList<RCAction> pendingEvents;

    private Future remoteImageUpdateWorkerTask, remoteControlWorkerTask;

    private MouseEventHandler mouseEventHandler;
    private KeyboardEventHandler keyboardEventHandler;
    private ScrollEventHandler scrollEventHandler;

    @FXML
    private ImageView remoteScreen;

    private Stage stage;

    private double frameCrop;

    @FXML
    public void startRemoteControl() {
        remoteScreen.addEventHandler(MouseEvent.MOUSE_MOVED, mouseEventHandler);
        remoteScreen.addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseEventHandler);
        remoteScreen.addEventHandler(MouseEvent.DRAG_DETECTED, mouseEventHandler);
        remoteScreen.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEventHandler);
        remoteScreen.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseEventHandler);
        remoteScreen.addEventHandler(ScrollEvent.SCROLL, scrollEventHandler);

        stage.addEventFilter(KeyEvent.KEY_PRESSED, keyboardEventHandler);
        stage.addEventFilter(KeyEvent.KEY_RELEASED, keyboardEventHandler);
    }

    @FXML
    public void stopRemoteControl() {
        remoteScreen.removeEventHandler(MouseEvent.MOUSE_MOVED, mouseEventHandler);
        remoteScreen.removeEventHandler(MouseEvent.MOUSE_DRAGGED, mouseEventHandler);
        remoteScreen.removeEventHandler(MouseEvent.DRAG_DETECTED, mouseEventHandler);
        remoteScreen.removeEventHandler(MouseEvent.MOUSE_PRESSED, mouseEventHandler);
        remoteScreen.removeEventHandler(MouseEvent.MOUSE_RELEASED, mouseEventHandler);
        remoteScreen.removeEventHandler(ScrollEvent.SCROLL, scrollEventHandler);

        stage.removeEventFilter(KeyEvent.KEY_PRESSED, keyboardEventHandler);
        stage.removeEventFilter(KeyEvent.KEY_RELEASED, keyboardEventHandler);
    }

    public void init(RemoteAgentConnection connection) {
        this.remoteCursor = new Image("/icons/cursor.png");

        this.connection = connection;

        this.pendingEvents = new LinkedList<RCAction>();

        this.mouseEventHandler = new MouseEventHandler();
        this.keyboardEventHandler = new KeyboardEventHandler();
        this.scrollEventHandler = new ScrollEventHandler();

        this.stage = (Stage) remoteScreen.getScene().getWindow();

        stage.setResizable(false);

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

        connection.disconnect();

        System.gc();
    }

    private class RemoteImageUpdaterWorker implements Runnable {
        private static final int TRANSPARENT_MASK = 0;
        private int currentScreenW, currentScreenH;
        private WritableImage showingImage, tempImage;
        private int frameCounter;
        private int[] receivedFrameBuffer;

        public void run() {
            try {

                FrameBody frameBody = connection.getRemoteFrame();

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

            tempImage.getPixelWriter().setPixels(0, 0, currentScreenW, currentScreenH, showingImage.getPixelReader(), 0, 0);

            Dimension scrDims = Toolkit.getDefaultToolkit().getScreenSize();

            double currentScreenAspectRatio = scrDims.getHeight() / scrDims.getWidth();
            int windowHeight = (int) (scrDims.getHeight() * 0.9);

            stage.setX(10);
            stage.setY(10);

            stage.setHeight(windowHeight);
            stage.setWidth(windowHeight / currentScreenAspectRatio);

            remoteScreen.setFitHeight(windowHeight - 60);
            remoteScreen.setFitWidth(windowHeight / currentScreenAspectRatio - 20);

            frameCrop = showingImage.getHeight() / remoteScreen.getBoundsInLocal().getHeight();

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

            PixelWriter showingImagePixelWriter = showingImage.getPixelWriter();

            showingImagePixelWriter.setPixels(0, 0, currentScreenW, currentScreenH, tempImage.getPixelReader(), 0, 0);

            //TODO: add remote cursor drawing

//            int cursorPosX = (remoteFrameBody.getPointerX() + 5 < currentScreenW) ? remoteFrameBody.getPointerX() : currentScreenW - 6;
//            int cursorPosY = (remoteFrameBody.getPointerY() + 5 < currentScreenH) ? remoteFrameBody.getPointerY() : currentScreenH - 6;
//
//            showingImagePixelWriter.setPixels(
//                    cursorPosX,
//                    cursorPosY,
//                    cursorPosX + 5,
//                    cursorPosY + 5,
//                    remoteCursor.getPixelReader(),
//                    0,
//                    0
//            );

        }

        private int getPixelFromReceivedBuffer(int x, int y) {
            return receivedFrameBuffer[currentScreenW * y + x];
        }
    }

    private class RemoteControlWorker implements Runnable {
        private final LinkedList<RCAction> actionsToSend;

        public RemoteControlWorker() {
            actionsToSend = new LinkedList<RCAction>();
        }

        public void run() {

            actionsToSend.clear();

            while (pendingEvents.size() > 0) {

                RCAction action = pendingEvents.removeFirst();

                if (actionsToSend.size() > 0 && actionsToSend.getLast() instanceof MouseMove) {
                    actionsToSend.removeLast();
                }

                actionsToSend.addLast(action);

                if (actionsToSend.size() > 50) {
                    break;
                }
            }

            if (actionsToSend.size() > 0) {
                try {
                    connection.sendControlActions(actionsToSend);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private class MouseEventHandler implements EventHandler<MouseEvent> {

        public void handle(MouseEvent mouseEvent) {
            mouseEvent.consume();

            int xPos = (int) (mouseEvent.getX() * frameCrop);
            int yPos = (int) (mouseEvent.getY() * frameCrop);

            if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_MOVED)) {
                pendingEvents.addLast(new MouseMove(xPos, yPos));
            }

            if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
                pendingEvents.addLast(new MouseDrag(xPos, yPos));
            }

            if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_PRESSED) ||
                    mouseEvent.getEventType().equals(MouseEvent.DRAG_DETECTED)) {
                byte mouseButton = -1;

                switch (mouseEvent.getButton()) {
                    case PRIMARY: {
                        mouseButton = 0;
                        break;
                    }
                    case MIDDLE: {
                        mouseButton = 1;
                        break;
                    }
                    case SECONDARY: {
                        mouseButton = 2;
                        break;
                    }
                }

                pendingEvents.addLast(new MousePress(xPos, yPos, mouseButton, true));
            }

            if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
                byte mouseButton = -1;

                switch (mouseEvent.getButton()) {
                    case PRIMARY: {
                        mouseButton = 0;
                        break;
                    }
                    case MIDDLE: {
                        mouseButton = 1;
                        break;
                    }
                    case SECONDARY: {
                        mouseButton = 2;
                        break;
                    }
                }

                pendingEvents.addLast(new MousePress(xPos, yPos, mouseButton, false));
            }
        }
    }

    private class KeyboardEventHandler implements EventHandler<KeyEvent> {
        private Field codeField;

        {
            for (Field field : KeyCode.class.getDeclaredFields()) {
                if (field.getName().equals("code")) {
                    codeField = field;
                    break;
                }
            }
        }

        public void handle(KeyEvent keyEvent) {
            keyEvent.consume();

            if (keyEvent.getEventType().equals(KeyEvent.KEY_PRESSED)) {
                pendingEvents.addLast(new KeyboardKeyPress(getIntKeyCodeFromFXEvent(keyEvent.getCode()), true));
            }
            if (keyEvent.getEventType().equals(KeyEvent.KEY_RELEASED)) {
                pendingEvents.addLast(new KeyboardKeyPress(getIntKeyCodeFromFXEvent(keyEvent.getCode()), false));
            }
        }

        private int getIntKeyCodeFromFXEvent(KeyCode keyCode) {
            int intKeyCode = -1;
            try {
                codeField.setAccessible(true);
                intKeyCode = codeField.getInt(keyCode);
                codeField.setAccessible(false);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return intKeyCode;
        }
    }

    private class ScrollEventHandler implements EventHandler<ScrollEvent> {

        public void handle(ScrollEvent scrollEvent) {
            scrollEvent.consume();
            pendingEvents.addLast(new MouseScroll((int) scrollEvent.getX(), (int) scrollEvent.getY(), (int) scrollEvent.getDeltaY()));
        }
    }
}

