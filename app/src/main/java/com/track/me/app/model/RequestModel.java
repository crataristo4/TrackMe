package com.track.me.app.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import java.util.List;

public class RequestModel extends BaseObservable {
    public String userName, userPhotoUrl, userId;
    public boolean isSharingLocation;
    private String id;
    private String name;
    private String photo;
    private String response;
    List<String> friends;


    public RequestModel() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public void setUserPhotoUrl(String userPhotoUrl) {
        this.userPhotoUrl = userPhotoUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }


    @Bindable
    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Bindable
    public boolean isSharingLocation() {
        return isSharingLocation;
    }

    public void setSharingLocation(boolean sharingLocation) {
        isSharingLocation = sharingLocation;
    }


    public List<String> getUFriends() {
        return friends;
    }

    public void setUsers(List<String> friends) {
        this.friends = friends;
    }
}
