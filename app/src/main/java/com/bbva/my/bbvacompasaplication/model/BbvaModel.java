package com.bbva.my.bbvacompasaplication.model;

import android.os.Parcel;
import android.os.Parcelable;

public class BbvaModel implements Parcelable {

    private String name;
    private String address;
    private double lat;
    private double lang;

    BbvaModel(Builder builder) {
        this.name = builder.name;
        this.address = builder.address;
        this.lat = builder.lat;
        this.lang = builder.lang;
    }

    protected BbvaModel(Parcel in) {
        name = in.readString();
        address = in.readString();
        lat = in.readDouble();
        lang = in.readDouble();
    }

    public static final Creator<BbvaModel> CREATOR = new Creator<BbvaModel>() {
        @Override
        public BbvaModel createFromParcel(Parcel in) {
            return new BbvaModel(in);
        }

        @Override
        public BbvaModel[] newArray(int size) {
            return new BbvaModel[size];
        }
    };

    public static Builder getBuilder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLang() {
        return lang;
    }

    public void setLang(double lang) {
        this.lang = lang;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(address);
        parcel.writeDouble(lat);
        parcel.writeDouble(lang);
    }

    public static class Builder {
        private String name;
        private String address;
        private double lat;
        private double lang;

        public BbvaModel build() throws NullPointerException, IllegalArgumentException {
            return new BbvaModel(this);
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder setLat(double lat) {
            this.lat = lat;
            return this;
        }

        public Builder setLang(double lang) {
            this.lang = lang;
            return this;
        }
    }
}