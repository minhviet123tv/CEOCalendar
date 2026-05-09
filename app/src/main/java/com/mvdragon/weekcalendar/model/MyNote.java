package com.mvdragon.weekcalendar.model;

public class MyNote {
    private int id_note, id_folder;
    private String note_name, note_content;
    private int status, role;
    private String create_datetime;

    //Khởi tạo rỗng
    public MyNote() {
    }

    //Khởi tạo đầy đủ (dùng khi lấy dữ liệu về)
    public MyNote(int id_note, int id_folder, String note_name, String note_content, int status, int role, String create_datetime) {
        this.id_note = id_note;
        this.id_folder = id_folder;
        this.note_name = note_name;
        this.note_content = note_content;
        this.status = status;
        this.role = role;
        this.create_datetime = create_datetime;
    }

    //Khởi tạo mới (id_note tự động, create_datetime tạo trong hàm)
    public MyNote(int id_folder, String note_name, String note_content, int status, int role) {
        this.id_folder = id_folder;
        this.note_name = note_name;
        this.note_content = note_content;
        this.status = status;
        this.role = role;
    }

    public int getId_note() {
        return id_note;
    }

    public void setId_note(int id_note) {
        this.id_note = id_note;
    }

    public int getId_folder() {
        return id_folder;
    }

    public void setId_folder(int id_folder) {
        this.id_folder = id_folder;
    }

    public String getNote_name() {
        return note_name;
    }

    public void setNote_name(String note_name) {
        this.note_name = note_name;
    }

    public String getNote_content() {
        return note_content;
    }

    public void setNote_content(String note_content) {
        this.note_content = note_content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getCreate_datetime() {
        return create_datetime;
    }

    public void setCreate_datetime(String create_datetime) {
        this.create_datetime = create_datetime;
    }
}
