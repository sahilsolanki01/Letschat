package com.solanki.sahil.letschat;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    EditText editTextUsername;
    EditText editTextPassword;
    Button buttonSignUp;
    TextView textView;
    Boolean sighUpActive = true;
    RelativeLayout backgroundLayout;


    public void showList() {

        Intent intent = new Intent(getApplicationContext(), ListActivity.class);
        startActivity(intent);
    }


    public void keyboardDown(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    public void toggle(View view) {

        if (view.getId() == R.id.textView) {
            if (sighUpActive) {
                sighUpActive = false;
                buttonSignUp.setText("LOGIN");
                textView.setText("or, SIGNUP");
            } else {
                sighUpActive = true;
                buttonSignUp.setText("SIGN UP");
                textView.setText("or, LOGIN");

            }
        }
    }

    public void signUpClick(View view) {

        if (editTextUsername.getText().toString().matches("") || editTextPassword.getText().toString().matches("")) {
            Toast.makeText(MainActivity.this, "A Username and a Password required", Toast.LENGTH_LONG).show();
        } else {


            if (sighUpActive) {
                ParseUser user = new ParseUser();
                user.setUsername(editTextUsername.getText().toString());
                user.setPassword(editTextPassword.getText().toString());

                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(MainActivity.this, "New Account Created", Toast.LENGTH_LONG).show();
                            editTextUsername.setText("");
                            editTextPassword.setText("");
                            showList();

                        } else {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                ParseUser.logInInBackground(editTextUsername.getText().toString(), editTextPassword.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (e == null) {
                            Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_LONG).show();
                            editTextUsername.setText("");
                            editTextPassword.setText("");
                            showList();

                        } else {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("LetsChat");
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPaasword);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        textView = findViewById(R.id.textView);
        backgroundLayout = findViewById(R.id.backgroundLayout);


        if (ParseUser.getCurrentUser() != null) {
            showList();
        }


        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }
}


