package ev.koslov.remote_control.agent.components;


import ev.koslov.remote_control.common.actions.*;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.*;


public class Clicker {
    private final static Set<Integer> pressedButtons, pressedMouseButtons;

    static {
        pressedButtons = Collections.synchronizedSet(new HashSet<Integer>());
        pressedMouseButtons = Collections.synchronizedSet(new HashSet<Integer>());
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

    public void doAction(RCAction action) {
        if (action.getClass().equals(MouseMove.class) || action.getClass().equals(MouseDrag.class)) {
            makeMouseMove((MouseMove) action);
        }
        if (action instanceof MousePress) {
            makeMouseClick((MousePress) action);
        }
        if (action instanceof MouseScroll) {
            makeMouseScroll((MouseScroll) action);
        }
        if (action instanceof KeyboardKeyPress) {
            makeKeyPress((KeyboardKeyPress) action);
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
            pressedMouseButtons.add(buttonMask);
            robot.mousePress(buttonMask);
        } else {
            pressedMouseButtons.remove(buttonMask);
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
        if (keyPress.isPress) {
            pressedButtons.add(keyPress.kbKey);
            robot.keyPress(keyPress.kbKey);
        } else {
            pressedButtons.remove(keyPress.kbKey);
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

    public void stop(){
        for (Integer i : pressedButtons){
            try {
                robot.keyRelease(i);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        for (Integer i : pressedMouseButtons){
            try {
                robot.mouseRelease(i);
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        pressedButtons.clear();
        pressedMouseButtons.clear();
    }
}
