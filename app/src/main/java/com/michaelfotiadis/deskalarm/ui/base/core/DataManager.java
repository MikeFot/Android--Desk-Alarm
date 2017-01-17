package com.michaelfotiadis.deskalarm.ui.base.core;

import com.michaelfotiadis.deskalarm.containers.TimeModel;

import java.util.SortedMap;

/**
 *
 */
public interface DataManager {
    @SuppressWarnings("MethodMayBeStatic")
    SortedMap<String, TimeModel> retrieveDailyData();

    @SuppressWarnings("MethodMayBeStatic")
    void clearUserData();
}
