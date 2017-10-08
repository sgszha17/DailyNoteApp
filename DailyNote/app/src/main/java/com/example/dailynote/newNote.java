/**
 * @(newNote).java     1.61 08/10/2017
 *
 * Copyright 2017 University of Melbourne. All rights reserved.
 *
 * @author Dawei Wang
 * @email daweiw@student.unimelb.edu.au
 *
 * @author Siyu Zhang
 * @email siyuz6@student.unimelb.edu.au
 *
 * @author Tong Zou
 * @email tzou2@student.unimelb.edu.au
 *
 **/
package com.example.dailynote;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static java.text.DateFormat.getDateInstance;

public class newNote extends Activity {

    private ImageButton mic;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private ImageButton backButton;
    private ImageButton recordButton;
    private TextView saveButton;
    private EditText noteTitle;
    private TextView dateCreated;
    private EditText noteContent;
    private Note newNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        /*microphone button: start talking*/
        mic = (ImageButton) findViewById(R.id.edit_note_record);
        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                promptSpeechInput();
            }
        });
        backButton = (ImageButton) findViewById(R.id.edit_note_back);
        saveButton = (TextView) findViewById(R.id.save);
        recordButton = (ImageButton) findViewById(R.id.edit_note_record);
        noteTitle = (EditText) findViewById(R.id.edit_note_title);
        dateCreated = (TextView) findViewById(R.id.edit_note_date);
        noteContent = (EditText) findViewById(R.id.multilineText);

        Intent i = getIntent();

        noteTitle.setText("");
        noteContent.setText("");
        Date date = Calendar.getInstance().getTime();
        newNote = new Note("", "", date);
        dateCreated.setText(("Date Created: " + newNote.date));

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isModified()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(newNote.this);
                    builder.setTitle("Return Warning");
                    builder.setMessage("You have unsaved changes. Do you want to return? The note will not be saved.");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            setResult(RESULT_CANCELED);
                            finish();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else {
                    setResult(RESULT_CANCELED);
                    finish();
                }
            }
        });

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptSpeechInput();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ri = new Intent();
                if (noteTitle.getText().toString().equals("")){
                    newNote.title = "Untitled";
                }else {
                    newNote.title = noteTitle.getText().toString();
                }
                newNote.content = noteContent.getText().toString();
                ri.putExtra("result_new_note", newNote);
                setResult(RESULT_OK, ri);
                finish();
            }
        });



    }

    public boolean isModified(){
        if (!noteTitle.getText().toString().equals(newNote.title)||!noteContent.getText().toString().equals(newNote.content)){
            return true;
        }
        return false;
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
                    noteContent.append(result.get(0));
                }
                break;
            }

        }
    }

    public void done(){

    }
}
