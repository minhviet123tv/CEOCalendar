package com.mvdragon.weekcalendar.database;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionMSSQL {

    //I. Tạo kết nối SQL Server
    @SuppressLint("NewApi")
    public Connection ConnectionClass(){

        String ip = "172.23.144.1", port="30661", dbname="WeekCalendar", dbuser="sa", dbpass="123456";
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Connection conn = null;
        String connectURL = null;

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            String connectionUrl = "jdbc:jtds:sqlserver://" + ip + ":" + port + ";databasename=" + dbname + ";User=" + dbuser + ";password=" + dbpass + ";";
            conn = DriverManager.getConnection(connectionUrl);

        } catch (Exception ex){
            Log.e("Set Error", ex.getMessage());
            Log.d("errorSQL", "Can't connect MS SQL from: class ConnectionMSSQL!");
        }

        return conn;
    }
}
