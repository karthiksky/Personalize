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

public class PendingBroadCastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        try {

            String sJsonArray = Preference.getSharedPreferenceString(context, Constants.MODES, "");

            if (!sJsonArray.equalsIgnoreCase("")) {

                try {
                    JSONArray jsonArray = new JSONArray(sJsonArray);

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String mode_type = jsonObject.getString(Constants.MODE_TYPE);
                        String is_select = jsonObject.getString(Constants.IS_SELECT);
                        String call = jsonObject.getString(Constants.CALL);
                        String music = jsonObject.getString(Constants.MUSIC);
                        String alarm = jsonObject.getString(Constants.ALARM);

                        jsonObject.put(Constants.IS_SELECT, false);

                        if (mode_type.toUpperCase().equals(intent.getAction().toUpperCase())) {
                            //your onClick action is here

                            updateVolume(context, AudioManager.STREAM_SYSTEM, Integer.parseInt(call));
                            updateVolume(context, AudioManager.STREAM_ALARM, Integer.parseInt(alarm));
                            updateVolume(context, AudioManager.STREAM_MUSIC, Integer.parseInt(music));

                            jsonObject.put(Constants.IS_SELECT, true);
                        }
                    }

                    Preference.setSharedPreferenceString(context, Constants.MODES, jsonArray.toString());
                    PersonalizeActivity.customNotification(context);
                    context.sendBroadcast(new Intent(Constants.ONLISTUPDATE));
                    updateWidgetManager(context);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {

            }
        } catch (Exception exp) {
            Toast.makeText(context, "Error " + exp.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateWidgetManager(Context context) {
        Intent intent = new Intent(context, NewAppWidget.class);
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        int ids[] = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, NewAppWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }

    private void updateVolume(Context context, int type, int value) {

        AudioManager am =
                (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        int max = value;

        if (value == -1) {
            max = am.getStreamMaxVolume(type);
        }

        am.setStreamVolume(
                type,
                max,
                0);
    }
}
