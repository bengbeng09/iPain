/*
This class represents the front side of a human body.
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

public class GuestFrontFragment extends Fragment {

    ImageButton btn_right_foot, btn_left_foot, btn_right_knee, btn_left_knee, btn_back_figure;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.g_front, container, false);

        findView(view);

        btn_left_foot.setOnClickListener(v -> newActivity("foot"));

        btn_right_foot.setOnClickListener(v -> newActivity("foot"));

        btn_left_knee.setOnClickListener(v -> newActivity("knee"));

        btn_right_knee.setOnClickListener(v -> newActivity("knee"));

        btn_back_figure.setOnClickListener(v -> {
            GuestBackFragment guestBackFragment = new GuestBackFragment();
            if(getActivity()!= null){
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right) // add the animations
                        .replace(((ViewGroup)getView().getParent()).getId(), guestBackFragment, "back")
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
        btn_left_foot = view.findViewById(R.id.btn_left_foot);
        btn_right_foot = view.findViewById(R.id.btn_right_foot);
        btn_right_knee = view.findViewById(R.id.btn_right_knee);
        btn_left_knee = view.findViewById(R.id.btn_left_knee);
        btn_back_figure = view.findViewById(R.id.btn_back_figure);
    }
}