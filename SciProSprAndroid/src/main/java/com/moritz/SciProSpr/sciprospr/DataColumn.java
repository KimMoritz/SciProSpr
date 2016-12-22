package com.moritz.SciProSpr.sciprospr;

import android.os.*;
import java.util.ArrayList;

public class DataColumn implements Parcelable{
    private int digits, decimals, dataIcon;
    private dataTypeEnum dataType;
    private String columnName;
    private ArrayList<String> value;

    public DataColumn(dataTypeEnum dataType, String columnName){
        this.dataType = dataType;
        this.digits =2;
        this.decimals = 2;
        this.dataIcon = 0;
        this.value= new ArrayList<>();
        switch(dataType){
            case INTEGER:
                this.dataIcon=R.drawable.integer;
                break;
            case LONG:
                this.dataIcon=R.drawable.lng;
                break;
            case TEXT:
                this.dataIcon=R.drawable.text;
                break;
            case COORDINATES:
                this.dataIcon=R.drawable.position;
                break;
            case TIMESTAMP:
                this.dataIcon=R.drawable.date;
                break;
            case PICTURE:
                this.dataIcon=R.drawable.pic;
                break;
            case TAGS:
                this.dataIcon=R.drawable.text;
                break;
        }
        this.columnName = columnName;
    }

    protected DataColumn(Parcel inParcel) {
        dataIcon = inParcel.readInt();
        dataType = (dataTypeEnum) inParcel.readSerializable();
        columnName = inParcel.readString();
        digits = inParcel.readInt();
        decimals = inParcel.readInt();
        value = inParcel.readArrayList(Object.class.getClassLoader());
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

    //Setters & getters
    public int getIcon(){return dataIcon;}
    dataTypeEnum getType(){ return dataType;}
    public String getName(){ return columnName;}
    void setDigits(int diginput){this.digits = diginput;}
    int getDigits(){return  digits;}
    void setDecimals(int dec){this.decimals = dec;}
    int getDecimals(){return decimals;}
    String getValue(int val){return value.get(val);}
    void addValue(String s){value.add(s);}
    int getValueSize(){return value.size();}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(dataIcon);
        dest.writeSerializable(dataType);
        dest.writeString(columnName);
        dest.writeInt(digits);
        dest.writeInt(decimals);
        dest.writeList(value);
    }
}