package com.example.dailynote;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncContext;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.ColumnDataType;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.MobileServiceLocalStoreException;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.SQLiteLocalStore;
import com.microsoft.windowsazure.mobileservices.table.sync.synchandler.SimpleSyncHandler;
import com.squareup.okhttp.OkHttpClient;

import static android.content.ContentValues.TAG;

public class ToDoActivity extends Activity {
    private LinearLayout mainLayout;
    private TextView appName;
    private AssetManager assetManager;
    private Typeface typeface;
    DisplayMetrics dm = new DisplayMetrics();
    private ImageButton facebook_login;
    private ImageButton google_login;
    private ImageButton wechat_login;

    private RegisterDialog registerDialog;
    private String mUsername="";
    private String mEmail="";
    private String mPassword="";
    private Button login;
    private Button register;
    private EditText enteredEmail;
    private EditText enteredPAssword;
    final private int registerByEmail = 0;
    final private int registerByFB = 1;
    final private int registerByGoogle = 2;
    final private int registerByWechat = 3;
    private Boolean exist;
    private ProgressBar progressBar;
    private  LinearLayout l;
    private Boolean logOut;
    private Users currentUser = new Users();
    /**
     * Mobile Service Client reference
     */
    private MobileServiceClient mClient;

    /**
     * Mobile Service Table used to access data
     */
    private MobileServiceTable<Users> user;

