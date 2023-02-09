package com.sklerbidi.therapistmobileapp.Patient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.sklerbidi.therapistmobileapp.R;

public class PTasksActivity extends AppCompatActivity {

    Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_task);

        findView();

        back.setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }

    private void findView(){
        back = findViewById(R.id.btn_back);
    }
}