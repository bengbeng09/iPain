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

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sklerbidi.therapistmobileapp.CustomClass.JavaMailAPI;
import com.sklerbidi.therapistmobileapp.CustomClass.PhoneAuthHelper;
import com.sklerbidi.therapistmobileapp.CustomClass.Util;
import com.sklerbidi.therapistmobileapp.R;

import java.util.Random;
import java.util.concurrent.TimeUnit;


public class RegisterFragment extends Fragment {

    Button btn_back_signup1, btn_back_signup2, btn_back_signup3,btn_back_signup4, btn_next_signup1, btn_next_signup2, btn_next_signup3, btn_register;
    LinearLayout container_signup_1, container_signup_2, container_signup_3, container_signup_4, container_mobile_otp, container_email_otp;
    Spinner spinner_user_type;
    TextView tv_clinic, tv_email, tv_mobile;
    EditText et_last_name, et_first_name, et_middle_name, et_clinic_name, et_username, et_password, et_reenter_password;
    EditText et_email, et_mobile, et_email_otp, et_mobile_otp;
    Button btn_email, btn_mobile;

    String authenticated_mobile, authenticated_email;

    private PhoneAuthHelper phoneAuthHelper;

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
            final String email = authenticated_email;
            final String mobile = authenticated_mobile;
            final String username = et_username.getText().toString();
            final String password = et_password.getText().toString();
            final String reenter_password = et_reenter_password.getText().toString();

