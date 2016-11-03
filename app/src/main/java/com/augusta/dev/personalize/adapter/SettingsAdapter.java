package com.augusta.dev.personalize.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.augusta.dev.personalize.R;
import com.augusta.dev.personalize.bean.SettingsEntity;
import com.augusta.dev.personalize.utliz.Constants;
import com.augusta.dev.personalize.utliz.Preference;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.MyViewHolder> {

    private List<SettingsEntity> profileList;
    private Activity activity;
    public interface OnClick {
        void onClick(int position);
        void onDeleteClick(int position);
    }

    OnClick onClick;

    public void setOnClick(OnClick onClick) {
        this.onClick = onClick;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(View view) {
            super(view);
        }
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public SettingsAdapter(List<SettingsEntity> moviesList) {
        this.profileList = moviesList;
    }
 
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_settings, parent, false);

        return new MyViewHolder(itemView);
    }
 
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        TextView txtTime = (TextView) holder.itemView.findViewById(R.id.txtTime);
        TextView txtMode = (TextView) holder.itemView.findViewById(R.id.txtMode);
        ImageView img_delete = (ImageView) holder.itemView.findViewById(R.id.img_delete);
        img_delete.setTag(position);
        img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onClick != null) {
                    onClick.onDeleteClick(position);
                }
            }
        });
        txtTime.setText(profileList.get(position).getTime());
        txtMode.setText(profileList.get(position).getMode());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onClick != null) {
                    onClick.onClick(position);
                }
            }
        });
    }
 
    @Override
    public int getItemCount() {
        return profileList.size();
    }
}