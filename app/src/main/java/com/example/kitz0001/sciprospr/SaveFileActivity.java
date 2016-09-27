package com.example.kitz0001.sciprospr;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class SaveFileActivity extends AppCompatActivity implements View.OnClickListener{

    EditText fileName;
    EditText emailAddress;
    Switch emailSwitch, driveSwitch, devStorageSwitch;
    ArrayList<DataColumn> dataColumns;
    String fileNameString;
    String emailString;
    Uri path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_file);
        path=null;
        fileName = (EditText) findViewById(R.id.fileName);
        emailSwitch = (Switch) findViewById(R.id.emailSwitch);
        driveSwitch = (Switch) findViewById(R.id.driveSwitch);
        devStorageSwitch = (Switch) findViewById(R.id.devStorageSwitch);
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
        switch (click.getId()){
            case R.id.saveButton:
                saveFile();
                break;
        }
    }

    private void saveFile(){
        fileNameString = fileName.getText().toString().concat(".txt");

        if(devStorageSwitch.isChecked()){
            saveOnDevice(fileNameString);
        }
        if(emailSwitch.isChecked()){
            sendEmail(fileNameString);
        }
        if(driveSwitch.isChecked()){
            uploadToDrive(/*fileNameString*/);
        }

    }

    private void sendEmail(String name){

        try {
            emailString = emailAddress.getText().toString();
            if (emailString.length()==0){
                throw new Exception();
            }
            File filelocation = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), name);
            path = Uri.fromFile(filelocation);
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("vnd.android.cursor.dir/email");
            String to[]={emailString};
            emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
            emailIntent.putExtra(Intent.EXTRA_STREAM, path.getPath());
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        } catch (Exception e) {
            Toast.makeText(this, "Error while creating the email", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadToDrive(/*String name*/){}

    public void saveOnDevice(String name){
            try {
                FileOutputStream fileout = openFileOutput(name, Context.MODE_PRIVATE);
                OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
                StringBuilder sb = new StringBuilder();
                for (int h = 0; h < dataColumns.size(); h++) {
                    for (int m = 0; m < dataColumns.get(h).getValueSize(); m++) {
                        String testText;
                        testText = dataColumns.get(h).getValue(m);
                        sb.append(" C" + Integer.toString(h) + "R"+ Integer.toString(m) + ":" + testText);
                    }
                }
                outputWriter.write(sb.toString());
                outputWriter.close();
                fileout.close();
                Toast.makeText(this, "File saved as " + fileNameString, Toast.LENGTH_SHORT).show();

            } catch (Throwable t) {
                Toast.makeText(this, "Error while saving the file", Toast.LENGTH_SHORT).show();
            }
        }
}
