package com.yogesh.voicepay;

import android.app.Notification;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.speech.tts.TextToSpeech;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PayListener extends NotificationListenerService
        implements TextToSpeech.OnInitListener {

    private TextToSpeech tts;
    private boolean ready = false;
    private String lastKey = "";

    private static final Pattern AMOUNT = Pattern.compile(
            "(?:\u20B9|Rs\\.?|INR)\\s*([0-9][0-9,]*(?:\\.[0-9]{1,2})?)",
            Pattern.CASE_INSENSITIVE);

    @Override
    public void onCreate() {
        super.onCreate();
        tts = new TextToSpeech(this, this);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(new Locale("hi", "IN"));
            ready = true;
        }
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (!ready) return;
        if (!sbn.getPackageName().startsWith("com.phonepe")) return;

        Notification n = sbn.getNotification();
        if ((n.flags & Notification.FLAG_GROUP_SUMMARY) != 0) return;

        CharSequence title = n.extras.getCharSequence(Notification.EXTRA_TITLE);
        CharSequence body = n.extras.getCharSequence(Notification.EXTRA_TEXT);
        String text = ((title == null ? "" : title) + " "
                + (body == null ? "" : body)).trim();
        if (text.isEmpty()) return;

        String key = sbn.getKey() + "|" + text;
        if (key.equals(lastKey)) return;
        lastKey = key;

        Matcher m = AMOUNT.matcher(text);
        if (!m.find()) return;
        String amt = m.group(1).replace(",", "");
        if (amt.endsWith(".00")) amt = amt.substring(0, amt.length() - 3);

        String lower = text.toLowerCase(Locale.ROOT);
        String msg;
        if (lower.contains("received") || lower.contains("credited")
                || lower.contains("paid you")) {
            msg = "फोनपे पर " + amt + " रुपये प्राप्त हुए";
        } else if (lower.contains("paid") || lower.contains("sent")
                || lower.contains("debited")) {
            msg = amt + " रुपये भेजे गए";
        } else {
            return;
        }

        tts.speak(msg, TextToSpeech.QUEUE_ADD, null, "pay" + System.nanoTime());
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
        }
