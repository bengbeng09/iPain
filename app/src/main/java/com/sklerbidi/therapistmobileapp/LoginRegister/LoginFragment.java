/*
This class is used for the login screen that allows users to enter their username and password.
It also includes buttons for logging in as a guest or creating a new account, as well as checkboxes
for remembering the user's login information and a progress bar to show that the app is processing
the user's request. It uses Firebase Realtime Database to store and retrieve user data.
 */


package com.sklerbidi.therapistmobileapp.LoginRegister;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sklerbidi.therapistmobileapp.ActivityNavigation;
import com.sklerbidi.therapistmobileapp.CustomClass.DBHelper;
import com.sklerbidi.therapistmobileapp.Dialog.DialogForgotPass;
import com.sklerbidi.therapistmobileapp.Guest.GuestActivity;
import com.sklerbidi.therapistmobileapp.R;


public class LoginFragment extends Fragment {

    Button btn_login, btn_guest_login;
    TextView tv_create_account, tv_forgot_password;
    EditText et_username, et_password;
    LinearLayout container_login;
    CheckBox cb_remember;
    View progressBarLayout;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://student-theses-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_login_main, container, false);

        findView(view);

        btn_login.setOnClickListener(view1 -> {

            final String username = et_username.getText().toString();
            final String password = et_password.getText().toString();

            // Check if the fields are not empty
            if(LoginActivity.isNotEmpty(new String[]{username, password})){

                // Check if the device is connected to internet
                if(getContext() != null & !LoginActivity.isNetworkConnected(getContext())){
                    Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Access the Firebase database and look for the entered username
                databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        boolean usernameExists = false;
                        User user = new User();

                        // Loop through the child nodes and look for the username
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            String username_check = userSnapshot.child("username").getValue(String.class);

                            if (username_check != null && username_check.equals(username)) {
                                user.setUserCode(userSnapshot.getKey());
                                user.setPassword(userSnapshot.child("password").getValue(String.class));
                                user.setUserType(userSnapshot.child("user_type").getValue(String.class));
                                user.setFirstName(userSnapshot.child("first_name").getValue(String.class));
                                user.setLastName(userSnapshot.child("last_name").getValue(String.class));
                                user.setEmail(userSnapshot.child("email").getValue(String.class));
                                usernameExists = true;
                                break;
                            }
                        }

                        if(usernameExists){
                            // Check if the entered password is correct
                            if(user.getPassword() != null && user.getPassword().equals(password)){

                                // Store the user data in an array and pass it to the next activity
                                String[][] data = {
                                        {"username", username},
                                        {"user_type", user.getUserType()},
                                        {"first_name", user.getFirstName()},
                                        {"last_name", user.getLastName()},
                                        {"user_code", user.getUserCode()},
                                        {"email", user.getEmail()}
                                };

                                if(cb_remember.isChecked()){
                                    ContentValues values = new ContentValues();
                                    values.put("id", 1);
                                    values.put("username", username);
                                    values.put("password", password);

                                    DBHelper dbHelper = new DBHelper(getContext());
                                    SQLiteDatabase db = dbHelper.getWritableDatabase();

                                    db.replace("user_table", null, values);

                                    db.close();
                                }

                                showProgressBar();
                                new Handler().postDelayed(() -> {
                                    // Start the ActivityLoading activity
                                    Intent intent = new Intent(getActivity(), ActivityNavigation.class);

                                    for (String[] pair : data) {
                                        intent.putExtra(pair[0], pair[1]);
                                    }

                                    toast("Login successfully");
                                    startActivity(intent);
                                    requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                                    if (getActivity() != null) {
                                        getActivity().finish();
                                    }
                                    // Hide the progress bar
                                    new Handler().postDelayed(() -> {
                                        hideProgressBar();
                                    }, 500);

                                }, 2000);


                            }else{
                                toast("Invalid Password");
                            }

                        }else{
                            toast("Invalid Credentials");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        toast("No internet connection");
                    }
                });
            }else{
                toast("Do not leave any field blank");
            }

        });

        btn_guest_login.setOnClickListener(v -> {
            showProgressBar();

            if(cb_remember.isChecked()){
                ContentValues values = new ContentValues();
                values.put("id", 1);
                values.put("username", "guest");
                values.put("password", "guest");

                DBHelper dbHelper = new DBHelper(getContext());
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                db.replace("user_table", null, values);

                db.close();
            }

            new Handler().postDelayed(() -> {
                // Start the ActivityLoading activity
                Intent intent = new Intent(getActivity(), GuestActivity.class);
                toast("Login as Guest");
                startActivity(intent);
                requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


                if (getActivity() != null) {
                    getActivity().finish();
                }
                // Hide the progress bar
                new Handler().postDelayed(this::hideProgressBar, 500);

            }, 2000);

        });

        tv_create_account.setOnClickListener(v -> {

            et_username.setText("");
            et_password.setText("");

            RegisterFragment registerFragment = new RegisterFragment();
            if(getActivity()!= null){
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right); // add the animations
                transaction.replace(((ViewGroup) getView().getParent()).getId(), registerFragment, "register");
                transaction.addToBackStack(null);
                transaction.commit();
            }

        });

        tv_forgot_password.setOnClickListener(v -> {
            DialogForgotPass dialogForgotPass = new DialogForgotPass();
            dialogForgotPass.setCancelable(false);
            dialogForgotPass.show(getParentFragmentManager(), "forgot pass");
        });

        return view;
    }

    public void showProgressBar() {
        // Inflate the progress bar layout
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View progressBarView = inflater.inflate(R.layout.layout_progress_bar, null);

        // Add the progress bar layout to the activity's content view
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        getActivity().addContentView(progressBarView, params);

        // Save a reference to the progress bar layout so that it can be removed later
        progressBarLayout = progressBarView;
    }

    public void hideProgressBar() {
        // Remove the progress bar layout from the activity's content view
        if (progressBarLayout != null) {
            ((ViewGroup) progressBarLayout.getParent()).removeView(progressBarLayout);
            progressBarLayout = null;
        }
    }

    public static class User {
        private String password;
        private String userType;
        private String firstName;
        private String lastName;
        private String userCode;
        private String email;

        public String getUserCode() {
            return userCode;
        }

        public void setUserCode(String userCode) {
            this.userCode = userCode;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
        public String getUserType() {
            return userType;
        }

        public void setUserType(String user_type) {
            this.userType = user_type;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        // Add constructor, getters and setters
    }


    public void toast(String toast){
        Toast.makeText(getActivity(), toast, Toast.LENGTH_SHORT).show();
    }


    public void findView(View view){
        btn_login = view.findViewById(R.id.btn_login);
        btn_guest_login = view.findViewById(R.id.btn_login_guest);
        container_login = view.findViewById(R.id.login_1);
        tv_create_account = view.findViewById(R.id.tv_create_account);
        et_username = view.findViewById(R.id.et_username);
        et_password = view.findViewById(R.id.et_password);
        tv_forgot_password = view.findViewById(R.id.tv_forgot_password);
        cb_remember = view.findViewById(R.id.cb_remember_me);
    }
}