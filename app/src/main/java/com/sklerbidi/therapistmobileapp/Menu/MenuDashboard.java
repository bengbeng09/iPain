package com.sklerbidi.therapistmobileapp.Menu;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sklerbidi.therapistmobileapp.R;

public class MenuDashboard extends Fragment {

    LinearLayout container_therapist;
    LinearLayout container_patient;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.menu_dashboard, container, false);

        findView(view);

        getParentFragmentManager().setFragmentResultListener("request_type", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                String user_type = bundle.getString("type");
                switch (user_type){
                    case "Regular User":
                        container_therapist.setVisibility(View.GONE);
                        container_patient.setVisibility(View.GONE);
                        break;
                    case "Clinic Therapist":
                        container_therapist.setVisibility(View.VISIBLE);
                        container_patient.setVisibility(View.GONE);
                        break;
                    case "Clinic Patient":
                        container_therapist.setVisibility(View.GONE);
                        container_patient.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        return view;
    }

    public void toast(String toast){
        Toast.makeText(getActivity(), toast, Toast.LENGTH_SHORT).show();
    }

    public void findView(View view){
        container_therapist = view.findViewById(R.id.container_therapist);
        container_patient = view.findViewById(R.id.container_patient);
    }
}