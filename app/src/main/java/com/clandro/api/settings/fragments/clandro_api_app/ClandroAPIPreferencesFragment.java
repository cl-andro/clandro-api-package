package com.clandro.api.settings.fragments.clandro_api_app;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Keep;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.clandro.api.R;
import com.clandro.shared.clandro.settings.preferences.ClandroAPIAppSharedPreferences;

@Keep
public class ClandroAPIPreferencesFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Context context = getContext();
        if (context == null) return;

        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setPreferenceDataStore(ClandroAPIPreferencesDataStore.getInstance(context));

        setPreferencesFromResource(R.xml.prefs__clandro_api_app___prefs__app, rootKey);
    }

}

class ClandroAPIPreferencesDataStore extends PreferenceDataStore {

    private final Context mContext;
    private final ClandroAPIAppSharedPreferences mPreferences;

    private static ClandroAPIPreferencesDataStore mInstance;

    private ClandroAPIPreferencesDataStore(Context context) {
        mContext = context;
        mPreferences = ClandroAPIAppSharedPreferences.build(context, true);
    }

    public static synchronized ClandroAPIPreferencesDataStore getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ClandroAPIPreferencesDataStore(context);
        }
        return mInstance;
    }

}
