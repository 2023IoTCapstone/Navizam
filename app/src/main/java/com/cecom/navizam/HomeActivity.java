package com.cecom.navizam;

import static com.cecom.navizam.R.id.navigation_home;
import static com.cecom.navizam.R.id.testTV;

import androidx.appcompat.app.AlertDialog;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cecom.navizam.fragments.fragment_alarm;
import com.cecom.navizam.fragments.fragment_cam;
import com.cecom.navizam.fragments.fragment_chart;
import com.cecom.navizam.fragments.fragment_settings;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    public String output = "";

    private BottomNavigationView bottomNavigationView;

    static final int REQUEST_ENABLE_BT = 10;
    int mPairedDeviceCount = 0;
    Set<BluetoothDevice> mDevices;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mRemoteDevice;
    BluetoothSocket mSocket;

    OutputStream mOutputStream;
    InputStream mInputStream;

  //  TextView tvResult;

    TextView tvFirst;
    TextView tvSecond;
    TextView tvThird;
    TextView tvForth;
    TextView testTV;
    ImageView imageView0, imageView2, imageView4;




    //검색된 블루투스 기기로부터 기기명을 받아옴
    //Get device name from searched  bluetooth devices
    BluetoothDevice getDeviceFromBondedList(String name) {
        BluetoothDevice selectedDevice = null;
        for (BluetoothDevice deivce : mDevices) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            if (name.equals(deivce.getName())) {
                selectedDevice = deivce;
                break;
            }
        }
        return selectedDevice;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        checkBluetooth();

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set a listener for Bottom Navigation Bar item clicks
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int itemId = item.getItemId();
            if (itemId == navigation_home) {
                selectedFragment = new fragment_cam();
            } else if (itemId == R.id.navigation_sleep) {
                selectedFragment = new fragment_chart();
            } else if (itemId == R.id.navigation_alarm) {
                selectedFragment = new fragment_alarm();
            } else if (itemId == R.id.navigation_settings) {
                selectedFragment = new fragment_settings();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, selectedFragment)
                        .commit();
            }

            return true;
        });

        // Initialize the default fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, new fragment_cam())
                .commit();


        Button btnRead = findViewById(R.id.btn_home_read);
        btnRead.setOnClickListener(view -> getData());

      //  tvResult = findViewById(R.id.tv_home_result);

        tvFirst = findViewById(R.id.tvFirst);
        tvSecond = findViewById(R.id.tvSecond);
        tvThird = findViewById(R.id.tvThird);
        tvForth = findViewById(R.id.tvForth);
        testTV = findViewById(R.id.testTV);
        imageView0 = (ImageView) findViewById(R.id.img0);
        imageView2 = (ImageView) findViewById(R.id.img2);
        imageView4 = (ImageView) findViewById(R.id.img4);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                selectDevice();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Bluetooth Unavailable", Toast.LENGTH_LONG).show();
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mInputStream.close();
            mOutputStream.close();
            mSocket.close();
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }
        finish();
    }

    void updateTextView(String str){



//         tvResult.setText(str);

        // Split the received string by ","
        String[] splitData = str.split(",");

        if (splitData.length >= 4) {
            try {

                if (splitData[3] == "D" || splitData[3] == "R" ) {//수면상태
                    imageView4.setImageResource(R.drawable.sleepbl);
                    tvForth.setText("수면중");
                }else{
                    imageView4.setImageResource(R.drawable.happybb);
                    tvForth.setText("활동중");
                }

                if (splitData[1] == "S" ) {//질식 위험
                    imageView2.setImageResource(R.drawable.ic_navi);
                    tvSecond.setText("안정");
                }else{
                    imageView2.setImageResource(R.drawable.pye);
                    tvSecond.setText("질식 위험");
                }

                if (splitData[4] == "C"  ) {//보챔
                    imageView0.setImageResource(R.drawable.crybb);
                    testTV.setText("보챔");
                }else{
                    imageView0.setImageResource(R.drawable.happybb);
                    testTV.setText("안정");
                }
                if(Float.parseFloat(splitData[0])>=80){
                tvFirst.setText(splitData[0]);
                }
                tvThird.setText(splitData[2]);
            }


            catch(Exception e){
                Log.e("TEST_ERR", e.toString());
            }

            // Set the first three substrings in separate TextView widgets

        } else {

        }
    }

    void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Reader reader = new InputStreamReader(mInputStream);

                    StringBuilder result = new StringBuilder();

                    for (int data = reader.read(); data != -1; data = reader.read()) {
                        if(data == '!'){
                                output = result.toString().trim();
                            result.setLength(0);

                            Log.d("BLUETOOTH", output);

                            // Pass the data to Fragment for real-time updates
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateTextView(output);
//                                    fragment_alarm alarmFragment = (fragment_alarm) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
//                                    if (alarmFragment != null) {
//                                        alarmFragment.updateAlarms(output);
//                                    }
//                                    fragment_chart chartFragment = (fragment_chart) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
//                                    if (chartFragment != null) {
//                                        chartFragment.updateCharts(output);
//                                    }

                                }
                            });

                        }else{
                            result.append((char)data);
                        }
                    }
                } catch (Exception e) {
                    // Handle Bluetooth or UI update errors
                    Log.e("ERR", e.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Bluetooth Error", Toast.LENGTH_LONG).show();
                        }
                    });
                    //Log.e("ERR", e.toString());
                    //Toast.makeText(getApplicationContext(), "Bluetooth Error", Toast.LENGTH_LONG).show();
                    //finish();
                }
            }
        }).start();
    }

    void checkBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth is Unsupported", Toast.LENGTH_LONG).show();
            finish();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                Toast.makeText(getApplicationContext(), "Bluetooth is Disabled", Toast.LENGTH_LONG).show();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else
                selectDevice();
        }
    }

    //기기에 페어링 되어 있는 블루투스 기기 목록 표시 및 기기 선택
    void selectDevice() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mDevices = mBluetoothAdapter.getBondedDevices();
        mPairedDeviceCount = mDevices.size();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        List<String> listItems = new ArrayList<String>();
        for (BluetoothDevice device : mDevices) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            listItems.add(device.getName());
        }
        listItems.add("No Device");
        listItems.add("Cancel");
        final CharSequence[] items = listItems.toArray(new CharSequence[0]);
        listItems.toArray(new CharSequence[0]);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (item == mPairedDeviceCount + 1) {
                    Toast.makeText(getApplicationContext(), "Canceled", Toast.LENGTH_LONG).show();
                    finish();
                } else if (item == mPairedDeviceCount) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                    builder.setMessage("Need to pair");
                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS));
                        }
                    });
                    builder.show();
                } else {
                    connectToSelectedDevice(items[item].toString());
                }
            }
        });
        builder.setCancelable(false);
        AlertDialog alert = builder.create();
        alert.show();
    }

    void connectToSelectedDevice(String selectedDeviceName) {
        mRemoteDevice = getDeviceFromBondedList(selectedDeviceName);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        Log.d("Bluetooth Connect", mRemoteDevice.getName());
        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                throw new Exception("err");
            }
            mSocket = mRemoteDevice.createRfcommSocketToServiceRecord(uuid);
            mSocket.connect();
            mInputStream = mSocket.getInputStream();
        }catch(Exception e) {
            Toast.makeText(getApplicationContext(), "Connect Fail", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public String getOutput(){
        return output;
    }
}