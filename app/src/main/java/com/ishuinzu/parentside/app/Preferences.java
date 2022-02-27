package com.ishuinzu.parentside.app;

import android.content.Context;
import android.content.SharedPreferences;

import com.ishuinzu.parentside.object.BatteryObject;
import com.ishuinzu.parentside.object.ChildDevice;
import com.ishuinzu.parentside.object.LatitudeLongitude;
import com.ishuinzu.parentside.object.Parent;

public class Preferences {
    private static Preferences PREFERENCES;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    private static final String PREFERENCES_NAME = "APP_PREFERENCES";
    private static final String INSTRUCTIONS_SEEN = "INSTRUCTIONS_SEEN";
    private static final String LOGGED_IN = "LOGGED_IN";
    private static final String QR_CODE_SCANNED = "QR_CODE_SCANNED";
    private static final String PARENT_EMAIL = "PARENT_EMAIL";
    private static final String PARENT_FCM_TOKEN = "PARENT_FCM_TOKEN";
    private static final String PARENT_HAVE_CHILD = "PARENT_HAVE_CHILD";
    private static final String PARENT_ID = "PARENT_ID";
    private static final String PARENT_IMAGE_URL = "PARENT_IMAGE_URL";
    private static final String PARENT_NAME = "PARENT_NAME";
    private static final String BATTERY_IS_CHARGING = "BATTERY_IS_CHARGING";
    private static final String BATTERY_PERCENTAGE = "BATTERY_PERCENTAGE";
    private static final String BATTERY_SCALE = "BATTERY_SCALE";
    private static final String BATTERY_TECHNOLOGY = "BATTERY_TECHNOLOGY";
    private static final String BATTERY_TEMPERATURE = "BATTERY_TEMPERATURE";
    private static final String BATTERY_VOLTAGE = "BATTERY_VOLTAGE";
    private static final String DEVICE_BRAND = "DEVICE_BRAND";
    private static final String DEVICE_DISPLAY = "DEVICE_DISPLAY";
    private static final String DEVICE_HARDWARE = "DEVICE_HARDWARE";
    private static final String DEVICE_LATITUDE = "DEVICE_LATITUDE";
    private static final String DEVICE_LONGITUDE = "DEVICE_LONGITUDE";
    private static final String DEVICE_MANUFACTURER = "DEVICE_MANUFACTURER";
    private static final String DEVICE_MODEL = "DEVICE_MODEL";
    private static final String DEVICE_SDK_NUMBER = "DEVICE_SDK_NUMBER";

