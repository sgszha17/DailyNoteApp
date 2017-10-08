/**
 * @(ToDoActivity).java     1.61 08/10/2017
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


import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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
import com.squareup.okhttp.OkHttpClient;

import static android.content.ContentValues.TAG;

public class ToDoActivity extends FragmentActivity
        implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    private static final int RC_SIGN_IN = 9001;
    final private int registerByEmail = 0;
    final private int registerByFB = 1;
    final private int registerByGoogle = 2;
    final private int registerByWechat = 3;
    private Boolean exist;
    private Boolean logOut;

    private ProgressDialog mProgressDialog;
    private RegisterDialog registerDialog;
    private LinearLayout mainLayout;
    private TextView appName;
    private AssetManager assetManager;
    private Typeface typeface;

    private List<Note> data;
    private  LinearLayout l;
    private Users currentUser = new Users();
    protected DisplayMetrics dm = new DisplayMetrics();
    private GoogleApiClient mGoogleApiClient;

    private ImageButton google_login;
    private Button login;
    private Button register;
    private EditText enteredEmail;
    private EditText enteredPAssword;
    private ProgressBar progressBar;


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




        /* google play initialize*/
        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // [END build_client]
        google_login = (ImageButton) findViewById(R.id.google);
        google_login.setOnClickListener(this);
        /* google play initialize*/



        progressBar = (ProgressBar) findViewById(R.id.loadingProgressBar);



    /**
     * Initial Back end connection
     */
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

    //****google sign in start****
    //silent google sign in commented for siyu

    /**
    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }*/


    @Override
    protected void onResume() {
        super.onResume();
        hideProgressDialog();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
    // [END onActivityResult]

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google:
                signIn();
                break;

        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            loginSucceed();
        } else {
            // Signed out, show unauthenticated UI.
            Toast.makeText(this, "Google Signin failed. Please try again.",
                    Toast.LENGTH_LONG).show();
        }
    }

    // [START signIn]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Connection Failed",
                Toast.LENGTH_LONG).show();
    }
    //****Google sign in End****


    /**
     * Initial login and check the enter item
     * */
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

    /**
     * Finish login process and jump to next activity.
     * */

    public void loginSucceed(){
        Toast.makeText(ToDoActivity.this,"Login succeed",Toast.LENGTH_SHORT).show();
        storeUserLoginInformationOnLocal(currentUser);
        Intent intent = new Intent();
        intent.putExtra("data", (Serializable) data);
        intent.setClass(ToDoActivity.this, Notes.class);
        startActivity(intent);
        ToDoActivity.this.finish();
    }

    /**
     * login and register event listener
     * */
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
                        currentUser.setUsername(registerDialog.userName.getText().toString());
                        currentUser.setPassword(registerDialog.password.getText().toString());
                        currentUser.setEmail(registerDialog.email.getText().toString());
                        if(currentUser.getUsername().isEmpty()||currentUser.getEmail().isEmpty()||currentUser.getPassword().isEmpty()){
                            Toast.makeText(ToDoActivity.this,"Please complete all empty input box.",Toast.LENGTH_SHORT).show();
                        }else{
                            enteredEmail.setText(currentUser.getEmail());
                            enteredPAssword.setText(currentUser.getPassword());
                            addItem();
                            Toast.makeText(ToDoActivity.this,"Succeed",Toast.LENGTH_SHORT).show();
                            registerDialog.dismiss();
                            Intent intent = new Intent();
                            intent.setClass(ToDoActivity.this, Notes.class);
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
     * Add a new item
     *
     *
     * The view that originated the call
     */
    public void addItem() {
        if (mClient == null) {
            return;
        }

        // Create a new item
        final Users item = new Users();

        item.setUsername(currentUser.getUsername());
        item.setEmail(currentUser.getEmail());
        item.setPassword(currentUser.getPassword());
        item.setRegisterType(currentUser.getRegisterType());
        item.setUseFB(currentUser.isUseFB());
        item.setUseWechat(currentUser.isUseWechat());
        item.setUseGoogle(currentUser.isUseGoogle());
        item.setAdditionalData(currentUser.getAdditionalData());

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

    /**
     * Write user information which has been stored on the phone
     * @param entity
     *             The instance of Users which contain user information needing to store.
     */
    public void storeUserLoginInformationOnLocal(Users entity){
        SharedPreferences preferences = getSharedPreferences("myPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("username",entity.getUsername());
        editor.putString("password",entity.getPassword());
        editor.putString("email",entity.getEmail());

        editor.commit();
        Log.d(TAG, "storeUserLoginInformationOnLocal: Done");

    }

    /**
     * Read user information which has been stored on the phone
     */
    public void userLoginInformationFromLocal(){
        Log.d(TAG, "userLoginInformationFromLocal: Here");
        SharedPreferences preferences = getSharedPreferences("myPref", MODE_PRIVATE);
        currentUser.setUsername( preferences.getString("username",""));
        currentUser.setPassword(preferences.getString("password",""));
        currentUser.setEmail(preferences.getString("email",""));
        enteredEmail.setText(currentUser.getEmail());
        enteredPAssword.setText(currentUser.getPassword());

        if(!logOut){
            login();
        }

    }

    /**
     * Process handler operated with database retrieve thread.
     */
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