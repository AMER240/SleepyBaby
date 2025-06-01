package com.example.sleepybaby;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.os.Vibrator;
import android.os.VibrationEffect;
import android.provider.Settings;
import android.widget.Toast;
import android.app.Activity;
import android.util.Log;
import android.view.WindowManager;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private AlarmNotification alarmNotification;
    private Context context;
    private PowerManager.WakeLock wakeLock;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Alarm received: " + intent.getAction());
        
        if (intent.getAction() == null || !intent.getAction().equals("com.example.sleepybaby.ALARM_TRIGGER")) {
            Log.e(TAG, "Invalid action received: " + intent.getAction());
            return;
        }

        this.context = context;
        
        // Acquire wake lock
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(
            PowerManager.FULL_WAKE_LOCK | 
            PowerManager.ACQUIRE_CAUSES_WAKEUP |
            PowerManager.ON_AFTER_RELEASE,
            "SleepyBaby:AlarmWakeLock"
        );
        wakeLock.acquire(30*60*1000L); // 30 minutes

        try {
            // Get alarm info
            long alarmId = intent.getLongExtra("ALARM_ID", -1);
            String childName = intent.getStringExtra("CHILD_NAME");
            
            Log.d(TAG, "Processing alarm for child: " + childName + ", alarm ID: " + alarmId);

            // Start alarm activity first
            Intent alarmIntent = new Intent(context, ActiveAlarmActivity.class);
            alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                               Intent.FLAG_ACTIVITY_CLEAR_TOP |
                               Intent.FLAG_INCLUDE_STOPPED_PACKAGES |
                               Intent.FLAG_ACTIVITY_NO_USER_ACTION);
            
            alarmIntent.putExtra("CHILD_NAME", childName);
            alarmIntent.putExtra("ALARM_ID", alarmId);
            context.startActivity(alarmIntent);

            // Show notification
            alarmNotification = new AlarmNotification(context);
            alarmNotification.showAlarmNotification(childName);

            // Start vibration
            startVibration();

            // Play alarm sound
            playAlarmSound();

        } catch (Exception e) {
            Log.e(TAG, "Error processing alarm: " + e.getMessage(), e);
        }
    }

    private void startVibration() {
        try {
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null && vibrator.hasVibrator()) {
                long[] pattern = {0, 1000, 500, 1000}; // Vibrate for 1 second, pause for 0.5 second
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0));
                } else {
                    vibrator.vibrate(pattern, 0);
                }
                Log.d(TAG, "Vibration started");
            } else {
                Log.w(TAG, "Vibrator not available");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error starting vibration: " + e.getMessage(), e);
        }
    }

    private void playAlarmSound() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }

            // Try to use custom alarm sound
            Uri alarmSound = null;
            try {
                alarmSound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.alarm_sound1);
                mediaPlayer = MediaPlayer.create(context, alarmSound);
            } catch (Exception e) {
                Log.w(TAG, "Custom alarm sound not found, using default");
            }

            // If custom sound failed, use default alarm sound
            if (mediaPlayer == null) {
                alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                if (alarmSound == null) {
                    alarmSound = Settings.System.DEFAULT_ALARM_ALERT_URI;
                }
                mediaPlayer = MediaPlayer.create(context, alarmSound);
            }

            if (mediaPlayer != null) {
                // Configure audio attributes
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    AudioAttributes attributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build();
                    mediaPlayer.setAudioAttributes(attributes);
                } else {
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                }

                // Set maximum volume
                AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                if (audioManager != null) {
                    int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
                    audioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0);
                }

                mediaPlayer.setLooping(true);
                mediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
                mediaPlayer.start();
                Log.d(TAG, "Alarm sound started");
            } else {
                Log.e(TAG, "Failed to create MediaPlayer");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error playing alarm sound: " + e.getMessage(), e);
        }
    }

    public void stopAlarm() {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
                mediaPlayer = null;
                Log.d(TAG, "Alarm sound stopped");
            }

            if (vibrator != null) {
                vibrator.cancel();
                Log.d(TAG, "Vibration stopped");
            }

            if (alarmNotification != null) {
                alarmNotification.cancelNotification();
                Log.d(TAG, "Notification cancelled");
            }

            if (wakeLock != null && wakeLock.isHeld()) {
                wakeLock.release();
                Log.d(TAG, "Wake lock released");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error stopping alarm: " + e.getMessage(), e);
        }
    }
}
