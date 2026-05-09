package com.mvdragon.weekcalendar.model;

public class MyNoteAvatar {
    private int id_note_avatar, id_note;
    private byte [] note_avatar;
    private int status;

    public MyNoteAvatar() {
    }

    public int getId_note_avatar() {
        return id_note_avatar;
    }

    public void setId_note_avatar(int id_note_avatar) {
        this.id_note_avatar = id_note_avatar;
    }

    public int getId_note() {
        return id_note;
    }

    public void setId_note(int id_note) {
        this.id_note = id_note;
    }

    public byte[] getNote_avatar() {
        return note_avatar;
    }

    public void setNote_avatar(byte[] note_avatar) {
        this.note_avatar = note_avatar;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public MyNoteAvatar(int id_note_avatar, int id_note, byte[] note_avatar, int status) {

        this.id_note_avatar = id_note_avatar;
        this.id_note = id_note;
        this.note_avatar = note_avatar;
        this.status = status;
    }
}
