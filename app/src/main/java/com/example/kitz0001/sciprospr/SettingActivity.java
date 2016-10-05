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
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import java.util.*;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener  {
    private List<DataColumn> dataCols = new ArrayList<>();
    private String inputText = "";
    private String inputText2 = "";
    public DataColumn col = null;
    private boolean padded = false;
    private boolean requiresInput = false;
    Button butInt, butLng, butStr, butGPS, butPho, butDat, butDel, butDon, butEnum;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        populateListView();
        butInt = (Button) findViewById(R.id.btnInt);
        butLng = (Button) findViewById(R.id.btnLon);
        butStr = (Button) findViewById(R.id.btnStr);
        butGPS = (Button) findViewById(R.id.btnGPS);
        butPho = (Button) findViewById(R.id.btnPho);
        butDat = (Button) findViewById(R.id.btnDat);
        butDel = (Button) findViewById(R.id.delBut);
        butDon = (Button) findViewById(R.id.btnDone);
        butEnum = (Button) findViewById(R.id.btnEnum);
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
        ArrayAdapter<DataColumn> adapter = new TableListAdapter(SettingActivity.this,R.layout.item_view, (ArrayList) dataCols);
        ListView list = (ListView) findViewById(R.id.listView);
        assert list != null;
        list.setAdapter(adapter);
    }

    @Override
    public void onClick(View arg0) {
        if(padded&arg0.getId()!=R.id.btnDone){
            dataCols.remove(dataCols.size()-1);
            padded=false;
        }
        switch(arg0.getId()){
            case R.id.btnInt:
                askForName(getString(R.string.integer_string));
                requiresInput=true;
                break;
            case R.id.btnLon:
                askForName(getString(R.string.long_string));
                requiresInput=true;
                break;
            case R.id.btnStr:
                askForName(getString(R.string.text_string));
                requiresInput=true;
                break;
            case R.id.btnGPS:
                askForName(getString(R.string.coordinates_string));
                break;
            case R.id.btnDat:
                askForName(getString(R.string.timestamp_string));
                break;
            case R.id.btnEnum:
                askForName(getString(R.string.enum_string));
                break;
            case R.id.btnPho:
                //askForName(getString(R.string.picture_string));
                break;
            case R.id.delBut:
                dataCols.clear();
                padded = false;
                requiresInput=false;
                break;
            case R.id.btnDone:
                Intent intentInput = new Intent(SettingActivity.this, Datafeed.class);
                if(dataCols.isEmpty()){
                    dataCols.add(0, new DataColumn(getString(R.string.integer_string), "Column A"));
                    dataCols.add(1, new DataColumn(getString(R.string.integer_string), "Column B"));
                    dataCols.add(2, new DataColumn(getString(R.string.integer_string), "Column C"));
                    dataCols.add(3, new DataColumn(getString(R.string.integer_string), "Column D"));
                }
                if(requiresInput){
                    if (!padded) {
                        dataCols.add(new DataColumn(getString(R.string.text_string), "Padding end column"));
                        padded=true;
                    }
                    intentInput.putParcelableArrayListExtra("DataColumns" , (ArrayList<DataColumn>) dataCols);
                    startActivity(intentInput);
                }else{
                    Toast.makeText(this, "At least one column must be of a data type that requires user input (integer, long, tag or text)", Toast.LENGTH_SHORT).show();
                }
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
                if(col.getType().equals(getString(R.string.integer_string)) | col.getType().equals(getString(R.string.text_string))){
                    askNoDigits(col);
                }
                if (col.getType().equals(getString(R.string.long_string))){
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

    void setStrings(int number){                //TODO: Complete function.
        String[] tags = new String[number];
        for(String tag : tags){
            tag = "";
        }
        TagSet tagSet = new TagSet(tags);
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

    protected void askLongDigits (final DataColumn dc){ //TODO: split into several methods ( much too long). Synergy between data type specific digit-askers.
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
}