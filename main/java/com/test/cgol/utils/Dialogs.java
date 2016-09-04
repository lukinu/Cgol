package com.test.cgol.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;

import com.test.cgol.BuildConfig;

/*
* Class instantiates all the dialogs in the app.
* Notifies listeners on data set.
* */
public class Dialogs {
    private static final String TAG = "DIALOGS";

    // create Save-dialog.
    public static Dialog createInputSaveConfigNameDialog(Context context, String title,
                                                         String prompt, final ValueSetListener<String> listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final EditText etConfigName = new EditText(context);
        etConfigName.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL);
        builder.setTitle(title)
                .setMessage(prompt)
                .setView(etConfigName)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        notifyListenerSaveConfigValueSet(listener, etConfigName.getText().toString());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        notifyListenerSaveConfigValueSet(listener, null);
                    }
                });
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "createInputSaveConfigNameDialog builds a dialog");
        }
        return builder.create();
    }

    private static <T> void notifyListenerSaveConfigValueSet(ValueSetListener<T> listener, T value) {
        if (listener != null)
            listener.onSaveConfigValueSet(value);
    }

    // create load-dialog listener
    public static Dialog createInputLoadConfigNameDialog(Context context, String title,
                                                         String prompt, final ValueSetListener<String> listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final EditText etConfigName = new EditText(context);
        etConfigName.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL);
        builder.setTitle(title)
                .setMessage(prompt)
                .setView(etConfigName)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        notifyListenerLoadConfigValueSet(listener, etConfigName.getText().toString());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        notifyListenerLoadConfigValueSet(listener, null);
                    }
                });
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "createInputSaveConfigNameDialog builds a dialog");
        }
        return builder.create();
    }

    private static <T> void notifyListenerLoadConfigValueSet(ValueSetListener<T> listener, T value) {
        if (listener != null)
            listener.onLoadConfigValueSet(value);
    }

    public interface ValueSetListener<T> {
        public void onLoadConfigValueSet(T value);

        public void onSaveConfigValueSet(T value);
    }
}