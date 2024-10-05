package com.example.myproject;

public class UserDetails {
    private String userName;
    private String userEmail;
    private String userPwd;
    private String userPhone;

    public UserDetails (String userName, String userEmail, String userPwd, String userPhone){
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPwd = userPwd;
        this.userPhone = userPhone;
    }
    public UserDetails (String userEmail, String userPwd){
        this.userEmail = userEmail;
        this.userPwd = userPwd;
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }
}
