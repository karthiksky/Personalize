package com.augusta.dev.personalize.utliz;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import com.augusta.dev.personalize.R;
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

    static int[] drawableSelect = new int[]{R.drawable.ic_notify_normal_select, R.drawable.ic_notify_silent_select, R.drawable.ic_notify_office_select, R.drawable.ic_notify_meeting_select, R.drawable.ic_notify_travel_select};
    static int[] drawableUnSelect = new int[]{R.drawable.ic_notify_normal_unselect, R.drawable.ic_notify_silent_unselect, R.drawable.ic_notify_office_unselect, R.drawable.ic_notify_meeting_unselect, R.drawable.ic_notify_travel_unselect};

    static int[] drawableSelectWidget = new int[]{R.drawable.ic_notify_normal_select_png, R.drawable.ic_notify_silent_select_png, R.drawable.ic_notify_office_select_png, R.drawable.ic_notify_meeting_select_png, R.drawable.ic_notify_travel_select_png};
    static int[] drawableUnSelectWidget = new int[]{R.drawable.ic_notify_normal_unselect_png, R.drawable.ic_notify_silent_unselect_png, R.drawable.ic_notify_office_unselect_png, R.drawable.ic_notify_meeting_unselect_png, R.drawable.ic_notify_travel_unselect_png};

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

    public static void setVectorRemoteView(RemoteViews remoteViews, int resourceId, int position, boolean isSelect) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (isSelect)
                remoteViews.setTextViewCompoundDrawables(resourceId, 0, drawableSelect[position], 0, 0);
            else
                remoteViews.setTextViewCompoundDrawables(resourceId, 0, drawableUnSelect[position], 0, 0);

        } else {
            if (isSelect)
                remoteViews.setTextViewCompoundDrawables(resourceId, 0, drawableSelectWidget[position], 0, 0);
            else
                remoteViews.setTextViewCompoundDrawables(resourceId, 0, drawableUnSelectWidget[position], 0, 0);
        }
    }
}
