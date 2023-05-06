
/*
This class sets up a dashboard view for the user, depending on the type of user.
The view displays different information for therapists and patients, and retrieves
data from a Firebase Realtime Database.
 */

package com.sklerbidi.therapistmobileapp.Menu;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sklerbidi.therapistmobileapp.ActivityNavigation;
import com.sklerbidi.therapistmobileapp.CustomClass.ReminderBroadcast;
import com.sklerbidi.therapistmobileapp.CustomClass.Util;
import com.sklerbidi.therapistmobileapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MenuDashboard extends Fragment {

    LinearLayout container_therapist;
    LinearLayout container_patient;
    LinearLayout container_patient_tasks;
    TextView tv_patients, tv_pending_task, tv_completed_task;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://student-theses-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNotificationChannel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_dashboard, container, false);

        findView(view);
        set_dashboard();
        return view;
    }

    private void set_dashboard(){
        switch (ActivityNavigation.user_type){
            case "Clinic Therapist":
                DatabaseReference patientsRef = databaseReference.child("users").child(ActivityNavigation.user_code).child("patients");
                patientsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int patient_count = (int) snapshot.getChildrenCount();
                        tv_patients.setText(String.valueOf(patient_count));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                container_therapist.setVisibility(View.VISIBLE);
                container_patient.setVisibility(View.GONE);

                break;
            case "Clinic Patient":
                databaseReference.child("users").child(ActivityNavigation.user_code).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int pending = 0, completed = 0;
                        for (DataSnapshot therapistSnapshot : snapshot.child("therapists").getChildren()) {
                            for (DataSnapshot activitySnapshot : therapistSnapshot.child("activities").getChildren()) {
                                String status = activitySnapshot.child("status").getValue(String.class);
                                if(status != null){
                                    if (status.equalsIgnoreCase("incomplete")) {
                                        pending++;
                                    }else{
                                        completed++;
                                    }
                                }
                            }
                        }
                        tv_pending_task.setText(String.valueOf(pending));
                        tv_completed_task.setText(String.valueOf(completed));
                    }



                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });

                container_therapist.setVisibility(View.GONE);
                container_patient.setVisibility(View.VISIBLE);
                set_card();
                break;
        }
    }

    private void set_card() {
        container_patient_tasks.removeAllViews();
        DatabaseReference therapistsRef = databaseReference.child("users").child(ActivityNavigation.user_code).child("therapists");
        therapistsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userCodeSnap : snapshot.getChildren()) {
                    String userCode = userCodeSnap.getKey();

                    if(userCode == null){
                        continue;
                    }
                    DatabaseReference activitiesRef = therapistsRef.child(userCode).child("activities");

                    Query query = activitiesRef.orderByChild("status").equalTo("incomplete");
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot activitySnap : snapshot.getChildren()) {
                                String activityName = activitySnap.getKey();
                                String repetition = activitySnap.child("repetition").getValue(String.class);
                                String hold = activitySnap.child("hold").getValue(String.class);
                                String complete = activitySnap.child("complete").getValue(String.class);
                                String link = activitySnap.child("link").getValue(String.class);
                                String note = activitySnap.child("note").getValue(String.class);
                                String status = activitySnap.child("status").getValue(String.class);
                                String date = activitySnap.child("date").getValue(String.class);
                                String time = activitySnap.child("time").getValue(String.class);
                                if (activityName != null && status != null) {
                                    add_card(activityName, repetition, hold, complete, link, note, status, userCode, date, time);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle error here
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error here
            }
        });
    }

    private void add_card(String name, String repetition, String hold, String complete, String link, String note, String status, String therapist_code, String date, String time){
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.layout_rounded_small_card, null);

        TextView nameView = view.findViewById(R.id.name);
        TextView schedule = view.findViewById(R.id.time);
        TextView details = view.findViewById(R.id.details);
        Button reminder = view.findViewById(R.id.btnAlarm);
        String activityName = name.toUpperCase();
        nameView.setText(activityName);
        String datetime = "";

        databaseReference.child("users").child(therapist_code).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String firstName = snapshot.child("first_name").getValue(String.class);
                String middleName = snapshot.child("middle_name").getValue(String.class);
                String lastName = snapshot.child("last_name").getValue(String.class);
                String clinicName = snapshot.child("clinic_name").getValue(String.class);

                if(middleName != null && !middleName.equals("")){
                    middleName = (middleName.charAt(0) + ".").toUpperCase();
                }

                String therapistDetails = Util.capitalizeWords(clinicName) + ": " + Util.capitalizeWords(lastName) + ", " + Util.capitalizeWords(firstName) + " "+ middleName;
                details.setText(therapistDetails);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error here
            }
        });

        if((date != null && !date.equals(""))){
            schedule.setVisibility(View.VISIBLE);
            datetime = "Schedule: " + date;

            if((time != null && !time.equals(""))){

                datetime += " - " + time;
            }
            schedule.setText(datetime);
        }

        reminder.setOnClickListener(v -> {
            // Get the current date and time
            Calendar calendar = Calendar.getInstance();

            // Show the date picker dialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), (wew, year, monthOfYear, dayOfMonth) -> {
                // Set the selected date on the calendar
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                // Show the time picker dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), (view1, hourOfDay, minute) -> {
                    // Set the selected time on the calendar
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    calendar.set(Calendar.SECOND, 0);

                    // Create a Date object from the selected date and time
                    Date simpleDate = calendar.getTime();
                    Intent intent = new Intent(getActivity(), ReminderBroadcast.class);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0 , intent, 0);

                    if(getActivity() != null){
                        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                        long alarmTime = simpleDate.getTime();
                        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);

                        SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy hh:mm a", Locale.getDefault());
                        String dateString = format.format(simpleDate);
                        Toast.makeText(getActivity(), "Reminder set at " + dateString, Toast.LENGTH_SHORT).show();
                    }

                    // Set the alarm for the selected date and time
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
                timePickerDialog.show();
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });


        /*
        button.setOnClickListener(v -> {
            Intent intent = new Intent(PTherapistSessionActivity.this, PTasksActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("repetition", repetition);
            intent.putExtra("hold", hold);
            intent.putExtra("complete", complete);
            intent.putExtra("link", link);
            intent.putExtra("note", note);
            intent.putExtra("status", status);
            intent.putExtra("therapist", therapist_code);
            startActivityForResult(intent, REQUEST_CODE_SECOND_ACTIVITY);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

         */

        container_patient_tasks.addView(view);

        view.setTranslationY(-100f);
        view.setAlpha(0f);
        view.animate().translationYBy(100f).alpha(1f).setDuration(500);
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "IPainReminderChannel";
            String description = "Channel for IPain Reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("IPainNotif", name, importance);
            channel.setDescription(description);

            if(getActivity() != null){
                NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Util.checkAndUpdateManualStatus(databaseReference, ActivityNavigation.user_code, "manual_dashboard", isManualEnabled -> {
            boolean showManual = !isManualEnabled;
            if(showManual){
                manual();
            }
        });


    }

    public void manual(){
        if (getView() != null) {
            getView().post(() -> {
                if (getContext() != null && getActivity() != null) {
                    String[] messages;
                    int[] gravities;
                    int count;

                    switch (ActivityNavigation.user_type) {
                        case "Clinic Therapist":
                            messages = new String[]{"Welcome to iPain, " +  Util.capitalizeWords(ActivityNavigation.firstname)+  "! ",
                                                    "This is your dashboard, were you can check the number of your patients"};
                            gravities = new int[]{Gravity.CENTER, Gravity.CENTER};
                            count = 2;
                            break;
                        case "Clinic Patient":
                            // handle for clinic patient
                            messages = new String[]{"Welcome to iPain, " + Util.capitalizeWords(ActivityNavigation.firstname) +  "! ",
                                    "This is your dashboard, were you can check the number of your pending and completed tasks.",
                                    "You can also set reminder on pending tasks that will be listed in here."};
                            gravities = new int[]{Gravity.CENTER, Gravity.CENTER, Gravity.BOTTOM};
                            count = 3;
                            break;
                        default:
                            messages = new String[]{};
                            gravities = new int[]{};
                            count = 0;
                            break;
                    }

                    Util.createPopUpWindows(getContext(), (ViewGroup) getView(), messages, gravities, count);
                }
            });
        }
    }

    public void toast(String toast){
        Toast.makeText(getActivity(), toast, Toast.LENGTH_SHORT).show();
    }



    public void findView(View view){
        container_therapist = view.findViewById(R.id.container_therapist);
        container_patient = view.findViewById(R.id.container_patient);
        container_patient_tasks = view.findViewById(R.id.container_patients_tasks);
        tv_patients = view.findViewById(R.id.tv_patients);
        tv_pending_task = view.findViewById(R.id.tv_pending_task);
        tv_completed_task = view.findViewById(R.id.tv_completed_task);
    }
}