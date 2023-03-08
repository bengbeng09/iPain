package com.sklerbidi.therapistmobileapp.Therapist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.sklerbidi.therapistmobileapp.R;


public class TMenuPatients extends Fragment {

    FloatingActionButton add_patient;
    EditText et_search_patient;
    LinearLayout container_patient;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://student-theses-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.menu_patients, container, false);

        findView(view);

        set_card();

        getParentFragmentManager().setFragmentResultListener("request_patient", this, (requestKey, bundle) -> {

            String patient_user_code = bundle.getString("user_code");

            Log.wtf("wtf", patient_user_code);

            databaseReference.child("users").child(ActivityNavigation.user_code).child("patients").child(patient_user_code).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        databaseReference.child("users").child(ActivityNavigation.user_code).child("patients").child(patient_user_code).child("status").setValue("active");
                        databaseReference.child("users").child(patient_user_code).child("therapists").child(ActivityNavigation.user_code).child("status").setValue("active");
                        set_card();
                        Toast.makeText(getActivity(), "Patient added successfully", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getActivity(), "Patient already exists", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        });

        add_patient.setOnClickListener(v -> {
            TDialogAddPatient TDialogAddPatient = new TDialogAddPatient();
            TDialogAddPatient.setCancelable(false);
            TDialogAddPatient.show(getActivity().getSupportFragmentManager(), "add patient");
        });

        et_search_patient.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter_cards(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    private void set_card(){
        container_patient.removeAllViews();
        databaseReference.child("users").child(ActivityNavigation.user_code).child("patients").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String patient_user_code = ds.getKey();

                    if(patient_user_code != null){
                        databaseReference.child("users").child(patient_user_code).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                String first_name = snapshot.child("first_name").getValue(String.class);
                                String last_name = snapshot.child("last_name").getValue(String.class);

                                if (first_name != null && last_name != null) {
                                    add_card(first_name, last_name, patient_user_code);
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

    private void add_card(String firstname, String lastname, String user_code){
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.layout_card, null);

        TextView nameView = view.findViewById(R.id.name);
        Button show = view.findViewById(R.id.btnView);

        String cardDetails = firstname.toUpperCase() + " " + lastname.toUpperCase();
        nameView.setText(cardDetails);

        show.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TPatientSessionActivity.class);
            intent.putExtra("first_name", firstname);
            intent.putExtra("last_name", lastname);
            intent.putExtra("user_code", user_code);
            startActivity(intent);
        });

        container_patient.addView(view);
    }

    private void filter_cards(String query) {
        for (int i = 0; i < container_patient.getChildCount(); i++) {
            View view = container_patient.getChildAt(i);
            TextView nameView = view.findViewById(R.id.name);

            String cardDetails = nameView.getText().toString().toUpperCase();

            if (query.isEmpty() || cardDetails.contains(query.toUpperCase())) {
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.GONE);
            }
        }
    }


    public void findView(View view){
        add_patient = view.findViewById(R.id.btn_add_patient);
        container_patient = view.findViewById(R.id.container_patient);
        et_search_patient = view.findViewById(R.id.et_search_patient);
    }
}