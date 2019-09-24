package com.mystartup.bezanberimapp.mediaplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.TelephonyManager;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.mystartup.bezanberimapp.MyApplication;
import com.mystartup.bezanberimapp.R;


import static com.mystartup.bezanberimapp.FitnessActivity.mLastClickTime;
import static com.mystartup.bezanberimapp.mediaplayer.MediaPlayerMain.songs;
import static com.mystartup.bezanberimapp.mediaplayer.PlayerActivity.seekBar;

import java.io.File;
import java.util.ArrayList;

public class MediaService extends Service implements MediaPlayer.OnCompletionListener {


    public static MediaPlayer mediaPlayer;

    static int position = 0;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            createPermanentNotification();
        }
    };

    private BroadcastReceiver phoneReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

            if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
                phoneCallPause();
            }
            if (TelephonyManager.EXTRA_STATE_IDLE.equals(state)) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }

                phoneCallEnded();
            }
            if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)) {
                phoneCallPause();
            }
            if (state == null) {

                phoneCallPause();
            }

        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter("song.paused.broadcast.intent");
        registerReceiver(broadcastReceiver, intentFilter);

        IntentFilter filter = new IntentFilter("android.intent.action.PHONE_STATE");
        registerReceiver(phoneReceiver, filter);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        position = intent.getExtras().getInt("pos");

        if ("call_ended".equals(intent.getExtras().get("call_ended"))) {

            mediaPlayer.start();
            Intent pauseBroadcast = new Intent("song.paused.from.notif.broadcast.intent");
            sendBroadcast(pauseBroadcast);
            createPermanentNotification();

            return START_STICKY;
        }

        if ("pause_call".equals(intent.getExtras().get("pause_call"))) {
            mediaPlayer.pause();
            Intent pauseBroadcast = new Intent("song.paused.from.notif.broadcast.intent");
            sendBroadcast(pauseBroadcast);

            createPermanentNotification();

            return START_STICKY;
        }

        if (("pausing").equals(intent.getExtras().get("pauseAction"))) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.start();
            }
            Intent broadIntent = new Intent("song.paused.from.notif.broadcast.intent");
            sendBroadcast(broadIntent);

            createPermanentNotification();

            return START_STICKY;
        }
        if ("next".equals(intent.getExtras().get("next"))) {
            playNextInBackground();

            return START_STICKY;
        }
        if ("prev".equals(intent.getExtras().get("prev"))) {
            previousSong();

            Intent broadIntent = new Intent("song.ended.broadcast.intent");
            broadIntent.putExtra("name_of_current", songs.get(position).getName().replace(".mp3", "").replace(".wav", ""));
            broadIntent.putExtra("pos", position);
            sendBroadcast(broadIntent);

            return START_STICKY;
        }

        if (null == mediaPlayer) {

            mediaPlayer = MediaPlayer.create(getApplicationContext(), (Uri) intent.getExtras().get("uri"));
            mediaPlayer.start();
        } else {
            try {
                mediaPlayer.stop();
                mediaPlayer = MediaPlayer.create(getApplicationContext(), (Uri) intent.getExtras().get("uri"));
                mediaPlayer.start();

            } catch (Exception e) {

            }


        }

        mediaPlayer.setOnCompletionListener(this);


        createPermanentNotification();

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {


        playNextInBackground();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        unregisterReceiver(phoneReceiver);

    }

    public void playNextInBackground() {

        mediaPlayer.stop();
        position = ((position + 1) % songs.size());
        Uri u = Uri.parse(songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(getApplicationContext(), MediaService.class).putExtra("uri", u).putExtra("pos", position));
        } else {
            startService(new Intent(getApplicationContext(), MediaService.class).putExtra("uri", u).putExtra("pos", position));
        }

        try {
            seekBar.setMax(mediaPlayer.getDuration());
            seekBar.setProgress(0);
        } catch (Exception e) {

        }

        Intent broadIntent = new Intent("song.ended.broadcast.intent");
        broadIntent.putExtra("name_of_current", songs.get(position).getName().replace(".mp3", "").replace(".wav", ""));
        broadIntent.putExtra("pos", position);
        sendBroadcast(broadIntent);
    }

    private void createPermanentNotification() {
        //testing
        Intent notificationIntent = new Intent(this, MediaPlayerMain.class);
        notificationIntent.putExtra("from_notif", songs.get(position).getName().replace(".mp3", "").replace(".wav", ""));
        notificationIntent.putExtra("position_from_notif", position);

        Intent broadIntent = new Intent("song.ended.broadcast.intent");
        broadIntent.putExtra("name_of_current", songs.get(position).getName().replace(".mp3", "").replace(".wav", ""));
        broadIntent.putExtra("pos", position);
        sendBroadcast(broadIntent);


        PendingIntent piPause;
        PendingIntent piNext;
        PendingIntent piPrevious;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            piPause = PendingIntent.getForegroundService(getApplicationContext(), 100, pauseAction(), PendingIntent.FLAG_UPDATE_CURRENT);
            piNext = PendingIntent.getForegroundService(getApplicationContext(), 200, nextAction(), PendingIntent.FLAG_UPDATE_CURRENT);
            piPrevious = PendingIntent.getForegroundService(getApplicationContext(), 300, prevAction(), PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            piPause = PendingIntent.getService(getApplicationContext(), 100, pauseAction(), PendingIntent.FLAG_UPDATE_CURRENT);
            piNext = PendingIntent.getService(getApplicationContext(), 200, nextAction(), PendingIntent.FLAG_UPDATE_CURRENT);
            piPrevious = PendingIntent.getService(getApplicationContext(), 300, prevAction(), PendingIntent.FLAG_UPDATE_CURRENT);
        }
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent deleteIntent = new Intent(this, MediaService.class);
        deleteIntent.putExtra("clear", "clear");
        NotificationCompat.Action action;
        if (mediaPlayer.isPlaying()) {
            action = new NotificationCompat.Action.Builder(R.drawable.pause, "pause", piPause).build();
        } else {
            action = new NotificationCompat.Action.Builder(R.drawable.play_arrow, "play", piPause).build();
        }
        Notification notification = new NotificationCompat.Builder(this, MyApplication.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setDefaults(0)
                .setContentTitle(songs.get(position).getName().replace(".mp3", ""))
                .addAction(R.drawable.previous_song, "previous", piPrevious)
                .addAction(action)
                .addAction(R.drawable.next_song, "next", piNext)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1, 2))
                .setContentIntent(pendingIntent).build();

        startForeground(1, notification);

    }

    private Intent pauseAction() {
        Intent intent = new Intent(getApplicationContext(), MediaService.class);
        intent.putExtra("pauseAction", "pausing");
        intent.putExtra("pos", position);
        return intent;
    }

    private Intent nextAction() {

        Intent intent = new Intent(getApplicationContext(), MediaService.class);
        intent.putExtra("next", "next");
        intent.putExtra("pos", position);
        return intent;
    }

    private Intent prevAction() {
        Intent intent = new Intent(getApplicationContext(), MediaService.class);
        intent.putExtra("prev", "prev");
        intent.putExtra("pos", position);
        return intent;
    }

    private void previousSong() {
        mediaPlayer.stop();
        position = ((position - 1) < 0) ? (songs.size() - 1) : (position - 1);
        Uri u = Uri.parse(songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getApplicationContext().startForegroundService(new Intent(getApplicationContext(), MediaService.class).putExtra("uri", u).putExtra("pos", position));
        } else {
            getApplicationContext().startService(new Intent(getApplicationContext(), MediaService.class).putExtra("uri", u).putExtra("pos", position));
        }

        try {
            seekBar.setMax(mediaPlayer.getDuration());
            seekBar.setProgress(0);
        } catch (Exception e) {

        }
    }

    private void phoneCallPause() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, MediaService.class).putExtra("pause_call", "pause_call").putExtra("pos", position));
        } else {
            startService(new Intent(this, MediaService.class).putExtra("pause_call", "pause_call").putExtra("pos", position));
        }
    }

    private void phoneCallEnded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, MediaService.class).putExtra("call_ended", "call_ended").putExtra("pos", position));
        } else {
            startService(new Intent(this, MediaService.class).putExtra("call_ended", "call_ended").putExtra("pos", position));
        }

    }


}

