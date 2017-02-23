package ev.koslov.remote_control.common.actions;

/**
 * Created by voron on 15.07.2016.
 */
public class MouseMove extends RCAction {
    public int x, y;

    public MouseMove(int mouseX, int mouseY) {
        super();
        x = mouseX;
        y = mouseY;
    }
}
