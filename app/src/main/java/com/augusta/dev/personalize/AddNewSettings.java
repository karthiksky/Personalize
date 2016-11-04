package com.augusta.dev.personalize;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.augusta.dev.personalize.broadcast.AlarmBroadCast;
import com.augusta.dev.personalize.utliz.Constants;
import com.augusta.dev.personalize.utliz.Preference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.augusta.dev.personalize.utliz.Constants.ONE_DAY;

public class AddNewSettings extends AppCompatActivity {

    EditText edt_time;
    Spinner spr_mode;
    Button btn_submit;
    TimePickerDialog mTimePicker;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> m_arr_modes = new ArrayList<>();
    AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_settings);

        edt_time = (EditText) findViewById(R.id.edt_time);
        spr_mode = (Spinner) findViewById(R.id.spr_mode);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edt_time.getText().toString().trim().length() == 0) {
                    Toast.makeText(AddNewSettings.this, "Time cannot be empty.", Toast.LENGTH_SHORT).show();
                } else {
                    if(addData(edt_time.getText().toString(), spr_mode.getSelectedItem().toString())) {
                        Toast.makeText(AddNewSettings.this, "Successfully added.", Toast.LENGTH_SHORT).show();
                        alarmManager= (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                        // Set the alarm to start at approximately 4:00 p.m.
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(System.currentTimeMillis());

                        SimpleDateFormat format_full = new SimpleDateFormat("hh:mm a");
                        SimpleDateFormat format_hrs = new SimpleDateFormat("HH");
                        SimpleDateFormat format_mins = new SimpleDateFormat("mm");
                        String sReqCode="";
                        try {
                            int hour_of_day = Integer.parseInt(format_hrs.format(format_full.parse(edt_time.getText().toString())));
                            int minute = Integer.parseInt(format_mins.format(format_full.parse(edt_time.getText().toString())));
                            calendar.set(Calendar.HOUR_OF_DAY, hour_of_day);
                            calendar.set(Calendar.MINUTE, minute);
                            calendar.set(Calendar.SECOND, 0);
                            sReqCode = hour_of_day + "" + minute;
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        Intent intent = new Intent(AddNewSettings.this, AlarmBroadCast.class);
                        intent.setAction("alarm");
                        intent.putExtra("mode", spr_mode.getSelectedItem().toString());
                        alarmIntent = PendingIntent.getBroadcast(AddNewSettings.this, Integer.parseInt(sReqCode), intent, 0);
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                                ONE_DAY, alarmIntent);

                        finish();

                    } else {
                        Toast.makeText(AddNewSettings.this, "Some error is occur", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        String str_json_modes = Preference.getSharedPreferenceString(AddNewSettings.this, Constants.MODES, "");
        if (str_json_modes.length() != 0) {
            try {
                JSONArray jsonArray = new JSONArray(str_json_modes);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String mode = jsonObject.getString(Constants.MODE_TYPE);
                    m_arr_modes.add(mode);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        arrayAdapter = new ArrayAdapter<String>(AddNewSettings.this, android.R.layout.simple_spinner_dropdown_item, m_arr_modes);
        spr_mode.setAdapter(arrayAdapter);

        edt_time.setFocusable(false);
        edt_time.setClickable(false);
        edt_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String hour="0", min="0";
                int iHour=0, iMin=0;
                SimpleDateFormat format_full = new SimpleDateFormat("hh:mm a");
                SimpleDateFormat format_hour = new SimpleDateFormat("hh");
                SimpleDateFormat format_min = new SimpleDateFormat("mm");
                SimpleDateFormat format_ampm = new SimpleDateFormat("a");

                if(edt_time.getText().length()!=0) {
                    try {
                        hour = format_hour.format(format_full.parse(edt_time.getText().toString()));
                        min = format_min.format(format_full.parse(edt_time.getText().toString()));

                        iHour = Integer.parseInt(hour);
                        iMin = Integer.parseInt(min);

                        if(format_ampm.format(format_full.parseObject(edt_time.getText().toString())).equalsIgnoreCase("pm")) {
                            iHour = iHour + 12;
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                mTimePicker = new TimePickerDialog(AddNewSettings.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        String ampm = " AM";

                        if (selectedHour > 12) {

                            selectedHour = selectedHour - 12;
                            ampm = " PM";
                        } else if (selectedHour == 0) {
                            selectedHour = 12;
                        }

                        String sSelectedHour="", sSelectedMinute="";
                        if(selectedHour <= 9) {
                            sSelectedHour = "0" + selectedHour;
                        } else {
                            sSelectedHour = selectedHour + "";
                        }

                        if(selectedMinute <= 9) {
                            sSelectedMinute = "0" + selectedMinute;
                        } else {
                            sSelectedMinute = selectedMinute + "";
                        }

                        edt_time.setText(sSelectedHour + ":" + sSelectedMinute + ampm);
                    }
                }, iHour, iMin, true);

                mTimePicker.show();
            }
        });
    }

    private boolean addData(String time, String mode) {
        String strSettings = Preference.getSharedPreferenceString(AddNewSettings.this, Constants.SETTINGS, "");

        if(strSettings.length() == 0) {
            try {
                JSONArray jsonArray = new JSONArray();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(Constants.TIME, time);
                jsonObject.put(Constants.MODE, mode);
                jsonArray.put(jsonObject);
                strSettings = jsonArray.toString();
                Preference.setSharedPreferenceString(AddNewSettings.this, Constants.SETTINGS, strSettings);
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            try {
                JSONArray jsonArray = new JSONArray(strSettings);
                JSONObject jsonObject = new JSONObject();

                jsonObject.put(Constants.TIME, time);
                jsonObject.put(Constants.MODE, mode);
                jsonArray.put(jsonObject);
                strSettings = jsonArray.toString();
                Preference.setSharedPreferenceString(AddNewSettings.this, Constants.SETTINGS, strSettings);
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }
}
