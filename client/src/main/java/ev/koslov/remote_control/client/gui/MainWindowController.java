package ev.koslov.remote_control.client.gui;

import ev.koslov.remote_control.common.dto.AgentInfo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

public class MainWindowController {

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
    void updateConnectedAgents(ActionEvent event) {

    }

}