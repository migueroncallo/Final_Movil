package com.example.migueroncallo.final_movil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.migueroncallo.final_movil.ViewAdapter.RecyclerClickListner;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ver_cursos extends AppCompatActivity implements ViewAdapter.RecyclerClickListner{

    private ViewAdapter viewAdapter;
    private RecyclerView recyclerView;
    List <ParseObject> ob;
    ArrayList values;
    List<Information>data;
    SharedPreferences sharedPreferences;
    int usertype;
    String username;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String USER = "userKey";
    public static final String TYPE = "typeKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_cursos);

        recyclerView = (RecyclerView) findViewById(R.id.recycle);
        sharedPreferences= getSharedPreferences(MyPREFERENCES,MODE_PRIVATE);
        usertype=sharedPreferences.getInt(TYPE, 0);
        username=sharedPreferences.getString(USER,"");

        data=new ArrayList<>();

        new GetData().execute();

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

        Toast toast= Toast.makeText(this,"Esta es la opcion"+position,Toast.LENGTH_SHORT);
        toast.show();
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

            try {
                ParseQuery<ParseObject> query = new ParseQuery<>("curso");
                ob = query.find();
                for (ParseObject dato : ob) {
                    ArrayList<String> list = new ArrayList<String>();
                    if(dato.getJSONArray("student_id")!=null){
                        int len = dato.getJSONArray("student_id").length();
                        for (int i=0;i<len;i++){
                            list.add(dato.getJSONArray("student_id").getString(i));
                        }


                    }
                    switch (usertype) {
                        case 1:
                        values.add("Curso: " + dato.get("name") + "   \nProfesor: " + dato.get("id_profesor"));
                            break;
                        case 2:
                            if(dato.get("id_profesor").equals(username)){
                                values.add("Curso: " + dato.get("name") + "   \nProfesor: " + dato.get("id_profesor"));
                            }
                            break;
                        case 3:
                            if(list.contains(username)){
                                values.add("Curso: " + dato.get("name") + "   \nProfesor: " + dato.get("id_profesor"));
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

            for (int i=0; i<values.size();i++){

                Information info = new Information(values.get(i).toString());
                data.add(info);
            }

            viewAdapter=new ViewAdapter(ver_cursos.this, data);
            viewAdapter.setRecyclerClickListner(ver_cursos.this);
            recyclerView.setAdapter(viewAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(ver_cursos.this));

        }
    }
}
