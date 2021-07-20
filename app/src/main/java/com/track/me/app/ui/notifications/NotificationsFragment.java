package com.track.me.app.ui.notifications;

import android.os.Bundle;
import android.os.SystemClock;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.track.me.app.R;
import com.track.me.app.activities.BaseActivity;
import com.track.me.app.adapters.LocationSharingAdapter;
import com.track.me.app.bottomsheets.UserLocation;
import com.track.me.app.constants.AppConstants;
import com.track.me.app.databinding.FragmentNotificationsBinding;
import com.track.me.app.model.ShareLocation;

import org.jetbrains.annotations.NotNull;

public class NotificationsFragment extends Fragment {

    FragmentNotificationsBinding fragmentNotificationsBinding;
    String id;
    RecyclerView rv;
    Query query;
    private LocationSharingAdapter adapter;
    private CollectionReference mCollectionReference;
    private long mLastClickTime = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        fragmentNotificationsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_notifications, container, false);

        return fragmentNotificationsBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        loadData();
    }

    void initViews() {
        id = BaseActivity.uid;
        rv = fragmentNotificationsBinding.alertRecyclerView;
        rv.setHasFixedSize(true);

        //locationDbRef = FirebaseDatabase.getInstance().getReference().child("Locations").child(MainActivity.userId);
        mCollectionReference = FirebaseFirestore.getInstance().collection("Locations");


    }

    private void loadData() {


        requireActivity().runOnUiThread(() -> {
            query = mCollectionReference.document(id).collection(id).orderBy("timeStamp");
            FirestoreRecyclerOptions<ShareLocation> options =
                    new FirestoreRecyclerOptions.Builder<ShareLocation>().setQuery(query,
                            ShareLocation.class).build();

            rv.setLayoutManager(new LinearLayoutManager(requireContext()));
            adapter = new LocationSharingAdapter(options);
            rv.setAdapter(adapter);

            adapter.setOnLocationItemClick((view, position) -> {
                String name = adapter.getItem(position).getUserName();
                long timeStamp = adapter.getItem(position).getTimeStamp();
                String knownLocation = adapter.getItem(position).getKnownName();
                String userPhoto = adapter.getItem(position).getPhoto();
                double lat = adapter.getItem(position).getLatitude();
                double lng = adapter.getItem(position).getLongitude();


                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }

                mLastClickTime = SystemClock.elapsedRealtime();

                UserLocation userLocation = new UserLocation();
                Bundle bundle = new Bundle();
                bundle.putString(AppConstants.USER_NAME, name);
                bundle.putString(AppConstants.KNOWN_LOCATION, knownLocation);
                bundle.putString(AppConstants.USER_PHOTO_URL, userPhoto);
                bundle.putLong(AppConstants.TIMESTAMP, timeStamp);
                bundle.putDouble(AppConstants.LATITUDE, lat);
                bundle.putDouble(AppConstants.LONGITUDE, lng);

                userLocation.setArguments(bundle);
                userLocation.setCancelable(false);
                userLocation.show(getChildFragmentManager(), "location");

               /* Intent viewUserData = new Intent(requireContext(), ViewUserLocationActivity.class);
                viewUserData.putExtra(AppConstants.USER_NAME, name);
                viewUserData.putExtra(AppConstants.KNOWN_LOCATION, knownLocation);
                viewUserData.putExtra(AppConstants.USER_PHOTO_URL, userPhoto);
                viewUserData.putExtra(AppConstants.TIMESTAMP, timeStamp);
                viewUserData.putExtra(AppConstants.LATITUDE, lat);
                viewUserData.putExtra(AppConstants.LONGITUDE, lng);

                startActivity(viewUserData);*/
            });


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


}