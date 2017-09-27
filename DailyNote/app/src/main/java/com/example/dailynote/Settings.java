package com.example.dailynote;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class Settings extends Activity {
    private AssetManager assetManager;
    private Typeface kannada;
    private Typeface sans_serif;

    private ImageButton returnButton;
    private TextView personInfo;
    private TextView photoText;
    private RoundImageView headshot;
    private TextView accountText;
    private TextView nameText;

    private TextView sysSetting;
    private CheckBox checkbox1;
    private CheckBox checkbox2;
    private CheckBox checkbox3;
    private TextView sysSetting1;
    private TextView sysSetting2;
    private TextView sysSetting3;

    private TextView general;
    private TextView logoutText;
    private TextView clearCacheText;
    private TextView deleteNotesText;
    private ImageButton logoutButton;
    private ImageButton clearCacheButton;
    private ImageButton deleteNotesButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        assetManager = getAssets();
        kannada = Typeface.createFromAsset(assetManager, "Kannada MN.ttc");
        sans_serif = Typeface.createFromAsset(assetManager, "Times_Sans_Serif.ttf");
        /*return button*/
        returnButton = (ImageButton) findViewById(R.id.return_button);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // return to the main page
                Intent intent = new Intent();
                intent.setClass(Settings.this, Notes.class);
                startActivity(intent);
                Settings.this.finish();
            }
        });

        /*setting text typeface for three setting titles*/
        personInfo = (TextView) findViewById(R.id.personInfo);
        personInfo.setTypeface(kannada);
        personInfo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

        sysSetting = (TextView) findViewById(R.id.sysSetting);
        sysSetting.setTypeface(kannada);
        sysSetting.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

        general = (TextView) findViewById(R.id.generalSetting);
        general.setTypeface(kannada);
        general.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

        /*Personal Setting*/
        photoText = (TextView) findViewById(R.id.photo_text);
        photoText.setTypeface(sans_serif);
        photoText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        headshot = (RoundImageView) findViewById(R.id.user_headShot);
        headshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // choose images from photo gallery or take new photos
                Intent intent = new Intent();
                intent.setClass(Settings.this, ChooseHeadshot.class);
                startActivity(intent);
                Settings.this.finish();
            }
        });

        accountText = (TextView) findViewById(R.id.account_text);
        accountText.setTypeface(sans_serif);
        accountText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        nameText = (TextView) findViewById(R.id.name_text);
        nameText.setTypeface(sans_serif);
        nameText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        /*System Setting*/
        sysSetting1 = (TextView) findViewById(R.id.sysSetting1);
        sysSetting1.setTypeface(sans_serif);
        sysSetting1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        sysSetting2 = (TextView) findViewById(R.id.sysSetting2);
        sysSetting2.setTypeface(sans_serif);
        sysSetting2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        sysSetting3 = (TextView) findViewById(R.id.sysSetting3);
        sysSetting3.setTypeface(sans_serif);
        sysSetting3.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        checkbox1 = (CheckBox) findViewById(R.id.checkbox_1);
        checkbox2 = (CheckBox) findViewById(R.id.checkbox_2);
        checkbox3 = (CheckBox) findViewById(R.id.checkbox_3);
        checkbox1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // automatically transform voice to text
            }
        });
        checkbox2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //display location on screen
            }
        });
        checkbox3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // display weather on screen
            }
        });

        /*GENERAL setting*/
        logoutText = (TextView) findViewById(R.id.log_out_text);
        logoutText.setTypeface(sans_serif);
        logoutText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

        clearCacheText = (TextView) findViewById(R.id.clear_cache_text);
        clearCacheText.setTypeface(sans_serif);
        clearCacheText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

        deleteNotesText = (TextView) findViewById(R.id.delete_notes_text);
        deleteNotesText.setTypeface(sans_serif);
        deleteNotesText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

        logoutButton = (ImageButton) findViewById(R.id.log_out_button);
        clearCacheButton = (ImageButton) findViewById(R.id.clear_cache_button);
        deleteNotesButton = (ImageButton) findViewById(R.id.delete_notes_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // logout of current account
            }
        });
        clearCacheButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // clear cache
            }
        });
        deleteNotesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // delete all current notes
            }
        });

    }
}
