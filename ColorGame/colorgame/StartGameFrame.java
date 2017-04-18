package com.example.anna.colorgame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class StartGameFrame extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.anna.colorgame";
    private String message = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game_frame);

        //Get the intent that started this activity and extract the string
        Intent intent = getIntent();
        message = intent.getStringExtra(MainFrame.EXTRA_MESSAGE);

        //Capture the layout's TextView and set the string as its text
        TextView textView = (TextView) findViewById(R.id.textIPView);
        textView.setText(message);
    }

    public void startGameClicked(View view){
        Intent intent = new Intent(this, GameFrame.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
}
