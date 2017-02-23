package ev.koslov.remote_control.common.actions;

/**
 * Created by voron on 15.07.2016.
 */
public class KeyboardKeyPress extends RCAction {
    public int kbKey;
    public boolean isPress;

    public KeyboardKeyPress(int kbKey, boolean isPress) {
        super();
        this.kbKey = kbKey;
        this.isPress = isPress;
    }
}
