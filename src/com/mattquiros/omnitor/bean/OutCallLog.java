package com.mattquiros.omnitor.bean;

import com.mattquiros.omnitor.util.This;

public class OutCallLog extends JsonLog {
    
    private String type;
    private long time_started;
    private long time_ended;
    private boolean roaming;
    private String number;
    private String sim_number;
    
    public OutCallLog(long timeStarted, long timeEnded, boolean isRoaming,
            String number, String simNumber) {
        super();
        this.type = This.TYPE_OUT_CALL;
        this.time_started = timeStarted;
        this.time_ended = timeEnded;
        this.roaming = isRoaming;
        this.number = number;
        this.sim_number = simNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTime_started() {
        return time_started;
    }

    public void setTime_started(long time_started) {
        this.time_started = time_started;
    }

    public long getTime_ended() {
        return time_ended;
    }

    public void setTime_ended(long time_ended) {
        this.time_ended = time_ended;
    }

    public boolean isRoaming() {
        return roaming;
    }

    public void setRoaming(boolean roaming) {
        this.roaming = roaming;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSim_number() {
        return sim_number;
    }

    public void setSim_number(String sim_number) {
        this.sim_number = sim_number;
    }
    
}
