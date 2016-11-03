package com.augusta.dev.personalize;

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

import com.augusta.dev.personalize.adapter.SettingsAdapter;
import com.augusta.dev.personalize.bean.SettingsEntity;
import com.augusta.dev.personalize.utliz.Constants;
import com.augusta.dev.personalize.utliz.Preference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

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
                Intent intent = new Intent(SettingsActivity.this, AddNewSettings.class);
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

        String str_settings_json = Preference.getSharedPreferenceString(SettingsActivity.this, Constants.SETTINGS, "");

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
        mAdapter.setActivity(SettingsActivity.this);
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
        new AlertDialog.Builder(SettingsActivity.this)
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
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void DeleteClick(int pos) {
        String str_json_settings = Preference.getSharedPreferenceString(SettingsActivity.this, Constants.SETTINGS, "");
        if(str_json_settings.length() != 0) {
            try {
                JSONArray jsonArray = new JSONArray(str_json_settings);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    jsonArray.remove(pos);
                    entities.remove(pos);
                }

                Preference.setSharedPreferenceString(SettingsActivity.this, Constants.SETTINGS, jsonArray.toString());
                mAdapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
