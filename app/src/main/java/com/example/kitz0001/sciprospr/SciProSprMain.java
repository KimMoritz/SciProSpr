package com.example.kitz0001.sciprospr;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.Button;

public class SciProSprMain extends Activity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sci_pro_spr_main);

        Button newTable = (Button) findViewById(R.id.NewTable);
        Button loadTable = (Button) findViewById(R.id.LoadTable);
        Button visitWebpage = (Button) findViewById(R.id.visitWebPageButton);

        try{
            newTable.setOnClickListener(this);
            loadTable.setOnClickListener(this);
            visitWebpage.setOnClickListener(this);
        }
        catch (Exception e){}
    }

    @Override
    public void onClick(View arg0) {
        switch(arg0.getId()){
            case R.id.NewTable:
                startActivity(new Intent(SciProSprMain.this, SettingActivity.class));
                break;
            case R.id.LoadTable:
                startActivity(new Intent(SciProSprMain.this, SettingActivity.class));
                break;
            case R.id.visitWebPageButton:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.moritz.com.se"));
                startActivity(browserIntent);
                }
    }
}