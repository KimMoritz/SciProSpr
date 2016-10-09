package com.example.kitz0001.sciprospr;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class TagSet implements Parcelable{
    private List<String> tagNames;

    TagSet(String[] names){
        tagNames = new ArrayList<>();
        for (String s: names){
            tagNames.add(s);
        }
    }

    public static final Creator <TagSet> CREATOR = new Creator<TagSet>() {
        @Override
        public TagSet createFromParcel(Parcel in) {
            return new TagSet(in);
        }

        @Override
        public TagSet[] newArray(int size) {
            return new TagSet[size];
        }
    };

    public TagSet(Parcel in) {

    }

    void setTagName(int i, String s){
        tagNames.set(i, s);
    }

    void addTagName(String s){
        tagNames.add(s);
    }

    int length(){
        return tagNames.size();
    }

    String getTagName(int i){
        return tagNames.get(i);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
