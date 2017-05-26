package com.example.anna.colorgame;


import android.content.Context;
import android.support.v7.app.AppCompatActivity;

/**
 * This class is used when the game is over to show AlertDialogs to player.
 */
public class GameOver extends AppCompatActivity {
    private String message = null;// message to show in AlertDialog window
    private Player player;
    private Context context = null;
    private String dataMessage = null;// "Give Up", "logout", "LOSE!", "WIN!"

    /**
     * This constructor creates new instance of GameOver class with specified parameters.
     *
     * @param context     Context
     * @param player      Player
     * @param dataMessage String
     */
    public GameOver(Context context, Player player, String dataMessage) {
        this.context = context;
        this.player = new Player(player.getName(), player.getUserID(), player.getChoosenIP());
        this.dataMessage = dataMessage;
        setTextMessageOfAlertDialog(dataMessage);
        displayMessage();
    }

    /**
     * This method is used to choose text that will be shown in AlertDialog.
     *
     * @param message String
     */
    private void setTextMessageOfAlertDialog(String message) {
        switch (message) {
            case "WIN!":
                this.message = "You are the winner!";
                break;
            case "LOSE!":
                this.message = "Epic fail!";
                break;
            case "logout":
                this.message = "Game is interrupted." + "\nYou will be logged out";
                break;
            case "Give Up":
                this.message = "Game is interrupted." + "\nProgress will be lost";
                break;
        }
    }

    /**
     * This method creates an AlertDialog to display when player won or lost the game.
     */
    public void displayMessage() {
        AlertDialogClass alertDialog = new AlertDialogClass(context, dataMessage);
        alertDialog.setTextToShowInAlertDialog(message);
        alertDialog.setPlayer(player);
        alertDialog.ButtonOK();
    }
}
