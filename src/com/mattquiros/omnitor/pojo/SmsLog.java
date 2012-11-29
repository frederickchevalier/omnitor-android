package com.mattquiros.omnitor.pojo;

public class SmsLog extends JsonLog {
    
    private String type;
    private String number;
    private String sim_number;
    private long time;
    private int length;
    private boolean roaming;
    
    public SmsLog(String type, String number, String simNumber, long time,
            int length, boolean isRoaming) {
        super();
        this.type = type;
        this.number = number;
        this.sim_number = simNumber;
        this.time = time;
        this.length = length;
        this.roaming = isRoaming;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean isRoaming() {
        return roaming;
    }

    public void setRoaming(boolean roaming) {
        this.roaming = roaming;
    }
    
}
