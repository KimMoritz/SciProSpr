package com.moritz.SciProSpr.sciprospr;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

class TableListAdapter extends ArrayAdapter<DataColumn> {
    ArrayList arrayList;
    SettingActivity settingActivity;
    public TableListAdapter(SettingActivity settingActivityIn, int layout, ArrayList arrayListIn) {
        super(settingActivityIn, layout, arrayListIn);
        arrayList = arrayListIn;
        settingActivity = settingActivityIn;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO: Fill out the view that the visual list representation presents.
        View itemView = convertView;
        if (itemView == null) {
            //Populate using inflater.
            itemView = settingActivity.getLayoutInflater().inflate(R.layout.item_view, parent, false);
        }
        DataColumn chosenColumn = (DataColumn) arrayList.get(position);
        //TODO: Fill out icon, name and data type.
        ImageView makeIcon = (ImageView) itemView.findViewById(R.id.imageView);
        makeIcon.setImageResource(chosenColumn.getIcon());
        TextView makeText = (TextView) itemView.findViewById(R.id.colName);
        makeText.setText(chosenColumn.getName());
        TextView makeText2 = (TextView) itemView.findViewById(R.id.dataTyp);
        makeText2.setText(chosenColumn.getType().toString());
        return itemView;
    }
}