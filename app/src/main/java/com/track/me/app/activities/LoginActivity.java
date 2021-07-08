package com.track.me.app.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.track.me.app.R;
import com.track.me.app.clickhandler.LogInClickHandler;
import com.track.me.app.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding activityLoginBinding;
    LogInClickHandler logInClickHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLoginBinding = DataBindingUtil.setContentView(this, R.layout
                .activity_login);


        logInClickHandler = new LogInClickHandler(this,
                activityLoginBinding.inputEmail,
                activityLoginBinding.inputPassword,
                activityLoginBinding.progressBar,
                activityLoginBinding.btnLogin);

        activityLoginBinding.setLoginHandler(logInClickHandler);


    }


}