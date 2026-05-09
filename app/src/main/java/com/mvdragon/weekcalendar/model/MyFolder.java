package com.mvdragon.weekcalendar.model;

public class MyFolder {
    private int id_folder;
    private String folder_name;
    private String folder_notes;
    private int status;
    private int role;
    private String create_datetime;

    public MyFolder() {
    }

    //Hàm khởi tạo đầy đủ (Dùng để get về)
    public MyFolder(int id_folder, String folder_name, String folder_notes, int status, int role, String create_datetime) {
        this.id_folder = id_folder;
        this.folder_name = folder_name;
        this.folder_notes = folder_notes;
        this.status = status;
        this.role = role;
        this.create_datetime = create_datetime;
    }

    //Hàm khởi tạo không có id, datetime (Dùng khi thêm vào bảng, id tự động tăng)
    public MyFolder(String folder_name, String folder_notes, int status, int role) {
        this.folder_name = folder_name;
        this.folder_notes = folder_notes;
        this.status = status;
        this.role = role;
    }

    public int getId_folder() {
        return id_folder;
    }

    public void setId_folder(int id_folder) {
        this.id_folder = id_folder;
    }

    public String getFolder_name() {
        return folder_name;
    }

    public void setFolder_name(String folder_name) {
        this.folder_name = folder_name;
    }

    public String getFolder_notes() {
        return folder_notes;
    }

    public void setFolder_notes(String folder_notes) {
        this.folder_notes = folder_notes;
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
