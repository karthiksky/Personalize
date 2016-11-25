package com.augusta.dev.personalize;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.augusta.dev.personalize.adapter.SettingsAdapter;
import com.augusta.dev.personalize.bean.SettingsEntity;
import com.augusta.dev.personalize.broadcast.AlarmBroadCastReceiver;
import com.augusta.dev.personalize.utliz.Constants;
import com.augusta.dev.personalize.utliz.Preference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends AppCompatActivity {

    RecyclerView rcv_list_automatic_settings;
    SettingsAdapter mAdapter;
    List<SettingsEntity> entities=new ArrayList<>();
    FloatingActionButton fab_add_new_settings;
    private int INTENT_SETTINGS=120;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        rcv_list_automatic_settings = (RecyclerView) findViewById(R.id.rcv_list_automatic_settings);
        fab_add_new_settings = (FloatingActionButton) findViewById(R.id.fab_add_new_settings);

        fab_add_new_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, AddNewSettingActivity.class);
                startActivityForResult(intent, INTENT_SETTINGS);
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rcv_list_automatic_settings.setLayoutManager(mLayoutManager);
        rcv_list_automatic_settings.setItemAnimator(new DefaultItemAnimator());

        prepareMovieData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        entities = new ArrayList<>();
        prepareMovieData();
    }

    private void prepareMovieData() {

        String str_settings_json = Preference.getSharedPreferenceString(SettingActivity.this, Constants.SETTINGS, "");

        try {
            JSONArray jsonArray = new JSONArray(str_settings_json);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);

                entities.add(new SettingsEntity(jsonObject.getString(Constants.TIME),
                        jsonObject.getString(Constants.MODE)));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        mAdapter = new SettingsAdapter(entities);
        mAdapter.setActivity(SettingActivity.this);
        mAdapter.setOnClick(new SettingsAdapter.OnClick() {
            @Override
            public void onClick(int position) {
            }

            @Override
            public void onDeleteClick(int position) {
                alertForDelete(position);
            }
        });
        rcv_list_automatic_settings.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void alertForDelete(final int position) {
        new AlertDialog.Builder(SettingActivity.this)
                .setTitle(R.string.app_name)
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        DeleteClick(position);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

    private void ClearAlarm (String time) {

        SimpleDateFormat format_full = new SimpleDateFormat("hh:mm a");
        SimpleDateFormat format_hour = new SimpleDateFormat("HH");
        SimpleDateFormat format_minute = new SimpleDateFormat("mm");
        int value = 0;
        
        try {
            String hour = format_hour.format(format_full.parse(time));
            String minute = format_minute.format(format_full.parse(time));
            
            value = Integer.parseInt(hour + minute + "");
            
        } catch (ParseException e) {
            e.printStackTrace();
        }

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        Intent updateServiceIntent = new Intent(this, AlarmBroadCastReceiver.class);
        PendingIntent pendingUpdateIntent = PendingIntent.getBroadcast(this, value, updateServiceIntent, 0);

        // Cancel alarms
        try {
            alarmManager.cancel(pendingUpdateIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void DeleteClick(int pos) {
        String str_json_settings = Preference.getSharedPreferenceString(SettingActivity.this, Constants.SETTINGS, "");
        if(str_json_settings.length() != 0) {
            try {
                JSONArray jsonArray = new JSONArray(str_json_settings);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    jsonArray.remove(pos);
                    ClearAlarm(entities.get(pos).getTime());
                    entities.remove(pos);
                }

                Preference.setSharedPreferenceString(SettingActivity.this, Constants.SETTINGS, jsonArray.toString());
                mAdapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
