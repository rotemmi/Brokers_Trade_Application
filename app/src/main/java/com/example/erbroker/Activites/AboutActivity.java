package com.example.erbroker.Activites;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.example.erbroker.R;

import java.io.IOException;

// Tutorial video , How to use ERBroker
public class AboutActivity extends AppCompatActivity implements SurfaceHolder.Callback,MediaPlayer.OnPreparedListener
{
    private SurfaceView surfaceView;
    private MediaPlayer mediaPlayer;
    private SurfaceHolder surfaceHolder;
    private  Uri uri;

    // streaming tutorial video from data_base
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        surfaceView = findViewById(R.id.surfaceView);
        surfaceHolder=surfaceView.getHolder();
        surfaceHolder.addCallback(AboutActivity.this);
        uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/erbroker-eb120.appspot.com/o/tutorial.mp4?alt=media&token=1abf0287-c833-4b41-a0e5-8f19e84f2831");
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder)
    {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDisplay(surfaceHolder);
        try {
            mediaPlayer.setDataSource(this,uri );
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(AboutActivity.this);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        } catch (IOException e) {

        }

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }

    @Override
    public void onPrepared(MediaPlayer mp)
    {
        mediaPlayer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaPlayer();
    }

    private void releaseMediaPlayer()
    {
        if(mediaPlayer!=null)
        {
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }
}