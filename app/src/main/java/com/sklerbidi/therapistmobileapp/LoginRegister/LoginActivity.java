package com.sklerbidi.therapistmobileapp.LoginRegister;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sklerbidi.therapistmobileapp.CustomClass.DBHelper;
import com.sklerbidi.therapistmobileapp.Dialog.DialogAbout;
import com.sklerbidi.therapistmobileapp.R;

import java.util.Objects;

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

        FragmentManager fragmentManager = this.getSupportFragmentManager();

        Fragment[] fragments = {
                fragmentManager.findFragmentByTag("login"), fragmentManager.findFragmentByTag("register")};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible()) {
                switch (Objects.requireNonNull(fragment.getTag())) {
                    case "login":
                        builder.setMessage("Exit?")
                                .setPositiveButton("Yes", (dialog, which) -> {
                                    this.finishAffinity();
                                })
                                .setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        break;
                    case "register":
                        builder.setMessage("Cancel Registration?")
                                .setPositiveButton("Yes", (dialog, which) -> {
                                   this.recreate();
                                })
                                .setNegativeButton("No", (dialog, which) -> dialog.dismiss());

                        break;
                }

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }
    }

    private void findView(){
        btn_about = findViewById(R.id.btn_about);
    }

}