
        //Here is AlertDialog
        alertDialog = new AlertDialog.Builder(MainFrame.this).create();
        alertDialog.setTitle("Connection fialed");
        alertDialog.setMessage("Connection to game server fialed");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });