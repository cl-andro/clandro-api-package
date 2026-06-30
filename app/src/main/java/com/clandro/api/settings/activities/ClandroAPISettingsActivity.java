package com.clandro.api.settings.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.clandro.api.R;
import com.clandro.shared.activities.ReportActivity;
import com.clandro.shared.file.FileUtils;
import com.clandro.shared.models.ReportInfo;
import com.clandro.shared.interact.ShareUtils;
import com.clandro.shared.android.PackageUtils;
import com.clandro.shared.clandro.settings.preferences.ClandroAPIAppSharedPreferences;
import com.clandro.shared.android.AndroidUtils;
import com.clandro.shared.clandro.ClandroConstants;
import com.clandro.shared.clandro.ClandroUtils;
import com.clandro.shared.activity.media.AppCompatActivityUtils;
import com.clandro.shared.theme.NightMode;

public class ClandroAPISettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatActivityUtils.setNightMode(this, NightMode.getAppNightMode().getName(), true);

        setContentView(R.layout.activity_clandro_api_settings);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new RootPreferencesFragment())
                .commit();
        }

        AppCompatActivityUtils.setToolbar(this, com.clandro.shared.R.id.toolbar);
        AppCompatActivityUtils.setShowBackButtonInActionBar(this, true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public static class RootPreferencesFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            Context context = getContext();
            if (context == null) return;

            setPreferencesFromResource(R.xml.sets__clandro, rootKey);

            new Thread() {
                @Override
                public void run() {
                    configureClandroAPIPreference(context);
                    configureAboutPreference(context);
                    configureDonatePreference(context);
                }
            }.start();
        }

        private void configureClandroAPIPreference(@NonNull Context context) {
            Preference clandroAPIPreference = findPreference("sets__clandro_api_app");
            if (clandroAPIPreference != null) {
                ClandroAPIAppSharedPreferences preferences = ClandroAPIAppSharedPreferences.build(context, false);
                // If failed to get app preferences, then likely app is not installed, so do not show its preference
                clandroAPIPreference.setVisible(preferences != null);
            }
        }

        private void configureAboutPreference(@NonNull Context context) {
            Preference aboutPreference = findPreference("link__clandro_about");
            if (aboutPreference != null) {
                aboutPreference.setOnPreferenceClickListener(preference -> {
                    new Thread() {
                        @Override
                        public void run() {
                            String title = "About";

                            StringBuilder aboutString = new StringBuilder();
                            aboutString.append(ClandroUtils.getAppInfoMarkdownString(context, ClandroUtils.AppInfoMode.CLANDRO_AND_PLUGIN_PACKAGE));
                            aboutString.append("\n\n").append(AndroidUtils.getDeviceInfoMarkdownString(context, true));
                            aboutString.append("\n\n").append(ClandroUtils.getImportantLinksMarkdownString(context));

                            ReportInfo reportInfo = new ReportInfo(title,
                                    ClandroConstants.CLANDRO_API_APP.CLANDRO_API_MAIN_ACTIVITY_NAME, title);
                            reportInfo.setReportString(aboutString.toString());
                            reportInfo.setReportSaveFileLabelAndPath(title,
                                    Environment.getExternalStorageDirectory() + "/" +
                                            FileUtils.sanitizeFileName(ClandroConstants.CLANDRO_API_APP_NAME.replaceAll(":", "") +
                                                    "-" + title + ".log", true, true));

                            ReportActivity.startReportActivity(context, reportInfo);
                        }
                    }.start();

                    return true;
                });
            }
        }

        private void configureDonatePreference(@NonNull Context context) {
            Preference donatePreference = findPreference("link__clandro_donate");
            if (donatePreference != null) {
                String signingCertificateSHA256Digest = PackageUtils.getSigningCertificateSHA256DigestForPackage(context);
                if (signingCertificateSHA256Digest != null) {
                    // If APK is a Google Playstore release, then do not show the donation link
                    // since Clandro isn't exempted from the playstore policy donation links restriction
                    // Check Fund solicitations: https://pay.google.com/intl/en_in/about/policy/
                    String apkRelease = ClandroUtils.getAPKRelease(signingCertificateSHA256Digest);
                    if (apkRelease == null || apkRelease.equals(ClandroConstants.APK_RELEASE_GOOGLE_PLAYSTORE_SIGNING_CERTIFICATE_SHA256_DIGEST)) {
                        donatePreference.setVisible(false);
                        return;
                    } else {
                        donatePreference.setVisible(true);
                    }
                }

                donatePreference.setOnPreferenceClickListener(preference -> {
                    ShareUtils.openUrl(context, ClandroConstants.CLANDRO_DONATE_URL);
                    return true;
                });
            }
        }
    }

}
