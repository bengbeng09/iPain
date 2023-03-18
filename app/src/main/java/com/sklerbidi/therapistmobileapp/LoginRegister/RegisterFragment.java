/*
 This class validates the input fields before registering a user, including checking if a username or
 email is already taken, if the password matches and meets certain criteria, and if the user type
 is valid. The code uses Firebase Realtime Database to store user data. The form also includes
 buttons to navigate through different sections of the form and a button to register a new user.
 */
package com.sklerbidi.therapistmobileapp.LoginRegister;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

                if(getContext() != null & !LoginActivity.isNetworkConnected(getContext())){
                    Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }

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

                        if (!username.matches("^[a-zA-Z0-9](_(?!([._]))|\\.(?!([_.]))|[a-zA-Z0-9]){8,18}[a-zA-Z0-9]$")) {
                            if (username.length() < 8 || username.length() > 18) {
                                toast("Username must be between 8 and 18 characters long");
                            } else if (!username.matches("[a-zA-Z0-9._]+")) {
                                toast("Username can only contain alphanumeric characters, dots, or underscores");
                            } else if (username.startsWith(".") || username.startsWith("_") || username.endsWith(".") || username.endsWith("_")) {
                                toast("Username cannot start or end with a dot or underscore");
                            } else {
                                toast("Username must contain at least one alphanumeric character");
                                }
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
                        user.setUser_type(user_type);
                        user.setLast_name(last_name);
                        user.setFirst_name(first_name);
                        user.setMiddle_name(middle_name);
                        if(user_type.equals("Clinic Therapist")){
                            user.setClinic_name(clinic_name);
                        }
                        user.setEmail(email);
                        user.setPassword(password);

                        databaseReference.child("users").child(user_code).setValue(user);

                        toast("Registered Successfully");

                        Fragment fragment= getParentFragmentManager().findFragmentByTag("login");

                        if(fragment != null){
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
                                    .replace(((ViewGroup) getView().getParent()).getId(), fragment)
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

        changeView(0,1);

        setSpinner(spinner_user_type, getResources().getStringArray(R.array.user_type));

        btn_back_signup1.setOnClickListener(view -> {
            Fragment fragment= getParentFragmentManager().findFragmentByTag("login");
            if(fragment != null){
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(((ViewGroup)getView().getParent()).getId(), fragment)
                        .addToBackStack(null)
                        .commit();
            }else{
                toast("why?");
            }

        });

        btn_back_signup2.setOnClickListener(view -> changeView(2,1));

        btn_back_signup3.setOnClickListener(view -> changeView(3,2));

        btn_next_signup1.setOnClickListener(view -> {
            if(LoginActivity.isNotEmpty(new String[]{spinner_user_type.getSelectedItem().toString()})){
                if(spinner_user_type.getSelectedItem().toString().equals("Clinic Therapist")){
                    et_clinic_name.setVisibility(View.VISIBLE);
                    tv_clinic.setVisibility(View.VISIBLE);
                }else{
                    et_clinic_name.setVisibility(View.GONE);
                    tv_clinic.setVisibility(View.GONE);
                }
                changeView(1,2);
            }else{
                toast("Select account type");
            }
        });

        btn_next_signup2.setOnClickListener(view -> {

            if(LoginActivity.isNotEmpty(new String[]{et_last_name.getText().toString(), et_first_name.getText().toString(), et_middle_name.getText().toString(), et_email.getText().toString()})){
                if(spinner_user_type.getSelectedItem().toString().equals("Clinic Therapist")){
                    if(LoginActivity.isNotEmpty(new String[]{et_clinic_name.getText().toString()})){
                        if (android.util.Patterns.EMAIL_ADDRESS.matcher(et_email.getText().toString()).matches()) {
                            changeView(2,3);
                        }else{
                            toast("Invalid email");
                        }
                    }else{
                        toast("Do not leave any field blank");
                    }
                }else{
                    if (android.util.Patterns.EMAIL_ADDRESS.matcher(et_email.getText().toString()).matches()) {
                        changeView(2,3);
                    }else{
                        toast("Invalid email");
                    }
                }

            }else{
                toast("Do not leave any field blank");
            }

        });
    }

    public void changeView(int from, int to){
        Animation slideOutAnim;
        Animation slideInAnim;
        switch (to){
            case 1:
                if(from == 2){
                    slideOutAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_right);
                    container_signup_2.startAnimation(slideOutAnim);
                    container_signup_2.setVisibility(View.GONE);

                    slideInAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_left);
                    container_signup_1.startAnimation(slideInAnim);
                    container_signup_1.setVisibility(View.VISIBLE);
                }else{
                    container_signup_2.setVisibility(View.VISIBLE);
                    container_signup_2.setVisibility(View.GONE);
                    container_signup_3.setVisibility(View.GONE);
                }
                break;
            case 2:
                if(from == 1){
                    slideInAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_left);
                    container_signup_1.startAnimation(slideInAnim);
                    container_signup_1.setVisibility(View.GONE);

                    slideOutAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_right);
                    container_signup_2.startAnimation(slideOutAnim);
                    container_signup_2.setVisibility(View.VISIBLE);
                }else{
                    slideInAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_right);
                    container_signup_3.startAnimation(slideInAnim);
                    container_signup_3.setVisibility(View.GONE);

                    slideOutAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_left);
                    container_signup_2.startAnimation(slideOutAnim);
                    container_signup_2.setVisibility(View.VISIBLE);
                }
                break;
            case 3:
                if(from == 2){
                    slideInAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_left);
                    container_signup_2.startAnimation(slideInAnim);
                    container_signup_2.setVisibility(View.GONE);

                    slideOutAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_right);
                    container_signup_3.startAnimation(slideOutAnim);
                    container_signup_3.setVisibility(View.VISIBLE);
                }
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
        private String user_type;
        private String last_name;
        private String first_name;
        private String middle_name;
        private String clinic_name;
        private String email;
        private String password;

        public User() {}

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getUser_type() {
            return user_type;
        }

        public void setUser_type(String user_type) {
            this.user_type = user_type;
        }

        public String getLast_name() {
            return last_name;
        }

        public void setLast_name(String last_name) {
            this.last_name = last_name;
        }

        public String getFirst_name() {
            return first_name;
        }

        public void setFirst_name(String first_name) {
            this.first_name = first_name;
        }

        public String getMiddle_name() {
            return middle_name;
        }

        public void setMiddle_name(String middle_name) {
            this.middle_name = middle_name;
        }

        public String getClinic_name() {
            return clinic_name;
        }

        public void setClinic_name(String clinic_name) {
            this.clinic_name = clinic_name;
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