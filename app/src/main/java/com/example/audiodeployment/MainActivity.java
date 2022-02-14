package com.example.audiodeployment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.audiodeployment.Audio.MaleModel;
import com.example.audiodeployment.helpers.MaleModelActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClassifyVoiceRegister(View view){
        // go to classify voice register
        Intent intent = new Intent (this, MaleModel.class);
        startActivity(intent);
    }
}