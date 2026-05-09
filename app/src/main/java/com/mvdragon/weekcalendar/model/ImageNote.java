package com.mvdragon.weekcalendar.model;

import java.io.Serializable;

public class ImageNote implements Serializable {
    private int id_image_note;
    private int id_note;
    private byte [] note_image;
    private String date_time_save;

    public ImageNote() {
    }

    public ImageNote(int id_image_note, int id_note, byte[] note_image, String date_time_save) {
        this.id_image_note = id_image_note;
        this.id_note = id_note;
        this.note_image = note_image;
        this.date_time_save = date_time_save;
    }

    public int getId_image_note() {
        return id_image_note;
    }

    public void setId_image_note(int id_image_note) {
        this.id_image_note = id_image_note;
    }

    public int getId_note() {
        return id_note;
    }

    public void setId_note(int id_note) {
        this.id_note = id_note;
    }

    public byte[] getNote_image() {
        return note_image;
    }

    public void setNote_image(byte[] note_image) {
        this.note_image = note_image;
    }

    public String getDate_time_save() {
        return date_time_save;
    }

    public void setDate_time_save(String date_time_save) {
        this.date_time_save = date_time_save;
    }
}
