package ev.koslov.remote_control.common.taglib;

public enum ServerTaglib implements ITaglib{
    INITIALIZE_AGENT,
    GET_AGENT_CONNECTIONS;

    private String[] propertyNames;

    ServerTaglib(String... propertyNames) {
        this.propertyNames = propertyNames;
    }

    public String[] getPropertyNames(){
        return propertyNames;
    }
}
