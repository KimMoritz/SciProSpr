package com.moritz.SciProSpr.sciprospr;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import com.google.android.gms.maps.model.LatLng;
import com.google.gdata.data.DateTime;

import java.util.ArrayList;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Toast;

import android.os.Vibrator;

public class Datafeed extends Activity implements View.OnClickListener, LocationListener {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 0;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private int j = 0, rowInt=1;
    Button one, two, three, four, five, six, seven, eight, nine, zero, cancel, send ;
    Button[] buttons;
    EditText disp, prev, rowOrSampleNo;
    ArrayList<Integer> textLength;
    ArrayList<DataColumn> dataColumns2;
    private LatLng latLng;
    String mLatitudeText, mLongitudeText;
    private LocationManager locationManager;
    private String provider;
    private Vibrator Vib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent dataIntent = getIntent();
        dataColumns2 = dataIntent.getParcelableArrayListExtra("DataColumns");
        setContentView(R.layout.datafeed_sci_pro_spr);
        mLatitudeText = "startLat";
        mLongitudeText = "startLong";
        super.onCreate(savedInstanceState);
        textLength = new ArrayList<>();
        for (int o = 0; o < dataColumns2.size() - 1; o++) {
            DataColumn dc = dataColumns2.get(o);
            Integer digs = dc.getDigits();
            textLength.add(digs);
        }
        Vib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        latLng = new LatLng(0.0, 0.0);
        one = (Button) findViewById(R.id.one);
        two = (Button) findViewById(R.id.two);
        three = (Button) findViewById(R.id.three);
        four = (Button) findViewById(R.id.four);
        five = (Button) findViewById(R.id.five);
        six = (Button) findViewById(R.id.six);
        seven = (Button) findViewById(R.id.seven);
        eight = (Button) findViewById(R.id.eight);
        nine = (Button) findViewById(R.id.nine);
        zero = (Button) findViewById(R.id.zero);
        cancel = (Button) findViewById(R.id.cancel);
        send = (Button) findViewById(R.id.sendButton);
        disp = (EditText) findViewById(R.id.display);
        prev = (EditText) findViewById(R.id.previnp);
        rowOrSampleNo = (EditText) findViewById(R.id.rowOrSampleNumber);
        buttons = new Button[]{one, two, three, four, five, six, seven, eight, nine, zero, cancel, send};

