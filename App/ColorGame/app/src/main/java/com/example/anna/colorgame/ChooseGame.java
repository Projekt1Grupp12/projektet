package com.example.anna.colorgame;import android.content.Intent;import android.content.res.AssetFileDescriptor;import android.media.MediaPlayer;import android.os.Bundle;import android.util.Log;import android.view.MenuItem;import android.view.View;import android.widget.EditText;import android.widget.SeekBar;import android.widget.TextView;import java.io.IOException;public class ChooseGame extends SuperActivity implements View.OnClickListener {    private static final String TAG = "debugChoose";    private static final String SEMICOLON = ";";    private Class startNextActivityClass = null;    private MediaPlayer mpChoose = null;    private SeekBar s = null;    private TextView editSeekBarText = null;    private AsyncResponse delegate = new AsyncResponse() {        /*        Method that has result from AsyncTask as parameter.        It is used to get result from Asynctask and store it in userID variable.        And at last sendMessageToNextActivity method is called.         */        @Override        public void postResult(String result) {            Log.d(TAG, "RESULTAT FRÅN SERVER: " + result);            if (result.contains("SocketTimeoutException")) {                closeProgressDialog();            }        }    };    @Override    protected void onDestroy() {        if(isShowingProgressBar()){            closeProgressDialog();        }        super.onDestroy();    }    @Override    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        setContentView(R.layout.choose_game);        Log.i(TAG, "onCreate().ChooseGame");        Intent intent = getIntent();        setPlayer((Player) intent.getSerializableExtra("player"));        startThreadChooseGame(this, getPlayer(), ChooseGame.this);        s = (SeekBar) findViewById(R.id.seekBar);        editSeekBarText = (TextView) findViewById(R.id.seekBarTextView);        editSeekBarText.setText("Score limit: " + String.valueOf(s.getProgress()+1));        s.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {            @Override            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {                int scoreLimit = (progress+1)*9;                editSeekBarText.setText("Score limit: " + String.valueOf(scoreLimit));            }            @Override            public void onStartTrackingTouch(SeekBar seekBar) {/*do nothing*/}            @Override            public void onStopTrackingTouch(SeekBar seekBar) {                String data = "scorelimit;0;" + String.valueOf((s.getProgress()+1)*9);                startAsyncTask(data, getPlayer(), delegate);                // TODO Auto-generated method stub            }        });        }    @Override    protected void onResume() {//added 05-16 for thread        Log.d(TAG, "onResume() ");        this.mpChoose = MediaPlayer.create(this, R.raw.dxnbeats_waiting);        try {            AssetFileDescriptor afd;            afd = getAssets().openFd("dxnbeats_waiting.mp3");            mpChoose.reset();            mpChoose.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());            mpChoose.setLooping(true);            mpChoose.prepare();            mpChoose.start();        } catch (IOException e) {            e.printStackTrace();        }        super.onResume();    }    @Override    protected void onStop() {//added 05-16 for thread        Log.d(TAG, "onDestroy() ");        mpChoose.stop();        closeReceiveThread();        super.onStop();    }    /*    This method is called when one of the buttons is clicked in the GUI.    Which button is pushed is stored in data variable.    Then the semicolon and userID is added to data string and sent to startAsyncTask method.     */    @Override    public void onClick(View view) {        startProgressDialog("Waiting for opponent...", this);        Log.d(TAG, "onClick()-metod");        String data = " ";        switch (view.getId()) {            case R.id.buttonPuzzle:                this.startNextActivityClass = PuzzleGame.class;                data = "PuzzleGame";                break;            case R.id.buttonTraffic:                this.startNextActivityClass = TrafficGame.class;                data = "TrafficGame";                break;            case R.id.buttonDuel:                this.startNextActivityClass = DuelGame.class;                data = "DuelGame";                break;            case R.id.buttonDeathmatch:                this.startNextActivityClass = DeathmatchGame.class;                data = "DeathmatchGame";                break;        }        startAsyncTask(data + SEMICOLON + getPlayer().getUserID(), this.getPlayer(), delegate);//using new class. need delegate    }    public void startGame() {        closeProgressDialog();//when start=true, start next activity        startNextActivity(getPlayer(), this, startNextActivityClass);    }    @Override    public boolean onOptionsItemSelected(MenuItem item) {        switch (item.getItemId()) {            case android.R.id.home:                onBackPressed();                break;        }        return true;    }    @Override    public void onBackPressed() {        startNextActivity(getPlayer(), this, MainMenu.class);    }}