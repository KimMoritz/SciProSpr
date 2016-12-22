package com.moritz.SciProSpr.sciprospr;

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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class SettingActivity extends AppCompatActivity implements View.OnClickListener  { //TODO: Byt klassens namn till SetupActivity
    private List<DataColumn> dataCols = new ArrayList<>();
    private String inputText = "", inputText2 = "";
    public DataColumn col = null;
    private boolean padded = false;
    private boolean requiresInput = false;
    private AdView mAdView3;//TODO: Se till att rätt kod laddas för banners
    Button butInt, butLng, butStr, butGPS, butDat, butDel, butDon;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        populateListView();
        butInt = (Button) findViewById(R.id.btnInt);
        butLng = (Button) findViewById(R.id.btnLon);
        butStr = (Button) findViewById(R.id.btnStr);
        butGPS = (Button) findViewById(R.id.btnGPS);
        butDat = (Button) findViewById(R.id.btnDat);
        butDel = (Button) findViewById(R.id.delBut);
        butDon = (Button) findViewById(R.id.btnDone);
        Button[] buttonArray = {butInt, butLng, butStr, butGPS, butDat, butDel, butDon};
        mAdView3 = (AdView) findViewById(R.id.adView3);
        AdRequest adRequest3 = new AdRequest.Builder().build();
        mAdView3.loadAd(adRequest3);
        try{
            for(Button button:buttonArray){
                button.setOnClickListener(this);
            }
        }
        catch (Exception e){}
    }

    private void populateListView() {
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
                askForName(dataTypeEnum.INTEGER);
                requiresInput=true;
                break;
            case R.id.btnLon:
                askForName(dataTypeEnum.LONG);
                requiresInput=true;
                break;
            case R.id.btnStr:
                askForName(dataTypeEnum.TEXT);
                requiresInput=true;
                break;
            case R.id.btnGPS:
                askForName(dataTypeEnum.COORDINATES);
                break;
            case R.id.btnDat:
                askForName(dataTypeEnum.TIMESTAMP);
                break;
            /*case R.id.btnEnum:
                askForName(dataTypeEnum.TAGS);
                requiresInput=true;
                break;*/
            case R.id.delBut:
                dataCols.clear();
                padded = false;
                requiresInput=false;
                break;
            case R.id.btnDone:
                Intent intentInput = new Intent(SettingActivity.this, Datafeed.class);
                if(dataCols.isEmpty()){
                    dataCols.add(0, new DataColumn(dataTypeEnum.INTEGER, "Column A"));
                    dataCols.add(1, new DataColumn(dataTypeEnum.INTEGER, "Column B"));
                    dataCols.add(2, new DataColumn(dataTypeEnum.INTEGER, "Column C"));
                    dataCols.add(3, new DataColumn(dataTypeEnum.INTEGER, "Column D"));
                    requiresInput=true;
                }
                if(requiresInput){
                    if (!padded) {
                        dataCols.add(new DataColumn(dataTypeEnum.TEXT, "Padding end column"));
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
    protected void askForName(final dataTypeEnum type){
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
                if(col.getType().equals(dataTypeEnum.INTEGER) | col.getType().equals(dataTypeEnum.TEXT)){
                    askNoDigits(col);
                } else if (col.getType().equals(dataTypeEnum.LONG)){
                    askLongDigits(col);
                } else if(col.getType().equals(dataTypeEnum.TAGS)){
                    askTags(col);
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

    void askTags(final DataColumn dc){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.how_many_tags));
        final EditText digits = new EditText(this);
        InputFilter[] filter = new InputFilter[1];
        filter[0] = new InputFilter.LengthFilter(1);
        digits.setFilters(filter);
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        digits.setInputType(InputType.TYPE_CLASS_NUMBER);
        digits.requestFocus();
        builder.setView(digits);
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                inputText = digits.getText().toString();
                int tagNumber = Integer.parseInt(inputText);
                dc.setDigits(1);
                imm.hideSoftInputFromWindow(digits.getWindowToken(), 0);
                setTags(dc, tagNumber);
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dc.setDigits(1);
                imm.hideSoftInputFromWindow(digits.getWindowToken(), 0);
                setTags(dc, 2);
            }
        });
        builder.show();
    }

    void setTags(final DataColumn dc, int tagNumber){
        final StringBuilder stringBuilder = new StringBuilder();
        ArrayList <String> tags = new ArrayList<>();
        for(int i=0; i<tagNumber; i++){
            AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
            builder2.setTitle(getString(R.string.Set_tag_name) + (i+1));
            final EditText digits = new EditText(this);
            InputFilter[] filter = new InputFilter[1];
            filter[0] = new InputFilter.LengthFilter(3);
            digits.setFilters(filter);
            final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            digits.setInputType(InputType.TYPE_CLASS_TEXT);
            digits.requestFocus();
            builder2.setView(digits);
            builder2.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (digits.length()<1){
                        digits.setText(R.string.unnamed_tag);
                    }
                    stringBuilder.append( digits.getText().toString());
                    imm.hideSoftInputFromWindow(digits.getWindowToken(), 0);
                }
            });
            builder2.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder2.show();
            tags.add(stringBuilder.toString());
        }
        String[] tagArray = new String[dc.getDigits()];
        for(int i = 0; i<tagArray.length; i++){
            tagArray[i] = tags.get(i);
        }
        ArrayList <String> tagSet = new ArrayList<>();
        tagSet.addAll(Arrays.asList(tagArray));
        dc.setTagSet(tagSet);
    }

    protected void askNoDigits(final DataColumn dc){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.how_many_digits));
        final EditText digits = new EditText(this);
        InputFilter[] filter = new InputFilter[1];
        filter[0] = new InputFilter.LengthFilter(1);
        digits.setFilters(filter);
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        digits.setInputType(InputType.TYPE_CLASS_NUMBER);
        digits.requestFocus();
        builder.setView(digits);
        setButtons(builder, dc, digits, imm);
        builder.show();
    }

    protected void askLongDigits (final DataColumn dc){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LinearLayout lila1= new LinearLayout(this);
        lila1.setOrientation(LinearLayout.VERTICAL);
        builder.setTitle(getString(R.string.how_many_decimal_digits));
        final EditText digits = new EditText(this);
        digits.setHint(R.string.no_digits);
        final EditText deci = new EditText(this);
        deci.setHint(R.string.no_decimals);
        InputFilter[] filter = new InputFilter[1];
        filter[0] = new InputFilter.LengthFilter(1);
        digits.setFilters(filter);
        deci.setFilters(filter);
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);        //use screen keyboard
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        digits.setInputType(InputType.TYPE_CLASS_NUMBER);
        deci.setInputType(InputType.TYPE_CLASS_NUMBER);
        digits.requestFocus();
        lila1.addView(digits);
        lila1.addView(deci);
        builder.setView(lila1);
        setLongButtons(builder, dc, deci, digits, imm );
        builder.show();
    }

    protected void setButtons(AlertDialog.Builder builder, final DataColumn dc, final EditText digits, final InputMethodManager imm){
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
    }

    protected void setLongButtons(AlertDialog.Builder builder, final DataColumn dc, final EditText deci, final EditText digits, final InputMethodManager imm ){
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
    }
}