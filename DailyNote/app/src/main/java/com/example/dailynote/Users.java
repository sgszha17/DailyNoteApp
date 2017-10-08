/**
 * @(Users).java     1.61 08/10/2017
 *
 * Copyright 2017 University of Melbourne. All rights reserved.
 *
 * @author Dawei Wang
 * @email daweiw@student.unimelb.edu.au
 *
 * @author Siyu Zhang
 * @email siyuz6@student.unimelb.edu.au
 *
 * @author Tong Zou
 * @email tzou2@student.unimelb.edu.au
 *
 **/
package com.example.dailynote;

public class Users {

    /**
     * Username of user
     */
    @com.google.gson.annotations.SerializedName("username")
    private String username;

    /**
     * Email of user
     */
    @com.google.gson.annotations.SerializedName("email")
    private String email;

    /**
     * Password of user
     */
    @com.google.gson.annotations.SerializedName("password")
    private String password;

    /**
     * ID of user
     */
    @com.google.gson.annotations.SerializedName("Id")
    private String id;

    /**
     * Register type of user
     */
    @com.google.gson.annotations.SerializedName("registerType")
    private int registerType;

    /**
     * Whether use google account to login
     */
    @com.google.gson.annotations.SerializedName("useGoogle")
    private Boolean useGoogle = false;

    /**
     * Whether use FaceBook account to login
     */
    @com.google.gson.annotations.SerializedName("useFB")
    private Boolean useFB = false;

    /**
     * Whether user wechat account to login
     */
    @com.google.gson.annotations.SerializedName("useWechat")
    private Boolean useWechat = false;

    /**
     * Anothre data need to be store.
     */
    @com.google.gson.annotations.SerializedName("additionalData")
    private String additionalData = "";

    /**
     * Constructor of Object Users without parameters.
     */
    public Users(){

    }

    /**
     * Constructor of Object Users with parameters.
     * @param username
     * @param email
     * @param password
     * @param registerType
     * @param useFB
     * @param useGoogle
     * @param useWechat
     * @param additionalData
     */
    public Users(String username, String email, int registerType,
                 String password, String additionalData,boolean
                         useWechat,boolean useFB,boolean useGoogle ){
        this.setUsername(username);
        this.setEmail(email);
        this.setPassword(password);
        this.setAdditionalData(additionalData);
        this.setRegisterType(registerType);
        this.setUseFB(useFB);
        this.setUseGoogle(useGoogle);
        this.setUseWechat(useWechat);
    }

    /**
     * Sets the username
     *
     * @param u
     *            username to set
     */
    public void setUsername(String u){
        this.username = u;
    }

    /**
     * Returns the username of user
     */
    public String getUsername(){return username;}


    /**
     * Sets the email
     *
     * @param e
     *            email to set
     */
    public void setEmail(String e) {
        this.email = e;
    }

    /**
     * Returns the email of user
     */
    public String getEmail() {
        return email;
    }


    public void setPassword(String p) {
        this.password = p;
    }

    /**
     * Returns the password of user
     */
    public String getPassword() {
        return password;
    }


    /**
     * Returns the ID of user
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the email
     *
     * @param id
     *            id to set
     */
    public final void setId(String id) {
        this.id = id;
    }


    /**
     * Returns the additional data of user
     */
    public String getAdditionalData() {
        return additionalData;
    }

    /**
     * Sets the email
     *
     * @param additionalData
     *            addition data to set
     */
    public void setAdditionalData(String additionalData) {
        this.additionalData = additionalData;
    }


    /**
     * Sets the email
     *
     * @param useFB
     *            uerFB to set
     */
    public void setUseFB(boolean useFB) {
        this.useFB = useFB;
    }

    /**
     * Returns whether user use FaceBook account
     */
    public boolean isUseFB() {
        return useFB;
    }

    /**
     * Sets the email
     *
     * @param useGoogle
     *            useGoogle to set
     */
    public void setUseGoogle(boolean useGoogle) {
        this.useGoogle = useGoogle;
    }

    /**
     * Returns whether user use Google account
     */
    public boolean isUseGoogle() {
        return useGoogle;
    }


    /**
     * Sets the email
     *
     * @param useWechat
     *            useWechat to set
     */
    public void setUseWechat(boolean useWechat) {
        this.useWechat = useWechat;
    }

    /**
     * Returns whether user use wechat account
     */
    public boolean isUseWechat() {
        return useWechat;
    }


    /**
     * Returns the type of user account
     */
    public int getRegisterType() {
        return registerType;
    }

    /**
     * Sets the email
     *
     * @param registerType
     *            register type to set
     */
    public void setRegisterType(int registerType) {
        this.registerType = registerType;
    }


    @Override
    public boolean equals(Object o) {
        return o instanceof Users && ((Users) o).id == id;
    }
}
