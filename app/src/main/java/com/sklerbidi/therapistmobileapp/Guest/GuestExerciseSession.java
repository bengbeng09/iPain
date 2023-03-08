package com.sklerbidi.therapistmobileapp.Guest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.sklerbidi.therapistmobileapp.Dialog.DialogAbout;
import com.sklerbidi.therapistmobileapp.Dialog.DialogAdvice;
import com.sklerbidi.therapistmobileapp.R;

public class GuestExerciseSession extends AppCompatActivity {

    TextView tv_exercise, tv_steps, tv_repetition;
    Button btn_done, btn_back;
    String exercise, steps, repetition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.g_exercise_session);

        findViewById();

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            exercise = extras.getString("exercise");
            steps = extras.getString("how");
            repetition = extras.getString("repetition");
        }

        tv_exercise.setText(exercise.toUpperCase());
        tv_steps.setText(steps);
        tv_repetition.setText(repetition);

        btn_done.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        DialogAdvice dialogAdvice = new DialogAdvice();
                        dialogAdvice.setCancelable(false);
                        dialogAdvice.show(getSupportFragmentManager(), "advice");
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

        btn_back.setOnClickListener(v -> finish());

    }

    private void findViewById() {
        tv_exercise = findViewById(R.id.tv_exercise);
        tv_steps = findViewById(R.id.tv_exercise_how);
        tv_repetition = findViewById(R.id.tv_exercise_session);
        btn_done = findViewById(R.id.btn_done);
        btn_back = findViewById(R.id.btn_back);
    }


}