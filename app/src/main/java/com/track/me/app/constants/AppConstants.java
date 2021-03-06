package com.track.me.app.constants;


import com.track.me.app.R;

public final class AppConstants {
    public static final String PREFS = "prefs";
    public static final String IS_DIALOG_SHOWN = "dialogShown";
    public static final String IS_LOCATION_SHARED = "location sharing";
    public static final int CAMERA_REQUEST_CODE = 1;
    public static final String SEND_REQUEST_TAG = "sendRequest";
    public static final String UID = "uid";
    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String RESPONSE = "response";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String USER_NAME = "userName";
    public static final String STATE = "state";
    public static final String COUNTRY = "country";
    public static final String KNOWN_LOCATION = "known location";
    public static final String TIMESTAMP = "timeStamp";
    public static final String USER_PHOTO_URL = "userPhotoUrl";
    public static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    public static final String CACHE_UTILS = "CacheUtils";
    public static final String LATITUDE = "lat";
    public static final String LONGITUDE = "lng";


    /**
     * Constant used in the location settings dialog.
     */
    public static final int REQUEST_CHECK_SETTINGS = 0x1;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    public final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    public final static String KEY_LOCATION = "location";
    public final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";







}