    /**
     * Initializes the activity
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);
        readData();
        exist = false;
        Intent intent = getIntent();
        logOut = intent.getBooleanExtra("logout",false);

        mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        mainLayout.setBackgroundColor(Color.rgb(48, 66,82));
        appName = (TextView) findViewById(R.id.appName);
        assetManager = getAssets();
        typeface = Typeface.createFromAsset(assetManager, "Kannada MN.ttc");
        appName.setTypeface(typeface);
        appName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50);
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        int width = dm.widthPixels;

        enteredEmail = (EditText)findViewById(R.id.enterEmail);
        enteredPAssword = (EditText)findViewById(R.id.enterPassword);

        facebook_login = (ImageButton) findViewById(R.id.facebook);
        facebook_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        google_login = (ImageButton) findViewById(R.id.google);
        google_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        wechat_login = (ImageButton) findViewById(R.id.wechat);
        wechat_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        progressBar = (ProgressBar) findViewById(R.id.loadingProgressBar);



        try {
            // Create the Mobile Service Client instance, using the provided
            // Mobile Service URL and key
            mClient = new MobileServiceClient(
                    "https://dailynote.azurewebsites.net",
                    this).withFilter(new ProgressFilter());

            // Extend timeout from default of 10s to 20s
            mClient.setAndroidHttpClientFactory(new OkHttpClientFactory() {
                @Override
                public OkHttpClient createOkHttpClient() {
                    OkHttpClient client = new OkHttpClient();
                    client.setReadTimeout(20, TimeUnit.SECONDS);
                    client.setWriteTimeout(20, TimeUnit.SECONDS);
                    return client;
                }
            });

        } catch (MalformedURLException e) {
         //   createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error");
        } catch (Exception e){
          //  createAndShowDialog(e, "Error");
        }

        //Get the Mobile Service Table instance to use
        user = mClient.getTable(Users.class);
        loginAndRegister();
    }

    @SuppressWarnings("unchecked")
    public void readData(){
        try {
            File dataFile = new File(this.getFilesDir(), "data.txt");
            FileInputStream fis = new FileInputStream(dataFile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Notes.data = (List<Note>) ois.readObject();
            ois.close();
        } catch (FileNotFoundException e){
            Notes.data = null;
            Log.e("FileNotFound", e.getMessage());
        } catch (Exception e){
            Notes.data = null;
            Log.e("MyException", "myexception");
        }
    }

    public void login(){
        try {
            Log.d(TAG, "onClick: something");

            currentUser.setEmail(enteredEmail.getText().toString());
            currentUser.setPassword(enteredPAssword.getText().toString());
            Log.d(TAG, "login: email is " +currentUser.getEmail());

            if (!currentUser.getEmail().isEmpty()&& !currentUser.getPassword().isEmpty()) {
                retrieveUsers();
            }else {
                Toast.makeText(ToDoActivity.this,"Please enter email and password.",Toast.LENGTH_SHORT).show();
            }

        }catch(ExecutionException ee){
            Log.e(TAG, "onClick: Execution ERROR:",ee );
        }catch(InterruptedException ie){
            Log.e(TAG, "onClick: Interryption ERROR:",ie );
        }
    }

    public void loginSucceed(){
        Toast.makeText(ToDoActivity.this,"Login succeed",Toast.LENGTH_SHORT).show();
        storeUserLoginInformationOnLocal(currentUser);
        Intent intent = new Intent();
        intent.setClass(ToDoActivity.this, Notes.class);
        Log.d(TAG, "loginSucceed: Username is "+mUsername);
        Log.d(TAG, "loginSucceed: password is "+mPassword);
        Log.d(TAG, "loginSucceed: email is "+mEmail);
        String[] userData = {currentUser.getUsername(),currentUser.getPassword(),currentUser.getEmail()};
        startActivity(intent);
        ToDoActivity.this.finish();
    }

    // login and register method
    public void loginAndRegister(){

        userLoginInformationFromLocal();

        login = (Button) findViewById(R.id.Login);
        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                login();
            }
        });
        register = (Button)findViewById(R.id.Register);
        register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                registerDialog = new RegisterDialog(ToDoActivity.this);
                registerDialog.show();
                registerDialog.setCancelOnclickListener(new RegisterDialog.onCancelOnclickListener(){
                    @Override
                    public void onCancelClick() {
                        registerDialog.dismiss();
                    }
                });
                registerDialog.setConfirmOnclickListener(new RegisterDialog.onConfirmOnclickListener(){
                    @Override
                    public void onConfirmClick() {
                        mUsername = registerDialog.userName.getText().toString();
                        mPassword = registerDialog.password.getText().toString();
                        mEmail = registerDialog.email.getText().toString();
                        if(mUsername.isEmpty()||mEmail.isEmpty()||mPassword.isEmpty()){
                            Toast.makeText(ToDoActivity.this,"Please complete all empty input box.",Toast.LENGTH_SHORT).show();
                        }else{
                            enteredEmail.setText(mEmail);
                            enteredPAssword.setText(mPassword);
                            addItem();
                            Toast.makeText(ToDoActivity.this,"Succeed",Toast.LENGTH_SHORT).show();
                            registerDialog.dismiss();
                            Intent intent = new Intent();
                            intent.setClass(ToDoActivity.this, Notes.class);
//                            String[] userData = {mUsername,mPassword,mEmail};
//                            intent.putExtra("getin",userData);
                            startActivity(intent);
                            ToDoActivity.this.finish();
                        }

                    }
                });
            }
        });
    }

    /**
     * Initializes the activity menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    /**
     * Select an option from the menu
     */
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.menu_refresh) {
//            refreshItemsFromTable();
//        }
//
//        return true;
//    }


    /**
     * Mark an item as completed in the Mobile Service Table
     *
     * @param item
     *            The item to mark
     */
    public void checkItemInTable(Users item) throws ExecutionException, InterruptedException {
        user.update(item).get();
    }

    /**
     * Add a new item
     *
     *
     *            The view that originated the call
     */
    public void addItem() {
        if (mClient == null) {
            return;
        }

        // Create a new item
        final Users item = new Users();

        item.setUsername(mUsername);
        item.setEmail(mEmail);
        item.setPassword(mPassword);
        item.setRegisterType(registerByEmail);
        item.setUseFB(false);
        item.setUseWechat(false);
        item.setUseGoogle(false);
        item.setAdditionalData("");

        // Insert the new item
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final Users entity = addItemInTable(item);
                    storeUserLoginInformationOnLocal(entity);

                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }
                return null;
            }
        };

        runAsyncTask(task);

    }


    /**
     * Add an item to the Mobile Service Table
     *
     * @param item
     *            The item to Add
     */
    public Users addItemInTable(Users item) throws ExecutionException, InterruptedException {
        Log.d(TAG, "addItemInTable: Debug");
       Users entity = user.insert(item).get();

        return entity;
    }
    /**
     * Retrieve items to the Mobile Service Table
     *
     *
     */

    private Boolean retrieveUsers() throws ExecutionException, InterruptedException {

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    List<Users> temp = user.where().field("email").eq(currentUser.getEmail()).and().field("password").eq(currentUser.getPassword()).execute().get();
                    Log.d(TAG, "doInBackground: the size is "+temp.size());
                    if (!temp.isEmpty()){
                        exist = true;
                        currentUser.setUsername(temp.get(0).getUsername());
                        Log.d(TAG, "doInBackground: exist is "+exist);
                        Log.d(TAG, "doInBackground: email is "+temp.get(0).getEmail());
                        Log.d(TAG, "doInBackground: password is "+temp.get(0).getPassword());
                    }

                } catch (final Exception e){
                    createAndShowDialogFromTask(e, "Error");
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Log.d(TAG, "onPostExecute: running, and exist is "+exist);
                if(exist){
                    if (progressBar != null) {
                        progressBar.setVisibility(ProgressBar.GONE);
                        l.setVisibility(l.VISIBLE);
                    }
                    loginSucceed();
                }else{
                    Toast.makeText(ToDoActivity.this,"Email or password wrong",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (progressBar != null) {
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                    l = (LinearLayout)findViewById(R.id.refisterAndLoginBotton);
                    l.setVisibility(l.GONE);
                }
            }
        };
        runAsyncTask(task);

        return exist;
    }

    /**
     * Initialize local storage
     * @return
     * @throws MobileServiceLocalStoreException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private AsyncTask<Void, Void, Void> initLocalStore() throws MobileServiceLocalStoreException, ExecutionException, InterruptedException {

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {

                    MobileServiceSyncContext syncContext = mClient.getSyncContext();

                    if (syncContext.isInitialized())
                        return null;

                    SQLiteLocalStore localStore = new SQLiteLocalStore(mClient.getContext(), "OfflineStore", null, 1);

                    Map<String, ColumnDataType> tableDefinition = new HashMap<String, ColumnDataType>();
                    tableDefinition.put("username", ColumnDataType.String);
                    tableDefinition.put("email", ColumnDataType.String);
                    tableDefinition.put("password", ColumnDataType.String);

                    localStore.defineTable("Users", tableDefinition);

                    SimpleSyncHandler handler = new SimpleSyncHandler();

                    syncContext.initialize(localStore, handler).get();

                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }

                return null;
            }
        };

        return runAsyncTask(task);
    }

    //Offline Sync
    /**
     * Sync the current context and the Mobile Service Sync Table
     * @return
     */
    /*
    private AsyncTask<Void, Void, Void> sync() {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    MobileServiceSyncContext syncContext = mClient.getSyncContext();
                    syncContext.push().get();
                    mToDoTable.pull(null).get();
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }
                return null;
            }
        };
        return runAsyncTask(task);
    }
    */

    /**
     * Creates a dialog and shows it
     *
     * @param exception
     *            The exception to show in the dialog
     * @param title
     *            The dialog title
     */
    private void createAndShowDialogFromTask(final Exception exception, String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createAndShowDialog(exception, "Error");
            }
        });
    }


    /**
     * Creates a dialog and shows it
     *
     * @param exception
     *            The exception to show in the dialog
     * @param title
     *            The dialog title
     */
    private void createAndShowDialog(Exception exception, String title) {
        Throwable ex = exception;
        if(exception.getCause() != null){
            ex = exception.getCause();
        }
        createAndShowDialog(ex.getMessage(), title);
    }

    /**
     * Creates a dialog and shows it
     *
     * @param message
     *            The dialog message
     * @param title
     *            The dialog title
     */
    private void createAndShowDialog(final String message, final String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }

    /**
     * Run an ASync task on the corresponding executor
     * @param task
     * @return
     */
    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }

    public void storeUserLoginInformationOnLocal(Users entity){
        SharedPreferences preferences = getSharedPreferences("myPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("username",entity.getUsername());
        editor.putString("password",entity.getPassword());
        editor.putString("email",entity.getEmail());

        editor.commit();
        Log.d(TAG, "storeUserLoginInformationOnLocal: Done");

    }

    public void userLoginInformationFromLocal(){
        Log.d(TAG, "userLoginInformationFromLocal: Here");
        SharedPreferences preferences = getSharedPreferences("myPref", MODE_PRIVATE);
        mUsername = preferences.getString("username","");
        mPassword = preferences.getString("password","");
        mEmail = preferences.getString("email","");
        enteredEmail.setText(mEmail);
        enteredPAssword.setText(mPassword);
        Log.d(TAG, "userLoginInformationFromLocal: The email is " +mEmail +" Password is " +mPassword);
        if(!logOut){
            login();
        }

    }
    private class ProgressFilter implements ServiceFilter {

        @Override
        public ListenableFuture<ServiceFilterResponse> handleRequest(ServiceFilterRequest request, NextServiceFilterCallback nextServiceFilterCallback) {

            final SettableFuture<ServiceFilterResponse> resultFuture = SettableFuture.create();


            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    if (progressBar != null) {
                        progressBar.setVisibility(ProgressBar.VISIBLE);
                        l = (LinearLayout)findViewById(R.id.refisterAndLoginBotton);
                        l.setVisibility(l.GONE);
                    }
                }
            });

            ListenableFuture<ServiceFilterResponse> future = nextServiceFilterCallback.onNext(request);

            Futures.addCallback(future, new FutureCallback<ServiceFilterResponse>() {
                @Override
                public void onFailure(Throwable e) {
                    resultFuture.setException(e);
                }

                @Override
                public void onSuccess(ServiceFilterResponse response) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (progressBar != null) {
                                progressBar.setVisibility(ProgressBar.GONE);
                                l.setVisibility(l.VISIBLE);
                            }
                        }
                    });

                    resultFuture.set(response);
                }
            });

            return resultFuture;
        }
    }

}