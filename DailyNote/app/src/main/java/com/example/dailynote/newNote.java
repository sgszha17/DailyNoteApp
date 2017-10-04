package com.example.dailynote;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class newNote extends Activity {
    private Button doneButton;
    private AssetManager assetManager;
    private Typeface sans_serif;
    private ImageButton mic;
    private EditText voiceNote;
    private final int REQ_CODE_SPEECH_INPUT = 100;
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
            public void onClick(View v){
                promptSpeechInput();
            }
        });
        voiceNote = (EditText) findViewById(R.id.content);

        /*'done' button*/
        doneButton = (Button) findViewById(R.id.done_button);
        doneButton.setTypeface(sans_serif);
        doneButton.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                /**
                 * Going back to previous page
                 * */
            }
        });

    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    voiceNote.append(result.get(0));
                }
                break;
            }

        }
    }

    public void done(){

    }
}
