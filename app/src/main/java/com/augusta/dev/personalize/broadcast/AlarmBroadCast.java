package com.augusta.dev.personalize.broadcast;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.widget.Toast;

import com.augusta.dev.personalize.NewAppWidget;
import com.augusta.dev.personalize.PersonalizeActivity;
import com.augusta.dev.personalize.utliz.Constants;
import com.augusta.dev.personalize.utliz.Preference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by skarthik on 11/3/2016.
 */

public class AlarmBroadCast extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equalsIgnoreCase("alarm") == true) {

            String mode = intent.getStringExtra("mode");

            String modes = Preference.getSharedPreferenceString(context, Constants.MODES, "");

            if(modes.length() != 0) {

                try {
                    JSONArray jsonArray = new JSONArray(modes);
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String str_json_mode = jsonObject.getString(Constants.MODE_TYPE);

                        jsonObject.put(Constants.IS_SELECT, false);

                        if(str_json_mode.toUpperCase().equalsIgnoreCase(mode.toUpperCase()) == true) {

                            int call = jsonObject.getInt(Constants.CALL);
                            int music = jsonObject.getInt(Constants.MUSIC);
                            int alarm = jsonObject.getInt(Constants.ALARM);

                            jsonObject.put(Constants.IS_SELECT, true);

                            NewAppWidget.updateVolume(context, AudioManager.STREAM_SYSTEM, call);
                            NewAppWidget.updateVolume(context, AudioManager.STREAM_MUSIC, music);
                            NewAppWidget.updateVolume(context, AudioManager.STREAM_ALARM, alarm);

                            Toast.makeText(context, "Successfully changed to " + mode + " time " + (new Date()).toString(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    Preference.setSharedPreferenceString(context, Constants.MODES, jsonArray.toString());
                    PersonalizeActivity.customNotification(context);
                    updateWidgetManager(context);
                    context.sendBroadcast(new Intent(Constants.ONLISTUPDATE));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateWidgetManager(Context context) {
        Intent intent = new Intent(context, NewAppWidget.class);
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        int ids[] = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, NewAppWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }
}
