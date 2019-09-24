package com.mystartup.bezanberimapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtUserName, edtEmail, edtPassword;
    private Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
//                WindowManager.LayoutParams.FLAG_SECURE);

        setContentView(R.layout.activity_main);

        edtUserName = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnSignUp = findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(MainActivity.this);


//        if (ParseUser.getCurrentUser() != null) {
//            ParseUser.logOut();
//
//        }

        if (ParseUser.getCurrentUser() != null) {

            Intent intent = new Intent(MainActivity.this, FitnessActivity.class);
            startActivity(intent);
            finish();
        }


        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


    }


    @Override
    public void onClick(View v) {
        ParseUser user = new ParseUser();
        user.setUsername(edtUserName.getText().toString());
        user.setEmail(edtEmail.getText().toString());
        user.setPassword(edtPassword.getText().toString());

        user.signUpInBackground(new SignUpCallback() {

            @Override
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.

                    Toast.makeText(MainActivity.this, "لینک فعال سازی برای شما ارسال شد", Toast.LENGTH_SHORT).show();

                    ParseUser.logOut();

                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong

                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

                }
            }
        });

    }
}


// Client ID
//192707213249-3n64qa7pblbdhm0r92sj2m898jvlfabl.apps.googleusercontent.com