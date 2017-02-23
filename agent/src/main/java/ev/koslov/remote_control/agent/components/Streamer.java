package ev.koslov.remote_control.agent.components;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;


public class Streamer {
    private static final int TRANSPARENT_MASK = (0 << 24) | (0 << 16) | (0 << 8) | 0;

    private BufferedImage previousImage, actualImage, deltaImage;
    private Robot robot;
    private Toolkit toolkit;
    private Rectangle screenSize;

    public Streamer() throws AWTException {
        robot = new Robot();
        toolkit = Toolkit.getDefaultToolkit();
        screenSize = new Rectangle(toolkit.getScreenSize());
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


//    private void addStatData(RemoteStreamingPacket streamingPacket) {
//        traffic += streamingPacket.getCompressedData().length;
//        running = System.currentTimeMillis() - startTime;
//        frames++;
//    }
//
//    private String getStatString() {
//        StringBuilder sb = new StringBuilder();
//        sb.append("Total frame sent: ");
//        sb.append(frames);
//        sb.append(". Traffic = ");
//        sb.append(traffic / 1024);
//        sb.append(" kB. Running: ");
//        sb.append(running / 1000);
//        sb.append(" s.");
//        return sb.toString();
//    }
}
