package com.solanki.sahil.letschat;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;


public class StarterApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //Tlk6dC91PWrm

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        // Add your initialization code here
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("3ebe3baf88fa1acb72e76ba96a1f7078def4984e")
                .clientKey("73a2efc86bb6ce7da1ea79a3ac8c3ac4618a21fa")
                .server("http://18.219.132.8:80/parse/")
                .build()
        );




        //ParseUser.enableAutomaticUser();

        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

    }
}

