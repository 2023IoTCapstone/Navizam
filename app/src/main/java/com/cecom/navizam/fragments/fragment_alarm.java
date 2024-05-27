package com.cecom.navizam.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.cecom.navizam.R;

public class fragment_alarm extends Fragment {

    private Switch switch1, switch2, switch3;
    private Button turnButton;

//    public static AlarmFragment newInstance(String[] splitData) {
//        AlarmFragment fragment = new AlarmFragment();
//        Bundle args = new Bundle();
//        args.putString("output", splitData);
//        fragment.setArguments(args);
//        return fragment;
//    }

    public void updateAlarms(String data) {
        suffo_alarm(data);
        cry_alarm(data);
    }

    // suffocation alarm 질식 위험 알림
    void suffo_alarm(String data) {

        String[] splitData = data.split(",");
        if (splitData.length >= 4 && splitData[2].equals("DANGER")) {
            Toast.makeText(getActivity(), "BABY IS IN DANGER!", Toast.LENGTH_SHORT).show();
        }
    }

    // 수면 중 보챔 알림
    void cry_alarm(String data) {
        String[] splitData = data.split(",");
        if (splitData.length >= 4 && splitData[4].equals("C")) {
            Toast.makeText(getActivity(), "BABY IS CRYING!", Toast.LENGTH_SHORT).show();
        }

    }

//    // turn over alarm 뒤집기 알림
//    void turn_alarm(String data) {
//        String[] splitData = data.split(",");
//        if (splitData.length >= 4 && splitData[3].equals("B")) {
//            Toast.makeText(getActivity(), "BABY TURNED OVER!", Toast.LENGTH_SHORT).show();
//        }
//
//    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        switch1 = view.findViewById(R.id.switch1);
        switch2 = view.findViewById(R.id.switch2);
        switch3 = view.findViewById(R.id.switch3);
        turnButton = view.findViewById(R.id.button);

        // 전체 알림 On/Off
        turnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (turnButton.getText().equals("Turn On")) {
                    switch1.setChecked(true);
                    switch2.setChecked(true);
                    switch3.setChecked(true);
                    turnButton.setText("Turn Off");
                    // turnButton.setBackground();
                } else {
                    switch1.setChecked(false);
                    switch2.setChecked(false);
                    switch3.setChecked(false);
                    turnButton.setText("Turn On");
                    // turnButton.setBackground();
                }

            }
        });

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setalarm, container, false);
    }
}
