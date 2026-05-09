package com.mvdragon.weekcalendar.database;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.mvdragon.weekcalendar.model.Event;
import com.mvdragon.weekcalendar.model.MyNote;
import com.mvdragon.weekcalendar.model.User;
import com.mvdragon.weekcalendar.model.ViewImageShared;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class UserUltils {

    //I.1 Lấy user lưu ở Shared
    public static User getUserLocal(Context context){

        //1. Tạo Shared: dùng màn hình (context), tên Shared, MODE_PRIVATE (mặc định - ẩn dữ liệu)
        SharedPreferences sharedPreferences = context.getSharedPreferences("userLocal", MODE_PRIVATE);

        //2. Lấy string json từ shared -> chuyển về object (user) bằng gson
        Gson gson = new Gson();

        //Tạo string json làm user mặc định | viewCalendar (1: 1 tuần, 2: 3 tuần, 3: 1 tháng) | Role: để chọn giao diện layout day name (1: ở trên, 2: ở dưới)
        User userDefault = new User("userName", "email", 0, 0, 2, 3, 1,false, LocalDate.now().toString(), 10, 1, 1, "");
        String jsonUserDefault = gson.toJson(userDefault);

        //Thực hiện lấy user lưu trong shared (lấy luôn mà có thể không cần tạo, vì đã có default)
        String jsonGetUser = sharedPreferences.getString("myUserLocal", jsonUserDefault);
        User getUser = gson.fromJson(jsonGetUser, User.class); //Truyền string json và class của object

        //Trả về user lưu trong shared
        return getUser;
    }

    //I.2 Lưu user (Để sử dụng thì có lẽ sẽ cần lấy trước (getUserLocal) rồi lấy user đó chỉnh sửa và lưu bằng hàm này
    public static void saveUserLocal(Context context, User user){

        //1. Tạo Shared: dùng màn hình (context), tên Shared, MODE_PRIVATE (mặc định - ẩn dữ liệu)
        SharedPreferences sharedPreferences = context.getSharedPreferences("userLocal", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //2. save: Chuyển object về string (json) (bằng gson) -> lưu shared
        Gson gson = new Gson();
        String jsonSaveUser = gson.toJson(user);
        editor.putString("myUserLocal", jsonSaveUser);
        editor.apply();
    }

    //I.3 Remove user - remove xong khi dùng sẽ tự lấy mặc định (Hiện tại chỉ dùng để test)
    public static void resetUserLocal(Context context){

        //1. Tạo Shared: dùng màn hình (context), tên Shared, MODE_PRIVATE (mặc định - ẩn dữ liệu)
        SharedPreferences sharedPreferences = context.getSharedPreferences("userLocal", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //2. Remove user
        editor.remove("myUserLocal");
        editor.apply();
    }

    //II.1 get event lưu ở Shared (dùng ở fragment update event - Hiện chưa sử dụng)
    public static Event getEventShared(Context context){

        //1. Tạo Shared: dùng màn hình (context), tên Shared, MODE_PRIVATE (mặc định - ẩn dữ liệu)
        SharedPreferences sharedPreferences = context.getSharedPreferences("userLocal", MODE_PRIVATE);

        //2. Lấy string json từ shared -> chuyển về object (user) bằng gson
        Gson gson = new Gson();

        //Tạo string json làm user mặc định | viewCalendar (1: 1 tuần, 2: 3 tuần, 3: 1 tháng) | Role: để chọn giao diện layout day name (1: ở trên, 2: ở dưới)
        //Phải có thời gian dạng "" mặc định theo định dạng cho event default do bên add event đã để "" khi không có thời gian chứ không phải là null

        Event eventDefault = new Event(1, "event", LocalDate.now().toString(), "", LocalDateTime.now().toString(), 1, "",1 );
        String jsonEventDefault = gson.toJson(eventDefault);

        //Thực hiện lấy user lưu trong shared (lấy luôn mà có thể không cần tạo, vì đã có default)
        String jsonGetEvent = sharedPreferences.getString("myEventShared", jsonEventDefault);
        Event getEvent = gson.fromJson(jsonGetEvent, Event.class); //Truyền string json và class của object

        //Trả về user lưu trong shared
        return getEvent;
    }

    //II.2 Lưu Event
    public static void saveEventLocal(Context context, Event event){

        //1. Tạo Shared: dùng màn hình (context), tên Shared, MODE_PRIVATE (mặc định - ẩn dữ liệu)
        SharedPreferences sharedPreferences = context.getSharedPreferences("userLocal", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //2. save: Chuyển object về string (json) (bằng gson) -> lưu shared
        Gson gson = new Gson();
        String jsonSaveEvent = gson.toJson(event);
        editor.putString("myEventShared", jsonSaveEvent);
        editor.apply();
    }

    //III.1 Lưu id_folder (Để sử dụng thì có lẽ sẽ cần lấy trước (getUserLocal) rồi lấy user đó chỉnh sửa và lưu bằng hàm này
    public static void saveId_folder (Context context, int id_folder){

        //1. Tạo Shared: dùng màn hình (context), tên Shared, MODE_PRIVATE (mặc định - ẩn dữ liệu)
        SharedPreferences sharedPreferences = context.getSharedPreferences("userLocal", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //2. save, lưu shared
        editor.putInt("id_folder", id_folder);
        editor.apply();
    }

    //III.2 Lấy id_folder lưu ở Shared | Chú ý: Nếu dùng Shared để lưu thì khi load ViewPager của main nó sẽ load luôn ViewPager cho fragment bên cạnh nên có thể sẽ gặp lỗi chưa có dữ liệu (ở ViewPager của fragment bên cạnh, phía trước)
    //Hiện tại về logic vẫn ổn: Nếu mới tạo app thì có 1 folder tạo sẵn với id = 1 | Nếu xoá hết folder thì kiểm tra xem dữ liệu cụ thể (như tên folder) có bị null không rồi mới dùng -> Hiện tại không bị lỗi nữa
    public static int getId_folder(Context context){

        //1. Tạo Shared: dùng màn hình (context), tên Shared, MODE_PRIVATE (mặc định - ẩn dữ liệu)
        SharedPreferences sharedPreferences = context.getSharedPreferences("userLocal", MODE_PRIVATE);

        //2. Lấy dữ liệu từ shared
        int id_folder = sharedPreferences.getInt("id_folder", 1);

        //Trả về user lưu trong shared
        return id_folder;
    }

    //IV.1 Lưu selectDate (Dùng cho fragment add event - Vì bị load trước nên chưa chọn được ngày select so với fragment calendar)
    public static void saveSelectDate (Context context, String selectDate){

        //1. Tạo Shared: dùng màn hình (context), tên Shared, MODE_PRIVATE (mặc định - ẩn dữ liệu)
        SharedPreferences sharedPreferences = context.getSharedPreferences("userLocal", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //2. save, lưu shared
        editor.putString("selectDate", selectDate);
        editor.apply();
    }

    //IV.2 Lấy selectDate lưu local
    public static LocalDate getSelectDate(Context context){

        //1. Tạo Shared: dùng màn hình (context), tên Shared, MODE_PRIVATE (mặc định - ẩn dữ liệu)
        SharedPreferences sharedPreferences = context.getSharedPreferences("userLocal", MODE_PRIVATE);

        //2. Lấy dữ liệu từ shared
        String selectDateString = sharedPreferences.getString("selectDate", LocalDate.now().toString());
        LocalDate selectDate = LocalDate.parse(selectDateString);

        //Trả về user lưu trong shared
        return selectDate;
    }

    //V.1 Get note lưu ở Shared (dùng ở fragment update event - Hiện chưa sử dụng)
    public static MyNote getNoteShared(Context context){

        //1. Tạo Shared: dùng màn hình (context), tên Shared, MODE_PRIVATE (mặc định - ẩn dữ liệu)
        SharedPreferences sharedPreferences = context.getSharedPreferences("userLocal", MODE_PRIVATE);

        //2. Lấy string json từ shared -> chuyển về object (user) bằng gson
        Gson gson = new Gson();

        //Tạo string json làm MyNote mặc định
        MyNote noteDefault = new MyNote();
        String jsonDefault = gson.toJson(noteDefault);

        //Thực hiện lấy user lưu trong shared (lấy luôn mà có thể không cần tạo, vì đã có default)
        String jsonGetNote = sharedPreferences.getString("myNoteShared", jsonDefault);
        MyNote myNote = gson.fromJson(jsonGetNote, MyNote.class); //Truyền string json và class của object

        //Trả về user lưu trong shared
        return myNote;
    }

    //V.2 Lưu note
    public static void saveNoteLocal(Context context, MyNote myNote){

        //1. Tạo Shared: dùng màn hình (context), tên Shared, MODE_PRIVATE (mặc định - ẩn dữ liệu)
        SharedPreferences sharedPreferences = context.getSharedPreferences("userLocal", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //2. save: Chuyển object về string (json) (bằng gson) -> lưu shared
        Gson gson = new Gson();
        String jsonSaveEvent = gson.toJson(myNote);
        editor.putString("myNoteShared", jsonSaveEvent);
        editor.apply();
    }

    //VI.1 get event lưu ở Shared (dùng ở fragment update event - Hiện chưa sử dụng)
    public static ViewImageShared getViewImageShared(Context context){

        //1. Tạo Shared: dùng màn hình (context), tên Shared, MODE_PRIVATE (mặc định - ẩn dữ liệu)
        SharedPreferences sharedPreferences = context.getSharedPreferences("userLocal", MODE_PRIVATE);

        //2. Lấy string json từ shared -> chuyển về object (user) bằng gson
        Gson gson = new Gson();

        //Tạo string json mặc định (nếu chưa có)
        ViewImageShared viewImageSharedDefault = new ViewImageShared();
        String jsonViewImageSharedDefault = gson.toJson(viewImageSharedDefault);

        //Thực hiện lấy user lưu trong shared (nếu chưa có thì dùng default)
        String jsonGetViewImageShared = sharedPreferences.getString("ViewImageShared", jsonViewImageSharedDefault);
        ViewImageShared getViewImageShared = gson.fromJson(jsonGetViewImageShared, ViewImageShared.class); //Truyền string json và class của object

        //Trả về user lưu trong shared
        return getViewImageShared;
    }

    //VI.2 Lưu Event
    public static void saveViewImageLocal(Context context, ViewImageShared viewImageShared){

        //1. Tạo Shared: dùng màn hình (context), tên Shared, MODE_PRIVATE (mặc định - ẩn dữ liệu)
        SharedPreferences sharedPreferences = context.getSharedPreferences("userLocal", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //2. save: Chuyển object về string (json) (bằng gson) -> lưu shared
        Gson gson = new Gson();
        String jsonSave = gson.toJson(viewImageShared);
        editor.putString("ViewImageShared", jsonSave);
        editor.apply();
    }
}
