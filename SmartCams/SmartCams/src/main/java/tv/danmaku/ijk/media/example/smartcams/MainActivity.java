package tv.danmaku.ijk.media.example.smartcams;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import tv.danmaku.ijk.media.example.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private String ip;
    private int resolutionWidth;
    private int resolutionHeight;
    private int cropWidth;
    private int cropHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("MainTestActivity", "Creating...");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);

        Log.i("MainTestActivity", "Starting...");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("MainTestActivity", "Pausing...");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("MainTestActivity", "Resuming...");
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onClick (View v) {
        ip = ((EditText) findViewById(R.id.ipText)).getText().toString();
        new ToServer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "G");
        new Client().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * La classe privée permettant d'envoyer les commandes au serveur
     */
    private class ToServer extends AsyncTask<String,Void, Void>
    {

        @Override
        protected void onProgressUpdate(Void... arg0) {
            super.onProgressUpdate();

            Toast.makeText(getApplicationContext(), "La connection a échoué", Toast.LENGTH_SHORT).show();
        }

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
                publishProgress();
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

    public void setResolutionCrop(int resolutionWidth, int resolutionHeight, int cropWidth, int cropHeight) {
        this.resolutionWidth = resolutionWidth;
        this.resolutionHeight = resolutionHeight;
        this.cropWidth = cropWidth;
        this.cropHeight = cropHeight;
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
                case "B":
                    setResolutionCrop(scan.nextInt(), scan.nextInt(), scan.nextInt(), scan.nextInt());
                    Intent optionActivity = new Intent(MainActivity.this, OptionsActivity.class);
                    optionActivity.putExtra("ip", ((EditText) findViewById(R.id.ipText)).getText().toString());
                    optionActivity.putExtra("resolutionWidth", resolutionWidth);
                    optionActivity.putExtra("resolutionHeight", resolutionHeight);
                    optionActivity.putExtra("cropWidth", cropWidth);
                    optionActivity.putExtra("cropHeight", cropHeight);
                    startActivity(optionActivity);
                    break;
                default:
                    Log.e(TAG, "Mauvaise commande");
                    Toast.makeText(getApplicationContext(), "La connection a échoué", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            String data;
            try {
                Socket skt = new Socket(ip, 3000);
                skt.setSoTimeout(5000);
                BufferedReader in = new BufferedReader(new InputStreamReader(skt.getInputStream()));
                Log.e(TAG, "Client - Starting");
                Log.e(TAG, "Client - Waiting for a command");
                data = in.readLine();
                Log.e(TAG, "Client - Command received : " + data);

                if (data.startsWith(".") && data.endsWith(".")) {
                    publishProgress(data.substring(2, data.length() - 2));
                } else {
                    Log.e(TAG, "Pas le bon format de string");
                }

            } catch (Exception e) {
                Log.e(TAG, "Problème de connection");
            }
            return null;
        }
    }
}
