package com.mvdragon.weekcalendar.model;

import java.io.Serializable;

public class ImageEvent implements Serializable {
    private int id_image_event;
    private int id_event;
    private byte [] event_image;
    private String date_time_save;

    public ImageEvent() {
    }

    public ImageEvent(int id_image_event, byte[] event_image, String date_time_save) {
        this.id_image_event = id_image_event;
        this.event_image = event_image;
        this.date_time_save = date_time_save;
    }

    public ImageEvent(int id_image_event, int id_event, byte[] event_image, String date_time_save) {
        this.id_image_event = id_image_event;
        this.id_event = id_event;
        this.event_image = event_image;
        this.date_time_save = date_time_save;
    }

    public int getId_image_event() {
        return id_image_event;
    }

    public void setId_image_event(int id_image_event) {
        this.id_image_event = id_image_event;
    }

    public int getId_event() {
        return id_event;
    }

    public void setId_event(int id_event) {
        this.id_event = id_event;
    }

    public byte[] getEvent_image() {
        return event_image;
    }

    public void setEvent_image(byte[] event_image) {
        this.event_image = event_image;
    }

    public String getDate_time_save() {
        return date_time_save;
    }

    public void setDate_time_save(String date_time_save) {
        this.date_time_save = date_time_save;
    }
}
