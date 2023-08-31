package com.example.gamemonitoring40;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class RegistratrionActivity extends AppCompatActivity {

    private String ip;
    private int port;

    private EditText regName, regPass, regUsername;
    private Button btReg;
    private TextView tvLogin;

    private ProgressDialog progress;

    private String username, password, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registratrion);

        Bundle extras = getIntent().getExtras();
        ip = extras.getString("ip","192.168.1.66");
        port = extras.getInt("port",8000);

        System.out.println("--------------------------REGISTRATION ACTIVITY--------------------------");

        //SET UP------------------------------------------------------------------------------------
        regName = findViewById(R.id.etRegName);
        regPass = findViewById(R.id.etPassword);
        regUsername = findViewById(R.id.etUsername);
        btReg = findViewById(R.id.btReg);
        tvLogin = findViewById(R.id.tvLogin);

        progress = new ProgressDialog(this);

        btReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = regName.getText().toString();
                username = regUsername.getText().toString();
                password = regPass.getText().toString();
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(getBaseContext(), "Please fill all data", Toast.LENGTH_SHORT).show();
                } else {
                    progress.setMessage("Just a moment...");
                    progress.show();
                    new regist().execute();
                }
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistratrionActivity.this,MainActivity.class));
            }
        });
    }

    class regist extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                Socket soc = new Socket(ip,port);
                System.out.println("Connected to Game Monitoring Server");
                PrintStream o = new PrintStream(soc.getOutputStream());

                o.println("regAcc," + username + "," + password + "," + name + ",parent");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplication(),"Registration success, please continue with log in", Toast.LENGTH_LONG).show();
                    }
                });
                startActivity(new Intent(RegistratrionActivity.this,MainActivity.class));
                progress.dismiss();

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(RegistratrionActivity.this,MainActivity.class));
        super.onBackPressed();
    }
}


