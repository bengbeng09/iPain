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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sklerbidi.therapistmobileapp.ActivityNavigation;
import com.sklerbidi.therapistmobileapp.R;

public class MenuDashboard extends Fragment {

    LinearLayout container_therapist;
    LinearLayout container_patient;
    TextView tv_patients, tv_pending_task, tv_completed_task;
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
            case "Clinic Therapist":
                DatabaseReference patientsRef = databaseReference.child("users").child(ActivityNavigation.user_code).child("patients");
                patientsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int patient_count = (int) snapshot.getChildrenCount();
                        tv_patients.setText(String.valueOf(patient_count));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                container_therapist.setVisibility(View.VISIBLE);
                container_patient.setVisibility(View.GONE);

                break;
            case "Clinic Patient":
                databaseReference.child("users").child(ActivityNavigation.user_code).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int pending = 0, completed = 0;
                        for (DataSnapshot therapistSnapshot : snapshot.child("therapists").getChildren()) {
                            for (DataSnapshot activitySnapshot : therapistSnapshot.child("activities").getChildren()) {
                                String status = activitySnapshot.child("status").getValue(String.class);
                                if(status != null){
                                    if (status.equalsIgnoreCase("incomplete")) {
                                        pending++;
                                    }else{
                                        completed++;
                                    }
                                }
                            }
                        }
                        tv_pending_task.setText(String.valueOf(pending));
                        tv_completed_task.setText(String.valueOf(completed));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });
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
        tv_patients = view.findViewById(R.id.tv_patients);
        tv_pending_task = view.findViewById(R.id.tv_pending_task);
        tv_completed_task = view.findViewById(R.id.tv_completed_task);
    }
}