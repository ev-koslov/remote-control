package ev.koslov.remote_control.agent.components;

import ev.koslov.data_exchanging.components.Message;
import ev.koslov.data_exchanging.components.RequestBody;
import ev.koslov.data_exchanging.module.AbstractClientRequestProcessor;
import ev.koslov.remote_control.agent.AgentRemoteControlInterface;
import ev.koslov.remote_control.common.bodies.FrameBody;
import ev.koslov.remote_control.common.taglib.RemoteControlTaglib;

import java.awt.*;
import java.io.IOException;

/**
 * Created by voron on 23.02.2017.
 */
public class FrameGrabber extends AbstractClientRequestProcessor<RemoteControlTaglib, AgentRemoteControlInterface> {
    Streamer streamer;

    {
        try {
            streamer = new Streamer();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected boolean accept(Message.Header requestHeader, RequestBody body) {
        return body.getCommand() instanceof RemoteControlTaglib;
    }

    @Override
    protected void process(Message.Header requestHeader, RequestBody<RemoteControlTaglib> body) {
        switch (body.getCommand()){
            case GET_FRAME: {
                try {
                    FrameBody frameBody = new FrameBody(streamer.getScreenW(), streamer.getScreenH(), streamer.nextFrame());
                    getAssociatedClient().response(requestHeader, frameBody);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
