package com.track.me.app.ui.dashboard;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.track.me.app.MainActivity;
import com.track.me.app.R;
import com.track.me.app.activities.BaseActivity;
import com.track.me.app.adapters.FriendRequestAdapter;
import com.track.me.app.constants.AppConstants;
import com.track.me.app.databinding.FragmentContactsBinding;
import com.track.me.app.model.RequestModel;
import com.track.me.app.services.LocationUpdatesService;
import com.track.me.app.utils.DisplayViewUI;
import com.track.me.app.utils.GetTimeAgo;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class FriendsFragment extends Fragment {
    private static final String TAG = "ContactsFragment";
    RecyclerView rv;
    String id;
    String yourLocation;
    double latitude, longitude;
    //private static final int INITIAL_LOAD = 15;
    private FragmentContactsBinding binding;
    private CollectionReference friendsCollectionReference, locationCollectionReference;
    // private RequestAdapter adapter;
    private FriendRequestAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_contacts, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        CollectionReference friendsCollectionReference = FirebaseFirestore.getInstance().collection("Friends");

        yourLocation = MainActivity.knownName;
        latitude = MainActivity.latitude;
        longitude = MainActivity.longitude;

        if (yourLocation == null || latitude == 0.0 || longitude == 0.0) {
            yourLocation = BaseActivity.knownName;
            latitude = BaseActivity.latitude;
            longitude = BaseActivity.longitude;
        }

        initViews();
        loadData();

        adapter.setOnItemClickListener((view1, position) -> requireActivity().runOnUiThread(() -> {
            SharedPreferences pref = requireActivity().getSharedPreferences(AppConstants.PREFS, 0);

            ProgressDialog progressBar = DisplayViewUI.displayProgress(requireActivity(), getString(R.string.XCC));
            //send location
            //Send location details to user
            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:MM a");
            String dateSent = dateFormat.format(Calendar.getInstance().getTime());

            String getUserName = adapter.getItem(position).getName();
            String getUserId = adapter.getItem(position).getId();
            String getUserPhoto = adapter.getItem(position).getPhoto();
            String name = BaseActivity.userName, photo = BaseActivity.userPhotoUrl, senderId = BaseActivity.uid;

            DisplayViewUI.displayAlertDialog(requireActivity(),
                    getString(R.string.sndloc),
                    MessageFormat.format(getString(R.string.qst), getUserName), getString(R.string.yes), getString(R.string.no), (dialogInterface, i) -> {
                        if (i == -1) {
                            // progressBar.show();
                            String sharedLocation = name + " " + getString(R.string.shLoc) + " " + getString(R.string.withU);
                            String locationReceived = getString(R.string.urLoc) + " " + getString(R.string.isShared) + " " + getUserName;

                            //get location coordinates
                            double latitude = Double.parseDouble(Double.toString(MainActivity.latitude));
                            double longitude = Double.parseDouble(Double.toString(MainActivity.longitude));
                            String url = "http://maps.google.com/maps?q=loc:" + latitude + "," + longitude + "&z=15";

                            //..location received from another user ..//
                            Map<String, Object> fromUser = new HashMap<>();
                            fromUser.put("location", locationReceived);
                            fromUser.put("knownName", yourLocation);
                            fromUser.put("url", url);
                            fromUser.put("date", dateSent);
                            fromUser.put("userName", "You");
                            fromUser.put("photo", getUserPhoto);
                            fromUser.put("latitude", latitude);
                            fromUser.put("longitude", longitude);
                            fromUser.put("timeStamp", GetTimeAgo.getTimeInMillis());
                            fromUser.put("isSharingLocation", false);

                            //..location sent to ..(user who sent  will view this) //
                            Map<String, Object> toReceiver = new HashMap<>();
                            toReceiver.put("location", sharedLocation);
                            toReceiver.put("knownName", yourLocation);
                            toReceiver.put("url", url);
                            toReceiver.put("date", dateSent);
                            toReceiver.put("userName", name);
                            toReceiver.put("latitude", latitude);
                            toReceiver.put("longitude", longitude);
                            toReceiver.put("timeStamp", GetTimeAgo.getTimeInMillis());
                            toReceiver.put("photo", photo);
                            toReceiver.put("isSharingLocation", true);

                           /* locationDbRef.child(senderId).child(locationDbId).setValue(fromUser);
                            locationDbRef.child(getUserId).child(locationDbId).setValue(toReceiver);
*/
                            //to cloud server
                            locationCollectionReference.document(senderId).collection(senderId).document(getUserId).set(fromUser);
                            locationCollectionReference.document(getUserId).collection(getUserId).document(senderId).set(toReceiver);

                            //update contacts when location is shared
                            friendsCollectionReference.document(senderId).collection(senderId).document(getUserId).update("isSharingLocation", true);
                            //  friendsCollectionReference.document(getUserId).collection(getUserId).document(senderId).update("isSharingLocation", true);


                            DisplayViewUI.displayToast(requireActivity(), getString(R.string.successFull));


                        } else if (i == -2) {
                            dialogInterface.dismiss();


                        }
                    });

        }));

    }


    void initViews() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mCurrentUser = mAuth.getCurrentUser();
        assert mCurrentUser != null;
        id = mCurrentUser.getUid();


        rv = binding.contactsRecyclerView;
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(requireActivity()));

        friendsCollectionReference = FirebaseFirestore.getInstance().collection("Friends");
        locationCollectionReference = FirebaseFirestore.getInstance().collection("Locations");

    }

    private void loadData() {

        requireActivity().runOnUiThread(() -> {
            Query query = friendsCollectionReference
                    .document(id)
                    .collection(id)
                    .orderBy("name", Query.Direction.ASCENDING);

            FirestoreRecyclerOptions<RequestModel> options =
                    new FirestoreRecyclerOptions.Builder<RequestModel>().setQuery(query,
                            RequestModel.class).build();

            adapter = new FriendRequestAdapter(options);
            rv.setAdapter(adapter);

        });


    }


    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (yourLocation == null || latitude == 0.0 || longitude == 0.0) {
            yourLocation = LocationUpdatesService.knownName;
            latitude = LocationUpdatesService.lat;
            longitude = LocationUpdatesService.lng;
        } else {

            yourLocation = BaseActivity.knownName;
            latitude = BaseActivity.latitude;
            longitude = BaseActivity.longitude;
        }

    }
}