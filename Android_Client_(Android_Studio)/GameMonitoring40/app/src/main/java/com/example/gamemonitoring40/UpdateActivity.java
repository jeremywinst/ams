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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Calendar;

public class UpdateActivity extends AppCompatActivity {

    private String ip;
    private int port;

    private Socket soc = null;
    BufferedReader i = null;
    PrintStream o = null;

    private EditText etChildID, etSetTime;

    private Button btUpdate;
    private RadioGroup rgInterval;
    private RadioButton radioButton;

    private ProgressDialog progress;

    private String parentUsr;
    private String childUsr;
    private String interfal;
    private String expired;
    private int setTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        progress = new ProgressDialog(this);

        Bundle extras = getIntent().getExtras();
        ip = extras.getString("ip","192.168.1.66");
        port = extras.getInt("port",8000);
        parentUsr = extras.getString("parentUsr","");

        rgInterval = findViewById(R.id.rgInterval);
        btUpdate = findViewById(R.id.btUpdate);
        etChildID = findViewById(R.id.etChildId);
        etSetTime = findViewById(R.id.etSetTime);

        System.out.println("--------------------------UPDATE ACTIVITY--------------------------");
        System.out.println("Parent Username: "+parentUsr);

        btUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int radioId = rgInterval.getCheckedRadioButtonId();
                radioButton = findViewById(radioId);
                childUsr = etChildID.getText().toString();

                if (TextUtils.isEmpty(childUsr) || TextUtils.isEmpty(etSetTime.getText().toString()) || radioButton == null) {
                    Toast.makeText(getBaseContext(), "Please fill all data", Toast.LENGTH_SHORT).show();
                } else {
                    interfal = radioButton.getText().toString();
                    setTime = Integer.parseInt(etSetTime.getText().toString()) * 60;
                    expired = setDate();
                    progress.setMessage("Updating...");
                    progress.show();
                    new doUpdate().execute();
                }
            }
        });
    }

    class doUpdate extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            try{
                soc = new Socket(ip,port);
                System.out.println("Connected to Game Monitoring Server");
                i = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                o = new PrintStream(soc.getOutputStream());

                o.println("setConn,"+parentUsr+","+childUsr);
                String msg = i.readLine();
                soc.close();

                if(msg.equals("false")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplication(),"Child username not found",Toast.LENGTH_LONG).show();
                        }
                    });
                    progress.dismiss();
                } else {
                    soc = new Socket(ip,port);
                    System.out.println("Connected to Game Monitoring Server");
                    o = new PrintStream(soc.getOutputStream());
                    o.println("setTime," + childUsr + "," + setTime + "," + interfal + "," + expired);
                    soc.close();

                    Intent itt = new Intent(UpdateActivity.this, StatusActivity.class);
                    startActivity(itt);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplication(),"Set Time Success",Toast.LENGTH_LONG).show();
                        }
                    });
                    progress.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(UpdateActivity.this,StatusActivity.class));
        super.onBackPressed();
    }

    private String setDate(){
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(calendar.getTime());

        //format : month/day/year
        String date[] = currentDate.split("/");
        int month = Integer.parseInt(date[0]);
        int day = Integer.parseInt(date[1]);
        int year = Integer.parseInt(date[2]);

        switch (interfal){
            case "Daily" :
                day = day + 1;
                if (day > 30) {
                    day = 1;
                    month = month + 1;
                }
                if (month == 12) {
                    month = 1;
                    year = year + 1;
                } break;
            case "Weekly" :
                day = day + 7;
                if (day > 30) {
                    day = day % 30;
                    month = month + 1;
                }
                if (month == 12) {
                    month = 1;
                    year = year + 1;
                } break;
            case "Monthly" :
                month = month + 1;
                if (month == 12) {
                    month = 1;
                    year = year + 1;
                } break;
        }
        String expiredDay = month +"/"+ day +"/"+ year;

        System.out.println("Current date: "+currentDate);
        System.out.println("Expired day: "+expiredDay);
        return expiredDay;
    }
}


