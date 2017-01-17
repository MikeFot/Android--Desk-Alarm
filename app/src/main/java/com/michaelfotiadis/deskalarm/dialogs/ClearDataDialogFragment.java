package com.michaelfotiadis.deskalarm.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.WindowManager.LayoutParams;

import com.michaelfotiadis.deskalarm.R;
import com.michaelfotiadis.deskalarm.ui.base.dialog.AlertDialogFactory;
import com.michaelfotiadis.deskalarm.ui.base.dialog.BaseDialogFragment;

/**
 * Class extending DialogFragment. Stores Alarm Time to SharedPreferences.
 *
 * @author Michael Fotiadis
 */
public class ClearDataDialogFragment extends BaseDialogFragment {

    public static BaseDialogFragment newInstance() {
        return new ClearDataDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {


        /** Turn Screen On and Unlock the keypad when this alert dialog is displayed */
        getActivity().getWindow().addFlags(
                LayoutParams.FLAG_TURN_SCREEN_ON | LayoutParams.FLAG_DISMISS_KEYGUARD);

        setCancelable(false);
        setRetainInstance(true);

        return new AlertDialogFactory(getActivity()).create(
                R.string.dialog_clear_user_data,
                R.string.dialog_clear_user_data_body,
                R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        clearData();
                    }
                },
                R.string.cancel, null);
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }


    private void clearData() {
        getFileHelper().clearSettingsFile();
        getDataManager().clearUserData();
        getPreferenceHandler().clearPreferences();
        getActivity().finish();
    }

}