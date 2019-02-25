package tv.danmaku.ijk.media.example.smartcams;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import tv.danmaku.ijk.media.example.R;

public class AddMusicianActivity extends AppCompatActivity {

    int instrumentID = 0;
    private static final String TAG = "AddMusicianActivity";
    private static final int GET_NEW_CADRE = 666;
    private int rectTop = -1;
    private int rectLeft = -1;
    private String ip;
    private int resolutionWidth;
    private int resolutionHeight;
    private int cropWidth;
    private int cropHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_musician);

        // Recupere l'ip de l'intent
        Intent intent = getIntent();
        ip = intent.getStringExtra("ip");
        resolutionWidth = intent.getIntExtra("resolutionWidth", 3840);
        resolutionHeight = intent.getIntExtra("resolutionHeight", 2160);
        cropWidth = intent.getIntExtra("cropWidth", 1920);
        cropHeight = intent.getIntExtra("cropHeight", 1080);

        // Met en place le menu déroulant pour choisir l'instrument
        final Spinner instrument = (Spinner) findViewById(R.id.instrument);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.instruments_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        instrument.setAdapter(adapter);

        instrument.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                instrumentID = (int) id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Met en place le champ de texte pour le canal DMX
        final EditText canal = (EditText) findViewById(R.id.canal);
        final EditText voie = (EditText) findViewById(R.id.voie);
        canal.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "512")});
        voie.setFilters(new InputFilter[] {new InputFilterMinMax("1", "8")});

        // Met en place le bouton de choix du cadre
        Button selectionCadreButton = (Button) findViewById(R.id.selectionCadreButton);
        selectionCadreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddMusicianActivity.this, CadreSelectActivity.class);
                intent.putExtra("ip", ip);
                intent.putExtra("resolutionWidth", resolutionWidth);
                intent.putExtra("resolutionHeight", resolutionHeight);
                intent.putExtra("cropWidth", cropWidth);
                intent.putExtra("cropHeight", cropHeight);
                startActivityForResult(intent, GET_NEW_CADRE);

            }
        });

        // Met en place le bouton OK
        Button addMusicianButton = (Button) findViewById(R.id.addMusicianButton);
        addMusicianButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String canalDMX = canal.getText().toString();
                String voieString = voie.getText().toString();
                int erreur = 0;
                if (canalDMX.equals("")) {
                    erreur = 1;
                }
                if (voieString.equals("")) {
                    erreur = 2;
                }
                if ((rectTop < 0) || (rectLeft < 0)) {
                    erreur = 3;
                }
                switch (erreur) {
                    case 0:
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("instrument", instrumentID);
                        returnIntent.putExtra("canal", Integer.parseInt(canalDMX));
                        returnIntent.putExtra("voie", Integer.parseInt(voieString));
                        returnIntent.putExtra("rectTop", rectTop);
                        returnIntent.putExtra("rectLeft", rectLeft);
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                        break;
                    case 1:
                        Toast.makeText(getApplicationContext(), "Veuillez préciser un canal DMX valide", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(getApplicationContext(), "Veuillez préciser une voie valide", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Toast.makeText(getApplicationContext(), "Veuillez préciser un cadre et un point valides", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_NEW_CADRE) {
            if (resultCode == Activity.RESULT_OK) {
                int rectTopWannabe = data.getIntExtra("rectTop", -1);
                int rectLeftWannabe = data.getIntExtra("rectLeft", -1);
                if ((rectTopWannabe >= 0) && (rectLeftWannabe >= 0)) {
                    rectTop = rectTopWannabe;
                    rectLeft = rectLeftWannabe;
                } else {
                    Toast.makeText(getApplicationContext(), "Une erreur est survenue, veuillez réessayer", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Nouveau cadre annulé", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
