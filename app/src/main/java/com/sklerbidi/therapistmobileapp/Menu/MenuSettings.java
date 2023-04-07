
/*
This this class provides a simple UI for the user to view their information and
perform common actions, logging out or changing their password.
 */

package com.sklerbidi.therapistmobileapp.Menu;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sklerbidi.therapistmobileapp.ActivityNavigation;
import com.sklerbidi.therapistmobileapp.CustomClass.DBHelper;
import com.sklerbidi.therapistmobileapp.Dialog.DialogChangePass;
import com.sklerbidi.therapistmobileapp.LoginRegister.LoginActivity;
import com.sklerbidi.therapistmobileapp.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class MenuSettings extends Fragment {
    TextView tv_user_id, tv_name, tv_email, tv_username;
    Button logout, change_password;
    LinearLayout layout_profile;
    ImageView change_profile;
    Bitmap bitmap;
    private static final int PICK_IMAGE_REQUEST = 1001;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.menu_settings, container, false);

        findView(view);
        tv_user_id.setText(ActivityNavigation.user_code);
        tv_name.setText(ActivityNavigation.firstname.toUpperCase() + " " + ActivityNavigation.lastname.toUpperCase());
        tv_username.setText(ActivityNavigation.username);
        tv_email.setText(ActivityNavigation.email);

        if(ActivityNavigation.user_type.equalsIgnoreCase("clinic therapist")){
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference imagesRef = storageRef.child("users").child(ActivityNavigation.user_code).child("profile");

            imagesRef.listAll().addOnSuccessListener(listResult -> {
                List<StorageReference> items = listResult.getItems();
                if (items.size() > 0) {
                    items.sort(Comparator.comparing(StorageReference::getName));

                    StorageReference latestProfileRef = items.get(items.size() - 1);

                    latestProfileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        if(getActivity()!=null){
                            Glide.with(getActivity())
                                    .load(uri)
                                    .transform(new CircleCrop())
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .into(change_profile);
                        }
                    });
                }else{
                    change_profile.setImageResource(R.drawable.ic_baseline_account_circle_24_black);
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(getActivity(), "Error retrieving profile, connection problem", Toast.LENGTH_SHORT).show();
            });

            layout_profile.setVisibility(View.VISIBLE);
        }

        getParentFragmentManager().setFragmentResultListener("request_info", this, (requestKey, bundle) -> {
            String user_type = bundle.getString("type");
            switch (user_type){
                case "Clinic Therapist":
                    break;
                case "Clinic Patient":
                    layout_profile.setVisibility(View.GONE);
                    break;
            }
        });

        change_profile.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        logout.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        DBHelper dbHelper = new DBHelper(getActivity());
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        db.delete("user_table", null, null);
                        db.close();
                        
                        startActivity(intent);
                        requireActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        });

        change_password.setOnClickListener( v -> {
            DialogChangePass dialogChangePass = new DialogChangePass();
            dialogChangePass.setCancelable(false);
            dialogChangePass.show(getParentFragmentManager(), "change pass");
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                if(bitmap != null){
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();
                    StorageReference imagesRef = storageRef.child("users").child(ActivityNavigation.user_code).child("profile");

                    String imageName = "profile_" + System.currentTimeMillis() + ".jpg";

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] newData = stream.toByteArray();

                    UploadTask uploadTask = imagesRef.child(imageName).putBytes(newData);

                    uploadTask.addOnSuccessListener(taskSnapshot -> {

                        Glide.with(getActivity())
                                .load(bitmap)
                                .transform(new CircleCrop())
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(change_profile);
                        Toast.makeText(getActivity(), "Profile uploaded successfully", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getActivity(), "Image upload failed for some reason", Toast.LENGTH_SHORT).show();
                    }).addOnProgressListener(snapshot -> {
                        // Track the progress of the image upload
                    });
                }else{
                    Toast.makeText(getActivity() , "Image upload failed, Bitmap is null", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void findView(View view){
        tv_user_id = view.findViewById(R.id.tv_id);
        tv_name = view.findViewById(R.id.tv_name);
        tv_username = view.findViewById(R.id.tv_username);
        tv_email = view.findViewById(R.id.tv_email);
        logout = view.findViewById(R.id.btn_logout);
        change_password = view.findViewById(R.id.btn_change_password);
        layout_profile = view.findViewById(R.id.layout_therapist_profile);
        change_profile = view.findViewById(R.id.img_settings_profile);

    }
}