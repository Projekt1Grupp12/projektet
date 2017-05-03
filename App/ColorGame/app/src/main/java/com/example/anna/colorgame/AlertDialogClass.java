package com.example.anna.colorgame;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import static android.support.v7.app.AlertDialog.*;

/**
 * Created by George on 2017-04-28.
 */

public class AlertDialogClass{

    String TAG = "DialogClassDebug";
    AlertDialog builder = null;
    private String answer = null;
    private boolean isDimissed = false;
    private Player player = null;
    private Context context;

    private Class intentNewGame = MainFrame.class;
    private Class intentPlayAgain= ChooseGame.class;

    public AlertDialogClass(Context context){
        Log.d(TAG,"AlertDialog Constructor");
        this.context = context;

        AlertDialog  builder = new AlertDialog.Builder(context).create();
        this.builder = builder;

        builder.setCanceledOnTouchOutside(false);
    }

    public void setTitle(String title){
        builder.setTitle(title);
        Log.d(TAG,"setTitle()");
    }

    public void setMessage(String message){
        Log.d(TAG,"setMssage()");

        builder.setMessage(message);
    }

    public void ButtonContinue(){
        Log.d(TAG,"ButtonContinue()");

        builder.setButton(BUTTON_NEUTRAL, "Continue", onClickListener);
        builder.setOnDismissListener(onDismissListener);
        builder.show();
    }

    public void ButtonOK(){
        Log.d(TAG,"ButtonOK()");
        builder.setButton(BUTTON_NEUTRAL, "OK", onClickListener);
        builder.setOnDismissListener(onDismissListener);
        builder.show();
    }

    public void ButtonYesNo(){
        Log.d(TAG,"ButtonYesNo");
        builder.setButton(BUTTON_POSITIVE, "Yes", onClickListener);
        builder.setButton(BUTTON_NEGATIVE, "No", onClickListener);
        builder.setOnDismissListener(onDismissListener);
        builder.show();;
    }

    public OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Log.d(TAG, "OnClickListener");
            switch (which) {
                case BUTTON_NEUTRAL:
                    Log.d(TAG, "OK button pressed");
                    break;
                case BUTTON_POSITIVE:
                    Log.d(TAG, "YES button pressed");
                    setAnswer("YES");
                    break;
                case BUTTON_NEGATIVE:
                    Log.d(TAG, "NO button pressed");
                    setAnswer("NO");
                    break;
            }
        }
    };

    public OnDismissListener onDismissListener = new OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            Log.d(TAG, "Dialog dismissed");

            if(player == null)
                Log.d(TAG, "player == null");
            else if (answer.contains("YES")) {
                sendMessageToNextActivity(intentPlayAgain, player);
            } else if (answer.contains("NO")) {
                sendMessageToNextActivity(intentNewGame, player);
            } else
                Log.d(TAG, "else, answer: " + answer);

            setIsDismissed(true);
            builder.dismiss();
        }
    };

    private void setAnswer(String answer){
        this.answer = answer;
    }
    public String getAnswer(){ return this.answer; }

    private void setIsDismissed(boolean isDimissed){
        this.isDimissed = isDimissed;
    }
    public boolean isDismissed(){ return this.isDimissed; }

    public void setPlayer(Player player){
        this.player = new Player(player.getName(), player.getUserID(), player.getChoosenIP());
    }

    /*
 This method sends data(IP, Name, userID) to next activity using Intent class.
  */
    public void sendMessageToNextActivity(Class startClass, Player player) {
        Log.d(TAG, "Creating new intent and sending data");
        Intent intent = new Intent(context, startClass);
        intent.putExtra("player", player);
        Log.d(TAG, "Starting new Activity");
        context.startActivity(intent);
    }
}
