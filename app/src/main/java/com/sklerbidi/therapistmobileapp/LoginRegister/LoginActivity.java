package com.sklerbidi.therapistmobileapp.LoginRegister;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sklerbidi.therapistmobileapp.CustomClass.DBHelper;
import com.sklerbidi.therapistmobileapp.Dialog.DialogAbout;
import com.sklerbidi.therapistmobileapp.R;

public class LoginActivity extends AppCompatActivity {

    FloatingActionButton btn_about;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findView();

        LoginFragment loginFragment = new LoginFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_login, loginFragment, "login")
                .addToBackStack(null)
                .commit();

        DBHelper dbHelper = new DBHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM agreement_table WHERE agreed = 1", null);
        if (!cursor.moveToFirst()) {
            DialogAbout dialogAbout = new DialogAbout();
            dialogAbout.setCancelable(false);
            dialogAbout.show(getSupportFragmentManager(), "about");
        }
        cursor.close();
        db.close();


        btn_about.setOnClickListener(v -> {
            DialogAbout dialogAbout = new DialogAbout();
            Bundle bundle = new Bundle();
            bundle.putString("title", "AGREEMENT");
            dialogAbout.setArguments(bundle);
            dialogAbout.setCancelable(false);
            dialogAbout.show(getSupportFragmentManager(), "about");
        });
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

    @Override
    public void onBackPressed() {
        // Do nothing
    }

    private void findView(){
        btn_about = findViewById(R.id.btn_about);
    }

}