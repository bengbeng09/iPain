
/*
This class is responsible for displaying the UI of the guest feature, and provides the user with the ability to logout and exit the application.
 */

package com.sklerbidi.therapistmobileapp.Guest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sklerbidi.therapistmobileapp.ActivityNavigation;
import com.sklerbidi.therapistmobileapp.CustomClass.DBHelper;
import com.sklerbidi.therapistmobileapp.CustomClass.Util;
import com.sklerbidi.therapistmobileapp.LoginRegister.LoginActivity;
import com.sklerbidi.therapistmobileapp.R;

public class GuestActivity extends AppCompatActivity {

    Button btn_back;
    public static boolean hasSeenAdvice = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.g_activity);

        btn_back = findViewById(R.id.btn_back);

        GuestFrontFragment guestFrontFragment = new GuestFrontFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_login, guestFrontFragment, "login")
                .addToBackStack(null)
                .commit();

        btn_back.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        DBHelper dbHelper = new DBHelper(getApplicationContext());
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        db.delete("user_table", null, null);
                        db.close();

                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

    }

    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Exit?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    finishAffinity();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}