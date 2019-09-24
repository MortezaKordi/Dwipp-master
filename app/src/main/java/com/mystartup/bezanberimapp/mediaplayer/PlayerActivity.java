package com.mystartup.bezanberimapp.mediaplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import static com.mystartup.bezanberimapp.FitnessActivity.mLastClickTime;
import static com.mystartup.bezanberimapp.mediaplayer.MediaPlayerMain.songs;
import static com.mystartup.bezanberimapp.mediaplayer.MediaService.mediaPlayer;
import static com.mystartup.bezanberimapp.mediaplayer.MediaService.position;

import com.mystartup.bezanberimapp.R;

public class PlayerActivity extends AppCompatActivity {

    private String fromIntent = "other";
    ImageView btnPause, btnNext, btnPrevious;
    TextView songTextLabel;
    static SeekBar seekBar;
    static String songName;
    Runnable runnable;
    Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        btnPause = findViewById(R.id.btnPlay);
        btnNext = findViewById(R.id.btnNext);
        btnPrevious = findViewById(R.id.btnPrevious);

        songTextLabel = findViewById(R.id.song_label);
        songTextLabel.setSelected(true);

        seekBar = findViewById(R.id.seekBar);
        seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.progress_background), PorterDuff.Mode.MULTIPLY);
        seekBar.getThumb().setColorFilter(getResources().getColor(R.color.exercise_colour), PorterDuff.Mode.SRC_IN);

        handler = new Handler();
