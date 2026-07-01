package com.clandro.api;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.widget.Toast;

import com.clandro.api.apis.AudioAPI;
import com.clandro.api.apis.BatteryStatusAPI;
import com.clandro.api.apis.BrightnessAPI;
import com.clandro.api.apis.CallLogAPI;
import com.clandro.api.apis.CameraInfoAPI;
import com.clandro.api.apis.CameraPhotoAPI;
import com.clandro.api.apis.ClipboardAPI;
import com.clandro.api.apis.ContactListAPI;
import com.clandro.api.apis.DialogAPI;
import com.clandro.api.apis.DownloadAPI;
import com.clandro.api.apis.FingerprintAPI;
import com.clandro.api.apis.InfraredAPI;
import com.clandro.api.apis.JobSchedulerAPI;
import com.clandro.api.apis.KeystoreAPI;
import com.clandro.api.apis.LocationAPI;
import com.clandro.api.apis.MediaPlayerAPI;
import com.clandro.api.apis.MediaScannerAPI;
import com.clandro.api.apis.MicRecorderAPI;
import com.clandro.api.apis.NfcAPI;
import com.clandro.api.apis.NotificationAPI;
import com.clandro.api.apis.NotificationListAPI;
import com.clandro.api.apis.SAFAPI;
import com.clandro.api.apis.SensorAPI;
import com.clandro.api.apis.ShareAPI;
import com.clandro.api.apis.SmsInboxAPI;
import com.clandro.api.apis.SmsSendAPI;
import com.clandro.api.apis.SpeechToTextAPI;
import com.clandro.api.apis.StorageGetAPI;
import com.clandro.api.apis.TelephonyAPI;
import com.clandro.api.apis.TextToSpeechAPI;
import com.clandro.api.apis.ToastAPI;
import com.clandro.api.apis.TorchAPI;
import com.clandro.api.apis.UsbAPI;
import com.clandro.api.apis.VibrateAPI;
import com.clandro.api.apis.VolumeAPI;
import com.clandro.api.apis.WallpaperAPI;
import com.clandro.api.apis.WifiAPI;
import com.clandro.api.activities.ClandroApiPermissionActivity;
import com.clandro.api.util.ResultReturner;
import com.clandro.shared.data.IntentUtils;
import com.clandro.shared.logger.Logger;
import com.clandro.shared.clandro.ClandroConstants;
import com.clandro.shared.clandro.plugins.ClandroPluginUtils;

