/*
 This class provides the functionality to add activities to a patient's session,
 display those activities as cards, and upload an image of the activity to the Firebase Storage.
 */

package com.sklerbidi.therapistmobileapp.Therapist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sklerbidi.therapistmobileapp.ActivityNavigation;
import com.sklerbidi.therapistmobileapp.CustomClass.Util;
import com.sklerbidi.therapistmobileapp.Dialog.PDialogAddActivity;
import com.sklerbidi.therapistmobileapp.Dialog.TDialogProof;
import com.sklerbidi.therapistmobileapp.LoginRegister.LoginActivity;
import com.sklerbidi.therapistmobileapp.R;

import java.io.ByteArrayOutputStream;


public class TPatientSessionActivity extends AppCompatActivity {

    FloatingActionButton add_activity;
    Button btn_back;
    LinearLayout container_completed, container_given;
    TextView tv_session_name, tv_session_id, tv_given, tv_completed;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://student-theses-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
    String name = "", user_code = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.t_patient_session);

        findView();

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            name = extras.getString("first_name") + " " + extras.getString("last_name") ;
            user_code = extras.getString("user_code");
        }

        tv_session_name.setText(name.toUpperCase());
        tv_session_id.setText(user_code);

        if(getApplicationContext() != null & !LoginActivity.isNetworkConnected(getApplicationContext())){
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }

        set_card();

        add_activity.setOnClickListener(v -> {
            PDialogAddActivity menuActivity = new PDialogAddActivity();
            Bundle bundle = new Bundle();
            bundle.putString("user_code", tv_session_id.getText().toString());
            menuActivity.setArguments(bundle);
            menuActivity.setCancelable(false);
            menuActivity.show(getSupportFragmentManager(), "add activity");
        });

        getSupportFragmentManager().setFragmentResultListener("request_activity", this, (requestKey, bundle) -> {
            String a_name = bundle.getString("a_name");
            String a_repetition = bundle.getString("a_repetition");
            String a_hold = bundle.getString("a_hold");
            String a_complete = bundle.getString("a_complete");
            String a_link = bundle.getString("a_link");
            String a_note = bundle.getString("a_note");
            String a_date = bundle.getString("a_date");
            String a_time = bundle.getString("a_time");
            Bitmap bitmap = bundle.getParcelable("a_image");

            DatabaseReference activity = databaseReference.child("users").child(user_code).child("therapists").child(ActivityNavigation.user_code).child("activities").child(a_name);
            activity.child("repetition").setValue(a_repetition);
            activity.child("hold").setValue(a_hold);
            activity.child("complete").setValue(a_complete);
            activity.child("link").setValue(a_link);
            activity.child("note").setValue(a_note);
            activity.child("status").setValue("incomplete");

            if(!a_date.equalsIgnoreCase("set date")){
                activity.child("date").setValue(a_date);
            }

            if(!a_time.equalsIgnoreCase("set time")){
                activity.child("time").setValue(a_time);
            }

            if(bitmap != null){
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                StorageReference imagesRef = storageRef.child("users").child(user_code).child("therapists").child(ActivityNavigation.user_code).child("activities").child(a_name);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] data = stream.toByteArray();

                UploadTask uploadTask = imagesRef.child("activity_image/image.jpg").putBytes(data);

                uploadTask
                        .addOnSuccessListener(taskSnapshot ->
                                Toast.makeText(getApplicationContext() , "Activity uploaded successfully", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> {
                            Toast.makeText(getApplicationContext(), "Image upload failed for some reason", Toast.LENGTH_SHORT).show();
                        })
                        .addOnProgressListener(snapshot -> {

                        });
            }else{
                Toast.makeText(getApplicationContext() , "Image upload failed, Bitmap is null", Toast.LENGTH_SHORT).show();
            }

            set_card();

        });

        btn_back.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

    }

    private void set_card(){

        tv_completed.setVisibility(View.GONE);
        tv_given.setVisibility(View.GONE);
        container_completed.removeAllViews();
        container_given.removeAllViews();

        DatabaseReference activity = databaseReference.child("users").child(user_code).child("therapists").child(ActivityNavigation.user_code).child("activities");
        activity.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String activity_name = ds.getKey();
                    String status =  ds.child("status").getValue(String.class);
                    String completion_date = ds.child("completion_date").getValue(String.class);
                    if(activity_name != null){
                        activity.child(activity_name).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                add_card(activity_name, status, completion_date);
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

    private void add_card(String name, String status, String completion_date){
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.layout_card_big, null);

        TextView nameView = view.findViewById(R.id.activity_name);
        CardView button = view.findViewById(R.id.btn_card);
        TextView tv_date = view.findViewById(R.id.tv_date);
        TextView tv_time = view.findViewById(R.id.tv_time);
        LinearLayout schedule = view.findViewById(R.id.container_schedule);

        button.setOnClickListener(v -> {
            TDialogProof proof = new TDialogProof();
            Bundle bundle = new Bundle();
            bundle.putString("user_code", tv_session_id.getText().toString());
            bundle.putString("activity", name);
            proof.setArguments(bundle);
            proof.setCancelable(false);
            proof.show(getSupportFragmentManager(), "proof");
        });

        String cardDetails = name.toUpperCase();
        nameView.setText(cardDetails);

        if(status.equalsIgnoreCase("incomplete")){
            if(tv_given.getVisibility() == View.GONE){
                tv_given.setVisibility(View.VISIBLE);
            }

            container_given.addView(view);
        }else{
            if(tv_completed.getVisibility() == View.GONE){
                tv_completed.setVisibility(View.VISIBLE);
            }

            if((completion_date != null && !completion_date.equals(""))){
                schedule.setVisibility(View.VISIBLE);
                tv_date.setText("Completion Date: ");
                tv_time.setText(completion_date);
            }

            int newColor = getResources().getColor(R.color.teal_200);
            ColorStateList colorStateList = ColorStateList.valueOf(newColor);
            button.setBackgroundTintList(colorStateList);
            container_completed.addView(view);
        }

        view.setTranslationY(-100f);
        view.setAlpha(0f);
        view.animate().translationYBy(100f).alpha(1f).setDuration(500);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Util.checkAndUpdateManualStatus(databaseReference, ActivityNavigation.user_code, "manual_patients_session", isManualEnabled -> {
            boolean showManual = !isManualEnabled;
            if(showManual){
                manual();
            }
        });
    }

    public void manual(){
        String[] messages = new String[]{"Welcome to the patient session! ",
                "This is where you can assign tasks for your patients to complete and help them reach their goals."};
        int[] gravities = new int[]{Gravity.CENTER, Gravity.BOTTOM};
        int count = 2;

        Util.createPopUpWindows(this, (ViewGroup) getWindow().getDecorView(), messages, gravities, count);
    }

    public void findView(){
        tv_session_name = findViewById(R.id.tv_session_name);
        tv_session_id = findViewById(R.id.tv_session_id);
        add_activity = findViewById(R.id.btn_add_activity);
        btn_back = findViewById(R.id.btn_back);
        container_completed = findViewById(R.id.container_completed);
        container_given = findViewById(R.id.container_given);
        tv_given = findViewById(R.id.tv_given);
        tv_completed = findViewById(R.id.tv_completed);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}