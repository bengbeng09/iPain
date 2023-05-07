package com.sklerbidi.therapistmobileapp.CustomClass;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.sklerbidi.therapistmobileapp.LoginRegister.RegisterFragment;

import java.util.concurrent.TimeUnit;

public class PhoneAuthHelper {

    private String mVerificationId;
    private ProgressDialog progressDialog;

    public interface OnVerificationStateChangedListener {
        void onVerificationCompleted(FirebaseUser user);
        void onVerificationFailed(String message);
        void onVerificationCodeSent(boolean isCodeSent, String code);
    }

    public void verifyPhoneNumber(String phoneNumber, Activity activity, OnVerificationStateChangedListener listener) {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Sending Mobile OTP...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                activity,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        FirebaseAuth.getInstance().signInWithCredential(credential)
                                .addOnCompleteListener(task -> {
                                    progressDialog.dismiss();
                                    if (task.isSuccessful()) {
                                        listener.onVerificationCompleted(task.getResult().getUser());
                                    } else {
                                        listener.onVerificationFailed(task.getException().getMessage());
                                    }
                                });
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        progressDialog.dismiss();
                        listener.onVerificationFailed("Verification failed. Please try again.");
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        progressDialog.dismiss();
                        mVerificationId = verificationId;
                        boolean isCodeSent = true;
                        if (listener != null) {
                            listener.onVerificationCodeSent(isCodeSent, verificationId);
                        }
                    }
                });
    }

    public Task<AuthResult> verifyCode(String code, String verificationId) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.getFirebaseAuthSettings().setAppVerificationDisabledForTesting(false);
        Task<AuthResult> task = mAuth.signInWithCredential(credential);
        task.addOnCompleteListener(result -> {
            mAuth.getFirebaseAuthSettings().setAppVerificationDisabledForTesting(true);
        });
        return task;
    }
}