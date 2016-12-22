package com.moritz.SciProSpr.sciprospr;

import java.util.ArrayList;

public class TxtWriter {
    public StringBuilder buildStringFromData(StringBuilder stringBuilderIn, ArrayList<DataColumn> dataColumnsIn){
        // Write headers
        for (int i = 0; i < dataColumnsIn.size()-1; i++) {
            stringBuilderIn.append(dataColumnsIn.get(i).getName());
            if(i != dataColumnsIn.size()-2)
                stringBuilderIn.append("\t");
        }
        stringBuilderIn.append("\n");

        // Write data
        for (int m = 0; m < dataColumnsIn.get(dataColumnsIn.size()-2).getValueSize(); m++) {
            for (int i = 0; i < dataColumnsIn.size()-1; i++) {
                String value = dataColumnsIn.get(i).getValue(m);
                stringBuilderIn.append(value);
                if(i != dataColumnsIn.size()-2)
                    stringBuilderIn.append("\t");
                if(i==dataColumnsIn.size()-2)
                    stringBuilderIn.append("\n");
            }
        }
        return stringBuilderIn;
    }
}
