package com.sklerbidi.therapistmobileapp.Patient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    TextView tv_name, tv_clinic, tv_completed, tv_pending;
    LinearLayout container_incomplete, container_completed;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://student-theses-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
    private static final int REQUEST_CODE_SECOND_ACTIVITY = 1;

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

        back.setOnClickListener(v -> finish());
    }

    private void set_card(){
        tv_completed.setVisibility(View.GONE);
        tv_pending.setVisibility(View.GONE);
        container_incomplete.removeAllViews();
        container_completed.removeAllViews();
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
                                add_card(activity_name, repetition, hold, complete, link, note, status, user_code);
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

    private void add_card(String name, String repetition, String hold, String complete, String link, String note, String status, String therapist_code){
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.layout_card_big, null);

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
            intent.putExtra("therapist", therapist_code);
            startActivityForResult(intent, REQUEST_CODE_SECOND_ACTIVITY);
        });

        if(status.equalsIgnoreCase("incomplete")){
            if(tv_pending.getVisibility() == View.GONE){
                tv_pending.setVisibility(View.VISIBLE);
            }
            container_incomplete.addView(view);
        }else{
            if(tv_completed.getVisibility() == View.GONE){
                tv_completed.setVisibility(View.VISIBLE);
            }
            int newColor = getResources().getColor(R.color.teal_200);
            ColorStateList colorStateList = ColorStateList.valueOf(newColor);
            button.setBackgroundTintList(colorStateList);
            container_completed.addView(view);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SECOND_ACTIVITY && resultCode == RESULT_OK) {
            // Call the desired method here
            set_card();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void findView(){
        back = findViewById(R.id.btn_back);
        container_incomplete = findViewById(R.id.container_incomplete);
        tv_name = findViewById(R.id.tv_therapist_name);
        tv_clinic = findViewById(R.id.tv_therapist_clinic);
        container_completed = findViewById(R.id.container_completed);
        tv_completed = findViewById(R.id.tv_completed);
        tv_pending = findViewById(R.id.tv_pending);
    }
}