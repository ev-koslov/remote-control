package ev.koslov.remote_control.common.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public class FXUtils {
    public static SceneControllerPair loadPair(String pathToFXML) throws IOException {
        FXMLLoader loader = new FXMLLoader(FXUtils.class.getResource(pathToFXML));
        Parent parent = (Parent) loader.load();
        Object controller = loader.getController();

        return new SceneControllerPair(parent, controller);
    }
}
