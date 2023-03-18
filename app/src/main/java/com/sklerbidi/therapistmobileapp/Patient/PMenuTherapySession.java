/*
This class is used the patients therapy sessions and allows them to add and view their therapists.
 */

package com.sklerbidi.therapistmobileapp.Patient;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sklerbidi.therapistmobileapp.ActivityNavigation;
import com.sklerbidi.therapistmobileapp.LoginRegister.LoginActivity;
import com.sklerbidi.therapistmobileapp.R;

public class PMenuTherapySession extends Fragment {

    Button btn_submit;
    TextView tv_welcome, tv_return;
    EditText et_therapist_code;
    LinearLayout add_therapist, container_therapist;
    RelativeLayout therapists;
    FloatingActionButton btn_add_therapist;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://student-theses-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.menu_therapy_session, container, false);

        findView(view);
        tv_welcome.setText("Welcome, " + cap(ActivityNavigation.firstname));
        set_card();

        databaseReference.child("users").child(ActivityNavigation.user_code).child("therapists").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    therapists.setVisibility(View.VISIBLE);
                    add_therapist.setVisibility(View.GONE);
                }else{
                    therapists.setVisibility(View.GONE);
                    add_therapist.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        tv_return.setOnClickListener( v -> {

            Animation slideOutAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_left);
            add_therapist.startAnimation(slideOutAnim);
            add_therapist.setVisibility(View.GONE);

            Animation slideInAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_right);
            therapists.startAnimation(slideInAnim);
            therapists.setVisibility(View.VISIBLE);

        });

        btn_add_therapist.setOnClickListener(v -> {

            Animation slideOutAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_right);
            therapists.startAnimation(slideOutAnim);
            therapists.setVisibility(View.GONE);

            Animation slideInAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_left);
            add_therapist.startAnimation(slideInAnim);
            add_therapist.setVisibility(View.VISIBLE);

        });

        btn_submit.setOnClickListener(v -> {
            String therapist_code = et_therapist_code.getText().toString().toUpperCase();
            if(LoginActivity.isNotEmpty(new String[]{et_therapist_code.getText().toString()})){

                if(getContext() != null & !LoginActivity.isNetworkConnected(getContext())){
                    Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }

                databaseReference.child("users").child(therapist_code).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {

                                databaseReference.child("users").child(ActivityNavigation.user_code).child("therapists").child(therapist_code).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (!snapshot.exists()) {
                                            String user_type = dataSnapshot.child("user_type").getValue(String.class);

                                            if(user_type != null && user_type.equals("Clinic Therapist")){
                                                databaseReference.child("users").child(ActivityNavigation.user_code).child("therapists").child(therapist_code).child("status").setValue("active");
                                                databaseReference.child("users").child(therapist_code).child("patients").child(ActivityNavigation.user_code).child("status").setValue("active");

                                                Animation slideOutAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_left);
                                                add_therapist.startAnimation(slideOutAnim);
                                                add_therapist.setVisibility(View.GONE);

                                                Animation slideInAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_right);
                                                therapists.startAnimation(slideInAnim);
                                                therapists.setVisibility(View.VISIBLE);

                                                set_card();

                                                et_therapist_code.setText(null);
                                                Toast.makeText(getActivity(), "Therapist added successfully", Toast.LENGTH_SHORT).show();

                                            }else{
                                                Toast.makeText(getActivity(), "Entered code is not a therapist", Toast.LENGTH_SHORT).show();
                                            }

                                        }else{
                                            Toast.makeText(getActivity(), "Therapist already added", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            } else {
                                Toast.makeText(getActivity(), "Invalid therapist code", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle error here
                        }
                    });
            }else{
                Toast.makeText(getActivity(), "Enter Therapist Code",
                        Toast.LENGTH_SHORT).show();
            }
        });

        return view;

    }

    private void set_card(){
        container_therapist.removeAllViews();
        databaseReference.child("users").child(ActivityNavigation.user_code).child("therapists").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String therapist_user_code = ds.getKey();

                    if(therapist_user_code != null){
                        databaseReference.child("users").child(therapist_user_code).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                String first_name = snapshot.child("first_name").getValue(String.class);
                                String last_name = snapshot.child("last_name").getValue(String.class);
                                String clinic = snapshot.child("clinic_name").getValue(String.class);

                                if (first_name != null && last_name != null) {
                                    add_card(first_name, last_name, therapist_user_code, clinic);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Handle error here
                            }
                        });
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void add_card(String firstname, String lastname, String user_code, String clinic){
        View view = getLayoutInflater().inflate(R.layout.layout_card_remove, null);

        TextView nameView = view.findViewById(R.id.name);
        Button remove = view.findViewById(R.id.btnRemove);
        Button show = view.findViewById(R.id.btnView);
        String cardDetails = firstname.toUpperCase() + " " + lastname.toUpperCase();
        nameView.setText(cardDetails);

        show.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PTherapistLoadingActivity.class);
            intent.putExtra("name", cardDetails);
            intent.putExtra("code", user_code);
            intent.putExtra("clinic", clinic);
            startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        remove.setOnClickListener(v -> {

            databaseReference.child("users").child(ActivityNavigation.user_code).child("password").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    String password = snapshot.getValue(String.class);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);

                    float titleTextSize = 1.1f;
                    float messageTextSize = 1.1f;
                    final EditText passwordInput = new EditText(getActivity());

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

                    params.setMargins(30, 0, 30, 0);

                    passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordInput.setPadding(20, 0, 20, 0);
                    passwordInput.setHeight(45);
                    passwordInput.setGravity(Gravity.CENTER_VERTICAL);
                    passwordInput.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    passwordInput.setTextSize(12);
                    passwordInput.setLayoutParams(params);
                    passwordInput.setBackground(getResources().getDrawable(R.drawable.button_round_outlined_red));

                    LinearLayout linearLayout = new LinearLayout(getActivity());
                    linearLayout.setLayoutParams(params);
                    linearLayout.addView(passwordInput);

                    SpannableStringBuilder titleBuilder = new SpannableStringBuilder("Remove Therapist");
                    titleBuilder.setSpan(new RelativeSizeSpan(titleTextSize/ getResources().getDisplayMetrics().scaledDensity), 0, titleBuilder.length(), 0);

                    SpannableStringBuilder messageBuilder = new SpannableStringBuilder("This will remove therapist and all their associated activities. \n \nEnter account password for confirmation:\n");
                    messageBuilder.setSpan(new RelativeSizeSpan(messageTextSize/ getResources().getDisplayMetrics().scaledDensity), 0, messageBuilder.length(), 0);

                    builder.setView(linearLayout)
                            .setMessage(messageBuilder)
                            .setTitle(titleBuilder)
                            .setPositiveButton("Delete", (dialog, id) -> {
                                String input = passwordInput.getText().toString();
                                if (input.equals(password)) {
                                    // Password is correct, perform deletion
                                    databaseReference.child("users").child(ActivityNavigation.user_code).child("therapists").child(user_code).removeValue()
                                            .addOnSuccessListener(aVoid -> Log.wtf("wtf", "success"))
                                            .addOnFailureListener(e -> Log.wtf("wtf", "failed:" + e));

                                    databaseReference.child("users").child(user_code).child("patients").child(ActivityNavigation.user_code).removeValue()
                                            .addOnSuccessListener(aVoid -> Log.wtf("wtf", "success"))
                                            .addOnFailureListener(e -> Log.wtf("wtf", "failed:" + e));

                                    Toast.makeText(getActivity(), "Therapist removed successfully", Toast.LENGTH_SHORT).show();
                                    set_card();
                                } else {
                                    // Password is incorrect, show error message
                                    Toast.makeText(getActivity(), "Incorrect password. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(), "Internal Error", Toast.LENGTH_SHORT).show();

                }
            });

        });

        container_therapist.addView(view);
        view.setTranslationY(-100f);
        view.setAlpha(0f);
        view.animate().translationYBy(100f).alpha(1f).setDuration(500);
    }

    public String cap(String originalString) {
        String[] words = originalString.split(" ");
        StringBuilder capitalizedString = new StringBuilder();
        for (String word : words) {
            capitalizedString.append(word.substring(0, 1).toUpperCase() + word.substring(1) + " ");
        }
        return capitalizedString.toString().trim();
    }

    private void findView(View view) {
        tv_return = view.findViewById(R.id.tv_return);
        btn_submit = view.findViewById(R.id.btn_submit);
        et_therapist_code = view.findViewById(R.id.et_therapist_code);
        tv_welcome = view.findViewById(R.id.tv_welcome);
        therapists = view.findViewById(R.id.view_therapists);
        add_therapist = view.findViewById(R.id.view_add_therapist);
        btn_add_therapist = view.findViewById(R.id.btn_add_therapist);
        container_therapist = view.findViewById(R.id.container_therapist);
    }
}