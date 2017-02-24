package ev.koslov.remote_control.agent.components;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;


public class Streamer {
    private static final int TRANSPARENT_MASK = 0;
    private static Robot robot;
    private static Toolkit toolkit;

    static {
        try {
            robot = new Robot();
            toolkit = Toolkit.getDefaultToolkit();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private BufferedImage previousImage, actualImage, deltaImage;

    private Rectangle screenSize;

    public Streamer() {
        screenSize = new Rectangle(toolkit.getScreenSize());
    }

    public int getPointerX(){
        return MouseInfo.getPointerInfo().getLocation().x;
    }

    public int getPointerY(){
        return MouseInfo.getPointerInfo().getLocation().y;
    }

    public int getScreenW() {
        return toolkit.getScreenSize().width;
    }

    public int getScreenH() {
        return toolkit.getScreenSize().height;
    }

    public int[] nextFrame() {

        //creating actual screenshot and getting its buffer content
        actualImage = robot.createScreenCapture(screenSize);
        int[] actualImageData = ((DataBufferInt) actualImage.getRaster().getDataBuffer()).getData();

        //if we have no previous image, or it has different size,
        //returning actual image buffer without any processing
        //and setting previous image value to actual
        if (previousImage == null ||
                previousImage.getWidth() != screenSize.width ||
                previousImage.getHeight() != screenSize.height) {

            previousImage = actualImage;
            return actualImageData;
        }

        //if delta image has different size of == null,  instantiating it.
        if (deltaImage == null || deltaImage.getWidth() != screenSize.width || deltaImage.getHeight() != screenSize.height) {
            deltaImage = new BufferedImage(screenSize.width, screenSize.height, BufferedImage.TYPE_INT_ARGB);
        }

        //getting prevoius and delta image buffers
        int[] previousImageData = ((DataBufferInt) previousImage.getRaster().getDataBuffer()).getData();
        int[] deltaImageData = ((DataBufferInt) deltaImage.getRaster().getDataBuffer()).getData();

        //iterating actual image buffer and if we have duplicate value in previous and actual buffer, setting
        //current cell value in delta buffer = TRANSPARENT_MASK;
        for (int i = 0; i < actualImageData.length; i++) {
            if (actualImageData[i] != previousImageData[i]) {
                deltaImageData[i] = actualImageData[i];
            } else {
                deltaImageData[i] = TRANSPARENT_MASK;
            }
        }

        previousImage = actualImage;

        return deltaImageData;
    }
}
