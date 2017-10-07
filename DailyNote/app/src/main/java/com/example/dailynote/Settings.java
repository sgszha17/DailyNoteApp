package com.example.dailynote;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import static android.content.ContentValues.TAG;

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


    private TextView general;
    private TextView logoutText;
    private TextView clearCacheText;
    private TextView deleteNotesText;
    private ImageButton logoutButton;
    private ImageButton clearCacheButton;
    private TextView cacheText;
    private ImageButton deleteNotesButton;
    private String[] userInformation;

    private EditText myaccountfield;
    private EditText mUsernamefield;

    private boolean isDeletedAll;

    /*headImage file*/
    private static final String IMAGE_FILE_NAME = "temp_head_image.jpg";
    private Bitmap photo = null;
    /*request code*/
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;

    /*headShot size*/
    private static int head_output_x = 150;
    private static int head_output_y = 150;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // android 7.0 system resolve permission issue
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        isDeletedAll = false;

//        Intent intent = getIntent();
//        userInformation = intent.getStringArrayExtra("goToSetting");

        userLoginInformationFromLocal();
        SharedPreferences preferences = getSharedPreferences("myPref", MODE_PRIVATE);
        String username = preferences.getString("username","");
        String email = preferences.getString("email","");

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
                intent.putExtra("delete_all", isDeletedAll);
