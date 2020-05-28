package com.example.jfood_android;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Food implements Parcelable{

    private int id;
    private String name;
    private int price;
    private String category;
    private Seller seller;

    public Food(int id, String name, int price, String category, Seller seller){
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.seller = seller;
    }

    protected Food(Parcel in) {
        id = in.readInt();
        name = in.readString();
        price = in.readInt();
        category = in.readString();
        seller = in.readParcelable(Seller.class.getClassLoader());
    }

    public static final Creator<Food> CREATOR = new Creator<Food>() {
        @Override
        public Food createFromParcel(Parcel in) {
            return new Food(in);
        }

        @Override
        public Food[] newArray(int size) {
            return new Food[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    @Override
    public String toString()
    {
        return "= Food ===============================" +
                "\nId     : " + id +
                "\nName      : " + name +
                "\nPrice          : " + price +
                "\nCategory   : " + category +
                "\nSeller      : " + seller +
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
        parcel.writeInt(price);
        parcel.writeString(category);
        parcel.writeParcelable(seller, i);
    }
}
