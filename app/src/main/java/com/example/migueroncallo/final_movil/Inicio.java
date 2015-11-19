package com.example.migueroncallo.final_movil;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class Inicio extends AppCompatActivity {


    SharedPreferences settings;

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String USER = "userKey";
    public static final String TYPE = "typeKey";
    int typeuser;
    String username,database,nombrecurso,idcurso;
    TextView welcome,nombreview,apellidoview, userview;
    Button verCurso, cerrarSesion,crearCurso;
    ArrayList values;
    boolean ready;
    ProgressDialog pDialog;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        ready=false;
        settings=getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        typeuser=settings.getInt(TYPE, 0);
        username=settings.getString(USER,"");

        cargar(typeuser);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inicio, menu);
        return true;
    }

    public void cargar(int i){
        RelativeLayout item = (RelativeLayout)findViewById(R.id.layoutbaby);
        item.removeAllViews();
        View child;
        switch (i){
            case 1:

                child=getLayoutInflater().inflate(R.layout.coordinador_inicio,null);
                item.addView(child);
                welcome= (TextView) findViewById(R.id.textView9);
                userview= (TextView) findViewById(R.id.textView11);
                nombreview=(TextView) findViewById(R.id.textView12);
                apellidoview= (TextView) findViewById(R.id.textView13);
                crearCurso=(Button)findViewById(R.id.buttonCrearCurso);
                cerrarSesion=(Button) findViewById(R.id.buttonCerrarSesCoord);
                verCurso=(Button)findViewById(R.id.buttonVerCursosHigh);
                welcome.setText(welcome.getText().toString() + ", Coordinador");
                database="high_user";
                new GetData().execute();
                userview.setText(userview.getText().toString() + " " + username);
                crearCurso.setVisibility(View.INVISIBLE);
                verCurso.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Inicio.this,ver_cursos.class);
                        startActivity(intent);
                    }
                });
                break;

            case 2:

                child=getLayoutInflater().inflate(R.layout.coordinador_inicio,null);
                item.addView(child);
                welcome= (TextView) findViewById(R.id.textView9);
                userview= (TextView) findViewById(R.id.textView11);
                nombreview=(TextView) findViewById(R.id.textView12);
                apellidoview= (TextView) findViewById(R.id.textView13);
                welcome.setText(welcome.getText().toString() + ", Profesor");
                crearCurso=(Button)findViewById(R.id.buttonCrearCurso);
                cerrarSesion=(Button) findViewById(R.id.buttonCerrarSesCoord);
                verCurso=(Button)findViewById(R.id.buttonVerCursosHigh);
                database="high_user";
                new GetData().execute();
                userview.setText(userview.getText().toString() + " " + username);
                crearCurso.setVisibility(View.VISIBLE);
                verCurso.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Inicio.this, ver_cursos.class);
                        startActivity(intent);
                    }
                });

                crearCurso.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final EditText className = new EditText(Inicio.this);
                        final EditText classId = new EditText(Inicio.this);
                        AlertDialog.Builder builder =new AlertDialog.Builder(Inicio.this);
                        builder.setTitle("Crear Curso");
                        builder.setMessage("Nombre del Curso");
                        builder.setView(className);
                        builder.setMessage("Id del curso");
                        builder.setView(classId);
                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                nombrecurso = className.getText().toString();
                                idcurso = classId.getText().toString();
                                new SenData().execute();
                            }
                        });
                        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        });
                        builder.show();
                    }
                });

                break;

            case 3:
                child=getLayoutInflater().inflate(R.layout.student_inicio,null);
                item.addView(child);
                userview=(TextView)findViewById(R.id.textView14);
                nombreview=(TextView)findViewById(R.id.textView15);
                apellidoview=(TextView)findViewById(R.id.textView16);
                imageView=(ImageView)findViewById(R.id.imageView);
                cerrarSesion=(Button) findViewById(R.id.buttonCloseStud);
                verCurso=(Button)findViewById(R.id.buttonVerClassSutd);
                crearCurso=(Button)findViewById(R.id.buttonAddClass);
                database="student";
                new GetData().execute();
                userview.setText(userview.getText().toString() + " " + username);
                verCurso.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent= new Intent(Inicio.this, ver_cursos.class);
                        startActivity(intent);
                    }
                });

            break;
        }
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


        }

        @Override
        protected Void doInBackground(Void... params) {
            // Create the array

            values = new ArrayList<String>();

                ParseQuery<ParseObject> query;
                query=ParseQuery.getQuery(database);
                query.whereEqualTo("username", username);
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        for (ParseObject dato : objects) {
                            values.add(dato.get("name"));
                            values.add(dato.get("apellido"));
                            nombreview.setText(nombreview.getText().toString() + " " + values.get(0));
                            apellidoview.setText(apellidoview.getText().toString() + " " + values.get(1));

                        }


                    }
                });


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

        }
    }

    private class SenData extends AsyncTask<Void,Void,Void>{
        protected Void doInBackground(Void... arg0){
            ParseObject test=new ParseObject("curso");
            test.put("id_curso",idcurso);
            test.put("name", nombrecurso);
            test.put("id_profesor",username);
            test.put("available", true);
            test.saveInBackground();
            return null;
        }
    }
}
