package com.ishuinzu.parentside.object;

public class AppUsageObject {
    private Boolean can_open;
    private int count;
    private long event_time;
    private int event_type;
    private Boolean is_system;
    private long mobile;
    private String mobile_data;
    private String name;
    private String package_name;
    private long usage_time;
    private String wifi_data;

    public AppUsageObject() {
    }

    public AppUsageObject(Boolean can_open, int count, long event_time, int event_type, Boolean is_system, long mobile, String mobile_data, String name, String package_name, long usage_time, String wifi_data) {
        this.can_open = can_open;
        this.count = count;
        this.event_time = event_time;
        this.event_type = event_type;
        this.is_system = is_system;
        this.mobile = mobile;
        this.mobile_data = mobile_data;
        this.name = name;
        this.package_name = package_name;
        this.usage_time = usage_time;
        this.wifi_data = wifi_data;
    }

    public Boolean getCan_open() {
        return can_open;
    }

    public void setCan_open(Boolean can_open) {
        this.can_open = can_open;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getEvent_time() {
        return event_time;
    }

    public void setEvent_time(long event_time) {
        this.event_time = event_time;
    }

    public int getEvent_type() {
        return event_type;
    }

    public void setEvent_type(int event_type) {
        this.event_type = event_type;
    }

    public Boolean getIs_system() {
        return is_system;
    }

    public void setIs_system(Boolean is_system) {
        this.is_system = is_system;
    }

    public long getMobile() {
        return mobile;
    }

    public void setMobile(long mobile) {
        this.mobile = mobile;
    }

    public String getMobile_data() {
        return mobile_data;
    }

    public void setMobile_data(String mobile_data) {
        this.mobile_data = mobile_data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public long getUsage_time() {
        return usage_time;
    }

    public void setUsage_time(long usage_time) {
        this.usage_time = usage_time;
    }

    public String getWifi_data() {
        return wifi_data;
    }

    public void setWifi_data(String wifi_data) {
        this.wifi_data = wifi_data;
    }
}