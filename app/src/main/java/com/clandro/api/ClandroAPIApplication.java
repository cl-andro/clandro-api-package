package com.clandro.api;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.clandro.api.util.ResultReturner;
import com.clandro.shared.logger.Logger;
import com.clandro.shared.clandro.ClandroConstants;
import com.clandro.shared.clandro.crash.ClandroCrashUtils;
import com.clandro.shared.clandro.settings.preferences.ClandroAPIAppSharedPreferences;


public class ClandroAPIApplication extends Application {

    public static final String LOG_TAG = "ClandroAPIApplication";

    public void onCreate() {
        super.onCreate();

        Log.i(LOG_TAG, "AppInit");

        Context context = getApplicationContext();

        // Set crash handler for the app
        ClandroCrashUtils.setCrashHandler(context);

        ResultReturner.setContext(this);

        // Set log config for the app
        setLogConfig(context, true);

        SocketListener.createSocketListener(this);
    }

    public static void setLogConfig(Context context, boolean commitToFile) {
        Logger.setDefaultLogTag(ClandroConstants.CLANDRO_API_APP_NAME.replaceAll("[: ]", ""));

        // Load the log level from shared preferences and set it to the {@link Logger.CURRENT_LOG_LEVEL}
        ClandroAPIAppSharedPreferences preferences = ClandroAPIAppSharedPreferences.build(context);
        if (preferences == null) return;
        preferences.setLogLevel(null, preferences.getLogLevel(true), commitToFile);
    }

}
