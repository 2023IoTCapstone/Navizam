package com.cecom.navizam.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.cecom.navizam.R;

public class fragment_cam extends Fragment {

    ImageButton mbtn_url1;
    ImageButton mbtn_url2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        //super.onCreate(savedInstanceState);

        View viewcam = inflater.inflate(R.layout.fragment_cam, container, false);
        mbtn_url1 = (ImageButton)viewcam.findViewById(R.id.bed); //imageButton 사용하기 위해 변수와 연결
        mbtn_url2 = (ImageButton)viewcam.findViewById(R.id.liv);

        mbtn_url1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){ //imageButton1 클릭했을때
                Intent urlintent1 = new Intent(Intent.ACTION_VIEW, Uri.parse("http://192.168.58.215"));//거실 캠 링크 연결
                startActivity(urlintent1);
            }
        });

        mbtn_url2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){//imageButton2 클릭했을 때
                Intent urlintent2 = new Intent(Intent.ACTION_VIEW, Uri.parse("http://192.168.252.212"));//방 캠 링크 연결
                startActivity(urlintent2);
            }
        });

        return viewcam;
    }
}