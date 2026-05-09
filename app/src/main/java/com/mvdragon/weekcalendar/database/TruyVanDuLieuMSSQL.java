//package com.mvdragon.weekcalendar.database;
//
//import android.util.Log;
//
//import com.mvdragon.weekcalendar.CalendarUtils;
//import com.mvdragon.weekcalendar.event.Event;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.List;
//
//public class TruyVanDuLieuMSSQL {
//    private final Connection connection = new ConnectionMSSQL().ConnectionClass();
//
//    //I. Lấy danh sách dữ liệu của một ngày
//    public List<Event> getListEventOfDay (LocalDate date) throws SQLException {
//
//        List<Event> eventList = new ArrayList<>();
//
//        if(connection != null){
//            String SQL = "Select * from EventOfDay where event_date = ? order by event_time ASC, time_record ASC";
//
//            PreparedStatement statement = connection.prepareStatement(SQL);
//            statement.setString(1, date.toString());
//            ResultSet result = statement.executeQuery();
//
//            while (result.next()){
//                int id_event = result.getInt("id_event");
//                String event_name = result.getString("event_name");
//                String event_date = result.getString("event_date");
//                String event_time = result.getString("event_time");
//                String date_time_record = result.getString("time_record");
//                int
//
//
//                Event event = new Event(id_event, event_name, event_date, event_time, date_time_record);
//
//                eventList.add(event);
//            }
//
//            //Trả về danh sách truy vấn, nếu không có dữ liệu thì danh sách rỗng
//            return eventList;
//        }
//
//        //Nếu không có kết nối thì trả về danh sách trống
//        return eventList;
//    }
//
//    //II. Thêm một sự kiện của một ngày vào bảng CSDL
//    public void insertEvent (Event event) throws SQLException {
//
//        //LocalDateTime: yyyy-mm-dd DayName HH:mm:ss.milisecond -> Cần dùng format để chuyển về đúng định dạng
////        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//
//        if(connection != null){
//            String SQL = "Insert into EventOfDay values (?,?,?,?)";
//
//            PreparedStatement statement = connection.prepareStatement(SQL);
//            //set các tham số để thêm vào CSDL, không set id_event vì để tự động tăng
//            statement.setString(1, event.getEvent_name());
//            statement.setString(2,  event.getEvent_date()); //LocalDate mặc định: yyyy-mm-dd
//            statement.setString(3, event.getEvent_time()); //LocalTime: HH:mm:ss.SSSSSSS
//            statement.setString(4, event.getDate_time_record());
//
//            statement.executeUpdate();
//
//        } else {
//            Log.d("errorSQL", "Can't connect MSSQL, from class TruyVanDuLieuMSSQL !!!");
//        }
//
//    }
//
//    //III. Check xem một ngày có sự kiện hay không
//    public boolean checkEventOfDay(String dateCheck) throws SQLException {
//
//        //Có thể dùng lệnh select count (*) nếu cần
//        String SQL = "Select * from EventOfDay where event_date = ?";
//
//        PreparedStatement statement = connection.prepareStatement(SQL);
//        statement.setString(1, dateCheck);
//        ResultSet result = statement.executeQuery();
//
//        int count = 0;
//        while (result.next()){
//            count++;
//        }
//
//        if(count == 0){
//            return false;
//        }
//
//        if(count > 0){
//            return true;
//        }
//
//        return false;
//    }
//}
