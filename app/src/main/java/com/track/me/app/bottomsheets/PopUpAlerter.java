package com.track.me.app.bottomsheets;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.track.me.app.R;
import com.track.me.app.activities.BaseActivity;
import com.track.me.app.constants.AppConstants;
import com.track.me.app.databinding.PopUpAlerterBottomSheetBinding;
import com.track.me.app.utils.DisplayViewUI;

import java.util.HashMap;
import java.util.Map;

public class PopUpAlerter extends BottomSheetDialogFragment {
    PopUpAlerterBottomSheetBinding popUpAlerterBottomSheetBinding;
    private static final String TAG = "PopUpAlerter";
    private String name;
    private String id;
    private String phoneNumber;
    private String photoUrl;
    String senderId;
    private AppCompatButton btnAddUser;
    private CollectionReference friendsCollectionReference, userCR;
    ConstraintLayout constraintLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        popUpAlerterBottomSheetBinding = DataBindingUtil.inflate(inflater, R.layout.pop_up_alerter_bottom_sheet, container, false);

        return popUpAlerterBottomSheetBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews();


    }

    @SuppressLint("SetTextI18n")
    private void initViews() {
        //ProgressDialog progressBar = DisplayViewUI.displayProgress(requireActivity(), getString(R.string.addingUser));
        TextView txtName = popUpAlerterBottomSheetBinding.txtName;
        ImageView imgPhoto = popUpAlerterBottomSheetBinding.imgPhoto;
        btnAddUser = popUpAlerterBottomSheetBinding.btnAddUser;
        constraintLayout = popUpAlerterBottomSheetBinding.linearLayout2;

        friendsCollectionReference = FirebaseFirestore.getInstance().collection("Friends");
        userCR = FirebaseFirestore.getInstance().collection("Users");

        Bundle getUserDetailsBundle = getArguments();
        if (getUserDetailsBundle != null) {

            name = getUserDetailsBundle.getString(AppConstants.USER_NAME);
            id = getUserDetailsBundle.getString(AppConstants.UID);
            photoUrl = getUserDetailsBundle.getString(AppConstants.USER_PHOTO_URL);
            //  phoneNumber = getUserDetailsBundle.getString(AppConstants.PHONE_NUMBER);

            txtName.setText(name);
            Glide.with(requireActivity()).load(photoUrl).into(imgPhoto);


        }
        //sender details
        String senderName = BaseActivity.userName;
        senderId = BaseActivity.uid;
        String senderPhotoUrl = BaseActivity.userPhotoUrl;
        // String senderPhoneNumber = BaseActivity.phoneNumber;

        //receiver
        Map<String, Object> from = new HashMap<>();
        from.put("id", senderId);
        from.put("name", senderName);
        from.put("photo", senderPhotoUrl);
        // from.put("phoneNumber", senderPhoneNumber);
        from.put("response", "received");
        from.put("isSharingLocation", false);

        //sender
        Map<String, Object> to = new HashMap<>();
        to.put("id", id);
        to.put("name", name);
        to.put("photo", photoUrl);
        to.put("phoneNumber", phoneNumber);
        to.put("response", "sent");
        from.put("isSharingLocation", false);

        if (BaseActivity.friends.contains(id)) {
            btnAddUser.setText("Added");
            btnAddUser.setOnClickListener(v -> DisplayViewUI.displayToast(getActivity(), "Already added"));
        } else {
            btnAddUser.setOnClickListener(view -> {
                try {
                    friendsCollectionReference.document(senderId).collection(senderId).document(id).set(to);
                    //update user friends list
                    userCR.document(senderId).update("friends", FieldValue.arrayUnion(id));
                    userCR.document(id).update("friends", FieldValue.arrayUnion(senderId));


                    friendsCollectionReference.document(id).collection(id).document(senderId).set(from);
                    //  DisplayViewUI.displaySnackBar(constraintLayout,"Request sent to " + name);
                    new Handler().postDelayed(this::dismiss, 2000);

                } catch (Exception e) {

                    e.printStackTrace();
                }

            });
        }


        popUpAlerterBottomSheetBinding.btnCancel.setOnClickListener(v -> dismiss());

        //Double checking
        //1. check friends db
        //2. check the senders and receivers node respectively
        //3. check the response and update the UI
        Query query = friendsCollectionReference.document(senderId).collection(senderId);


    }

    void deleteOrRemoveUser() {
        friendsCollectionReference.document(id).collection(id).document(senderId).delete();
        friendsCollectionReference.document(senderId).collection(senderId).document(id).delete();
        new Handler().postDelayed(this::dismiss, 2000);
    }

}