        try {
            for(Button button:buttons){
                button.setOnClickListener(this);
            }
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        }
    }

    @Override
    public void onClick(View view) {
        //updateCurrentColumnDisplay(j);
        respondToButton(view);
        if(view.getId()!=R.id.sendButton){
            chooseInput();
            updateCurrentColumnDisplay(j);
        }
    }

    private void respondToButton(View view){
        Vib.vibrate(50);
        Editable str = disp.getText();
        switch (view.getId()) {
            case R.id.zero: disp.setText(str.append(zero.getText())); break;
            case R.id.one: disp.setText(str.append(one.getText())); break;
            case R.id.two: disp.setText(str.append(two.getText())); break;
            case R.id.three: disp.setText(str.append(three.getText())); break;
            case R.id.four: disp.setText(str.append(four.getText())); break;
            case R.id.five: disp.setText(str.append(five.getText())); break;
            case R.id.six: disp.setText(str.append(six.getText())); break;
            case R.id.seven: disp.setText(str.append(seven.getText())); break;
            case R.id.eight: disp.setText(str.append(eight.getText())); break;
            case R.id.nine: disp.setText(str.append(nine.getText())); break;
            case R.id.cancel:
                disp.setText(R.string.empty_string);
                disp.setHint(R.string.ask_for_input_value);
                break;
            case R.id.sendButton:
                Intent sendIntent = new Intent(Datafeed.this, SaveFileActivity.class);
                sendIntent.putParcelableArrayListExtra("DataColumns2", dataColumns2);
                startActivity(sendIntent);
                break;
        }
    }

    private void chooseInput(){
        DataColumn currentDC = dataColumns2.get(j);
        dataTypeEnum dataType = currentDC.getType();
        switch (dataType){
            case COORDINATES: inputGPS();break;
            case INTEGER: inputInt();break;
            case LONG: inputLng();break;
            case TEXT: //inputText();
                /*j++;
                if (j == dataColumns2.size() - 1) {
                    j = 0;
                    rowInt++;
                }*/
                break;
            case TIMESTAMP: inputTime();break;
        }
    }

    //TODO: Appen kan inte spara om text skrivs in. Varför? Ta bort padding?
    public void inputText(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.add_comment));
        final EditText input = new EditText(this);
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        InputFilter[] filter = new InputFilter[1];
        filter[0] = new InputFilter.LengthFilter(dataColumns2.get(j).getDigits());
        input.setFilters(filter);
        input.requestFocus();
        builder.setView(input);
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DataColumn dc = dataColumns2.get(j);
                dc.addValue(input.getText().toString());
                dataColumns2.set(j, dc);
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                j++;
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DataColumn dc = dataColumns2.get(j);
                dc.addValue("No comment");
                dataColumns2.set(j, dc);
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                j++;
            }
        });
        disp.setText("");
        builder.show();
    }

    public void inputInt() {
        if (disp.getText().length() >= textLength.get(j)) {
                String inputvar2 = disp.getText().toString();
                DataColumn dc = dataColumns2.get(j);
                dc.addValue(inputvar2);
                dataColumns2.set(j, dc);
                j++;
                disp.setText("");
                if (j == dataColumns2.size() - 1) {
                    j = 0;
                    rowInt++;
                }
            }
    }

    public void inputLng(){
        if (disp.getText().length() >= textLength.get(j)) {
            DataColumn dc2 = dataColumns2.get(j);
            int declength = dc2.getDecimals();
            if(disp.getText().length() == textLength.get(j)){
                Editable str = disp.getText();
                disp.setText(str.append("."));}
            else if(disp.getText().length()  >= textLength.get(j) + declength +1 ){
                String inputVar3 = disp.getText().toString();
                DataColumn dc = dataColumns2.get(j);
                dc.addValue(inputVar3);
                dataColumns2.set(j, dc);
                j++;
                disp.setText("");
                if (j == dataColumns2.size()-1) {
                    j = 0;
                    rowInt++;
                } //end inner if
            } //end middle if/else if
        } //end outer if
    }

    public void inputGPS(){
        DataColumn dc = dataColumns2.get(j);
        String coordinates = this.getCoordinates();
        coordinates = coordinates.replace(',', ';');
        dc.addValue(coordinates);
        dataColumns2.set(j, dc);
        j++;
        if (j == dataColumns2.size()-1) {
            j = 0;
            rowInt++;
        }
    }

    public void inputTime(){
        DataColumn dc = dataColumns2.get(j);
        DateTime dateTime = DateTime.now() ;
        String time = dateTime.toString();
        dc.addValue(time);
        dataColumns2.set(j, dc);
        j++;
        if (j == dataColumns2.size()-1) {
            j = 0;
            rowInt++;
        }
    }

    public String getCoordinates() {
        return latLng.toString();
    }

    private void updateCurrentColumnDisplay(int index){
        if(index >= dataColumns2.size()){
            index=0;
        }
        if (!dataColumns2.get(index).getType().equals(dataTypeEnum.COORDINATES) && !dataColumns2.get(index).getType().equals(dataTypeEnum.TIMESTAMP)&& !dataColumns2.get(index).getType().equals(dataTypeEnum.TEXT)) {
            String updated = dataColumns2.get(index).getType() + " " + dataColumns2.get(index).getName();
            prev.setText(updated);
            String rowString = ""+(rowInt);
            rowOrSampleNo.setText(rowString);
        }else if(dataColumns2.get(index).getType().equals(dataTypeEnum.TEXT)&& !dataColumns2.get(index).getName().equals("Padding end column")){
            j--;
            inputText();
            j++;
            if (j == dataColumns2.size() - 1) {
                j = 0;
                rowInt++;
            }
        }else if(index<dataColumns2.size()){
            updateCurrentColumnDisplay(index+1);
        }else if (index >=dataColumns2.size()){
            index=0;
            updateCurrentColumnDisplay(index);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            ActivityCompat.requestPermissions( this, new String[] {  Manifest.permission.ACCESS_FINE_LOCATION  },MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }else {
            locationManager.requestLocationUpdates(provider, 400, 1, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this, "ACCESS_COARSE_LOCATION Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this, "ACCESS_FINE_LOCATION Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        latLng= new LatLng(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(this);
    }
}