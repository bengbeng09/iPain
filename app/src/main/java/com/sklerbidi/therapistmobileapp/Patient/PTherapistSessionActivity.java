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
                    String repetition = ds.child("repetition").getValue(String.class);
                    String hold = ds.child("hold").getValue(String.class);
                    String complete = ds.child("complete").getValue(String.class);
                    String link = ds.child("link").getValue(String.class);
                    String note = ds.child("note").getValue(String.class);
                    String status = ds.child("status").getValue(String.class);

                    if(activity_name != null && status != null){
                        activity.child(activity_name).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(status.equalsIgnoreCase("incomplete")){
                                    add_card(activity_name, repetition, hold, complete, link, note, status);
                                }

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

    private void add_card(String name, String repetition, String hold, String complete, String link, String note, String status){
        View view = getLayoutInflater().inflate(R.layout.layout_card_big, null);

        TextView nameView = view.findViewById(R.id.activity_name);
        CardView button = view.findViewById(R.id.btn_card);
        String cardDetails = name.toUpperCase();
        nameView.setText(cardDetails);

        button.setOnClickListener(v -> {
            Intent intent = new Intent(PTherapistSessionActivity.this, PTasksActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("repetition", repetition);
            intent.putExtra("hold", hold);
            intent.putExtra("complete", complete);
            intent.putExtra("link", link);
            intent.putExtra("note", note);
            intent.putExtra("status", status);
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