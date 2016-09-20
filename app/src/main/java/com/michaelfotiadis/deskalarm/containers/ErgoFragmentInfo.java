package com.michaelfotiadis.deskalarm.containers;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * @author Alexandros Schillings
 */
public final class ErgoFragmentInfo {
    private final Class<?> clss;
    private final Bundle args;

    // OR

    private final Fragment frag;

    public ErgoFragmentInfo(Class<?> _class, Bundle _args) {
        clss = _class;
        args = _args;
        frag = null;
    }

    public ErgoFragmentInfo(Fragment _frag) {
        clss = null;
        args = null;
        frag = _frag;
    }

    public Bundle getArgs() {
        return args;
    }

    public Fragment getFrag() {
        return frag;
    }

    public Class<?> getClss() {
        return clss;
    }
}