package com.sklerbidi.therapistmobileapp.Menu;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sklerbidi.therapistmobileapp.R;

public class MenuSettings extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.menu_settings, container, false);

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

        return view;
    }
}