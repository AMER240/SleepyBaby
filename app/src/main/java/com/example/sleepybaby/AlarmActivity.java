package com.example.sleepybaby;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AlarmActivity extends AppCompatActivity {

    private TextView tvChildName, tvAlarmType;
    private Button btnStopAlarm;
    private MediaPlayer mediaPlayer;
    private String childName;
    private String alarmType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // لإظهار Activity فوق شاشة القفل وجعلها تظهر حتى لو كان التطبيق مغلقًا
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_alarm);

        tvChildName = findViewById(R.id.tvChildName);
        tvAlarmType = findViewById(R.id.tvAlarmType);
        btnStopAlarm = findViewById(R.id.btnStopAlarm);

        // استخراج البيانات من Intent (التي سيرسلها NotificationReceiver)
        childName = getIntent().getStringExtra("childName");
        alarmType = getIntent().getStringExtra("notificationType"); // "sleep_time" or "wake_time"

        tvChildName.setText("اسم الطفل: " + childName);
        if ("sleep_time".equals(alarmType)) {
            tvAlarmType.setText("النوع: وقت النوم");
        } else if ("wake_time".equals(alarmType)) {
            tvAlarmType.setText("النوع: وقت الاستيقاظ");
        }

        // تشغيل صوت المنبه
        // تحتاج إلى إضافة ملف صوتي (مثل mp3) إلى مجلد res/raw/
        // على سبيل المثال، قم بإنشاء مجلد 'raw' داخل 'res' وضع فيه ملف 'alarm_sound.mp3'
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound);
        mediaPlayer.setLooping(true); // لجعل الصوت يتكرر
        mediaPlayer.start();

        btnStopAlarm.setOnClickListener(v -> stopAlarm());
    }

    private void stopAlarm() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        finish(); // إغلاق النشاط
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // التأكد من تحرير MediaPlayer عند تدمير النشاط
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}