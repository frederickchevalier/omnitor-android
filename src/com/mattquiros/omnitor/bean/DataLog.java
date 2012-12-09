package com.mattquiros.omnitor.bean;

import com.mattquiros.omnitor.util.This;

public class DataLog extends JsonLog {
    
    private String type;
    private long time;
    private long mobile_sent;
    private long mobile_received;
    private long network_sent;
    private long network_received;
    private boolean roaming;
    
    public DataLog(long time, long mobileSent, long mobileReceived,
            long networkSent, long networkReceived, boolean isRoaming) {
        super();
        this.type = This.TYPE_DATA;
        this.time = time;
        this.mobile_sent = mobileSent;
        this.mobile_received = mobileReceived;
        this.network_sent = networkSent;
        this.network_received = networkReceived;
        this.roaming = isRoaming;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getMobile_sent() {
        return mobile_sent;
    }

    public void setMobile_sent(long mobile_sent) {
        this.mobile_sent = mobile_sent;
    }

    public long getMobile_received() {
        return mobile_received;
    }

    public void setMobile_received(long mobile_received) {
        this.mobile_received = mobile_received;
    }

    public long getNetwork_sent() {
        return network_sent;
    }

    public void setNetwork_sent(long network_sent) {
        this.network_sent = network_sent;
    }

    public long getNetwork_received() {
        return network_received;
    }

    public void setNetwork_received(long network_received) {
        this.network_received = network_received;
    }

    public boolean isRoaming() {
        return roaming;
    }

    public void setRoaming(boolean roaming) {
        this.roaming = roaming;
    }
    
}
