package ev.koslov.remote_control.client.gui;

import ev.koslov.remote_control.client.RemoteControlClientInterface;
import ev.koslov.remote_control.common.dto.AgentInfo;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {

    private RemoteControlClientInterface remoteControlClientInterface;
    private Stage remoteControlStage;

    {
        remoteControlClientInterface = new RemoteControlClientInterface();
        try {
            remoteControlClientInterface.connect("localhost", 5555);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private ListView<AgentInfo> agentsList;

    @FXML
    private Button connect;

    @FXML
    private Button update;

    @FXML
    void connectToAgent(ActionEvent event) {

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
        if (agentsList.getSelectionModel().getSelectedItem() != null) {
            connect.setVisible(true);
        } else {
            connect.setVisible(false);
        }
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        remoteControlStage = new Stage();
        remoteControlStage.initModality(Modality.WINDOW_MODAL);
    }
}