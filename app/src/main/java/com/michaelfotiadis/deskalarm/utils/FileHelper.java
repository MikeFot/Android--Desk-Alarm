package com.michaelfotiadis.deskalarm.utils;

import android.content.Context;

import com.michaelfotiadis.deskalarm.constants.DataStorage;
import com.michaelfotiadis.deskalarm.containers.TimeModel;
import com.michaelfotiadis.deskalarm.utils.log.AppLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileHelper {

    // path of the internal storage file
    private static final String INTERNAL_STORAGE_FILE = "user_data";

    private final Context mContext;

    private FileOutputStream mFosOut;
    private FileInputStream mFosIn;

    public FileHelper(final Context context) {
        this.mContext = context;
    }

    public boolean checkForConfigFile() {
        boolean didOperationSucceed = false;
        // Check if Config File exists
        final String result = readFromSettingsFile();
        if (result == null) {
            AppLog.i("Error while accessing Config File");
        } else if (result.equals("") || result.length() < 2) {
            AppLog.i("Config File is empty");
        } else {
            AppLog.i("Retrieved Contents of Config File: " + result);
            didOperationSucceed = true;
        }
        return didOperationSucceed;
    }

    public boolean deleteSettingsFile() {
        return new File(INTERNAL_STORAGE_FILE).delete();
    }

    public boolean writeToSettingsFile(final String data) {
        try {
            mFosOut = mContext.getApplicationContext().openFileOutput(INTERNAL_STORAGE_FILE,
                    Context.MODE_APPEND);
            mFosOut.write(data.getBytes());

            AppLog.i("Successfully Written Data File");
            return true;
        } catch (final FileNotFoundException e) {
            AppLog.i(String.format("Exception Writing File: %s", e));
            return false;
        } catch (final IOException e) {
            AppLog.i(String.format("Exception Writing File: %s", e));
            return false;
        }
    }

    public boolean clearSettingsFile() {
        try {
            mFosOut = mContext.getApplicationContext().openFileOutput(INTERNAL_STORAGE_FILE,
                    Context.MODE_PRIVATE);
            mFosOut.write("".getBytes());
            AppLog.i("Successfully Cleared Data File");
            return true;
        } catch (final FileNotFoundException e) {
            AppLog.i(String.format("Exception Writing File: %s", e));
            return false;
        } catch (final IOException e) {
            AppLog.i(String.format("Exception Writing File: %s", e));
            return false;
        } finally {
            try {
                mFosOut.close();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void parseFromFileByLine() {
        try {
            AppLog.i("Opening Config File " + INTERNAL_STORAGE_FILE);

            // Open the file
            mFosIn = mContext.getApplicationContext().openFileInput(INTERNAL_STORAGE_FILE);
            // Create an InputStream
            final InputStreamReader inputStreamReader = new InputStreamReader(mFosIn);

            final BufferedReader bufferedReader = new BufferedReader(
                    inputStreamReader);
            String receiveString;
            // parse each line individually
            while ((receiveString = bufferedReader.readLine()) != null) {
                final String[] dataLine = receiveString.split(",");
                AppLog.i(receiveString);
                if (dataLine.length == 2) {
                    final long timeLogged = PrimitiveConversions.tryLong(dataLine[0], 0l);

                    // try parsing time logged to integer
                    final int timeElapsed = PrimitiveConversions.tryInteger(dataLine[1], 0);

                    if (timeLogged > 0 && timeElapsed > 0) {
                        // create a new TimeModel
                        final TimeModel dataInstance = new TimeModel(
                                timeLogged, timeElapsed);
                        // add to memory
                        DataStorage.getInstance().addToUsageData(mContext.getApplicationContext(),
                                dataInstance);
                        AppLog.i("Parsed: " + dataInstance.toOutputString());
                    }
                }
            }
            bufferedReader.close();
            inputStreamReader.close();
        } catch (final IOException e) {
            AppLog.i(String.format("Exception Parsing File: %s", e));
        }
    }

    private String readFromSettingsFile() {
        try {
            AppLog.i(String.format("Opening Config File %s", INTERNAL_STORAGE_FILE));

            // Open the file
            mFosIn = mContext.getApplicationContext().openFileInput(INTERNAL_STORAGE_FILE);
            // Create an InputStream
            final InputStreamReader inputStreamReader = new InputStreamReader(mFosIn);

            final BufferedReader bufferedReader = new BufferedReader(
                    inputStreamReader);
            String receiveString;
            final StringBuilder stringBuilder = new StringBuilder();

            while ((receiveString = bufferedReader.readLine()) != null) {
                stringBuilder.append(receiveString);
                AppLog.i("File InputStream : " + receiveString);
            }

            bufferedReader.close();
            inputStreamReader.close();
            AppLog.i("File Contents : " + stringBuilder.toString());
            return stringBuilder.toString();
        } catch (final FileNotFoundException e) {
            AppLog.i(String.format("Exception Reading File: %s", e));
            return null;
        } catch (final IOException e) {
            AppLog.i(String.format("Exception Reading File: %s", e));
            return null;
        }
    }

}
