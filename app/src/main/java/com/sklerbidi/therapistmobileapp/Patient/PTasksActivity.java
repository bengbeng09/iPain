/*
This class is used to display a specific task for a patient, including information about the
task's name, repetition, hold, completion, notes, and an image of the task.
 */

package com.sklerbidi.therapistmobileapp.Patient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import com.bumptech.glide.Glide;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sklerbidi.therapistmobileapp.ActivityNavigation;
import com.sklerbidi.therapistmobileapp.CustomClass.Util;
import com.sklerbidi.therapistmobileapp.Dialog.PDialogAddActivity;
import com.sklerbidi.therapistmobileapp.Dialog.PDialogUploadProof;
import com.sklerbidi.therapistmobileapp.LoginRegister.LoginActivity;
import com.sklerbidi.therapistmobileapp.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PTasksActivity extends AppCompatActivity {

    Button back, finish;
    ImageView image, therapist_profile;
    CardView card_link;
    TextView tv_name, tv_repeat, tv_hold, tv_complete, tv_link, tv_notes, tv_proof;
    LinearLayout container_proof;
    String therapist_code, activity_name, status;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://student-theses-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();


    @SuppressLint("QueryPermissionsNeeded")
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
            Glide.with(getApplicationContext())
                    .load(uri)
                    .transform(new RoundedCorners(20))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(image);
        }).addOnFailureListener(e -> {
            // handle any errors that occur while trying to retrieve the image
           Toast.makeText(getApplicationContext(), "Error retrieving image", Toast.LENGTH_SHORT).show();
        });

        if(status.equalsIgnoreCase("complete")){
            finish.setVisibility(View.GONE);
            tv_proof.setVisibility(View.VISIBLE);

            // Get a reference to the completion_proof folder
            StorageReference proofRef = storageRef.child("users").child(ActivityNavigation.user_code).child("therapists").child(therapist_code).child("activities").child(activity_name).child("completion_proof");

            // List all the files in the completion_proof folder
            proofRef.listAll().addOnSuccessListener(listResult -> {
                // Check if there are any files in the folder
                if (!listResult.getItems().isEmpty()) {
                    // Loop through the list of files and load them into ImageViews
                    for (StorageReference fileRef : listResult.getItems()) {
                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {

                            ImageView imageView = new ImageView(getApplicationContext());
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                            params.setMargins(0, 0, 0, 15);
                            imageView.setLayoutParams(params);

                            Glide.with(getApplicationContext())
                                    .load(uri)
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .into(imageView);
                            container_proof.addView(imageView);
                        });
                    }
                } else {
                    // Handle the case where there are no images in the folder
                    // For example, you could display a message to the user
                }
            }).addOnFailureListener(e -> {
                // Handle any errors, such as if the folder doesn't exist
            });

        }

        back.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        finish.setOnClickListener(v ->{

            PDialogUploadProof uploadProof = new PDialogUploadProof();
            uploadProof.setCancelable(false);
            uploadProof.show(getSupportFragmentManager(), "upload proof");

        });

        getSupportFragmentManager().setFragmentResultListener("request_evidence", this, (requestKey, bundle) -> {
            ArrayList<Uri> urisList = bundle.getParcelableArrayList("images_list");

            if (getApplicationContext() != null && !LoginActivity.isNetworkConnected(getApplicationContext())) {
                Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                return;
            }

            if (urisList == null || urisList.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Image upload failed, no images selected", Toast.LENGTH_SHORT).show();
                return;
            }

            StorageReference evidenceRef = storageRef.child("users").child(ActivityNavigation.user_code).child("therapists").child(therapist_code).child("activities").child(activity_name);
            int image_count = 0;
            for (int i = 0; i < urisList.size(); i++) {
                Uri uri = urisList.get(i);
                String imageName = "image_proof" + i + ".jpg";
                Bitmap bitmap = null;

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (bitmap != null) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    UploadTask uploadTask = evidenceRef.child("completion_proof/" + imageName).putBytes(byteArray);
                    image_count++;
                    uploadTask.addOnSuccessListener(taskSnapshot -> {
                        // Handle the success case if needed
                    }).addOnFailureListener(exception -> {
                        Toast.makeText(getApplicationContext(), imageName + " upload failed", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            if (image_count > 0) {
                DatabaseReference activity = databaseReference.child("users").child(ActivityNavigation.user_code).child("therapists").child(therapist_code).child("activities").child(activity_name);
                activity.child("status").setValue("complete");

                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault());
                String completionDate = dateFormat.format(new Date());

                activity.child("completion_date").setValue(completionDate);

                Toast.makeText(this, "Task Completed, Uploaded " + image_count + " images out of " + urisList.size(), Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            } else {
                Toast.makeText(getApplicationContext(), "Image upload failed, unable to retrieve bitmap", Toast.LENGTH_SHORT).show();
            }
        });

        card_link.setOnClickListener(v -> {
            String url = tv_link.getText().toString();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "Invalid link", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Util.checkAndUpdateManualStatus(databaseReference, ActivityNavigation.user_code, "manual_task_activity", isManualEnabled -> {
            boolean showManual = !isManualEnabled;
            if(showManual){
                manual();
            }
        });
    }

    public void manual(){
        String[] messages = new String[]{"Here's one of the tasks your therapist has assigned for you. Let's tackle it together and take a step closer towards achieving your goals!", "To mark it as complete, you can upload a proof of completion after clicking the 'Finish' button "};
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
        tv_name = findViewById(R.id.tv_activity_name);
        tv_repeat = findViewById(R.id.tv_repeat);
        tv_hold = findViewById(R.id.tv_hold);
        tv_complete = findViewById(R.id.tv_complete);
        tv_link = findViewById(R.id.tv_link);
        tv_notes = findViewById(R.id.tv_note);
        tv_proof = findViewById(R.id.tv_proof);
        container_proof = findViewById(R.id.container_proof);
        finish = findViewById(R.id.btn_finish);
        image = findViewById(R.id.image_view);
        card_link = findViewById(R.id.card_link);
    }
}