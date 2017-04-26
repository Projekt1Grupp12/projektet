package com.example.anna.colorgame;

import android.content.DialogInterface;
import android.content.Intent;
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

/*
Class is an activity that shows a Login window of application.
It has two EditText and a Button.
 */
public class MainFrame extends AppCompatActivity {
    private static final String TAG = "debug";
    private String ip = "";
    private String name = "";
    private String userID = "";
    private String[] ipAdresses = {"Choose IP from the list.", "10.2.19.242", "10.2.19.28"};
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
                alertDialog.show();
            }
            else {
                userID = result;
                sendMessageToNextActivity();
            }
        }
    };


    private AlertDialog alertDialog;



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
                        //EditText editText0 = (EditText) findViewById(R.id.editIPText);
                        // editText0.setText(ipAdresses[0]);
                        // Whatever you want to happen when the first item gets selected
                        break;
                    case 1:
                        EditText editText1 = (EditText) findViewById(R.id.editIPText);
                        editText1.setText(ipAdresses[1]);
                        dynamicSpinner.setSelection(0);
                        // Whatever you want to happen when the first item gets selected
                        break;
                    case 2:
                        EditText editText2 = (EditText) findViewById(R.id.editIPText);
                        editText2.setText(ipAdresses[2]);
                        dynamicSpinner.setSelection(0);
                        // Whatever you want to happen when the second item gets selected
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        alertDialog = new AlertDialog.Builder(MainFrame.this).create();
        alertDialog.setTitle("Connection fialed");
        alertDialog.setMessage("Connection to game server fialed");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
    }
    /*
    This method is called when the button is clicked.
    upadateIPName method is called to store input from user as data in variables.
    Next we send data to server to get back userID.
     */
    public void sendMessage(View view) {//Button
        updateIPName();
        ConnectToServer runner = new ConnectToServer(ip, delegate);
        Log.d(TAG, "Task created");
        String messageToServer = "" + name;//we want to send name to server
        Log.d(TAG, "Execute task");
        runner.execute(messageToServer);
    }
    /*
    This method updates ip and name variables with user input.
     */
    public void updateIPName() {
        EditText editIPText = (EditText) findViewById(R.id.editIPText);
        EditText editNameText = (EditText) findViewById(R.id.editNameText);
        //this.ip = "10.2.19.28";
        this.ip = editIPText.getText().toString();
        //this.name = "george";
        this.name = editNameText.getText().toString();
    }
    /*
    This method sends data(IP, Name, userID) to next activity using Intent class.
     */
    public void sendMessageToNextActivity() {
        Log.d(TAG, "Creating new intent");
        Intent intent = new Intent(this, MainMenu.class);
        Log.d(TAG, "IP and NAME" + ip + " " + name + " " + userID);
        intent.putExtra("ip", ip);
        intent.putExtra("name", name);
        intent. putExtra("userid", userID);
        Log.d(TAG, "Starting new Activity");
        startActivity(intent);
    }
}


