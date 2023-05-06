package com.sklerbidi.therapistmobileapp.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sklerbidi.therapistmobileapp.ActivityNavigation;
import com.sklerbidi.therapistmobileapp.R;

public class TDialogProof  extends AppCompatDialogFragment {

    LinearLayout container_proof;
    Button btn_ok;
    String user_code, activity_name;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
        View view = getLayoutInflater().inflate(R.layout.dialog_proof,null);

        findView(view);

        Bundle args = getArguments();
        if (args != null) {
            user_code = args.getString("user_code");
            activity_name = args.getString("activity");

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference proofRef = storageRef.child("users").child(user_code).child("therapists").child(ActivityNavigation.user_code).child("activities").child(activity_name).child("completion_proof");

            proofRef.listAll().addOnSuccessListener(listResult -> {

                if (!listResult.getItems().isEmpty()) {
                    if(getActivity() != null){
                        for (StorageReference fileRef : listResult.getItems()) {
                            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {

                                ImageView imageView = new ImageView(getActivity());
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                                params.setMargins(0, 0, 0, 15);
                                imageView.setLayoutParams(params);

                                Glide.with(getActivity())
                                        .load(uri)
                                        .transition(DrawableTransitionOptions.withCrossFade())
                                        .into(imageView);
                                container_proof.addView(imageView);

                            });
                        }
                    }
                } else {

                }
            }).addOnFailureListener(e -> {

            });
        }


        btn_ok.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction().remove(TDialogProof.this).commit();
        });

        builder.setView(view);
        return builder.create();
    }

    private void findView(View view) {
        btn_ok = view.findViewById(R.id.btn_ok);
        container_proof = view.findViewById(R.id.container_proof);
    }
}