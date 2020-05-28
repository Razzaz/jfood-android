package com.example.jfood_android;

import android.os.Parcel;
import android.os.Parcelable;

public class Seller implements Parcelable {

    private int id;
    private String name;
    private String email;
    private String phoneNumber;
    private Location location;

    public Seller(int id, String name, String email, String phoneNumber, Location location){
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.location = location;
    }

    protected Seller(Parcel in) {
        id = in.readInt();
        name = in.readString();
        email = in.readString();
        phoneNumber = in.readString();
    }

    public static final Creator<Seller> CREATOR = new Creator<Seller>() {
        @Override
        public Seller createFromParcel(Parcel in) {
            return new Seller(in);
        }

        @Override
        public Seller[] newArray(int size) {
            return new Seller[size];
        }
    };

    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String getEmail(){
        return email;
    }

    public String getPhoneNumber(){
        return phoneNumber;
    }

    public Location getLocation(){
        return location;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String toString()
    {
        return "= Seller ===============================" +
                "\nId     : " + id +
                "\nName      : " + name +
                "\nEmail          : " + email +
                "\nPhone   : " + phoneNumber +
                "\nLocation      : " + location +
                "\n==========================================";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(email);
        parcel.writeString(phoneNumber);
    }
}
