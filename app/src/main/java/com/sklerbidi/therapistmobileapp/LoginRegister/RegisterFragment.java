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
import android.widget.TextView;
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
    TextView tv_clinic;
    EditText et_last_name, et_first_name, et_middle_name, et_clinic_name, et_email, et_username, et_password, et_reenter_password;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://student-theses-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login_register, container, false);

        findView(view);

        layout_controls();

        btn_register.setOnClickListener(view1 -> {
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
                            String email_check = userSnapshot.child("email").getValue(String.class);

                            if (username_check != null && username_check.equalsIgnoreCase(username)) {
                                toast("Username already exists");
                                return;
                            }

                            if (email_check != null && email_check.equalsIgnoreCase(email)) {
                                toast("Email already exists");
                                return;
                            }
                        }

                        if (username.length() < 7) {
                            toast("Username must be at least 8 characters");
                            return;
                        }

                        if(!password.equals(reenter_password)){
                            toast("Password does not match");
                            return;
                        }

                        if(password.length() < 7){
                            toast("Password is too short");
                            return;
                        }

                        Random random = new Random();
                        String hex = Integer.toHexString(random.nextInt(16777216)).toUpperCase();
                        hex = String.format("%06x", Integer.parseInt(hex, 16));
                        final String user_code = hex.toUpperCase();

                        User user = new User();
                        user.setUsername(username);
                        user.setUserType(user_type);
                        user.setLastName(last_name);
                        user.setFirstName(first_name);
                        user.setMiddleName(middle_name);
                        if(user_type.equals("Clinic Therapist")){
                            user.setClinicName(clinic_name);
                        }
                        user.setEmail(email);
                        user.setPassword(password);

                        databaseReference.child("users").child(user_code).setValue(user);

                        toast("Registered Successfully");

                        Fragment fragment= getParentFragmentManager().findFragmentByTag("login");

                        if(fragment != null){
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(((ViewGroup)getView().getParent()).getId(), fragment)
                                    .remove(RegisterFragment.this)
                                    .commit();
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
        });

        return view;
    }

    public void layout_controls(){

        changeView(1);

        setSpinner(spinner_user_type, getResources().getStringArray(R.array.user_type));

        btn_back_signup1.setOnClickListener(view -> {
            Fragment fragment= getParentFragmentManager().findFragmentByTag("login");
            if(fragment != null){
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(((ViewGroup)getView().getParent()).getId(), fragment)
                        .addToBackStack(null)
                        .commit();
            }else{
                toast("why?");
            }

        });

        btn_back_signup2.setOnClickListener(view -> changeView(1));

        btn_back_signup3.setOnClickListener(view -> changeView(2));

        btn_next_signup1.setOnClickListener(view -> {
            if(LoginActivity.isNotEmpty(new String[]{spinner_user_type.getSelectedItem().toString()})){
                if(spinner_user_type.getSelectedItem().toString().equals("Clinic Therapist")){
                    et_clinic_name.setVisibility(View.VISIBLE);
                    tv_clinic.setVisibility(View.VISIBLE);
                }else{
                    et_clinic_name.setVisibility(View.GONE);
                    tv_clinic.setVisibility(View.GONE);
                }
                changeView(2);
            }else{
                toast("Select account type");
            }

        });

        btn_next_signup2.setOnClickListener(view -> {

            if(LoginActivity.isNotEmpty(new String[]{et_last_name.getText().toString(), et_first_name.getText().toString(), et_middle_name.getText().toString(), et_email.getText().toString()})){
                if(spinner_user_type.getSelectedItem().toString().equals("Clinic Therapist")){
                    if(LoginActivity.isNotEmpty(new String[]{et_clinic_name.getText().toString()})){
                        if (android.util.Patterns.EMAIL_ADDRESS.matcher(et_email.getText().toString()).matches()) {
                            changeView(3);
                        }else{
                            toast("Invalid email");
                        }
                    }else{
                        toast("Do not leave any field blank");
                    }
                }else{
                    if (android.util.Patterns.EMAIL_ADDRESS.matcher(et_email.getText().toString()).matches()) {
                        changeView(3);
                    }else{
                        toast("Invalid email");
                    }
                }

            }else{
                toast("Do not leave any field blank");
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

    private void setSpinner(Spinner spinner, String[] array){
        ArrayAdapter<String> adapterItems = new ArrayAdapter<String>(getContext(), R.layout.layout_list_item, array);
        spinner.setAdapter(adapterItems);
    }

    public static class User {
        private String username;
        private String userType;
        private String lastName;
        private String firstName;
        private String middleName;
        private String clinicName;
        private String email;
        private String password;

        public User() {}

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getUserType() {
            return userType;
        }

        public void setUserType(String userType) {
            this.userType = userType;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getMiddleName() {
            return middleName;
        }

        public void setMiddleName(String middleName) {
            this.middleName = middleName;
        }

        public String getClinicName() {
            return clinicName;
        }

        public void setClinicName(String clinicName) {
            this.clinicName = clinicName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
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

        tv_clinic = view.findViewById(R.id.tvl_clinic_name);

        spinner_user_type = view.findViewById(R.id.spinner_user);
    }
}