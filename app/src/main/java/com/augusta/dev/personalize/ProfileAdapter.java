package com.augusta.dev.personalize;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.MyViewHolder> {
 
    private List<String> profileList;
    public interface OnClick {
        void onClick(int position);
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
 
 
    public ProfileAdapter(List<String> moviesList) {
        this.profileList = moviesList;
    }
 
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);

        return new MyViewHolder(itemView);
    }
 
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        TextView textView = (TextView) holder.itemView.findViewById(android.R.id.text1);
        textView.setText(profileList.get(position));
        textView.setTextColor(Color.parseColor("#000000"));
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