package com.example.kitz0001.sciprospr;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Toast;

public class Datafeed extends Activity implements View.OnClickListener, LocationListener {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 0;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private int j = 0, rowInt=1;
    Button one, two, three, four, five, six, seven, eight, nine, zero, cancel, save, load, send;
    EditText disp, prev, rowOrSampleNo;
    ArrayList<Integer> textLength;
    ArrayList<DataColumn> dataColumns2;
    private LatLng latLng;
    String mLatitudeText, mLongiitudeText;
    private LocationManager locationManager;
    private String provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent dataIntent = getIntent();
        dataColumns2 = dataIntent.getParcelableArrayListExtra("DataColumns");
        setContentView(R.layout.datafeed_sci_pro_spr);
        mLatitudeText = "startLat";
        mLongiitudeText = "startLong";
        super.onCreate(savedInstanceState);
        textLength = new ArrayList<>();
        for (int o = 0; o < dataColumns2.size() - 1; o++) {
            DataColumn dc = dataColumns2.get(o);
            Integer digs = dc.getDigits();
            textLength.add(digs);
        }

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
        save = (Button) findViewById(R.id.save);
        load = (Button) findViewById(R.id.load);
        send = (Button) findViewById(R.id.sendButton);
        disp = (EditText) findViewById(R.id.display);
        prev = (EditText) findViewById(R.id.previnp); //TODO:Change name to something informative
        rowOrSampleNo = (EditText) findViewById(R.id.rowOrSampleNumber);

