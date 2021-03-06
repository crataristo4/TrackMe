package com.track.me.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.track.me.app.activities.BaseActivity;
import com.track.me.app.activities.EditProfileActivity;
import com.track.me.app.activities.SplashScreenActivity;
import com.track.me.app.databinding.ActivityMainBinding;
import com.track.me.app.ui.search.SearchContactActivity;
import com.track.me.app.utils.DisplayViewUI;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_search:
                Intent searchIntent = new Intent(getApplicationContext(), SearchContactActivity.class);

                startActivity(searchIntent);
                break;

            case R.id.menu_logout:

                DisplayViewUI.displayAlertDialog(this,
                        getString(R.string.logOut), getString(R.string.xcvv),
                        getString(R.string.logMeOut), getString(R.string.cancel),
                        (dialogInterface, i) -> {
                            if (i == -1) {

                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(this, SplashScreenActivity.class)
                                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                finish();
                            } else if (i == -2) {
                                dialogInterface.dismiss();
                            }


                        });

                break;

            case R.id.menu_edit_profile:

                startActivity(new Intent(this, EditProfileActivity.class));

                break;


        }


        return true;
    }

}