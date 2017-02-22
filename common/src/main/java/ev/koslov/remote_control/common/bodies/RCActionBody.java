package ev.koslov.remote_control.common.bodies;

import ev.koslov.data_exchanging.components.RequestBody;
import ev.koslov.remote_control.common.taglib.RemoteControlTaglib;
import ev.koslov.remote_control.common.actions.RCAction;

import java.util.List;

public class RCActionBody extends RequestBody<RemoteControlTaglib> {

    private List<RCAction> actions;

    public RCActionBody(RemoteControlTaglib requestCommand, List<RCAction> actions) {
        super(requestCommand);
        this.actions = actions;
    }

    public List<RCAction> getActions() {
        return actions;
    }
}
