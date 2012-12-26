package com.mattquiros.omnitor.bean;

public class DeviceLog extends JsonLog {
    
    private String type;
    private String manufacturer;
    private String model;
    private long first_run;
    
    public DeviceLog() {}
    
    public DeviceLog(String uuid, String manufacturer, String model, long firstRun) {
        super(uuid);
        this.type = "device";
        this.manufacturer = manufacturer;
        this.model = model;
        this.first_run = firstRun;
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

    public long getFirst_run() {
        return first_run;
    }

    public void setFirst_run(long first_run) {
        this.first_run = first_run;
    }
    
}
