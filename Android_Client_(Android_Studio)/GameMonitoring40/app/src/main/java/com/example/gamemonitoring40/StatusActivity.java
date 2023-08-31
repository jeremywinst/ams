package com.example.gamemonitoring40;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class StatusActivity extends AppCompatActivity {

    private String ip;
    private int port;

    private Socket soc = null;
    private BufferedReader i = null;
    private PrintStream o = null;

    public static final String SHARED_PREFS = "sharedPrefs";

    private long backPressedTime;
    private Toast backToast;

    private String pName;
    private String cName;
    private String parentUsr;
    private String childUsr;
    private String tLeft;
    private String sTime;
    private String interval;
    private Boolean refresh = false;
    private int setTime;
    private int timeLeft;
    private int delay = 2000;

    Handler handler;
    Runnable runnable;

    TextView tvTitle, tvChild, tvTimegiven, tvTimeleft, tvGame;
    ListView lv;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //SETUP------------------------------------------------------------------------------------
        tvTitle = findViewById(R.id.tvTitle);
        tvChild = findViewById(R.id.tvChild);
        tvTimegiven = findViewById(R.id.tvTimegiven);
        tvTimeleft = findViewById(R.id.tvTimeleft);
        tvGame = findViewById(R.id.tvGame);
        lv = findViewById(R.id.lv);
        pb = findViewById(R.id.durationprogress);
        pb.setMax(100);

        SharedPreferences pref = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        System.out.println("--------------------------STATUS ACTIVITY--------------------------");
        //SET TITLE--------------------------------------------------------------------------------
        Bundle extras = getIntent().getExtras();
        if (getIntent().hasExtra("ip")) {
            pName = extras.getString("pName", "");
            parentUsr = extras.getString("parentUsr", "");
            ip = extras.getString("ip","");
            port = extras.getInt("port");
            editor.putString("ip",ip);
            editor.putInt("port",port);
            editor.putString("pName", pName);
            editor.putString("parentUsr", parentUsr);
            editor.apply();
            System.out.println("Extra updated");
        }

        ip = pref.getString("ip","192.168.1.66");
        port = pref.getInt("port",8000);
        parentUsr = pref.getString("parentUsr", "");
        tvTitle.setText(pref.getString("pName", ""));

        //PROGRAM----------------------------------------------------------------------------------
        //CHECK CONNECTION FIRST
        new checkConn().execute();
        SystemClock.sleep(1000);

        if (childUsr.equals("false")) {
            System.out.println("child user not added");
            tvTimegiven.setText("-");
            tvTimeleft.setText("-");
            tvGame.setText("Game List: -");
        } else {
            //REFRESH DATA AND UI EVERY 2 seconds
            doRefresh();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.updateMenu :
                Intent i1 = new Intent(StatusActivity.this, UpdateActivity.class);
                i1.putExtra("parentUsr", parentUsr);
                i1.putExtra("ip", ip);
                i1.putExtra("port", port);
                startActivity(i1);
                break;
            case R.id.logoutMenu :
                if (refresh){
                    handler.removeCallbacks(runnable);
                }
                Intent i2 = new Intent(StatusActivity.this, MainActivity.class);
                i2.putExtra("nullid", "reset");
                startActivity(i2);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            if(refresh){
                handler.removeCallbacks(runnable);
            }
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    class checkConn extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            try {
                soc = new Socket(ip,port);
                System.out.println("Connected to Game Monitoring Server - CHECK CONNECTION");
                i = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                o = new PrintStream(soc.getOutputStream());

                o.println("getConn," + "jeremywinst");
                String msg = i.readLine();
                soc.close();

                if (msg.equals("false")) {
                    childUsr = msg;
                    System.out.println(msg);
                    System.out.println("no connection found please set your timer fo the first time");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvChild.setText("No child added");
                            tvTimegiven.setText("-");
                            tvTimeleft.setText("-");
                        }
                    });
                } else {
                    childUsr = msg;
                    soc = new Socket(ip,port);
                    System.out.println("Connected to Game Monitoring Server - GET NAME");
                    i = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                    o = new PrintStream(soc.getOutputStream());

                    o.println("getName," + childUsr);
                    cName = i.readLine();
                    soc.close();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvChild.setText(cName);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(parentUsr);
            System.out.println(childUsr);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            return null;
        }
    }

    private void doRefresh(){
        try{
            refresh = true;
            ArrayList<HashMap<String, String>> arrGameList = new ArrayList<>();
            String msg;
            System.out.println("____________________refreshing____________________");

            //check expired date
            soc = new Socket(ip,port);
            System.out.println("Connected to Game Monitoring Server - CHECK EXPIRED");
            i = new BufferedReader(new InputStreamReader(soc.getInputStream()));
            o = new PrintStream(soc.getOutputStream());

            o.println("checkExpired,"+childUsr);
            //(expired,interfal)
            msg = i.readLine();
            soc.close();
            String split[] = msg.split(",");

            Calendar calendar = Calendar.getInstance();
            String currentDate = DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(calendar.getTime());
            System.out.println("Interfal = " + split[1]);
            System.out.println("Current date = "+currentDate);
            System.out.println("Expired = " + split[0]);

            if (split[0].equals(currentDate)){
                soc = new Socket(ip,port);
                System.out.println("Connected to Game Monitoring Server - RESETTT");
                o = new PrintStream(soc.getOutputStream());

                String expiredDate = exp_converter(split[0],split[1]);
                System.out.println("NEW EXPIRED DATE = "+expiredDate);

                o.println("resetALLRT,"+childUsr+","+expiredDate);
                soc.close();
                System.out.println("---------------------------EXPIRED REACHED RESET RUNTIME---------------------------");
            } else {
                //refresh data
                System.out.println("NOT EXPIRED YET");
                soc = new Socket(ip,port);
                System.out.println("Connected to Game Monitoring Server - REFRESH");
                i = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                o = new PrintStream(soc.getOutputStream());

                o.println("getTable,"+childUsr);
                int count = 0;
                int totalRunTime = 0;
                String GameName;
                String rtime;
                int RUNTIME[] = new int[20];
                String data[];

                while((msg = i.readLine()) != null) {
                    if (msg.trim().equals(",,,done")) break;
                    //GameName,runTime,setTime,interfal
                    data = msg.split(",");
                    System.out.println("check table: "+msg);

                    //get each game runTime
                    int rt = Integer.valueOf(data[1]);
                    int Tsecond = rt % 60;
                    int Thour = rt / 60;
                    int Tminute = Thour % 60;
                    Thour = Thour / 60;
                    rtime = Thour + " Hour " + Tminute + " Minute " + Tsecond + " Second";

                    //Update Game list
                    GameName = data[0];
                    HashMap<String, String> ggame = new HashMap<>();
                    ggame.put("game",GameName);
                    ggame.put("run",rtime);
                    arrGameList.add(ggame);
                    ListAdapter adapter = new SimpleAdapter(StatusActivity.this,arrGameList,
                            R.layout.game_template, new String[]{"game","run"}, new int[]{R.id.tvGame,R.id.tvDurGame});
                    lv.setAdapter(adapter);

                    //get set time and interval
                    setTime = Integer.parseInt(data[2]);
                    interval = data[3];

                    //get total runTime
                    RUNTIME[count] = Integer.parseInt(data[1]);
                    totalRunTime = totalRunTime + RUNTIME[count];
                    count++;
                }
                soc.close();
                //get timeLeft
                timeLeft = setTime - totalRunTime;

                tLeft = time_converter(timeLeft);
                sTime = time_converter(setTime);

                //UPDATE UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int perc = (timeLeft*100)/setTime;
                        tvTimegiven.setText(sTime+", "+interval);
                        tvTimeleft.setText(tLeft);
                        System.out.println("time = "+perc+" % left");
                        pb.setProgress(perc);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        refresh(delay);
    }

    private void refresh (int miliseconds) {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                doRefresh();
            }
        };
        handler.postDelayed(runnable,miliseconds);
    }

    private String time_converter(int second){
        int Tsecond = second % 60;
        int Thour = second / 60;
        int Tminute = Thour % 60;
        Thour = Thour / 60;
        if (Tminute > 0 && Thour == 0){
            return Tminute+" Minute";
        } else if (Thour==0 && Tminute==0 && Tsecond<=0){
            return "Time Out!";
        } else if (Tminute == 0 && Thour == 0){
            return Tsecond+" Second";
        } else {
            return Thour+" Hour "+Tminute+" Minute";
        }
    }

    private String exp_converter(String expired, String interfal){

        String data[] = expired.split("/");

        int day = Integer.parseInt(data[1]);
        int month = Integer.parseInt(data[0]);
        int year = Integer.parseInt(data[2]);

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
        return expiredDay;
    }
}
