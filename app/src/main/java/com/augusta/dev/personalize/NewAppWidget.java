package com.augusta.dev.personalize;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.augusta.dev.personalize.utliz.Constants;
import com.augusta.dev.personalize.utliz.Preference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                         int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.app_name);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);

        views.setOnClickPendingIntent(R.id.normal, getPendingSelfIntent(context, "normal"));
        views.setOnClickPendingIntent(R.id.silent, getPendingSelfIntent(context, "silent"));
        views.setOnClickPendingIntent(R.id.office, getPendingSelfIntent(context, "office"));
        views.setOnClickPendingIntent(R.id.meeting, getPendingSelfIntent(context, "meeting"));
        views.setOnClickPendingIntent(R.id.travel, getPendingSelfIntent(context, "travel"));

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, NewAppWidget.class);
        intent.setAction(action);

        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        try {

            String sJsonArray = Preference.getSharedPreferenceString(context, Constants.MODES, "");

            if (!sJsonArray.equalsIgnoreCase("")) {

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

                        updateVolume(context, AudioManager.STREAM_SYSTEM, Integer.parseInt(call));
                        updateVolume(context, AudioManager.STREAM_ALARM, Integer.parseInt(alarm));
                        updateVolume(context, AudioManager.STREAM_MUSIC, Integer.parseInt(music));

                        jsonObject.put(Constants.IS_SELECT, true);
                    }
                }

                Preference.setSharedPreferenceString(context, Constants.MODES, jsonArray.toString());
            }
        } catch (Exception exp) {
            Toast.makeText(context, "Error " + exp.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static void updateVolume(Context context, int type, int value) {

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

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
}

