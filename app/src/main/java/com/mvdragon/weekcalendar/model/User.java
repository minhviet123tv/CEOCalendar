package com.mvdragon.weekcalendar.model;


public class User {
    private String userName;
    private String email;
    private int countLogin;

    //Phần điều chỉnh trong Menu control setting
    private int fragment_user; //Lưu fragment cuối cùng sử dụng (tự lưu mỗi khi vào fragment) | Dự kiến khi có ghi chú sẽ dùng (lưu cả 3 menu bottom)
    private int startWeek; //Bắt đầu của tuần (thứ 2 hay CN | 1: CN, 2: T2)
    private int viewCalendar; //Lưu kiểu view 1 tuần, 3 tuần
    private int dateFormat; //Kiểu ngày tháng dd/MM/YYYY
    private boolean premium; //Có phải là tài khoản cao cấp (premium) hay không
    private String premium_expiration_date; //Ngày hết hạn tài khoản premium
    private int my_coin; //coin (dự kiến để sử dụng khi ghi chú, nếu không muốn mua thì xem quảng cáo 15 - 30s: 3 vé)
    private int status; // tạo tham số dự trữ (Vì nếu không khi update thêm tính năng thì user sẽ không có sẵn mà có thể phải tạo Shared mới)
    private int role; //tham số dự trữ -> Đang dùng làm lưu giao diện bottom/top
    private String user_notes; //tham số dự trữ

    public User(String userName, String email, int countLogin, int fragment_user, int startWeek, int viewCalendar, int dateFormat, boolean premium, String premium_expiration_date, int my_coin, int status, int role, String user_notes) {
        this.userName = userName;
        this.email = email;
        this.countLogin = countLogin;
        this.fragment_user = fragment_user;
        this.startWeek = startWeek;
        this.viewCalendar = viewCalendar;
        this.dateFormat = dateFormat;
        this.premium = premium;
        this.premium_expiration_date = premium_expiration_date;
        this.my_coin = my_coin;
        this.status = status;
        this.role = role;
        this.user_notes = user_notes;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getCountLogin() {
        return countLogin;
    }

    public void setCountLogin(int countLogin) {
        this.countLogin = countLogin;
    }

    public int getFragment_user() {
        return fragment_user;
    }

    public void setFragment_user(int fragment_user) {
        this.fragment_user = fragment_user;
    }

    public int getStartWeek() {
        return startWeek;
    }

    public void setStartWeek(int startWeek) {
        this.startWeek = startWeek;
    }

    public int getViewCalendar() {
        return viewCalendar;
    }

    public void setViewCalendar(int viewCalendar) {
        this.viewCalendar = viewCalendar;
    }

    public int getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(int dateFormat) {
        this.dateFormat = dateFormat;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public String getPremium_expiration_date() {
        return premium_expiration_date;
    }

    public void setPremium_expiration_date(String premium_expiration_date) {
        this.premium_expiration_date = premium_expiration_date;
    }

    public int getMy_coin() {
        return my_coin;
    }

    public void setMy_coin(int my_coin) {
        this.my_coin = my_coin;
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

    public String getUser_notes() {
        return user_notes;
    }

    public void setUser_notes(String user_notes) {
        this.user_notes = user_notes;
    }
}
