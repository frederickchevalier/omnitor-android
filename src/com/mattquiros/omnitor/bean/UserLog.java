package com.mattquiros.omnitor.bean;

public class UserLog extends JsonLog {
    
    private String type;
    private String email;
    
    public UserLog() {}
    
    public UserLog(String uuid, String email) {
        super(uuid);
        this.type = "user";
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
}
