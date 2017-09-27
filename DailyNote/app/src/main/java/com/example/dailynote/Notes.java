package com.example.dailynote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;


public class Notes extends Activity {
    private ImageButton settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        settings = (ImageButton) findViewById(R.id.settings);
        /*Jump to setting*/
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(Notes.this, Settings.class);
                startActivity(intent);
                Notes.this.finish();
            }
        });
    }
}
