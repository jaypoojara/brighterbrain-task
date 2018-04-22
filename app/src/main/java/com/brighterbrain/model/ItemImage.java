package com.brighterbrain.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jaypoojara on 22-04-2018.
 */
public class ItemImage implements Parcelable {
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ItemImage> CREATOR = new Parcelable.Creator<ItemImage>() {
        @Override
        public ItemImage createFromParcel(Parcel in) {
            return new ItemImage(in);
        }

        @Override
        public ItemImage[] newArray(int size) {
            return new ItemImage[size];
        }
    };
    private int id;
    private String imagePath;
    private int item_id;

    public ItemImage(){

    }

    public ItemImage(Parcel in) {
        id = in.readInt();
        imagePath = in.readString();
        item_id = in.readInt();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(imagePath);
        dest.writeInt(item_id);
    }
}