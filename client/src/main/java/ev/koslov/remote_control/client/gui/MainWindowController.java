package ev.koslov.remote_control.client.gui;

import ev.koslov.remote_control.client.ClientApplication;
import ev.koslov.remote_control.client.RemoteControlClientInterface;
import ev.koslov.remote_control.common.dto.AgentInfo;
import ev.koslov.remote_control.common.gui.FXUtils;
import ev.koslov.remote_control.common.gui.SceneControllerPair;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainWindowController {

    private RemoteControlClientInterface remoteControlClientInterface;
    private Stage remoteControlStage;

    @FXML
    private ListView<AgentInfo> agentsList;

    @FXML
    private Button connect;

    @FXML
    private Button update;

    @FXML
    private ImageView connectionStatus;

    private Image okIcon, errorIcon;

    public void init(RemoteControlClientInterface remoteControlClientInterface) {
        this.remoteControlClientInterface = remoteControlClientInterface;

        remoteControlStage = new Stage();

        okIcon = new Image("/icons/ok.png");
        errorIcon = new Image("/icons/error.png");

        ClientApplication.getExecutorService().scheduleWithFixedDelay(
                new Runnable() {
                    public void run() {
                        Platform.runLater(new Runnable() {
                            public void run() {
                                redrawInterface();
                            }
                        });
                    }
                },
                0,
                100,
                TimeUnit.MILLISECONDS);
    }

    @FXML
    void connectToAgent(ActionEvent event) throws IOException {
        SceneControllerPair<RemoteControlController> pair = FXUtils.loadPair("/fxml/remote_control.fxml");

        remoteControlStage.setScene(pair.getScene());

        pair.getController().init(agentsList.getSelectionModel().getSelectedItem().getAgentId(), remoteControlClientInterface);

        remoteControlStage.show();

    }

    @FXML
    void updateConnectedAgents() throws IOException, InterruptedException {
        final List<AgentInfo> agentInfoList = remoteControlClientInterface.getAvailableAgents();
        if (agentInfoList != null) {
            Platform.runLater(new Runnable() {
                public void run() {
                    AgentInfo selectedAgent = agentsList.getSelectionModel().getSelectedItem();
                    agentsList.getItems().clear();
                    agentsList.getItems().addAll(agentInfoList);
                    if (agentsList.getItems().contains(selectedAgent)) {
                        agentsList.getSelectionModel().select(selectedAgent);
                    }
                    redrawInterface();
                }
            });
        }
    }

    @FXML
    void redrawInterface() {
        if (remoteControlClientInterface.isConnected()) {
            connectionStatus.setImage(okIcon);

            if (agentsList.getSelectionModel().getSelectedItem() != null) {
                connect.setVisible(true);
            } else {
                connect.setVisible(false);
            }

            update.setDisable(false);

        } else {
            connectionStatus.setImage(errorIcon);

            agentsList.getItems().clear();
            connect.setVisible(false);
            update.setDisable(true);
        }
    }
}