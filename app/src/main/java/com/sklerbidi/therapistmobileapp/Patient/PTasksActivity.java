package com.sklerbidi.therapistmobileapp.Patient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.sklerbidi.therapistmobileapp.R;

public class PTasksActivity extends AppCompatActivity {

    Button back, finish;
    TextView tv_name, tv_repeat, tv_hold, tv_complete, tv_link, tv_notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_task);

        findView();

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            tv_name.setText(extras.getString("name").toUpperCase());
            tv_repeat.setText(extras.getString("repetition"));
            tv_hold.setText(extras.getString("hold"));
            tv_complete.setText(extras.getString("complete"));
            tv_link.setText(extras.getString("link"));
            tv_notes.setText(extras.getString("note"));
        }

        back.setOnClickListener(v -> {
            finish();
        });

        finish.setOnClickListener(v ->{

        });
    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }

    private void findView(){
        back = findViewById(R.id.btn_back);
        tv_name = findViewById(R.id.tv_activity_name);
        tv_repeat = findViewById(R.id.tv_repeat);
        tv_hold = findViewById(R.id.tv_hold);
        tv_complete = findViewById(R.id.tv_complete);
        tv_link = findViewById(R.id.tv_link);
        tv_notes = findViewById(R.id.tv_note);
        finish = findViewById(R.id.btn_finish);
    }
}