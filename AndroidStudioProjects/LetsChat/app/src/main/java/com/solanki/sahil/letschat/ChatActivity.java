package com.solanki.sahil.letschat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    String activeUser = "";
    private static final String TAG = "ChatActivity";
    ArrayList<String> messages = new ArrayList<>();
    ArrayAdapter adapter;
    ListView listView;
    Button button;
    EditText editText;



    public void sendChat(View view) {
        final EditText editText = findViewById(R.id.editText);
        final String messageContent;

        messageContent = editText.getText().toString();


        ParseObject message = new ParseObject("Message");
        message.put("sender", ParseUser.getCurrentUser().getUsername());
        message.put("receiver", activeUser);
        message.put("message", messageContent);
        editText.setText("");

        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null && messageContent.trim().length()!=0) {
                    messages.add(messageContent);
                    adapter.notifyDataSetChanged();

                }else if (messageContent.trim().length()==0){
                    button.setEnabled(false);
                }
            }
        });

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        button = findViewById(R.id.button);
        editText = findViewById(R.id.editText);



        Intent intent = getIntent();
        activeUser = intent.getStringExtra("username");
        setTitle("Chat with  " + activeUser);

        listView = findViewById(R.id.listView);

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, messages);
        listView.setAdapter(adapter);

        ParseQuery<ParseObject> query1 = new ParseQuery<ParseObject>("Message");
        query1.whereEqualTo("sender", ParseUser.getCurrentUser().getUsername());
        query1.whereEqualTo("receiver", activeUser);

        ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("Message");
        query2.whereEqualTo("receiver", ParseUser.getCurrentUser().getUsername());
        query2.whereEqualTo("sender", activeUser);

        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
        queries.add(query1);
        queries.add(query2);

        ParseQuery<ParseObject> query = ParseQuery.or(queries);

        query.orderByAscending("createdAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() > 0) {
                    messages.clear();
                    for (ParseObject message : objects) {
                        String messageContent = message.getString("message");

                        if(message.getString("sender").equals(ParseUser.getCurrentUser().getUsername())){
                            messageContent = "<--  " + messageContent;
                        }else  {
                            messageContent = "-->  "+messageContent;
                        }

                        messages.add(messageContent);


                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if(s.length()==0){
                    button.setEnabled(false);
                    button.setTextColor(0xffaaaaaa);
                }else{
                    button.setTextColor(Color.parseColor("#ff33e5b5"));
                    button.setEnabled(true);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                final String deleteMessage = messages.get(position);

                new  AlertDialog.Builder(ChatActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Are You Sure?")
                        .setMessage("Are You Sure You Want To Delete This Message?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                messages.remove(position);
                                adapter.notifyDataSetChanged();

                                ParseQuery<ParseObject> query1 = new ParseQuery<ParseObject>("Message");
                                query1.whereEqualTo("message",deleteMessage);
                                query1.findInBackground(new FindCallback<ParseObject>() {
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





                            }
                        }).setNegativeButton("NO",null)
                        .show();
                return true;

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menu,menu);

        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<String> tempList = new ArrayList();

                for(String temp : messages){
                    if(temp.toLowerCase().contains(newText.toLowerCase())){
                        tempList.add(temp);
                    }
                }
                ArrayAdapter adapter = new ArrayAdapter(ChatActivity.this,android.R.layout.simple_list_item_1,tempList);
                listView.setAdapter(adapter);

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

        if(item.getItemId()==R.id.clear){
            messages.clear();
            adapter.notifyDataSetChanged();

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
                            try{
                                message.delete();
                            }
                            catch (Exception em)
                            {
                                em.getMessage();
                            }
                        }

                    }
                }
            });



        }else if(item.getItemId()==R.id.help){
            Intent intent = new Intent(ChatActivity.this,HelpActivity.class);
            startActivity(intent);
        }


        return super.onOptionsItemSelected(item);
    }



}

