package com.sklerbidi.therapistmobileapp.Dialog;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.sklerbidi.therapistmobileapp.LoginRegister.LoginActivity;
import com.sklerbidi.therapistmobileapp.R;

import java.io.IOException;


public class PDialogAddActivity extends AppCompatDialogFragment {

    private static final int PICK_IMAGE_REQUEST = 1001;
    EditText et_activity,et_repetition, et_hold, et_complete, et_link, et_therapist;
    ImageView image;
    Button btn_back, btn_confirm, btn_upload;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
        View view = getLayoutInflater().inflate(R.layout.dialog_patient_add_activity,null);

        findView(view);

        btn_upload.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        btn_confirm.setOnClickListener(v -> {
            Bundle bundle = new Bundle();

            if(LoginActivity.isNotEmpty(new String[]{et_activity.getText().toString()})){
                bundle.putString("name",et_activity.getText().toString());
                getParentFragmentManager().setFragmentResult("request_activity", bundle);
                getParentFragmentManager().beginTransaction().remove(PDialogAddActivity.this).commit();
            }else{
                Toast.makeText(getActivity(), "Fill up all fields",
                        Toast.LENGTH_LONG).show();
            }
        });

        btn_back.setOnClickListener(v -> getParentFragmentManager().beginTransaction().remove(PDialogAddActivity.this).commit());

        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                image.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void findView(View view){
        et_activity = view.findViewById(R.id.et_activity_name);
        et_repetition = view.findViewById(R.id.et_repetition);
        et_hold = view.findViewById(R.id.et_hold);
        et_complete = view.findViewById(R.id.et_complete);
        et_link = view.findViewById(R.id.et_link);
        et_therapist = view.findViewById(R.id.et_notes);
        btn_back = view.findViewById(R.id.btn_back);
        btn_confirm = view.findViewById(R.id.btn_add);
        btn_upload = view.findViewById(R.id.btn_upload);
        image = view.findViewById(R.id.image_view);
    }
}