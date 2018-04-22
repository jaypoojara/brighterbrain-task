package com.brighterbrain.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by jaypoojara on 22-04-2018.
 */
public class Item implements Parcelable {
    private int id;
    private String name;
    private String description;
    private String latitude;
    private String longitude;
    private int cost;
    private ArrayList<ItemImage> itemImages;

    public ArrayList<ItemImage> getItemImages() {
        return itemImages;
    }

    public void setItemImages(ArrayList<ItemImage> itemImages) {
        this.itemImages = itemImages;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public int getId() {
        return id;

    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
    public Item(){

    }
    public Item(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        cost = in.readInt();
        if (in.readByte() == 0x01) {
            itemImages = new ArrayList<ItemImage>();
            in.readList(itemImages, ItemImage.class.getClassLoader());
        } else {
            itemImages = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeInt(cost);
        if (itemImages == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(itemImages);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
}