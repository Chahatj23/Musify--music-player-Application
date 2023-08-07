package com.example.musify;

import static android.app.Service.START_NOT_STICKY;
import static android.os.Build.*;
import static android.os.Build.VERSION.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
MediaPlayer player;
AudioManager audioManager;
TextView cposition;
int progresssave;
SeekBar seekprog,seekvol;
TextView duration;
SharedPreferences getshared;
SharedPreferences shrd;
LottieAnimationView lottieAnimationView;

// time conversion
    public String timerConversion(long value) {
        String audioTime;
        int dur = (int) value;
        int hrs = (dur / 3600000);
        int mns = (dur / 60000) % 60000;
        int scs = dur % 60000 / 1000;

        if (hrs > 0) {
            audioTime = String.format("%02d:%02d:%02d", hrs, mns, scs);
        } else {
            audioTime = String.format("%02d:%02d", mns, scs);
        }
        return audioTime;
    }
    int playstatus=0;
    public void playorpause(View view){
        duration= findViewById(R.id.duration);
        cposition=findViewById(R.id.position);
        ImageView playorpause=(ImageView) view;
    if(playstatus==0) {
        getshared =getSharedPreferences("data",MODE_PRIVATE);
        int progressed =getshared.getInt("progress",0);
        player.seekTo(progressed);
        player.start();
        playstatus=1;
        double dur=player.getDuration();
        duration.setText(timerConversion((long)dur));
        lottieAnimationView.setVisibility(View.VISIBLE);
        playorpause.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24);
}
    else if(playstatus==1) {
        playstatus=0;
        player.pause();
        progresssave=player.getCurrentPosition();
        SharedPreferences shrd =getSharedPreferences("data",MODE_PRIVATE);
        SharedPreferences.Editor editor=shrd.edit();
        editor.putInt("progress",progresssave);
        editor.apply();
        lottieAnimationView.setVisibility(View.INVISIBLE);
        playorpause.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);
    }
}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lottieAnimationView= findViewById(R.id.animation_view);
        player=MediaPlayer.create(this,R.raw.music);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        Toast.makeText(getApplicationContext(), "Oncreate", Toast.LENGTH_SHORT).show();
        getshared =getSharedPreferences("data",MODE_PRIVATE);
        int progressed =getshared.getInt("progress",0);
        player.seekTo(progressed);
        int maxvol =audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int curvol =audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        seekvol=findViewById(R.id.volume);
        seekvol.setMax(maxvol);
        seekvol.setProgress(curvol);
        seekvol.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekprog =findViewById(R.id.playing);
        seekprog.setMax(player.getDuration());
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                seekprog.setProgress(player.getCurrentPosition());

                try {
                    double current=player.getCurrentPosition();
                    cposition.setText(timerConversion((long)current));
                }
                catch (Exception e){
                    Log.d("error","can't load progress");
                }
            }
        },
      0,  10);

        seekprog.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if dont write fromUser line then song lags
//                player.seekTo(progress);
                if(fromUser) {
                    player.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
//        Toast.makeText(getApplicationContext(), "Detached", Toast.LENGTH_SHORT).show();
        SharedPreferences shrd =getSharedPreferences("data",MODE_PRIVATE);
        SharedPreferences.Editor editor=shrd.edit();
        editor.putInt("progress",progresssave);
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Toast.makeText(getApplicationContext(), "Destroyed", Toast.LENGTH_SHORT).show();
        progresssave=player.getCurrentPosition();
       shrd =getSharedPreferences("data",MODE_PRIVATE);
        SharedPreferences.Editor editor=shrd.edit();
        editor.putInt("progress",progresssave);
        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        Toast.makeText(getApplicationContext(), "Start", Toast.LENGTH_SHORT).show();
        getshared =getSharedPreferences("data",MODE_PRIVATE);
        int progressed =getshared.getInt("progress",0);
        seekprog.setProgress(progressed);
    }
    @Override
    protected void onStop() {
        super.onStop();
//        Toast.makeText(getApplicationContext(), "Stopped", Toast.LENGTH_SHORT).show();
//        progresssave=player.getCurrentPosition();
//        shrd =getSharedPreferences("data",MODE_PRIVATE);
//        SharedPreferences.Editor editor=shrd.edit();
//        editor.putInt("progress",progresssave);
//        editor.apply();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
//        Toast.makeText(getApplicationContext(), "Restart", Toast.LENGTH_SHORT).show();
        getshared =getSharedPreferences("data",MODE_PRIVATE);
        int progressed =getshared.getInt("progress",0);
        seekprog.setProgress(progressed);
//        player.seekTo(progressed);
    }
    @Override
    public void onBackPressed() {
//        Not to use super line so that activity doesnt destroy on back button followed by the code
//        super.onBackPressed();
        moveTaskToBack(true);
    }

    // Changes for Notification
    if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        CharSequence channelName = "Music Player";
        String channelId = "music_player_channel";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = null;
        if (SDK_INT >= VERSION_CODES.O) {
            channel = new NotificationChannel(channelId, channelName, importance);
        }

        NotificationManager notificationManager = null;
        if (SDK_INT >= VERSION_CODES.M) {
            notificationManager = getSystemService(NotificationManager.class);
        }
        if (SDK_INT >= VERSION_CODES.O) {
            notificationManager.createNotificationChannel(channel);
        }
    }
    // In your music player service or main activity
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "music_player_channel")
            .setSmallIcon(R.drawable.musifylogo) // Your custom music icon
            .setContentTitle("Now Playing")
            .setContentText("Song Title")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

    // Create a PendingIntent for launching the music player when the notification is clicked
//    Intent intent = new Intent(this, MusicPlayerActivity.class); // Your music player activity
//    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
//builder.setContentIntent(pendingIntent);

    // Build the notification
    Notification notification = builder.build();

    // Start the foreground service with the notification
//    startForeground(NOTIFICATION_ID, notification);
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        String action = intent.getAction();
//
//        if ("PLAY_PAUSE".equals(action)) {
//            // Toggle playback or pause here
//        }
//
//        // Handle other actions...
//
//        return START_NOT_STICKY;
//    }
}