            if(LoginActivity.isNotEmpty(new String[]{user_type,last_name, first_name, middle_name, email, mobile, username, password, reenter_password})){

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
                        user.setMobile(mobile);
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

    String mobile_code, email_code;
    boolean email_authenticated = false;
    boolean mobile_authenticated = false;

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

        btn_back_signup4.setOnClickListener(view -> changeView(4,3));

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

            if(LoginActivity.isNotEmpty(new String[]{et_last_name.getText().toString(), et_first_name.getText().toString(), et_middle_name.getText().toString()})){
                if(spinner_user_type.getSelectedItem().toString().equals("Clinic Therapist")){
                    if(LoginActivity.isNotEmpty(new String[]{et_clinic_name.getText().toString()})){
                        changeView(2,3);
                    }else{
                        toast("Do not leave any field blank");
                    }
                }else{
                    changeView(2,3);
                }

            }else{
                toast("Do not leave any field blank");
            }

        });

        btn_email.setOnClickListener(view -> {
            String email = et_email.getText().toString();

            if (LoginActivity.isNotEmpty(new String[]{email})) {

                if(getContext() != null & !LoginActivity.isNetworkConnected(getContext())){
                    Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!Util.isValidEmail(email)){
                    Toast.makeText(getActivity(), "Invalid email address. Please enter a valid email address and try again.", Toast.LENGTH_SHORT).show();
                    return;
                }

                databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean emailExist = false;
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            String email_check = userSnapshot.child("email").getValue(String.class);

                            if (email_check != null && email_check.equals(email)) {
                                emailExist = true;
                                break;
                            }
                        }

                        //Email exists
                        if(!emailExist){
                            //Generate six digit code that will be sent to email
                            Random random = new Random();
                            String ResetCode = Integer.toHexString(random.nextInt(16777216)).toUpperCase();
                            ResetCode = String.format("%06x", Integer.parseInt(ResetCode, 16));
                            email_code = ResetCode.toUpperCase();

                            //Send the six digit code with a message using JavaMailAPI
                            JavaMailAPI javaMailAPI = new JavaMailAPI(getActivity(), email, "Email Authentication for iPAIN App", "Dear iPAIN User,\n" +
                                    "\n" +
                                    "Thank you for using our service. This email contains your OTP code for authentication purposes. Please do not share this code with anyone.\n" +
                                    "\n" +
                                    "Your OTP code is "+ email_code +" . If you didn't request this, contact support at 09574225472. Thank you for choosing iPAIN.\n" +
                                    "\n" +
                                    "Best regards,\n" +
                                    "iPAIN Team");
                            javaMailAPI.execute();

                            authenticated_email = email;
                            container_email_otp.setVisibility(View.VISIBLE);
                        }else{
                            Toast.makeText(getContext(), "Email already bound to an account", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Database Error", Toast.LENGTH_SHORT).show();
                    }
                });

            }else{
                Toast.makeText(getContext(), "Please enter an email address.", Toast.LENGTH_SHORT).show();
            }
        });

        et_email_otp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String code = et_email_otp.getText().toString();
                if(code.equalsIgnoreCase(email_code)){
                    email_authenticated = true;
                    et_email_otp.setBackground(getResources().getDrawable(R.drawable.button_round_outlined));
                    tv_email.setVisibility(View.GONE);
                    if(mobile_authenticated){
                        btn_next_signup3.setText("NEXT");
                    }

                }else{
                    email_authenticated = false;
                    et_email_otp.setBackground(getResources().getDrawable(R.drawable.button_round_outlined_red));
                    tv_email.setVisibility(View.VISIBLE);
                }
            }
        });

        btn_mobile.setOnClickListener(v -> {

            if (!LoginActivity.isNotEmpty(new String[]{et_mobile.getText().toString()})) {
                Toast.makeText(getActivity(), "Please enter mobile number", Toast.LENGTH_SHORT).show();
                return;
            }

            if(getContext() != null & !LoginActivity.isNetworkConnected(getContext())){
                Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
                return;
            }

            String mobile = Util.formatPhoneNumberToPHAreaCode(et_mobile.getText().toString());// Replace with user's phone number
            Log.wtf("wtf", mobile);
            if(mobile == null){
                Toast.makeText(getContext(), " Invalid Number", Toast.LENGTH_SHORT).show();
                return;
            }



            databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean mobileExist = false;
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String mobile_check = userSnapshot.child("mobile").getValue(String.class);

                        if (mobile_check != null && mobile_check.equals(mobile)) {
                            mobileExist = true;
                            break;
                        }
                    }

                    if(!mobileExist){
                        PhoneAuthHelper phoneAuthHelper = new PhoneAuthHelper();
                        phoneAuthHelper.verifyPhoneNumber(mobile, getActivity(), new PhoneAuthHelper.OnVerificationStateChangedListener() {
                            @Override
                            public void onVerificationCompleted(FirebaseUser user) {
                            }

                            @Override
                            public void onVerificationFailed(String message) {
                                container_mobile_otp.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "Verification failed", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onVerificationCodeSent(boolean code, String otp) {
                                if(code){
                                    Toast.makeText(getContext(), " OTP Sent to mobile: " + otp, Toast.LENGTH_SHORT).show();
                                    authenticated_mobile = mobile;
                                    mobile_code = otp;
                                    showMobileOTP();
                                }
                            }

                        });
                    }else{
                        Toast.makeText(getContext(), "Mobile number already bound to an account", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Database Error", Toast.LENGTH_SHORT).show();
                }
            });


        });

        et_mobile_otp.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        btn_next_signup3.setOnClickListener(view -> {

            if(!mobile_authenticated){
                if(mobile_code == null || mobile_code.trim().equals("")){
                    Toast.makeText(getContext(), "Mobile OTP not sent", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(et_mobile_otp.getText().toString().trim().equals("")){
                    Toast.makeText(getContext(), "Please enter OTP", Toast.LENGTH_SHORT).show();
                    return;
                }

                phoneAuthHelper = new PhoneAuthHelper();
                String code = et_mobile_otp.getText().toString().trim();

                phoneAuthHelper.verifyCode(code, mobile_code).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mobile_authenticated = true;
                        et_mobile_otp.setBackground(getResources().getDrawable(R.drawable.button_round_outlined));
                        if(email_authenticated){
                            btn_next_signup3.setText("NEXT");
                        }
                        tv_mobile.setVisibility(View.GONE);
                    } else {
                        mobile_authenticated = false;
                        et_mobile_otp.setBackground(getResources().getDrawable(R.drawable.button_round_outlined_red));
                        tv_mobile.setVisibility(View.VISIBLE);
                    }
                });
            }else{
                if(email_authenticated){
                    changeView(3,4);
                }else{
                    Toast.makeText(getContext(), "Email not authenticated", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void showMobileOTP(){
        container_mobile_otp.setVisibility(View.VISIBLE);
        Toast.makeText(getContext(), "OTP Sent", Toast.LENGTH_SHORT).show();
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
                }else{
                    slideInAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_right);
                    container_signup_4.startAnimation(slideInAnim);
                    container_signup_4.setVisibility(View.GONE);

                    slideOutAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_left);
                    container_signup_3.startAnimation(slideOutAnim);
                    container_signup_3.setVisibility(View.VISIBLE);
                }
                break;
            case 4:
                if(from == 3){
                    slideInAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_left);
                    container_signup_3.startAnimation(slideInAnim);
                    container_signup_3.setVisibility(View.GONE);

                    slideOutAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_right);
                    container_signup_4.startAnimation(slideOutAnim);
                    container_signup_4.setVisibility(View.VISIBLE);
                }
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
        private String mobile;
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

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }
    }

    public void findView(View view){
        container_signup_1 = view.findViewById(R.id.login_2);
        container_signup_2 = view.findViewById(R.id.login_3);
        container_signup_3 = view.findViewById(R.id.login_4);
        container_signup_4 = view.findViewById(R.id.login_5);
        container_email_otp = view.findViewById(R.id.container_email_otp);
        container_mobile_otp = view.findViewById(R.id.container_mobile_otp);

        btn_back_signup1 = view.findViewById(R.id.btn_back1);
        btn_back_signup2 = view.findViewById(R.id.btn_back2);
        btn_back_signup3 = view.findViewById(R.id.btn_back3);
        btn_back_signup4 = view.findViewById(R.id.btn_back4);

        btn_next_signup1 = view.findViewById(R.id.btn_next1);
        btn_next_signup2 = view.findViewById(R.id.btn_next2);
        btn_next_signup3 = view.findViewById(R.id.btn_next3);
        btn_register = view.findViewById(R.id.btn_finish);

        btn_email = view.findViewById(R.id.btn_email_otp);
        btn_mobile = view.findViewById(R.id.btn_mobile_otp);

        et_last_name = view.findViewById(R.id.et_last_name);
        et_first_name = view.findViewById(R.id.et_first_name);
        et_middle_name = view.findViewById(R.id.et_middle_name);
        et_clinic_name = view.findViewById(R.id.et_clinic_name);
        et_username = view.findViewById(R.id.et_signup_username);
        et_password = view.findViewById(R.id.et_signup_password1);
        et_reenter_password = view.findViewById(R.id.et_signup_password2);
        et_email = view.findViewById(R.id.et_email);
        et_email_otp = view.findViewById(R.id.et_email_otp);
        et_mobile = view.findViewById(R.id.et_mobile);
        et_mobile_otp = view.findViewById(R.id.et_mobile_otp);

        tv_clinic = view.findViewById(R.id.tvl_clinic_name);
        tv_email = view.findViewById(R.id.tv_invalid_email_otp);
        tv_mobile = view.findViewById(R.id.tv_invalid_mobile_otp);

        spinner_user_type = view.findViewById(R.id.spinner_user);
    }
}