package com.sklerbidi.therapistmobileapp.LoginRegister;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.sklerbidi.therapistmobileapp.R;

public class LoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginFragment loginFragment = new LoginFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_login, loginFragment, "login")
                .addToBackStack(null)
                .commit();
    }

    public static boolean isNotEmpty(String[] items) {

        for (String item : items) {

            if (item == null) {
                return false;
            }

            if (item.trim().length() < 1) {
                return false;
            }

            if (item.trim().toLowerCase().contains("select account type")) {
                return false;
            }
        }
        return true;
    }

}