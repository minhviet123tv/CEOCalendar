package com.mvdragon.weekcalendar.model;

public class ViewImageShared {
    private int id_image;
    private int id_event_or_note;
    private int position;

    public ViewImageShared() {
    }

    public ViewImageShared(int id_image, int id_event_or_note, int position) {
        this.id_image = id_image;
        this.id_event_or_note = id_event_or_note;
        this.position = position;
    }

    public int getId_image() {
        return id_image;
    }

    public void setId_image(int id_image) {
        this.id_image = id_image;
    }

    public int getId_event_or_note() {
        return id_event_or_note;
    }

    public void setId_event_or_note(int id_event_or_note) {
        this.id_event_or_note = id_event_or_note;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
