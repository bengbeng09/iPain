
/*
This this class provides a simple UI for the user to view their information and
perform common actions, logging out or changing their password.
 */

package com.sklerbidi.therapistmobileapp.Menu;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sklerbidi.therapistmobileapp.ActivityNavigation;
import com.sklerbidi.therapistmobileapp.CustomClass.DBHelper;
import com.sklerbidi.therapistmobileapp.Dialog.DialogChangePass;
import com.sklerbidi.therapistmobileapp.LoginRegister.LoginActivity;
import com.sklerbidi.therapistmobileapp.R;

public class MenuSettings extends Fragment {
    TextView tv_user_id, tv_name, tv_email, tv_username;
    Button logout, change_password;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.menu_settings, container, false);

        findView(view);
        tv_user_id.setText(ActivityNavigation.user_code);
        tv_name.setText(ActivityNavigation.firstname.toUpperCase() + " " + ActivityNavigation.lastname.toUpperCase());
        tv_username.setText(ActivityNavigation.username);
        tv_email.setText(ActivityNavigation.email);

        getParentFragmentManager().setFragmentResultListener("request_info", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                String user_type = bundle.getString("type");
                switch (user_type){
                    case "Clinic Therapist":
                        break;
                    case "Clinic Patient":
                        break;
                }
            }
        });

        logout.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        DBHelper dbHelper = new DBHelper(getActivity());
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        db.delete("user_table", null, null);
                        db.close();
                        
                        startActivity(intent);
                        requireActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        });

        change_password.setOnClickListener( v -> {
            DialogChangePass dialogChangePass = new DialogChangePass();
            dialogChangePass.setCancelable(false);
            dialogChangePass.show(getParentFragmentManager(), "change pass");
        });

        return view;
    }

    private void findView(View view){
        tv_user_id = view.findViewById(R.id.tv_id);
        tv_name = view.findViewById(R.id.tv_name);
        tv_username = view.findViewById(R.id.tv_username);
        tv_email = view.findViewById(R.id.tv_email);
        logout = view.findViewById(R.id.btn_logout);
        change_password = view.findViewById(R.id.btn_change_password);
    }
}