//                intent.putExtra("getin",userInformation);
                startActivity(intent);
                Settings.this.finish();
            }
        });


        /*setting text typeface for three setting titles*/
        personInfo = (TextView) findViewById(R.id.personInfo);
        personInfo.setTypeface(kannada);
        personInfo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);

        general = (TextView) findViewById(R.id.generalSetting);
        general.setTypeface(kannada);
        general.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);

        /*Personal Setting*/
        photoText = (TextView) findViewById(R.id.photo_text);
        photoText.setTypeface(sans_serif);
        photoText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);

        headshot = (RoundImageView) findViewById(R.id.user_headShot);
        /*setting user's headshot if exists*/
        Drawable image = loadDrawable();
        if(image!=null){
            BitmapDrawable bp = (BitmapDrawable) image;
            headshot.setImageBitmap(bp.getBitmap());
        }
        headshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // choose images from photo gallery or take new photos
                PopupMenu editHeadshot = new PopupMenu(getBaseContext(), headshot);
                editHeadshot.getMenuInflater().inflate(R.menu.edit_headshot, editHeadshot.getMenu());
                editHeadshot.show();
                editHeadshot.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        String command = menuItem.getTitle().toString();
                        if(command.equals("Take Photo")){
                            /*take new photo to be headshot*/
                            takeHeadImageFromCamera();

                        }else if(command.equals("Choose from Album")){
                            /*choose photo from album to be headshot*/
                            chooseHeadImageFromGallery();
                        }
                        return true;
                    }
                });
            }
        });

        accountText = (TextView) findViewById(R.id.account_text);
        accountText.setTypeface(sans_serif);
        accountText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);

        myaccountfield = (EditText) findViewById(R.id.myAcount);
        myaccountfield.setText(email);
        myaccountfield.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        myaccountfield.setFocusable(false);
        myaccountfield.setFocusableInTouchMode(false);

        nameText = (TextView) findViewById(R.id.name_text);
        nameText.setTypeface(sans_serif);
        nameText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);

        mUsernamefield = (EditText) findViewById(R.id.myusername);
        mUsernamefield.setText(username);
        mUsernamefield.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        mUsernamefield.setFocusable(false);
        mUsernamefield.setFocusableInTouchMode(false);

        /*GENERAL setting*/
        logoutText = (TextView) findViewById(R.id.log_out_text);
        logoutText.setTypeface(sans_serif);
        logoutText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);

        clearCacheText = (TextView) findViewById(R.id.clear_cache_text);
        clearCacheText.setTypeface(sans_serif);
        clearCacheText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);

        cacheText = (TextView) findViewById(R.id.cacheSize);
        cacheText.setTypeface(sans_serif);
        cacheText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        try {
            cacheText.setText(CacheDataManager.getTotalCacheSize(this));
        }catch (Exception e){
            e.printStackTrace();
        }

        deleteNotesText = (TextView) findViewById(R.id.delete_notes_text);
        deleteNotesText.setTypeface(sans_serif);
        deleteNotesText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);

        logoutButton = (ImageButton) findViewById(R.id.log_out_button);
        clearCacheButton = (ImageButton) findViewById(R.id.clear_cache_button);
        deleteNotesButton = (ImageButton) findViewById(R.id.delete_notes_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // logout of current account
                confirmLogout();
            }
        });
        clearCacheButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // clear cache
                new Thread(new clearCache()).start();
            }
        });
        deleteNotesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
                builder.setTitle("Delete All Note Entries");
                builder.setMessage("Are you sure you want to delete all?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        isDeletedAll = true;
                        Toast.makeText(Settings.this, "Delete Success",
                                Toast.LENGTH_LONG).show();
                        dialog.dismiss();
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

    }

    public void confirmLogout(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(Settings.this);
        dialog.setTitle("Alert");
        dialog.setMessage("Are you sure you want to logout? ");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(Settings.this,ToDoActivity.class);
                intent.putExtra("logout",true);
                startActivity(intent);
                Settings.this.finish();
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /*choose headShot from gallery*/
    private void chooseHeadImageFromGallery(){
        Intent intentFromGallery = new Intent();
        // set file type
        intentFromGallery.setType("image/*");
        intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intentFromGallery, CODE_GALLERY_REQUEST);
    }
    /*initiate camera & take photo as headImage*/
    private void takeHeadImageFromCamera(){
        Intent intentFromCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // judge whether sdCard is available to store image file
        if(hasSdcard()){
            intentFromCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.
                    fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
        }
        startActivityForResult(intentFromCamera, CODE_CAMERA_REQUEST);
    }

    /*check whether sdCard is available*/
    public static boolean hasSdcard(){
        String state = Environment.getExternalStorageState();
        if(state.equals(Environment.MEDIA_MOUNTED)){
            // has sdCard
            return true;
        }else{
            return false;
        }
    }

    /*crop original photo*/
    public void cropPhoto(Uri uri, int aspect_x, int aspect_y, int output_x, int output_y){
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // setting crop
        intent.putExtra("crop", "true");
        // image proportion
        intent.putExtra("aspect_x", aspect_x);
        intent.putExtra("aspect_y", aspect_y);
        // image width & height
        intent.putExtra("output_x", output_x);
        intent.putExtra("output_y", output_y);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, CODE_RESULT_REQUEST);
    }

    /*set Image as headShot*/
    private void setImageToHeadshot(Intent intent, ImageView headshot){
        Bundle extras = intent.getExtras();
        if(extras != null){
            Bitmap photo = extras.getParcelable("data");
            headshot.setImageBitmap(photo);
            this.photo = photo;
            // save photo to cellphone
            SharedPreferences mSharedPreferences=getSharedPreferences("ThumbLock", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor=mSharedPreferences.edit();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, os);
            String imagebase64 = new String(android.util.Base64.encodeToString(os.toByteArray(), android.util.Base64.DEFAULT));
            editor.putString("P", imagebase64);
            editor.commit();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        // no effective operation from user, return
        if(resultCode == RESULT_CANCELED){
            Toast.makeText(getApplication(), "Cancel", Toast.LENGTH_LONG).show();
            return;
        }
        switch (requestCode){
            case CODE_GALLERY_REQUEST:
                cropPhoto(intent.getData(), 1, 1, head_output_x, head_output_y);
                break;
            case CODE_CAMERA_REQUEST:
                if(hasSdcard()){
                    File tempFile = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
                    cropPhoto(Uri.fromFile(tempFile), 1, 1, head_output_x, head_output_y);
                } else{
                    Toast.makeText(getApplication(), "No SdCard", Toast.LENGTH_LONG).show();
                }
                break;
            case CODE_RESULT_REQUEST:
                if(intent != null){
                    setImageToHeadshot(intent, headshot);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    public void userLoginInformationFromLocal(){
        Log.d(TAG, "userLoginInformationFromLocal: Here");
        SharedPreferences preferences = getSharedPreferences("myPref", MODE_PRIVATE);
        String mUsername = preferences.getString("username","");
        String  mPassword = preferences.getString("password","");
        String mEmail = preferences.getString("email","");
        Log.d(TAG, "userLoginInformationFromLocal in setting: The email is " +mEmail +" Password is " +mPassword);

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

    class clearCache implements Runnable {
        @Override
        public void run() {
            try {
                CacheDataManager.clearAllCache(Settings.this);
                Thread.sleep(3000);
                if (CacheDataManager.getTotalCacheSize(Settings.this).startsWith("0")) {
                    handler.sendEmptyMessage(0);
                }
            } catch (Exception e) {
                return;
            }
        }
    }
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(Settings.this,"Cleaning Cache Done",Toast.LENGTH_SHORT).show();
                    try {
                        cacheText.setText(CacheDataManager.getTotalCacheSize(Settings.this));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        }
    };
}
