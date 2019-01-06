package com.nurbol.android.tempmonitor;

public class Temperature {
    private String mTemperature;
    private String mHumidity;
    private String mRoom;
    private String mDate;
    private String mLight;
    private int mId;

    public Temperature(String temperature, String humidity, String room, String date, String light, int id) {
        mTemperature = temperature;
        mHumidity = humidity;
        mRoom = room;
        mDate = date;
        mLight = light;
        mId = id;
    }
    public Temperature(String temperature, String humidity, String date, String light) {
        mTemperature = temperature;
        mHumidity = humidity;
        mDate = date;
        mLight = light;
    }

    public String getTemperature() {
        return mTemperature;
    }

    public String getRoom() {
        return mRoom;
    }

    public String getDate() {
        return mDate;
    }

    public String getHumidity() { return mHumidity; }

    public String getLight() { return mLight; }

    public int getId() { return mId; }
}
