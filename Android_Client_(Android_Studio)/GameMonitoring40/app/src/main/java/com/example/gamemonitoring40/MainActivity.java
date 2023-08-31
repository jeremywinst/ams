package com.example.gamemonitoring40;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class MainActivity extends AppCompatActivity {

    private String ip = "192.168.1.66";
    private int port = 8000;

    private EditText etID, etPassword;
    private TextView tvPassAttempt, tvRegister;
    private Button btLogin;

    private ProgressDialog progress;

    private String parentUsr;
    private int counter = 5;
    private String id;
    private String pass;

    final String SHARED_PREFS = "sharedPrefs";

    private long backPressedTime;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //SETUP-------------------------------------------------------------------------------
        etID = findViewById(R.id.etID);
        etPassword = findViewById(R.id.etPassword);
        tvPassAttempt = findViewById(R.id.tvPassAttempt);
        btLogin = findViewById(R.id.btLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvPassAttempt.setText("No of attemp remaining : 5");
        progress = new ProgressDialog(this);

        SharedPreferences pref = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor spEditor = pref.edit();
        Bundle extras = getIntent().getExtras();

        //logout setting
        if (getIntent().hasExtra("nullid")){
            parentUsr = extras.getString("nullid");
            spEditor.putString("id", parentUsr);
            spEditor.apply();
            System.out.println("get extra from status activity");
        }

        parentUsr = pref.getString("id", "");

        System.out.println("--------------------------LOGIN ACTIVITY--------------------------");
        System.out.println("Parent Username: "+parentUsr);

        //validate parentUsr
        if (parentUsr.equals("reset")){
            btLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    id = etID.getText().toString();
                    pass = etPassword.getText().toString();
                    if (TextUtils.isEmpty(id) || TextUtils.isEmpty(pass)) {
                        Toast.makeText(getBaseContext(), "Please fill all data", Toast.LENGTH_SHORT).show();
                    } else {
                        progress.setMessage("Verifiying...");
                        progress.show();
                        new validate().execute();
                    }
                }
            });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itt1 = new Intent(MainActivity.this, RegistratrionActivity.class);
                itt1.putExtra("ip", ip);
                itt1.putExtra("port", port);
                startActivity(itt1);
            }
        });
        } else {
            startActivity(new Intent(MainActivity.this, StatusActivity.class));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("id", parentUsr);
        editor.apply();
    }

    class validate extends AsyncTask <String,String,String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                Socket soc = new Socket(ip, port);
                System.out.println("Connected to Game Monitoring Server");
                BufferedReader i = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                PrintStream o = new PrintStream(soc.getOutputStream());

                o.println("login," + id + "," + pass + ",parent");

                String msg[]= i.readLine().split(",");
                System.out.println("From server : "+ msg);
                soc.close();

                if (msg[0].equals("true")) {
                    parentUsr = id;
                    Intent itt = new Intent(MainActivity.this, StatusActivity.class);
                    itt.putExtra("pName",msg[1]);
                    itt.putExtra("parentUsr",id);
                    itt.putExtra("ip", ip);
                    itt.putExtra("port", port);
                    startActivity(itt);
                    System.out.println("Login success");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplication(),"Login Success!", Toast.LENGTH_LONG).show();
                        }
                    });
                    progress.dismiss();
                } else if (msg[0].equals("false")){
                    counter--;
                    System.out.println("Username or password incorect");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplication(),"Username or password incorrect", Toast.LENGTH_SHORT).show();
                            tvPassAttempt.setText("No of attempts remaining : " + counter);
                        }
                    });
                    progress.dismiss();
                }
                if (counter == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() { btLogin.setEnabled(false);
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}
