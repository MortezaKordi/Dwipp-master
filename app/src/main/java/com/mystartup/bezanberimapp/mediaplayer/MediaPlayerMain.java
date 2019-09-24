package com.mystartup.bezanberimapp.mediaplayer;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.mystartup.bezanberimapp.R;
import com.mystartup.bezanberimapp.FitnessActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.mystartup.bezanberimapp.FitnessActivity.mLastClickTime;
import static com.mystartup.bezanberimapp.FitnessActivity.pd;
import static com.mystartup.bezanberimapp.mediaplayer.MediaService.mediaPlayer;
import static com.mystartup.bezanberimapp.mediaplayer.MediaService.position;


public class MediaPlayerMain extends AppCompatActivity {

    public static ArrayList<File> songs;
    ListView listView;
    private static final int REQ_CODE_FOR_STATE = 4321;
    TextView txtNameOfThe;
    static Uri u;
    ImageView imgPlay;


    private BroadcastReceiver playPauseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!mediaPlayer.isPlaying()) {
                imgPlay.setBackgroundResource(R.drawable.play_arrow);
            } else {
                imgPlay.setBackgroundResource(R.drawable.pause);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (null == mediaPlayer) {
            txtNameOfThe.setText(songs.get(0).getName().replace(".mp3", "").replace(".wav", ""));
        } else {
            txtNameOfThe.setText(songs.get(position).getName().replace(".mp3", "").replace(".wav", ""));

        }
        if (null != mediaPlayer) {
            if (!mediaPlayer.isPlaying()) {
                imgPlay.setBackgroundResource(R.drawable.play_arrow);
            } else {
                imgPlay.setBackgroundResource(R.drawable.pause);
            }
        }

        IntentFilter intentFilter = new IntentFilter("song.ended.broadcast.intent");
        registerReceiver(broadcastReceiver, intentFilter);

        IntentFilter broadIntent = new IntentFilter("song.paused.from.notif.broadcast.intent");
        registerReceiver(playPauseReceiver, broadIntent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player_main);

        imgPlay = findViewById(R.id.imgPlay);
        listView = findViewById(R.id.songsList);
        txtNameOfThe = findViewById(R.id.txtCurrentSong);
        txtNameOfThe.setSelected(true);
        txtNameOfThe.setFocusable(true);


        fetchTheMusic();
        pd.dismiss();

        askForPhonePerm();

        if (null != getIntent().getExtras()) {

            if (null != getIntent().getExtras().get("from_notif")) {

                txtNameOfThe.setText(songs.get(position).getName().replace(".mp3", "").replace(".wav", ""));
            }
        }

        imgPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (null == mediaPlayer) {
                    u = Uri.parse(songs.get(0).toString());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(new Intent(getApplicationContext(), MediaService.class).putExtra("uri", u).putExtra("pos", position));
                    } else {
                        startService(new Intent(getApplicationContext(), MediaService.class).putExtra("uri", u).putExtra("pos", position));
                    }
                    Intent broadIntent = new Intent("song.paused.broadcast.intent");
                    sendBroadcast(broadIntent);
                    txtNameOfThe.setText(songs.get(position).getName().replace(".mp3", "").replace(".wav", ""));
                    imgPlay.setBackgroundResource(R.drawable.pause);
                } else {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        Intent broadIntent = new Intent("song.paused.broadcast.intent");
                        sendBroadcast(broadIntent);
                        imgPlay.setBackgroundResource(R.drawable.play_arrow);
                    } else {
                        mediaPlayer.start();
                        Intent broadIntent = new Intent("song.paused.broadcast.intent");
                        sendBroadcast(broadIntent);
                        imgPlay.setBackgroundResource(R.drawable.pause);
                    }
                }
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();


                u = Uri.parse(songs.get(i).toString());
                Intent player = new Intent(getApplicationContext(), PlayerActivity.class);
                player.putExtra("uri", u);
                player.putExtra("position", i);
                player.putExtra("fromList", "fromList");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(new Intent(getApplicationContext(), MediaService.class).putExtra("uri", u).putExtra("pos", i));
                } else {
                    startService(new Intent(getApplicationContext(), MediaService.class).putExtra("uri", u).putExtra("pos", i));
                }
                Intent broadIntent = new Intent("song.paused.broadcast.intent");
                sendBroadcast(broadIntent);
                startActivityForResult(player, 0);


            }
        });

        txtNameOfThe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), PlayerActivity.class);
                i.putExtra("positionX", MediaService.position);
                i.putExtra("name", songs.get(MediaService.position).getName().replace(".mp3", "").replace(".wav", ""));

                startActivityForResult(i, 0);
            }
        });

    }


    private void fetchTheMusic() {

        GetAllSongsTask task = new GetAllSongsTask();
        try {
            songs = task.execute(Environment.getExternalStorageDirectory()).get();
            if (songs.isEmpty()) {
                Toast.makeText(this, "You don't have any songs!", Toast.LENGTH_LONG).show();
                finish();
            }
            u = Uri.parse(songs.get(0).toString());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException ie) {
            Toast.makeText(this, "No songs are found", Toast.LENGTH_SHORT).show();
            finish();
        }

    }


    class CustomAdapter extends BaseAdapter {

        ArrayList<File> songs;
        Context context;

        CustomAdapter(Context c, ArrayList<File> songs) {
            this.context = c;
            this.songs = songs;
        }

        @Override
        public int getCount() {

            try {
                return songs.size();
            } catch (Exception e) {
                return 0;
            }
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService((Context.LAYOUT_INFLATER_SERVICE));
            View row = layoutInflater.inflate(R.layout.custom_layout_for_list_view, parent, false);
            CardView songImg = row.findViewById(R.id.songImg);
            TextView songName = row.findViewById(R.id.nameOfTheSong);

            songName.setText(songs.get(position).getName().replace(".mp3", "").replace(".wav", ""));

            return row;
        }
    }

    private class GetAllSongsTask extends AsyncTask<File, Void, ArrayList<File>> {

        ArrayList<File> findSong(File file) {

            ArrayList<File> arrayList = new ArrayList<>();
            File[] files = file.listFiles();

            for (File singleFile : files) {
                if (singleFile.isDirectory() && !singleFile.isHidden()) {
                    arrayList.addAll(findSong(singleFile));
                } else {
                    if (singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav")) {
                        arrayList.add(singleFile);
                    }
                }
            }
            return arrayList;
        }


        @Override
        protected ArrayList<File> doInBackground(File... files) {


            return findSong(files[0]);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected void onPostExecute(ArrayList<File> files) {

            try {
                CustomAdapter customAdapter = new CustomAdapter(MediaPlayerMain.this, songs);
                listView.setAdapter(customAdapter);
                customAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                Toast.makeText(MediaPlayerMain.this, "No songs found!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (null == mediaPlayer) {
            return;
        }
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            position = 0;
            stopService(new Intent(this, MediaService.class));
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            txtNameOfThe.setText(intent.getStringExtra("name_of_current"));
            if (mediaPlayer.isPlaying()) {
                imgPlay.setBackgroundResource(R.drawable.pause);
            } else {
                imgPlay.setBackgroundResource(R.drawable.play_arrow);
            }


        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        unregisterReceiver(playPauseReceiver);
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (null != mediaPlayer) {
//            if (!mediaPlayer.isPlaying()) {
//                mediaPlayer.stop();
//                mediaPlayer.release();
//                mediaPlayer = null;
//                position = 0;
//                stopService(new Intent(this, MediaService.class));
//            }
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null != mediaPlayer) {
            if (mediaPlayer.isPlaying()) {
                imgPlay.setBackgroundResource(R.drawable.pause);
            } else {
                imgPlay.setBackgroundResource(R.drawable.play_arrow);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        switch (requestCode) {
            case REQ_CODE_FOR_STATE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else if (Build.VERSION.SDK_INT >= 23 && !shouldShowRequestPermissionRationale(permissions[0])) {

                    Toast.makeText(this, "If you want media player to pause while receiving or sending calls you must enable phone permissions in app settings", Toast.LENGTH_LONG)
                            .show();
                } else {
                    Toast.makeText(this, "Media will not pause playing when you get phone call", Toast.LENGTH_SHORT).show();
                }

                return;
        }

    }

    private void askForPhonePerm() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    REQ_CODE_FOR_STATE);

        }
    }
}

