package com.sklerbidi.therapistmobileapp.Patient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import com.bumptech.glide.Glide;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sklerbidi.therapistmobileapp.ActivityNavigation;
import com.sklerbidi.therapistmobileapp.LoginRegister.LoginActivity;
import com.sklerbidi.therapistmobileapp.R;

public class PTasksActivity extends AppCompatActivity {

    Button back, finish;
    ImageView image;
    CardView card_link;
    TextView tv_name, tv_repeat, tv_hold, tv_complete, tv_link, tv_notes;
    String therapist_code, activity_name, status;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://student-theses-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_task);

        findView();


        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            activity_name = extras.getString("name");
            status = extras.getString("status");
            tv_name.setText(activity_name.toUpperCase());
            tv_repeat.setText(extras.getString("repetition"));
            tv_hold.setText(extras.getString("hold"));
            tv_complete.setText(extras.getString("complete"));
            tv_link.setText(extras.getString("link"));
            tv_notes.setText(extras.getString("note"));
            therapist_code = extras.getString("therapist");
        }


        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child("users").child(ActivityNavigation.user_code).child("therapists").child(therapist_code).child("activities").child(activity_name).child("activity_image/image.jpg");

        imagesRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // use the uri to load the image into an ImageView or to display it in some other way
            Glide.with(getApplicationContext()).load(uri).into(image);
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // handle any errors that occur while trying to retrieve the image
               Toast.makeText(getApplicationContext(), "Error retrieving image, no internet connection", Toast.LENGTH_SHORT).show();
            }
        });

        if(status.equalsIgnoreCase("complete")){
            finish.setVisibility(View.GONE);
        }

        back.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        finish.setOnClickListener(v ->{

            if(getApplicationContext() != null & !LoginActivity.isNetworkConnected(getApplicationContext())){
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        DatabaseReference activity = databaseReference.child("users").child(ActivityNavigation.user_code).child("therapists").child(therapist_code).child("activities").child(activity_name);
                        activity.child("status").setValue("complete");
                        Toast.makeText(this, "Task Completed", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        });

        card_link.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", tv_link.getText().toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getApplicationContext(), "Link copied to clipboard", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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
        image = findViewById(R.id.image_view);
        card_link = findViewById(R.id.card_link);
    }
}