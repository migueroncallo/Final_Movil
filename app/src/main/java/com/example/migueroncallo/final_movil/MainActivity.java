package com.example.migueroncallo.final_movil;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Spinner spinner1;
    EditText usernametext, passwordtext;
    Button login, signup;
    String usertype,user,password;
    int typeuser;
    List <ParseObject> ob;
    String database;
    ProgressDialog pDialog;
    boolean logged;
    ArrayList values;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String USER = "userKey";
    public static final String TYPE = "typeKey";
    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spinner1= (Spinner)findViewById(R.id.spinner);
        usernametext = (EditText) findViewById(R.id.username);
        passwordtext = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login_button);
        signup = (Button) findViewById(R.id.signup_button);

        settings = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                usertype = spinner1.getItemAtPosition(position).toString();
                Toast.makeText(MainActivity.this, usertype, Toast.LENGTH_LONG).show();

                if (usertype.contentEquals("Estudiante")){
                    signup.setVisibility(View.VISIBLE);
                    typeuser=3;
                    database="student";
                }else{
                    signup.setVisibility(View.INVISIBLE);
                    if (usertype.contentEquals("Coordinador")){
                        typeuser=1;
                    }else{
                        typeuser=2;
                    }
                    database="high_user";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = usernametext.getText().toString();
                password = passwordtext.getText().toString();
                new GetData().execute();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Sign_up.class);
                startActivity(intent);
            }
        });



    }
    private class GetData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            pDialog = new ProgressDialog(MainActivity.this);
            // Set progressdialog title
            pDialog.setTitle("Conectando");
            // Set progressdialog message
            pDialog.setMessage("Logging in...");
            pDialog.setIndeterminate(false);
            // Show progressdialog
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Create the array

            values = new ArrayList<String>();

            try {
                ParseQuery<ParseObject> query = new ParseQuery<>(database);
                ob = query.find();
                for (ParseObject dato : ob) {
                    values.add(dato.get("username")+""+dato.get("password"));
                }
            } catch (com.parse.ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Locate the listview in listview_main.xml
            // Pass the results into ListViewAdapter.java
            if (values.contains(user+password)) {

                SharedPreferences.Editor editor= settings.edit();
                editor.putString(USER, user);
                editor.putInt(TYPE,typeuser);
                editor.commit();

                Intent intent = new Intent(MainActivity.this,Inicio.class);
                startActivity(intent);
                // Close the progressdialog
                pDialog.dismiss();
            }else{
                pDialog.dismiss();
                Toast.makeText(MainActivity.this, "El usuario o contraseña es incorrecto", Toast.LENGTH_SHORT).show();


            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

}
