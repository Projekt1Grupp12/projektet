package com.example.anna.colorgame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainFrame extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.anna.colorgame";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_frame);
    }

    public void sendMessage(View view){
        Intent intent = new Intent(this, StartGameFrame.class);
        EditText editText = (EditText) findViewById(R.id.editIPText);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
}
