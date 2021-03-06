package com.track.me.app.adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.track.me.app.R;
import com.track.me.app.activities.BaseActivity;
import com.track.me.app.databinding.LayoutAddUserBinding;
import com.track.me.app.model.RequestModel;
import com.track.me.app.utils.DisplayViewUI;

public class ContactsAdapter extends FirestoreRecyclerAdapter<RequestModel, ContactsAdapter.RequestViewHolder> {

    private static onItemClickListener onItemClickListener;


    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ContactsAdapter(@NonNull FirestoreRecyclerOptions<RequestModel> options) {
        super(options);

    }

    @Override
    protected void onBindViewHolder(@NonNull RequestViewHolder holder, int position, @NonNull RequestModel users) {

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(DisplayViewUI.getRandomDrawableColor());
        requestOptions.error(DisplayViewUI.getRandomDrawableColor());
        requestOptions.centerCrop();

        DocumentSnapshot ds = getSnapshots().getSnapshot(position);
        String id = ds.getId();
        String userId = BaseActivity.uid;

        //check if the searched id is equal to the current user
        if (userId.equals(id)) {
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) holder.layoutAddUserBinding.getRoot().getLayoutParams();
            layoutParams.height = 0;
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            holder.layoutAddUserBinding.getRoot().setVisibility(View.VISIBLE);

        } else {
            holder.layoutAddUserBinding.setUsers(users);
            Glide.with(holder.layoutAddUserBinding.getRoot().getContext())
                    .load(users.userPhotoUrl)
                    .apply(requestOptions)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Drawable> target, boolean isFirstResource) {

                            if (isFirstResource) {
                                holder.layoutAddUserBinding.pbLoading.setVisibility(View.INVISIBLE);

                            }
                            holder.layoutAddUserBinding.pbLoading.setVisibility(View.VISIBLE);

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {

                            holder.layoutAddUserBinding.pbLoading.setVisibility(View.INVISIBLE);

                            return false;
                        }
                    }).error(ContextCompat.getDrawable(holder.layoutAddUserBinding.getRoot().getContext(), R.drawable.photo))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.imageView);
        }


    }


    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RequestViewHolder((DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_add_user, parent, false)));

    }



    public void setOnItemClickListener(onItemClickListener onItemClickListener) {
        ContactsAdapter.onItemClickListener = onItemClickListener;

    }

    public interface onItemClickListener {
        void onClick(View view, int position);
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final LayoutAddUserBinding layoutAddUserBinding;
        final ImageView imageView;
        final Button btnAdd;


        public RequestViewHolder(@NonNull LayoutAddUserBinding layoutAddUserBinding) {
            super(layoutAddUserBinding.getRoot());
            this.layoutAddUserBinding = layoutAddUserBinding;

            btnAdd = layoutAddUserBinding.btnAddContact;
            imageView = layoutAddUserBinding.imgContactPhoto;
            btnAdd.setOnClickListener(this);


        }


        //display the response details
        void showResponse(String response) {

            if (response.equals("sent")) {
                btnAdd.setText(R.string.dcln);


            }
            if (response.equals("accepted")) {

                btnAdd.setText(R.string.frnds);


            }

            if (response.equals("declined")) {

                btnAdd.setText(R.string.Pending);


            }

            if (response.equals("received")) {

                btnAdd.setText(R.string.Pending);


            }


        }


        @Override
        public void onClick(View view) {
            onItemClickListener.onClick(layoutAddUserBinding.getRoot(), getAdapterPosition());

        }
    }



}
