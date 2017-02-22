package ev.koslov.remote_control.client;

import ev.koslov.remote_control.client.gui.MainWindowController;
import ev.koslov.remote_control.common.gui.FXUtils;
import ev.koslov.remote_control.common.gui.SceneControllerPair;
import javafx.application.Application;
import javafx.stage.Stage;


public class ClientLauncher extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        SceneControllerPair<MainWindowController> pair = FXUtils.loadPair("/fxml/main_window.fxml");

        stage.setScene(pair.getScene());
        stage.show();
    }

    public static void main(String[] args){
        ClientLauncher.launch(args);
    }
}
