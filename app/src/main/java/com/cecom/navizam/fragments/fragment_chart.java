package com.cecom.navizam.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cecom.navizam.HomeActivity;
import com.cecom.navizam.R;

import android.graphics.Color;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class fragment_chart extends Fragment {

    PieChart pieChart;
    TextView totaltext;
    TextView deeptext;
    TextView remtext;
    Button timer;
    Button reset;

    final static int Init = 0;
    final static int Run = 1;
    final static int Pause = 2;
    int cur_Status = Init;
    long myBaseTime;
    long myPauseTime;
    long myPrevDTime =  -1;
    long myPrevRTime = -1;
    long myPrevWTime = -1;
    long sumRTime = -1;
    long sumDTime = -1;
    long sumWTime = -1;
    boolean isStarted = false;
    String prevSleepType = null;

    int[] colorArray= new int[]{Color.BLACK, Color.GRAY};

    String output;

    public void updateCharts(String data){
        output = sleep_quality(data);
        updatePieChart();
    }

    String sleep_quality(String data) {
        String[] splitData = data.split(",");
        return splitData[3];
    }


    @SuppressLint("CutPasteId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_chart, container, false);

        pieChart = (PieChart) view.findViewById(R.id.picChart); //원형그래프
        PieDataSet pieDataSet = new PieDataSet(datavalue, "수면질 차트(%)");
        pieDataSet.setColors(colorArray);
        PieData pieData = new PieData(pieDataSet);
        pieChart.setDrawEntryLabels(true);
        pieChart.setData(pieData);
        pieChart.invalidate();

        totaltext = (TextView) view.findViewById(R.id.time);//총 수면 시간
        deeptext = (TextView) view.findViewById(R.id.textView1);//DEEP 시간
        remtext = (TextView) view.findViewById(R.id.textView2);//REM 시간


        reset = (Button) view.findViewById(R.id.button3); //리셋 버튼

        reset.setOnClickListener(new View.OnClickListener() {//리셋 버튼 눌렀을 때
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (cur_Status == Run || cur_Status == Pause) {
                    myTimer.removeMessages(0);
                    totaltext.setText("총 수면 시간  00:00:00");
                    deeptext.setText("- DEEP  00:00:00");
                    remtext.setText("- REM  00:00:00");
                    cur_Status = Init;
                }
            }
        });

        timer = (Button) view.findViewById(R.id.button2); //타이머 측정 버튼

        timer.setOnClickListener(new View.OnClickListener() { //수면 시간 측정 버튼 눌렀을 때
            @Override
            public void onClick(View v) {
                try {
                    handleTimerClick();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void handleTimerClick() {
        if (cur_Status == Init) { // 초기화 상태일 때
            myBaseTime = SystemClock.elapsedRealtime();
            //System.out.println(myBaseTime);
            myTimer.sendEmptyMessage(0);
            timer.setText("수면 중");
            cur_Status = Run;
        }

        else if (cur_Status == Run) { // 측정 중일 때
            myTimer.removeMessages(0);
            myPauseTime = SystemClock.elapsedRealtime();
            timer.setText("수면 시작");
            cur_Status = Pause;
            updateCharts(((HomeActivity)this.getActivity()).output);
        }

        else {//측정 중 잠깐 멈췄을 때

            long now = SystemClock.elapsedRealtime();
            myTimer.sendEmptyMessage(0);
            myBaseTime += (now - myPauseTime);
            timer.setText("수면 시작");
            cur_Status = Run;
            if( prevSleepType == "D"){

            }


        }

    }

    Handler myTimer = new Handler(){
        @SuppressLint("HandlerLeak")
        public void handleMessage(Message msg){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        updateUI();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

          // updateCharts(output);

            if(cur_Status==Run){
                myTimer.sendEmptyMessage(0);
            }
           // myTimer.sendEmptyMessage(0);
        }
    };

    private void updateUI() {
        totaltext.setText(getTimeOut());

        if(!isStarted){
            myPrevDTime = myBaseTime;
            myPrevRTime = myBaseTime;
            isStarted = true;

        }else if(sleep_quality(((HomeActivity)this.getActivity()).output).equals("D")){
            if(prevSleepType == null || !prevSleepType.equals("D")){
                sumRTime = sumRTime + myPrevRTime - myPrevDTime;
            }
            deeptext.setText(getTimeOut_deep());
            prevSleepType = "D";
            myPrevDTime = SystemClock.elapsedRealtime();
        }

        else if(sleep_quality(((HomeActivity)this.getActivity()).output).equals("R")){
            if(prevSleepType == null || !prevSleepType.equals("R")){
                sumDTime = sumDTime + myPrevDTime - myPrevRTime;

            }
            remtext.setText(getTimeOut_rem());
            prevSleepType = "R";
            myPrevRTime = SystemClock.elapsedRealtime();

            // deeptext.setText("- DEEP  00:00:00");
        }


    }


    long outTime, outTime_deep, outTime_rem;
    String getTimeOut(){
        long now = SystemClock.elapsedRealtime();
        long outTime = now - myBaseTime;
        String easy_outTime = String.format("총 수면 시간 %02d:%02d:%02d", outTime/3600000, (outTime%3600000)/60000,(outTime%60000)/1000);
        return easy_outTime;
    }
    ;
    String getTimeOut_deep(){
        long now = SystemClock.elapsedRealtime();
        long outTime_deep = sumDTime + now - myPrevRTime;
        String easy_outTime1 = String.format("- DEEP %02d:%02d:%02d", outTime_deep / 3600000, (outTime_deep % 3600000) / 60000, (outTime_deep % 60000) / 1000);
        return easy_outTime1;
    }
    String getTimeOut_rem(){
        long now = SystemClock.elapsedRealtime();
        long outTime_rem = sumRTime + now - myPrevDTime;
        String easy_outTime2 = String.format("- REM %02d:%02d:%02d", outTime_rem / 3600000, (outTime_rem % 3600000) / 60000, (outTime_rem % 60000) / 1000);
        return easy_outTime2;
    }
    public long getOutTime(){
        long now = SystemClock.elapsedRealtime();
        return now - myBaseTime;
    }
    public long getOutTime_deep(){
        long now = SystemClock.elapsedRealtime();

        return now - myPrevRTime;
    }
    public long getOutTime_rem(){
        long now = SystemClock.elapsedRealtime();
        return now - myPrevDTime;
    }

    private ArrayList<PieEntry> datavalue = new ArrayList<>();
    private void updatePieChart(){
        long deep_per, rem_per,total_per;
        if (getOutTime() == 0) {
            deep_per = 0;
            rem_per = 0;
            total_per = 0;
        } else {
            total_per = getOutTime();
            deep_per = getOutTime_deep() ;
            rem_per = getOutTime_rem() ;
        }

        datavalue.clear();
     //   datavalue.add(new PieEntry(total_per - deep_per - rem_per, "WAKE"));
        datavalue.add(new PieEntry(rem_per, "REM"));
        datavalue.add(new PieEntry(deep_per, "DEEP"));

        PieDataSet pieDataSet = new PieDataSet(datavalue, "수면질 차트");
        pieDataSet.setColors(colorArray);
        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.notifyDataSetChanged();
        pieChart.invalidate();

    }
}