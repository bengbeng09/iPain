package com.sklerbidi.therapistmobileapp.Therapist;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentResultListener;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sklerbidi.therapistmobileapp.Dialog.PDialogAddActivity;
import com.sklerbidi.therapistmobileapp.R;


public class TPatientSessionActivity extends AppCompatActivity {

    FloatingActionButton add_activity;
    Button btn_back;
    LinearLayout container_activity;
    TextView tv_session_name, tv_session_id;
    public Bundle getBundle = null;
    String name = "", user_code = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.t_patient_session);

        findView();

        Bundle extras = getIntent().getExtras();


        if (extras != null) {
            name = extras.getString("first_name") + " " + extras.getString("last_name") ;
            user_code = extras.getString("user_code");
        }

        tv_session_name.setText(name.toUpperCase());
        tv_session_id.setText(user_code);


        getSupportFragmentManager().setFragmentResultListener("request_activity", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                String id = bundle.getString("id");
                String name = bundle.getString("name");

                add_card(id, name);
            }
        });

        add_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PDialogAddActivity menuActivity = new PDialogAddActivity();
                menuActivity.setCancelable(false);
                menuActivity.show(getSupportFragmentManager(), "add activity");
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void add_card(String id, String name){
        View view = getLayoutInflater().inflate(R.layout.layout_card_big, null);

        TextView nameView = view.findViewById(R.id.activity_name);

        String cardDetails = name.toUpperCase();
        nameView.setText(cardDetails);

        container_activity.addView(view);
    }

    public void findView(){
        tv_session_name = findViewById(R.id.tv_session_name);
        tv_session_id = findViewById(R.id.tv_session_id);
        add_activity = findViewById(R.id.btn_add_activity);
        btn_back = findViewById(R.id.btn_back);
        container_activity = findViewById(R.id.container_activity);
    }
}