package ev.koslov.remote_control.common.gui;

import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * Created by voron on 22.02.2017.
 */
public class SceneControllerPair<T extends Object> {
    private Scene scene;
    private T controller;

    protected SceneControllerPair(Parent root, T controller) {
        this.scene = new Scene(root);
        this.controller = controller;
    }

    public Scene getScene() {
        return scene;
    }

    public T getController() {
        return controller;
    }
}
