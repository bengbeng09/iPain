package com.sklerbidi.therapistmobileapp.Menu;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sklerbidi.therapistmobileapp.ActivityNavigation;
import com.sklerbidi.therapistmobileapp.R;

public class MenuDashboard extends Fragment {

    LinearLayout container_therapist;
    LinearLayout container_patient;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://student-theses-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();

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
        set_dashboard();
        return view;
    }

    private void set_dashboard(){
        switch (ActivityNavigation.user_type){
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

    public void toast(String toast){
        Toast.makeText(getActivity(), toast, Toast.LENGTH_SHORT).show();
    }

    public void findView(View view){
        container_therapist = view.findViewById(R.id.container_therapist);
        container_patient = view.findViewById(R.id.container_patient);
    }
}