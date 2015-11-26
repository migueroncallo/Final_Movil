package com.example.migueroncallo.final_movil;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class perfil_estud extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String USER = "userKey";
    public static final String TYPE = "typeKey";
    public static final String INTENCION = "intencionKey";
    public static final String CURSO = "cursoKey";
    public static final String IDKEY = "idKey";
    public static final String STUDIDKEY = "studidkey";
    String username, curso, idstring, idstud,cursoid;
    List<Information> data;
    int usertype;
    TextView codstud, nomstud, lastnstud;
    Button agregarcorte;
    private RecyclerView recyclerView;
    ArrayList values;
    List<ParseObject> ob;
    private ViewAdapter viewAdapter;
    Context context;
    float conoc_base,conoc_clin,his_clin,rondas,orales,finalexamen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_estud);

        sharedPreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        curso = sharedPreferences.getString(CURSO, "");
        idstring = sharedPreferences.getString(IDKEY, "");
        idstud = sharedPreferences.getString(STUDIDKEY, "");
        usertype= sharedPreferences.getInt(TYPE,0);
        codstud = (TextView) findViewById(R.id.codstudprof);
        nomstud = (TextView) findViewById(R.id.nomstudprof);
        lastnstud = (TextView) findViewById(R.id.lastnamestudprof);
        agregarcorte = (Button) findViewById(R.id.addcortebutton);
        recyclerView = (RecyclerView) findViewById(R.id.recyclenotas);
        codstud.setText(codstud.getText().toString()+" "+idstud);
        cursoid=curso+idstring;
        context=this;


        data = new ArrayList<>();

        new GetData().execute();

        viewAdapter = new ViewAdapter(this,data);
        recyclerView.setAdapter(viewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        switch (usertype){
            case 1:
                agregarcorte.setVisibility(View.INVISIBLE);
                break;
            case 2:
                agregarcorte.setVisibility(View.VISIBLE);
                agregarcorte.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LayoutInflater li = LayoutInflater.from(context);
                        View promptsView = li.inflate(R.layout.rubrica_prompt, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(perfil_estud.this);
                        alertDialogBuilder.setView(promptsView);
                        final EditText userInput = (EditText) promptsView.findViewById(R.id.conocbaseedit);
                        final EditText userInput2 = (EditText) promptsView.findViewById(R.id.conocclicedit);
                        final EditText userInput3 = (EditText) promptsView.findViewById(R.id.HisClinedit);
                        final EditText userInput4 = (EditText) promptsView.findViewById(R.id.rondmededit);
                        final EditText userInput5 = (EditText) promptsView.findViewById(R.id.pregoralesedit);
                        final EditText userInput6 = (EditText) promptsView.findViewById(R.id.finaledit);

                        alertDialogBuilder.setCancelable(false)
                                .setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        conoc_base= Float.parseFloat(userInput.getText().toString());
                                        conoc_clin= Float.parseFloat(userInput2.getText().toString());
                                        his_clin= Float.parseFloat(userInput3.getText().toString());
                                        rondas= Float.parseFloat(userInput4.getText().toString());
                                        orales= Float.parseFloat(userInput5.getText().toString());
                                        finalexamen = Float.parseFloat(userInput6.getText().toString());

                                        new SenData().execute();
                                        new GetData().execute();

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
                agregarcorte.setVisibility(View.INVISIBLE);
                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_perfil_estud, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class GetData extends AsyncTask<Void, Void, Void> {
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

                        values.add(dato.get("name"));
                        values.add(dato.get("apellido"));
                    }
                }
            } catch (com.parse.ParseException e) {
                e.printStackTrace();
            }

            try {
                ParseQuery<ParseObject> query = new ParseQuery<>("rubrica");
                ob=query.find();
                for (ParseObject dato:ob){
                    if(idstud.equals(dato.get("student_id"))&&cursoid.equals(dato.get("class_id"))){
                        values.add("Conocimientos basicos: "+dato.get("conoc_base").toString()+"\nConocimientos clinicos: "+dato.get("conoc_clin").toString()
                        +"\nHistoria clinica: "+dato.get("his_clin").toString()+"\nRondas Medicas: "+dato.get("rondas").toString()
                        +"\nPreguntas Orales: "+dato.get("orales").toString()+"\nExamen Final: "+dato.get("final").toString());
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
            data.clear();

            nomstud.setText(nomstud.getText().toString() + " " + values.get(0).toString());
            lastnstud.setText(lastnstud.getText().toString()+" "+values.get(1).toString());

            for (int i=2; i<values.size();i++){

                Information info = new Information(values.get(i).toString());
                data.add(info);
            }

            viewAdapter.notifyDataSetChanged();
        }
    }

    private class SenData extends AsyncTask<Void,Void,Void>{
        protected Void doInBackground(Void... arg0){
            ParseObject test=new ParseObject("rubrica");
            test.put("class_id",cursoid);
            test.put("student_id", idstud);
            test.put("conoc_base", conoc_base);
            test.put("conoc_clin", conoc_clin);
            test.put("his_clin",his_clin);
            test.put("rondas", rondas);
            test.put("orales",orales);
            test.put("final", finalexamen);
            test.saveInBackground();

            return null;
        }
    }
}
