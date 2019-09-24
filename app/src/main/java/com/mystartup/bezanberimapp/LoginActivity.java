package com.mystartup.bezanberimapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

public class LoginActivity extends AppCompatActivity {


    private EditText edt_login_username, edt_login_password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edt_login_username = findViewById(R.id.edt_login_username);
        edt_login_password = findViewById(R.id.edt_login_password);

       findViewById(R.id.btn_login_final).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               loginUserWithEmailVerification();
           }
       });
       findViewById(R.id.btnForgotPassword).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               showResetPasswordDialog();
           }
       });

    }

    public void loginUserWithEmailVerification() {
        ParseUser.logInInBackground(edt_login_username.getText().toString(), edt_login_password.getText().toString(), new LogInCallback() {

            @Override
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    Boolean emailVerified = user.getBoolean("emailVerified");
                    if (emailVerified == true) {
                        // Hooray! The user is logged in.

                        Toast.makeText(LoginActivity.this, "خوش آمدید", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this, FitnessActivity.class);

                        startActivity(intent);
                        finish();


                    } else {
                        // User did not confirm the e-mail!!

                        Toast.makeText(LoginActivity.this, "لطفا ایمیل خود را تایید کنید", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    // Signup failed. Look at the ParseException to see what happened.

                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    public void showResetPasswordDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_diolog, null);
        dialogBuilder.setView(dialogView);

        final EditText edtVerifyEmail = (EditText) dialogView.findViewById(R.id.edtVerifyEmail);

        dialogBuilder.setTitle("توجه");
        dialogBuilder.setMessage("ایمیل خود را در کادر زیر وارد کنید");
        dialogBuilder.setPositiveButton("ارسال رمز عبور", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();

                ParseUser.requestPasswordResetInBackground(edtVerifyEmail.getText().toString(), new RequestPasswordResetCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            // An email was successfully sent with reset instructions.
                            Toast.makeText(LoginActivity.this, "رمز عبور به ایمیلتان ارسال شد", Toast.LENGTH_SHORT).show();

                        } else {
                            // Something went wrong. Look at the ParseException to see what's up.
                        }
                    }
                });
            }
        });
        dialogBuilder.setNegativeButton("انصراف", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }
}
