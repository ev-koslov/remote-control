package ev.koslov.remote_control.common.bodies;

import ev.koslov.data_exchanging.components.ResponseBody;
import ev.koslov.data_exchanging.components.SerializationUtils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.*;


public class FrameBody extends ResponseBody {
    private int frameW, frameH;
    private byte[] data;

    public FrameBody(int frameW, int frameH, int[] frameBuffer) throws IOException {
        this.frameW = frameW;
        this.frameH = frameH;
        this.data = SerializationUtils.compress(SerializationUtils.serialize(frameBuffer));
    }

    public int getFrameW() {
        return frameW;
    }

    public int getFrameH() {
        return frameH;
    }

    public int[] getFrameBuffer() throws IOException {
        return SerializationUtils.deserialize(SerializationUtils.decompress(data));
    }

    public BufferedImage getImage(int imageType) throws IOException {
        BufferedImage result = new BufferedImage(frameW, frameH, imageType);
        int[] sourceBuffer = getFrameBuffer();
        int[] resultImageBuffer = ((DataBufferInt) result.getRaster().getDataBuffer()).getData();
        for (int i=0; i<resultImageBuffer.length; i++){
            resultImageBuffer[i] = sourceBuffer[i];
        }
        return result;
    }

    public int compressedDataSize(){
        return data.length;
    }
}
