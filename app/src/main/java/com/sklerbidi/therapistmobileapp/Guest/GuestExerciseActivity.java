
/*
I displays a list of exercises for the selected parts the body. Each exercise is shown as a
CardView with information about it. It sets up the CardViews to respond to clicks by launching a
new screen with more information about the exercise.
 */

package com.sklerbidi.therapistmobileapp.Guest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.sklerbidi.therapistmobileapp.Dialog.DialogAdvice;
import com.sklerbidi.therapistmobileapp.R;

public class GuestExerciseActivity extends AppCompatActivity {
    String type;
    Button btn_back;
    LinearLayout container_foot, container_knee, container_upper_back, container_lower_back;
    CardView card_shoulder, card_low, card_upper, card_scalene, card_partial, card_lumbar, card_dead, card_bird, card_superman,
             card_wall, card_heel, card_clam, card_straight, card_terminal, card_ankle, card_heel2, card_single, card_gentle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.g_exercise);

        findViewById();

        Bundle extras = getIntent().getExtras();

        if(!GuestActivity.hasSeenAdvice){
            GuestActivity.hasSeenAdvice = true;
            DialogAdvice dialogAdvice = new DialogAdvice();
            dialogAdvice.setCancelable(false);
            dialogAdvice.show(getSupportFragmentManager(), "advice");
        }

        if (extras != null) {
            type = extras.getString("type");
        }

        if(type != null){
            switch (type){
                case "foot":
                    container_foot.setVisibility(View.VISIBLE);
                    break;
                case "knee":
                    container_knee.setVisibility(View.VISIBLE);
                    break;
                case "upper back":
                    container_upper_back.setVisibility(View.VISIBLE);
                    break;
                case "lower back":
                    container_lower_back.setVisibility(View.VISIBLE);
                    break;
            }
        }

        CardView[] cards = {card_shoulder, card_low, card_upper, card_scalene, card_partial, card_lumbar, card_dead, card_bird, card_superman, card_wall, card_heel, card_clam, card_straight, card_terminal, card_ankle, card_heel2, card_single, card_gentle};
        int[] exerciseStrings = {R.string.shoulder_shrugs, R.string.low_row, R.string.upper_cut, R.string.scalene_stretch, R.string.partial_sit_up, R.string.lumbar_rocks, R.string.dead_boos, R.string.bird_dog, R.string.superman, R.string.wall_squat, R.string.heel_raises, R.string.clam_shell, R.string.straight_leg, R.string.terminal_knee, R.string.ankle, R.string.heel_raises, R.string.single_leg, R.string.gentle_hopping};
        int[] howToStrings = {R.string.shoulder_shrugs_how, R.string.low_row_how, R.string.upper_cut_how, R.string.scalene_stretch_how, R.string.partial_sit_up_how, R.string.lumbar_rocks_how, R.string.dead_boos_how, R.string.bird_dog_how, R.string.superman_how, R.string.wall_squat_how, R.string.heel_raises_how, R.string.clam_shell_how, R.string.straight_leg_how, R.string.terminal_knee_how, R.string.ankle_how, R.string.heel_raises_how, R.string.single_leg_how, R.string.gentle_hopping_how};
        int[] sessionStrings = {R.string.shoulder_shrugs_session, R.string.low_row_session, R.string.upper_cut_session, R.string.scalene_stretch_session, R.string.partial_sit_up_session, R.string.lumbar_rocks_session, R.string.dead_boos_session, R.string.bird_dog_session, R.string.superman_session, R.string.wall_squat_session, R.string.heel_raises_session, R.string.clam_shell_session, R.string.straight_leg_session, R.string.terminal_knee_session, R.string.ankle_session, R.string.heel_raises_session, R.string.single_leg_session, R.string.gentle_hopping_session};

        for (int i = 0; i < cards.length; i++) {
            setButton(cards[i], getResources().getString(exerciseStrings[i]),
                    getResources().getString(howToStrings[i]),
                    getResources().getString(sessionStrings[i]));
        }

        btn_back.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
    }



    private void setButton(CardView card, String exercise, String steps, String repetition){
        card.setOnClickListener(v -> {
            Intent intent = new Intent(this, GuestExerciseSession.class);
            intent.putExtra("exercise", exercise);
            intent.putExtra("how", steps);
            intent.putExtra("repetition", repetition);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void findViewById() {
        container_foot = findViewById(R.id.container_foot);
        container_knee = findViewById(R.id.container_knee);
        container_upper_back = findViewById(R.id.container_upper_back);
        container_lower_back = findViewById(R.id.container_lower_back);

        card_shoulder = findViewById(R.id.card_shoulder_rugs);
        card_low = findViewById(R.id.card_low_row);
        card_upper = findViewById(R.id.card_uppercut);
        card_scalene = findViewById(R.id.card_scalene);

        card_partial = findViewById(R.id.card_partial);
        card_lumbar = findViewById(R.id.card_lumbar);
        card_dead = findViewById(R.id.card_dead);
        card_bird = findViewById(R.id.card_bird);
        card_superman = findViewById(R.id.card_superman);

        card_wall = findViewById(R.id.card_wall);
        card_heel = findViewById(R.id.card_heel);
        card_clam = findViewById(R.id.card_clam);
        card_straight = findViewById(R.id.card_straight);
        card_terminal = findViewById(R.id.card_terminal);

        card_ankle = findViewById(R.id.card_ankle);
        card_heel2 = findViewById(R.id.card_heel2);
        card_single = findViewById(R.id.card_single);
        card_gentle = findViewById(R.id.card_gentle);

        btn_back = findViewById(R.id.btn_back);
    }
}