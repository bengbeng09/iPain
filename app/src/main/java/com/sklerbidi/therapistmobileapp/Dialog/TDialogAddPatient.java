
/*
This custom Dialog allows a user to select a patient from a list of clinic patients
and add them to the list of patients for the current clinic.
 */

package com.sklerbidi.therapistmobileapp.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sklerbidi.therapistmobileapp.LoginRegister.LoginActivity;
import com.sklerbidi.therapistmobileapp.R;
import com.sklerbidi.therapistmobileapp.CustomClass.UserInfo;

import java.util.ArrayList;


public class TDialogAddPatient extends AppCompatDialogFragment {

    Button btn_back, btn_confirm;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://student-theses-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
    ArrayList<UserInfo> users = new ArrayList<>();
    AutoCompleteTextView et_patient_name;
    ArrayAdapter<String> arrayAdapter;
    String[] patients;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
        View view = getLayoutInflater().inflate(R.layout.dialog_add_patient,null);

        findView(view);

        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String user_code = ds.getKey();
                    String first_name = ds.child("first_name").getValue(String.class);
                    String last_name = ds.child("last_name").getValue(String.class);
                    String user_type = ds.child("user_type").getValue(String.class);
                    String username = ds.child("username").getValue(String.class);

                    if (user_type != null && user_type.equals("Clinic Patient")) {
                        UserInfo user = new UserInfo(username, first_name, last_name, user_type, user_code);
                        users.add(user);
                    }
                }

                patients = new String[users.size()];
                for (int i = 0; i < users.size(); i++) {
                    UserInfo user = users.get(i);
                    patients[i] = cap(user.getFirst_name()) + " " + cap(user.getLast_name()) + " (" + user.getUser_code().toUpperCase() + ") ";
                }

                if(patients != null){
                    arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.layout_list_item, patients);
                    et_patient_name.setAdapter(arrayAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        et_patient_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (arrayAdapter.getPosition(et_patient_name.getText().toString()) < 0) {
                    et_patient_name.setError("Patient not found");
                } else {
                    et_patient_name.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    et_patient_name.setError(null);
                }
            }
        });

        btn_confirm.setOnClickListener(v -> {
            Bundle bundle = new Bundle();

            if(LoginActivity.isNotEmpty(new String[]{et_patient_name.getText().toString()})){
                String input = et_patient_name.getText().toString();
                String userCode;

                if (input.contains("(") && input.contains(")")) {
                    userCode = input.substring(input.indexOf("(") + 1, input.indexOf(")"));

                    if (!userCode.isEmpty()) {
                        databaseReference.child("users").child(userCode)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            String user_code = dataSnapshot.getKey();
                                            if (user_code != null){
                                                DataSnapshot userSnapshot = dataSnapshot.child(user_code);
                                                String username = userSnapshot.child("username").getValue(String.class);
                                                bundle.putString("user_code",user_code);
                                                bundle.putString("username",username);
                                                getParentFragmentManager().setFragmentResult("request_patient", bundle);
                                                getParentFragmentManager().beginTransaction().remove(TDialogAddPatient.this).commit();
                                            }else{
                                                Toast.makeText(getActivity(), "Unknown Error", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(getActivity(), "Patient not found", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Handle error here
                                    }
                                });
                    } else {
                        Toast.makeText(getActivity(), "Invalid patient Name/ID", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Invalid patient Name/ID", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(getActivity(), "Enter patient Name/ID",
                        Toast.LENGTH_SHORT).show();
            }
        });

        btn_back.setOnClickListener(v -> getParentFragmentManager().beginTransaction().remove(TDialogAddPatient.this).commit());

        builder.setView(view);
        return builder.create();
    }

    public String cap(String originalString) {
        String[] words = originalString.split(" ");
        StringBuilder capitalizedString = new StringBuilder();
        for (String word : words) {
            capitalizedString.append(word.substring(0, 1).toUpperCase()).append(word.substring(1)).append(" ");
        }
        return capitalizedString.toString().trim();
    }

    public void findView(View view){
        et_patient_name = view.findViewById(R.id.et_patient_name);
        btn_back = view.findViewById(R.id.btn_back);
        btn_confirm = view.findViewById(R.id.btn_confirm);
    }
}