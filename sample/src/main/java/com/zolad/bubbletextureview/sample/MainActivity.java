package com.zolad.bubbletextureview.sample;

import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Surface;
import android.view.TextureView;

import com.zolad.bubbletextureview.BubbleTextureView;
import com.zolad.bubbletextureview.interfaces.SurfaceListener;

public class MainActivity extends AppCompatActivity {

    BubbleTextureView mBtv,mBtv2;
    private MediaPlayer mediaPlayer,mediaPlayer2;
    private boolean loaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtv = findViewById(R.id.btv);
        mBtv2 = findViewById(R.id.btv2);
        this.mediaPlayer = new MediaPlayer();
        this.mediaPlayer2 = new MediaPlayer();
        mBtv.setCornerRadiusAndArrow(40,0.12f, 0.6f,true);
        mBtv2.setCornerRadiusAndArrow(40,0.12f,0f,false);
        mBtv.setSurfaceListner(new SurfaceListener() {
            @Override
            public void onSurfaceCreated(SurfaceTexture surface) {
                if (mediaPlayer != null) {


                    mediaPlayer.setSurface(new Surface(surface));

                    Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sample_video);

                    setVideoURI(uri);

                    start();

                }
            }
        });
        mBtv2.setSurfaceListner(new SurfaceListener() {
            @Override
            public void onSurfaceCreated(SurfaceTexture surface) {
                if (mediaPlayer2 != null) {


                    mediaPlayer2.setSurface(new Surface(surface));

                    Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sample_video);

                    setVideoURI2(uri);

                    start2();

                }
            }
        });
        mBtv.addSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {

            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

            }
        });

    }


    public void setVideoURI(Uri uri) {


        try {
            mediaPlayer.setDataSource(this, uri);
            mediaPlayer.prepare();
            loaded = true;
        } catch (Exception ex) {

        }
    }

    public void start() {


        if (loaded) {
            mediaPlayer.setLooping(true);
            mediaPlayer.start();

        }


    }

    public void setVideoURI2(Uri uri) {


        try {
            mediaPlayer2.setDataSource(this, uri);
            mediaPlayer2.prepare();
            loaded = true;
        } catch (Exception ex) {

        }
    }

    public void start2() {


        if (loaded) {
            mediaPlayer2.setLooping(true);
            mediaPlayer2.start();

        }


    }
}
