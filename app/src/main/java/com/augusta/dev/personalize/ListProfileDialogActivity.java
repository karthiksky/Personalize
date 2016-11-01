package com.augusta.dev.personalize;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListProfileDialogActivity extends Activity {

    private List<String> movieList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ProfileAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_profile_dialog);

        recyclerView = (RecyclerView) findViewById(R.id.rcv_list_profile);

        mAdapter = new ProfileAdapter(movieList);
        mAdapter.setOnClick(new ProfileAdapter.OnClick() {
            @Override
            public void onClick(int position) {
                onReceive(position);
                finish();
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        prepareMovieData();
    }

    public void onReceive(int position) {

        Context context = this;

        try {

            if(position == 0) {
                //your onClick action is here

                updateVolume(context, AudioManager.STREAM_SYSTEM, 3);
                updateVolume(context, AudioManager.STREAM_ALARM, 3);
                updateVolume(context, AudioManager.STREAM_MUSIC, 3);

                Toast.makeText(context, "Successfully updated", Toast.LENGTH_SHORT).show();

            } else if (position == 1) {
                //your onClick action is here

                updateVolume(context, AudioManager.STREAM_SYSTEM, 5);
                updateVolume(context, AudioManager.STREAM_ALARM, 5);
                updateVolume(context, AudioManager.STREAM_MUSIC, 5);

                Toast.makeText(context, "Successfully updated", Toast.LENGTH_SHORT).show();

            } else if (position == 2) {
                //your onClick action is here
                updateVolume(context, AudioManager.STREAM_SYSTEM, 0);
                updateVolume(context, AudioManager.STREAM_ALARM, 0);
                updateVolume(context, AudioManager.STREAM_MUSIC, 0);

                Toast.makeText(context, "Successfully updated", Toast.LENGTH_SHORT).show();
            } else if (position == 3) {
                //your onClick action is here
                updateVolume(context, AudioManager.STREAM_SYSTEM, 2);
                updateVolume(context, AudioManager.STREAM_ALARM, 2);
                updateVolume(context, AudioManager.STREAM_MUSIC, 2);

                Toast.makeText(context, "Successfully updated", Toast.LENGTH_SHORT).show();
            } else if (position == 4) {
                //your onClick action is here
                updateVolume(context, AudioManager.STREAM_SYSTEM, 0);
                updateVolume(context, AudioManager.STREAM_ALARM, 0);
                updateVolume(context, AudioManager.STREAM_MUSIC, 0);

                Toast.makeText(context, "Successfully updated", Toast.LENGTH_SHORT).show();
            } else if (position == 5) {
                //your onClick action is here
                updateVolume(context, AudioManager.STREAM_SYSTEM, 1);
                updateVolume(context, AudioManager.STREAM_ALARM, 1);
                updateVolume(context, AudioManager.STREAM_MUSIC, 1);

                Toast.makeText(context, "Successfully updated", Toast.LENGTH_SHORT).show();
            } else if (position == 6) {
                //your onClick action is here
                updateVolume(context, AudioManager.STREAM_SYSTEM, -1);
                updateVolume(context, AudioManager.STREAM_ALARM, -1);
                updateVolume(context, AudioManager.STREAM_MUSIC, -1);

                Toast.makeText(context, "Successfully updated", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception exp) {
            Toast.makeText(context, "Error " + exp.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateVolume(Context context, int type, int value) {

        AudioManager am =
                (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        int max = value;

        if(value == -1) {
            max = am.getStreamMaxVolume(type);
        }

        am.setStreamVolume(
                type,
                max,
                0);
    }

    private void prepareMovieData() {

        movieList.add("Home");
        movieList.add("Normal");
        movieList.add("Flight");
        movieList.add("Travel");
        movieList.add("Meeting");

        mAdapter.notifyDataSetChanged();
    }
}
