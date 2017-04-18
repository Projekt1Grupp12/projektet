package com.example.anna.colorgame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class GameFrame extends AppCompatActivity {
    private static final String RED = "RED";
    private static final String YELLOW = "YELLOW";
    private static final String GREEN = "GREEN";
    private static final String COMMA = ",";
    private String str = "";
    private String message = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_frame);

        //Get the intent that started this activity and extract the string
        Intent intent = getIntent();
        message = intent.getStringExtra(StartGameFrame.EXTRA_MESSAGE);

        //Capture the layout's TextView and set the string as its text
        TextView textView = (TextView) findViewById(R.id.textIPView);
        textView.setText(message);
    }

    public void redButtonCLicked(View view){
        TextView result = (TextView) findViewById(R.id.textView);
        str = RED + COMMA;
        result.append(str);
    }

    public void yellowButtonClicked(View view){
        TextView result = (TextView) findViewById(R.id.textView);
        str = YELLOW + COMMA;
        result.append(str);
    }

    public void greenButtonClicked(View view){
        TextView result = (TextView) findViewById(R.id.textView);
        str = GREEN + COMMA;
        result.append(str);
    }
}
