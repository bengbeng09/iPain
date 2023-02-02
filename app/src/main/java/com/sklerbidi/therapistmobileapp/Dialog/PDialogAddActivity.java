package com.sklerbidi.therapistmobileapp.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sklerbidi.therapistmobileapp.LoginRegister.LoginActivity;
import com.sklerbidi.therapistmobileapp.R;


public class PDialogAddActivity extends AppCompatDialogFragment {

    EditText et_activity,et_repetition, et_hold, et_complete, et_link, et_therapist;
    Button btn_back, btn_confirm;

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
        View view = getLayoutInflater().inflate(R.layout.dialog_patient_add_activity,null);

        findView(view);

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();


                if(LoginActivity.isNotEmpty(new String[]{et_activity.getText().toString()})){
                    bundle.putString("name",et_activity.getText().toString());
                    getParentFragmentManager().setFragmentResult("request_activity", bundle);
                    getParentFragmentManager().beginTransaction().remove(PDialogAddActivity.this).commit();
                }else{
                    Toast.makeText(getActivity(), "Fill up all fields",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction().remove(PDialogAddActivity.this).commit();
            }
        });

        builder.setView(view);
        return builder.create();
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
    }
}