package vp.usw.finalproject;

import java.io.Serializable;

public class Schedule implements Serializable {
    private static final long serialVersionUID = 1L;

    private String title;
    private String startTime;
    private String endTime;
    private String location;
    private String memo;

    Schedule() {

    }

    Schedule(String title, String sTime, String eTime, String location, String memo) {
        this.title = title;
        this.startTime = sTime;
        this.endTime = eTime;
        this.location = location;
        this.memo = memo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
