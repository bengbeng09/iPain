package com.sklerbidi.therapistmobileapp.Therapist;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sklerbidi.therapistmobileapp.Dialog.TDialogAddPatient;
import com.sklerbidi.therapistmobileapp.R;


public class TMenuPatients extends Fragment {

    FloatingActionButton add_patient;
    LinearLayout container_patient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.menu_patients, container, false);

        findView(view);

        getParentFragmentManager().setFragmentResultListener("request_patient", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                String id = bundle.getString("id");
                String name = bundle.getString("name");

                add_card(id, name);
            }
        });

        add_patient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TDialogAddPatient TDialogAddPatient = new TDialogAddPatient();
                TDialogAddPatient.setCancelable(false);
                TDialogAddPatient.show(getActivity().getSupportFragmentManager(), "add patient");
            }
        });

        return view;
    }

    private void add_card(String id, String name){
        View view = getLayoutInflater().inflate(R.layout.layout_card, null);

        TextView nameView = view.findViewById(R.id.name);
        Button show = view.findViewById(R.id.btnView);

        String cardDetails = name.toUpperCase();
        nameView.setText(cardDetails);

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TPatientSessionActivity.class);
                intent.putExtra("x", name.toString());
                startActivity(intent);
            }
        });

        container_patient.addView(view);
    }

    public void findView(View view){
        add_patient = view.findViewById(R.id.btn_add_patient);
        container_patient = view.findViewById(R.id.container_patient);
    }
}