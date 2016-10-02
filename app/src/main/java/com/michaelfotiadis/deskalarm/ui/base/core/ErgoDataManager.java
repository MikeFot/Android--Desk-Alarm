package com.michaelfotiadis.deskalarm.ui.base.core;

import com.michaelfotiadis.deskalarm.containers.TimeModelInstance;

import java.util.SortedMap;

/**
 *
 */
public interface ErgoDataManager {
    @SuppressWarnings("MethodMayBeStatic")
    SortedMap<String, TimeModelInstance> retrieveDailyData();

    @SuppressWarnings("MethodMayBeStatic")
    void clearUserData();
}
