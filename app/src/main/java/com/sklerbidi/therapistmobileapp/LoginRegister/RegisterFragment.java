package com.sklerbidi.therapistmobileapp.LoginRegister;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sklerbidi.therapistmobileapp.R;

import java.util.Random;


public class RegisterFragment extends Fragment {

    Button btn_back_signup1, btn_back_signup2, btn_back_signup3, btn_next_signup1, btn_next_signup2, btn_register;
    LinearLayout container_signup_1, container_signup_2, container_signup_3;
    Spinner spinner_user_type;
    EditText et_last_name, et_first_name, et_middle_name, et_clinic_name, et_email, et_username, et_password, et_reenter_password;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://student-theses-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login_register, container, false);

        findView(view);

        layout_controls();

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String user_type = spinner_user_type.getSelectedItem().toString();
                final String last_name = et_last_name.getText().toString();
                final String first_name = et_first_name.getText().toString();
                final String middle_name = et_middle_name.getText().toString();
                final String clinic_name = et_clinic_name.getText().toString();
                final String email = et_email.getText().toString();
                final String username = et_username.getText().toString();
                final String password = et_password.getText().toString();
                final String reenter_password = et_reenter_password.getText().toString();

                if(LoginActivity.isNotEmpty(new String[]{user_type,last_name, first_name, middle_name, email, username, password, reenter_password})){
                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                String username_check = userSnapshot.child("username").getValue(String.class);
                                if (username_check != null && username_check.equals(username)) {
                                    toast("Username already exists");
                                    return;
                                }
                            }

                            if(password.equals(reenter_password)){
                                Random random = new Random();
                                String hex = Integer.toHexString(random.nextInt(16777216)).toUpperCase();
                                hex = String.format("%06x", Integer.parseInt(hex, 16));
                                final String user_code = hex.toUpperCase();

                                databaseReference.child("users").child(user_code).child("username").setValue(username);
                                databaseReference.child("users").child(user_code).child("user_type").setValue(user_type);
                                databaseReference.child("users").child(user_code).child("last_name").setValue(last_name);
                                databaseReference.child("users").child(user_code).child("first_name").setValue(first_name);
                                databaseReference.child("users").child(user_code).child("middle_name").setValue(middle_name);
                                databaseReference.child("users").child(user_code).child("clinic_name").setValue(clinic_name);
                                databaseReference.child("users").child(user_code).child("email").setValue(email);
                                databaseReference.child("users").child(user_code).child("password").setValue(password);

                                toast("Registered Successfully");

                                Fragment fragment= getParentFragmentManager().findFragmentByTag("login");
                                if(fragment != null){
                                    getActivity().getSupportFragmentManager().beginTransaction()
                                            .replace(((ViewGroup)getView().getParent()).getId(), fragment)
                                            .remove(RegisterFragment.this)
                                            .commit();
                                }else{
                                    toast("why?");
                                }

                            }else{
                                toast("Password does not match");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            toast("unknown database error");
                        }
                    });
                }else{
                    toast("do not leave any field blank");
                }
            }

        });

        return view;
    }

    public void layout_controls(){

        changeView(1);

        setSpinner(spinner_user_type, getResources().getStringArray(R.array.user_type));

        btn_back_signup1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment= getParentFragmentManager().findFragmentByTag("login");
                if(fragment != null){
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(((ViewGroup)getView().getParent()).getId(), fragment)
                            .addToBackStack(null)
                            .commit();
                }else{
                    toast("why?");
                }

            }
        });

        btn_back_signup2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeView(1);
            }
        });

        btn_back_signup3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeView(2);
            }
        });

        btn_next_signup1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(LoginActivity.isNotEmpty(new String[]{spinner_user_type.getSelectedItem().toString()})){
                    changeView(2);
                }else{
                    toast("Select account type");
                }

            }
        });

        btn_next_signup2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(LoginActivity.isNotEmpty(new String[]{et_last_name.getText().toString(), et_first_name.getText().toString(), et_middle_name.getText().toString(), et_clinic_name.getText().toString(), et_email.getText().toString()})){
                    if (android.util.Patterns.EMAIL_ADDRESS.matcher(et_email.getText().toString()).matches()) {
                        changeView(3);
                    }else{
                        toast("Invalid email");
                    }
                }else{
                    toast("Do not leave any field blank");
                }

            }
        });
    }

    public void changeView(int num){
        switch (num){
            case 1:
                container_signup_1.setVisibility(View.VISIBLE);
                container_signup_2.setVisibility(View.GONE);
                container_signup_3.setVisibility(View.GONE);
                break;
            case 2:
                container_signup_1.setVisibility(View.GONE);
                container_signup_2.setVisibility(View.VISIBLE);
                container_signup_3.setVisibility(View.GONE);
                break;
            case 3:
                container_signup_1.setVisibility(View.GONE);
                container_signup_2.setVisibility(View.GONE);
                container_signup_3.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void toast(String toast){
        Toast.makeText(getActivity(), toast, Toast.LENGTH_SHORT).show();
    }

    public static String generateRandomHex(int length) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        while (sb.length() < length) {
            sb.append(Integer.toHexString(random.nextInt()));
        }
        return sb.toString().substring(0, length);
    }
    private void setSpinner(Spinner spinner, String[] array){
        ArrayAdapter<String> adapterItems = new ArrayAdapter<String>(getContext(), R.layout.layout_list_item, array);
        spinner.setAdapter(adapterItems);
    }

    public void findView(View view){
        container_signup_1 = view.findViewById(R.id.login_2);
        container_signup_2 = view.findViewById(R.id.login_3);
        container_signup_3 = view.findViewById(R.id.login_4);

        btn_back_signup1 = view.findViewById(R.id.btn_back1);
        btn_back_signup2 = view.findViewById(R.id.btn_back2);
        btn_back_signup3 = view.findViewById(R.id.btn_back3);
        btn_next_signup1 = view.findViewById(R.id.btn_next1);
        btn_next_signup2 = view.findViewById(R.id.btn_next2);
        btn_register = view.findViewById(R.id.btn_finish);

        et_last_name = view.findViewById(R.id.et_last_name);
        et_first_name = view.findViewById(R.id.et_first_name);
        et_middle_name = view.findViewById(R.id.et_middle_name);
        et_clinic_name = view.findViewById(R.id.et_clinic_name);
        et_email = view.findViewById(R.id.et_email_address);
        et_username = view.findViewById(R.id.et_signup_username);
        et_password = view.findViewById(R.id.et_signup_password1);
        et_reenter_password = view.findViewById(R.id.et_signup_password2);

        spinner_user_type = view.findViewById(R.id.spinner_user);
    }
}