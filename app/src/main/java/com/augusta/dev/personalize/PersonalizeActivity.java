package com.augusta.dev.personalize;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.augusta.dev.personalize.adapter.ModeAdapter;
import com.augusta.dev.personalize.bean.ModeChildBean;
import com.augusta.dev.personalize.bean.ModeParentBean;
import com.augusta.dev.personalize.utliz.CommonFunction;
import com.augusta.dev.personalize.utliz.Constants;
import com.augusta.dev.personalize.utliz.Preference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PersonalizeActivity extends AppCompatActivity {

    private ExpandableListView elvModeList;
    private ModeAdapter mModeAdapter;
    private ArrayList<ModeParentBean> mModeType;
    private ArrayList<ArrayList<ModeChildBean>> mModeItems;
    private int INTENT_WRITE_SETTINGS=121;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalize);

        findViewById();
        if (!Preference.getSharedPreferenceBoolean(this, Constants.IS_FIRST, false)) {
            initDataPopulate();
        } else {
            dataPopulate();
        }

        setAdapter();
        CustomNotification();
    }

    public void CustomNotification() {
        // Using RemoteViews to bind custom layouts into Notification
        RemoteViews remoteViews = new RemoteViews(getPackageName(),
                R.layout.custom_notification);

        // Open NotificationView.java Activity
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, new Intent(),
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("Notification")
                .setAutoCancel(false)
                .setContentIntent(pIntent)
                .setContent(remoteViews);

        remoteViews.setOnClickPendingIntent(R.id.normal, getPendingSelfIntent(this, "normal"));
        remoteViews.setOnClickPendingIntent(R.id.silent, getPendingSelfIntent(this, "silent"));
        remoteViews.setOnClickPendingIntent(R.id.office, getPendingSelfIntent(this, "office"));
        remoteViews.setOnClickPendingIntent(R.id.meeting, getPendingSelfIntent(this, "meeting"));
        remoteViews.setOnClickPendingIntent(R.id.travel, getPendingSelfIntent(this, "travel"));

       /* String sJsonArray = Preference.getSharedPreferenceString(PersonalizeActivity.this, Constants.MODES, "");

        if(!sJsonArray.equalsIgnoreCase("")) {

            try {
                JSONArray jsonArray = new JSONArray(sJsonArray);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String mode_type = jsonObject.getString(Constants.MODE_TYPE);
                    boolean is_select = jsonObject.getBoolean(Constants.IS_SELECT);
                    String call = jsonObject.getString(Constants.CALL);
                    String music = jsonObject.getString(Constants.MUSIC);
                    String alarm = jsonObject.getString(Constants.ALARM);

                    if (is_select == true) {
                        //your onClick action is here

                        if(mode_type.toUpperCase().equalsIgnoreCase("NORMAL")) {
                            remoteViews.setTextColor(R);
                        } else if(mode_type.toUpperCase().equalsIgnoreCase("SILENT")) {

                        } else if(mode_type.toUpperCase().equalsIgnoreCase("OFFICE")) {

                        } else if(mode_type.toUpperCase().equalsIgnoreCase("MEETING")) {

                        } else if(mode_type.toUpperCase().equalsIgnoreCase("TRAVEL")) {

                        }

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {

        }*/

        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_NO_CLEAR;
        notificationmanager.notify(Constants.NOTIFICATION_ID, notification);
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {

        Intent intent = new Intent(context, PendingBroadCastReceiver.class);
        intent.setAction(action);

        if (action.equalsIgnoreCase("others")) {
            Intent intent1 = new Intent(context, ListProfileDialogActivity.class);
            return PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }

    private void dataPopulate() {
        String modeTypes = Preference.getSharedPreferenceString(this, Constants.MODES, "");
        try {
            JSONArray modesArray = new JSONArray(modeTypes);
            mModeType = new ArrayList<>();

            mModeItems = new ArrayList<>();
            ArrayList<ModeChildBean> subItem;
            ModeChildBean modeChildBean;

            for (int i = 0; i < modesArray.length(); i++) {
                JSONObject obj = modesArray.getJSONObject(i);
                ModeParentBean parentBean;

                parentBean = new ModeParentBean(obj.getString(Constants.MODE_TYPE), obj.getBoolean(Constants.IS_SELECT));
                mModeType.add(parentBean);

                int alarm = obj.getInt(Constants.ALARM);
                int call = obj.getInt(Constants.CALL);
                int music = obj.getInt(Constants.MUSIC);

                subItem = new ArrayList<>();
                modeChildBean = new ModeChildBean(call, music, alarm);
                subItem.add(modeChildBean);
                mModeItems.add(i, subItem);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initDataPopulate() {
        mModeType = new ArrayList<>();
        ModeParentBean parentBean;

        parentBean = new ModeParentBean("Normal", true);
        mModeType.add(parentBean);
        parentBean = new ModeParentBean("Silent", false);
        mModeType.add(parentBean);
        parentBean = new ModeParentBean("Office", false);
        mModeType.add(parentBean);
        parentBean = new ModeParentBean("Meeting", false);
        mModeType.add(parentBean);
        parentBean = new ModeParentBean("Travel", false);
        mModeType.add(parentBean);

        mModeItems = new ArrayList<>();
        ArrayList<ModeChildBean> subItem;
        ModeChildBean modeChildBean;

        subItem = new ArrayList<>();
        modeChildBean = new ModeChildBean(0, 0, 0);
        subItem.add(modeChildBean);
        mModeItems.add(0, subItem);


        subItem = new ArrayList<>();
        modeChildBean = new ModeChildBean(0, 0, 0);
        subItem.add(modeChildBean);
        mModeItems.add(1, subItem);

        subItem = new ArrayList<>();
        modeChildBean = new ModeChildBean(0, 0, 0);
        subItem.add(modeChildBean);
        mModeItems.add(2, subItem);

        subItem = new ArrayList<>();
        modeChildBean = new ModeChildBean(0, 0, 0);
        subItem.add(modeChildBean);
        mModeItems.add(3, subItem);

        subItem = new ArrayList<>();
        modeChildBean = new ModeChildBean(0, 0, 0);
        subItem.add(modeChildBean);
        mModeItems.add(4, subItem);

        CommonFunction.generateModesType(this, mModeType, mModeItems);
        Preference.setSharedPreferenceBoolean(this, Constants.IS_FIRST, true);
    }

    private void setAdapter() {
        mModeAdapter = new ModeAdapter(this, mModeType, mModeItems);
        elvModeList.setAdapter(mModeAdapter);
    }

    private void findViewById() {
        elvModeList = (ExpandableListView) findViewById(R.id.elv_mode);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }
}
