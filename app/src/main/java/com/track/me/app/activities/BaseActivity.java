package com.track.me.app.activities;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.track.me.app.R;
import com.track.me.app.constants.AppConstants;
import com.track.me.app.model.RequestModel;
import com.track.me.app.utils.DisplayViewUI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    /**
     * Time when the location was updated represented as a String.
     */
    public static String knownName, state, country, address;
    public static double latitude, longitude;
    private static Object mContext;
    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;
    /**
     * Provides access to the Location Settings API.
     */
    private SettingsClient mSettingsClient;
    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    private LocationRequest mLocationRequest;
    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    private LocationSettingsRequest mLocationSettingsRequest;
    /**
     * Callback for Location events.
     */
    private LocationCallback mLocationCallback;
    /**
     * Represents a geographical location.
     */
    private Location mCurrentLocation;
    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    private Boolean mRequestingLocationUpdates;
    public static Geocoder geocoder;
    private CollectionReference friendsCollectionReference, locationCollectionReference;
    public static String uid, userName, userPhotoUrl, email;
    public static ArrayList<String> friends;
    public static long timeStamp;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private CollectionReference usersCollectionRef;

    public static Context getAppContext() {
        return (Context) mContext;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        friendsCollectionReference = FirebaseFirestore.getInstance().collection("Friends");
        locationCollectionReference = FirebaseFirestore.getInstance().collection("Locations");

        loadUserData();

        mContext = getApplicationContext();
        mRequestingLocationUpdates = false;

        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);
        geocoder = new Geocoder(this, Locale.getDefault());

        if (checkPermissions()) {
            createLocationCallback();
            createLocationRequest();
        }

    }



    /**
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(AppConstants.KEY_REQUESTING_LOCATION_UPDATES)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        AppConstants.KEY_REQUESTING_LOCATION_UPDATES);
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(AppConstants.KEY_LOCATION)) {
                // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(AppConstants.KEY_LOCATION);
            }

        }
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(AppConstants.UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(AppConstants.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Creates a callback for receiving location events.
     */
    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                mCurrentLocation = locationResult.getLastLocation();
                updateLocationUI();
            }
        };
    }

    private void updateLocationUI() {
        if (mCurrentLocation != null) {

            latitude = mCurrentLocation.getLatitude();
            longitude = mCurrentLocation.getLongitude();

            try {
                List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);

                if (addressList != null) {
                    address = addressList.get(0).getAddressLine(0);
                    state = addressList.get(0).getAdminArea();
                    country = addressList.get(0).getCountryName();
                    knownName = addressList.get(0).getFeatureName();
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            updateLocationIfSharing();



            Log.i(TAG, String.format(Locale.ENGLISH, "%s: %f", "lat",
                    mCurrentLocation.getLatitude()));
            Log.i(TAG, String.format(Locale.ENGLISH, "%s: %f", "lng",
                    mCurrentLocation.getLongitude()));


        }
    }


    /**
     * Uses a {@link LocationSettingsRequest.Builder} to build
     * a {@link LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == AppConstants.REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mRequestingLocationUpdates) {
                    Log.i(TAG, "Permission granted, updates requested, starting location updates");
                    startLocationUpdates();
                }
            } else {

                requestPermissions();

            }
        }
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            DisplayViewUI.displayAlertDialog(BaseActivity.this,
                    getString(R.string.grantPerm),
                    getString(R.string.permission_rationale), getString(R.string.ok), (dialogInterface, i) -> {
// Request permission
                        ActivityCompat.requestPermissions(BaseActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                AppConstants.REQUEST_PERMISSIONS_REQUEST_CODE);
                    });

        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(BaseActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    AppConstants.REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Requests location updates from the FusedLocationApi. Note: we don't call this unless location
     * runtime permission has been granted.
     */
    private void startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, locationSettingsResponse -> {
                    Log.i(TAG, "All location settings are satisfied.");

                    //noinspection MissingPermission
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    if (mLocationCallback !=null) {
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());
                    }


                    //updateUI();
                })
                .addOnFailureListener(this, e -> {
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                    "location settings ");

                            try {
                                // Show the dialog by calling startResolutionForResult(), and check the
                                // result in onActivityResult().
                                ResolvableApiException rae = (ResolvableApiException) e;
                                rae.startResolutionForResult(BaseActivity.this, AppConstants.REQUEST_CHECK_SETTINGS);

                            } catch (IntentSender.SendIntentException sie) {
                                Log.i(TAG, "PendingIntent unable to execute request.");
                            }

                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            String errorMessage = getString(R.string.errMsg) + getString(R.string.fixIn);
                            Log.e(TAG, errorMessage);
                            DisplayViewUI.displayToast(getAppContext(), errorMessage);
                            mRequestingLocationUpdates = false;
                    }

                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!checkPermissions()) {
            requestPermissions();
        } else {
            buildLocationSettingsRequest();
            startLocationUpdates();
        }
    }


    void updateLocationIfSharing() {

        if (firebaseUser != null) {

            runOnUiThread(() -> friendsCollectionReference.document(uid)
                    .collection(uid)
                    .get().addOnSuccessListener(this, queryDocumentSnapshots -> {

                        for (QueryDocumentSnapshot ds : queryDocumentSnapshots) {

                            RequestModel data = ds.toObject(RequestModel.class);

                            boolean isLocationSharing = data.isSharingLocation();
                            String name = data.getName();
                            String idOfFriend = data.getId();

                            Log.i(TAG,
                                    String.format("Sharing location with : %s isSharing: %s : id %s",
                                            name, isLocationSharing, idOfFriend));


                            if (isLocationSharing) {

                                Map<String, Object> updateLocationMap = new HashMap<>();
                                updateLocationMap.put("latitude", latitude);
                                updateLocationMap.put("longitude", longitude);
                                updateLocationMap.put("knownName", knownName);

                                Log.i(TAG, "still sharing: ");
                                locationCollectionReference.document(uid)
                                        .collection(uid)
                                        .document(idOfFriend).update(updateLocationMap);

                                locationCollectionReference.document(idOfFriend)
                                        .collection(idOfFriend)
                                        .document(uid).update(updateLocationMap);

                                Log.i(TAG, "Name: " + name + " isSharing: " + knownName);


                            } else {

                                Log.i(TAG, "not sharing: ");

                            }

                        }


                    }));


        }


    }

    //loads users details to be used by all activities
    void loadUserData() {
        runOnUiThread(() -> {
            if (firebaseUser != null) {

                uid = firebaseUser.getUid();

                usersCollectionRef = FirebaseFirestore.getInstance().collection("Users");

                usersCollectionRef.get().addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        if (!task.getResult().getDocuments().isEmpty()) {
                            DocumentReference usersDocDbRef = usersCollectionRef.document(uid);

                            usersDocDbRef.get().addOnCompleteListener(task1 -> {

                                if (task1.isSuccessful()) {
                                    DocumentSnapshot document = task1.getResult();
                                    if (document.exists()) {

                                        userPhotoUrl = Objects.requireNonNull(document.getString("userPhotoUrl"));
                                        userName = Objects.requireNonNull(Objects.requireNonNull(document).getString("userName"));
                                        email = Objects.requireNonNull(Objects.requireNonNull(document).getString("email"));
                                        timeStamp = (long) Objects.requireNonNull(document.get("timeStamp"));
                                        friends = (ArrayList<String>) Objects.requireNonNull(document.get("friends"));

                                        Log.i(TAG, "User details : " + userName + " friends" + friends);

                                    }

                                }
                            });
                        }
                    }
                });
            }
        });

    }


}
