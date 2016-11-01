package com.augusta.dev.personalize.utliz;

import android.app.Activity;
import android.util.Log;

import com.augusta.dev.personalize.bean.ModeChildBean;
import com.augusta.dev.personalize.bean.ModeParentBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Shanmugavel on 10/28/2016.
 */

public class CommonFunction {
    public static void generateModesType(Activity mActivity, ArrayList<ModeParentBean> mModeType, ArrayList<ArrayList<ModeChildBean>> mModeItems) {
        JSONArray modesArray = new JSONArray();
        for (int i = 0; i < mModeType.size(); i++) {
            JSONObject jsonObj = new JSONObject();
            try {
                jsonObj.put(Constants.MODE_TYPE, mModeType.get(i).getModeType());
                jsonObj.put(Constants.IS_SELECT, mModeType.get(i).isSelected());

                jsonObj.put(Constants.ALARM, mModeItems.get(i).get(0).getAlarmValue());
                jsonObj.put(Constants.CALL, mModeItems.get(i).get(0).getCallValue());
                jsonObj.put(Constants.MUSIC, mModeItems.get(i).get(0).getMusicValue());

                Log.e("jsonObj", jsonObj.toString());

                modesArray.put(jsonObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Preference.setSharedPreferenceString(mActivity, Constants.MODES, modesArray.toString());
    }
}
