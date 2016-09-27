package com.example.kitz0001.sciprospr;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener  {
    private List<DataColumn> dataCols = new ArrayList<>();
    private String inputText = "";
    private String inputText2 = "";
    public DataColumn col = null;
    private boolean padded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        populateListView();

        //Button links required
        Button butInt = (Button) findViewById(R.id.btnInt);
        Button butLng = (Button) findViewById(R.id.btnLon);
        Button butStr = (Button) findViewById(R.id.btnStr);
        Button butGPS = (Button) findViewById(R.id.btnGPS);
        Button butPho = (Button) findViewById(R.id.btnPho);
        Button butDat = (Button) findViewById(R.id.btnDat);
        Button butDel = (Button) findViewById(R.id.delBut);
        Button butDon = (Button) findViewById(R.id.btnDone);
        Button butEnum = (Button) findViewById(R.id.btnEnum);
        try{
            butInt.setOnClickListener(this);
            butLng.setOnClickListener(this);
            butStr.setOnClickListener(this);
            butGPS.setOnClickListener(this);
            butPho.setOnClickListener(this);
            butDel.setOnClickListener(this);
            butDon.setOnClickListener(this);
            butDat.setOnClickListener(this);
            butEnum.setOnClickListener(this);
        }
        catch (Exception e){}
    }

    private void populateListView() {
        //Add objects to the list of data columns
        ArrayAdapter<DataColumn> adapter = new MyListAdapter();
        ListView list = (ListView) findViewById(R.id.listView);
        assert list != null;
        list.setAdapter(adapter);
    }

    @Override
    //What happens when which button is clicked.
    public void onClick(View arg0) {
        if(padded&arg0.getId()!=R.id.btnDone){
            dataCols.remove(dataCols.size()-1);
            padded=false;
        }
        switch(arg0.getId()){
            case R.id.btnInt:
                askForName("INTEGER");
                break;
            case R.id.btnLon:
                askForName("LONG");
                break;
            case R.id.btnStr:
                askForName("TEXT");
                break;
            case R.id.btnGPS:
                askForName("COORDINATES");
                break;
            case R.id.btnDat:
                askForName("TIMESTAMP");
                break;
            case R.id.btnEnum:
                //askForName("ENUM");
                break;
            case R.id.btnPho:
                //askForName("PICTURE");
                break;
            case R.id.delBut:
                dataCols.clear();
                padded = false;
                break;
            case R.id.btnDone:
                Intent intentInput = new Intent(SettingActivity.this, Datafeed.class);
                if(dataCols.isEmpty()){
                    dataCols.add(0, new DataColumn("TEXT", "Column A"));
                    dataCols.add(1, new DataColumn("TEXT", "Column B"));
                    dataCols.add(2, new DataColumn("TEXT", "Column C"));
                    dataCols.add(3, new DataColumn("TEXT", "Column D"));
                } else if (!padded) {
                    dataCols.add(new DataColumn("TEXT", "Padding end column"));
                    padded=true;
                }
                intentInput.putParcelableArrayListExtra("DataColumns" , (ArrayList<DataColumn>) dataCols);
                startActivity(intentInput);
                break;
        }
        populateListView();
    }

    //Dialog for getting names of the columns (as strings in objects)
    protected void askForName(final String type){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.name_column));
        final EditText input = new EditText(this);
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.requestFocus();
        builder.setView(input);
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(input.length()<1){
                    input.setText(R.string.unnamed_column);
                }
                inputText = input.getText().toString();
                //Adds the column to the dataCols ArrayList and gives the name from input dialog and type from button clicked.
                dataCols.add(new DataColumn(type, inputText));
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                col = dataCols.get(dataCols.size() - 1);
                if(col.getType().equals("INTEGER") | col.getType().equals("TEXT")){
                    askNoDigits(col);
                }
                if (col.getType().equals("LONG")){
                    askLongDigits(col);
                }
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                inputText = getString(R.string.unnamed_column);
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
            }
        });
        builder.show();
    }

    //Dialog for setting number of digits of the columns (as integers in DataColumn objects)
    protected void askNoDigits(final DataColumn dc){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.how_many_digits));
        final EditText digits = new EditText(this);
        InputFilter[] filter = new InputFilter[1];
        filter[0] = new InputFilter.LengthFilter(1);
        digits.setFilters(filter);
        //use screen keyboard
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        digits.setInputType(InputType.TYPE_CLASS_NUMBER);
        digits.requestFocus();
        builder.setView(digits);
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (digits.length()<1){
                    digits.setText("1");
                }
                inputText = digits.getText().toString();
                int inputInt = Integer.parseInt(inputText);
                dc.setDigits(inputInt);
                imm.hideSoftInputFromWindow(digits.getWindowToken(), 0);
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dc.setDigits(1);
                imm.hideSoftInputFromWindow(digits.getWindowToken(), 0);
            }
        });
        builder.show();
    }

    protected void askLongDigits (final DataColumn dc){ //TODO: merge with above method to avoid code duplications.
                                                        //TODO: split into several methods (too long).
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LinearLayout lila1= new LinearLayout(this);
        lila1.setOrientation(LinearLayout.VERTICAL);
        builder.setTitle(getString(R.string.how_many_decimal_digits));
        final EditText digits = new EditText(this);
        digits.setHint("no. of digits");
        final EditText deci = new EditText(this);
        deci.setHint("no. of decimals");
        InputFilter[] filter = new InputFilter[1];
        filter[0] = new InputFilter.LengthFilter(1);
        digits.setFilters(filter);
        deci.setFilters(filter);
        //use screen keyboard
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        digits.setInputType(InputType.TYPE_CLASS_NUMBER);
        deci.setInputType(InputType.TYPE_CLASS_NUMBER);
        digits.requestFocus();
        lila1.addView(digits);
        lila1.addView(deci);
        builder.setView(lila1);
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (digits.length()<1){
                    digits.setText("1");
                }
                if (deci.length()<1){
                    deci.setText("1");
                }
                inputText = digits.getText().toString();
                inputText2 = deci.getText().toString();
                int inputInt = Integer.parseInt(inputText);
                int inputInt2 = Integer.parseInt(inputText2);
                dc.setDigits(inputInt);
                dc.setDecimals(inputInt2);
                imm.hideSoftInputFromWindow(digits.getWindowToken(), 0);
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dc.setDigits(2);
                dc.setDecimals(1);
                imm.hideSoftInputFromWindow(digits.getWindowToken(), 0);
            }
        });
        builder.show();
    }

    private class MyListAdapter extends ArrayAdapter<DataColumn> {  //Make package private and move to a separate class file.
        public MyListAdapter() {
            super(SettingActivity.this, R.layout.item_view, dataCols);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Fill out the view that the visual list representation presents.
            View itemView = convertView;
            if (itemView == null) {
                //Populate using inflater.
                itemView = getLayoutInflater().inflate(R.layout.item_view, parent, false);
            }
            DataColumn chosenColumn = dataCols.get(position);

            //Fill out icon, name and data type.
            ImageView makeIcon = (ImageView) itemView.findViewById(R.id.imageView);
            makeIcon.setImageResource(chosenColumn.getIcon());
            TextView makeText = (TextView) itemView.findViewById(R.id.colName);
            makeText.setText(chosenColumn.getName());
            TextView makeText2 = (TextView) itemView.findViewById(R.id.dataTyp);
            makeText2.setText(chosenColumn.getType());

            return itemView;
        }
    }
}
