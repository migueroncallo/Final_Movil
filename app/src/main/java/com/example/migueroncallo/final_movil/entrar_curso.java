package com.example.migueroncallo.final_movil;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class entrar_curso extends AppCompatActivity implements ViewAdapter.RecyclerClickListner {

    SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String USER = "userKey";
    public static final String TYPE = "typeKey";
    public static final String INTENCION = "intencionKey";
    public static final String CURSO = "cursoKey";
    public static final String IDKEY = "idKey";
    int usertype;
    String username,curso,idstring;
    List<ParseObject> ob;
    ArrayList values;
    private ViewAdapter viewAdapter;
    private RecyclerView mRecyclerView;
    List<Information>data;
    ArrayList<String> estudiantes;
    TextView tvid,tvnombre,tvteacher;
    boolean enabledclass;
    Button enable,corte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrar_curso);


        sharedPreferences=getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        username=sharedPreferences.getString(USER, "");
        usertype=sharedPreferences.getInt(TYPE, 0);
        curso=sharedPreferences.getString(CURSO, "");
        idstring=sharedPreferences.getString(IDKEY, "");
        mRecyclerView = (RecyclerView) findViewById(R.id.recycle2);

        data=new ArrayList<>();

        new GetData().execute();

        tvid=(TextView)findViewById(R.id.id_curso_home);
        tvnombre=(TextView)findViewById(R.id.nombre_curso_home);
        tvteacher=(TextView)findViewById(R.id.profesor_curso_home);
        tvid.setText(tvid.getText().toString()+" "+idstring);
        tvnombre.setText(tvnombre.getText().toString()+" "+curso);
        viewAdapter= new ViewAdapter(this,data);
        viewAdapter.setRecyclerClickListner(this);
        mRecyclerView.setAdapter(viewAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        enable=(Button)findViewById(R.id.enableButtoncurso);
        corte = (Button) findViewById(R.id.buttonAddCorte);

        switch (usertype){
            case 1:
                  enable.setVisibility(View.INVISIBLE);
                    corte.setVisibility(View.INVISIBLE);
                    break;
            case 2:
                enable.setVisibility(View.VISIBLE);
                corte.setVisibility(View.VISIBLE);
                enable.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new SenData().execute();
                        if (enabledclass) {
                            Toast.makeText(entrar_curso.this, "El curso ha sido deshabilitado", Toast.LENGTH_SHORT).show();
                            enabledclass = false;
                        } else {
                            Toast.makeText(entrar_curso.this, "El curso ha sido habilitado", Toast.LENGTH_SHORT).show();
                            enabledclass = true;

                        }
                    }
                });

                corte.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(enabledclass){
                            AlertDialog.Builder builder = new AlertDialog.Builder(entrar_curso.this);
                            builder.setTitle("Error");
                            builder.setMessage("El curso debe estar deshabilitado para agregar un corte");
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                }
                            });
                            builder.show();
                        }else{

                            AlertDialog.Builder builder = new AlertDialog.Builder(entrar_curso.this);
                            builder.setTitle("Agregar Corte");
                            builder.setMessage("Seguro que desea agregar un corte?");
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    new CreateCorte().execute();
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            builder.show();
                        }
                    }
                });


                break;
            case 3:
                enable.setVisibility(View.INVISIBLE);
                corte.setVisibility(View.INVISIBLE);
                break;
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_entrar_curso, menu);
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
//comment
            values = new ArrayList<String>();

            ArrayList<String> test=new ArrayList<>();
            try {
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("curso");
                ob = query.find();
                for (ParseObject dato : ob) {

                    if(dato.get("name").equals(curso)&& dato.get("id_curso").equals(idstring)){
                        values.add(dato.get("id_profesor"));
                        values.add(dato.get("available"));
                        if(dato.getJSONArray("student_id")!=null) {
                            int len=dato.getJSONArray("student_id").length();
                            for(int i=0;i<len;i++){
                                test.add(dato.getJSONArray("student_id").getString(i));
                            }
                        }
                    }
                }

                ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("student");
                ob = query2.find();
                for (ParseObject dato : ob) {
                    if(test.contains(dato.get("username"))){
                        values.add(dato.get("name")+" "+dato.get("apellido"));
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
            // Locate the listview in listview_main.xml
            // Pass the results into ListViewAdapter.java
            data.clear();

            tvteacher.setText(tvteacher.getText().toString()+" "+values.get(0).toString());

            for (int i=2; i<values.size();i++){

                Information info = new Information(values.get(i).toString());
                data.add(info);
            }

            enabledclass= (boolean) values.get(1);
            viewAdapter.notifyDataSetChanged();
        }
    }

    private class SenData extends AsyncTask<Void,Void,Void>{
        protected Void doInBackground(Void... arg0){
            try {
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("curso");
                ob = query.find();
                for (ParseObject dato : ob) {

                    if (dato.get("name").equals(curso) && dato.get("id_curso").equals(idstring)) {
                        if(dato.get("available").equals(true)) {
                            dato.put("available", false);
                            dato.saveInBackground();
                        }else{
                            dato.put("available", true);
                            dato.saveInBackground();
                        }
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class CreateCorte extends AsyncTask<Void,Void,Void>{
        protected Void doInBackground(Void... arg0){

            ArrayList<String> test=new ArrayList<>();
            try {
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("curso");
                ob = query.find();
                for (ParseObject dato : ob) {

                    if (dato.get("name").equals(curso) && dato.get("id_curso").equals(idstring)) {
                        if(dato.getJSONArray("student_id")!=null) {
                            int len=dato.getJSONArray("student_id").length();
                            for(int i=0;i<len;i++){
                                test.add(dato.getJSONArray("student_id").getString(i));
                            }
                        }

                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ParseObject corte=new ParseObject("rubrica");

            for(int i=0; i<test.size();i++) {

                corte.put("student_id", test.get(i).toString());
                corte.put("class_id", curso+idstring);

                corte.saveInBackground();


            }

            return null;
        }
    }



    @Override
    public void itemClick(View view, int position) {

        Toast.makeText(entrar_curso.this, values.get(position+2).toString(), Toast.LENGTH_SHORT).show();

    }
}
