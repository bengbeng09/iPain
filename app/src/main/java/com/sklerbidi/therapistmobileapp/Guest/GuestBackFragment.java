/*
This class represents the back side of a human body.
It allows the user to select a body part and navigate to a corresponding exercise activity.
 */

package com.sklerbidi.therapistmobileapp.Guest;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.sklerbidi.therapistmobileapp.R;

public class GuestBackFragment extends Fragment {

    ImageButton btn_right_upper_back, btn_left_upper_back, btn_middle_upper_back, btn_lower_back, btn_front_figure;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.g_back, container, false);

        findView(view);

        btn_right_upper_back.setOnClickListener(v -> {
            newActivity("upper back");
        });

        btn_left_upper_back.setOnClickListener(v -> {
            newActivity("upper back");
        });

        btn_middle_upper_back.setOnClickListener(v -> {
            newActivity("upper back");
        });

        btn_lower_back.setOnClickListener(v -> {
            newActivity("lower back");
        });

        btn_front_figure.setOnClickListener(v -> {
            GuestFrontFragment guestFrontFragment = new GuestFrontFragment();
            if(getActivity()!= null){
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left) // add the animations
                        .replace(((ViewGroup)getView().getParent()).getId(), guestFrontFragment, "front")
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

    private void newActivity(String text){
        Intent intent = new Intent(getActivity(), GuestExerciseActivity.class);
        intent.putExtra("type", text);
        startActivity(intent);
        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void findView(View view) {
        btn_right_upper_back = view.findViewById(R.id.btn_right_upper_back);
        btn_left_upper_back = view.findViewById(R.id.btn_left_upper_back);
        btn_middle_upper_back = view.findViewById(R.id.btn_middle_upper_back);
        btn_lower_back = view.findViewById(R.id.btn_lower_back);
        btn_front_figure = view.findViewById(R.id.btn_front_figure);
    }
}