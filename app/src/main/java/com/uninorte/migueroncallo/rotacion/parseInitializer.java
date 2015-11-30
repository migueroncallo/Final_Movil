package com.uninorte.migueroncallo.rotacion;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by migueroncallo on 11/17/15.
 */
public class parseInitializer extends Application{
    @Override
    public void onCreate(){
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this,"9W1aJHLZmfP4uFYcEQTMl0cVJGtQ3K0AilUrW45S", "cQbM5paB853qoPETGrNG8Gd4vI10aaUHwYOSlpS6");
    }

}
