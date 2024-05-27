package com.cecom.navizam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnEnter = findViewById(R.id.btn_main_enter);
        btnEnter.setOnClickListener(view ->
                startActivity(new Intent(getApplicationContext(), HomeActivity.class)));
    }
}