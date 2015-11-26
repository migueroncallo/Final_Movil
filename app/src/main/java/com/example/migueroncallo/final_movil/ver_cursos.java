package com.example.migueroncallo.final_movil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ver_cursos extends AppCompatActivity implements ViewAdapter.RecyclerClickListner {

    private ViewAdapter viewAdapter;
    private RecyclerView mRecyclerView;
    List<ParseObject> ob;
    ArrayList values, nombrecursos, idcursos;
    List<Information> data;
    SharedPreferences sharedPreferences;
    int usertype;
    String username;
    public static final String STUDIDKEY="studidkey";
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String USER = "userKey";
    public static final String TYPE = "typeKey";
    public static final String INTENCION = "intencionKey";
    public static final String CURSO = "cursoKey";
    public static final String IDKEY = "idKey";
    boolean error;
    int intencion;
    String titulo, claseid;
    private String TAG = "VerCursosTest";
    private Button buttonAdd;
    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_cursos);
        error = false;
        mRecyclerView = (RecyclerView) findViewById(R.id.recycle);
        sharedPreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        usertype = sharedPreferences.getInt(TYPE, 0);
        username = sharedPreferences.getString(USER, "");
        intencion = sharedPreferences.getInt(INTENCION, 0);
        buttonAdd = (Button) findViewById(R.id.buttonAddClass);

        data = new ArrayList<>();

        new GetData().execute();

        viewAdapter = new ViewAdapter(this, data);
        viewAdapter.setRecyclerClickListner(this);
        mRecyclerView.setAdapter(viewAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Log.d(TAG,"usertype "+usertype+" intencion "+intencion);

        if (usertype != 3){
            buttonAdd.setVisibility(View.GONE);

        } else {
            buttonAdd.setVisibility(View.VISIBLE);
            if (intencion == 2) {
                TextView titulo = (TextView) findViewById(R.id.textView17);
                titulo.setText("Cursos Disponibles");
                //buttonAdd.setVisibility(View.GONE);
                buttonAdd.setText("OK");
            } else {

                TextView titulo = (TextView) findViewById(R.id.textView17);
                titulo.setText("Cursos Matriculados");
                buttonAdd.setText("+");
                buttonAdd.setVisibility(View.VISIBLE);
            }
        }

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intencion != 2)
                    intencion = 2;
                else
                    intencion = 1;
                settings=getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
                SharedPreferences.Editor editor= settings.edit();
                editor.putInt(INTENCION,intencion);
                editor.commit();
                //Intent intent = getIntent();
                //finish();
                //startActivity(intent);
                //recreate();
                //Intent intent = new Intent(this, ver_cursos.class);
                //startActivity(intent);

                if (Build.VERSION.SDK_INT >= 11) {
                    recreate();
                } else {
                    Intent intent = getIntent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    finish();
                    overridePendingTransition(0, 0);

                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ver_cursos, menu);
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

    @Override
    public void itemClick(View view, int position) {


        titulo = nombrecursos.get(position).toString();
        claseid = idcursos.get(position).toString();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CURSO, titulo);
        editor.putString(IDKEY, claseid);
        editor.putString(STUDIDKEY, username);
        editor.commit();

        if (usertype == 3) {
            if (intencion == 2) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Agregar");
                builder.setMessage("Desea unirse a este curso?");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        new Matricula().execute();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            } else {
                //SharedPreferences.Editor editor = sharedPreferences.edit();
                //editor.putString(STUDIDKEY, username);
                //editor.commit();

                Intent intent = new Intent(ver_cursos.this, perfil_estud.class);
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(ver_cursos.this, entrar_curso.class);
            startActivity(intent);
        }

        Toast.makeText(ver_cursos.this, titulo + " " + claseid, Toast.LENGTH_SHORT).show();
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
            nombrecursos = new ArrayList<String>();
            idcursos = new ArrayList<String>();

            try {
                ParseQuery<ParseObject> query = new ParseQuery<>("curso");
                ob = query.find();
                for (ParseObject dato : ob) {
                    ArrayList<String> list = new ArrayList<String>();

                    switch (usertype) {
                        case 1:
                            values.add("Curso: " + dato.get("name") + "\nId: " + dato.get("id_curso") + "\nProfesor: " + dato.get("id_profesor"));
                            nombrecursos.add(dato.get("name"));
                            idcursos.add(dato.get("id_curso"));
                            break;
                        case 2:
                            if (dato.get("id_profesor").equals(username)) {
                                values.add("Curso: " + dato.get("name") + "\nId: " + dato.get("id_curso") + "\nProfesor: " + dato.get("id_profesor"));
                                nombrecursos.add(dato.get("name"));
                                idcursos.add(dato.get("id_curso"));
                            }
                            break;
                        case 3:
                            switch (intencion) {
                                case 1:
                                    if (dato.getJSONArray("student_id") != null) {
                                        int len = dato.getJSONArray("student_id").length();
                                        Log.d(TAG, "Filtro por estudiante " + username);
                                        for (int i = 0; i < len; i++) {
                                            if (dato.getJSONArray("student_id").getString(i).contains(username)) {
                                                values.add("Curso: " + dato.get("name") + "\nId: " + dato.get("id_curso") + "\nProfesor: " + dato.get("id_profesor"));
                                                nombrecursos.add(dato.get("name"));
                                                idcursos.add(dato.get("id_curso"));

                                            }
                                        }
                                    }


                                    break;
                                case 2:
                                    if (dato.getBoolean("available")) {
                                        values.add("Curso: " + dato.get("name") + "\nId: " + dato.get("id_curso") + "\nProfesor: " + dato.get("id_profesor"));
                                        nombrecursos.add(dato.get("name"));
                                        idcursos.add(dato.get("id_curso"));
                                    }
                            }
                            break;
                    }
                }
            } catch (com.parse.ParseException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            data.clear();

            for (int i = 0; i < values.size(); i++) {

                Information info = new Information(values.get(i).toString());
                data.add(info);
            }

            viewAdapter.notifyDataSetChanged();


        }
    }

    private class Matricula extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            values = new ArrayList<String>();
            ParseQuery<ParseObject> query;
            query = ParseQuery.getQuery("curso");
            query.whereEqualTo("name", titulo);
            query.whereEqualTo("id_curso", claseid);
            try {
                ob = query.find();

                Log.d(TAG, "dato l " + ob.size());
                for (ParseObject dato : ob) {
                    ArrayList<String> list = new ArrayList<String>();
                    if (dato.getJSONArray("student_id") != null) {
                        int len = dato.getJSONArray("student_id").length();
                        for (int i = 0; i < len; i++) {
                            try {
                                list.add(dato.getJSONArray("student_id").getString(i));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    if (list.contains(username)) {

                        error = true;
                    } else {
                        dato.add("student_id", username);
                        dato.saveInBackground();
                        error = false;
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
            if (error) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ver_cursos.this);
                builder.setTitle("Error");
                builder.setMessage("El estudiante ya hace parte de este curso");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                builder.show();
            } else {
                new GetData().execute();
            }

        }
    }
}
