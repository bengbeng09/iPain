//Loading screen for a patient's therapist

package com.sklerbidi.therapistmobileapp.Patient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sklerbidi.therapistmobileapp.ActivityNavigation;
import com.sklerbidi.therapistmobileapp.R;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PTherapistLoadingActivity extends AppCompatActivity {
    String name, code, clinic;
    TextView tv_name;
    ImageView therapist_profile;

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

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference imagesRef = storageRef.child("users").child(code).child("profile");

            imagesRef.listAll().addOnSuccessListener(listResult -> {
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
        }

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(PTherapistLoadingActivity.this, PTherapistSessionActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("code", code);
            intent.putExtra("clinic", clinic);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }, 2000);
    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }

    private void findView(){
        tv_name = findViewById(R.id.tv_loading_therapist_name);
        therapist_profile = findViewById(R.id.img_therapist_profile);
    }

}