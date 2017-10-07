package com.example.dailynote;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class newNote extends Activity {
    private Button doneButton;
    private AssetManager assetManager;
    private Typeface sans_serif;
    private ImageButton mic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);
        assetManager = getAssets();
        sans_serif = Typeface.SANS_SERIF;

        /*microphone button: start talking*/
        mic = (ImageButton) findViewById(R.id.microphone);
        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // press this, start recording
            }
        });

        /*'done' button*/
        doneButton = (Button) findViewById(R.id.done_button);
        doneButton.setTypeface(sans_serif);
        doneButton.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // done talking
            }
        });

    }
}
