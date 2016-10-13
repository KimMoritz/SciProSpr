package com.example.kitz0001.sciprospr;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.*;
import java.io.*;
import java.util.ArrayList;

public class SaveFileActivity extends AppCompatActivity implements View.OnClickListener{

    EditText fileName, emailAddress;
    Switch emailSwitch;
    ArrayList<DataColumn> dataColumns;
    String fileNameString, emailString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_file);
        fileName = (EditText) findViewById(R.id.fileName);
        emailSwitch = (Switch) findViewById(R.id.emailSwitch);
        Button saveButton = (Button) findViewById(R.id.saveButton);
        Intent sendIntent = getIntent();
        dataColumns = sendIntent.getParcelableArrayListExtra("DataColumns2");
        emailAddress = (EditText) findViewById(R.id.emailAddress);
        try{
            assert saveButton != null;
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
        fileNameString = fileName.getText().toString().concat(".txt");
        File fileDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        saveOnDevice(fileNameString, fileDirectory);
        if(emailSwitch.isChecked()){
            emailString = emailAddress.getText().toString();
            sendEmail(fileNameString, fileDirectory, emailString);}
    }

    private void sendEmail(String fileName, File directory, String emailStringIn){
        try {
            if (emailStringIn.length()==0){throw new Exception();}
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String [] {emailStringIn});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
            emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(directory, fileName)));
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        } catch (Exception e) {
            Toast.makeText(this, "Error while creating the email. Check filename and email address.", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveOnDevice(String fileName, File directory){
            try {
                FileOutputStream fileout = new FileOutputStream(new File(directory, fileName));
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileout);
                StringBuilder stringBuilder = new StringBuilder();
                buildStringFromData(stringBuilder, dataColumns);
                outputStreamWriter.write(stringBuilder.toString());
                outputStreamWriter.close();
                fileout.close();
                Toast.makeText(this, "File saved as " + fileNameString, Toast.LENGTH_SHORT).show();}
            catch (Throwable t) {
                Toast.makeText(this, "Error while saving the file. Check filename and free space on drive.", Toast.LENGTH_SHORT).show();
            }
        }

    public void buildStringFromData(StringBuilder stringBuilderIn, ArrayList<DataColumn> dataColumnsIn){
        for (int h = 0; h < dataColumnsIn.size(); h++) {
            for (int m = 0; m < dataColumnsIn.get(h).getValueSize(); m++) {
                String testText;
                testText = dataColumnsIn.get(h).getValue(m);
                stringBuilderIn.append(" C" + Integer.toString(h) + "R"+ Integer.toString(m) + ":" + testText);
            } //end inner for
        }//end outer for
    }

} //end class