package com.example.jameedean.nav2.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class AgencyModel {

    private String name_agency;
    private String email_agency;
    private String address_agency;
    private String phone_num;
    private long createdAt;

    public AgencyModel() {}

    public AgencyModel(String name_agency, String email_agency,String address_agency, String phone_num, long createdAt) {
        this.name_agency = name_agency;
        this.email_agency = email_agency;
        this.address_agency = address_agency;
        this.phone_num = phone_num;
        this.createdAt = createdAt;
    }

    public String getName() {
        return name_agency;
    }

    public void setName(String name_agency) {
        this.name_agency = name_agency;
    }

    public String getEmail() {
        return email_agency;
    }

    public void setEmail(String email_agency) {
        this.email_agency = email_agency;
    }

    public String getAddress_agency() {return address_agency;}

    public void setAddress_agency() {this.address_agency = address_agency;}

    public String getPhone_num() {return phone_num;}

    public void setPhone_num() {this.phone_num = phone_num;}

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
