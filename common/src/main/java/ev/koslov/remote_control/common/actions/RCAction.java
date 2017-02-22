package ev.koslov.remote_control.common.actions;

import java.io.Serializable;

public class RCAction implements Serializable {
    public boolean[] modifiers;

    protected RCAction(boolean[] modifiers) {
        this.modifiers = modifiers;
    }
}
