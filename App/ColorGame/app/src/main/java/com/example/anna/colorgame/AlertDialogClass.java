package com.example.anna.colorgame;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AlertDialogLayout;
import android.util.Log;
import android.view.View;

/**
 * Created by George on 2017-04-28.
 */

public class AlertDialogClass extends AlertDialog{

    String TAG = "DialogClasDebug";
    AlertDialog builder = null;

    public AlertDialogClass(Context context){
        super(context);
        Log.d(TAG,"AlertDialog Constructor");
        builder = new AlertDialog.Builder(context).create();
        setCanceledOnTouchOutside(false);
    }

    public void setTitle(String title){
        super.setTitle(title);
        Log.d(TAG,"setTitle");
    }

    public void setMessage(String message){
        super.setMessage(message);
        Log.d(TAG,"setMssage()");
    }

    public void ButtonOK(){
        Log.d(TAG,"ButtonOK");
        super.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", onClickListener);
    }

    public void ButtonYesNo(){
        Log.d(TAG,"ButtonYesNo");
        super.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", onClickListener);
        super.setButton(AlertDialog.BUTTON_NEGATIVE, "No", onClickListener);
    }


    public OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Log.d(TAG, "OnClickListener");
            dialog.dismiss();
            switch (which) {
                case BUTTON_NEUTRAL:
                    Log.d(TAG, "OK button pressed");
                    break;
                case BUTTON_POSITIVE:
                    Log.d(TAG, "YES button pressed");
                    break;
                case BUTTON_NEGATIVE:
                    Log.d(TAG, "NO button pressed");
                    break;
            }
        }
    };
}