//        playCycle();


        if (null != getIntent().getExtras().get("from_notif")) {
            position = getIntent().getExtras().getInt("position_from_notif");
            fromIntent = "notification";
            if (mediaPlayer.isPlaying()) {
                btnPause.setBackgroundResource(R.drawable.pause);

            } else {
                btnPause.setBackgroundResource(R.drawable.play_arrow);
            }
            seekBar.setMax(mediaPlayer.getDuration());
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            songTextLabel.setText(getIntent().getExtras().get("from_notif").toString());

        }

        if (null != getIntent().getExtras().get("positionX")) {
            songTextLabel.setText(getIntent().getExtras().get("name").toString());
            position = getIntent().getExtras().getInt("positionX");
            if (null != mediaPlayer) {
                seekBar.setMax(mediaPlayer.getDuration());
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                if (mediaPlayer.isPlaying()) {
                    btnPause.setBackgroundResource(R.drawable.pause);
                } else {
                    btnPause.setBackgroundResource(R.drawable.play_arrow);
                }
            } else {
                btnPause.setBackgroundResource(R.drawable.play_arrow);
            }

        }
        if (null != getIntent().getExtras().get("fromList")) {

            position = getIntent().getExtras().getInt("position");
            songTextLabel.setText(songs.get(position).getName().replace(".mp3", "").replace(".wav", ""));
            try {
                seekBar.setMax(mediaPlayer.getDuration());

            } catch (Exception e) {
                btnPause.setBackgroundResource(R.drawable.play_arrow);
            }

            seekBar.setProgress(0);
        }

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null == mediaPlayer) {
                    Uri u = Uri.parse(songs.get(position).toString());
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        getApplicationContext().startForegroundService(new Intent(getApplicationContext(), MediaService.class).putExtra("uri", u).putExtra("pos", position));
                    } else {
                        getApplicationContext().startService(new Intent(getApplicationContext(), MediaService.class).putExtra("uri", u).putExtra("pos", position));
                    }
                    seekBar.setMax(mediaPlayer.getDuration());
                    seekBar.setProgress(0);
                    playCycle();
                    btnPause.setBackgroundResource(R.drawable.pause);
                    return;
                }
                if (mediaPlayer.isPlaying()) {
                    Intent broadIntent = new Intent("song.paused.broadcast.intent");
                    sendBroadcast(broadIntent);
                    mediaPlayer.pause();
                    btnPause.setBackgroundResource(R.drawable.play_arrow);
                } else {
                    Intent broadIntent = new Intent("song.paused.broadcast.intent");
                    sendBroadcast(broadIntent);
                    mediaPlayer.start();
                    btnPause.setBackgroundResource(R.drawable.pause);
                    playCycle();
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                    return;
                }

                mLastClickTime = SystemClock.elapsedRealtime();

                playNextSong();
                btnPause.setBackgroundResource(R.drawable.pause);
            }
        });
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();


                playPrevious();
                btnPause.setBackgroundResource(R.drawable.pause);
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                if (b) {
                    if (null == mediaPlayer) {
                        Uri u = Uri.parse(songs.get(position).toString());
                        mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
                        seekBar.setMax(mediaPlayer.getDuration());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            getApplicationContext().startForegroundService(new Intent(getApplicationContext(), MediaService.class).putExtra("uri", u).putExtra("pos", position));
                        } else {
                            getApplicationContext().startService(new Intent(getApplicationContext(), MediaService.class).putExtra("uri", u).putExtra("pos", position));
                        }
                        btnPause.setBackgroundResource(R.drawable.pause);
                        mediaPlayer.seekTo(i);
                        playCycle();
                    }

                    mediaPlayer.seekTo(i);
                }


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                mediaPlayer.seekTo(seekBar.getProgress());

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();


        IntentFilter intentFilter = new IntentFilter("song.ended.broadcast.intent");
        registerReceiver(broadcastReceiver, intentFilter);
        playCycle();
        IntentFilter notifFilter = new IntentFilter("song.paused.from.notif.broadcast.intent");
        registerReceiver(playPauseReceiver, notifFilter);

        songTextLabel.setText(songs.get(position).getName().replace(".mp3", "").replace(".wav", ""));

        if (null != mediaPlayer) {
            if (mediaPlayer.isPlaying()) {
                btnPause.setBackgroundResource(R.drawable.pause);
            } else {
                btnPause.setBackgroundResource(R.drawable.play_arrow);
            }
        } else {
            btnPause.setBackgroundResource(R.drawable.play_arrow);
        }

    }


    public void playNextSong() {


        mediaPlayer.stop();
        position = ((position + 1) % songs.size());

        Uri u = Uri.parse(songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), u);

        songTextLabel.setText(songs.get(position).getName().replace(".mp3", "").replace(".wav", ""));
        songName = (songs.get(position).getName().replace(".mp3", "").replace(".wav", ""));


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getApplicationContext().startForegroundService(new Intent(getApplicationContext(), MediaService.class).putExtra("uri", u).putExtra("pos", position));
        } else {
            getApplicationContext().startService(new Intent(getApplicationContext(), MediaService.class).putExtra("uri", u).putExtra("pos", position));
        }

        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setProgress(0);


    }

    public void playPrevious() {

        mediaPlayer.stop();
        position = ((position - 1) < 0) ? (songs.size() - 1) : (position - 1);
        Uri u = Uri.parse(songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
        songTextLabel.setText(songs.get(position).getName().replace(".mp3", "").replace(".wav", ""));
        songName = (songs.get(position).getName().replace(".mp3", "").replace(".wav", ""));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getApplicationContext().startForegroundService(new Intent(getApplicationContext(), MediaService.class).putExtra("uri", u).putExtra("pos", position));
        } else {
            getApplicationContext().startService(new Intent(getApplicationContext(), MediaService.class).putExtra("uri", u).putExtra("pos", position));
        }

        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setProgress(0);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if ("notification".equals(fromIntent)) {
            if (!mediaPlayer.isPlaying()) {
                stopService(new Intent(this, MediaService.class));
            }
        }

    }

    @Override
    public void finish() {
        Intent x = new Intent();
        x.putExtra("current", songs.get(position).getName().replace(".mp3", "").replace(".wav", ""));
        x.putExtra("pos", position);
        setResult(RESULT_OK, x);
        super.finish();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            songTextLabel.setText(intent.getStringExtra("name_of_current"));
            position = intent.getExtras().getInt("pos");
            if (!mediaPlayer.isPlaying()) {
                btnPause.setBackgroundResource(R.drawable.play_arrow);
            } else {
                btnPause.setBackgroundResource(R.drawable.pause);
            }

        }
    };
    private BroadcastReceiver playPauseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!mediaPlayer.isPlaying()) {
                btnPause.setBackgroundResource(R.drawable.play_arrow);
            } else {
                btnPause.setBackgroundResource(R.drawable.pause);
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        unregisterReceiver(playPauseReceiver);
        handler.removeCallbacks(runnable);
    }

    public void playCycle() {

        try {
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            if (null != mediaPlayer) {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        playCycle();
                    }
                };
                handler.postDelayed(runnable, 500);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}