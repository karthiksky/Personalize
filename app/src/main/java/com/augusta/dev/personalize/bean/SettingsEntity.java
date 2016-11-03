package com.augusta.dev.personalize.bean;

/**
 * Created by skarthik on 11/2/2016.
 */

public class SettingsEntity
{
    String time="";
    String mode="";

    public SettingsEntity(String time, String mode) {
        this.time = time;
        this.mode = mode;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
