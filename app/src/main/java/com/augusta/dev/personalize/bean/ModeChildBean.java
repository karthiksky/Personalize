package com.augusta.dev.personalize.bean;

/**
 * Created by Shanmugavel on 10/26/2016.
 */

public class ModeChildBean {
    int callValue;
    int musicValue;
    int alarmValue;

    public ModeChildBean(int callValue, int musicValue, int alarmValue) {
        this.callValue = callValue;
        this.musicValue = musicValue;
        this.alarmValue = alarmValue;
    }

    public int getCallValue() {
        return callValue;
    }

    public void setCallValue(int callValue) {
        this.callValue = callValue;
    }

    public int getMusicValue() {
        return musicValue;
    }

    public void setMusicValue(int musicValue) {
        this.musicValue = musicValue;
    }

    public int getAlarmValue() {
        return alarmValue;
    }

    public void setAlarmValue(int alarmValue) {
        this.alarmValue = alarmValue;
    }
}
