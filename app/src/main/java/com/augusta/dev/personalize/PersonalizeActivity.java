package com.augusta.dev.personalize;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
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

import static com.augusta.dev.personalize.utliz.Constants.ONLISTUPDATE;

public class PersonalizeActivity extends AppCompatActivity {

    public static Activity mPersonalizeActivity;
    private ExpandableListView elvModeList;
    public ModeAdapter mModeAdapter;
    private ArrayList<ModeParentBean> mModeType;
    private ArrayList<ArrayList<ModeChildBean>> mModeItems;

    static int[] resourceId = new int[]{R.id.normal, R.id.silent, R.id.office, R.id.meeting, R.id.travel};
    static int[] drawableSelect = new int[]{R.drawable.ic_notify_normal_select, R.drawable.ic_notify_silent_select, R.drawable.ic_notify_office_select, R.drawable.ic_notify_meeting_select, R.drawable.ic_notify_travel_select};
    static int[] drawableUnSelect = new int[]{R.drawable.ic_notify_normal_unselect, R.drawable.ic_notify_silent_unselect, R.drawable.ic_notify_office_unselect, R.drawable.ic_notify_meeting_unselect, R.drawable.ic_notify_travel_unselect};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalize);
        findViewById();
        mPersonalizeActivity = this;
        registerReceiver(broadcastReceiver, new IntentFilter(ONLISTUPDATE));
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // internet lost alert dialog method call from here...
            dataPopulate();
            setAdapter();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!Preference.getSharedPreferenceBoolean(this, Constants.IS_FIRST, false)) {
            initDataPopulate();
        } else {
            dataPopulate();
        }

        setAdapter();
        customNotification(this);
    }

    public static void customNotification(Context mActivity) {
        // Using RemoteViews to bind custom layouts into Notification
        RemoteViews remoteViews = new RemoteViews(mActivity.getPackageName(),
                R.layout.custom_notification);

        // Open NotificationView.java Activity
        PendingIntent pIntent = PendingIntent.getActivity(mActivity, 0, new Intent(),
                PendingIntent.FLAG_UPDATE_CURRENT);

        updateAppWidget(mActivity, remoteViews);

        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(mActivity)
                .setSmallIcon(R.drawable.ic_p_24)
                .setTicker("Notification")
                .setAutoCancel(false)
                .setContentIntent(pIntent)
                .setContent(remoteViews);

        remoteViews.setOnClickPendingIntent(R.id.normal, getPendingSelfIntent(mActivity, "normal"));
        remoteViews.setOnClickPendingIntent(R.id.silent, getPendingSelfIntent(mActivity, "silent"));
        remoteViews.setOnClickPendingIntent(R.id.office, getPendingSelfIntent(mActivity, "office"));
        remoteViews.setOnClickPendingIntent(R.id.meeting, getPendingSelfIntent(mActivity, "meeting"));
        remoteViews.setOnClickPendingIntent(R.id.travel, getPendingSelfIntent(mActivity, "travel"));

        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) mActivity.getSystemService(NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_NO_CLEAR;
        notificationmanager.notify(Constants.NOTIFICATION_ID, notification);
    }

    protected static PendingIntent getPendingSelfIntent(Context context, String action) {

        Intent intent = new Intent(context, PendingBroadCastReceiver.class);
        intent.setAction(action);

        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
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
        modeChildBean = new ModeChildBean(2, 2, 2);
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

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.notification_clear:
                String str = item.getTitle().toString();
                if (str.equals(getString(R.string.notification_clear))) {
                    NotificationManager notificationManager = (NotificationManager) this
                            .getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(Constants.NOTIFICATION_ID);
                    item.setTitle(R.string.notification_show);
                } else {
                    item.setTitle(R.string.notification_clear);
                    customNotification(this);
                }

                return true;
            case R.id.settings:
                Intent intent = new Intent(PersonalizeActivity.this, SettingsActivity.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private static void updateAppWidget(Context mActivity, RemoteViews remoteViews) {

        try {

            String sJsonArray = Preference.getSharedPreferenceString(mActivity, Constants.MODES, "");

            if (!sJsonArray.equalsIgnoreCase("")) {

                JSONArray jsonArray = new JSONArray(sJsonArray);

                for (int i = 0; i < jsonArray.length(); i++) {

                    remoteViews.setTextViewCompoundDrawables(resourceId[i], 0, drawableUnSelect[i], 0, 0);
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.getBoolean(Constants.IS_SELECT))
                        remoteViews.setTextViewCompoundDrawables(resourceId[i], 0, drawableSelect[i], 0, 0);
                }
            }
        } catch (Exception exp) {
            Toast.makeText(mActivity, "Error " + exp.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }


}
