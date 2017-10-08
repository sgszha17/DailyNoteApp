/**
 * @(RegisterDialog).java     1.61 08/10/2017
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

import android.app.Dialog;
import android.os.Bundle;
import android.content.Context;
import android.view.View;
import android.widget.*;


/**
 * Created by Siyu Zhang on 2017/10/1.
 */

public class RegisterDialog extends Dialog {

    private Button confirm;
    private Button cancel;
    protected EditText userName;
    protected EditText email;
    protected EditText password;

    private onCancelOnclickListener  cancelOnclickListener;
    private onConfirmOnclickListener  confrimOnclickListener;

    public RegisterDialog(Context context){
        super(context, R.style.MyDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_dialog);
        setCanceledOnTouchOutside(false);
        userName = (EditText) findViewById(R.id.username2);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);


        confirm = (Button) findViewById(R.id.Confirm);
        cancel = (Button) findViewById(R.id.Cancel);
        initEvent();
    }

    private void initEvent() {

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confrimOnclickListener != null) {
                    confrimOnclickListener.onConfirmClick();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancelOnclickListener != null) {
                    cancelOnclickListener.onCancelClick();
                }
            }
        });
    }

    public void setCancelOnclickListener(onCancelOnclickListener onCancelOnclickListener) {

        this.cancelOnclickListener= onCancelOnclickListener;
    }


    public void setConfirmOnclickListener(onConfirmOnclickListener onConfirmOnclickListener) {

        this.confrimOnclickListener = onConfirmOnclickListener;
    }

    public interface onConfirmOnclickListener {
        public void onConfirmClick();
    }

    public interface onCancelOnclickListener {
        public void onCancelClick();
    }
}
