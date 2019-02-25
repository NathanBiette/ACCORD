package tv.danmaku.ijk.media.example;

import android.annotation.TargetApi;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;

import tv.danmaku.ijk.media.example.widget.media.AndroidMediaController;
import tv.danmaku.ijk.media.example.widget.media.IjkVideoView;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class MainTestActivity extends AppCompatActivity {

    private IjkVideoView mVideoView;
    private AndroidMediaController mMediaController;
    private TableLayout mHudView;

    private IjkVideoView mVideoView2;
    private AndroidMediaController mMediaController2;
    private TableLayout mHudView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_maintest);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onStart() {
        super.onStart();

                    // init UI
        mMediaController = new AndroidMediaController(this, false);

        mHudView = (TableLayout) findViewById(R.id.hud_view2);

        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        mVideoView = (IjkVideoView) findViewById(R.id.video_view2);
        mMediaController.hide();
        mMediaController.setEnabled(false);
        mVideoView.setMediaController(mMediaController);
        mMediaController.setVisibility(View.INVISIBLE);
        mVideoView.setHudView(mHudView);
        mHudView.setVisibility(View.INVISIBLE);
        mVideoView.setVideoURI(Uri.parse("udp://137.194.67.255:1234"));
        mVideoView.start();

        // init UI
        mMediaController2 = new AndroidMediaController(this, false);

        mHudView2 = (TableLayout) findViewById(R.id.hud_view3);

        mVideoView2 = (IjkVideoView) findViewById(R.id.video_view3);
        mMediaController2.hide();
        mMediaController2.setEnabled(false);
        mVideoView2.setMediaController(mMediaController2);
        mMediaController2.setVisibility(View.INVISIBLE);
        mVideoView2.setHudView(mHudView2);
        mHudView2.setVisibility(View.INVISIBLE);
        mVideoView2.setVideoURI(Uri.parse("udp://137.194.67.255:1236"));
        mVideoView2.start();

    }
}
