/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamemonitoringserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;

/**
 *
 * @author ASUS
 */
public class GameMonitoringServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ServerSocket conn = null;
        Socket soc = null;
        BufferedReader i = null;
        PrintStream o = null;

        String l;
        String usernm = null;
        String pass = null;
        String name = null;
        String type = null;
        String child_id;
        String parent_id;
        String expired;
        int setTime;
        int runTime;
        String interfal;

        try {
            conn = new ServerSocket(8000, 10);
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/sql_gamemonitoring", "root", "password");
            while (true) {
                soc = conn.accept();
                i = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                o = new PrintStream(soc.getOutputStream());

                l = i.readLine();
                System.out.println("Client: " + l);
                String data[] = l.split(",");

                switch (data[0]) {
                    //login(login,username,pass,type)
                    case "login":
                        Statement st = con.createStatement();
                        ResultSet rs = st.executeQuery("SELECT * from users where username = '" + data[1] + "'");
                        if (rs.next()) {
                            usernm = rs.getString("username");
                            pass = rs.getString("password");
                            type = rs.getString("type");
                            name = rs.getString("name");
                        }
                        if (data[1].equals(usernm) && data[2].equals(pass) && data[3].equals(type)) {
                            System.out.println("login success");
                            o.println("true," + name);
                        } else {
                            System.out.println("Login failed");
                            o.println("false");
                        }
                        break;
                    
                    //RegisterAccount (regAcc,username,password,name,type)
                    case "regAcc":
                        Statement st0 = con.createStatement();
                        st0.executeUpdate("insert into users (username, password, name, type) values ('" + data[1] + "', '" + data[2] + "', '" + data[3] + "', '" + data[4] + "')");
                        System.out.println("User " + data[3] + " registered to Database");
                        break;

                    //getName(getName,username)
                    case "getName":
                        Statement st3 = con.createStatement();
                        ResultSet rs3 = st3.executeQuery("select * from users where username = '" + data[1] + "'");
                        if (rs3.next()) {
                            name = rs3.getString("name");
                            o.println(name);
                            System.out.println("Name of " + data[1] + ": " + name);
                        } else {
                            System.out.println("username not found");
                            o.println("false");
                        }
                        break;

                    //setConn(setConn,parentUsr,childUsr)
                    case "setConn":
                        Statement st6 = con.createStatement();
                        ResultSet rs6 = st6.executeQuery("select * from users where username = '" + data[2] + "'");
                        if (rs6.next()) {
                            st6.executeUpdate("UPDATE users set conn = '" + data[2] + "' WHERE username = '" + data[1] + "'");
                            System.out.println("setConn success");
                            o.println("success");
                        } else {
                            
                            o.println("false");
                        }
                        break;

                    //getConn(getConn,parentUsr)
                    case "getConn":
                        Statement st9 = con.createStatement();
                        ResultSet rs9 = st9.executeQuery("select * from users where username = '" + data[1] + "'");
                        if (rs9.next()) {
                            String msg = rs9.getString("conn");
                            if (msg.equals("not set")) {
                                o.println("false");
                                System.out.println("getConn failed");
                            } else {
                                o.println(msg);
                                System.out.println("getConn success");
                            }
                        }
                        break;

                    //setTime(setTime,childUsr,setTime,interfal,expired)
                    case "setTime":
                        Statement st7 = con.createStatement();
                        st7.executeUpdate("UPDATE gametime set setTime = '" + data[2] + "', interfal = '" + data[3]
                                + "', runTime = '0', expired = '" + data[4] + "' WHERE username = '" + data[1] + "'");
                        System.out.println("setTime success");
                        break;
                        
                    //InsertGame (insGame, username, GameName)
                    case "insGame":
                        Statement st1 = con.createStatement();
                        st1.executeUpdate("insert into gametime (username, GameName) values ('" + data[1] + "', '" + data[2] + "')");
                        System.out.println("Game " + data[2] + " Inserted to Database");
                        break;

                    //SendRuntime (setRT, username, GameName, runtime)
                    case "setRT":
                        Statement st2 = con.createStatement();
                        st2.executeUpdate("update gametime set runTime = '" + data[3] + "' where username = '" + data[1] + "' and GameName = '" + data[2] + "'");
                        System.out.println("Game " + data[2] + " runTime updated to " + data[3]);
                        break;

                    //SendRuntime (setALLRT, username, expiredDate)
                    case "resetALLRT":
                        Statement st8 = con.createStatement();
                        st8.executeUpdate("update gametime set runTime = '0', expired = '" + data[2] + "' where username = '" + data[1] + "'");
                        System.out.println("Run time restarted updated");
                        break;

                    //GetTime (getTime,username,GameName)    
                    case "getTime":
                        Statement st11 = con.createStatement();
                        ResultSet rs11 = st11.executeQuery("select * from gametime where username = '" + data[1] + "' and GameName = '" + data[2] + "'");

                        if (rs11.next()) {
                            setTime = rs11.getInt("setTime");
                            runTime = rs11.getInt("runTime"); 
                            interfal = rs11.getString("interfal");
                            o.println(setTime + "," + runTime + "," + interfal);
                            System.out.println("setTime = " + setTime + " runTime = " + runTime);
                        }
                        break;

                    //GetTable (getTable,username)    
                    case "getTable":
                        Statement st5 = con.createStatement();
                        ResultSet rs5 = st5.executeQuery("select * from gametime where username = '" + data[1] + "'");
                        while (rs5.next()) {
                            String a = rs5.getString("GameName");
                            String b = rs5.getString("runTime");
                            String c = rs5.getString("setTime");
                            String d = rs5.getString("interfal");
                            o.println(a + "," + b + "," + c + "," + d);
                        }
                        o.println(",,,done");
                        System.out.println("Game List Table Sent!");
                        break;

                    //checkExpired (checkExpired, childUsr)
                    case "checkExpired":
                        Statement st13 = con.createStatement();
                        ResultSet rs13 = st13.executeQuery("select * from gametime where username = '" + data[1] + "'");
                        if (rs13.next()) {
                            interfal = rs13.getString("interfal");
                            expired = rs13.getString("expired");
                            o.println(expired + "," + interfal);
                            System.out.println("Expired date send: " + expired + "," +interfal);
                        }
                        break;
                    
                    //DeleteGame from listTable (delGame,username,GameName)
                    case "delGame":
                        Statement st10 = con.createStatement();
                        st10.executeUpdate("DELETE from gametime WHERE username = '" + data[1] + "' and GameName = '" + data[2] + "'");
                        System.out.println("Game " + data[2] + " Deleted from Database");
                        break;
                        
                    //delUser ("delUser,username")
                    case "delUser":
                        Statement st33 = con.createStatement();
                        st33.executeUpdate("DELETE from users WHERE username = '"+data[1]+"'");
                        System.out.println("User "+data[1]+" Deleted from Database");
                        break;
                        
                    //ADMIN get Table Acc (ADMgetTableAcc)    
                    case "ADMgetTableAcc":
                        Statement stA = con.createStatement();
                        ResultSet rsA = stA.executeQuery("select * from users");
                        while (rsA.next()) {
                            String a = rsA.getString("username");
                            String b = rsA.getString("password");
                            String c = rsA.getString("name");
                            String d = rsA.getString("type");
                            String e = rsA.getString("conn");
                            o.println(a + "," + b + "," + c + "," + d + "," + e);
                        }
                        o.println(",,,done");
                        System.out.println("Account Table Sent!");
                        break;

                    //ADMIN get Table Game (ADMgetTableGame) 
                    case "ADMgetTableGame":
                        Statement stB = con.createStatement();
                        ResultSet rsB = stB.executeQuery("select * from gametime");
                        while(rsB.next()) {
                            String z = rsB.getString("username");
                            String a = rsB.getString("GameName");
                            String b = rsB.getString("runTime");
                            String c = rsB.getString("setTime");
                            String d = rsB.getString("interfal");
                            String e = rsB.getString("expired");
                            o.println(z+","+a+","+b+","+c+","+d+","+e);
                        }
                        o.println(",,,,,,done");
                        System.out.println("Game List Table Sent!");
                        break;

                    //ADMIN update Account List (ADMupdateTableAcc,username,password,name,type,conn)    
                    case "ADMupdateTableAcc":
                        Statement stC = con.createStatement();
                        stC.executeUpdate("update users set password='" + data[2] + "', name='" + data[3] + "', type='" + data[4] + "', conn='" + data[5] + "' where username='" + data[1] + "'");
                        System.out.println("Account Table Updated");
                        break;

                    //ADMIN update Game List (ADMupdateGameList,username,gamename,runtime,settime,interfal)    
                    case "ADMupdateGameList": 
                        Statement stD = con.createStatement();
                        stD.executeUpdate("update gametime set username='"+data[1]+"', runTime='"+data[3]+"', setTime='"+data[4]+"', interfal='"+data[5]+"', expired='"+data[6]+"' where GameName='"+data[2]+"'");
                        System.out.println("Game Table Updated");
                        break;

                    default:
                        System.out.println("something wrong");
                        o.println("something wrong");
                        break;
                }
                soc.close();
            }
        } catch (IOException | ClassNotFoundException | SQLException e) {
            System.out.println(e);
        }
    }
}
