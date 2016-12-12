package com.example.kitz0001.sciprospr;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class SciProSprMain extends Activity implements View.OnClickListener {

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sci_pro_spr_main);
        Button newTable = (Button) findViewById(R.id.NewTable);
        Button visitWebpage = (Button) findViewById(R.id.visitWebPageButton);
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        try{
            newTable.setOnClickListener(this);
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
            case R.id.visitWebPageButton:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.moritz.com.se"));
                startActivity(browserIntent);
                }
    }
}