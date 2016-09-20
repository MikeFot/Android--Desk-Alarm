package com.michaelfotiadis.deskalarm.constants;


/**
 * Class for storing constants used throughout the application
 *
 * @author Michael Fotiadis
 */
public class AppConstants {

    public enum Broadcasts {
        STEP_SERVICE_STOPPED("Service Stopped"),
        ALARM_STOPPED("Alarm Stopped"),
        ALARM_TRIGGERED("Alarm Triggered"),
        IDLE_DETECTED("Idle Detected"),
        DATA_CHANGED("Data Changed"),
        CLOCK_MODE_CHANGED("Clock Normal");

        private String text;

        Broadcasts(String description) {
            text = description;
        }

        public String getString() {
            return text;
        }

    }

    ;

    public enum Payloads {
        PAYLOAD_1("Payload_1"),
        ALARM_PAYLOAD("Payload_5"),
        CLOCK_MODE_PAYLOAD("Payload_6");

        private String text;

        Payloads(String description) {
            text = description;
        }

        public String getString() {
            return text;
        }
    }

    ;

    public enum Results {
        RESULT_1("Result_1"),// unused
        RESULT_2("Result_2"),// unused
        RESULT_3("Result_3");// unused

        private String text;

        Results(String description) {
            text = description;
        }

        public String getString() {
            return text;
        }
    }

    ;

    public enum Requests {
        REQUEST_CODE_1(1);

        private int code;

        Requests(int number) {
            code = number;
        }

        public int getCode() {
            return code;
        }
    }

    ;

    public enum PreferenceKeys {
        KEY_1("Key_1_Time_Started");

        private String text;

        PreferenceKeys(String description) {
            text = description;
        }

        public String getString() {
            return text;
        }
    }

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    // Calendar Fields
    public static final String EXTENDED_DATE_FORMAT_STRING = "yyyy MM dd HH:mm:ss";
    public static final String SIMPLE_DATE_FORMAT_STRING = "HH:mm:ss";

    // Used to convert milliseconds to minutes
    public static final int FACTOR_MSEC_TO_MINUTES = 60000; // TODO correct is 60000

    public static final int ALARM_NOTIFICATION_ID = 517; // random ID

    // path of the internal storage file
    public static final String INTERNAL_STORAGE_FILE = "user_data";
}
