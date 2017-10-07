package com.example.dailynote;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.File;

import static android.content.ContentValues.TAG;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

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
import java.util.Date;
import java.util.List;
import java.util.Calendar;


public class Notes extends Activity {
    private ImageButton settings;
    private ImageButton createNewNoteButton;
    private RelativeLayout background;
    private SensorManager sensorManager;
    private Sensor sensorAccelerometer;
    private RoundImageView headShot;
    private TextView userName;
    private ListView notelist;
    private AlertDialog.Builder builder;
    private AlertDialog newNoteDialog;

    // time interval between 2 detecting
    private static final int UPTATE_INTERVAL_TIME = 1000;
    // speed threshold for accelerometer sensor
    private static final int SPEED_THRESHOLD = 100;
    /*about taking photos*/
    private static final String IMAGE_FILE_NAME = "newphoto"+(int)System.currentTimeMillis()+".jpg";
    private static final int CODE_CAMERA_REQUEST = 0xa5;

    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    public static final int REQUEST_EDIT_NOTE = 1;
    public static final int REQUEST_NEW_NOTE = 2;

    public static List<Note> data = Collections.emptyList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
//        Intent intent = getIntent();
//        final String[] userData = intent.getStringArrayExtra("getin");
//        Log.d(TAG, "onCreate: Get the data is "+ userData[0]);
        Intent intent = getIntent();
        if (intent.getBooleanExtra("delete_all", false)){
            deleteAll();
        }
        else{
            if (data == null||data.isEmpty()){
                deleteAll();
            }
            sortData();
        }


        background = (RelativeLayout) findViewById(R.id.personalBackground);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        /*setting headShot, getting headShot from settings*/
        headShot = (RoundImageView) findViewById(R.id.headShot);
        /*setting user's headshot if exists*/
        Drawable image = loadDrawable();
        if(image!=null){
            BitmapDrawable bp = (BitmapDrawable) image;
            headShot.setImageBitmap(bp.getBitmap());
        }

        /*setting userName*/
        userName = (TextView) findViewById(R.id.userName);
        SharedPreferences preferences = getSharedPreferences("myPref", MODE_PRIVATE);
        String username = preferences.getString("username","");
        userName.setText(username);

        recyclerView = (RecyclerView) findViewById(R.id.notes_list);

        myAdapter = new MyAdapter(this, data);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        settings = (ImageButton) findViewById(R.id.settings);


        /*Jump to setting*/
        createNewNoteButton = (ImageButton) findViewById(R.id.notes_create_new);
        createNewNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setClass(Notes.this, newNote.class);
                startActivityForResult(i, REQUEST_NEW_NOTE);
            }
        });
        settings = (ImageButton) findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(Notes.this, Settings.class);
//                intent.putExtra("goToSetting",userData);
                startActivity(intent);
                Notes.this.finish();

            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
        }
    }

    private SensorEventListener sensorEventListener = new SensorEventListener(){
        private float lastX;
        private float lastY;
        private float lastZ;

        private long lastUpdateTime;
        @Override
        public void onSensorChanged(SensorEvent sensorEvent){
            /*if time interval isn't enough then return*/
            long currentUpdateTime = System.currentTimeMillis();
            long timeInterval = currentUpdateTime - lastUpdateTime;
            if (timeInterval < UPTATE_INTERVAL_TIME) {
                return;
            }
            lastUpdateTime = currentUpdateTime;
            float[] values = sensorEvent.values;
            // get x,y,z accelerated speed
            float x = values[0];
            float y = values[1];
            float z = values[2];
            // get changed value of x, y, z
            float deltaX = x - lastX;
            float deltaY = y - lastY;
            float deltaZ = z - lastZ;
            // update 'last' coordinate
            lastX = x;
            lastY = y;
            lastZ = z;
            double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) / timeInterval * 10000;
            if (speed > SPEED_THRESHOLD) {
                Message msg = new Message();
                msg.what = SPEED_THRESHOLD;
                handler.sendMessage(msg);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor s, int accuracy){}
    };

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case SPEED_THRESHOLD:
                    createNewNoteDialog();
            }
        }
    };

    private void createNewNoteDialog(){
        builder = new AlertDialog.Builder(this);
        RelativeLayout newNoteDialog_layuot = (RelativeLayout) getLayoutInflater().inflate(R.layout.create_newnote_pop, null);
        builder.setView(newNoteDialog_layuot);
        builder.setCancelable(true);
        newNoteDialog = builder.create();
        newNoteDialog.show();
        ImageButton new_photo = (ImageButton) newNoteDialog.findViewById(R.id.new_photo);
        ImageButton new_text = (ImageButton) newNoteDialog.findViewById(R.id.new_text);
        ImageButton new_voice = (ImageButton) newNoteDialog.findViewById(R.id.new_voice);
        new_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeImageFromCamera();
            }
        });
        new_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setClass(Notes.this, newNote.class);
                startActivityForResult(i, REQUEST_NEW_NOTE);
            }
        });
        new_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setClass(Notes.this, newNote.class);
                startActivityForResult(i, REQUEST_NEW_NOTE);
            }
        });

    }
    @Override
    protected void onResume(){
        super.onResume();
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(sensorEventListener, sensorAccelerometer, sensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    protected void onPause(){
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
        writeData();
        System.out.println("now here");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(newNoteDialog != null){
            newNoteDialog.dismiss();
        }
        writeData();

    }

    /*initiate camera & take photo as headImage*/
    private void takeImageFromCamera(){
        Intent intentFromCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // judge whether sdCard is available to store image file
        if(Settings.hasSdcard()){
            intentFromCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.
                    fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
        }
        startActivityForResult(intentFromCamera, CODE_CAMERA_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        // no effective operation from user, return
        if(resultCode == RESULT_CANCELED){
            Toast.makeText(getApplication(), "Cancel", Toast.LENGTH_LONG).show();
            return;
        }
        switch (requestCode){
            case CODE_CAMERA_REQUEST:
                if(Settings.hasSdcard()){
                    File tempFile = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
                } else{
                    Toast.makeText(getApplication(), "No SdCard", Toast.LENGTH_LONG).show();
                }
                break;
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
        super.onActivityResult(requestCode, resultCode, data);
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


    @Override
    protected void onStop() {
        super.onStop();
        writeData();
    }


    //sort entries by date
    public static void sortData(){
        Collections.sort(data, Collections.reverseOrder());
    }

    public void deleteAll(){
        List<Note> tempData = new ArrayList<>();
        Date tempDate = new Date();
        Note tempNote = new Note ("You have no note entries now. Go create one!", "", tempDate);
        tempData.add(tempNote);
        data = tempData;
    }

    //data IO
    public void writeData(){
        try {
            File dataFile = new File(this.getFilesDir(), "data.txt");
            FileOutputStream fos = new FileOutputStream(dataFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(data);
            oos.close();
        } catch (IOException e){
            Log.e("IOException", e.getMessage());
        } catch (Exception e){
            Log.e("MyException", e.getMessage());
        }
    }


    public void createDataSet(){
        try {
            List<Note> data = new ArrayList<>();
            for (int i = 0;i<3;i++){
                Date d = new Date();
                Note tempNote = new Note(Integer.toString(i), Integer.toString(i), d);
                data.add(tempNote);
            }
            Date d= new Date(2017, 9, 25);
            Note tempNote = new Note("4", "4", d);
            data.add(tempNote);
            File file = new File(this.getFilesDir(), "data.txt");
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(data);
            oos.close();
        } catch (Exception e){

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
