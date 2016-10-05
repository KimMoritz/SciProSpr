package com.example.kitz0001.sciprospr;

import java.util.ArrayList;
import java.util.List;

public class TagSet {
    List<String> tagNames;

    TagSet(String... names){
        tagNames = new ArrayList<>();
        for (String s: names){
            tagNames.add(s);
        }
    }
}
