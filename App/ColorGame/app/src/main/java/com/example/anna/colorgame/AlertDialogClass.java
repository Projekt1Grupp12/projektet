package com.example.anna.colorgame;import android.content.Context;import android.content.DialogInterface;import android.content.Intent;import android.support.v7.app.AlertDialog;import android.util.Log;import static android.support.v7.app.AlertDialog.*;/** * Created by George on 2017-04-28. */public class AlertDialogClass{    private String TAG = "DialogClassDebug";    private AlertDialog builder = null;    private String answer = null;    private Player player = null;    private Context context;    private String dataMessage = null; //contains a message regarding what kind of action should be executed onDismissed dialog    private Class MainFrame = MainFrame.class;    private Class ChooseGame = ChooseGame.class;    private Class MainMenu = MainMenu.class;    private HighScore HighScoreClass = null;    private boolean booleanYesNo = false;    /**    This constuctor creates an AlertDialog with specified context, empty title and empty message.     */    public AlertDialogClass(Context context){        this(context, " ");    }    /**    This constuctor creates an AlertDialog with specified context, empty title and    specified message.     */    public AlertDialogClass(Context context, String dataMessage){        this(context, " ", dataMessage);    }    /**     * Constructor that creates an AlertDialog. Is used in non game over classes.     * @param context     * @param title     * @param dataMessage     */    public AlertDialogClass(Context context, String title, String dataMessage){        this(context, title, " ", dataMessage);    }    /**     This constuctor creates an AlertDialog with specified context, specified title and     specified message.     */    public AlertDialogClass(Context context, String title, String message, String dataMessage){        this(title, context,message);        Log.d(TAG,"AlertDialog Constructor");        this.context = context;        AlertDialog  builder = new AlertDialog.Builder(context).create();        this.dataMessage = dataMessage;        this.builder = builder;        this.setTitleMessage(title, message);        builder.setCanceledOnTouchOutside(false);    }    /**     * Constructor creates an AlertDialog. Is used in game over classen.     * @param title     * @param context     * @param message     */    public AlertDialogClass(String title, Context context, String message){    }    /**     *     * @param thisClass     * @param title     * @param message     * @param dataMessage     */    public AlertDialogClass(HighScore thisClass, String title, String message, String dataMessage){        Log.d(TAG,"AlertDialog Constructor");        this.context = thisClass;        AlertDialog  builder = new AlertDialog.Builder(thisClass).create();        this.dataMessage = dataMessage;        this.builder = builder;        this.setTitleMessage(title, message);        builder.setCanceledOnTouchOutside(false);        this.HighScoreClass = thisClass;    }    public void setTitleOfAlertDialog(String title){        builder.setTitle(title);        Log.d(TAG,"setTitleOfAlertDialog()");    }    public void setTextToShowInAlertDialog(String message){        Log.d(TAG,"setMssage()");        builder.setMessage(message);    }    public void ButtonContinue(){        Log.d(TAG,"ButtonContinue()");        builder.setButton(BUTTON_NEUTRAL, "Continue", onClickListener);        builder.setOnDismissListener(onDismissListener);        builder.show();    }    /**     * Adds an OK button with onClickListener to AlertDialog, adds an onDismissListener and shows     * AlertDialog.     */    public void ButtonOK(){        Log.d(TAG,"ButtonOK()");        builder.setButton(BUTTON_NEUTRAL, "OK", onClickListener);        builder.setOnDismissListener(onDismissListener);        builder.show();    }    /**     * Adds two buttons "YES and NO" with onClickListener to AlertDialog, adds an onDismissListener     * and shows AlertDialog.     */    public void ButtonYesNo(){        booleanYesNo = true;        Log.d(TAG,"ButtonYesNo");        builder.setButton(BUTTON_POSITIVE, "Yes", onClickListener);        builder.setButton(BUTTON_NEGATIVE, "No", onClickListener);        builder.setOnDismissListener(onDismissListener);        builder.show();    }    public OnClickListener onClickListener = new OnClickListener() {        @Override        public void onClick(DialogInterface dialog, int which) {            Log.d(TAG, "OnClickListener");            switch (which) {                case BUTTON_NEUTRAL:                    Log.d(TAG, "OK button pressed");                    setAnswer("OK");                    break;                case BUTTON_POSITIVE:                    Log.d(TAG, "YES button pressed");                    setAnswer("YES");                    break;                case BUTTON_NEGATIVE:                    Log.d(TAG, "NO button pressed");                    setAnswer("NO");                    break;            }            builder.dismiss();        }    };    public OnDismissListener onDismissListener = new OnDismissListener() {        @Override        public void onDismiss(DialogInterface dialog) {            Log.d(TAG, "Dialog dismissed");            if(player == null){                Log.d(TAG, "player == null");            }            Log.d(TAG, "answer: " + String.valueOf(answer));            if(answer == null && booleanYesNo == false){                setAnswer("OK");            } else if(answer == null && booleanYesNo == true){                setAnswer("NO");            }            if(answer.contains("YES")) {                if(player.getUserID().contains("0")) {                    sendMessageToNextActivity(ChooseGame, player);                }else{                    sendMessageToNextActivity(MainMenu, player);                }            }else if(answer.contains("NO")) {                sendMessageToNextActivity(MainFrame, player);                logoutMessageToServer(player);//sends "logout;id" to server using asynctask            } else if(answer.contains("OK")){                Log.d(TAG, "else, answer: " + answer);                if(context.getClass().getName().contains("Game")){                    Log.d(TAG, "if getClass().getName(): " + String.valueOf(context.getClass().getName()));                    Log.d(TAG, dataMessage);                    if(dataMessage.contains("Give") || dataMessage.contains("logout")) {//dataMessage is evaluated                        Log.d(TAG, "else, answer: Give");                        logoutMessageToServer(player);//sends "logout;id" to server using asynctask                        sendMessageToNextActivity(MainFrame, player);                    }else if(dataMessage.contains("WIN!") || dataMessage.contains("LOSE!")){                        /*                         * Prepares the alertDialogClass to ask the player if they want                         * to play again or not.                         */                        AlertDialogClass adc = new AlertDialogClass(context,"New game?", "Do you want to play again?", " ");                        adc.setPlayer(player);                        adc.ButtonYesNo();                    }                } else if(context.getClass().getName().contains("HighScore")){                    HighScoreClass.onBackPressed();                } else if(context.getClass().getName().contains("MainMenu")){                    Log.d(TAG, "else, answer: logout from MainMenu");                    logoutMessageToServer(player);//sends "logout;id" to server using asynctask                    sendMessageToNextActivity(MainFrame, player);                }            }        }    };    /**     * This method sets answer to specified value. Answer is users button choice "YES, NO, OK".     * @param answer     */    private void setAnswer(String answer){        this.answer = answer;    }    /**     * This method sets player variable to specified value. Player variable is an instance of player     * class.     * @param player     */    public void setPlayer(Player player){        this.player = new Player(player.getName(), player.getUserID(), player.getChoosenIP());    }    public void setTitleMessage(String title, String message){        setTitleOfAlertDialog(title);        setTextToShowInAlertDialog(message);    }    /* This method sends data(IP, Name, userID) to next activity using Intent class.  */    /**     * This method creates an intent with flag set, sends data stored is player variable to next     * activity. Starts next activity.     * @param startClass     * @param player     */    public void sendMessageToNextActivity(Class startClass, Player player) {        Log.d(TAG, "Creating new intent and sending data");        Intent intent = new Intent(context, startClass);        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);        intent.putExtra("player", player);        Log.d(TAG, "Starting new Activity");        context.startActivity(intent);    }    private void logoutMessageToServer(Player player){//this method is used to send "logout" message to server using asynctask        String message = "logout" + ";" + player.getUserID();        ConnectToServer connectToServer = new ConnectToServer(player);        Log.d(TAG, "connectToServer is created " + player.getName());        System.gc();        connectToServer.execute(message);    }}