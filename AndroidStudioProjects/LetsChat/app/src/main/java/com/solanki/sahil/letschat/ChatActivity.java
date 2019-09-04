package com.solanki.sahil.letschat;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.IBinder;
import android.support.v4.app.JobIntentService;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements ServiceCallbacks {
    String activeUser = "";
    private static final String TAG = "ChatActivity&#@*@*@*@*@";
    ArrayList<String> messages = new ArrayList<>();
    Button button;
    EditText editText;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private MyService myService;
    private boolean bound = false;
    Intent intent;
    ParseQuery<ParseObject> query;


    public void sendChat(View view) {
        final EditText editText = findViewById(R.id.editText);
        final String messageContent;

        String temp = editText.getText().toString();
        messageContent = "<--  " + temp;


        ParseObject message = new ParseObject("Message");
        message.put("sender", ParseUser.getCurrentUser().getUsername());
        message.put("receiver", activeUser);
        message.put("message", messageContent);
        editText.setText("");

        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null && messageContent.trim().length() != 0) {
                    messages.add(messageContent);
                    mAdapter.notifyDataSetChanged();

                } else if (messageContent.trim().length() == 0) {
                    button.setEnabled(false);
                }
            }
        });

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

    }

    public void getData() {

        ParseQuery<ParseObject> query1 = new ParseQuery<ParseObject>("Message");
        query1.whereEqualTo("sender", ParseUser.getCurrentUser().getUsername());
        query1.whereEqualTo("receiver", activeUser);

        ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("Message");
        query2.whereEqualTo("receiver", ParseUser.getCurrentUser().getUsername());
        query2.whereEqualTo("sender", activeUser);

        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
        queries.add(query1);
        queries.add(query2);

        query = ParseQuery.or(queries);

        query.orderByAscending("createdAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() > 0) {
                    messages.clear();
                    for (ParseObject message : objects) {
                        String temp = message.getString("message");
                        String messageContent = Normalizer.normalize(temp, Normalizer.Form.NFD).replaceAll("[^a-zA-Z0-9 ]", "");

                        if (message.getString("sender").equals(ParseUser.getCurrentUser().getUsername())) {
                            messageContent = "<--  " + messageContent;
                        } else {
                            messageContent = "-->  " + messageContent;
                        }

                        messages.add(messageContent);

                    }
                    mAdapter.notifyDataSetChanged();
                }
            }
        });

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        button = findViewById(R.id.button);
        editText = findViewById(R.id.editText);


        Intent intent = getIntent();
        activeUser = intent.getStringExtra("username");
        setTitle(activeUser);

        mRecyclerView = findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyRecyclerViewAdapter(messages);
        mRecyclerView.setAdapter(mAdapter);

        getData();

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if (s.length() == 0) {
                    button.setEnabled(false);
                    button.setTextColor(0xffaaaaaa);
                } else {
                    button.setTextColor(Color.parseColor("#00574B"));
                    button.setEnabled(true);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        ((MyRecyclerViewAdapter) mAdapter).setOnItemLongClickListener(new MyRecyclerViewAdapter.MyLongClickListener() {
            @Override
            public boolean onItemLongClick(final int position, View view) {

                String temp = messages.get(position);
                final String deleteMessage = Normalizer.normalize(temp, Normalizer.Form.NFD).replaceAll("[^a-zA-Z0-9 ]", "");


                new AlertDialog.Builder(ChatActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Are You Sure?")
                        .setMessage("Are You Sure You Want To Delete This Message?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                messages.remove(position);
                                mAdapter.notifyDataSetChanged();

                                ParseQuery<ParseObject> query1 = new ParseQuery<>("Message");
                                query1.whereEqualTo("message", deleteMessage);
                                query1.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> objects, ParseException e) {
                                        Toast.makeText(ChatActivity.this, "1", Toast.LENGTH_SHORT).show();
                                        if (e == null && objects.size() > 0) {
                                            Toast.makeText(ChatActivity.this, "1", Toast.LENGTH_SHORT).show();
                                            for (ParseObject message : objects) {
                                                try {
                                                    message.delete();

                                                } catch (Exception em) {
                                                    em.getMessage();
                                                }

                                            }
                                        }
                                    }
                                });


                            }
                        }).setNegativeButton("NO", null)
                        .show();
                return true;

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        intent = new Intent(ChatActivity.this, MyService.class);
        intent.putExtra("token", activeUser);
        startService(intent);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {

            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            myService = binder.getService();
            bound = true;
            myService.setCallbacks(ChatActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };


    @Override
    public void doSomething() {
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() > 0) {
                    messages.clear();
                    for (ParseObject message : objects) {
                        String temp = message.getString("message");
                        String messageContent = Normalizer.normalize(temp, Normalizer.Form.NFD).replaceAll("[^a-zA-Z0-9 ]", "");

                        if (message.getString("sender").equals(ParseUser.getCurrentUser().getUsername())) {
                            messageContent = "<--  " + messageContent;
                        } else {
                            messageContent = "-->  " + messageContent;
                        }

                        messages.add(messageContent);

                    }
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        stopService(intent);
        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (bound) {
            myService.setCallbacks(null); // unregister
            unbindService(serviceConnection);
            bound = false;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<String> tempList = new ArrayList();

                for (String temp : messages) {
                    if (temp.toLowerCase().contains(newText.toLowerCase())) {
                        tempList.add(temp);
                    }
                }
                RecyclerView.Adapter adapter = new MyRecyclerViewAdapter(tempList);
                mRecyclerView.setAdapter(adapter);

                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

        });

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.clear) {
            messages.clear();
            mAdapter.notifyDataSetChanged();

            ParseQuery<ParseObject> query1 = new ParseQuery<ParseObject>("Message");
            query1.whereEqualTo("sender", ParseUser.getCurrentUser().getUsername());
            query1.whereEqualTo("receiver", activeUser);

            ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("Message");
            query2.whereEqualTo("receiver", ParseUser.getCurrentUser().getUsername());
            query2.whereEqualTo("sender", activeUser);

            List<ParseQuery<ParseObject>> queries = new ArrayList<>();
            queries.add(query1);
            queries.add(query2);

            ParseQuery<ParseObject> query = ParseQuery.or(queries);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null && objects.size() > 0) {
                        for (ParseObject message : objects) {
                            try {
                                message.delete();
                            } catch (Exception em) {
                                em.getMessage();
                            }
                        }

                    }
                }
            });


        } else if (item.getItemId() == R.id.help) {
            Intent intent = new Intent(ChatActivity.this, HelpActivity.class);
            startActivity(intent);
        }


        return super.onOptionsItemSelected(item);
    }




   /* @Override
    protected void onResume() {
        super.onResume();
        // Register for the particular broadcast based on ACTION string
        IntentFilter filter = new IntentFilter(Custom_Service.ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(testReceiver, filter);
        // or `registerReceiver(testReceiver, filter)` for a normal broadcast
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener when the application is paused
        LocalBroadcastManager.getInstance(this).unregisterReceiver(testReceiver);
        // or `unregisterReceiver(testReceiver)` for a normal broadcast
    }

    // Define the callback for what to do when data is received
    private BroadcastReceiver testReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int resultCode = intent.getIntExtra("resultCode", RESULT_CANCELED);
            if (resultCode == RESULT_OK) {
                String resultValue = intent.getStringExtra("resultValue");
                Toast.makeText(ChatActivity.this, resultValue, Toast.LENGTH_SHORT).show();
            }
        }
    };*/


}

