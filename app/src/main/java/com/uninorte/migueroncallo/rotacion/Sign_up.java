package com.uninorte.migueroncallo.rotacion;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class Sign_up extends AppCompatActivity {
    EditText codigo,nombre,apellido,clave,confirmar;
    String code,name,lastname,password,conf_password;
    Button registrar;
    Boolean check;
    ArrayList values;
    List<ParseObject> ob;
    int type;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);


        codigo = (EditText) findViewById(R.id.student_code);
        nombre = (EditText) findViewById(R.id.student_name);
        apellido = (EditText) findViewById(R.id.student_lastname);
        clave = (EditText) findViewById(R.id.student_password);
        confirmar = (EditText) findViewById(R.id.password_confirm);
        registrar = (Button) findViewById(R.id.confirm_button);
        check=false;
        type=3;

        new GetData().execute();






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
                    values.add(dato.get("username"));
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


                // Close the progressdialog

        }
    }

    public void validate(View v){
        code=codigo.getText().toString();
        name = nombre.getText().toString();
        lastname = apellido.getText().toString();
        password=clave.getText().toString();
        conf_password=confirmar.getText().toString();

        if(code.isEmpty()||name.isEmpty()||lastname.isEmpty()||password.isEmpty()||conf_password.isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error");
            builder.setMessage("Todos los campos son necesarios.");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
            builder.show();

        }else{
            if(!password.equals(conf_password)){

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Error");
                builder.setMessage("La contraseña no es igual");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                builder.show();
            }else{
                if(values.contains(code)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Error");
                    builder.setMessage("El código de estudiante ya eixste.");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    });
                    builder.show();

                }else{
                    new SenData().execute();
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Operación exitosa.");
                    builder.setMessage("El usuario fue creado correctamente");
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            finish();
                        }
                    });
                    builder.show();
                }
            }
        }

    }

    private class SenData extends AsyncTask<Void,Void,Void>{
        protected Void doInBackground(Void... arg0){
            ParseObject test=new ParseObject("student");
            test.put("username",code);
            test.put("name",name);
            test.put("apellido", lastname);
            test.put("password",password);
            test.put("type",type);
            test.saveInBackground();
            return null;
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
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
