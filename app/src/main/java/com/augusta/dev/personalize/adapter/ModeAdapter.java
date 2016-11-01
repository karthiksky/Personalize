package com.augusta.dev.personalize.adapter;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.augusta.dev.personalize.R;
import com.augusta.dev.personalize.bean.ModeChildBean;
import com.augusta.dev.personalize.bean.ModeParentBean;
import com.augusta.dev.personalize.utliz.CommonFunction;

import java.util.ArrayList;

/**
 * Created by Shanmugavel on 10/26/2016.
 */

public class ModeAdapter extends BaseExpandableListAdapter {

    private Activity mActivity;
    private ArrayList<ModeParentBean> mModeType;
    private ArrayList<ArrayList<ModeChildBean>> mModeItems;
    private AudioManager audioManager;

    private int callValue;
    private int musicValue;
    private int alarmValue;

    public ModeAdapter(Activity mActivity, ArrayList<ModeParentBean> mModeType, ArrayList<ArrayList<ModeChildBean>> mModeItems) {
        this.mActivity = mActivity;
        this.mModeType = mModeType;
        this.mModeItems = mModeItems;

        audioManager = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);

        callValue = 0;
        musicValue = 0;
        alarmValue = 0;
    }

    @Override
    public int getGroupCount() {
        return mModeType.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mModeItems.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mModeType.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mModeItems.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean b, View view, ViewGroup viewGroup) {

        final ParentViewHolder viewHolder;
        if (view == null) {
            view = mActivity.getLayoutInflater().inflate(R.layout
                    .view_parent_layout, null);
            viewHolder = new ParentViewHolder();
            viewHolder.mModeTypes = (TextView) view.findViewById(R.id.tv_mode_types);
            viewHolder.mIsSelected = (ImageView) view.findViewById(R.id.iv_is_selected);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ParentViewHolder) view.getTag();
        }

        final ModeParentBean bean = mModeType.get(groupPosition);
        viewHolder.mModeTypes.setText(bean.getModeType());

        if (bean.isSelected()) {
            viewHolder.mIsSelected.setImageResource(R.drawable.ic_check_circle);
            viewHolder.mModeTypes.setTextColor(ContextCompat.getColor(mActivity, R.color.teal));
        } else {
            viewHolder.mIsSelected.setImageResource(R.drawable.ic_check_blank_circle);
            viewHolder.mModeTypes.setTextColor(ContextCompat.getColor(mActivity, R.color.grey_500));
        }

        viewHolder.mIsSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mActivity, "" + groupPosition, Toast.LENGTH_SHORT).show();

                for (int i = 0; i < mModeType.size(); i++) {
                    mModeType.get(i).setSelected(false);
                }
                mModeType.get(groupPosition).setSelected(true);
                notifyDataSetChanged();
                CommonFunction.generateModesType(mActivity, mModeType, mModeItems);

                updateAudioManager();
            }
        });
        return view;
    }

    @Override
    public View getChildView(final int parentPosition, final int childPosition, boolean b, View view, ViewGroup viewGroup) {
        final ChildViewHolder viewHolder;

        // General ListView optimization code.

        if (view == null) {
            view = mActivity.getLayoutInflater().inflate(R.layout
                    .view_chlid_layout, null);
            viewHolder = new ChildViewHolder();
            viewHolder.mCall = (SeekBar) view.findViewById(R.id.sb_call);
            viewHolder.mMusic = (SeekBar) view.findViewById(R.id.sb_music);
            viewHolder.mAlarm = (SeekBar) view.findViewById(R.id.sb_alarm);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ChildViewHolder) view.getTag();
        }

        final ModeChildBean currentBean = mModeItems.get(parentPosition).get(childPosition);

        viewHolder.mCall.setMax(audioManager
                .getStreamMaxVolume(AudioManager.STREAM_SYSTEM));
        viewHolder.mMusic.setMax(audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        viewHolder.mAlarm.setMax(audioManager
                .getStreamMaxVolume(AudioManager.STREAM_ALARM));

        Log.e("Before Call ", String.valueOf(currentBean.getCallValue()));
        viewHolder.mCall.setProgress(currentBean.getCallValue());
        viewHolder.mMusic.setProgress(currentBean.getMusicValue());
        viewHolder.mAlarm.setProgress(currentBean.getAlarmValue());

        try {
            viewHolder.mCall.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        mModeItems.get(parentPosition).get(childPosition).setCallValue(viewHolder.mCall.getProgress());
                        CommonFunction.generateModesType(mActivity, mModeType, mModeItems);
                        updateAudioManager();
                    }
                    return false;
                }
            });

            viewHolder.mMusic.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        mModeItems.get(parentPosition).get(childPosition).setMusicValue(viewHolder.mMusic.getProgress());
                        CommonFunction.generateModesType(mActivity, mModeType, mModeItems);
                        updateAudioManager();
                    }
                    return false;
                }
            });

            viewHolder.mAlarm.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        mModeItems.get(parentPosition).get(childPosition).setAlarmValue(viewHolder.mAlarm.getProgress());
                        CommonFunction.generateModesType(mActivity, mModeType, mModeItems);
                        updateAudioManager();
                    }
                    return false;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    private class ParentViewHolder {
        private TextView mModeTypes;
        private ImageView mIsSelected;
    }

    private class ChildViewHolder {
        private SeekBar mCall;
        private SeekBar mMusic;
        private SeekBar mAlarm;
    }

    private void updateAudioManager() {

        for (int i = 0; i < mModeType.size(); i++) {
            if (mModeType.get(i).isSelected()) {
                audioManager.setStreamVolume(
                        AudioManager.STREAM_SYSTEM, mModeItems.get(i).get(0).getCallValue(), 0);

                audioManager.setStreamVolume(
                        AudioManager.STREAM_MUSIC, mModeItems.get(i).get(0).getMusicValue(), 0);

                audioManager.setStreamVolume(
                        AudioManager.STREAM_ALARM, mModeItems.get(i).get(0).getAlarmValue(), 0);
            }
        }
    }
}
