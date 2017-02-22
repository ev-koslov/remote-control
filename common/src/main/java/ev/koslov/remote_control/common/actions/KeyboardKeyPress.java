package ev.koslov.remote_control.common.actions;

/**
 * Created by voron on 15.07.2016.
 */
public class KeyboardKeyPress extends RCAction {
    public int kbKey;

    public KeyboardKeyPress(int kbKey, boolean[] modifiers) {
        super(modifiers);
        this.kbKey = kbKey;
    }
}