public class ClandroApiReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = "ClandroApiReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        ClandroAPIApplication.setLogConfig(context, false);
        Logger.logDebug(LOG_TAG, "Intent Received:\n" + IntentUtils.getIntentString(intent));

        try {
            doWork(context, intent);
        } catch (Throwable t) {
            String message = "Error in " + LOG_TAG;
            // Make sure never to throw exception from BroadCastReceiver to avoid "process is bad"
            // behaviour from the Android system.
            Logger.logStackTraceWithMessage(LOG_TAG, message, t);

            ClandroPluginUtils.sendPluginCommandErrorNotification(context, LOG_TAG,
                    ClandroConstants.CLANDRO_API_APP_NAME + " Error", message, t);

            ResultReturner.noteDone(this, intent);
        }
    }

    private void doWork(Context context, Intent intent) {
        String apiMethod = intent.getStringExtra("api_method");
        if (apiMethod == null) {
            Logger.logError(LOG_TAG, "Missing 'api_method' extra");
            return;
        }

        switch (apiMethod) {
            case "AudioInfo":
                AudioAPI.onReceive(this, context, intent);
                break;
            case "BatteryStatus":
                BatteryStatusAPI.onReceive(this, context, intent);
                break;
            case "Brightness":
                if (!Settings.System.canWrite(context)) {
                    ClandroApiPermissionActivity.checkAndRequestPermissions(context, intent, Manifest.permission.WRITE_SETTINGS);
                    Toast.makeText(context, "Please enable permission for Clandro:API", Toast.LENGTH_LONG).show();

                    // user must enable WRITE_SETTINGS permission this special way
                    Intent settingsIntent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    context.startActivity(settingsIntent);
                    return;
                }
                BrightnessAPI.onReceive(this, context, intent);
                break;
            case "CameraInfo":
                CameraInfoAPI.onReceive(this, context, intent);
                break;
            case "CameraPhoto":
                if (ClandroApiPermissionActivity.checkAndRequestPermissions(context, intent, Manifest.permission.CAMERA)) {
                    CameraPhotoAPI.onReceive(this, context, intent);
                }
                break;
            case "CallLog":
                if (ClandroApiPermissionActivity.checkAndRequestPermissions(context, intent, Manifest.permission.READ_CALL_LOG)) {
                    CallLogAPI.onReceive(context, intent);
                }
                break;
            case "Clipboard":
                ClipboardAPI.onReceive(this, context, intent);
                break;
            case "ContactList":
                if (ClandroApiPermissionActivity.checkAndRequestPermissions(context, intent, Manifest.permission.READ_CONTACTS)) {
                    ContactListAPI.onReceive(this, context, intent);
                }
                break;
            case "Dialog":
                DialogAPI.onReceive(context, intent);
                break;
            case "Download":
                DownloadAPI.onReceive(this, context, intent);
                break;
            case "Fingerprint":
                FingerprintAPI.onReceive(context, intent);
                break;
            case "InfraredFrequencies":
                if (ClandroApiPermissionActivity.checkAndRequestPermissions(context, intent, Manifest.permission.TRANSMIT_IR)) {
                    InfraredAPI.onReceiveCarrierFrequency(this, context, intent);
                }
                break;
            case "InfraredTransmit":
                if (ClandroApiPermissionActivity.checkAndRequestPermissions(context, intent, Manifest.permission.TRANSMIT_IR)) {
                    InfraredAPI.onReceiveTransmit(this, context, intent);
                }
                break;
            case "JobScheduler":
                JobSchedulerAPI.onReceive(this, context, intent);
                break;
            case "Keystore":
                KeystoreAPI.onReceive(this, intent);
                break;
            case "Location":
                if (ClandroApiPermissionActivity.checkAndRequestPermissions(context, intent, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    LocationAPI.onReceive(this, context, intent);
                }
                break;
            case "MediaPlayer":
                MediaPlayerAPI.onReceive(context, intent);
                break;
            case "MediaScanner":
                MediaScannerAPI.onReceive(this, context, intent);
                break;
            case "MicRecorder":
                if (ClandroApiPermissionActivity.checkAndRequestPermissions(context, intent, Manifest.permission.RECORD_AUDIO)) {
                    MicRecorderAPI.onReceive(context, intent);
                }
                break;
            case "Nfc":
                NfcAPI.onReceive(context, intent);
                break;
            case "NotificationList":
                ComponentName cn = new ComponentName(context, NotificationListAPI.NotificationService.class);
                String flat = Settings.Secure.getString(context.getContentResolver(), "enabled_notification_listeners");
                final boolean NotificationServiceEnabled = flat != null && flat.contains(cn.flattenToString());
                if (!NotificationServiceEnabled) {
                    Toast.makeText(context,"Please give Clandro:API Notification Access", Toast.LENGTH_LONG).show();
                    context.startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                } else {
                    NotificationListAPI.onReceive(this, context, intent);
                }
                break;
            case "Notification":
                NotificationAPI.onReceiveShowNotification(this, context, intent);
                break;
            case "NotificationChannel":
                NotificationAPI.onReceiveChannel(this, context, intent);
                break;
            case "NotificationRemove":
                NotificationAPI.onReceiveRemoveNotification(this, context, intent);
                break;
            case "NotificationReply":
                NotificationAPI.onReceiveReplyToNotification(this, context, intent);
                break;
            case "SAF":
                SAFAPI.onReceive(this, context, intent);
                break;
            case "Sensor":
                SensorAPI.onReceive(context, intent);
                break;
            case "Share":
                ShareAPI.onReceive(this, context, intent);
                break;
            case "SmsInbox":
                if (ClandroApiPermissionActivity.checkAndRequestPermissions(context, intent, Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS)) {
                    SmsInboxAPI.onReceive(this, context, intent);
                }
                break;
            case "SmsSend":
                if (ClandroApiPermissionActivity.checkAndRequestPermissions(context, intent, Manifest.permission.READ_PHONE_STATE, Manifest.permission.SEND_SMS)) {
                    SmsSendAPI.onReceive(this, context, intent);
                }
                break;
            case "StorageGet":
                StorageGetAPI.onReceive(this, context, intent);
                break;
            case "SpeechToText":
                if (ClandroApiPermissionActivity.checkAndRequestPermissions(context, intent, Manifest.permission.RECORD_AUDIO)) {
                    SpeechToTextAPI.onReceive(context, intent);
                }
                break;
            case "TelephonyCall":
                if (ClandroApiPermissionActivity.checkAndRequestPermissions(context, intent, Manifest.permission.CALL_PHONE)) {
                    TelephonyAPI.onReceiveTelephonyCall(this, context, intent);
                }
                break;
            case "TelephonyCellInfo":
                if (ClandroApiPermissionActivity.checkAndRequestPermissions(context, intent, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    TelephonyAPI.onReceiveTelephonyCellInfo(this, context, intent);
                }
                break;
            case "TelephonyDeviceInfo":
                if (ClandroApiPermissionActivity.checkAndRequestPermissions(context, intent, Manifest.permission.READ_PHONE_STATE)) {
                    TelephonyAPI.onReceiveTelephonyDeviceInfo(this, context, intent);
                }
                break;
            case "TextToSpeech":
                TextToSpeechAPI.onReceive(context, intent);
                break;
            case "Toast":
                ToastAPI.onReceive(context, intent);
                break;
            case "Torch":
                TorchAPI.onReceive(this, context, intent);
                break;
            case "Usb":
                UsbAPI.onReceive(context, intent);
                break;
            case "Vibrate":
                VibrateAPI.onReceive(this, context, intent);
                break;
            case "Volume":
                VolumeAPI.onReceive(this, context, intent);
                break;
            case "Wallpaper":
                WallpaperAPI.onReceive(context, intent);
                break;
            case "WifiConnectionInfo":
                WifiAPI.onReceiveWifiConnectionInfo(this, context, intent);
                break;
            case "WifiScanInfo":
                if (ClandroApiPermissionActivity.checkAndRequestPermissions(context, intent, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    WifiAPI.onReceiveWifiScanInfo(this, context, intent);
                }
                break;
            case "WifiEnable":
                WifiAPI.onReceiveWifiEnable(this, context, intent);
                break;
            default:
                Logger.logError(LOG_TAG, "Unrecognized 'api_method' extra: '" + apiMethod + "'");
        }
    }

}
