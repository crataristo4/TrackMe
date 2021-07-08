package com.track.me.app.clickhandler;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.track.me.app.R;
import com.track.me.app.activities.LoginActivity;
import com.track.me.app.utils.DisplayViewUI;

import java.util.Objects;

public class SignUpClickHandler {

    Button btnSignUp;
    FirebaseAuth mAuth;
    private Context context;
    private TextInputLayout txtEmail;
    private TextInputLayout txtPassword;
    private TextInputLayout txtConfirmPassword;
    private ProgressBar pbLoading;


    public SignUpClickHandler() {
    }

    public SignUpClickHandler(Context context, TextInputLayout txtEmail, TextInputLayout txtPassword, TextInputLayout txtConfirmPassword,
                              ProgressBar pbLoading,
                              Button btnSignUp) {
        this.context = context;
        this.txtEmail = txtEmail;
        this.txtPassword = txtPassword;
        this.txtConfirmPassword = txtConfirmPassword;
        this.pbLoading = pbLoading;
        this.btnSignUp = btnSignUp;
        mAuth = FirebaseAuth.getInstance();
    }


    public void signUp(View view) {
        String email = Objects.requireNonNull(txtEmail.getEditText()).getText().toString();
        String password = Objects.requireNonNull(txtPassword.getEditText()).getText().toString();
        String confirmPassword = Objects.requireNonNull(txtConfirmPassword.getEditText()).getText().toString();

        if (Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length() >= 8 && confirmPassword.equals(password)) {
            pbLoading.setVisibility(View.VISIBLE);

            //register user and send email verification code
            mAuth.createUserWithEmailAndPassword(email, password).addOnFailureListener(e -> DisplayViewUI.displayToast(context, "Error " + e.getMessage())).addOnCompleteListener((Activity) context, task -> {

                if (task.isSuccessful()) {
                    pbLoading.setVisibility(View.GONE);
                    DisplayViewUI.displayAlertDialogMsg(context, "Account successfully created.\nPlease check your email , verify and log in back!!", "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                            context.startActivity(new Intent(view.getContext(), LoginActivity.class));

                        }
                    });
                }

            });


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

        if (confirmPassword.trim().isEmpty()) {
            pbLoading.setVisibility(View.GONE);
            txtConfirmPassword.setErrorEnabled(true);
            txtConfirmPassword.setError(context.getResources().getString(R.string.passwordRequired));
        }
        if (password.length() < 8 || confirmPassword.length() < 8) {
            pbLoading.setVisibility(View.GONE);
            txtPassword.setErrorEnabled(true);
            txtPassword.setError(context.getResources().getString(R.string.passwordTooShort));
        }

        if (!confirmPassword.equals(password)) {
            pbLoading.setVisibility(View.GONE);
            txtPassword.setErrorEnabled(true);
            txtConfirmPassword.setErrorEnabled(true);
            txtPassword.setError(context.getResources().getString(R.string.passwordDontMatch));
            txtConfirmPassword.setError(context.getResources().getString(R.string.passwordDontMatch));
        }

    }

}
