package com.sklerbidi.therapistmobileapp.Guest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.sklerbidi.therapistmobileapp.LoginRegister.LoginActivity;
import com.sklerbidi.therapistmobileapp.R;

import java.util.List;

public class GuestAdvice extends AppCompatActivity {

    Button btn_continue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.g_advice);
        btn_continue = findViewById(R.id.btn_continue);

        btn_continue.setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    public void onBackPressed() {

    }
}