        //TODO: use inner classes instead? Compare Core Java I: chapter about event handling
        try {
            zero.setOnClickListener(this);
            one.setOnClickListener(this);
            two.setOnClickListener(this);
            three.setOnClickListener(this);
            four.setOnClickListener(this);
            five.setOnClickListener(this);
            six.setOnClickListener(this);
            seven.setOnClickListener(this);
            eight.setOnClickListener(this);
            nine.setOnClickListener(this);
            save.setOnClickListener(this);
            load.setOnClickListener(this);
            cancel.setOnClickListener(this);
            send.setOnClickListener(this);
        } catch (Exception e) {
            System.out.print(Thread.currentThread().getStackTrace().toString());
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
    public void onClick(View arg0) {
        respondToButton(arg0);
        chooseInput();
        updateCurrentColumnDisplay(j);
    }

    private void respondToButton(View arg0){
        Editable str = disp.getText();
        switch (arg0.getId()) {
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
                disp.setText("");
                disp.setHint("Please input value");
                break;
            case R.id.save:
                this.saveToFile();
                break;
            case R.id.load:
                this.loadFromFile();
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
            case COORDINATES:
                inputGPS();
                break;
            case INTEGER:
                inputInt();
                break;
            case LONG:
                inputLng();
                break;
            case TEXT:
                inputText();
                break;
            case TIMESTAMP:
                inputTime();
                break;
            case PICTURE:
                getPhoto();
                break;
            case TAGS:
                inputTag();
                break;
        }
    }

    private void inputTag() {
            final String [] tagArray = dataColumns2.get(j).getTagSet(); //TODO: Doesn't obtain any strings -> tag set empty here.
            final AlertDialog.Builder builder = new AlertDialog.Builder(Datafeed.this);
            builder.setTitle(R.string.Set_tag_name)
                    .setItems(tagArray, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            DataColumn dc = dataColumns2.get(j);
                            String inputVar2 = tagArray[which];
                            dc.addValue(inputVar2);
                            dataColumns2.set(j, dc);
                        }
                    });
            builder.show();
            j++;
            disp.setText("");
            if (j == dataColumns2.size()-1) {
                j = 0;
                rowInt++;
            }
    }

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
                String inputVar2;
                inputVar2 = input.getText().toString();
                DataColumn dc = dataColumns2.get(j);
                dc.addValue(inputVar2);
                dataColumns2.set(j, dc);
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String inputVar2;
                inputVar2 = "No comment";
                DataColumn dc = dataColumns2.get(j);
                dc.addValue(inputVar2);
                dataColumns2.set(j, dc);
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
            }
        });
        builder.show();
        j++;
        disp.setText("");
        //New row, start over from first column)
        if (j == dataColumns2.size()-1) {
            j = 0;
            rowInt++;
        }
    }

    public void inputInt(){
        if (disp.getText().length() >= textLength.get(j)) {
            String inputvar2 = disp.getText().toString();
            DataColumn dc = dataColumns2.get(j);
            dc.addValue(inputvar2);
            dataColumns2.set(j, dc);
            j++;
            disp.setText("");
            //New row, start over from first column
            if (j == dataColumns2.size()-1) {
                j = 0;
                rowInt++;
            } //end inner if
        }//end outer if
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
                //New row, start over from first column)
                if (j == dataColumns2.size()-1) {
                    j = 0;
                    rowInt++;
                } //end inner if
            } //end middle if/else if
        } //en outer if
    }

    public void inputGPS(){
        DataColumn dc = dataColumns2.get(j);
        String coordinates = this.getCoordinates();
        dc.addValue(coordinates);
        dataColumns2.set(j, dc);
        j++;
        //New row, start over from first column)
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
        //New row, start over from first column)
        if (j == dataColumns2.size()-1) {
            j = 0;
            rowInt++;
        }
    }

    public void getPhoto() {}

    public String getCoordinates() {
        return latLng.toString();
    }

    private void updateCurrentColumnDisplay(int index){     //Inspect whether it is a variable that requires input. If so write it to the current type-field.
        if(index >= dataColumns2.size()){
            index=0;
        }
        if (!dataColumns2.get(index).getType().equals(dataTypeEnum.COORDINATES) && !dataColumns2.get(index).getType().equals(dataTypeEnum.TIMESTAMP) && !dataColumns2.get(index).getName().equals("Padding end column")) {
            String updated = dataColumns2.get(index).getType() + " " + dataColumns2.get(index).getName();
            prev.setText(updated);
            String rowString = ""+(rowInt);
            rowOrSampleNo.setText(rowString);
        }else if(index<dataColumns2.size()){                  // Otherwise, recursively inspect the next element until the criterion is fulfilled.
            updateCurrentColumnDisplay(index+1);
        }else if (index >=dataColumns2.size()){
            index=0;
            updateCurrentColumnDisplay(index);
        }
    }

    public void saveToFile() {
        try {
            FileOutputStream fileout = openFileOutput("SciProSprFile.txt", Context.MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
            StringBuilder stringBuilder = new StringBuilder();
            for (int h = 0; h < dataColumns2.size(); h++) {
                for (int m = 0; m < dataColumns2.get(h).getValueSize(); m++) {
                    String testText;
                    testText = dataColumns2.get(h).getValue(m);
                    stringBuilder.append(" C" + Integer.toString(h) + "R"+ Integer.toString(m) + ":" + testText);
                }
            }
            outputWriter.write(stringBuilder.toString());
            outputWriter.close();
            fileout.close();
            disp.setText("");
            disp.setHint("Please input data");
        } catch (Throwable t) {
            String err = "Error";
            disp.setText(err);
        }
    }

    public void loadFromFile() {
        try {
            InputStream instream = openFileInput("SciProSprFile.txt");
            InputStreamReader inputreader = new InputStreamReader(instream);
            BufferedReader buffreader = new BufferedReader(inputreader);
            String line;
            StringBuilder out = new StringBuilder();
            while ((line = buffreader.readLine()) != null) {
                out.append(line);
            }
            disp.setText("");
            disp.setHint(out.toString());
            buffreader.close();
            inputreader.close();
            instream.close();
        } catch (Throwable t) {
            String err = "Error";
            disp.setText(err);
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

    //Asks for permissions to access location
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION:
                // Permission Granted
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    // Permission Denied
                    Toast.makeText(this, "ACCESS_COARSE_LOCATION Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                } else {
                    // Permission Denied
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
}//end of activity class