package com.michaelfotiadis.deskalarm.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager.LayoutParams;

import com.michaelfotiadis.deskalarm.R;
import com.michaelfotiadis.deskalarm.utils.AppUtils;
import com.michaelfotiadis.deskalarm.utils.Logger;

/**
 * Class extending DialogFragment. Stores Alarm Time to SharedPreferences.
 *
 * @author Michael Fotiadis
 */
public class DialogClearPreferencesFragment extends DialogFragment implements
        OnClickListener {

    private final String TAG = "ClearPreferencesDialogFragment";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        /**
         * Turn Screen On and Unlock the keypad when this alert dialog is
         * displayed
         */
        getActivity().getWindow().addFlags(
                LayoutParams.FLAG_TURN_SCREEN_ON
                        | LayoutParams.FLAG_DISMISS_KEYGUARD);

        /** Creating a alert dialog builder */
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Disable dismiss
        builder.setCancelable(false);

        /** Setting title for the alert dialog */
        builder.setTitle(R.string.dialog_delete_preferences);

        /** Setting the content for the alert dialog */
        builder.setMessage(R.string.dialog_delete_preferences_body);

        /** Defining an OK button event listener */
        builder.setPositiveButton(R.string.ok, this);
        builder.setNegativeButton(R.string.cancel, this);

        setRetainInstance(true);

        Logger.d(TAG, "Showing Clear Preferences Fragment");

        /** Creating the alert dialog window */
        return builder.create();
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                SharedPreferences.Editor editor = new AppUtils()
                        .getAppSharedPreferences(
                                getActivity().getApplicationContext()).edit();
                editor.clear();
                editor.commit();
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                dismiss();
                break;
            default:
                break;
        }
    }

}