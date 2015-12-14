package com.uninorte.migueroncallo.rotacion;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class RubricasActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String USER = "userKey";
    public static final String TYPE = "typeKey";
    public static final String INTENCION = "intencionKey";
    public static final String CURSO = "cursoKey";
    public static final String IDKEY = "idKey";
    public static final String STUDIDKEY = "studidkey";
    String username, curso, idstring, idstud,cursoid;
    int usertype;


    float nota;
    int tipo;

    TextView codstud, nomstud;
    TextView bigText;
    Button agregarcorte,calculardefinitiva;

    private Toolbar mToolbar;
    List<ParseObject> ob;
    ArrayList values;
    private Context mContext;
    private String TAG = "RubricasActivityTest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rubricas);

        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);

        bigText = (TextView) findViewById(R.id.editTextNota);
        codstud = (TextView) findViewById(R.id.codstudprof);
        nomstud = (TextView) findViewById(R.id.nomstudprof);
        agregarcorte = (Button) findViewById(R.id.addnotabutton);
        calculardefinitiva = (Button) findViewById(R.id.definitivabutton);

        mContext = this;

        sharedPreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        curso = sharedPreferences.getString(CURSO, "");
        idstring = sharedPreferences.getString(IDKEY, "");
        idstud = sharedPreferences.getString(STUDIDKEY, "");
        usertype= sharedPreferences.getInt(TYPE, 0);
        codstud = (TextView) findViewById(R.id.codstudprof);
        nomstud = (TextView) findViewById(R.id.nomstudprof);

        codstud.setText(codstud.getText().toString()+" "+idstud);
        cursoid=curso+idstring;

        new GetNotasActuales().execute();


        switch (usertype){
            case 1:
                agregarcorte.setVisibility(View.INVISIBLE);
                calculardefinitiva.setVisibility(View.INVISIBLE);
                break;
            case 2:
                calculardefinitiva.setVisibility(View.VISIBLE);
                agregarcorte.setVisibility(View.VISIBLE);
                agregarcorte.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LayoutInflater li = LayoutInflater.from(mContext);
                        View promptsView = li.inflate(R.layout.notas_promt, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RubricasActivity.this);
                        alertDialogBuilder.setView(promptsView);
                        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextNota);
                        final Spinner spinner = (Spinner) promptsView.findViewById(R.id.spinnerNota);


                        alertDialogBuilder.setCancelable(false)
                                .setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //conoc_base= Float.parseFloat(userInput.getText().toString());
                                        //conoc_clin= Float.parseFloat(userInput2.getText().toString());
                                        //his_clin= Float.parseFloat(userInput3.getText().toString());
                                        //rondas= Float.parseFloat(userInput4.getText().toString());
                                        //orales= Float.parseFloat(userInput5.getText().toString());
                                        //finalexamen = Float.parseFloat(userInput6.getText().toString());
                                        //total = conoc_base*0.1f+conoc_clin*0.15f+his_clin*0.15f+rondas*0.15f+orales*0.15f+finalexamen*0.30f;
                                        nota = Float.parseFloat(userInput.getText().toString());
                                        tipo =  spinner.getSelectedItemPosition();
                                        new SenData().execute();
                                        //new GetData().execute();

                                    }
                                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();


                    }
                });
                break;
            case 3:
                calculardefinitiva.setVisibility(View.INVISIBLE);
                agregarcorte.setVisibility(View.INVISIBLE);
                break;
        }
    }


    private class GetNotasActuales extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog

        }

        @Override
        protected Void doInBackground(Void... params) {
            // Create the array

            values = new ArrayList<String>();

            try {
                ParseQuery<ParseObject> query = new ParseQuery<>("student");
                ob = query.find();
                for (ParseObject dato : ob) {
                    if (idstud.equals(dato.get("username"))) {
                        //values.add(dato.get("name"));
                        //values.add(dato.get("apellido"));
                        username = dato.get("name") + " " + dato.get("apellido");
                    }
                }
            } catch (com.parse.ParseException e) {
                e.printStackTrace();
            }

            try {
                ParseQuery<ParseObject> query = new ParseQuery<>("rubricaind");
                ob=query.find();
                for (ParseObject dato:ob){
                    if(idstud.equals(dato.get("student_id"))&&cursoid.equals(dato.get("class_id"))){
                        Log.d(TAG,"idx "+dato.get("type").toString()+"   "+Integer.parseInt(dato.get("type").toString()));
                        values.add(getResources().getStringArray(R.array.notas_arrays)[Integer.parseInt(dato.get("type").toString())]+
                                        "\nNota: "+dato.get("nota").toString()
                                        +"\nFecha: "+dato.getCreatedAt().toString()
                                );
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }


            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            // Locate the listview in listview_main.xml
            // Pass the results into ListViewAdapter.java

            String text = "";

            Log.d(TAG, "GetNotasActuales onPostExecute size " + values.size());
            nomstud.setText(username);

            if (values.size() == 0){
                bigText.setText("No hay notas");
            } else {
                for (int i = 0; i < values.size(); i++) {

                    text = text+values.get(i).toString() + "\n";
                }
                bigText.setText(text);
            }

        }
    }

    private class SenData extends AsyncTask<Void,Void,Void>{
        protected Void doInBackground(Void... arg0){
            ParseObject test=new ParseObject("rubricaind");
            test.put("class_id",cursoid);
            test.put("student_id", idstud);
            test.put("type",tipo);
            test.put("nota", nota);
            test.saveInBackground();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            new GetNotasActuales().execute();
        }
    }
}
