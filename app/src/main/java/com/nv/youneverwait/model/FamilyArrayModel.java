package com.nv.youneverwait.model;

import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by sharmila on 11/7/18.
 */

public class FamilyArrayModel  implements Serializable{

  /*  public List<FamilyModel> getmArrayuserProfile() {
        return mArrayuserProfile;
    }

    private List<FamilyModel>  mArrayuserProfile;*/
  public FamilyArrayModel getUserProfile() {
      return userProfile;
  }


    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    String user;

    public void setUserProfile(FamilyArrayModel userProfile) {
        this.userProfile = userProfile;
    }

    @SerializedName("userProfile")
    private FamilyArrayModel userProfile;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPrimaryMobileNo() {
        return primaryMobileNo;
    }

    public String getDob() {
        return dob;
    }

    public String getGender() {
        return gender;
    }

    public int getId() {
        return id;
    }

    int id;
    String firstName;

    public void setId(int id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    String lastName;
    String primaryMobileNo;
    String dob;
    String gender;


    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    boolean check=false;
}
