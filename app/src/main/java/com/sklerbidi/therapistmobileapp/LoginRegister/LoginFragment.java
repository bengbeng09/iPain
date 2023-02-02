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
import com.sklerbidi.therapistmobileapp.R;


public class LoginFragment extends Fragment {

    Button btn_login;
    TextView tv_create_account;
    EditText et_username, et_password;
    LinearLayout container_login;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://student-theses-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_login_main, container, false);

        findView(view);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final String username = et_username.getText().toString();
                final String password = et_password.getText().toString();

                if(LoginActivity.isNotEmpty(new String[]{username, password})){
                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(username)){
                                final String get_password = snapshot.child(username).child("password").getValue(String.class);
                                final String get_user_type = snapshot.child(username).child("user_type").getValue(String.class);
                                final String get_first_name = snapshot.child(username).child("first_name").getValue(String.class);
                                final String get_last_name = snapshot.child(username).child("last_name").getValue(String.class);
                                if(get_password.equals(password)){

                                    toast("Login successfully");

                                    Intent intent = new Intent(getActivity(), ActivityNavigation.class);
                                    intent.putExtra("username", username);
                                    intent.putExtra("user_type", get_user_type);
                                    intent.putExtra("first_name", get_first_name);
                                    intent.putExtra("last_name", get_last_name);

                                    startActivity(intent);

                                    getActivity().finish();

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



            }
        });

        tv_create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_username.setText("");
                et_password.setText("");

                RegisterFragment registerFragment = new RegisterFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(((ViewGroup)getView().getParent()).getId(), registerFragment, "register")
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }


    public void toast(String toast){
        Toast.makeText(getActivity(), toast, Toast.LENGTH_SHORT).show();
    }

    public void findView(View view){
        btn_login = view.findViewById(R.id.btn_login);
        container_login = view.findViewById(R.id.login_1);
        tv_create_account = view.findViewById(R.id.tv_create_account);
        et_username = view.findViewById(R.id.et_username);
        et_password = view.findViewById(R.id.et_password);
    }
}