
/*
This is a custom dialog fragment for adding an activity for a therapist's patient.
The dialog contains various EditText fields for entering details about the activity
such as its name, repetition, hold, completion, link, and therapist's note. It also
has an ImageView for uploading an image related to the activity.
 */
package com.sklerbidi.therapistmobileapp.Dialog;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.app.Dialog;
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
    String user_code;
    Bitmap bitmap;
    Button btn_back, btn_confirm, btn_upload;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
        View view = getLayoutInflater().inflate(R.layout.dialog_patient_add_activity,null);

        findView(view);

        getParentFragmentManager().setFragmentResultListener("set_activity", this, (requestKey, bundle) -> user_code = bundle.getString("user_code"));

        btn_confirm.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            final String activity_name = et_activity.getText().toString();
            final String activity_repetition = et_repetition.getText().toString();
            final String activity_hold = et_hold.getText().toString();
            final String activity_complete = et_complete.getText().toString();
            final String activity_link = et_link.getText().toString();
            final String activity_note = et_therapist.getText().toString();


            if(LoginActivity.isNotEmpty(new String[]{activity_name, activity_repetition, activity_hold, activity_complete, activity_link, activity_note})){

                bundle.putString("a_name", activity_name);
                bundle.putString("a_repetition", activity_repetition);
                bundle.putString("a_hold", activity_hold);
                bundle.putString("a_complete", activity_complete);
                bundle.putString("a_link", activity_link);
                bundle.putString("a_note", activity_note);

                if(bitmap != null){
                    bundle.putParcelable("a_image", bitmap);
                }
                getParentFragmentManager().setFragmentResult("request_activity", bundle);
                getParentFragmentManager().beginTransaction().remove(PDialogAddActivity.this).commit();

            }else{
                Toast.makeText(getActivity(), "Fill up all fields",
                        Toast.LENGTH_LONG).show();
            }
        });

        btn_upload.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
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
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
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