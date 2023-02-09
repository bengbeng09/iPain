package com.sklerbidi.therapistmobileapp.Patient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sklerbidi.therapistmobileapp.ActivityNavigation;
import com.sklerbidi.therapistmobileapp.R;

public class PTherapistSessionActivity extends AppCompatActivity {

    Button back;
    String user_code, name, clinic;
    TextView tv_name, tv_clinic;
    LinearLayout container_activities;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://student-theses-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_therapist_session);

        Bundle extras = getIntent().getExtras();

        findView();

        if (extras != null) {
            name = extras.getString("name");
            user_code = extras.getString("code");
            clinic = extras.getString("clinic");
            tv_name.setText(name);
            tv_clinic.setText(clinic.toUpperCase());
        }

        set_card();

        back.setOnClickListener(v -> {
            finish();
        });
    }

    private void set_card(){
        container_activities.removeAllViews();
        DatabaseReference activity = databaseReference.child("users").child(ActivityNavigation.user_code).child("therapists").child(user_code).child("activities");
        activity.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String activity_name = ds.getKey();

                    if(activity_name != null){
                        activity.child(activity_name).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                add_card(activity_name);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Handle error here
                            }
                        });
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void add_card(String name){
        View view = getLayoutInflater().inflate(R.layout.layout_card_big, null);

        TextView nameView = view.findViewById(R.id.activity_name);
        CardView button = view.findViewById(R.id.btn_card);
        String cardDetails = name.toUpperCase();
        nameView.setText(cardDetails);

        button.setOnClickListener(v -> {
            Intent intent = new Intent(PTherapistSessionActivity.this, PTasksActivity.class);
            startActivity(intent);
        });

        container_activities.addView(view);
    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }

    private void findView(){
        back = findViewById(R.id.btn_back);
        container_activities = findViewById(R.id.container_activity);
        tv_name = findViewById(R.id.tv_therapist_name);
        tv_clinic = findViewById(R.id.tv_therapist_clinic);
    }
}