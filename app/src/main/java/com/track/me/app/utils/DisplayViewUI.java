package com.track.me.app.utils;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.track.me.app.R;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.Random;

public class DisplayViewUI {


    private static final ColorDrawable[] vibrantLightColorList =
            {
                    new ColorDrawable(Color.parseColor("#ffeead")),
                    new ColorDrawable(Color.parseColor("#93cfb3")),
                    new ColorDrawable(Color.parseColor("#fd7a7a")),
                    new ColorDrawable(Color.parseColor("#faca5f")),
                    new ColorDrawable(Color.parseColor("#1ba798")),
                    new ColorDrawable(Color.parseColor("#6aa9ae")),
                    new ColorDrawable(Color.parseColor("#ffbf27")),
                    new ColorDrawable(Color.parseColor("#d93947"))
            };

    public static String getAlphaNumericString(int n) {

        // lower limit for LowerCase Letters
        int lowerLimit = 97;

        // lower limit for LowerCase Letters
        int upperLimit = 122;


        SecureRandom random = new SecureRandom();

        // Create a StringBuffer to store the result
        StringBuilder r = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // take a random value between 97 and 122
            int nextRandomChar = lowerLimit
                    + (int) (random.nextFloat()
                    * (upperLimit - lowerLimit + 1));

            // append a character at the end of bs
            r.append((char) nextRandomChar);
        }

        // return the resultant string
        return r.toString();
    }

    public static ColorDrawable getRandomDrawableColor() {
        int idx = new Random().nextInt(vibrantLightColorList.length);
        return vibrantLightColorList[idx];
    }


    public static void displayToast(Context ctx, String s) {
        Toast toast = Toast.makeText(ctx, s, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static ProgressDialog displayProgress(Context ctx, String message) {
        ProgressDialog loading = new ProgressDialog(ctx);
        loading.setCancelable(false);
        loading.setMessage(message);
        // loading.show();

        return loading;
    }

    public static void displaySnackBar(@NonNull View ctx, String message) {

        Snackbar snackbar = Snackbar.make(ctx, message, Snackbar.LENGTH_LONG);
        snackbar.show();

    }

    static public void displayAlertDialog(Context context, String title, String msg, String btnPos, String btnNeg, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(false);
        if (btnPos != null) builder.setPositiveButton(btnPos, onClickListener);
        if (btnNeg != null) builder.setNegativeButton(btnNeg, onClickListener);
        builder.setIcon(ContextCompat.getDrawable(context, R.drawable.applogo));
        builder.show();
    }

    static public void displayAlertDialog(Context context, String title, String msg, String btnPos, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(false);
        if (btnPos != null) builder.setPositiveButton(btnPos, onClickListener);
        builder.setIcon(R.drawable.ic_gps);
        builder.show();
    }

    static public void displayAlertDialogMsg(Context context, String msg, String btnPos, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setCancelable(false);
        if (btnPos != null) builder.setPositiveButton(btnPos, onClickListener);
        builder.setIcon(ContextCompat.getDrawable(context, R.drawable.sorry));
        builder.show();
    }

    static public void displayAlertDialogMsg(Context context, String title, String msg, String btnNeg, String btnPos, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(false);
        // builder.setNegativeButtonIcon(ContextCompat.getDrawable(context,R.drawable.ic_baseline_cancel_24));
        if (btnNeg != null) builder.setNegativeButton(btnNeg, onClickListener);
        if (btnPos != null) builder.setPositiveButton(btnPos, onClickListener);
        builder.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_baseline_check_circle_24));
        builder.show();
    }

    static public boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) Objects.requireNonNull(context).getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        assert networkInfo != null;
        return networkInfo.isConnectedOrConnecting();

    }

    static public void openGallery(AppCompatActivity context) {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setInitialCropWindowPaddingRatio(0)
                //.setAspectRatio(16, 16)
                .start(context);
    }

    static public void openGallery(Context context, Fragment fragment) {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setInitialCropWindowPaddingRatio(0)
                //.setAspectRatio(16, 16)
                .start(context, fragment);
    }
}
