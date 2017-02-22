package ev.koslov.remote_control.common.taglib;

/**
 * Created by voron on 13.02.2017.
 */
public enum  RemoteControlTaglib implements ITaglib{
    GET_FRAME,
    DO_INPUT;

    private String[] propertyNames;

    RemoteControlTaglib(String... propertyNames) {
        this.propertyNames = propertyNames;
    }

    public String[] getPropertyNames() {
        return propertyNames;
    }
}
