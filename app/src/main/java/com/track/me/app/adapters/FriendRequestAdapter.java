package com.track.me.app.adapters;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.track.me.app.R;
import com.track.me.app.activities.BaseActivity;
import com.track.me.app.databinding.LayoutRequestReceivedBinding;
import com.track.me.app.model.RequestModel;
import com.track.me.app.utils.DisplayViewUI;

import java.text.MessageFormat;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendRequestAdapter extends FirestoreRecyclerAdapter<RequestModel, FriendRequestAdapter.RequestViewHolder> {
    private static FriendRequestAdapter.onItemClickListener onItemClickListener;


    public FriendRequestAdapter(@NonNull FirestoreRecyclerOptions<RequestModel> options) {
        super(options);
    }


    public void setOnItemClickListener(FriendRequestAdapter.onItemClickListener onItemClickListener) {
        FriendRequestAdapter.onItemClickListener = onItemClickListener;

    }

    @SuppressLint({"CheckResult", "UseCompatLoadingForDrawables"})
    @Override
    protected void onBindViewHolder(@NonNull RequestViewHolder holder, int position, @NonNull RequestModel users) {
        String friends = "friends", declined = "declined", response = "response";

        holder.layoutRequestReceivedBinding.setRequests(users);
        holder.showResponse(users.getResponse());
        if (users.getResponse().equals(friends)) {

            holder.isSharingLocation(users.isSharingLocation());

        }

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(DisplayViewUI.getRandomDrawableColor());
        requestOptions.error(DisplayViewUI.getRandomDrawableColor());
        requestOptions.centerCrop();

        Glide.with(holder.layoutRequestReceivedBinding.getRoot().getContext())
                .load(users.getPhoto())
                .thumbnail(0.5f)
                .error(holder.layoutRequestReceivedBinding.getRoot().getContext().getDrawable(R.drawable.photo))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imgPhoto);


        CollectionReference friendsCollectionReference = FirebaseFirestore.getInstance().collection("Friends");

        //todo ---
        String id = BaseActivity.uid;
        String receiverId = getSnapshots().get(position).getId();
        String name = getSnapshots().get(position).getName();


        holder.btnAccept.setOnClickListener(view -> {
            friendsCollectionReference.document(receiverId).collection(receiverId).document(id).update(response, friends);
            friendsCollectionReference.document(id).collection(id).document(receiverId).update(response, friends);
            DisplayViewUI.displayToast(view.getContext(), view.getContext().getResources().getString(R.string.successFull));


        });


        if (holder.btnDecline.getText().toString().equals(holder.layoutRequestReceivedBinding.getRoot().getResources().getString(R.string.cancelRequest))) {
            holder.btnDecline.setOnClickListener(view -> DisplayViewUI.displayAlertDialog(view.getContext(),
                    view.getContext().getString(R.string.dcln), view.getContext().getString(R.string.cancelRqst) + name,
                    view.getContext().getString(R.string.yes), view.getContext().getString(R.string.no),
                    (dialogInterface, i) -> {
                        if (i == -1) {

                            //remove document id for both users
                            friendsCollectionReference.document(id).collection(id).document(receiverId).delete();
                            friendsCollectionReference.document(receiverId).collection(receiverId).document(id).delete();
                        } else if (i == -2) {
                            dialogInterface.dismiss();
                        }


                    }));
        } else if (holder.btnDecline.getText().toString().equals(holder.layoutRequestReceivedBinding.getRoot().getResources().getString(R.string.dcln))) {
            holder.btnDecline.setOnClickListener(view -> DisplayViewUI.displayAlertDialog(view.getContext(),
                    view.getContext().getString(R.string.declin), view.getContext().getString(R.string.sureDcl) + " " + name,
                    view.getContext().getString(R.string.yes), view.getContext().getString(R.string.no),
                    (dialogInterface, i) -> {
                        if (i == -1) {

                            // friendsCollectionReference.document(receiverId).collection(receiverId).document(id).update(response, declined);
                            //remove document id for receiver
                            friendsCollectionReference.document(id).collection(id).document(receiverId).delete();
                            friendsCollectionReference.document(receiverId).collection(receiverId).document(id).delete();


                        } else if (i == -2) {
                            dialogInterface.dismiss();
                        }


                    }));

        }

        if (holder.btnSendLocation.getText().toString().equals(holder.layoutRequestReceivedBinding.getRoot().getResources().getString(R.string.stpLocSharing))) {
            holder.btnSendLocation.setOnClickListener(view -> DisplayViewUI.displayAlertDialog(view.getContext(),
                    holder.layoutRequestReceivedBinding.getRoot().getResources().getString(R.string.cancelloc),
                    MessageFormat.format(holder.layoutRequestReceivedBinding.getRoot().getResources().getString(R.string.qstCancel), users.getName()),
                    holder.layoutRequestReceivedBinding.getRoot().getResources().getString(R.string.yes),
                    holder.layoutRequestReceivedBinding.getRoot().getResources().getString(R.string.no),
                    (dialogInterface, i) -> {
                        if (i == -1) {

                            friendsCollectionReference
                                    .document(id)
                                    .collection(id)
                                    .document(receiverId)
                                    .update("isSharingLocation", false);
                            DisplayViewUI.displayToast(view.getContext(), view.getContext().getResources().getString(R.string.locationStoped));


                        } else if (i == -2) {
                            dialogInterface.dismiss();


                        }
                    }));
        }

        holder.imgDelete.setOnClickListener(view -> DisplayViewUI.displayAlertDialog(view.getContext(),
                view.getContext().getString(R.string.rmUser), view.getContext().getString(R.string.sure) + " " + name + " ?",
                view.getContext().getString(R.string.yesDel), view.getContext().getString(R.string.cancel),
                (dialogInterface, i) -> {
                    if (i == -1) {
                        ProgressDialog progressDialog = DisplayViewUI.displayProgress(view.getContext(), view.getContext().getString(R.string.dltUser));
                        progressDialog.show();
                        //remove document id for receiver
                        //  friendsCollectionReference.document(receiverId).collection(receiverId).document(id).update(response, declined);
                        //completely delete user from both friends
                        friendsCollectionReference.document(id).collection(id).document(receiverId).delete();
                        friendsCollectionReference.document(receiverId).collection(receiverId).document(id).delete();

                        new Handler().postDelayed(progressDialog::dismiss, 3000);


                    } else if (i == -2) {
                        dialogInterface.dismiss();
                    }

                }));

    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RequestViewHolder((DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.layout_request_received, parent, false)));

    }

    public interface onItemClickListener {
        void onClick(View view, int position);
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final LayoutRequestReceivedBinding layoutRequestReceivedBinding;
        final Button btnAccept;
        final Button btnDecline;
        final Button btnSendLocation;
        final CircleImageView imgPhoto;
        final ImageView imgDelete, imgIsSharingLocation;
        final TextView txtRequestDes;
        final ProgressBar pbImageLoading;

        public RequestViewHolder(@NonNull LayoutRequestReceivedBinding layoutRequestReceivedBinding) {
            super(layoutRequestReceivedBinding.getRoot());
            this.layoutRequestReceivedBinding = layoutRequestReceivedBinding;

            btnAccept = layoutRequestReceivedBinding.accept;
            btnDecline = layoutRequestReceivedBinding.decline;
            btnSendLocation = layoutRequestReceivedBinding.btnSendLocation;
            imgPhoto = layoutRequestReceivedBinding.userImage;
            imgIsSharingLocation = layoutRequestReceivedBinding.imgIsSharingLocation;
            imgDelete = layoutRequestReceivedBinding.imgDeleteUser;
            txtRequestDes = layoutRequestReceivedBinding.txtRequestDes;
            pbImageLoading = layoutRequestReceivedBinding.pbImageLoading;
            imgIsSharingLocation.setVisibility(View.GONE);

            btnSendLocation.setOnClickListener(this);


        }


        //display the response details
        void showResponse(@NonNull String response) {

            switch (response) {
                case "friends":

                    btnAccept.setText(R.string.frnds);
                    btnAccept.setEnabled(false);
                    btnDecline.setVisibility(View.GONE);
                    imgDelete.setVisibility(View.VISIBLE);
                    btnSendLocation.setVisibility(View.VISIBLE);
                    txtRequestDes.setVisibility(View.GONE);
                    imgIsSharingLocation.setVisibility(View.VISIBLE);


                    break;
                case "received":

                    imgIsSharingLocation.setVisibility(View.GONE);
                    btnAccept.setVisibility(View.VISIBLE);
                    btnDecline.setVisibility(View.VISIBLE);
                    btnSendLocation.setVisibility(View.GONE);
                    imgDelete.setVisibility(View.GONE);
                    txtRequestDes.setVisibility(View.VISIBLE);

                    txtRequestDes.setText(R.string.newReq);
                    txtRequestDes.setTextColor(this.layoutRequestReceivedBinding.getRoot().getContext().getResources().getColor(R.color.acceptGreen));


                    break;
                case "sent":

                    btnAccept.setVisibility(View.GONE);
                    imgIsSharingLocation.setVisibility(View.GONE);
                    btnDecline.setText(R.string.cancelRequest);
                    btnSendLocation.setVisibility(View.GONE);
                    imgDelete.setVisibility(View.GONE);
                    txtRequestDes.setVisibility(View.VISIBLE);
                    txtRequestDes.setText(R.string.requestSentTo);
                    txtRequestDes.setTextColor(this.layoutRequestReceivedBinding.getRoot().getContext().getResources().getColor(R.color.colorRed));


                    break;
                case "declined":
                    btnAccept.setText(R.string.Pending);
                    btnAccept.setVisibility(View.VISIBLE);
                    btnDecline.setVisibility(View.GONE);
                    btnSendLocation.setVisibility(View.GONE);
                    imgDelete.setVisibility(View.GONE);
                    btnAccept.setEnabled(false);

                    break;
            }


        }

        //display location sharing
        void isSharingLocation(boolean isSharingLocation) {
            if (!isSharingLocation) {
                imgIsSharingLocation.setVisibility(View.VISIBLE);
                imgIsSharingLocation.setImageDrawable(ContextCompat.getDrawable(layoutRequestReceivedBinding.getRoot().getContext(), R.drawable.redoffline));
                btnSendLocation.setText(R.string.sendLocation);

            } else {

                imgIsSharingLocation.setVisibility(View.VISIBLE);
                imgIsSharingLocation.setImageDrawable(ContextCompat.getDrawable(layoutRequestReceivedBinding.getRoot().getContext(), R.drawable.green));
                btnSendLocation.setText(R.string.stpLocSharing);
                btnSendLocation.setTextColor(layoutRequestReceivedBinding.getRoot().getContext().getResources().getColor(R.color.white));


            }

        }


        @Override
        public void onClick(View view) {
            onItemClickListener.onClick(layoutRequestReceivedBinding.getRoot(), getAdapterPosition());
            // onItemClickListener.onClickLocation(btnSendLocation, getAdapterPosition());


        }
    }


}
