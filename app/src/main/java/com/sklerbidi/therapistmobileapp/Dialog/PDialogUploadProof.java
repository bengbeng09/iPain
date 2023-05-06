package com.sklerbidi.therapistmobileapp.Dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.sklerbidi.therapistmobileapp.R;

import java.util.ArrayList;

public class PDialogUploadProof extends AppCompatDialogFragment {

    Button btn_upload, btn_back, btn_submit;
    LinearLayout container_proof;

    private static final int PICK_IMAGE_REQUEST = 1002;
    ArrayList<Uri> selectedImages = new ArrayList<>();

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
        View view = getLayoutInflater().inflate(R.layout.dialog_upload_evidence,null);

        findView(view);

        btn_back.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction().remove(PDialogUploadProof.this).commit();
        });

        btn_upload.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        });

        btn_submit.setOnClickListener(v -> {

            if (selectedImages.isEmpty()) {
                Toast.makeText(getActivity(), "Select images to finish", Toast.LENGTH_SHORT).show();
                return;
            }

            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("images_list", selectedImages);
            getParentFragmentManager().setFragmentResult("request_evidence", bundle);
        });

        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        selectedImages = new ArrayList<>();
        container_proof.removeAllViews();

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if ( data != null && data.getClipData() != null && getActivity() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();

                    if (imageUri != null) {
                        selectedImages.add(imageUri);
                    }

                    ImageView imageView = new ImageView(getActivity());
                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    imageView.setImageURI(imageUri);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0, 16, 0, 0);

                    imageView.setLayoutParams(layoutParams);
                    container_proof.addView(imageView);
                }
            } else if (data != null && data.getData() != null && getActivity() != null) {
                Uri imageUri = data.getData();

                if (imageUri != null) {
                    selectedImages.add(imageUri);
                }

                ImageView imageView = new ImageView(getActivity());
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setImageURI(imageUri);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 16, 0, 0);

                imageView.setLayoutParams(layoutParams);

                container_proof.addView(imageView);
            }else{
                Toast.makeText(getActivity(), "Upload Image Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void findView(View view){
        btn_upload = view.findViewById(R.id.btn_upload);
        container_proof = view.findViewById(R.id.container_images);
        btn_back = view.findViewById(R.id.btn_back);
        btn_submit = view.findViewById(R.id.btn_submit);
    }
}
