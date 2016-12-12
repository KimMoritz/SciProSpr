package com.example.kitz0001.sciprospr;

import android.os.*;
import java.util.ArrayList;

public class DataColumn implements Parcelable{
    private int digits = 2, decimals = 1, dataIcon;
    private dataTypeEnum dataType;
    private String columnName;
    private ArrayList<String> value, tagSet;

    public DataColumn(dataTypeEnum dataType, String columnName){
        this.dataType = dataType;
        this.dataIcon = 0;
        this.value= new ArrayList<>();
        this.tagSet = new ArrayList<>();
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
        tagSet = inParcel.readArrayList(Object.class.getClassLoader());
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
    public dataTypeEnum getType(){ return dataType;}
    public String getName(){ return columnName;}
    public void setDigits(int diginput){this.digits = diginput;}
    public int getDigits(){return  digits;}
    public void setDecimals(int dec){this.decimals = dec;}
    public int getDecimals(){return decimals;}
    public String getValue(int val){return value.get(val);}
    public void addValue(String s){value.add(s);}
    public int getValueSize(){return value.size();}
    public void setTagSet(ArrayList<String> tagSetIn){
        tagSet = tagSetIn;
    }
    public String[] getTagSet(){
        String[] tagSetReturnString = new String[tagSet.size()];
        for(int i =0; i<tagSet.size();i++){tagSetReturnString[i] = tagSet.get(i);}
        return tagSetReturnString;
    }

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
        dest.writeList(tagSet);
    }
}