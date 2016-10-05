package com.example.kitz0001.sciprospr;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class SaveFileActivity extends AppCompatActivity implements View.OnClickListener{

    EditText fileName;
    EditText emailAddress;
    Switch emailSwitch, devStorageSwitch;
    ArrayList<DataColumn> dataColumns;
    String fileNameString;
    String emailString;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_file);
        fileName = (EditText) findViewById(R.id.fileName);
        emailSwitch = (Switch) findViewById(R.id.emailSwitch);
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
            prepareEmail();
        }
    }

    private void prepareEmail(){
        openFile();
    }

    private void sendEmail(){
        try {
            emailString = "kim.moritz@hotmail.com"; //emailAddress.getText().toString(); TODO: Change to accept user input text from text field instead when function is working
            if (emailString.length()==0){
                throw new Exception();
            }
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("text/plain");
            String to[]={emailString};
            emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        } catch (Exception e) {
            Toast.makeText(this, "Error while creating the email", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveOnDevice(String name){
            try {
                File fileDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
                File fileForOutPut = new File(fileDirectory, name);
                FileOutputStream fileout = new FileOutputStream(fileForOutPut);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileout);
                StringBuilder stringBuilder = new StringBuilder();
                for (int h = 0; h < dataColumns.size(); h++) {
                    for (int m = 0; m < dataColumns.get(h).getValueSize(); m++) {
                        String testText;
                        testText = dataColumns.get(h).getValue(m);
                        stringBuilder.append(" C" + Integer.toString(h) + "R"+ Integer.toString(m) + ":" + testText);
                    }
                }
                outputStreamWriter.write(stringBuilder.toString());
                outputStreamWriter.close();
                fileout.close();
                Toast.makeText(this, "File saved as " + fileNameString, Toast.LENGTH_SHORT).show();
            } catch (Throwable t) {
                Toast.makeText(this, "Error while saving the file", Toast.LENGTH_SHORT).show();
            }
        }

    public void openFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra("return-data", true);
        startActivityForResult(Intent.createChooser(intent, "Choose file using..."), 101);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 101 && resultCode == RESULT_OK) {
            uri = data.getData();
            sendEmail();
        }
    }
}