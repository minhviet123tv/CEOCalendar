package com.mvdragon.weekcalendar.model;

import java.io.Serializable;

public class Event implements Serializable {

    //Cấu tạo của một model event
    private int id_event;
    private String event_name;
    private String event_date; //LocalDate
    private String event_time; //LocalTime
    private String date_time_record; //LocalDateTime
    private int event_status;
    private String event_notes;
    private int event_notify;

    //Khởi tạo trống
    public Event() {
    }

    //Khởi tạo đầy đủ (để select từ CSDL)
    public Event(int id_event, String event_name, String event_date, String event_time, String date_time_record, int event_status, String event_notes, int event_notify) {
        this.id_event = id_event;
        this.event_name = event_name;
        this.event_date = event_date;
        this.event_time = event_time;
        this.date_time_record = date_time_record;
        this.event_status = event_status;
        this.event_notes = event_notes;
        this.event_notify = event_notify;
    }


    //Khởi tạo không có id (Để thêm vào CSDL vì id_event sẽ tự động tăng)
    public Event(String event_name, String event_date, String event_time, String date_time_record, int event_status, String event_notes, int event_notify) {
        this.event_name = event_name;
        this.event_date = event_date;
        this.event_time = event_time;
        this.date_time_record = date_time_record;
        this.event_status = event_status;
        this.event_notes = event_notes;
        this.event_notify = event_notify;
    }

    public int getId_event() {
        return id_event;
    }

    public void setId_event(int id_event) {
        this.id_event = id_event;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getEvent_date() {
        return event_date;
    }

    public void setEvent_date(String event_date) {
        this.event_date = event_date;
    }

    public String getEvent_time() {
        return event_time;
    }

    public void setEvent_time(String event_time) {
        this.event_time = event_time;
    }

    public String getDate_time_record() {
        return date_time_record;
    }

    public void setDate_time_record(String date_time_record) {
        this.date_time_record = date_time_record;
    }

    public int getEvent_status() {
        return event_status;
    }

    public void setEvent_status(int event_status) {
        this.event_status = event_status;
    }

    public String getEvent_notes() {
        return event_notes;
    }

    public void setEvent_notes(String event_notes) {
        this.event_notes = event_notes;
    }

    public int getEvent_notify() {
        return event_notify;
    }

    public void setEvent_notify(int event_notify) {
        this.event_notify = event_notify;
    }
}
