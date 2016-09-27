package com.example.kitz0001.sciprospr;

import android.graphics.Picture;
import android.graphics.drawable.Icon;
import android.media.Image;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.Objects;


public class DataColumn implements Parcelable{
    private int dataIcon;
    private String dataType;
    private String columnName;
    private int digits = 2;
    private ArrayList<String> value =new ArrayList<>();
    private int decimals = 1;

    public DataColumn(String dataType, String columnName){
        this.dataType = dataType;
        this.dataIcon = 0;
        this.value= new ArrayList<>();
        switch(dataType){
            case "INTEGER":// TODO: Change all strings to string references
                this.dataIcon=R.drawable.integer;
                break;
            case "LONG":
                this.dataIcon=R.drawable.lng;
                break;
            case "TEXT":
                this.dataIcon=R.drawable.text;
                break;
            case "COORDINATES":
                this.dataIcon=R.drawable.position;
                break;
            case "TIMESTAMP":
                this.dataIcon=R.drawable.date;
                break;
            case "PICTURE":
                this.dataIcon=R.drawable.pic;
                break;
        }
        this.columnName = columnName;
    }

    protected DataColumn(Parcel in) {
        dataIcon = in.readInt();
        dataType = in.readString();
        columnName = in.readString();
        digits = in.readInt();
        decimals = in.readInt();
    }

    public static final Creator <DataColumn> CREATOR = new Creator<DataColumn>() {
        @Override
        public DataColumn createFromParcel(Parcel in) {
            return new DataColumn(in);
        }

        @Override
        public DataColumn[] newArray(int size) {
            return new DataColumn[size];
        }
    };

    public int getIcon(){return dataIcon;}

    public String getType(){ return dataType;}

    public String getName(){ return columnName;}

    public void setDigits(int diginput){this.digits = diginput;}

    public int getDigits(){return  digits;}

    public void setDecimals(int dec){this.decimals = dec;}

    public int getDecimals(){return decimals;}

    public String getValue(int val){return value.get(val);}

    public void addValue(String s){value.add(s);}

    public int getValueSize(){return value.size();}


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(dataIcon);
        dest.writeString(dataType);
        dest.writeString(columnName);
        dest.writeInt(digits);
        dest.writeInt(decimals);
    }
}
