package com.example.dailynote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Calendar;


public class Notes extends Activity {
    private ImageButton settings;
    private ImageButton newNoteButton;
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    public static final int REQUEST_EDIT_NOTE = 1;
    public static final int REQUEST_NEW_NOTE = 2;

    public static List<Note> data = Collections.emptyList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        recyclerView = (RecyclerView) findViewById(R.id.notes_list);
        if(data.isEmpty()) {
            getData();
        }
        myAdapter = new MyAdapter(this, data);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        settings = (ImageButton) findViewById(R.id.settings);
        newNoteButton = (ImageButton) findViewById(R.id.new_note);
        newNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setClass(Notes.this, newNote.class);
                startActivityForResult(i, REQUEST_NEW_NOTE);
            }
        });





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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case REQUEST_EDIT_NOTE:
                if (resultCode == RESULT_OK){
                    int id = data.getIntExtra("result_id", -1);
                    Notes.data.set(id, (Note) data.getSerializableExtra("result_note"));
                    myAdapter.notifyItemChanged(id);
                }
                if (resultCode == RESULT_CANCELED){
                    break;
                }
                break;
            case REQUEST_NEW_NOTE:
                if (resultCode == RESULT_OK){
                    Notes.data.add(0, (Note) data.getSerializableExtra("result_new_note"));
                    myAdapter.notifyItemInserted(0);
                }
                if (resultCode == RESULT_CANCELED){
                    break;
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        writeData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        writeData();
    }

    //sort entries by date
    public void sortData(){
        Collections.sort(data);
    }

    //data IO
    public void writeData(){
        try {
            File dataFile = new File(this.getFilesDir(), "data.txt");
            FileOutputStream fos = new FileOutputStream(dataFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(data);
            oos.flush();
            oos.close();
        } catch (IOException e){
            Log.e("IOException", e.getMessage());
        } catch (Exception e){
            Log.e("MyException", e.getMessage());
        }
    }

    public void readData(){
        try {
            File dataFile = new File(this.getFilesDir(), "data.txt");
            FileInputStream fis = new FileInputStream(dataFile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            data = (List<Note>) ois.readObject();
            ois.close();
        } catch (FileNotFoundException e){
            Log.e("FileNotFound", e.getMessage());
        } catch (Exception e){
            Log.e("MyException", e.getMessage());
        }
    }


    public static void getData(){
        List<Note> data = new ArrayList<>();
        String[] dates = {"9.12", "12.30", "2.3","9.15"};
        String[] titles = {"first", "wdajuidabnwduiadbnqa2ue", "123ieobn12ioncqwoifnqwfon", "last"};
        String[] contents = {"I am content number 1.", "I am content number 2.", "I am content number 3.", "lalala"};

        for (int i = 0; i<dates.length; i++){
            Note current = new Note();
            current.date = dates[i];
            current.title = titles[i];
            current.content = contents[i];
            data.add(current);
        }
        Notes.data = data;
    }
}
