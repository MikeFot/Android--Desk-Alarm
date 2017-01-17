package com.michaelfotiadis.deskalarm.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.WindowManager;

import com.michaelfotiadis.deskalarm.R;
import com.michaelfotiadis.deskalarm.ui.base.dialog.AlertDialogFactory;
import com.michaelfotiadis.deskalarm.ui.base.dialog.BaseDialogFragment;

/**
 * Class extending DialogFragment. Stores Alarm Time to SharedPreferences.
 *
 * @author Michael Fotiadis
 */
public class ClearPreferencesDialogFragment extends BaseDialogFragment {


    public static BaseDialogFragment newInstance() {
        return new ClearPreferencesDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {


        /** Turn Screen On and Unlock the keypad when this alert dialog is displayed */
        getActivity().getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        return new AlertDialogFactory(getActivity()).create(
                R.string.dialog_delete_preferences,
                R.string.dialog_delete_preferences_body,
                R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        getPreferenceHandler().clearPreferences();
                        getActivity().finish();
                    }
                },
                R.string.cancel, null);
    }


}