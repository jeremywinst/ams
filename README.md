## Application Monitoring System (AMS) (Android and Desktop)



## Introduction
AMS (Application Monitoring System) is a system of Android and Desktop applications designed to assist parents in monitoring and limiting their children's PC gaming activities. This repository contains the codebase for the Android app, Windows app, server, and database system. We utilize MySQL Workbench for database management and TCP/IP for the communication protocol between devices.

Explanation and demo video (Indonesian): https://www.youtube.com/watch?v=jONRPO99gek&ab_channel=JOSHUAALEXANDERH

## Components
**1. Android_Client:** An Android application designed for parents to monitor their children's gaming activity. Developed on Android Studio using the Java programming language.

**2. Desktop_Client:** A desktop application installed on the child's PC, responsible for monitoring and transmitting data to the parent. Developed using Eclipse IDE with the SwingDesigner plugin.

**3. Server:** A desktop application serving as a user interface to manage the database (administrator). Developed using Eclipse IDE with the SwingDesigner plugin.

**4. Database:** A MySQL file storing the database structure. Developed using MySQL Workbench.





## How Do They Work?
The Game Monitoring Application is structured with three clients: the child's client (desktop app), the parent's client (Android app), the administrator's client (desktop app), and a server (MySQL Workbench). These clients interact seamlessly through the server acting as a mediator. Please make sure that all devices are connected to the same network.

**The parent's application** is designed to provide playtime duration to their child and monitor the child's gaming activities through a smartphone. 

**The child's application** works to limit the child's PC/laptop playtime as set by the parent. Once the allocated time expires, the app will automatically close the game and cannot be reopened. It also sends gaming activity data to the parent via the server.

**The administrator's application** has complete control over the database, playing a vital role in enhancing database security and streamlining database management for administrators without the need for intricate MySQL queries.

**The database** in use is MySQL Workbench, comprising two tables: the users' table for storing user data and the gametime table for storing monitored data from the child's application.
<p align="center">
  <img src="/figures/dbUser.png" style="width:500px;"/>
</p>
<p align="center">
  <img src="/figures/dbGame.png" style="width:500px;"/>
</p>

Note:<br>
- username: Unique user id
- GameName: Name of the game played by the child
- runTime: Duration of playtime for each game (in minutes)
- setTime: Playing Time limit set by the parents (in minutes)
- interval: Daily/Weekly/Monthly (Ex: 3 hours/day, 10 hours/week, 40 hours/month)
- expired: Expiry date / when to reset the timer
- conn: The name of the child connected to the parent id






## Gallery
### Android App
<p align="center">
  <img src="/figures/MainActivity.jpeg" style="width:250px;"/>
  <img src="/figures/RegistrationActivity.jpeg" style="width:250px;"/>
</p>
<p align="center">
  <img src="/figures/StatusActivity - con.jpeg" style="width:250px;"/>
  <img src="/figures/UpdateActivity.jpeg" style="width:250px;"/>
</p>

### Desktop App
<p align="center">
  <img src="/figures/LoginPC.png" style="width:500px;"/>
</p>
<p align="center">
    <img src="/figures/RegistrationPC.png" style="width:320px;"/>
</p>
<p align="center">
  <img src="/figures/MonitoringPC.png" style="width:500px;"/>
</p>

### Server App (Administrator)
<p align="center">
  <img src="/figures/AdminLogin.png" style="width:320px;"/>
</p>
<p align="center">
    <img src="/figures/AdminMain.png" style="width:350px;"/>
</p>
<p align="center">
  <img src="/figures/AdminMain2.png" style="width:350px;"/>
</p>
<p align="center">
  <img src="/figures/AdminReg.png" style="width:350px;"/>
</p>
