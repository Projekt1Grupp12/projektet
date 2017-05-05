package com.example.anna.colorgame;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
/*
Class is an activity that shows a Login window of application.
It has two EditText and a Button.
 */
public class MainFrame extends AppCompatActivity {
    private static final String TAG = "debug";
    private Player player = new Player(null, null, null);
    private AlertDialogClass alertDialog;
    private String[] ipAdresses = {"Choose IP from the list.", "10.2.19.242", "10.2.19.28"};
    private Button loginButton = null;

    private AsyncResponse delegate = new AsyncResponse() {
        /*
        Method that has result from AsyncTask as parameter.
        It is used to get result from Asynctask and store it in userID variable.
        And at last sendMessageToNextActivity method is called.
         */
        @Override
        public void postResult(String result) {
            Log.d(TAG, "RESULTAT FRÅN SERVER " + result);
            if(result.isEmpty()){
                alertDialog = new AlertDialogClass(MainFrame.this);
                alertDialog.setTitle("Connection fialed");
                alertDialog.setMessage("Connection to game server failed");
                alertDialog.ButtonOK();
                loginButton.setEnabled(true);
            }
            else {
                player.setUserID(result);
                startNextActivity();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_frame);

        //Here is drop-down list
        final Spinner dynamicSpinner = (Spinner) findViewById(R.id.spinner);
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ipAdresses);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.my_spinner, ipAdresses);
        adapter.setDropDownViewResource(R.layout.my_spinner_dropdown);
        dynamicSpinner.setAdapter(adapter);
        dynamicSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "selection" + parent.getItemAtPosition(position));
                switch (position) {
                    case 0:
                        // Whatever you want to happen when the first item gets selected
                        break;
                    case 1:
                        EditText editText1 = (EditText) findViewById(R.id.editIPText);
                        editText1.setText(ipAdresses[1]);
                        dynamicSpinner.setSelection(0);
                        break;
                    case 2:
                        EditText editText2 = (EditText) findViewById(R.id.editIPText);
                        editText2.setText(ipAdresses[2]);
                        dynamicSpinner.setSelection(0);
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        this.loginButton = (Button)findViewById(R.id.loginButton);
    }
    /*
    This method is called when the button is clicked.
    upadateIPName method is called to store input from user as data in variables.
    Next we send data to server to get back userID.
     */
    public void sendMessage(View view) {//Button
        updateIPName();
        loginButton.setEnabled(false);
        ConnectToServer runner = new ConnectToServer(player.getChoosenIP(), delegate);
        Log.d(TAG, "Task created");
        String messageToServer = "" + player.getName();//we want to send name to server
        Log.d(TAG, "Execute task");
        runner.execute(messageToServer);
    }
    /*
    This method updates ip and name variables with user input.
     */
    public void updateIPName() {
        EditText editIPText = (EditText) findViewById(R.id.editIPText);
        EditText editNameText = (EditText) findViewById(R.id.editNameText);
        player.setChoosenIP(editIPText.getText().toString());
        player.setName(editNameText.getText().toString());
    }
    /*
    This method sends data(IP, Name, userID) to next activity using Intent class.
     */
    private void startNextActivity() {
        Log.d(TAG, "Creating new intent and sending data");
        Intent intent = new Intent(this, MainMenu.class);
        intent.putExtra("player", player);
        Log.d(TAG, "Starting new Activity");
        startActivity(intent);
    }
}


