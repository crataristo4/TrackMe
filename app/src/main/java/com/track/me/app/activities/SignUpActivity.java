package com.track.me.app.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.track.me.app.clickhandler.SignUpClickHandler;
import com.track.me.app.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySignUpBinding signUpBinding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(signUpBinding.getRoot());

        SignUpClickHandler signUpClickHandler = new SignUpClickHandler(this, signUpBinding.inputEmail, signUpBinding.inputPassword, signUpBinding.inputConfirmPassword, signUpBinding.progressBar, signUpBinding.btnSignUp);
        signUpBinding.setSignUp(signUpClickHandler);
    }
}