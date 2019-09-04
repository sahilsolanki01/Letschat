package com.solanki.sahil.letschat;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {

    public int counter=0;
    private final IBinder binder = new LocalBinder();
    private ServiceCallbacks serviceCallbacks;
    String activeUser = " ";
    ParseQuery<ParseObject> query;
    ChangeListerner changeListerner = new ChangeListerner();


    public class LocalBinder extends Binder {
        MyService getService() {
            return MyService.this;
        }
    }

    public MyService(Context applicationContext) {
        super();
        Log.i("HERE", "here I am!");
    }

    public MyService() {
    }



    public void getData()
    {


        query = new ParseQuery<ParseObject>("Message");
        query.whereEqualTo("receiver", ParseUser.getCurrentUser().getUsername());
        query.whereEqualTo("sender", activeUser);
        query.orderByAscending("createdAt");


        query.countInBackground(new CountCallback() {
            public void done(int count, ParseException e) {
                if (e == null) {
                    // The count request succeeded. Log the count
                    Log.d("score", "Sean has played " + count + " games");
                } else {
                    // The request failed
                }
            }
        });

    }

    @Override
    public void onCreate() {
        changeListerner.setOnIntegerChangeListener(new ChangeListerner.OnIntegerChangeListener() {
            @Override
            public void onIntegerChanged(int newValue)
            {
                if (serviceCallbacks != null) {
                    serviceCallbacks.doSomething();
                }
            }
        });

        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        activeUser = intent.getStringExtra("token");
        getData();
        startTimer();
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
        stoptimertask();
    }


    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;

    public void startTimer() {
        timer = new Timer();

        initializeTimerTask();

        timer.schedule(timerTask, 5000, 5000);
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.i("in timer", "in timer ++++  "+ (counter++));
                query.countInBackground(new CountCallback() {
                    public void done(int count, ParseException e) {
                        if (e == null) {
                            changeListerner.set(count);
                            Log.d("score", "Sean has played " + count + " games");

                        } else {
                            e.printStackTrace();
                        }
                    }
                });


            }
        };
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return  binder;
    }

    public void setCallbacks(ServiceCallbacks callbacks) {
        serviceCallbacks = callbacks;
    }

}
