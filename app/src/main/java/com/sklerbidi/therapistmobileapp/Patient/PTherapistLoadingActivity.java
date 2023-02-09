package com.sklerbidi.therapistmobileapp.Patient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sklerbidi.therapistmobileapp.R;

public class PTherapistLoadingActivity extends AppCompatActivity {
    String name, code, clinic;
    TextView tv_name;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://student-theses-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_therapist_loading);

        Bundle extras = getIntent().getExtras();

        findView();

        if (extras != null) {
            name = extras.getString("name");
            code = extras.getString("code");
            clinic = extras.getString("clinic");
            tv_name.setText(name);
        }



        new Handler().postDelayed(() -> {
            Intent intent = new Intent(PTherapistLoadingActivity.this, PTherapistSessionActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("code", code);
            intent.putExtra("clinic", clinic);
            startActivity(intent);
            finish();
        }, 2000);
    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }

    private void findView(){
        tv_name = findViewById(R.id.tv_loading_therapist_name);
    }

}