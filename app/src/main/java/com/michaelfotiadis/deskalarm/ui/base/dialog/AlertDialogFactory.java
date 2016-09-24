package com.michaelfotiadis.deskalarm.ui.base.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.widget.ListAdapter;

import com.michaelfotiadis.deskalarm.R;

/**
 *
 */
public class AlertDialogFactory {

    private static final int STYLE = R.style.AppTheme_AppCompatAlertDialogStyle;

    private final Activity mActivity;

    public AlertDialogFactory(final Activity activity) {
        this.mActivity = activity;
    }

    public void show(final Integer titleResId, final Integer messageResId, final Integer positiveTextResId) {
        show(getString(titleResId), getString(messageResId), getString(positiveTextResId));

    }

    public void show(final CharSequence title, final CharSequence message, final CharSequence positiveText) {
        create(title, message, positiveText).show();
    }

    private String getString(final Integer resId) {
        return resId != null ? mActivity.getString(resId) : null;
    }

    public AlertDialog create(final CharSequence title,
                              final CharSequence message,
                              final CharSequence positiveText) {
        return create(title, message, positiveText, null, null, null);
    }


    public AlertDialog create(final CharSequence title,
                              final CharSequence message,
                              final CharSequence positiveText,
                              final DialogInterface.OnClickListener positiveListener) {
        return create(title, message, positiveText, positiveListener, null, null);
    }

    public AlertDialog create(final CharSequence title,
                              final CharSequence message,
                              final CharSequence positiveText,
                              DialogInterface.OnClickListener positiveListener,
                              final CharSequence negativeText,
                              DialogInterface.OnClickListener negativeListener) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(mActivity, STYLE));

        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }

        if (!TextUtils.isEmpty(message)) {
            builder.setMessage(message);
        }

        if (!TextUtils.isEmpty(positiveText)) {
            if (positiveListener == null) {
                positiveListener = new DismissDialogOnClickListener();
            }
            builder.setPositiveButton(positiveText, positiveListener);
        }

        if (!TextUtils.isEmpty(negativeText)) {
            if (negativeListener == null) {
                negativeListener = new DismissDialogOnClickListener();
            }
            builder.setNegativeButton(negativeText, negativeListener);
        }

        return builder.create();
    }

    public AlertDialog create(final Integer titleResId,
                              final Integer messageResId,
                              final Integer positiveTextResId) {
        return create(getString(titleResId), getString(messageResId), getString(positiveTextResId), null, null, null);
    }


    public AlertDialog create(final Integer titleResId,
                              final Integer messageResId,
                              final Integer positiveTextResId,
                              final DialogInterface.OnClickListener positiveListener) {
        return create(getString(titleResId), getString(messageResId), getString(positiveTextResId), positiveListener, null, null);
    }

    public AlertDialog create(final Integer titleResId,
                              final Integer messageResId,
                              final Integer positiveTextResId,
                              final DialogInterface.OnClickListener positiveListener,
                              final Integer negativeTextResId,
                              final DialogInterface.OnClickListener negativeListener) {
        return create(getString(titleResId), getString(messageResId), getString(positiveTextResId), positiveListener, getString(negativeTextResId), negativeListener);
    }

    public void show(final CharSequence title,
                     final CharSequence message,
                     final CharSequence positiveText,
                     final DialogInterface.OnClickListener positiveListener) {
        create(title, message, positiveText, positiveListener, null, null).show();
    }

    public void show(final Integer titleResId,
                     final Integer messageResId,
                     final Integer positiveTextResId,
                     final DialogInterface.OnClickListener positiveListener,
                     final Integer negativeTextResId,
                     final DialogInterface.OnClickListener negativeListener) {
        create(getString(titleResId), getString(messageResId), getString(positiveTextResId), positiveListener, getString(negativeTextResId), negativeListener).show();
    }

    public void show(final CharSequence title,
                     final CharSequence message,
                     final CharSequence positiveText,
                     final DialogInterface.OnClickListener positiveListener,
                     final CharSequence negativeText,
                     final DialogInterface.OnClickListener negativeListener) {
        create(title, message, positiveText, positiveListener, negativeText, negativeListener).show();
    }

    public void showSelector(final CharSequence title,
                             final CharSequence positiveText,
                             final DialogInterface.OnClickListener positiveListener,
                             final ListAdapter adapter,
                             final int checkedItem,
                             final DialogInterface.OnClickListener adapterListener,
                             final DialogInterface.OnCancelListener onCancelListener) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(mActivity, STYLE));

        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        if (adapter != null && adapterListener != null) {
            builder.setSingleChoiceItems(adapter, checkedItem, adapterListener);
        }

        if (!TextUtils.isEmpty(positiveText) && positiveListener != null) {
            builder.setPositiveButton(positiveText, positiveListener);
        }
        if (onCancelListener != null) {
            builder.setOnCancelListener(onCancelListener);
        }
        builder.create().show();
    }

    /**
     * Dummy {@link android.content.DialogInterface.OnClickListener} for dialogs which only have
     * a single button and no action
     */
    private static class DismissDialogOnClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(final DialogInterface dialog, final int which) {
            dialog.dismiss();
        }
    }

}
