package com.augusta.dev.personalize.bean;

/**
 * Created by Shanmugavel on 10/26/2016.
 */

public class ModeParentBean {
    String modeType;
    boolean isSelected;

    public ModeParentBean(String modeType, boolean isSelected) {
        this.modeType = modeType;
        this.isSelected = isSelected;
    }

    public String getModeType() {
        return modeType;
    }

    public void setModeType(String modeType) {
        this.modeType = modeType;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
