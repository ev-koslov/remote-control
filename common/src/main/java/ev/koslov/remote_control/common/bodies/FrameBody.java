package ev.koslov.remote_control.common.bodies;

import ev.koslov.data_exchanging.components.ResponseBody;
import ev.koslov.data_exchanging.components.SerializationUtils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.*;
import java.util.Arrays;


public class FrameBody extends ResponseBody {
    private int frameW, frameH, pointerX, pointerY;
    private byte[] data;

    public FrameBody(int frameW, int frameH, int pointerX, int pointerY, int[] frameBuffer) throws IOException {
        this.frameW = frameW;
        this.frameH = frameH;
        this.pointerX = pointerX;
        this.pointerY = pointerY;
        this.data = SerializationUtils.compress(SerializationUtils.serialize(frameBuffer));
    }

    public int getFrameW() {
        return frameW;
    }

    public int getFrameH() {
        return frameH;
    }

    public int getPointerX() {
        return pointerX;
    }

    public int getPointerY() {
        return pointerY;
    }

    public int[] getFrameBuffer() throws IOException {
        return SerializationUtils.deserialize(SerializationUtils.decompress(data));
    }

    public BufferedImage getImage(int imageType) throws IOException {
        BufferedImage result = new BufferedImage(frameW, frameH, imageType);
        int[] sourceBuffer = getFrameBuffer();
        int[] resultImageBuffer = ((DataBufferInt) result.getRaster().getDataBuffer()).getData();

        System.arraycopy(sourceBuffer, 0, resultImageBuffer, 0, sourceBuffer.length);

        return result;
    }

    public int compressedDataSize() {
        return data.length;
    }
}
