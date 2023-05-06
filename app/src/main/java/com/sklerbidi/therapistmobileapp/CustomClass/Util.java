package com.sklerbidi.therapistmobileapp.CustomClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.sklerbidi.therapistmobileapp.R;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.concurrent.atomic.AtomicInteger;

public class Util {

    public static void createPopUpWindows(Context context, ViewGroup viewGroup, String[] messages, int[] gravities, int count) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popUpView = inflater.inflate(R.layout.pop_up_manual, viewGroup, false);

        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        boolean focusable = true;

        PopupWindow[] popupWindows = new PopupWindow[count];
        for (int i = 0; i < count; i++) {
            popupWindows[i] = new PopupWindow(popUpView, width, height, focusable);
            popupWindows[i].setAnimationStyle(R.anim.fade_in);
        }
        showPopUpWindow(context, viewGroup, messages, gravities, popupWindows, 0, count);
    }

    private static void showPopUpWindow(Context context, ViewGroup viewGroup, String[] messages, int[] gravities, PopupWindow[] popupWindows, int index, int count) {
        if (index >= popupWindows.length) {
            return;
        }

        PopupWindow popupWindow = popupWindows[index];

        // calculate X and Y coordinates based on gravity
        int gravity = gravities[index];

        popupWindow.showAtLocation(viewGroup, gravity, 0, 0);

        Button btn_ok = popupWindow.getContentView().findViewById(R.id.ok_button);
        TextView text = popupWindow.getContentView().findViewById(R.id.message_textview);
        LinearLayout messageBox = popupWindow.getContentView().findViewById(R.id.message_box);

        text.setText(messages[index]);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) messageBox.getLayoutParams();

        params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.removeRule(RelativeLayout.CENTER_IN_PARENT);
        params.removeRule(RelativeLayout.ALIGN_PARENT_TOP);

        if (gravity == Gravity.BOTTOM) {
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        } else if (gravity == Gravity.CENTER) {
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
        } else if (gravity == Gravity.TOP) {
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        }

        messageBox.setLayoutParams(params);

        AtomicInteger counter = new AtomicInteger();
        btn_ok.setOnClickListener(v -> {
            Log.wtf("wtf", counter.get() + " : " + count);
            if (counter.get() == count - 1) {
                popupWindow.setAnimationStyle(R.anim.fade_out);
                popupWindow.dismiss();
            } else {
                text.setText(messages[(index + 1) % messages.length]);
                int nextGravity = gravities[(index + 1) % gravities.length];
                params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                params.removeRule(RelativeLayout.CENTER_IN_PARENT);
                params.removeRule(RelativeLayout.ALIGN_PARENT_TOP);

                if (nextGravity == Gravity.BOTTOM) {
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                } else if (nextGravity == Gravity.CENTER) {
                    params.addRule(RelativeLayout.CENTER_IN_PARENT);
                } else if (nextGravity == Gravity.TOP) {
                    params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                }
                messageBox.setLayoutParams(params);
            }
            counter.getAndIncrement();
        });
    }

    public interface OnManualStatusCheckedListener {
        void onManualStatusChecked(boolean isManualEnabled);
    }

    public static String capitalizeWords(String text) {

        if(text == null){
            return null;
        }
        StringBuilder sb = new StringBuilder();
        String[] words = text.toLowerCase().split("\\s+");
        for (String word : words) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)));
                sb.append(word.substring(1));
                sb.append(" ");
            }
        }
        return sb.toString().trim();
    }

    public static void checkAndUpdateManualStatus(DatabaseReference databaseReference, String userCode, String manualToCheck, OnManualStatusCheckedListener listener) {
        databaseReference.child("users").child(userCode).child("manual").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DataSnapshot manualSnapshot = snapshot.child(manualToCheck);
                    if (manualSnapshot.exists()) {
                        Boolean isManualEnabled = manualSnapshot.getValue(Boolean.class);
                        if (isManualEnabled != null && isManualEnabled) {
                            // manual is enabled
                            listener.onManualStatusChecked(true);
                        } else {
                            // manual is disabled, set it to true and return false
                            databaseReference.child("users").child(userCode).child("manual").child(manualToCheck).setValue(true);
                            listener.onManualStatusChecked(false);
                        }
                    } else {
                        // manual does not exist, set it to true and return false
                        databaseReference.child("users").child(userCode).child("manual").child(manualToCheck).setValue(true);
                        listener.onManualStatusChecked(false);
                    }
                } else {
                    // "manual" child does not exist, create it and set manualToCheck to true
                    databaseReference.child("users").child(userCode).child("manual").child(manualToCheck).setValue(true);
                    listener.onManualStatusChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // onCancelled implementation
            }
        });
    }
}
