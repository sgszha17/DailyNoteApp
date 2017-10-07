package com.example.dailynote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;

public class newNoteText extends AppCompatActivity {
    private ImageView headShot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note_text);
        /*show head Image*/
        headShot = (ImageView) findViewById(R.id.headShot);
        /*
        Intent headshotIntent = getIntent();
        if(headshotIntent != null){
            Bitmap photo = headshotIntent.getParcelableExtra("bitmap");
            headShot.setImageBitmap(photo);
        }*/

        /*setting user's headshot if exists*/
        Drawable image = loadDrawable();
        if(image!=null){
            BitmapDrawable bp = (BitmapDrawable) image;
            headShot.setImageBitmap(bp.getBitmap());
        }
    }

    public Drawable loadDrawable(){
        Drawable image = null;
        SharedPreferences share = getSharedPreferences("ThumbLock", MODE_PRIVATE);
        String temp = share.getString("P", "");
        if(temp != null){
            ByteArrayInputStream is = new ByteArrayInputStream(android.util.Base64.decode
                    (temp.getBytes(), android.util.Base64.DEFAULT));
            image = Drawable.createFromStream(is, "");
        }
        return image;
    }
}
