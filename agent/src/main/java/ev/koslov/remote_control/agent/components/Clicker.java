package ev.koslov.remote_control.agent.components;


import ev.koslov.remote_control.common.actions.*;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.*;
import java.util.List;


/**
 * Created by voron on 29.06.2016.
 */
public class Clicker {
    private final static HashSet<Integer> pressedButtons;

    static {
        pressedButtons = new HashSet<Integer>();
    }

    private Robot robot;

    {
        try {
            robot = new Robot();
            robot.setAutoDelay(5);
            robot.setAutoWaitForIdle(true);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public synchronized void doActions(List<RCAction> actions) {
        for (RCAction action : actions) {
            if (action.getClass().equals(MouseMove.class)) {
                makeMouseMove((MouseMove) action);
                continue;
            }
            if (action.getClass().equals(MousePress.class)) {
                makeMouseClick((MousePress) action);
                continue;
            }
            if (action.getClass().equals(MouseScroll.class)) {
                makeMouseScroll((MouseScroll) action);
                continue;
            }
            if (action.getClass().equals(KeyboardKeyPress.class)) {
                makeKeyPress((KeyboardKeyPress) action);
                continue;
            }
        }
    }

    //move mouse to position
    private void makeMouseMove(MouseMove mouseMove) {
        int x = mouseMove.x;
        int y = mouseMove.y;
        robot.mouseMove(x, y);
    }

    private void makeMouseClick(MousePress mousePress) {
        makeMouseMove(mousePress);

        //getMouseButton from event
        int buttonMask = getMouseButtonCode(mousePress);

        //make click
        if (mousePress.isPress) {
            robot.mousePress(buttonMask);
        } else {
            robot.mouseRelease(buttonMask);
        }
    }


    private void makeMouseScroll(MouseScroll mouseScroll) {
        makeMouseMove(mouseScroll);

        if (mouseScroll.pos > 0) {
            robot.mouseWheel(-1);
        } else {
            robot.mouseWheel(1);
        }
    }

    private void makeKeyPress(KeyboardKeyPress keyPress) {
        if (keyPress.isPress){
            robot.keyPress(keyPress.kbKey);
        } else {
            robot.keyRelease(keyPress.kbKey);
        }
    }

    private int getMouseButtonCode(MousePress mousePress) {
        int button = -1;
        switch (mousePress.button) {
            case 0: {
                button = InputEvent.BUTTON1_DOWN_MASK;
                break;
            }
            case 1: {
                button = InputEvent.BUTTON2_DOWN_MASK;
                break;
            }
            case 2: {
                button = InputEvent.BUTTON3_DOWN_MASK;
                break;
            }
        }
        return button;
    }
}
