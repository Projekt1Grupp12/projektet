package com.example.anna.colorgame;import android.app.Activity;import android.app.ProgressDialog;import android.content.Intent;import android.content.res.AssetFileDescriptor;import android.media.MediaPlayer;import android.support.v7.app.AppCompatActivity;import android.os.Bundle;import android.util.Log;import android.view.MenuItem;import android.view.View;import android.widget.Button;import java.io.IOException;/*Class is an activity that shows main menu of the application.It has three TextView to show data. */public class MainMenu extends AppCompatActivity {    private static final String TAG = "debugMenu";    private Player player;    private Button chooseGameBtn;    private Class nextActivity = null;    private ConnectToServer runner;    private RecieveDataThread recieveDataThread;    private Thread thread;    private ProgressDialog pd;    private boolean start=false;    private MediaPlayer mpMenu = null;    private AsyncResponse delegate = new AsyncResponse() {        @Override        public void postResult(String result) {            Log.d(TAG, "RESULTAT FRÅN SERVER " + result);            if(result.contains("SocketTimeOut")){                //do nothing when SocketTimeOut exception            }        }};    @Override    protected void onCreate(Bundle savedInstanceState) {        Log.i(TAG, "onCreate(). MainMenu");        super.onCreate(savedInstanceState);        setContentView(R.layout.main_menu);        Intent intent = getIntent();        player = (Player)intent.getSerializableExtra("player");        recieveDataThread = new RecieveDataThread(this, player, this);//added thread 05-16        thread = new Thread(recieveDataThread);        thread.start();        startAsyncTask("choosegame?;0");        //Text on the Button change depending on userID        chooseGameBtn = (Button) findViewById(R.id.choose_game_button);        if(player.getUserID().equals("0")){            chooseGameBtn.setText("Choose Game");            chooseGameBtn.setEnabled(false);        }else if(player.getUserID().equals("1")){            chooseGameBtn.setText("Join Game");            chooseGameBtn.setEnabled(false);        }        this.mpMenu = MediaPlayer.create(this, R.raw.dxnbeats_waiting);        try {            AssetFileDescriptor afd;            afd = getAssets().openFd("dxnbeats_waiting.mp3");            mpMenu.reset();            mpMenu.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());            mpMenu.setLooping(true);            mpMenu.prepare();            mpMenu.start();        } catch (IOException e) {            e.printStackTrace();        }    }    @Override    protected void onStop(){        Log.d(TAG, "onStop() ");        recieveDataThread.setIsRunning(false);        thread.interrupt();        mpMenu.stop();        super.onStop();    }    /**     * This method is called when button "Choose Game" is clicked.     * It starts next activity and sends data to it using Intent class.     * @param view     */    public void chooseGame(View view){        Log.d(TAG, "Button Choose Game or Join Game is clicked");        Intent intent = null;        if (player.getUserID().equals("0")) {            intent = new Intent(this, ChooseGame.class);            intent.putExtra("player", player);            startActivity(intent);        }        if (player.getUserID().equals("1")) {            startAsyncTask("ready;1");//sending message to server            startProgressDialog("Starting...");        }        if (player == null) {            Log.d(TAG, "Player null");        }    }    private void startProgressDialog(String message){        pd = new ProgressDialog(MainMenu.this);        pd.setCanceledOnTouchOutside(false);        pd.setProgressStyle(pd.STYLE_SPINNER);        pd.setMessage(message);        pd.show();    }    public void startGame() {        Intent intent = null;        pd.dismiss();//when start=true, start next activity        intent = new Intent(this, nextActivity);        intent.putExtra("player", player);        startActivity(intent);    }    public void setButtonActive(){ this.chooseGameBtn.setEnabled(true);}//is used in a while loop    public void setNextActivityGame(String str) {        if (str.contains(("PuzzleGame"))) {//if server sends PuzzleGame start PuzzleGame            nextActivity = PuzzleGame.class;        } else if (str.contains(("TrafficGame"))) {            nextActivity = TrafficGame.class;        } else if (str.contains(("DuelGame"))) {            nextActivity = DuelGame.class;        } else if (str.contains(("DeathmatchGame"))) {            nextActivity = DeathmatchGame.class;        }    }    public void showHighScore(View view) {        Intent intent = new Intent(this, HighScore.class);        intent.putExtra("player", player);        startActivity(intent);    }    public void startAsyncTask(String message) {        runner = new ConnectToServer(player.getChoosenIP(), delegate);        Log.d(TAG, "Task created");        runner.execute(message);    }    @Override    public boolean onNavigateUpFromChild(Activity child) {        Log.d(TAG, "onNavigationUpFromChild");        onBackPressed();        return true;    }    @Override    public void onBackPressed() {        String message = "logout" + ";" + player.getUserID();        ConnectToServer connectToServer = new ConnectToServer(player);        Log.d(TAG, "connectToServer is created " + player.getName());        System.gc();        connectToServer.execute(message);        Log.d(TAG, "Creating new intent and sending data");        Intent intent = new Intent(this, MainFrame.class);        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);        intent.putExtra("player", player);        Log.d(TAG, "Starting new Activity");        startActivity(intent);    }}