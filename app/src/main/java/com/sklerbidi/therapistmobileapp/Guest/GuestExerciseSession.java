
/*
This class is used to display information about a selected exercise, including an image of the exercise.
It receives data from the previous screen and sets up the layout accordingly.
 */

package com.sklerbidi.therapistmobileapp.Guest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sklerbidi.therapistmobileapp.R;

import java.util.HashMap;
import java.util.Map;

public class GuestExerciseSession extends AppCompatActivity {

    TextView tv_exercise, tv_steps, tv_repetition;
    Button btn_done, btn_back;
    ImageView img_exercise;
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

        Map<String, Integer> exerciseDrawableMap = new HashMap<>();
        exerciseDrawableMap.put("shoulder shrugs", R.drawable.shoulder_shrug);
        exerciseDrawableMap.put("low row", R.drawable.low_row);
        exerciseDrawableMap.put("upper cut", R.drawable.uppercut);
        exerciseDrawableMap.put("scalene stretch", R.drawable.scalene_stretch);
        exerciseDrawableMap.put("partial sit up", R.drawable.partial_sit_up);
        exerciseDrawableMap.put("lumbar rocks", R.drawable.lumbar_rocks);
        exerciseDrawableMap.put("dead bugs", R.drawable.dead_bugs);
        exerciseDrawableMap.put("bird dog", R.drawable.bird_dog);
        exerciseDrawableMap.put("superman", R.drawable.superman);
        exerciseDrawableMap.put("wall squat", R.drawable.wall_squat);
        exerciseDrawableMap.put("heel raises", R.drawable.heel_raises);
        exerciseDrawableMap.put("clam shell", R.drawable.clam_shell);
        exerciseDrawableMap.put("straight leg raises hip", R.drawable.straight_leg_raises);
        exerciseDrawableMap.put("terminal knee extension", R.drawable.terminal_knee);
        exerciseDrawableMap.put("ankle pf tb", R.drawable.ankle_pf_tb);
        exerciseDrawableMap.put("single leg balance", R.drawable.single_leg);
        exerciseDrawableMap.put("gentle hopping", R.drawable.gentle_hopping);

// Look up the drawable resource ID using the exercise name as the key
        Integer drawableId = exerciseDrawableMap.get(exercise.toLowerCase());
        if (drawableId != null) {
            img_exercise.setImageResource(drawableId);
        }

        btn_done.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        finish();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

        btn_back.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


    private void findViewById() {
        tv_exercise = findViewById(R.id.tv_exercise);
        tv_steps = findViewById(R.id.tv_exercise_how);
        tv_repetition = findViewById(R.id.tv_exercise_session);
        btn_done = findViewById(R.id.btn_done);
        btn_back = findViewById(R.id.btn_back);
        img_exercise = findViewById(R.id.exercise_image);
    }


}