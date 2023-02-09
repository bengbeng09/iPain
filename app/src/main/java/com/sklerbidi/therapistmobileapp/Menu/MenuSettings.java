package com.sklerbidi.therapistmobileapp.Menu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.sklerbidi.therapistmobileapp.LoginRegister.LoginActivity;
import com.sklerbidi.therapistmobileapp.Patient.PTherapistLoadingActivity;
import com.sklerbidi.therapistmobileapp.Patient.PTherapistSessionActivity;
import com.sklerbidi.therapistmobileapp.R;

public class MenuSettings extends Fragment {
    TextView tv_user_id, tv_name;
    Button logout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.menu_settings, container, false);

        findView(view);
        tv_user_id.setText(ActivityNavigation.user_code);
        tv_name.setText(ActivityNavigation.firstname.toUpperCase() + " " + ActivityNavigation.lastname.toUpperCase());

        getParentFragmentManager().setFragmentResultListener("request_info", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                String user_type = bundle.getString("type");
                switch (user_type){
                    case "Regular User":
                        break;
                    case "Clinic Therapist":
                        break;
                    case "Clinic Patient":
                        break;
                }
            }
        });

        logout.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Are you sure?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        });

        return view;
    }

    private void findView(View view){
        tv_user_id = view.findViewById(R.id.tv_id);
        tv_name = view.findViewById(R.id.tv_name);
        logout = view.findViewById(R.id.btn_logout);
    }
}