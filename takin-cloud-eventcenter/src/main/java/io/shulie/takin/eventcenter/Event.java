package io.shulie.takin.eventcenter;


import java.io.Serializable;

/**
 * 事件
 */
public class Event implements Serializable {

    private String eventName;

    private Object ext;

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Object getExt() {
        return ext;
    }

    public void setExt(Object ext) {
        this.ext = ext;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventName='" + eventName + '\'' +
                ", ext=" + ext +
                '}';
    }
}
