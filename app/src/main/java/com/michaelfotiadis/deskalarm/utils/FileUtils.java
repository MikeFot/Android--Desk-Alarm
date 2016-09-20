package com.michaelfotiadis.deskalarm.utils;

import android.content.Context;

import com.michaelfotiadis.deskalarm.constants.AppConstants;
import com.michaelfotiadis.deskalarm.constants.Singleton;
import com.michaelfotiadis.deskalarm.containers.ErgoTimeDataInstance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileUtils {

    private final String TAG = "FileUtils";
    private FileOutputStream fosOut;
    private FileInputStream fosIn;

    public boolean checkForConfigFile(final Context context) {
        boolean didOperationSucceed = false;
        // Check if Config File exists
        String result = readFromSesttingsFile(context);
        if (result == null) {
            Logger.i(TAG, "Error while accessing Config File");
        } else if (result.equals("") || result.length() < 2) {
            Logger.i(TAG, "Config File is empty");
        } else {
            Logger.i(TAG, "Retrieved Contents of Config File: " + result);
            didOperationSucceed = true;
        }
        return didOperationSucceed;
    }


    public String readFromSesttingsFile(final Context context) {
        try {
            Logger.i(TAG, "Opening Config File " + AppConstants.INTERNAL_STORAGE_FILE);

            // Open the file
            fosIn = context.getApplicationContext().openFileInput(AppConstants.INTERNAL_STORAGE_FILE);
            // Create an InputStream
            InputStreamReader inputStreamReader = new InputStreamReader(fosIn);

            BufferedReader bufferedReader = new BufferedReader(
                    inputStreamReader);
            String receiveString = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ((receiveString = bufferedReader.readLine()) != null) {
                stringBuilder.append(receiveString);
                Logger.i(TAG, "File InputStream : " + receiveString);
            }

            bufferedReader.close();
            inputStreamReader.close();
            Logger.i(TAG, "File Contents : " + stringBuilder.toString());
            return stringBuilder.toString();
        } catch (FileNotFoundException e) {
            Logger.i(TAG, "Exception Reading File: " + e);
            return null;
        } catch (IOException e) {
            Logger.i(TAG, "Exception Reading File: ", e);
            return null;
        }
    }

    public boolean deleteSettingsFile(final Context context) {
        return new File(AppConstants.INTERNAL_STORAGE_FILE).delete();
    }

    public boolean writeToSettingsFile(final Context context, String data) {
        try {
            fosOut = context.getApplicationContext().openFileOutput(AppConstants.INTERNAL_STORAGE_FILE,
                    Context.MODE_APPEND);
            fosOut.write(data.getBytes());
            fosOut.close();
            Logger.i(TAG, "Successfully Written Data File");
            return true;
        } catch (FileNotFoundException e) {
            Logger.i(TAG, "Exception Writing File: ", e);
            return false;
        } catch (IOException e) {
            Logger.i(TAG, "Exception Writing File: ", e);
            return false;
        }
    }

    public boolean clearSettingsFile(final Context context) {
        try {
            fosOut = context.getApplicationContext().openFileOutput(AppConstants.INTERNAL_STORAGE_FILE,
                    Context.MODE_PRIVATE);
            fosOut.write("".getBytes());
            fosOut.close();
            Logger.i(TAG, "Successfully Cleared Data File");
            return true;
        } catch (FileNotFoundException e) {
            Logger.i(TAG, "Exception Writing File: ", e);
            return false;
        } catch (IOException e) {
            Logger.i(TAG, "Exception Writing File: ", e);
            return false;
        }
    }

    public void parseFromFileByLine(final Context context) {
        try {
            Logger.i(TAG, "Opening Config File " + AppConstants.INTERNAL_STORAGE_FILE);

            // Open the file
            fosIn = context.getApplicationContext().openFileInput(AppConstants.INTERNAL_STORAGE_FILE);
            // Create an InputStream
            InputStreamReader inputStreamReader = new InputStreamReader(fosIn);

            BufferedReader bufferedReader = new BufferedReader(
                    inputStreamReader);
            String receiveString = "";
            StringBuilder stringBuilder = new StringBuilder();

            // parse each line individually
            while ((receiveString = bufferedReader.readLine()) != null) {
                String[] dataLine = receiveString.split(",");
                Logger.i(TAG, receiveString);
                if (dataLine.length == 2) {
                    long timeLogged = PrimitiveConversions.tryLong(dataLine[0], 0l);

                    // try parsing time logged to integer
                    int timeElapsed = PrimitiveConversions.tryInteger(dataLine[1], 0);

                    if (timeLogged > 0 && timeElapsed > 0) {
                        // create a new ErgoTimeDataInstance
                        ErgoTimeDataInstance dataInstance = new ErgoTimeDataInstance(
                                timeLogged, timeElapsed);
                        // add to memory
                        Singleton.getInstance().addToUsageData(context.getApplicationContext(),
                                dataInstance);
                        Logger.i(TAG, "Parsed: " + dataInstance.toOutputString());
                    }
                }
            }
            bufferedReader.close();
            inputStreamReader.close();
            Logger.i(TAG, "File Contents : " + stringBuilder.toString());
        } catch (FileNotFoundException e) {
            Logger.i(TAG, "Exception Parsing File: " + e);
        } catch (IOException e) {
            Logger.i(TAG, "Exception Parsing File: ", e);
        }
    }

}
