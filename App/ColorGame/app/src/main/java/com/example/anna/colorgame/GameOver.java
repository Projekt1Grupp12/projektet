package com.example.anna.colorgame;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;

import java.io.IOException;

/**
 * Created by George on 2017-04-28.
 */

public class GameOver extends AppCompatActivity {

    private String TAG = "GameOverDebug";
    private AlertDialogClass alertDialog = null;
    private String message = null;
    private String playAgainTitle = "New game?";
    private String playAgain = "Do you want to play again?";
    private int userID = -1;
    private boolean userIsWinner = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void playerIsWinner(){
        this.message = "You have won!";
    }

    public void playerIsLoser(){
        this.message = "You have lost.";
    }

    private AsyncResponse delegate = new AsyncResponse() {
        /*
        Method that has result from AsyncTask as parameter.
        It is used to update TextView in this activity.
         */
        @Override
        public void postResult(String result) {
                if (result.contains("WINNER")) {
                    playerIsWinner();
                } else if (result.contains("LOSER")) {
                    playerIsLoser();
                }
                displayMessage();
        }
    };

    public void displayMessage(){
        alertDialog = new AlertDialogClass(GameOver.this);
        alertDialog.ButtonOK();
        alertDialog.setTitle("");
        alertDialog.setMessage(message);
        alertDialog.show();
        try {
            wait(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        alertDialog.dismiss();

        alertDialog = new AlertDialogClass(GameOver.this);
        alertDialog.setTitle(playAgainTitle);
        alertDialog.setMessage(playAgain);
        alertDialog.ButtonYesNo();
    }

}
