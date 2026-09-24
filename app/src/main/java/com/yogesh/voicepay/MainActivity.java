package com.yogesh.voicepay;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends Activity {

    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(48, 48, 48, 48);

        TextView info = new TextView(this);
        info.setTextSize(18);
        info.setText("VoicePay\n\n"
                + "1. Notification access ON kar\n"
                + "2. Battery: Unrestricted kar\n"
                + "3. Test dabake awaaz check kar\n");

        Button access = new Button(this);
        access.setText("Notification access kholo");
        access.setOnClickListener(v -> startActivity(
                new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)));

        Button battery = new Button(this);
        battery.setText("App info / Battery setting");
        battery.setOnClickListener(v -> startActivity(
                new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + getPackageName()))));

        Button test = new Button(this);
        test.setText("Test voice");
        test.setOnClickListener(v -> {
            if (tts != null) {
                tts.speak("फोनपे पर 10 रुपये प्राप्त हुए",
                        TextToSpeech.QUEUE_FLUSH, null, "test");
            }
        });

        root.addView(info);
        root.addView(access);
        root.addView(battery);
        root.addView(test);
        setContentView(root);

        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(new Locale("hi", "IN"));
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
