package com.example.dailynote;

/**
 * Created by David on 2017/10/5.
 */

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

import java.util.ArrayList;
import java.util.Locale;


public class EditNote extends Activity {
    private ImageButton backButton;
    private ImageButton recordButton;
    private TextView saveButton;
    private EditText noteTitle;
    private TextView dateCreated;
    private EditText noteContent;
    private Note noteEdit;
    private int id;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        backButton = (ImageButton) findViewById(R.id.edit_note_back);
        saveButton = (TextView) findViewById(R.id.save);
        recordButton = (ImageButton) findViewById(R.id.edit_note_record);
        noteTitle = (EditText) findViewById(R.id.edit_note_title);
        dateCreated = (TextView) findViewById(R.id.edit_note_date);
        noteContent = (EditText) findViewById(R.id.multilineText);

        Intent i = getIntent();
        id = i.getIntExtra("id", -1);
        noteEdit = (Note) i.getSerializableExtra("note");

        noteTitle.setText(noteEdit.title);
        dateCreated.setText("Date Created: " + noteEdit.date);
        noteContent.setText(noteEdit.content);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditNote.this);
                builder.setTitle("Return Warning");
                builder.setMessage("Do you want to discard the changes and return?");
                builder.setPositiveButton("Discard and Return", new DialogInterface.OnClickListener() {
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
                noteEdit.title = noteTitle.getText().toString();
                noteEdit.content = noteContent.getText().toString();
                ri.putExtra("result_note", noteEdit);
                ri.putExtra("result_id", id);
                setResult(RESULT_OK, ri);
                finish();
            }
        });
    }

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
}
