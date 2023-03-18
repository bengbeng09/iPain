/*
This is a custom dialog fragment class for the "About" dialog box in the therapist mobile app.
It displays the terms and conditions of the app, and provides the option to agree or decline.
 */

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


public class DialogAbout extends AppCompatDialogFragment {

    Button btn_agree;
    TextView tv_decline;
    TextView tv_agreement;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
        View view = getLayoutInflater().inflate(R.layout.dialog_about,null);

        findView(view);

        Bundle args = getArguments();
        if(args != null){
            tv_agreement.setText(args.getString("title"));
            btn_agree.setText("OK");
            tv_decline.setVisibility(View.GONE);
        }

        // Set the click listener for the agree button
        btn_agree.setOnClickListener(v -> {
            if(btn_agree.getText() != "OK"){
                // If the user agrees, insert the agreement status into the database
                ContentValues contentValues = new ContentValues();
                contentValues.put("agreed", 1);
                DBHelper dbHelper = new DBHelper(getContext());
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.insert("agreement_table", null, contentValues);
                db.close();
            }
            getParentFragmentManager().beginTransaction().remove(DialogAbout.this).commit();
        });

        // Set the click listener for the decline button
        tv_decline.setOnClickListener(v -> {
            if(getActivity() != null){
                getActivity().finishAffinity();
            }
        });

        builder.setView(view);
        return builder.create();
    }

    private void findView(View view) {
        btn_agree = view.findViewById(R.id.btn_agree);
        tv_decline = view.findViewById(R.id.tv_decline);
        tv_agreement = view.findViewById(R.id.tv_agreement);
    }
}