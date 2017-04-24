package com.example.anna.colorgame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
/*
Class is an activity that shows main menu of the application.
It has three TextView to show data.
 */
public class StartGameFrame extends AppCompatActivity {
    private String ip = "";
    private String name = "";
    private String userID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game_frame);

        //Get the intent that started this activity and extract the string
        Intent intent = getIntent();
        ip = intent.getStringExtra("ip");
        name = intent.getStringExtra("name");
        userID = intent.getStringExtra("userid");

        //Capture the layout's TextView and set the string as its text
        TextView textIPView = (TextView) findViewById(R.id.textViewMove);
        textIPView.setText(ip);
        TextView textNameView = (TextView) findViewById(R.id.textNameView);
        textNameView.setText(name);
        TextView textIDView = (TextView) findViewById(R.id.textIDView);
        textIDView.setText(userID);
    }
    /*
    This method is called when button is clicked.
    It starts next activity and sends data to it using Intent class.
    */
    public void startGameClicked(View view){//Button Start Game
        Intent intent = new Intent(this, GameFrame.class);
        intent.putExtra("ip", ip);
        intent.putExtra("name", name);
        intent.putExtra("userid", userID);
        startActivity(intent);
    }
}
