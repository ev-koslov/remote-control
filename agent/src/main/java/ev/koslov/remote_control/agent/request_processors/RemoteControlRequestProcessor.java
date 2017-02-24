package ev.koslov.remote_control.agent.request_processors;

import ev.koslov.data_exchanging.components.Message;
import ev.koslov.data_exchanging.components.RequestBody;
import ev.koslov.data_exchanging.components.ResponseBody;
import ev.koslov.data_exchanging.components.tags.StatusTag;
import ev.koslov.data_exchanging.module.AbstractClientRequestProcessor;
import ev.koslov.remote_control.agent.AgentRemoteControlInterface;
import ev.koslov.remote_control.agent.components.Clicker;
import ev.koslov.remote_control.agent.components.Streamer;
import ev.koslov.remote_control.common.actions.RCAction;
import ev.koslov.remote_control.common.bodies.FrameBody;
import ev.koslov.remote_control.common.bodies.RCActionBody;
import ev.koslov.remote_control.common.taglib.RemoteControlTaglib;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class RemoteControlRequestProcessor extends AbstractClientRequestProcessor<RemoteControlTaglib, AgentRemoteControlInterface> {
    private Map<Long, Streamer> streamers;
    private Clicker clicker;

    public RemoteControlRequestProcessor() {
        streamers = new HashMap<Long, Streamer>();
        clicker = new Clicker();
    }

    @Override
    protected boolean accept(Message.Header requestHeader, RequestBody body) {
        return body.getCommand() instanceof RemoteControlTaglib;
    }

    @Override
    protected synchronized void process(Message.Header requestHeader, RequestBody<RemoteControlTaglib> body) {
        switch (body.getCommand()) {

            case START_RC: {
                try {
                    streamers.put((Long) body.getProperty("clientId"), new Streamer());
                    getAssociatedClient().response(requestHeader, StatusTag.OK);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }

            case STOP_RC: {
                try {
                    Long clientId = body.getProperty("clientId");
                    streamers.remove(clientId);
                    clicker.stop();
                    getAssociatedClient().response(requestHeader, StatusTag.OK);

                    System.out.println("Client disconnected: "+body.getProperty("clientId"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }

            case GET_FRAME: {
                try {
                    Streamer streamer = streamers.get(requestHeader.getSourceId());

                    FrameBody frameBody = new FrameBody(
                            streamer.getScreenW(),
                            streamer.getScreenH(),
                            streamer.getPointerX(),
                            streamer.getPointerY(),
                            streamer.nextFrame()
                    );

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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
