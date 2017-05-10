package com.example.anna.colorgame;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

/*
Class is an activity that shows a Login window of application.
It has two EditText and a Button.
 */
public class MainFrame extends AppCompatActivity {
    private static final String TAG = "debug";
    private Player player = null;
    private String[] ipAdresses = {"Choose IP from the list.", "10.2.19.242", "10.2.19.28", "10.2.28.40"};
    private Button loginButton = null;
    private EditText editIPText = null;
    private EditText editNameText = null;

    /*
    Method that has result from AsyncTask as parameter.
    It is used to get result from Asynctask and store it in userID variable.
    And at last sendMessageToNextActivity method is called.
     */
    private AsyncResponse delegate = new AsyncResponse() {
        @Override
        public void postResult(String result) {
            Log.d(TAG, "RESULTAT FRÅN SERVER " + result);
            if (result.contains("SocketTimeoutException")) {
                AlertDialogClass alertDialog = new AlertDialogClass(MainFrame.this);
                alertDialog.setTitle("Connection failed");
                alertDialog.setMessage("Connection to game server failed");
                alertDialog.ButtonOK();
                loginButton.setEnabled(true);
            } else {
                player.setUserID(result);
                startNextActivity();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_frame);
        this.loginButton = (Button) findViewById(R.id.loginButton);
        //EditText code here
        editNameText = (EditText) findViewById(R.id.editNameText);
        editIPText = (EditText) findViewById(R.id.editIPText);
        //This listener is called when focus state of editIPText is changed.
        editIPText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                editIPText.setError(null);
                boolean temp = validateIP(editIPText.getText().toString());
                if (!hasFocus) {
                    Log.d(TAG, "When editIPText loses focus, validate ip");
                    if (!temp) {
                        editIPText.setError("Ip address is not valid");
                    }
                }
            }
        });
        //Spinner code here
        final Spinner dynamicSpinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.my_spinner, ipAdresses);
        adapter.setDropDownViewResource(R.layout.my_spinner_dropdown);
        dynamicSpinner.setAdapter(adapter);
        dynamicSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "selection" + parent.getItemAtPosition(position));
                selectionMade(dynamicSpinner, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "MF onRestart()");
        super.onRestart();
        editIPText = (EditText) findViewById(R.id.editIPText);
        editNameText = (EditText) findViewById(R.id.editNameText);
        editNameText.setText(null);//chnged "" to null 05-09
        editIPText.setText(null);
        loginButton.setEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }

    /*
    This method is called when the button is clicked.
    upadateIPName method is called to store input from user as data in variables.
    Next we send data to server to get back userID.
     */
    private void login(View view) {//Button
        updateIPName();
        loginButton.setEnabled(false);
        ConnectToServer runner = new ConnectToServer(player.getChoosenIP(), delegate);
        Log.d(TAG, "Task created");
        String messageToServer = "" + player.getName();
        Log.d(TAG, "Execute task");
        runner.execute(messageToServer);
    }

    /*
    This method updates ip and name variables with user input.
     */
    private void updateIPName() {
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //Log.d(TAG, "inside dispatchTouchEvent");
        boolean handleReturn = super.dispatchTouchEvent(ev);
        View view = getCurrentFocus();
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        if (view instanceof EditText) {
            View innerView = getCurrentFocus();
            // Log.d(TAG, "view instanceof EditText");
            if (ev.getAction() == MotionEvent.ACTION_UP &&
                    !getLocationOnScreen((EditText) innerView).contains(x, y)) {
                // Log.d(TAG, "ev.getAction() == MotionEvent.ACTION_UP");
                InputMethodManager input = (InputMethodManager)
                        getSystemService(MainFrame.this.INPUT_METHOD_SERVICE);
                input.hideSoftInputFromWindow(getWindow().getCurrentFocus()
                        .getWindowToken(), 0);
            }
        }

        return handleReturn;
    }

    protected Rect getLocationOnScreen(EditText mEditText) {
        Rect mRect = new Rect();
        int[] location = new int[2];
        mEditText.getLocationOnScreen(location);
        mRect.left = location[0];
        mRect.top = location[1];
        mRect.right = location[0] + mEditText.getWidth();
        mRect.bottom = location[1] + mEditText.getHeight();

        return mRect;
    }

    /*
    This method validates Ip address. It is called when focus state of editIPText is changed.
    If IP is correct return true and if not return false
     */
    private boolean validateIP(String str) {
        Log.d(TAG, "validateIP " + str);
        String[] parts = str.split("\\.");
        if (str == null || str.isEmpty() || parts.length != 4 || str.endsWith(".")) {
            return false;
        } else {
            for (String s : parts) {
                int digit = Integer.parseInt(s);
                if ((digit < 0) || (digit > 255)) {
                    return false;
                }
            }
        }
        return true;
    }

    /*
    This method is called by Spinners onItemSelectedListener.
     */
    private void selectionMade(Spinner dynamicSpinner, int position) {
        switch (position) {
            case 0:
                // Whatever you want to happen when the first item gets selected
                break;
            case 1:
                editIPText.setText(ipAdresses[1]);
                editIPText.requestFocus();
                dynamicSpinner.setSelection(0);
                break;
            case 2:
                editIPText.setText(ipAdresses[2]);
                editIPText.requestFocus();
                dynamicSpinner.setSelection(0);
                break;
            case 3:
                editIPText.setText(ipAdresses[3]);
                editIPText.requestFocus();
                dynamicSpinner.setSelection(0);
                break;
        }
    }
}






