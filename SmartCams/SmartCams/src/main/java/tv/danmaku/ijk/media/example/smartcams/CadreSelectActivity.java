package tv.danmaku.ijk.media.example.smartcams;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.internal.widget.ActivityChooserModel;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.Toast;

import tv.danmaku.ijk.media.example.R;
import tv.danmaku.ijk.media.example.widget.media.AndroidMediaController;
import tv.danmaku.ijk.media.example.widget.media.IjkVideoView;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class CadreSelectActivity extends AppCompatActivity {

    private IjkVideoView mVideoView;
    private AndroidMediaController mMediaController;
    private TableLayout mHudView;

    private int rectTop = 1;
    private int rectLeft = 1;
    private RectangleView cadre = null;
    private int x;
    private int y;
    private PointView point = null;
    private String ip;
    private int resolutionWidth;
    private int resolutionHeight;
    private int cropWidth;
    private int cropHeight;
    private int selectionWidth;
    private int selectionHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadre_select);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Recuperation de l'ip
        Intent intent = getIntent();
        ip = intent.getStringExtra("ip");
        resolutionWidth = intent.getIntExtra("resolutionWidth", 3840);
        resolutionHeight = intent.getIntExtra("resolutionHeight", 2160);
        final int absCropWidth = intent.getIntExtra("cropWidth", 1920);
        final int absCropHeight = intent.getIntExtra("cropHeight", 1080);

        // Recuperation de toutes les views utiles

        /* Views IjkPlayer */
        mHudView = (TableLayout) findViewById(R.id.hud_view_select);
        mVideoView = (IjkVideoView) findViewById(R.id.video_view_select);

        /* Layouts */
        final RelativeLayout selectionLayoutSelect = (RelativeLayout) findViewById(R.id.selectionLayoutSelect);

        /* Boutons */
        final Button validCadreSelect = (Button) findViewById(R.id.validCadreSelect);

        // Initialisation IjkPlayer
        mMediaController = new AndroidMediaController(this, false);

        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        mMediaController.hide();
        mMediaController.setEnabled(false);
        mVideoView.setMediaController(mMediaController);
        mMediaController.setVisibility(View.INVISIBLE);
        mVideoView.setHudView(mHudView);
        mHudView.setVisibility(View.INVISIBLE);
        mVideoView.setVideoURI(Uri.parse("udp://" + ip + ":1236"));
        mVideoView.start();

        // Initialisation selectionLayoutSelect
        selectionLayoutSelect.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int event = motionEvent.getAction();
                if (event == MotionEvent.ACTION_DOWN) {
                    int rectTopWannabe = (int) motionEvent.getY();
                    int rectLeftWannabe = (int) motionEvent.getX();

                    cadre.setRectangle(rectLeftWannabe, rectTopWannabe);
                    cadre.invalidate();
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {

                    int rectTopWannabe = (int) motionEvent.getY();
                    int rectLeftWannabe = (int) motionEvent.getX();


                    cadre.setRectangle(rectLeftWannabe, rectTopWannabe);
                    cadre.invalidate();
                }
                if (event == MotionEvent.ACTION_UP) {

                    int rectTopWannabe = (int) motionEvent.getY();
                    int rectLeftWannabe = (int) motionEvent.getX();

                    if (((rectLeftWannabe + cropWidth) > selectionWidth) || ((rectTopWannabe + cropHeight) > selectionHeight)) {
                        cadre.setRectangle(rectLeft, rectTop);
                        cadre.invalidate();

                        Toast.makeText(getApplicationContext(), "Veuillez garder le cadre sur l'écran", Toast.LENGTH_SHORT).show();
                    } else {
                        rectTop = rectTopWannabe;
                        rectLeft = rectLeftWannabe;

                        cadre.setRectangle(rectLeft, rectTop);
                        cadre.invalidate();
                    }
                }

                return true;
            }
        });

        selectionLayoutSelect.post(new Runnable() {
            @Override
            public void run() {
                selectionWidth = selectionLayoutSelect.getMeasuredWidth();
                selectionHeight = selectionLayoutSelect.getMeasuredHeight();

                cropWidth = (int) (((float) absCropWidth/resolutionWidth)*selectionWidth);
                cropHeight = (int) (((float) absCropHeight/resolutionHeight)*selectionHeight);

                cadre = new RectangleView(getApplicationContext(), 1, 1, cropWidth, cropHeight);
                cadre.validateColor();
                selectionLayoutSelect.addView(cadre);
            }
        });

        // Initialisation validCadreButton
        validCadreSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("rectTop", (int) (((float) rectTop/selectionHeight)*resolutionHeight));
                returnIntent.putExtra("rectLeft", (int) (((float) rectLeft/selectionWidth)*resolutionWidth));
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });


    }

    @Override
    protected void onStop() {
        super.onStop();

        mVideoView.stopPlayback();
    }
}
