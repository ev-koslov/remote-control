package ev.koslov.remote_control.common.actions;

/**
 * Created by voron on 15.07.2016.
 */
public class MousePress extends MouseMove {
    public byte button;

    public boolean isPress;

    public MousePress(int mouseX, int mouseY, byte mouseButton, boolean isPress) {
        super(mouseX, mouseY);
        this.button = mouseButton;
        this.isPress = isPress;
    }
}
