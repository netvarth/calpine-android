package com.jaldeeinc.jaldee.response;

import java.io.Serializable;

public class CustomerDetails implements Serializable {
    String dob;
    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }
    String firstName;
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    String lastName;

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    String gender;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJaldeeId() {
        return jaldeeId;
    }

    public void setJaldeeId(String jaldeeId) {
        this.jaldeeId = jaldeeId;
    }

    String jaldeeId;





}
