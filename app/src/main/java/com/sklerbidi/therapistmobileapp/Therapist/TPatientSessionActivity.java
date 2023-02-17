package com.sklerbidi.therapistmobileapp.Therapist;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentResultListener;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sklerbidi.therapistmobileapp.ActivityNavigation;
import com.sklerbidi.therapistmobileapp.Dialog.PDialogAddActivity;
import com.sklerbidi.therapistmobileapp.R;

import java.io.ByteArrayOutputStream;


public class TPatientSessionActivity extends AppCompatActivity {

    FloatingActionButton add_activity;
    Button btn_back;
    LinearLayout container_activity;
    TextView tv_session_name, tv_session_id;
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

        set_card();

        getSupportFragmentManager().setFragmentResultListener("request_activity", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                String a_name = bundle.getString("a_name");
                String a_repetition = bundle.getString("a_repetition");
                String a_hold = bundle.getString("a_hold");
                String a_complete = bundle.getString("a_complete");
                String a_link = bundle.getString("a_link");
                String a_note = bundle.getString("a_note");
                Bitmap bitmap = bundle.getParcelable("bitmap");

                DatabaseReference activity = databaseReference.child("users").child(user_code).child("therapists").child(ActivityNavigation.user_code).child("activities").child(a_name);
;
                activity.child("repetition").setValue(a_repetition);
                activity.child("hold").setValue(a_hold);
                activity.child("complete").setValue(a_complete);
                activity.child("link").setValue(a_link);
                activity.child("note").setValue(a_note);
                activity.child("status").setValue("incomplete");

                if(bitmap != null){
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();
                    StorageReference imagesRef = storageRef.child("users").child(user_code).child("therapists").child(ActivityNavigation.user_code).child("activities").child(a_name);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] data = stream.toByteArray();

                    UploadTask uploadTask = imagesRef.child("images/image.jpg").putBytes(data);

                    uploadTask
                            .addOnSuccessListener(taskSnapshot ->
                                    Toast.makeText(getApplicationContext() , "Activity uploaded successfully", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e ->
                                    Toast.makeText(getApplicationContext() , "Image upload failed", Toast.LENGTH_SHORT).show())
                            .addOnProgressListener(snapshot -> {
                                //double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                            });
                }else{
                    Toast.makeText(getApplicationContext() , "Image upload failed", Toast.LENGTH_SHORT).show();
                }

                set_card();

            }
        });

        add_activity.setOnClickListener(v -> {
            PDialogAddActivity menuActivity = new PDialogAddActivity();
            Bundle bundle = new Bundle();
            bundle.putString("user_code", tv_session_id.getText().toString());
            menuActivity.setArguments(bundle);
            menuActivity.setCancelable(false);
            menuActivity.show(getSupportFragmentManager(), "add activity");
        });

        btn_back.setOnClickListener(v -> finish());

    }

    private void set_card(){
        container_activity.removeAllViews();
        DatabaseReference activity = databaseReference.child("users").child(user_code).child("therapists").child(ActivityNavigation.user_code).child("activities");
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

        String cardDetails = name.toUpperCase();
        nameView.setText(cardDetails);

        container_activity.addView(view);
    }

    public void findView(){
        tv_session_name = findViewById(R.id.tv_session_name);
        tv_session_id = findViewById(R.id.tv_session_id);
        add_activity = findViewById(R.id.btn_add_activity);
        btn_back = findViewById(R.id.btn_back);
        container_activity = findViewById(R.id.container_activity);
    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }
}