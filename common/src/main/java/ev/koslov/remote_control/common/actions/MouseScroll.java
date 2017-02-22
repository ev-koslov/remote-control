package ev.koslov.remote_control.common.actions;

/**
 * Created by voron on 15.07.2016.
 */
public class MouseScroll extends MouseMove {
    public boolean isScrollUP;

    public MouseScroll(int mouseX, int mouseY, boolean isScrollUP, boolean[] modifiers) {
        super(mouseX, mouseY, modifiers);
        this.isScrollUP = isScrollUP;
    }
}
