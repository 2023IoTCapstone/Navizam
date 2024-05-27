package com.cecom.navizam.fragments;//mainactivity

import static android.app.PendingIntent.getBroadcast;
import static android.text.format.DateFormat.*;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Bundle;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TimePicker;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.text.DateFormat;
import java.util.Calendar;

import com.cecom.navizam.R;

public class fragment_settings extends Fragment implements TimePickerDialog.OnTimeSetListener{

    TextView wakeText;
    TextView sleepText;
    Button btn_wake;
    Button btn_sleep;
    Switch btn_onoff;
    final Calendar[] Time = new Calendar[2];
    private static final int NOTIFICATION_ID = 1;
    private NotificationManager notificationManager;
    private Calendar sleepTime = Calendar.getInstance();
    private Calendar wakeTime = Calendar.getInstance();
    private boolean timesSet = false; // Flag to check if times have been set
    private static final long ALARM_INTERVAL = 1 * 1 * 60 * 1000; // 3 hours


    @SuppressLint("MissingInflatedId")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        //super.onCreate(savedInstanceState);

        View settingView = inflater.inflate(R.layout.fragment_settings, container,false);

        wakeText =  settingView.findViewById(R.id.textView1);
        sleepText = settingView.findViewById(R.id.textView2);
        btn_wake = settingView.findViewById(R.id.btn_waketime);
        btn_sleep = settingView.findViewById(R.id.btn_sleeptime);
        btn_onoff = settingView.findViewById(R.id.btn_alarm);

        Time[0] = Calendar.getInstance(); // Initialize with current time
        Time[1] = Calendar.getInstance(); // Initialize with current time

//        int alarmHour = 0, alarmMinute = 0;

        // Initialize UI with default sleep and wake times
//        updateTimeText_sleep(sleepTime);
//        updateTimeText_wake(wakeTime);


        btn_wake.setOnClickListener(new View.OnClickListener(){//일어날 시간 설정
            @Override
            public void onClick(View v){
                TimePickerDialog timePickerDialog = new TimePickerDialog(v.getContext(),android.R.style.Theme_Holo_Light_Dialog,new TimePickerDialog.OnTimeSetListener(){
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute){
                        Time[0] = Calendar.getInstance();
                        Time[0].set(Calendar.HOUR_OF_DAY, hourOfDay);
                        Time[0].set(Calendar.MINUTE, minute);
                        Time[0].set(Calendar.SECOND, 0);

                        // Set wake time and update UI
                        wakeTime = Time[0];
                        updateTimeText_wake(wakeTime);
                        timesSet = true;

                        // Start alarm if btn_onoff is checked
                        if (btn_onoff.isChecked() && timesSet) {
                            startAlarm();
                        }
                    }
                },Time[0].get(Calendar.HOUR_OF_DAY), Time[0].get(Calendar.MINUTE), false);
                timePickerDialog.show();
            }
        });

        btn_sleep.setOnClickListener(new View.OnClickListener() {//잠드는 시간 설정
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(v.getContext(),android.R.style.Theme_Holo_Light_Dialog,new TimePickerDialog.OnTimeSetListener(){
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute){
                        Time[1] = Calendar.getInstance();
                        Time[1].set(Calendar.HOUR_OF_DAY, hourOfDay);
                        Time[1].set(Calendar.MINUTE, minute);
                        Time[1].set(Calendar.SECOND, 0);

                        // Set sleep time and update UI
                        sleepTime = Time[1];
                        updateTimeText_sleep(sleepTime);
                        timesSet = true;

                        // Start alarm if btn_onoff is checked
                        if (btn_onoff.isChecked() && timesSet) {
                            startAlarm();
                        }
                    }
                },Time[1].get(Calendar.HOUR_OF_DAY), Time[1].get(Calendar.MINUTE), false);
                timePickerDialog.show();
            }
        });


        btn_onoff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){ //알람 on
//                    if(Time[0].getTimeInMillis()>=System.currentTimeMillis())
//                        startAlarm(Time[0]);
                    // Start alarm when the switch is turned on
                    if (timesSet) {
                        startAlarm();
                    }
                }
                else{ //알람 off
                    cancelAlarm();
                }
            }
        });

        return settingView;
    }

    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(requireContext(), AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), 1, intent, PendingIntent.FLAG_IMMUTABLE);

        if (pendingIntent != null && alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
    public class AlertReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            NotificationHelper notificationHelper = new NotificationHelper(context);
            NotificationCompat.Builder nb = notificationHelper.getChannelNotification();
            notificationHelper.getManager().notify(1,nb.build());
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);

        updateTimeText_wake(c);
        startAlarm();
    }

    private void updateTimeText_wake(Calendar c){//일어날 시간 텍스트 반영
        String timeText = "일어날 시간   ";
        timeText += DateFormat.getTimeInstance().format(c.getTime());
        wakeText.setText(timeText);
    }
    private void updateTimeText_sleep(Calendar c){//잠드는 시간 텍스트 반영
        String timeText = "잠드는 시간   ";
        timeText += DateFormat.getTimeInstance().format(c.getTime());
        sleepText.setText(timeText);
    }

    private void startAlarm() {//알람 기능

        cancelAlarm(); // Cancel any existing alarms

//        long initialAlarmTime = sleepTime.getTimeInMillis();
//        long currentTime = System.currentTimeMillis();
//
//        if (initialAlarmTime <= currentTime) {
//            initialAlarmTime += ALARM_INTERVAL; // Start alarm immediately if sleep time has already passed
//        }

        String Text = "수유 시간 알림이 설정되었습니다.";
        Toast.makeText(getActivity(), Text, Toast.LENGTH_SHORT).show();

        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(requireContext(), AlertReceiver.class);
        PendingIntent pendingIntent = getBroadcast(requireContext(), 1, intent, PendingIntent.FLAG_IMMUTABLE);

        if (pendingIntent != null && alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }

        long initialAlarmTime = Time[0].getTimeInMillis();
        long currentTime = System.currentTimeMillis();

        if (initialAlarmTime <= currentTime) {
            initialAlarmTime += AlarmManager.INTERVAL_DAY;
        }

        long alarmInterval = ALARM_INTERVAL;
        // long alarmInterval = 3 * AlarmManager.INTERVAL_HOUR;

        while (initialAlarmTime + alarmInterval <= Time[1].getTimeInMillis()) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, initialAlarmTime, pendingIntent);
            initialAlarmTime += alarmInterval;
        }
    }

//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, initialAlarmTime, pendingIntent);
//
//        while (initialAlarmTime + alarmInterval <= Time[1].getTimeInMillis()) {
//            initialAlarmTime += alarmInterval;
//            alarmManager.setExact(AlarmManager.RTC_WAKEUP, initialAlarmTime, pendingIntent);
//        }
//
//        while (initialAlarmTime <= wakeTime.getTimeInMillis()) {
//            alarmManager.setExact(AlarmManager.RTC_WAKEUP, initialAlarmTime, pendingIntent);
//            initialAlarmTime += ALARM_INTERVAL;
//        }


}