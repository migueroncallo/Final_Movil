package com.example.migueroncallo.final_movil;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Spinner spinner1;
    Button signup;
    String usertype, user, password;
    int typeuser;
    List<ParseObject> ob;
    String database;
    ProgressDialog pDialog;
    boolean logged;
    ArrayList values;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String USER = "userKey";
    public static final String TYPE = "typeKey";
    SharedPreferences settings;

    private RelativeLayout mRoot;
    private TextInputLayout mEmailLayout;
    private TextInputLayout mPasswordLayout;
    private EditText mInputEmail;
    private EditText mInputPassword;
    private Toolbar mToolbar;


    private View.OnClickListener mSnackBarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);

        spinner1 = (Spinner) findViewById(R.id.spinner);
        spinner1.setSelection(2);
        signup = (Button) findViewById(R.id.signup_button);
        mRoot = (RelativeLayout) findViewById(R.id.mainActivity);
        mEmailLayout = (TextInputLayout) findViewById(R.id.email_layout);
        mPasswordLayout = (TextInputLayout) findViewById(R.id.password_layout);
        mInputEmail = (EditText) findViewById(R.id.input_email);
        mInputPassword = (EditText) findViewById(R.id.input_password);

        settings = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                usertype = spinner1.getItemAtPosition(position).toString();
                //Toast.makeText(MainActivity.this, usertype, Toast.LENGTH_LONG).show();

                if (usertype.contentEquals("Estudiante")) {
                    signup.setVisibility(View.VISIBLE);
                    typeuser = 3;
                    database = "student";
                } else {
                    signup.setVisibility(View.INVISIBLE);
                    if (usertype.contentEquals("Coordinador")) {
                        typeuser = 1;
                    } else {
                        typeuser = 2;
                    }
                    database = "high_user";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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


    public void submit(View view) {

        boolean isEmptyEmail = isEmptyEmail();
        boolean isEmptyPassword = isEmptyPassword();
        if (isEmptyEmail && isEmptyPassword) {
            Snackbar.make(mRoot, "Uno o mas campos estan vacios", Snackbar.LENGTH_SHORT)
                    .setAction("Forget", mSnackBarClickListener)
                    .show();
        } else if (isEmptyEmail && !isEmptyPassword) {
            mEmailLayout.setError("Usuario no puede estar vacio");
            mPasswordLayout.setError(null);
        } else if (!isEmptyEmail && isEmptyPassword) {
            mPasswordLayout.setError("Password no puede estar vacio");
            mEmailLayout.setError(null);
        } else {
            //All Good Here
            user = mInputEmail.getText().toString();
            password = mInputPassword.getText().toString();
            new GetData().execute();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        mInputEmail.requestFocus();
        mInputEmail.getText().clear();
        mInputPassword.getText().clear();
    }

    private boolean isEmptyEmail() {
        return mInputEmail.getText() == null
                || mInputEmail.getText().toString() == null
                || mInputEmail.getText().toString().isEmpty();
    }

    private boolean isEmptyPassword() {
        return mInputPassword.getText() == null
                || mInputPassword.getText().toString() == null
                || mInputPassword.getText().toString().isEmpty();
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
                    values.add(dato.get("username") + "" + dato.get("password"));
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
            if (values.contains(user + password)) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(USER, user);
                editor.putInt(TYPE, typeuser);
                editor.commit();

                Intent intent = new Intent(MainActivity.this, Inicio.class);
                startActivity(intent);
                // Close the progressdialog
                pDialog.dismiss();
            } else {
                pDialog.dismiss();
                Toast.makeText(MainActivity.this, "El usuario o contraseña es incorrecto", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
