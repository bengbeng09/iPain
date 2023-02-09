package com.sklerbidi.therapistmobileapp.Patient;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sklerbidi.therapistmobileapp.ActivityNavigation;
import com.sklerbidi.therapistmobileapp.Dialog.TDialogAddPatient;
import com.sklerbidi.therapistmobileapp.LoginRegister.LoginActivity;
import com.sklerbidi.therapistmobileapp.R;
import com.sklerbidi.therapistmobileapp.Therapist.TPatientSessionActivity;

public class PMenuTherapySession extends Fragment {

    Button btn_submit;
    TextView tv_welcome;
    EditText et_therapist_code;
    LinearLayout add_therapist, container_therapist;
    RelativeLayout therapists;
    FloatingActionButton btn_add_therapist;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://student-theses-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.menu_therapy_session, container, false);

        findView(view);
        tv_welcome.setText("Welcome, " + cap(ActivityNavigation.firstname));
        set_card();

        databaseReference.child("users").child(ActivityNavigation.user_code).child("therapists").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    therapists.setVisibility(View.VISIBLE);
                    add_therapist.setVisibility(View.GONE);
                }else{
                    therapists.setVisibility(View.GONE);
                    add_therapist.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btn_add_therapist.setOnClickListener(v -> {
            therapists.setVisibility(View.GONE);
            add_therapist.setVisibility(View.VISIBLE);
        });

        btn_submit.setOnClickListener(v -> {
            String therapist_code = et_therapist_code.getText().toString().toUpperCase();
            if(LoginActivity.isNotEmpty(new String[]{et_therapist_code.getText().toString()})){
                databaseReference.child("users").child(therapist_code).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {

                                databaseReference.child("users").child(ActivityNavigation.user_code).child("therapists").child(therapist_code).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (!snapshot.exists()) {
                                            String user_type = dataSnapshot.child("user_type").getValue(String.class);

                                            if(user_type.equals("Clinic Therapist")){
                                                databaseReference.child("users").child(ActivityNavigation.user_code).child("therapists").child(therapist_code).child("status").setValue("active");
                                                databaseReference.child("users").child(therapist_code).child("patients").child(ActivityNavigation.user_code).child("status").setValue("active");

                                                set_card();

                                                therapists.setVisibility(View.VISIBLE);
                                                add_therapist.setVisibility(View.GONE);
                                                et_therapist_code.setText(null);
                                                Toast.makeText(getActivity(), "Therapist added successfully", Toast.LENGTH_SHORT).show();

                                            }else{
                                                Toast.makeText(getActivity(), "Entered code is not a therapist", Toast.LENGTH_SHORT).show();
                                            }

                                        }else{
                                            Toast.makeText(getActivity(), "Therapist already added", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            } else {
                                Toast.makeText(getActivity(), "Invalid therapist code", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle error here
                        }
                    });
            }else{
                Toast.makeText(getActivity(), "Enter Therapist Code",
                        Toast.LENGTH_SHORT).show();
            }
        });

        return view;

    }

    private void set_card(){
        container_therapist.removeAllViews();
        databaseReference.child("users").child(ActivityNavigation.user_code).child("therapists").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String therapist_user_code = ds.getKey();

                    if(therapist_user_code != null){
                        databaseReference.child("users").child(therapist_user_code).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                String first_name = snapshot.child("first_name").getValue(String.class);
                                String last_name = snapshot.child("last_name").getValue(String.class);
                                String clinic = snapshot.child("clinic_name").getValue(String.class);

                                if (first_name != null && last_name != null) {
                                    add_card(first_name, last_name, therapist_user_code, clinic);
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


    private void add_card(String firstname, String lastname, String user_code, String clinic){
        View view = getLayoutInflater().inflate(R.layout.layout_card, null);

        TextView nameView = view.findViewById(R.id.name);
        Button show = view.findViewById(R.id.btnView);
        Button remove = view.findViewById(R.id.btnRemove);
        remove.setVisibility(View.VISIBLE);
        String cardDetails = firstname.toUpperCase() + " " + lastname.toUpperCase();
        nameView.setText(cardDetails);

        show.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PTherapistLoadingActivity.class);
            intent.putExtra("name", cardDetails);
            intent.putExtra("code", user_code);
            intent.putExtra("clinic", clinic);
            startActivity(intent);
        });

        container_therapist.addView(view);
    }

    public String cap(String originalString) {
        String[] words = originalString.split(" ");
        StringBuilder capitalizedString = new StringBuilder();
        for (String word : words) {
            capitalizedString.append(word.substring(0, 1).toUpperCase() + word.substring(1) + " ");
        }
        return capitalizedString.toString().trim();
    }

    private void findView(View view) {
        btn_submit = view.findViewById(R.id.btn_submit);
        et_therapist_code = view.findViewById(R.id.et_therapist_code);
        tv_welcome = view.findViewById(R.id.tv_welcome);
        therapists = view.findViewById(R.id.view_therapists);
        add_therapist = view.findViewById(R.id.view_add_therapist);
        btn_add_therapist = view.findViewById(R.id.btn_add_therapist);
        container_therapist = view.findViewById(R.id.container_therapist);
    }
}