    private Preferences(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized Preferences getInstance(Context context) {
        if (PREFERENCES == null) {
            PREFERENCES = new Preferences(context);
        }
        return PREFERENCES;
    }

    public void setInstructionsSeen(Boolean instructionsSeen) {
        editor = sharedPreferences.edit();
        editor.putBoolean(INSTRUCTIONS_SEEN, instructionsSeen);
        editor.apply();
    }

    public Boolean getInstructionsSeen() {
        return sharedPreferences.getBoolean(INSTRUCTIONS_SEEN, false);
    }

    public void setLoggedIn(Boolean isLoggedIn) {
        editor = sharedPreferences.edit();
        editor.putBoolean(LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    public Boolean getLoggedIn() {
        return sharedPreferences.getBoolean(LOGGED_IN, false);
    }

    public void setQrCodeScanned(Boolean codeScanned) {
        editor = sharedPreferences.edit();
        editor.putBoolean(QR_CODE_SCANNED, codeScanned);
        editor.apply();
    }

    public Boolean getQrCodeScanned() {
        return sharedPreferences.getBoolean(QR_CODE_SCANNED, false);
    }

    public void setParent(Parent parent) {
        editor = sharedPreferences.edit();

        editor.putString(PARENT_EMAIL, parent.getEmail());
        editor.putString(PARENT_FCM_TOKEN, parent.getFcm_token());
        editor.putString(PARENT_HAVE_CHILD, parent.getHave_child());
        editor.putString(PARENT_ID, parent.getId());
        editor.putString(PARENT_IMAGE_URL, parent.getImg_url());
        editor.putString(PARENT_NAME, parent.getName());
        editor.apply();
    }

    public Parent getParent() {
        return new Parent(
                sharedPreferences.getString(PARENT_EMAIL, null),
                sharedPreferences.getString(PARENT_FCM_TOKEN, null),
                sharedPreferences.getString(PARENT_HAVE_CHILD, null),
                sharedPreferences.getString(PARENT_ID, null),
                sharedPreferences.getString(PARENT_IMAGE_URL, null),
                sharedPreferences.getString(PARENT_NAME, null)
        );
    }

    public void setChildDevice(ChildDevice childDevice) {
        editor = sharedPreferences.edit();

        editor.putString(BATTERY_IS_CHARGING, childDevice.getBattery().getIs_charging());
        editor.putString(BATTERY_PERCENTAGE, childDevice.getBattery().getPercentage());
        editor.putString(BATTERY_SCALE, childDevice.getBattery().getScale());
        editor.putString(BATTERY_TECHNOLOGY, childDevice.getBattery().getTechnology());
        editor.putString(BATTERY_TEMPERATURE, childDevice.getBattery().getTemperature());
        editor.putString(BATTERY_VOLTAGE, childDevice.getBattery().getVoltage());
        editor.putString(DEVICE_BRAND, childDevice.getBrand());
        editor.putString(DEVICE_DISPLAY, childDevice.getDisplay());
        editor.putString(DEVICE_HARDWARE, childDevice.getHardware());
        editor.putFloat(DEVICE_LATITUDE, Float.parseFloat("" + childDevice.getLocation().getLatitude()));
        editor.putFloat(DEVICE_LONGITUDE, Float.parseFloat("" + childDevice.getLocation().getLongitude()));
        editor.putString(DEVICE_MANUFACTURER, childDevice.getManufacturer());
        editor.putString(DEVICE_MODEL, childDevice.getModel());
        editor.putString(DEVICE_SDK_NUMBER, childDevice.getSdk_number());
        editor.apply();
    }

    public ChildDevice getChildDevice() {
        return new ChildDevice(
                new BatteryObject(sharedPreferences.getString(BATTERY_IS_CHARGING, null), sharedPreferences.getString(BATTERY_PERCENTAGE, null), sharedPreferences.getString(BATTERY_SCALE, null), sharedPreferences.getString(BATTERY_TECHNOLOGY, null), sharedPreferences.getString(BATTERY_TEMPERATURE, null), sharedPreferences.getString(BATTERY_VOLTAGE, null)),
                sharedPreferences.getString(DEVICE_BRAND, null),
                sharedPreferences.getString(DEVICE_DISPLAY, null),
                sharedPreferences.getString(DEVICE_HARDWARE, null),
                new LatitudeLongitude(true, Double.parseDouble("" + sharedPreferences.getFloat(DEVICE_LATITUDE, 0f)), Double.parseDouble("" + sharedPreferences.getFloat(DEVICE_LONGITUDE, 0f))),
                sharedPreferences.getString(DEVICE_MANUFACTURER, null),
                sharedPreferences.getString(DEVICE_MODEL, null),
                sharedPreferences.getString(PARENT_ID, null),
                sharedPreferences.getString(DEVICE_SDK_NUMBER, null)
        );
    }

    public void updateLocation(LatitudeLongitude location) {
        editor = sharedPreferences.edit();

        editor.putFloat(DEVICE_LATITUDE, Float.parseFloat("" + location.getLatitude()));
        editor.putFloat(DEVICE_LONGITUDE, Float.parseFloat("" + location.getLongitude()));
        editor.apply();
    }

    public void updateFCMToken(String fcmToken) {
        editor = sharedPreferences.edit();

        editor.putString(PARENT_FCM_TOKEN, fcmToken);
        editor.apply();
    }

    public void updateBattery(BatteryObject batteryObject) {
        editor = sharedPreferences.edit();

        editor.putString(BATTERY_IS_CHARGING, batteryObject.getIs_charging());
        editor.putString(BATTERY_PERCENTAGE, batteryObject.getPercentage());
        editor.putString(BATTERY_SCALE, batteryObject.getScale());
        editor.putString(BATTERY_TECHNOLOGY, batteryObject.getTechnology());
        editor.putString(BATTERY_TEMPERATURE, batteryObject.getTemperature());
        editor.putString(BATTERY_VOLTAGE, batteryObject.getVoltage());
        editor.apply();
    }

    public BatteryObject getBattery() {
        return new BatteryObject(sharedPreferences.getString(BATTERY_IS_CHARGING, null), sharedPreferences.getString(BATTERY_PERCENTAGE, null), sharedPreferences.getString(BATTERY_SCALE, null), sharedPreferences.getString(BATTERY_TECHNOLOGY, null), sharedPreferences.getString(BATTERY_TEMPERATURE, null), sharedPreferences.getString(BATTERY_VOLTAGE, null));
    }

    public String getToken() {
        return  sharedPreferences.getString(PARENT_FCM_TOKEN, null);
    }
}