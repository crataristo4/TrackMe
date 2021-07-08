package com.track.me.app.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.track.me.app.R;
import com.track.me.app.databinding.LayoutLocationSharingBinding;
import com.track.me.app.model.ShareLocation;
import com.track.me.app.utils.GetTimeAgo;

import de.hdodenhof.circleimageview.CircleImageView;

public class LocationSharingAdapter extends FirestoreRecyclerAdapter<ShareLocation, LocationSharingAdapter.LocationSharingViewHolder> {
    private static OnLocationItemClick onLocationItemClick;

    public void setOnLocationItemClick(OnLocationItemClick onLocationItemClick) {
        LocationSharingAdapter.onLocationItemClick = onLocationItemClick;
    }


    public interface OnLocationItemClick {

        void onclick(View view, int position);
    }

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public LocationSharingAdapter(@NonNull FirestoreRecyclerOptions<ShareLocation> options) {
        super(options);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onBindViewHolder(@NonNull LocationSharingViewHolder holder, int i, @NonNull ShareLocation shareLocation) {

        holder.layoutLocationSharingBinding.setLocation(shareLocation);
        holder.txtTime.setText(GetTimeAgo.getTimeAgo(shareLocation.getTimeStamp()));
        Glide.with(holder.layoutLocationSharingBinding.getRoot())
                .load(shareLocation.getPhoto())
                .thumbnail(0.5f)
                .error(holder.layoutLocationSharingBinding.getRoot().getResources().getDrawable(R.drawable.photo))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imgPhoto);


    }

    @NonNull
    @Override
    public LocationSharingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LocationSharingViewHolder((DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_location_sharing, parent, false)));

    }

    public static class LocationSharingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final LayoutLocationSharingBinding layoutLocationSharingBinding;
        final CircleImageView imgPhoto;
        final TextView txtTime;

        public LocationSharingViewHolder(@NonNull LayoutLocationSharingBinding layoutLocationSharingBinding) {
            super(layoutLocationSharingBinding.getRoot());
            this.layoutLocationSharingBinding = layoutLocationSharingBinding;

            layoutLocationSharingBinding.getRoot().setOnClickListener(this);
            imgPhoto = layoutLocationSharingBinding.imgPhoto;
            txtTime = layoutLocationSharingBinding.txtTime;


        }

        @Override
        public void onClick(View view) {

            onLocationItemClick.onclick(layoutLocationSharingBinding.getRoot(), getAdapterPosition());

        }
    }

}
