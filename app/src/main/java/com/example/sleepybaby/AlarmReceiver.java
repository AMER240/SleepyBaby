package com.example.sleepybaby;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.PowerManager;
import android.os.Vibrator;
import android.os.VibrationEffect;
import android.util.Log;
import android.app.KeyguardManager;
import android.view.WindowManager;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";
    private static MediaPlayer mediaPlayer;
    private static Vibrator vibrator;
    private static PowerManager.WakeLock wakeLock;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Alarm received: " + intent.getAction());
        
        if (intent.getAction() == null || !intent.getAction().equals("com.example.sleepybaby.ALARM_TRIGGER")) {
            Log.e(TAG, "Invalid action received: " + intent.getAction());
            return;
        }

        try {
            // Wake Lock al
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(
                PowerManager.FULL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE,
                "SleepyBaby:AlarmWakeLock"
            );
            wakeLock.acquire(30*60*1000L); // 30 minutes

            // Alarm bilgilerini al
            String childName = intent.getStringExtra("CHILD_NAME");
            long alarmId = intent.getLongExtra("ALARM_ID", -1);
            
            Log.d(TAG, "Processing alarm for child: " + childName + ", alarm ID: " + alarmId);

            // Alarm aktivitesini başlat
            Intent alarmIntent = new Intent(context, ActiveAlarmActivity.class);
            alarmIntent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                Intent.FLAG_ACTIVITY_NO_USER_ACTION
            );
            
            // Ekran kilidini aç
            KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            if (keyguardManager != null && keyguardManager.isKeyguardLocked()) {
                alarmIntent.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            }
            
            alarmIntent.putExtra("CHILD_NAME", childName);
            alarmIntent.putExtra("ALARM_ID", alarmId);
            context.startActivity(alarmIntent);

            // Titreşimi başlat
            startVibration(context);

            // Alarm sesini çal
            playAlarmSound(context);

        } catch (Exception e) {
            Log.e(TAG, "Error processing alarm: " + e.getMessage(), e);
        }
    }

    private void startVibration(Context context) {
        try {
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null && vibrator.hasVibrator()) {
                long[] pattern = {0, 1000, 500, 1000};
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0));
                } else {
                    vibrator.vibrate(pattern, 0);
                }
                Log.d(TAG, "Vibration started");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error starting vibration: " + e.getMessage(), e);
        }
    }

    private void playAlarmSound(Context context) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }

            // Varsayılan alarm sesini kullan
            mediaPlayer = MediaPlayer.create(context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
            
            if (mediaPlayer != null) {
                mediaPlayer.setLooping(true);
                mediaPlayer.setVolume(1.0f, 1.0f);
                mediaPlayer.start();
                Log.d(TAG, "Alarm sound started");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error playing alarm sound: " + e.getMessage(), e);
        }
    }

    public static void stopAlarm() {
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

            if (wakeLock != null && wakeLock.isHeld()) {
                wakeLock.release();
                Log.d(TAG, "Wake lock released");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error stopping alarm: " + e.getMessage(), e);
        }
    }
}
