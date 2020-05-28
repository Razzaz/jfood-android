package com.example.jfood_android;

import android.os.Parcel;
import android.os.Parcelable;

public class Location implements Parcelable {

    private String province;
    private String description;
    private String city;

    public Location(String province, String description, String city){
        this.province = province;
        this.city = city;
        this.description = description;
    }

    protected Location(Parcel in) {
        province = in.readString();
        description = in.readString();
        city = in.readString();
    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    public String getProvince(){
        return province;
    }

    public String getDescription(){
        return description;
    }

    public String getCity(){
        return city;
    }

    public void setProvince(String province){
        this.province = province;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setCity(String city){
        this.city = city;
    }

    @Override
    public String toString()
    {
        return "= Location ===============================" +
                "\nProvince      : " + province +
                "\nCity          : " + city +
                "\nDescription   : " + description +
                "\n==========================================";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(province);
        parcel.writeString(description);
        parcel.writeString(city);
    }
}
