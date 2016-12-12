package com.example.kitz0001.sciprospr;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.*;
import java.io.*;
import java.util.ArrayList;
import android.Manifest;

public class SaveFileActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener{
    EditText fileName, emailAddress, subjectEditText;
    Switch emailSwitch;
    ArrayList<DataColumn> dataColumns;
    CsvWriter csvWriter;
    TxtWriter txtWriter;
    Spinner fileTypeSpinner;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_file);
        fileName = (EditText) findViewById(R.id.fileName);
        emailSwitch = (Switch) findViewById(R.id.emailSwitch);
        fileTypeSpinner = (Spinner) findViewById(R.id.spinner);
        Button saveButton = (Button) findViewById(R.id.saveButton);
        Intent sendIntent = getIntent();
        dataColumns = sendIntent.getParcelableArrayListExtra("DataColumns2");
        emailAddress = (EditText) findViewById(R.id.emailAddress);
        subjectEditText = (EditText) findViewById(R.id.subjectEditText);
        csvWriter = new CsvWriter();
        txtWriter = new TxtWriter();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.file_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fileTypeSpinner.setAdapter(adapter);
        fileTypeSpinner.setOnItemSelectedListener(this);
        try{assert saveButton != null;
            saveButton.setOnClickListener(this);}
        catch (Exception e){}
    }

    @Override
    public void onClick(View click) {
        if(click.getId()==R.id.saveButton){
                saveFile();
        }
    }

    private void saveFile(){
        String fileNameString = fileName.getText().toString().concat(fileTypeSpinner.getSelectedItem().toString());
        File fileDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        saveOnDevice(fileNameString, fileDirectory, fileNameString);
        if(emailSwitch.isChecked()){
            String emailString = emailAddress.getText().toString();
            String subjectString = subjectEditText.getText().toString();
            sendEmail(fileNameString, fileDirectory, emailString, subjectString);}
    }

    private void sendEmail(String fileName, File directory, String emailStringIn, String subjectStringIn){
        try {
            if (emailStringIn.length()==0){throw new Exception();}
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String [] {emailStringIn});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subjectStringIn);
            emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(directory, fileName)));
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        } catch (Exception e) {
            Toast.makeText(this, "Error while creating the email. Check filename and email address.", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveOnDevice(String fileName, File directory, String fileNameStringIn){
            try {
                verifyStoragePermissions(this);
                FileOutputStream fileout = new FileOutputStream(new File(directory, fileName));
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileout);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder = csvWriter.buildStringFromData(stringBuilder, dataColumns);
                outputStreamWriter.write(stringBuilder.toString());
                outputStreamWriter.close();
                fileout.close();
                Toast.makeText(this, "File saved as " + fileNameStringIn, Toast.LENGTH_SHORT).show();}
            catch (Throwable t) {
                Toast.makeText(this, t.getMessage(), Toast.LENGTH_SHORT).show();
                //Toast.makeText(this, "Error while saving the file. Check filename and free space on drive.", Toast.LENGTH_SHORT).show();
            }
        }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        verifyStoragePermissions(this);
    }

    } //end class