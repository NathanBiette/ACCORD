package tv.danmaku.ijk.media.example.smartcams;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
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
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;

import tv.danmaku.ijk.media.example.R;
import tv.danmaku.ijk.media.example.widget.media.AndroidMediaController;
import tv.danmaku.ijk.media.example.widget.media.IjkVideoView;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class OptionsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "OptionsActivity";
    private static final int GET_NEW_MUSICIAN = 42;
    private Hashtable<Integer, Musician> musicianList = new Hashtable<>();
    private int numberOfMusicians = 0;
    private int actualNumberOfMusicians = 0;
    private ImageView selectedImageView = null;
    private int isPlacing = 0;
    private String ip;
    private boolean enable = false;

    private int resolutionWidth;
    private int resolutionHeight;
    private int cropWidth;
    private int cropHeight;

    private IjkVideoView mVideoView;
    private AndroidMediaController mMediaController;
    private TableLayout mHudView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        Log.i("OptionActivity", "Creating...");
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Récupère l'ip de l'intent
        Intent intent = getIntent();
        ip = intent.getStringExtra("ip");
        resolutionWidth = intent.getIntExtra("resolutionWidth", 3840);
        resolutionHeight = intent.getIntExtra("resolutionHeight", 2160);
        cropWidth = intent.getIntExtra("cropWidth", 1920);
        cropHeight = intent.getIntExtra("cropHeight", 1080);

        // Initialise les boutons OK et Retour
        Button okButton = (Button) findViewById(R.id.okButton);
        okButton.setOnClickListener(this);

        Button returnButton = (Button) findViewById(R.id.returnButton);
        returnButton.setOnClickListener(this);

        // Initialise le menuMusicien en associant les fonctions liées aux options du menu
        final Button menuMusicien = (Button) findViewById(R.id.menuMusicien);
        menuMusicien.setOnClickListener(new View.OnClickListener() {

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View v) {

                PopupMenu popup = new PopupMenu(OptionsActivity.this, menuMusicien);

                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        if (isPlacing != 0) {
                            Toast.makeText(getApplicationContext(), "Un musicien est en cours de placement", Toast.LENGTH_SHORT).show();
                            return true;
                        }

                        Context context;
                        CharSequence text;
                        int duration;
                        Toast toast;
                        switch (item.getItemId()) {

                            // Ajoute un musicien sur la scène
                            case R.id.addMusician:

                                if (actualNumberOfMusicians > 24) {

                                    context = getApplicationContext();
                                    text = "Trop de musiciens";
                                    duration = Toast.LENGTH_SHORT;

                                    toast = Toast.makeText(context, text, duration);
                                    toast.show();

                                } else {

                                    Intent getNewMusician = new Intent(OptionsActivity.this, AddMusicianActivity.class);
                                    getNewMusician.putExtra("ip", ip);
                                    getNewMusician.putExtra("resolutionWidth", resolutionWidth);
                                    getNewMusician.putExtra("resolutionHeight", resolutionHeight);
                                    getNewMusician.putExtra("cropWidth", cropWidth);
                                    getNewMusician.putExtra("cropHeight", cropHeight);
                                    startActivityForResult(getNewMusician, GET_NEW_MUSICIAN);

                                }

                                break;

                            // Supprime le musicien sélectionné
                            case R.id.deleteMusician:
                                if (selectedImageView == null) {

                                    context = getApplicationContext();
                                    text = "Pas de musicien sélectionné";
                                    duration = Toast.LENGTH_SHORT;

                                    toast = Toast.makeText(context, text, duration);
                                    toast.show();

                                } else {

                                    int id = (int) selectedImageView.getTag();
                                    musicianList.remove(id);
                                    RelativeLayout scene = (RelativeLayout) findViewById(R.id.scene);
                                    scene.removeView(selectedImageView);
                                    selectedImageView = null;
                                    actualNumberOfMusicians -= 1;

                                    new ToServer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "D", String.valueOf(id));
                                }

                                break;
                        }
                        return true;
                    }
                });

                popup.show();
            }
        });

        // Initialise les 6 boutons de contrôle de la caméra et du zoom
        ImageButton upButton = (ImageButton) findViewById(R.id.optionsUp);
        upButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        enable = true;
                        new ServerLoop().execute("C", "U");
                        break;
                    case MotionEvent.ACTION_UP:
                        enable = false;
                        break;
                }
                return true;
            }
        });
        /* upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Server().execute("C","U");
            }
        }); */

        ImageButton downButton = (ImageButton) findViewById(R.id.optionsDown);
        downButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        enable = true;
                        new ServerLoop().execute("C", "D");
                        break;
                    case MotionEvent.ACTION_UP:
                        enable = false;
                        break;
                }
                return true;
            }
        });
        /* downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Server().execute("C","D");
            }
        }); */

        ImageButton rightButton = (ImageButton) findViewById(R.id.optionsRight);
        rightButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        enable = true;
                        new ServerLoop().execute("C", "R");
                        break;
                    case MotionEvent.ACTION_UP:
                        enable = false;
                        break;
                }
                return true;
            }
        });
        /* rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Server().execute("C","R");
            }
        }); */

        ImageButton leftButton = (ImageButton) findViewById(R.id.optionsLeft);
        leftButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        enable = true;
                        new ServerLoop().execute("C", "L");
                        break;
                    case MotionEvent.ACTION_UP:
                        enable = false;
                        break;
                }
                return true;
            }
        });
        /* leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Server().execute("C","L");
            }
        }); */

        ImageButton zoomPlus = (ImageButton) findViewById(R.id.optionsZoomPlus);
        zoomPlus.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        enable = true;
                        new ServerLoop().execute("Z", "1");
                        break;
                    case MotionEvent.ACTION_UP:
                        enable = false;
                        break;
                }
                return true;
            }
        });

        ImageButton zoomMinus = (ImageButton) findViewById(R.id.optionsZoomMinus);
        zoomMinus.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        enable = true;
                        new ServerLoop().execute("Z", "0");
                        break;
                    case MotionEvent.ACTION_UP:
                        enable = false;
                        break;
                }
                return true;
            }
        });

        // Initialise le layout scene pour que les musiciens ajoutés apparaissent à l'endroit touché
        final RelativeLayout scene = (RelativeLayout) findViewById(R.id.scene);

        scene.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isPlacing != 0) {

                    if (isPlacing == 2) {
                        scene.removeView(selectedImageView);
                    }

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        final int startX = (int) event.getX();
                        final int startY = (int) event.getY();

                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                        params.leftMargin = startX;
                        params.topMargin = startY;

                        scene.addView(selectedImageView, params);

                        final Musician musician = musicianList.get(selectedImageView.getTag());

                        scene.post(new Runnable() {
                            @Override
                            public void run() {
                                int width = scene.getMeasuredWidth();
                                int height = scene.getMeasuredHeight();
                                musician.setxCoord(startX/((float) width));
                                musician.setyCoord(startY/((float) height));
                            }
                        });


                        isPlacing = 0;
                        selectedImageView.setBackgroundColor(Color.BLUE);

                    }
                }
                return false;
            }
        });

        Log.i("OptionsActivity", "Starting...");
    }

    @Override
    protected void onPause() {
        super.onPause();

        mVideoView.stopPlayback();

        Log.i("OptionsActivity", "Pausing...");
    }

    @Override
    protected void onResume() {
        super.onResume();

        mHudView = (TableLayout) findViewById(R.id.hud_view_option);
        mVideoView = (IjkVideoView) findViewById(R.id.video_view_option);

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

        Log.i("OptionsActivity", "Resuming...");
    }

    // On initialise les onClickListener de okButton et returnButton ici
    @Override
    public void onClick(View v) {

        if (isPlacing != 0) {
            Toast.makeText(getApplicationContext(), "Un musicien est en cours de placement", Toast.LENGTH_SHORT).show();
        } else {

            switch (v.getId()) {

                // On vérifie qu'il y a au moins un musicien et qu'ils sont tous calibrés avant d'ouvrir AssistantActivity
                case R.id.okButton:
                    if (actualNumberOfMusicians == 0) {

                        Toast.makeText(getApplicationContext(), "Ajoutez au moins un musicien", Toast.LENGTH_SHORT).show();

                    } else {

                        Intent assistantActivity = new Intent(OptionsActivity.this, AssistantActivity.class);
                        assistantActivity.putExtra("MusicianList", musicianList);
                        assistantActivity.putExtra("IP", ip);
                        assistantActivity.putExtra("resolutionWidth", resolutionWidth);
                        assistantActivity.putExtra("resolutionHeight", resolutionHeight);
                        assistantActivity.putExtra("cropWidth", cropWidth);
                        assistantActivity.putExtra("cropHeight", cropHeight);
                        startActivity(assistantActivity);
                    }
                    break;

                case R.id.returnButton:
                    finish();
                    break;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_NEW_MUSICIAN) {
            if (resultCode == Activity.RESULT_OK) {
                int instrumentID = data.getIntExtra("instrument", -1);
                int canal = data.getIntExtra("canal", -1);
                int voie = data.getIntExtra("voie", -1);

                int rectTop = data.getIntExtra("rectTop", -1);
                int rectLeft = data.getIntExtra("rectLeft", -1);
                if ((instrumentID >= 0) && (canal >= 0) && (voie >= 0) && (rectTop >= 0)&& (rectLeft >= 0)) {
                    addMusician(instrumentID);
                    new ToServer().execute("A", String.valueOf(numberOfMusicians - 1), String.valueOf(canal), String.valueOf(voie), String.valueOf(rectTop), String.valueOf(rectLeft));
                } else {
                    Toast.makeText(getApplicationContext(), "Une erreur est survenue, veuillez réessayer", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Ajout annulé", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Crée une nouvelle imageView correspondant à un musicien et initialise ses fonctions de replacement
     * @param instrumentID L'id de l'instrument
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
                if (isPlacing == 0) {
                    if (selectedImageView != null) {
                        selectedImageView.setBackgroundColor(Color.TRANSPARENT);
                    }
                    selectedImageView = musician;
                    musician.setBackgroundColor(Color.BLUE);
                }
            }
        });

        musician.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (selectedImageView != null) {
                    selectedImageView.setBackgroundColor(Color.TRANSPARENT);
                }
                selectedImageView = musician;
                musician.setBackgroundColor(Color.YELLOW);
                isPlacing = 2;
                return true;
            }
        });
        return musician;
    }

    /**
     * Crée le musicien à ajouter
     */
    public void addMusician(int instrumentID) {
        Musician musician = new Musician(numberOfMusicians, instrumentID);
        musicianList.put(numberOfMusicians, musician);

        ImageView musicianView = newMusicianView(instrumentID);
        musicianView.setTag(numberOfMusicians);
        if (selectedImageView != null) {
            selectedImageView.setBackgroundColor(Color.TRANSPARENT);
        }
        selectedImageView = musicianView;
        musicianView.setBackgroundColor(Color.BLUE);
        isPlacing = 1;

        Toast.makeText(getApplicationContext(), "Cliquez où vous voulez ajouter le musicien", Toast.LENGTH_SHORT).show();
        numberOfMusicians += 1;
        actualNumberOfMusicians += 1;
    }

    // La classe privée envoyant les commandes de contrôle de la caméra en boucle tant qu'on reste appuyé sur le bouton
    private class ServerLoop extends AsyncTask<String, String, Void>
    {

        @Override
        protected Void doInBackground(String... arg0) {
            String data;
            Socket skt = null;
            PrintWriter out = null;
            while (enable) {
                try {
                    data = ". ";
                    for (String command : arg0) {
                        data += command + " ";
                    }
                    data += ".";

                    skt = new Socket(ip, 1234);
                    out = new PrintWriter(skt.getOutputStream(), true);
                    publishProgress(data);
                    out.print(data);
                    out.close();
                    skt.close();
                } catch (Exception e) {
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
            }
            return null;
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
}
