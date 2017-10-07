package com.example.dailynote;

/**
 * Created by Siyu Zhang on 2017/10/2.
 */

public class Users {

    @com.google.gson.annotations.SerializedName("username")
    private String username;

    @com.google.gson.annotations.SerializedName("email")
    private String email;

    @com.google.gson.annotations.SerializedName("password")
    private String password;

    @com.google.gson.annotations.SerializedName("Id")
    private String id;

    @com.google.gson.annotations.SerializedName("registerType")
    private int registerType;

    @com.google.gson.annotations.SerializedName("useGoogle")
    private Boolean useGoogle;

    @com.google.gson.annotations.SerializedName("useFB")
    private Boolean useFB;

    @com.google.gson.annotations.SerializedName("useWechat")
    private Boolean useWechat;

    @com.google.gson.annotations.SerializedName("additionalData")
    private String additionalData;

    public Users(){

    }

    public Users(String username, String email, int registerType,
                 String password, String additionalData,boolean
                         useWechat,boolean useFB,boolean useGoogle ){
        this.setUsername(username);
        this.setEmail(email);
        this.setPassword(password);
//        this.setId(id);
        this.setAdditionalData(additionalData);
        this.setRegisterType(registerType);
        this.setUseFB(useFB);
        this.setUseGoogle(useGoogle);
        this.setUseWechat(useWechat);
    }

    public void setUsername(String u){
        this.username = u;
    }

    public String getUsername(){return username;}

    public void setEmail(String e) {
        this.email = e;
    }

    public String getEmail() {
        return email;
    }

    public void setPassword(String p) {
        this.password = p;
    }

    public String getPassword() {
        return password;
    }

//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public String getId() {
//        return id;
//    }

    public String getId() {
        return id;
    }

    public final void setId(String id) {
        this.id = id;
    }


    public String getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(String additionalData) {
        this.additionalData = additionalData;
    }

    public void setUseFB(boolean useFB) {
        this.useFB = useFB;
    }

    public void setUseGoogle(boolean useGoogle) {
        this.useGoogle = useGoogle;
    }

    public void setUseWechat(boolean useWechat) {
        this.useWechat = useWechat;
    }

    public boolean isUseFB() {
        return useFB;
    }

    public boolean isUseGoogle() {
        return useGoogle;
    }

    public boolean isUseWechat() {
        return useWechat;
    }

    public int getRegisterType() {
        return registerType;
    }

    public void setRegisterType(int registerType) {
        this.registerType = registerType;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Users && ((Users) o).email == email;
    }
}
