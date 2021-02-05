package com.example.chatlistassignment.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import com.example.chatlistassignment.fragments.ChatListFragment;

public class AlertDialogHelper {
    Context context;
    AlertDialog alertDialog = null;
    AlertDialogListener callBack;
    Activity current_activity;
    ChatListFragment chatListFragment;
    public AlertDialogHelper(Context context, ChatListFragment chatListFragment) {
        this.context = context;
        this.current_activity = (Activity) context;
        this.chatListFragment=chatListFragment;
        callBack = (AlertDialogListener) chatListFragment;
    }

    /**
     * Displays the AlertDialog with 3 Action buttons
     * <p>
     * you can set cancelable property
     *
     * @param title
     * @param message
     * @param positive
     * @param negative
     * @param neutral
     * @param from
     * @param isCancelable
     */
    public void showAlertDialog(String title, String message, String positive, String negative, String neutral, final int from, boolean isCancelable) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(chatListFragment.getContext());

        if (!TextUtils.isEmpty(title))
            alertDialogBuilder.setTitle(title);
        if (!TextUtils.isEmpty(message))
            alertDialogBuilder.setMessage(message);

        if (!TextUtils.isEmpty(positive)) {
            alertDialogBuilder.setPositiveButton(positive,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                            callBack.onPositiveClick(from);
                            alertDialog.dismiss();
                        }
                    });
        }
        if (!TextUtils.isEmpty(neutral)) {
            alertDialogBuilder.setNeutralButton(neutral,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            callBack.onNeutralClick(from);
                            alertDialog.dismiss();
                        }
                    });
        }
        if (!TextUtils.isEmpty(negative)) {
            alertDialogBuilder.setNegativeButton(negative,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            callBack.onNegativeClick(from);
                            alertDialog.dismiss();
                        }
                    });
        } else {
            try {
                new Thread() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        alertDialog = alertDialogBuilder.create();
                        alertDialog.show();

                        Button negative_button = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                        negative_button.setVisibility(View.GONE);

                        Looper.loop();

                    }
                }.start();
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }

        alertDialogBuilder.setCancelable(isCancelable);


        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * Displays the AlertDialog with positive action button only
     * <p>
     * you can set cancelable property
     *
     * @param title
     * @param message
     * @param positive
     * @param from
     * @param isCancelable
     */
    public void showAlertDialog(String title, String message, String positive, final int from, boolean isCancelable) {
        showAlertDialog(title, message, positive, "", "", from, isCancelable);
    }

    /**
     * Displays the AlertDialog with positive action button only
     * <p>
     * cancelable property is false (Default)
     *
     * @param title
     * @param message
     * @param positive
     * @param from
     */
    public void showAlertDialog(String title, String message, String positive, final int from) {
        showAlertDialog(title, message, positive, "", "", from, false);
    }


    /**
     * Displays the AlertDialog with positive & negative buttons
     * <p>
     * you can set cancelable property
     *
     * @param title
     * @param message
     * @param positive
     * @param negative
     * @param from
     * @param isCancelable
     */

    public void showAlertDialog(String title, String message, String positive, String negative, final int from, boolean isCancelable) {
        showAlertDialog(title, message, positive, negative, "", from, isCancelable);
    }

    /**
     * Displays the AlertDialog with positive & negative buttons
     * <p>
     * cancelable property is false (Default)
     *
     * @param title
     * @param message
     * @param positive
     * @param negative
     * @param from
     */
    public void showAlertDialog(String title, String message, String positive, String negative, final int from) {
        showAlertDialog(title, message, positive, negative, "", from, false);
    }

    /**
     * Displays the AlertDialog with 3 Action buttons
     * <p>
     * cancelable property is false (Default)
     *
     * @param title
     * @param message
     * @param positive
     * @param negative
     * @param neutral
     * @param from
     */
    public void showAlertDialog(String title, String message, String positive, String negative, String neutral, final int from) {
        showAlertDialog(title, message, positive, negative, neutral, from, false);
    }


    public interface AlertDialogListener {
        public void onPositiveClick(int from);

        public void onNegativeClick(int from);

        public void onNeutralClick(int from);
    }
}

