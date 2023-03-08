package com.sklerbidi.therapistmobileapp.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sklerbidi.therapistmobileapp.ActivityNavigation;
import com.sklerbidi.therapistmobileapp.LoginRegister.LoginActivity;
import com.sklerbidi.therapistmobileapp.R;

public class DialogChangePass extends AppCompatDialogFragment {

    EditText et_current_password, et_new_password, et_retype_new_password;
    Button btn_back, btn_submit;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://student-theses-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
        View view = getLayoutInflater().inflate(R.layout.dialog_change_pass,null);

        findView(view);

        btn_submit.setOnClickListener(v -> {

            final String current_password = et_current_password.getText().toString();
            final String new_password = et_new_password.getText().toString();
            final String re_new_password = et_retype_new_password.getText().toString();

            if(LoginActivity.isNotEmpty(new String[]{current_password, new_password, re_new_password})){

                if(new_password.equals(re_new_password)){

                    if(new_password.length() < 7){
                        Toast.makeText(getContext(), "New password is too short", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    DatabaseReference userRef = databaseReference.child("users").child(ActivityNavigation.user_code);
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String username_check = snapshot.child("username").getValue(String.class);

                            if (username_check != null && username_check.equals(ActivityNavigation.username)) {
                                String current_password_check = snapshot.child("password").getValue(String.class);

                                if (current_password.equals(current_password_check)) {

                                    userRef.child("password").setValue(new_password);
                                    Toast.makeText(getContext(), "Password changed successfully", Toast.LENGTH_SHORT).show();
                                    getParentFragmentManager().beginTransaction().remove(DialogChangePass.this).commit();

                                } else {
                                    Toast.makeText(getContext(), "Current password is incorrect", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getContext(), "System Error", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle error
                        }
                    });
                }else{
                    Toast.makeText(getContext(), "New password does not match", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(getContext(), "Do not leave any field blank", Toast.LENGTH_SHORT).show();
            }

        });

        btn_back.setOnClickListener(v -> {
            AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
            builder2.setMessage("Cancel Reset Password?")
                    .setPositiveButton("Yes", (dialog, which) -> getParentFragmentManager().beginTransaction().remove(DialogChangePass.this).commit())
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            AlertDialog alertDialog = builder2.create();
            alertDialog.show();

        });


        builder.setView(view);
        return builder.create();
    }

    private void findView(View view) {
        et_current_password = view.findViewById(R.id.et_current_password);
        et_new_password = view.findViewById(R.id.et_new_password);
        et_retype_new_password = view.findViewById(R.id.et_retype_new_password);
        btn_back = view.findViewById(R.id.btn_back);
        btn_submit = view.findViewById(R.id.btn_submit);
    }
}