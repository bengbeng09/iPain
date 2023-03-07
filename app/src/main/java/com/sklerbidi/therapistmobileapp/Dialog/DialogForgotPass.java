package com.sklerbidi.therapistmobileapp.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sklerbidi.therapistmobileapp.CustomClass.JavaMailAPI;
import com.sklerbidi.therapistmobileapp.LoginRegister.LoginActivity;
import com.sklerbidi.therapistmobileapp.R;

import java.util.Random;


public class DialogForgotPass extends AppCompatDialogFragment {
    EditText et_email, et_code, et_new_pass, et_re_new_pass;
    Button btn_submit, btn_send_email, btn_back;
    TextView tv_invalid_code;
    LinearLayout container_code, container_pass, container_email;
    String user_code, reset_code;
    boolean code_check = false;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://student-theses-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
        View view = getLayoutInflater().inflate(R.layout.dialog_forgot_pass,null);

        findView(view);

        btn_send_email.setOnClickListener( v -> {

            String email = et_email.getText().toString();

            if (LoginActivity.isNotEmpty(new String[]{email})) {
                databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean emailExist = false;
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            String email_check = userSnapshot.child("email").getValue(String.class);
                            user_code = userSnapshot.getKey();
                            if (email_check != null && email_check.equals(email)) {

                                emailExist = true;
                                break;
                            }
                        }

                        if(emailExist){

                            Random random = new Random();
                            String ResetCode = Integer.toHexString(random.nextInt(16777216)).toUpperCase();
                            ResetCode = String.format("%06x", Integer.parseInt(ResetCode, 16));
                            reset_code = ResetCode.toUpperCase();

                            JavaMailAPI javaMailAPI = new JavaMailAPI(getActivity(), email, "Password Reset Request for iPAIN App", "Dear iPAIN User,\n" +
                                    "\n" +
                                    "We received a request to reset your iPain app password. To ensure your safety and privacy, remember to use a unique and strong password, avoid sharing personal info, and keep the app updated.\n" +
                                    "\n" +
                                    "Your reset code is "+ reset_code +" . If you didn't request this, contact support at 09574225472. Thank you for choosing iPAIN.\n" +
                                    "\n" +
                                    "Best regards,\n" +
                                    "iPAIN Team");
                            javaMailAPI.execute();

                            container_email.setVisibility(View.GONE);
                            container_code.setVisibility(View.VISIBLE);
                        }else{
                            Toast.makeText(getContext(), "Entered email is not registered", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Database Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                Toast.makeText(getContext(), "Database Error", Toast.LENGTH_SHORT).show();
            }
        });

        et_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String code = et_code.getText().toString();
                if(code.equalsIgnoreCase(reset_code)){
                    code_check = true;
                    et_code.setBackground(getResources().getDrawable(R.drawable.button_round_outlined));
                    tv_invalid_code.setVisibility(View.GONE);

                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        container_code.setVisibility(View.GONE);
                        btn_submit.setVisibility(View.VISIBLE);
                        container_pass.setVisibility(View.VISIBLE);
                    }, 2000);



                }else{
                    code_check = false;
                    et_code.setBackground(getResources().getDrawable(R.drawable.button_round_outlined_red));
                    tv_invalid_code.setVisibility(View.VISIBLE);
                }
            }
        });

        btn_submit.setOnClickListener(v -> {
            String new_password = et_new_pass.getText().toString();
            String re_new_password = et_re_new_pass.getText().toString();
            if (LoginActivity.isNotEmpty(new String[]{new_password, re_new_password}) && code_check) {
                if(new_password.equals(re_new_password)){

                    if(new_password.length() < 7){
                        Toast.makeText(getContext(), "New password is too short", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    DatabaseReference userRef = databaseReference.child("users").child(user_code);
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            userRef.child("password").setValue(new_password);
                            Toast.makeText(getContext(), "Password changed successfully", Toast.LENGTH_SHORT).show();
                            getParentFragmentManager().beginTransaction().remove(DialogForgotPass.this).commit();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getContext(), "Internal error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Toast.makeText(getContext(), "Password does not match", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_back.setOnClickListener(v->{
            AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
            builder2.setMessage("Cancel Reset Password?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        getParentFragmentManager().beginTransaction().remove(DialogForgotPass.this).commit();
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            AlertDialog alertDialog = builder2.create();
            alertDialog.show();
        });


        builder.setView(view);
        return builder.create();
    }

    private void findView(View view) {
        et_email = view.findViewById(R.id.et_email);
        et_code = view.findViewById(R.id.et_code);
        et_new_pass = view.findViewById(R.id.et_new_password);
        et_re_new_pass = view.findViewById(R.id.et_retype_new_password);
        btn_submit = view.findViewById(R.id.btn_submit);
        btn_send_email = view.findViewById(R.id.btn_send_email);
        btn_back = view.findViewById(R.id.btn_back);
        container_code = view.findViewById(R.id.container_code);
        container_pass = view.findViewById(R.id.container_new_pass);
        container_email = view.findViewById(R.id.container_email);
        tv_invalid_code = view.findViewById(R.id.tv_invalid_code);
    }
}