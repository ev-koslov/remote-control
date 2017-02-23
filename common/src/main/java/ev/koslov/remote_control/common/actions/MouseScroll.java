package ev.koslov.remote_control.common.actions;

/**
 * Created by voron on 15.07.2016.
 */
public class MouseScroll extends MouseMove {
    public int pos;

    public MouseScroll(int mouseX, int mouseY, int pos) {
        super(mouseX, mouseY);
        this.pos = pos;
    }
}
