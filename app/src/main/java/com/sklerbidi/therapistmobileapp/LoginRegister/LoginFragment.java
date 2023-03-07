package com.sklerbidi.therapistmobileapp.LoginRegister;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.sklerbidi.therapistmobileapp.ActivityNavigation;
import com.sklerbidi.therapistmobileapp.Dialog.DialogChangePass;
import com.sklerbidi.therapistmobileapp.Dialog.DialogForgotPass;
import com.sklerbidi.therapistmobileapp.Guest.GuestActivity;
import com.sklerbidi.therapistmobileapp.R;


public class LoginFragment extends Fragment {

    Button btn_login, btn_guest_login;
    TextView tv_create_account, tv_forgot_password;
    EditText et_username, et_password;
    LinearLayout container_login;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://student-theses-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_login_main, container, false);

        findView(view);

        btn_login.setOnClickListener(view1 -> {

            final String username = et_username.getText().toString();
            final String password = et_password.getText().toString();

            if(LoginActivity.isNotEmpty(new String[]{username, password})){
                databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        boolean usernameExists = false;
                        User user = new User();

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

                            if(user.getPassword() != null && user.getPassword().equals(password)){
                                toast("Login successfully");

                                String[][] data = {
                                        {"username", username},
                                        {"user_type", user.getUserType()},
                                        {"first_name", user.getFirstName()},
                                        {"last_name", user.getLastName()},
                                        {"user_code", user.getUserCode()},
                                        {"email", user.getEmail()}
                                };

                                Intent intent = new Intent(getActivity(), ActivityNavigation.class);

                                for (String[] pair : data) {
                                    intent.putExtra(pair[0], pair[1]);
                                }

                                startActivity(intent);

                                if (getActivity() != null) {
                                    getActivity().finish();
                                }

                            }else{
                                toast("Invalid Password");
                            }

                        }else{
                            toast("Invalid Credentials");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }else{
                toast("Do not leave any field blank");
            }

        });

        btn_guest_login.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), GuestActivity.class);

            startActivity(intent);

            if (getActivity() != null) {
                getActivity().finish();
            }

        });

        tv_create_account.setOnClickListener(v -> {

            et_username.setText("");
            et_password.setText("");

            RegisterFragment registerFragment = new RegisterFragment();
            if(getActivity()!= null){
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(((ViewGroup)getView().getParent()).getId(), registerFragment, "register")
                        .addToBackStack(null)
                        .commit();
            }

        });

        tv_forgot_password.setOnClickListener(v -> {
            DialogForgotPass dialogForgotPass = new DialogForgotPass();
            dialogForgotPass.setCancelable(false);
            dialogForgotPass.show(getParentFragmentManager(), "forgot pass");
        });

        return view;
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
    }
}