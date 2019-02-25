package tv.danmaku.ijk.media.example.smartcams;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.Set;

import tv.danmaku.ijk.media.example.R;
import tv.danmaku.ijk.media.example.widget.media.AndroidMediaController;
import tv.danmaku.ijk.media.example.widget.media.IjkVideoView;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class AssistantActivity extends AppCompatActivity {

    private static final String TAG = "AssistantActivity";
    private boolean camtext = false;
    private Hashtable<Integer, Musician> musicianList = null;
    private ImageView selectedMusician;
    private boolean mode = false;
    private String ip;
    private boolean rectPlacing = false;
    private RectangleView cadre = null;
    private RectangleView cadreWannabe = null;
    private int rectTop = 1;
    private int rectLeft = 1;
    private RectangleView cadreSuivi;
    private int rectSuiviTop;
    private int rectSuiviLeft;
    private int rectSuiviBot;
    private int rectSuiviRight;
    private Button cadreButton;
    private CheckBox suiviAutomatiqueBox;
    private int resolutionWidth;
    private int resolutionHeight;
    private int cropWidth;
    private int cropHeight;
    private int selectionWidth;
    private int selectionHeight;
    private Client client = new Client();

    private IjkVideoView mVideoView;
    private AndroidMediaController mMediaController;
    private TableLayout mHudView;

    private IjkVideoView mVideoView2;
    private AndroidMediaController mMediaController2;
    private TableLayout mHudView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assistant);

        Log.i(TAG, "Creating...");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onStart() {
        super.onStart();

        // Recuperation de toutes les views utiles

        /* Views IjkPlayer */
        mHudView = (TableLayout) findViewById(R.id.hud_view_main);
        mVideoView = (IjkVideoView) findViewById(R.id.video_view_main);
        mHudView2 = (TableLayout) findViewById(R.id.hud_view_main2);
        mVideoView2 = (IjkVideoView) findViewById(R.id.video_view_main2);

        /* Layouts */
        final LinearLayout linearLayoutAssistant = (LinearLayout) findViewById(R.id.linearLayoutAssistant);
        final RelativeLayout selectionLayout = (RelativeLayout) findViewById(R.id.selectionLayout);
        final RelativeLayout selectionSuiviLayout = (RelativeLayout) findViewById(R.id.selectionSuiviLayout);
        final RelativeLayout sceneLayout = (RelativeLayout) findViewById(R.id.assistantSceneLayout);
        final FrameLayout frameLayoutMain2 = (FrameLayout) findViewById(R.id.frameLayoutMain2);

        /* Boutons */
        cadreButton = (Button) findViewById(R.id.cadreButton);
        final Button validCadreButton = (Button) findViewById(R.id.validCadreButton);
        final Button validSuiviButton = (Button) findViewById(R.id.validSuiviButton);
        ToggleButton panButton = (ToggleButton) findViewById(R.id.panButton);
        ToggleButton hdButton = (ToggleButton) findViewById(R.id.hdButton);
        ToggleButton mobileButton = (ToggleButton) findViewById(R.id.mobileButton);
        final ToggleButton switchCamButton = (ToggleButton) findViewById(R.id.switchCamButton);
        final ToggleButton assistantToggleModeButton = (ToggleButton) findViewById(R.id.assistantToggleModeButton);
        final ToggleButton recordButton = (ToggleButton) findViewById(R.id.record);
        suiviAutomatiqueBox = (CheckBox) findViewById(R.id.assistantSuiviAutomatiqueBox);

        suiviAutomatiqueBox.setVisibility(View.INVISIBLE);

        // Récupère musicianList et ip de l'intent
        Intent intent = getIntent();
        musicianList = new Hashtable<>( (HashMap<Integer, Musician>)intent.getExtras().get("MusicianList"));
        ip = intent.getExtras().getString("IP");
        resolutionWidth = intent.getIntExtra("resolutionWidth", 3840);
        resolutionHeight = intent.getIntExtra("resolutionHeight", 2160);
        final int absCropWidth = intent.getIntExtra("cropWidth", 1920);
        final int absCropHeight = intent.getIntExtra("cropHeight", 1080);

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
        mVideoView.setVideoURI(Uri.parse("udp://" + ip + ":1235"));
        mVideoView.start();

        mMediaController2 = new AndroidMediaController(this, false);

        mMediaController2.hide();
        mMediaController2.setEnabled(false);
        mVideoView2.setMediaController(mMediaController2);
        mMediaController2.setVisibility(View.INVISIBLE);
        mVideoView2.setHudView(mHudView2);
        mHudView2.setVisibility(View.INVISIBLE);
        mVideoView2.setVideoURI(Uri.parse("udp://" + ip + ":1236"));
        mVideoView2.start();

        // Initialisation cadreButton
        cadreButton.setVisibility(View.INVISIBLE);
        cadreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!rectPlacing) {
                    linearLayoutAssistant.setVisibility(View.INVISIBLE);
                    mVideoView.setVisibility(View.INVISIBLE);
                    selectionLayout.setVisibility(View.VISIBLE);
                    rectPlacing = true;
                } else {
                    if (cadreWannabe != null) {
                        selectionLayout.removeView(cadreWannabe);
                        cadreWannabe = null;
                    }
                    selectionLayout.setVisibility(View.INVISIBLE);
                    mVideoView.setVisibility(View.VISIBLE);
                    linearLayoutAssistant.setVisibility(View.VISIBLE);
                    rectPlacing = false;
                }
            }
        });

        // Initialisation validCadreButton
        validCadreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cadreWannabe != null) {
                    if (cadre != null) {
                        selectionLayout.removeView(cadre);
                    }
                    cadre = cadreWannabe;
                    cadreWannabe = null;
                    cadre.validateColor();
                    cadre.invalidate();

                    int top = (int) (((float) rectTop/selectionHeight)*resolutionHeight);
                    int left = (int) (((float) rectLeft/selectionWidth)*resolutionWidth);

                    new ToServer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "R", String.valueOf(top), String.valueOf(left));

                } else {
                    Toast.makeText(getApplicationContext(), "Sélectionnez un nouveau cadre avant de le valider", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Initialisation selectionLayout
        selectionLayout.setVisibility(View.INVISIBLE);
        selectionLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    int rectTopWannabe = (int) motionEvent.getY();
                    int rectLeftWannabe = (int) motionEvent.getX();

                    if (cadreWannabe != null) {
                        selectionLayout.removeView(cadreWannabe);
                    }

                    cadreWannabe = new RectangleView(getApplicationContext(), rectLeftWannabe, rectTopWannabe, cropWidth, cropHeight);
                    selectionLayout.addView(cadreWannabe);
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {

                    int rectTopWannabe = (int) motionEvent.getY();
                    int rectLeftWannabe = (int) motionEvent.getX();


                    cadreWannabe.setRectangle(rectLeftWannabe, rectTopWannabe);
                    cadreWannabe.sortRectangle();
                    cadreWannabe.invalidate();
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    int rectTopWannabe = (int) motionEvent.getY();
                    int rectLeftWannabe = (int) motionEvent.getX();

                    if (((rectLeftWannabe + cropWidth) > selectionWidth) || ((rectTopWannabe + cropHeight) > selectionHeight)) {
                        cadreWannabe.setRectangle(rectLeft, rectTop);
                        cadreWannabe.invalidate();

                        Toast.makeText(getApplicationContext(), "Veuillez garder le cadre sur l'écran", Toast.LENGTH_SHORT).show();
                    } else {
                        rectTop = rectTopWannabe;
                        rectLeft = rectLeftWannabe;

                        cadreWannabe.setRectangle(rectLeft, rectTop);
                        cadreWannabe.invalidate();
                    }
                }
                return true;
            }
        });

        selectionLayout.post(new Runnable() {
            @Override
            public void run() {
                selectionWidth = selectionLayout.getMeasuredWidth();
                selectionHeight = selectionLayout.getMeasuredHeight();

                cropWidth = (int) (((float) absCropWidth/resolutionWidth)*selectionWidth);
                cropHeight = (int) (((float) absCropHeight/resolutionHeight)*selectionHeight);
            }
        });

        // Initialisation validSuiviButton
        validSuiviButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cadreSuivi != null) {
                    selectionSuiviLayout.removeView(cadreSuivi);
                    cadreSuivi = null;

                    selectionSuiviLayout.setVisibility(View.INVISIBLE);
                    cadreButton.setVisibility(View.VISIBLE);
                    linearLayoutAssistant.setVisibility(View.VISIBLE);


                    int rectSuiviHeight = (int) (((float) (rectSuiviBot - rectSuiviTop)/selectionHeight)*resolutionHeight);
                    int rectSuiviWidth = (int) (((float) (rectSuiviRight - rectSuiviLeft)/selectionWidth)*resolutionWidth);
                    rectSuiviTop = (int) (((float) rectSuiviTop/selectionHeight)*resolutionHeight);
                    rectSuiviLeft = (int) (((float) rectSuiviLeft/selectionWidth)*resolutionWidth);

                    new ToServer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "T", "1", String.valueOf(rectSuiviLeft), String.valueOf(rectSuiviTop), String.valueOf(rectSuiviWidth), String.valueOf(rectSuiviHeight));
                } else {
                    Toast.makeText(getApplicationContext(), "Sélectionnez un nouveau cadre avant de le valider", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Initialisation selectionSuiviLayout
        selectionSuiviLayout.setVisibility(View.INVISIBLE);
        selectionSuiviLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    if (cadreSuivi != null) {
                        selectionSuiviLayout.removeView(cadreSuivi);
                    }
                    rectSuiviTop = (int) motionEvent.getY();
                    rectSuiviLeft = (int) motionEvent.getX();

                    cadreSuivi = new RectangleView(getApplicationContext(), rectSuiviLeft, rectSuiviTop, 1, 1);
                    selectionSuiviLayout.addView(cadreSuivi);
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {

                    rectSuiviBot = (int) motionEvent.getY();
                    rectSuiviRight = (int) motionEvent.getX();

                    cadreSuivi.setRectangleSize(rectSuiviRight, rectSuiviBot);
                    cadreSuivi.sortRectangle();
                    cadreSuivi.invalidate();
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

                    int rectSuiviBotWannabe = (int) motionEvent.getY();
                    int rectSuiviRightWannabe = (int) motionEvent.getX();
                    int rectSuiviTopWannabe = rectSuiviTop;
                    int rectSuiviLeftWannabe = rectSuiviLeft;

                    cadreSuivi.setRectangleSize(rectSuiviRightWannabe, rectSuiviBotWannabe);
                    cadreSuivi.sortRectangle();
                    cadreSuivi.invalidate();

                    rectSuiviTop = Math.min(rectSuiviBotWannabe, rectSuiviTopWannabe);
                    rectSuiviBot = Math.max(rectSuiviBotWannabe, rectSuiviTopWannabe);
                    rectSuiviLeft = Math.min(rectSuiviLeftWannabe, rectSuiviRightWannabe);
                    rectSuiviRight = Math.max(rectSuiviLeftWannabe, rectSuiviRightWannabe);
                }
                return true;
            }
        });

        // Initialise les boutons de choix du flux en sélectionnant mobileButton
        panButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camSwitch(R.id.panButton, true);
            }
        });

        hdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camSwitch(R.id.hdButton, true);
            }
        });

        mobileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camSwitch(R.id.mobileButton, true);
            }
        });

        camSwitch(R.id.mobileButton, true);

        // Change aussi le texte dans le coin haut droit
        switchCamButton.setBackgroundColor(Color.TRANSPARENT);

        switchCamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camTextSwitch();
            }
        });

        // Met en place le sceneLayout
        sceneLayout.setBackgroundColor(Color.WHITE);
        Set<Integer> musicianSet = musicianList.keySet();
        for (Integer key : musicianSet) {
            final Musician musician = musicianList.get(key);
            final ImageView musicianView = newMusicianView(musician.getInstrumentID());
            musicianView.setTag(musician.getId());

            sceneLayout.post(new Runnable() {
                @Override
                public void run() {
                    int width = sceneLayout.getMeasuredWidth();
                    int height = sceneLayout.getMeasuredHeight();
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    params.leftMargin = (int) (width * musician.getxCoord());
                    params.topMargin = (int) (height * musician.getyCoord());

                    sceneLayout.addView(musicianView, params);
                }
            });
        }

        sceneLayout.post(new Runnable() {
            @Override
            public void run() {
                switchMusician(musicianList.entrySet().iterator().next().getKey(), true);
            }
        });

        // Met en place le toggleModeButton qui switche entre les modes automatique et manuel du choix de musicien ciblé
        assistantToggleModeButton.setChecked(true);

        assistantToggleModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (assistantToggleModeButton.isChecked()) {
                    new ToServer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "S", "1");
                } else {
                    new ToServer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "S", "0");
                }

                mode = !mode;
            }
        });

        // Met en place le bouton d'activation de l'enregistrement
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recordButton.isChecked()) {
                    new ToServer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "E", "1");
                } else {
                    new ToServer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "E", "0");
                }
            }
        });

        // Met en place le bouton d'activation du tracking
        suiviAutomatiqueBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (suiviAutomatiqueBox.isChecked()) {
                    selectionSuiviLayout.setVisibility(View.VISIBLE);
                    cadreButton.setVisibility(View.INVISIBLE);
                    linearLayoutAssistant.setVisibility(View.INVISIBLE);
                } else {
                    new ToServer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "T", "0");
                }
            }
        });

        // Mise en place recepteur de commandes
        client.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        Log.i(TAG, "Starting...");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "Resuming...");
    }

    @Override
    protected void onPause() {
        super.onPause();

        mVideoView.stopPlayback();
        mVideoView2.stopPlayback();
        Log.i(TAG, "Pausing...");
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * Change l'affichage de quel flux est envoyé au montage et envoie la commande au serveur pour effectivement le changer si send est true
     * @param cam : L'id du bouton de choix de flux à sélectionner
     * @param send : Envoi ou non de la commande au serveur
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void camSwitch(int cam, boolean send) {
        ToggleButton panButton = (ToggleButton) findViewById(R.id.panButton);
        ToggleButton hdButton = (ToggleButton) findViewById(R.id.hdButton);
        ToggleButton mobileButton = (ToggleButton) findViewById(R.id.mobileButton);

        panButton.setChecked(false);
        hdButton.setChecked(false);
        mobileButton.setChecked(false);
        ((ToggleButton) findViewById(cam)).setChecked(true);

        if (send) {
            String button = null;
            switch (cam) {
                case R.id.panButton:
                    button = "0";
                    break;
                case R.id.hdButton:
                    button = "1";
                    break;
                case R.id.mobileButton:
                    button = "2";
                    break;
                default:
                    break;
            }
            new ToServer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "F", button);
        }
    }

    /**
     * Switche le texte en haut à droite du flux affiché
     */
    public void camTextSwitch() {
        FrameLayout frameLayoutMain = (FrameLayout) findViewById(R.id.frameLayoutMain);
        FrameLayout frameLayoutMain2 = (FrameLayout) findViewById(R.id.frameLayoutMain2);
        if (camtext) {
            ((TextView) findViewById(R.id.camText)).setText("Caméra mobile");
            frameLayoutMain2.removeView(mVideoView);
            frameLayoutMain.removeView(mVideoView2);
            frameLayoutMain2.addView(mVideoView2);
            frameLayoutMain.addView(mVideoView);
            cadreButton.setVisibility(View.INVISIBLE);
            suiviAutomatiqueBox.setVisibility(View.INVISIBLE);
        } else {
            ((TextView) findViewById(R.id.camText)).setText("Caméra 4K");
            frameLayoutMain2.removeView(mVideoView2);
            frameLayoutMain.removeView(mVideoView);
            frameLayoutMain2.addView(mVideoView);
            frameLayoutMain.addView(mVideoView2);
            cadreButton.setVisibility(View.VISIBLE);
            suiviAutomatiqueBox.setVisibility(View.VISIBLE);
        }
        camtext = !camtext;
    }

    /**
     * Crée une nouvelle ImageView correspondant à un musicien
     * @return L'ImageView du musicien ajouté
     */
    public ImageView newMusicianView(int instrumentID) {
        final ImageView musician = new ImageView(this);
        switch (instrumentID) {
            case 0:
                musician.setImageResource(R.mipmap.guitar_electric_off);
                break;
            case 1:
                musician.setImageResource(R.mipmap.guitar_classic_off);
                break;
            case 2:
                musician.setImageResource(R.mipmap.singer_off);
                break;
            case 3:
                musician.setImageResource(R.mipmap.drum_set_off);
                break;
            case 4:
                musician.setImageResource(R.mipmap.piano_off);
                break;
            case 5:
                musician.setImageResource(R.mipmap.violin_off);
                break;
            default:
                Toast.makeText(getApplicationContext(), "Une erreur est survenue", Toast.LENGTH_SHORT).show();
        }
        musician.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchMusician((int) musician.getTag(), true);
            }
        });
        return musician;
    }

    /**
     * Change le musicien ciblé par le système et envoie la commande au serveur si send est true
     * @param id : L'id du musicien à cibler
     * @param send : Envoi ou non de la commande au serveur
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void switchMusician(int id, boolean send) {

        RelativeLayout sceneLayout = (RelativeLayout) findViewById(R.id.assistantSceneLayout);

        if (selectedMusician != null) {
            selectedMusician.setBackgroundColor(Color.TRANSPARENT);
        }

        ImageView musician = (ImageView) sceneLayout.findViewWithTag(id);
        selectedMusician = musician;
        musician.setBackgroundColor(Color.BLUE);

        if (send) {
            new ToServer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "M", String.valueOf(id));
        }
    }

    /**
     * La classe privée permettant d'envoyer les commandes au serveur
     */
    private class ToServer extends AsyncTask<String, Void, Void>
    {

        @Override
        protected Void doInBackground(String... arg0) {

            String data;
            Socket skt = null;
            PrintWriter out = null;
            try {
                data = ". ";
                for (String command : arg0) {
                    data += command + " ";
                }
                data += ".";

                skt = new Socket(ip, 1234);
                out = new PrintWriter(skt.getOutputStream(), true);
                out.print(data);
                out.close();
                skt.close();
            }
            catch(Exception e) {
                Log.e(TAG, "Problème de connection");
                try {
                    if (out != null) {
                        out.close();
                    }
                    if (skt != null) {
                        skt.close();
                    }
                } catch (IOException e1) {
                    Log.e(TAG, "Impossible de fermer skt");
                }
            }
            return null;
        }
    }



    /**
     *  La classe privée permettant de recevoir des commandes du serveur
     */
    private class Client extends AsyncTask<Void, String, Void>
    {

        @Override
        protected void onProgressUpdate(String... arg0) {
            super.onProgressUpdate(arg0);
            Scanner scan = new Scanner(arg0[0]);
            switch (scan.next()) {
                case "F":
                    int button = R.id.panButton;
                    switch (scan.nextInt()) {
                        case 0:
                            button = R.id.panButton;
                            break;
                        case 1:
                            button = R.id.hdButton;
                            break;
                        case 2:
                            button = R.id.mobileButton;
                            break;
                        default:
                            Log.e(TAG, "Mauvaise commande de flux");
                    }
                    camSwitch(button, false);
                    break;
                case "M":
                    switchMusician(scan.nextInt(), false);
                    break;
                default:
                    Log.e(TAG, "Mauvaise commande");
            }
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            String data;
            try {
                Socket skt = new Socket(ip, 3000);
                BufferedReader in = new BufferedReader(new InputStreamReader(skt.getInputStream()));
                Log.e(TAG, "Client - Starting");
                while (true) {
                    Log.e(TAG, "Client - Waiting for a command");
                    data = in.readLine();
                    Log.e(TAG, "Client - Command received");

                    if (data.startsWith(".") && data.endsWith(".")) {
                        publishProgress(data.substring(2, data.length() - 2));
                    } else {
                        Log.e(TAG, "Pas le bon format de string");
                    }
                }

            } catch (Exception e) {
                Log.e(TAG, "Problème de connection");
            }
            return null;
        }
    }
    
}