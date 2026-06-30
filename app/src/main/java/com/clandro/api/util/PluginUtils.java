package com.clandro.api.util;

import android.app.PendingIntent;
import android.content.Context;

import com.clandro.shared.clandro.settings.preferences.ClandroPreferenceConstants.CLANDRO_API_APP;
import com.clandro.shared.clandro.settings.preferences.ClandroAPIAppSharedPreferences;

public class PluginUtils {

    /**
     * Try to get the next unique {@link PendingIntent} request code that isn't already being used by
     * the app and which would create a unique {@link PendingIntent} that doesn't conflict with that
     * of any other execution commands.
     *
     * @param context The {@link Context} for operations.
     * @return Returns the request code that should be safe to use.
     */
    public synchronized static int getLastPendingIntentRequestCode(final Context context) {
        if (context == null) return CLANDRO_API_APP.DEFAULT_VALUE_KEY_LAST_PENDING_INTENT_REQUEST_CODE;

        ClandroAPIAppSharedPreferences preferences = ClandroAPIAppSharedPreferences.build(context);
        if (preferences == null) return CLANDRO_API_APP.DEFAULT_VALUE_KEY_LAST_PENDING_INTENT_REQUEST_CODE;

        int lastPendingIntentRequestCode = preferences.getLastPendingIntentRequestCode();

        int nextPendingIntentRequestCode = lastPendingIntentRequestCode + 1;

        if (nextPendingIntentRequestCode == Integer.MAX_VALUE || nextPendingIntentRequestCode < 0)
            nextPendingIntentRequestCode = CLANDRO_API_APP.DEFAULT_VALUE_KEY_LAST_PENDING_INTENT_REQUEST_CODE;

        preferences.setLastPendingIntentRequestCode(nextPendingIntentRequestCode);
        return nextPendingIntentRequestCode;
    }

}
