package com.sklerbidi.therapistmobileapp.Patient;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sklerbidi.therapistmobileapp.R;

public class PMenuTherapySession extends Fragment {

    Button btn_submit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.menu_therapy_session, container, false);

        findView(view);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PTherapistsActivity.class);
                startActivity(intent);
            }
        });

        return view;

    }

    private void findView(View view) {
        btn_submit = view.findViewById(R.id.btn_submit);
    }
}