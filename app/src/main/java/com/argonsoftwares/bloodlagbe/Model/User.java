package com.argonsoftwares.bloodlagbe.Model;

public class User {
    String bloodGroup, email, fullName, id, nid, phoneNumber, profileImageUrl, search, type;

    public User() {

    }

    public User(String bloodGroup, String email, String fullName, String id, String nid, String phoneNumber, String profileImageUrl, String search, String type) {
        this.bloodGroup = bloodGroup;
        this.email = email;
        this.fullName = fullName;
        this.id = id;
        this.nid = nid;
        this.phoneNumber = phoneNumber;
        this.profileImageUrl = profileImageUrl;
        this.search = search;
        this.type = type;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
