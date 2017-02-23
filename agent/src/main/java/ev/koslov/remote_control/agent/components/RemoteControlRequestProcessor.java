package ev.koslov.remote_control.agent.components;

import ev.koslov.data_exchanging.components.Message;
import ev.koslov.data_exchanging.components.RequestBody;
import ev.koslov.data_exchanging.module.AbstractClientRequestProcessor;
import ev.koslov.remote_control.agent.AgentRemoteControlInterface;
import ev.koslov.remote_control.common.actions.RCAction;
import ev.koslov.remote_control.common.bodies.FrameBody;
import ev.koslov.remote_control.common.bodies.RCActionBody;
import ev.koslov.remote_control.common.taglib.RemoteControlTaglib;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Created by voron on 23.02.2017.
 */
public class RemoteControlRequestProcessor extends AbstractClientRequestProcessor<RemoteControlTaglib, AgentRemoteControlInterface> {
    private Streamer streamer;
    private Clicker clicker;

    {
        try {
            streamer = new Streamer();
            clicker = new Clicker();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected boolean accept(Message.Header requestHeader, RequestBody body) {
        return body.getCommand() instanceof RemoteControlTaglib;
    }

    @Override
    protected synchronized void process(Message.Header requestHeader, RequestBody<RemoteControlTaglib> body) {
        switch (body.getCommand()){

            case START_RC: {
                
                break;
            }

            case STOP_RC: {
                clicker.stop();
                break;
            }

            case GET_FRAME: {
                try {
                    FrameBody frameBody = new FrameBody(streamer.getScreenW(), streamer.getScreenH(), streamer.nextFrame());
                    getAssociatedClient().response(requestHeader, frameBody);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            case DO_INPUT: {
                RCActionBody actionBody = (RCActionBody) body;
                List<RCAction> actions = actionBody.getActions();

                for (RCAction action : actions) {
                    try {
                        clicker.doAction(action);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
