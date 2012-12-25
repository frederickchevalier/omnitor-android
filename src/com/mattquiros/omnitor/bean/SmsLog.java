package com.mattquiros.omnitor.bean;

public class SmsLog extends JsonLog {
    
    private String type;
    private String number;
    private String sim_number;
    private long time;
    private int length;
    private boolean roaming;
    
    public SmsLog(String uuid, String type, String number, String simNumber,
            long time, int length, boolean isRoaming) {
        super(uuid);
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + length;
        result = prime * result + ((number == null) ? 0 : number.hashCode());
        result = prime * result + (roaming ? 1231 : 1237);
        result = prime * result
                + ((sim_number == null) ? 0 : sim_number.hashCode());
        result = prime * result + (int) (time ^ (time >>> 32));
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        SmsLog other = (SmsLog) obj;
        if (length != other.length)
            return false;
        if (number == null) {
            if (other.number != null)
                return false;
        } else if (!number.equals(other.number))
            return false;
        if (roaming != other.roaming)
            return false;
        if (sim_number == null) {
            if (other.sim_number != null)
                return false;
        } else if (!sim_number.equals(other.sim_number))
            return false;
        if (time != other.time)
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
    
}
