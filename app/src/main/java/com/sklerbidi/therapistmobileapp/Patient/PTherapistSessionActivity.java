/*
The code shows therapist session details including activities, their status (complete/incomplete), and related data.
The user can click on the card for each activity to access more details about it.
 */

package com.sklerbidi.therapistmobileapp.Patient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sklerbidi.therapistmobileapp.ActivityNavigation;
import com.sklerbidi.therapistmobileapp.CustomClass.Util;
import com.sklerbidi.therapistmobileapp.LoginRegister.LoginActivity;
import com.sklerbidi.therapistmobileapp.R;

import org.w3c.dom.Text;

import java.util.Comparator;
import java.util.List;

public class PTherapistSessionActivity extends AppCompatActivity {

    Button back;
    String user_code, name, clinic;
    TextView tv_name, tv_clinic, tv_completed, tv_pending;
    LinearLayout container_incomplete, container_completed;
    ImageView therapist_profile;
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

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference therapistRef = storageRef.child("users").child(user_code).child("profile");

        therapistRef.listAll().addOnSuccessListener(listResult -> {
            List<StorageReference> items = listResult.getItems();
            if (items.size() > 0) {
                items.sort(Comparator.comparing(StorageReference::getName));

                StorageReference latestProfileRef = items.get(items.size() - 1);

                latestProfileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    if(getApplicationContext()!=null){
                        Glide.with(getApplicationContext())
                                .load(uri)
                                .transform(new CircleCrop())
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(therapist_profile);
                    }
                });
            }else{
                therapist_profile.setImageResource(R.drawable.ic_baseline_account_circle_24);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getApplicationContext(), "Error retrieving profile, connection problem", Toast.LENGTH_SHORT).show();
        });

        if(getApplicationContext() != null & !LoginActivity.isNetworkConnected(getApplicationContext())){
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }

        set_card();

        back.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
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
                    String date = ds.child("date").getValue(String.class);
                    String time = ds.child("time").getValue(String.class);
                    String completion_date = ds.child("completion_date").getValue(String.class);

                    if(activity_name != null && status != null){
                        activity.child(activity_name).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                add_card(activity_name, repetition, hold, complete, link, note, status, user_code, date, time, completion_date);
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

    private void add_card(String name, String repetition, String hold, String complete, String link, String note, String status, String therapist_code, String date, String time, String completion_date){
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.layout_card_big, null);

        TextView nameView = view.findViewById(R.id.activity_name);
        CardView button = view.findViewById(R.id.btn_card);
        TextView tv_date = view.findViewById(R.id.tv_date);
        TextView tv_time = view.findViewById(R.id.tv_time);
        ImageView image = view.findViewById(R.id.image_view);

        LinearLayout schedule = view.findViewById(R.id.container_schedule);
        String cardDetails = name.toUpperCase();
        nameView.setText(cardDetails);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child("users").child(ActivityNavigation.user_code).child("therapists").child(therapist_code).child("activities").child(name).child("activity_image/image.jpg");

        imagesRef.getDownloadUrl().addOnSuccessListener(uri -> {
            image.setVisibility(View.VISIBLE);

            Glide.with(getApplicationContext())
                    .load(uri)
                    .transform(new CenterCrop(), new RoundedCorners(20))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(image);
        }).addOnFailureListener(e -> {

        });

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
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        if(status.equalsIgnoreCase("incomplete")){
            if(tv_pending.getVisibility() == View.GONE){
                tv_pending.setVisibility(View.VISIBLE);
            }

            if((date != null && !date.equals(""))){
                schedule.setVisibility(View.VISIBLE);

                tv_date.setText("Schedule: "+ date);
                if((time != null && !time.equals(""))){
                    tv_time.setText( " - " + time);
                }
            }

            container_incomplete.addView(view);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SECOND_ACTIVITY && resultCode == RESULT_OK) {
            // Call the desired method here
            set_card();
        }
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
        String[] messages = new String[]{"Welcome to your therapy session! ",
                "Here, you can view all of your pending tasks and completed tasks"};
        int[] gravities = new int[]{Gravity.CENTER, Gravity.BOTTOM};
        int count = 2;

        Util.createPopUpWindows(this, (ViewGroup) getWindow().getDecorView(), messages, gravities, count);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void findView(){
        back = findViewById(R.id.btn_back);
        container_incomplete = findViewById(R.id.container_incomplete);
        tv_name = findViewById(R.id.tv_therapist_name);
        tv_clinic = findViewById(R.id.tv_therapist_clinic);
        container_completed = findViewById(R.id.container_completed);
        tv_completed = findViewById(R.id.tv_completed);
        tv_pending = findViewById(R.id.tv_pending);
        therapist_profile = findViewById(R.id.img_therapist_profile);
    }
}