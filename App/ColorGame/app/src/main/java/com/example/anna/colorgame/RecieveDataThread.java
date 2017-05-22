package com.example.anna.colorgame;import android.content.Context;import android.util.Log;import java.io.IOException;import java.io.InterruptedIOException;import java.net.DatagramPacket;import java.net.DatagramSocket;import java.net.InetSocketAddress;import java.net.SocketException;import java.net.SocketTimeoutException;import android.os.Handler;/** * This class is a thread that is used in PuzzleGame class(Activity). * After it is started it waits for a specified message from server. * When thread recieve message from the server it shows an AlertDialog to the user. */public class RecieveDataThread implements Runnable {    private static final String TAG = "debugRecieve";   // private static final int PORT = 4445;    private final Handler handler;    private Context context;    private Player player;    private DatagramSocket mySocket = null;    private DatagramPacket receivePacket = null;    private byte[] receiveData = null;    private boolean isRunning=true;    private ChooseGame ChooseGameClass = null;    private MainMenu MainMenuClass = null;    /**     * This constructor creates new instance of a class with context as parameter.     * It initiate context and handler instances.     * @param context     * @param player     */    public RecieveDataThread(Context context, Player player) {        this.player = player;        this.context = context;        handler = new Handler(context.getMainLooper());    }    /**     * This constructor creates new instance of a class with context as parameter.     * It initiate context and handler instances.     * @param context     * @param player     */    public RecieveDataThread(Context context, Player player, ChooseGame thisClass) {        this.player = player;        this.context = context;        handler = new Handler(context.getMainLooper());        this.ChooseGameClass = thisClass;    }    /**     * This constructor creates new instance of a class with context as parameter.     * It initiate context and handler instances.     * @param context     * @param player     */    public RecieveDataThread(Context context, Player player, MainMenu thisClass) {        this.player = player;        this.context = context;        handler = new Handler(context.getMainLooper());        this.MainMenuClass = thisClass;    }    /**     * This method contains code that makes it possible to wait for a message from server and     * show AlertDialog.     * First udp socket is created that is supposed to listen for incoming data on port 4445 in an     * infinite while loop.     */    @Override    public void run() {        Log.d(TAG, "RecieveDataThread, run method");        try {            int PORT = 4445;            Log.d(TAG, "Thread. Creating Socket");            mySocket = new DatagramSocket(null);            mySocket.setReuseAddress(true);            mySocket.bind(new InetSocketAddress(PORT));            //mySocket.setSoTimeout(1000);            while (isRunning) {                try {                    Log.d(TAG, "Thread. Initialising byte array and DatagramPacket");                    receiveData = new byte[24];                    receivePacket = new DatagramPacket(receiveData, receiveData.length);                    Log.d(TAG, "Thread. Waiting for message from server");                    mySocket.receive(receivePacket);                    Log.d(TAG, "Thread. Reading DatagramPacket we got from server");                    final String messageFromServer = putChar(receiveData, receiveData.length);                    /*                    Here must be a if-else for different messages in AlertDialog frame                    depending on message from server.                     */                    if (messageFromServer.equals("WIN!")) {                        runOnUiThread(new Runnable() {                            @Override                            public void run() {                                Log.d(TAG, "runOnUiThread, run method WIN");                                GameOver gameOver = new GameOver(context, player, "WIN!");                            }                        });                    } else if (messageFromServer.equals("LOSE!")) {                        runOnUiThread(new Runnable() {                            @Override                            public void run() {                                Log.d(TAG, "runOnUiThread, run method LOSE");                                GameOver gameOver = new GameOver(context, player, "LOSE!");                            }                        });                    } else if (messageFromServer.equals("logout")) {                        runOnUiThread(new Runnable() {                            @Override                            public void run() {                                Log.d(TAG, "runOnUiThread, run method logout");                                GameOver gameOver = new GameOver(context, player, "logout");                            }                        });                    /*                    server send start to both users.                    User0 see "waiting for opponent".                    User1 see "starting..."                    when they get message "start" game starts for both of them.                     */                    } else if (messageFromServer.equals("start")) {                        runOnUiThread(new Runnable() {                            @Override                            public void run() {                                Log.d(TAG, "runOnUiThread, run method start");                                if (context.getClass().getName().contains("MainMenu")) {                                    MainMenuClass.startGame();                                } else if (context.getClass().getName().contains("ChooseGame")) {                                    ChooseGameClass.startGame();                                }                            }                        });                    /*when user0 made a choice of game. we get choice from server                    for example(Puzzle Game)                     */                    } else if (messageFromServer.contains("Game")) {                        runOnUiThread(new Runnable() {                            @Override                            public void run() {                                Log.d(TAG, "runOnUiThread, run method game");                                MainMenuClass.setNextActivityGame(messageFromServer);                                MainMenuClass.setButtonActive();                            }                        });                    }else if (messageFromServer.contains("choosegame")) {                        runOnUiThread(new Runnable() {                            @Override                            public void run() {                                Log.d(TAG, "runOnUiThread, run method game");                                MainMenuClass.setButtonActive();                            }                        });                    }                }catch (SocketTimeoutException e){                    Log.d(TAG, "SocketTimeoutException!");                }                Log.d(TAG, "isRunning: " + String.valueOf(isRunning));            }            closeSocket();            Log.d(TAG, "isRunning: " + String.valueOf(isRunning));        }catch (SocketException e) {                e.printStackTrace();            } catch (InterruptedIOException e) {            closeSocket();            Log.d(TAG, "Thread is interrupted, socket closed");        } catch (IOException e) {                Log.e(TAG, e.getMessage());            }    }    /**     * This method is used to sort data received from server to sort away all of the "0".     * @param receiveData     * @param l     * @return     */    private String putChar(byte[] receiveData, int l) {        String tmp = "";        for (int i = 0; i < l; i++) {            if (receiveData[i] != 0) {                tmp += (char) receiveData[i];            }        }        return tmp;    }    /**     * This method is used to update main thread.     * @param runnable     */    private void runOnUiThread(Runnable runnable) {        handler.post(runnable);    }    /**     * This method is used to close a socket that is used by the thread.     */    public void closeSocket(){        mySocket.close();    }    /**     * This method sets variable isRunning true or false.     * Setting it false stops while loop inside run method.     * So we can close socket and interrupt thread more safely.     * @param isRunning     */    public void setIsRunning(boolean isRunning){        this.isRunning=isRunning;    }}