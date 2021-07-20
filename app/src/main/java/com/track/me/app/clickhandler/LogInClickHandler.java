package com.track.me.app.clickhandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.track.me.app.MainActivity;
import com.track.me.app.R;
import com.track.me.app.activities.SignUpActivity;
import com.track.me.app.utils.DisplayViewUI;

import java.util.Objects;

public class LogInClickHandler {

    Button btnLogin;
    FirebaseAuth mAuth;
    FirebaseUser mCurrentUser;
    private Context context;
    private TextInputLayout txtEmail;
    private TextInputLayout txtPassword;
    private ProgressBar pbLoading;

    public LogInClickHandler() {
    }

    public LogInClickHandler(Context context, TextInputLayout txtEmail, TextInputLayout txtPassword,
                             ProgressBar pbLoading,
                             Button btnLogin) {
        this.context = context;
        this.txtEmail = txtEmail;
        this.txtPassword = txtPassword;
        this.pbLoading = pbLoading;
        this.btnLogin = btnLogin;
        mAuth = FirebaseAuth.getInstance();
    }

    public void gotoSignUp(View view) {
        view.getContext().startActivity(new Intent(view.getContext(), SignUpActivity.class));


    }


    public void validateInputs(final View view) {
        try {
            String email = Objects.requireNonNull(txtEmail.getEditText()).getText().toString();
            String password = Objects.requireNonNull(txtPassword.getEditText()).getText().toString();

            if (Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length() >= 8) {
                pbLoading.setVisibility(View.VISIBLE);

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener((Activity) context, task -> {
                    if (task.isSuccessful()) {
                        //if user's email is verified ..push to main
                        if (Objects.requireNonNull(mAuth.getCurrentUser()).isEmailVerified()) {
                            pbLoading.setVisibility(View.GONE);
                            view.getContext().startActivity(new Intent(context, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

                        } else { // let user verify email prompt

                            pbLoading.setVisibility(View.GONE);
                            new AlertDialog.Builder(context)
                                    .setMessage("Your email" + " " + email + " " + "\n" + "is not yet verified" + "\n" + "please  verify to continue")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .create()
                                    .show();


                        }

                    }


                }).addOnFailureListener(e -> DisplayViewUI.displayToast(context, "Error " + e.getMessage()));


            } else {

                pbLoading.setVisibility(View.VISIBLE);
                txtEmail.setErrorEnabled(false);
                txtPassword.setErrorEnabled(false);
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                pbLoading.setVisibility(View.GONE);
                txtEmail.setErrorEnabled(true);
                txtEmail.setError(context.getResources().getString(R.string.invalidEmail));
            }
            if (email.trim().isEmpty()) {
                pbLoading.setVisibility(View.GONE);
                txtEmail.setErrorEnabled(true);
                txtEmail.setError(context.getResources().getString(R.string.emailRequired));
            }
            if (password.trim().isEmpty()) {
                pbLoading.setVisibility(View.GONE);
                txtPassword.setErrorEnabled(true);
                txtPassword.setError(context.getResources().getString(R.string.passwordRequired));
            }
            if (password.length() < 8) {
                pbLoading.setVisibility(View.GONE);
                txtPassword.setErrorEnabled(true);
                txtPassword.setError(context.getResources().getString(R.string.passwordTooShort));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
