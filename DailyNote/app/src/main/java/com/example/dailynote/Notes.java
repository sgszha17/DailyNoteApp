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


public class Notes extends Activity {
    private ImageButton settings;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
//        Intent intent = getIntent();
//        final String[] userData = intent.getStringArrayExtra("getin");
//        Log.d(TAG, "onCreate: Get the data is "+ userData[0]);


        background = (RelativeLayout) findViewById(R.id.personalBackground);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        notelist = (ListView) findViewById(R.id.noteList);

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

        /*Jump to setting*/
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
                Intent intent = new Intent();
                intent.setClass(Notes.this, newNoteText.class);
                startActivity(intent);
                Notes.this.finish();
            }
        });
        new_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(Notes.this, newNote.class);
                startActivity(intent);
                Notes.this.finish();
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
        System.out.println("now here");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(newNoteDialog != null){
            newNoteDialog.dismiss();
        }

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
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
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
        }
        super.onActivityResult(requestCode, resultCode, intent);
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
