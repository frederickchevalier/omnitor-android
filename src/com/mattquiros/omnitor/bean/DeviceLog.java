package com.mattquiros.omnitor.bean;

public class DeviceLog extends JsonLog {
    
    private String type;
    private String manufacturer;
    private String model;
    private long installTime;
    
    public DeviceLog() {}
    
    public DeviceLog(String uuid, String manufacturer, String model, long installTime) {
        super(uuid);
        this.type = "device";
        this.manufacturer = manufacturer;
        this.model = model;
        this.installTime = installTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public long getInstallTime() {
        return installTime;
    }

    public void setInstallTime(long installTime) {
        this.installTime = installTime;
    }
    
}
