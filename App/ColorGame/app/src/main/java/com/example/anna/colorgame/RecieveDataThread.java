package com.example.anna.colorgame;import android.content.Context;import android.util.Log;import java.io.IOException;import java.net.DatagramPacket;import java.net.DatagramSocket;import java.net.SocketException;import android.os.Handler;/** * This class is a thread that is used in PuzzleGame class(Activity). * After it is started it waits for a specified message from server. * When thread recieve message from the server it shows an AlertDialog to the user. */public class RecieveDataThread implements Runnable {    private static final String TAG = "debugRecieve";    private static final int PORT = 4445;    private final Handler handler;    private Context context;    private Player player;    private DatagramSocket mySocket = null;    private DatagramPacket receivePacket = null;    private byte[] receiveData = null;    private boolean isRunning=true;    /**     * This constructor creates new instance of a class with context as parameter.     * It initiate context and handler instances.     * @param context     * @param player     */    public RecieveDataThread(Context context, Player player) {        this.player = player;        this.context = context;        handler = new Handler(context.getMainLooper());    }    /**     * This method contains code that makes it possible to wait for a message from server and     * show AlertDialog.     * First udp socket is created that is supposed to listen for incoming data on port 4445 in an     * infinite while loop.     */    @Override    public void run() {        Log.d(TAG, "RecieveDataThread, run method");        try {            Log.d(TAG, "Thread. Creating Socket");            mySocket = new DatagramSocket(PORT);            mySocket.setReuseAddress(true);        } catch (SocketException e) {            e.printStackTrace();        }        while (isRunning) {            try {                Log.d(TAG, "Thread. Initialising byte array and DatagramPacket");                receiveData = new byte[24];                receivePacket = new DatagramPacket(receiveData, receiveData.length);                Log.d(TAG, "Thread. Waiting for message from server");                mySocket.receive(receivePacket);                Log.d(TAG, "Thread. Reading DatagramPacket we got from server");                final String messageFromServer = putChar(receiveData, receiveData.length);                    /*                    Here must be a if-else for different messages in AlertDialog frame                    depending on message from server.                     */                if (messageFromServer.equals("WIN!")) {                    runOnUiThread(new Runnable() {                        @Override                        public void run() {                            Log.d(TAG, "runOnUiThread, run method WIN");                            GameOver gameOver = new GameOver(context, player, "WIN!");                        }                    });                }else if (messageFromServer.equals("LOSE!")) {                    runOnUiThread(new Runnable() {                        @Override                        public void run() {                            Log.d(TAG, "runOnUiThread, run method LOSE");                            GameOver gameOver = new GameOver(context, player, "LOSE!");                        }                    });                }else if(messageFromServer.equals("logoutok")){                    runOnUiThread(new Runnable() {                        @Override                        public void run() {                            Log.d(TAG, "runOnUiThread, run method logoutok");                            GameOver gameOver = new GameOver(context, player, "logoutok");                        }                    });                    /*                    server send start to both users.                    User0 see "waiting for opponent".                    User1 see "starting..."                    when they get message "start" game starts for both of them.                     */                }else if(messageFromServer.equals("start")){                    runOnUiThread(new Runnable() {                        @Override                        public void run() {                            Log.d(TAG, "runOnUiThread, run method start");                            if(context.getClass().getName().contains("MainMenu")){                                MainMenu mainMenu = new MainMenu();                                mainMenu.setStart(true);                            }                            else if(context.getClass().getName().contains("ChooseGame")){                                ChooseGame chooseGame = new ChooseGame();                                chooseGame.setStart(true);                            }                        }                    });                    /*when user0 made a choice of game. we get choice from server                    for example(Puzzle Game)                     */                }else if(messageFromServer.contains("game")){                    runOnUiThread(new Runnable() {                        @Override                        public void run() {                            Log.d(TAG, "runOnUiThread, run method game");                                MainMenu mainMenu = new MainMenu();                                mainMenu.setGame(true);                                mainMenu.setNextActivityGame(messageFromServer);                        }                    });                }            } catch (SocketException e) {                e.printStackTrace();            } catch (IOException e) {                Log.e(TAG, e.getMessage());            }        }    }    /**     * This method is used to sort data received from server to sort away all of the "0".     * @param receiveData     * @param l     * @return     */    private String putChar(byte[] receiveData, int l) {        String tmp = "";        for (int i = 0; i < l; i++) {            if (receiveData[i] != 0) {                tmp += (char) receiveData[i];            }        }        return tmp;    }    /**     * This method is used to update main thread.     * @param runnable     */    private void runOnUiThread(Runnable runnable) {        handler.post(runnable);    }    /**     * This method is used to close a socket that is used by the thread.     */    public void closeSocket(){        mySocket.close();    }    /**     * This method sets variable isRunning true or false.     * Setting it false stops while loop inside run method.     * So we can close socket and interrupt thread more safely.     * @param isRunning     */    public void setIsRunning(boolean isRunning){        this.isRunning=isRunning;    }}