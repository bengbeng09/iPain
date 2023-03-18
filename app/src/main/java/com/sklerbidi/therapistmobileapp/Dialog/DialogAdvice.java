package com.sklerbidi.therapistmobileapp.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sklerbidi.therapistmobileapp.CustomClass.DBHelper;
import com.sklerbidi.therapistmobileapp.R;


public class DialogAdvice extends AppCompatDialogFragment {

    Button btn_continue;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
        View view = getLayoutInflater().inflate(R.layout.dialog_advice,null);

        findView(view);

        btn_continue.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction().remove(DialogAdvice.this).commit();
        });


        builder.setView(view);
        return builder.create();
    }

    private void findView(View view) {
        btn_continue = view.findViewById(R.id.btn_continue);
    }
}