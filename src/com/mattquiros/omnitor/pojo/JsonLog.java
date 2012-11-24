package com.mattquiros.omnitor.pojo;

abstract class JsonLog {
    
    private String uuid;
    
    public JsonLog() {}
    
    public JsonLog(String uuid) {
        this.uuid = uuid;
    }
    
    public String getUuid() {
        return uuid;
    }
    
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    
}
