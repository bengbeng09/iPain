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


public class TDialogAddPatient extends AppCompatDialogFragment {

    EditText et_patient_name;
    Button btn_back, btn_confirm;

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
        View view = getLayoutInflater().inflate(R.layout.dialog_add_patient,null);

        findView(view);

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();


                if(LoginActivity.isNotEmpty(new String[]{et_patient_name.getText().toString()})){
                    bundle.putString("name",et_patient_name.getText().toString());
                    getParentFragmentManager().setFragmentResult("request_patient", bundle);
                    getParentFragmentManager().beginTransaction().remove(TDialogAddPatient.this).commit();
                }else{
                    Toast.makeText(getActivity(), "Enter patient Name/ID",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction().remove(TDialogAddPatient.this).commit();
            }
        });

        builder.setView(view);
        return builder.create();
    }

    public void findView(View view){
        et_patient_name = view.findViewById(R.id.et_patient_name);
        btn_back = view.findViewById(R.id.btn_back);
        btn_confirm = view.findViewById(R.id.btn_confirm);
    }
}