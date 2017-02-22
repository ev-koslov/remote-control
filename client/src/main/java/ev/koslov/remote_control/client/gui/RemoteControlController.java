package ev.koslov.remote_control.client.gui;

import ev.koslov.remote_control.client.RemoteControlClientInterface;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.stage.WindowEvent;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by voron on 22.02.2017.
 */
public class RemoteControlController implements Initializable {
    private RemoteControlClientInterface anInterface;
    private long remoteAgentId;

    @FXML
    private ImageView remoteScreen;

    @FXML
    private Pane parent;


    public void initialize(URL url, ResourceBundle resourceBundle) {

        remoteScreen.fitHeightProperty().bind(parent.heightProperty());
        remoteScreen.fitWidthProperty().bind(parent.widthProperty());

        remoteScreen.fitWidthProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                System.out.println(remoteScreen.getBoundsInParent().getHeight());
            }
        });
    }

    public void init(long remoteAgentId, RemoteControlClientInterface anInterface) {
        this.anInterface = anInterface;
        this.remoteAgentId = remoteAgentId;

        parent.getScene().getWindow().setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent windowEvent) {
                closeConnection();
            }
        });
    }


    private void closeConnection(){
        System.out.println("Closed connection: "+remoteAgentId);
    }
}

