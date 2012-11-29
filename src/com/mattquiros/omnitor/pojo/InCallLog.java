package com.mattquiros.omnitor.pojo;

import com.mattquiros.omnitor.util.This;

public class InCallLog extends OutCallLog {
    
    private long time_answered;
    
    public InCallLog(long timeStarted, long timeAnswered, long timeEnded,
            boolean isRoaming, String number, String simNumber) {
        super(timeStarted, timeEnded, isRoaming, number, simNumber);
        this.setType(This.TYPE_IN_CALL);
        this.time_answered = timeAnswered;
    }

    public long getTime_answered() {
        return time_answered;
    }

    public void setTime_answered(long time_answered) {
        this.time_answered = time_answered;
    }
    
}
