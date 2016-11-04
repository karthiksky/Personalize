package com.augusta.dev.personalize;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.augusta.dev.personalize.utliz.Constants;
import com.augusta.dev.personalize.utliz.Preference;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {

    public RemoteViews views;
    private AppWidgetManager mAppWidgetManager;
    int mAppWidgetId;
    int[] resourceId = new int[]{R.id.normal, R.id.silent, R.id.office, R.id.meeting, R.id.travel};
    int[] drawableSelect = new int[]{R.drawable.ic_notify_normal_select, R.drawable.ic_notify_silent_select, R.drawable.ic_notify_office_select, R.drawable.ic_notify_meeting_select, R.drawable.ic_notify_travel_select};
    int[] drawableUnSelect = new int[]{R.drawable.ic_notify_normal_unselect, R.drawable.ic_notify_silent_unselect, R.drawable.ic_notify_office_unselect, R.drawable.ic_notify_meeting_unselect, R.drawable.ic_notify_travel_unselect};


    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                         int appWidgetId) {
        mAppWidgetManager = appWidgetManager;
        mAppWidgetId = appWidgetId;

        // Construct the RemoteViews object
        views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);

        views.setOnClickPendingIntent(R.id.normal, getPendingSelfIntent(context, "normal"));
        views.setOnClickPendingIntent(R.id.silent, getPendingSelfIntent(context, "silent"));
        views.setOnClickPendingIntent(R.id.office, getPendingSelfIntent(context, "office"));
        views.setOnClickPendingIntent(R.id.meeting, getPendingSelfIntent(context, "meeting"));
        views.setOnClickPendingIntent(R.id.travel, getPendingSelfIntent(context, "travel"));

        // Instruct the widget manager to update the widget
        //appWidgetManager.updateAppWidget(appWidgetId, views);

        updateAppWidget(context);
        mAppWidgetManager.updateAppWidget(mAppWidgetId, views);
    }

    private void updateAppWidget(Context context) {
        try {

            String sJsonArray = Preference.getSharedPreferenceString(context, Constants.MODES, "");

            if (!sJsonArray.equalsIgnoreCase("")) {

                JSONArray jsonArray = new JSONArray(sJsonArray);

                for (int i = 0; i < jsonArray.length(); i++) {

                    views.setTextViewCompoundDrawables(resourceId[i], 0, drawableUnSelect[i], 0, 0);
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.getBoolean(Constants.IS_SELECT))
                        views.setTextViewCompoundDrawables(resourceId[i], 0, drawableSelect[i], 0, 0);
                }
            }
        } catch (Exception exp) {
            Toast.makeText(context, "Error " + exp.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, NewAppWidget.class);
        intent.setAction(action);

        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        boolean result = false;
        views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        ComponentName thisWidget = new ComponentName(context, NewAppWidget.class);

        try {

            String sJsonArray = Preference.getSharedPreferenceString(context, Constants.MODES, "");

            if (!sJsonArray.equalsIgnoreCase("")) {

                JSONArray jsonArray = new JSONArray(sJsonArray);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String mode_type = jsonObject.getString(Constants.MODE_TYPE);
                    String call = jsonObject.getString(Constants.CALL);
                    String music = jsonObject.getString(Constants.MUSIC);
                    String alarm = jsonObject.getString(Constants.ALARM);

                    jsonObject.put(Constants.IS_SELECT, false);

                    views.setTextViewCompoundDrawables(resourceId[i], 0, drawableUnSelect[i], 0, 0);
                    if (mode_type.toUpperCase().equals(intent.getAction().toUpperCase())) {
                        updateVolume(context, AudioManager.STREAM_SYSTEM, Integer.parseInt(call));
                        updateVolume(context, AudioManager.STREAM_ALARM, Integer.parseInt(alarm));
                        updateVolume(context, AudioManager.STREAM_MUSIC, Integer.parseInt(music));
                        result = true;
                        jsonObject.put(Constants.IS_SELECT, true);
                        views.setTextViewCompoundDrawables(resourceId[i], 0, drawableSelect[i], 0, 0);
                    }
                }
                if (result) {
                    AppWidgetManager.getInstance(context).updateAppWidget(thisWidget, views);
                    Preference.setSharedPreferenceString(context, Constants.MODES, jsonArray.toString());
                    PersonalizeActivity.customNotification(context);
                }
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

