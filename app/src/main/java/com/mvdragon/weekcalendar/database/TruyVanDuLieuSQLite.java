package com.mvdragon.weekcalendar.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import androidx.annotation.NonNull;

import com.mvdragon.weekcalendar.model.Event;
import com.mvdragon.weekcalendar.model.ImageEvent;
import com.mvdragon.weekcalendar.model.ImageNote;
import com.mvdragon.weekcalendar.model.MyFolder;
import com.mvdragon.weekcalendar.model.MyNote;
import com.mvdragon.weekcalendar.model.MyNoteAvatar;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TruyVanDuLieuSQLite {
    private Context context;
    public DatabaseSQLite databaseSQLite;

    //Khởi tạo: truyền màn hình Context, khai báo SQLite (Sử dụng context được truyền vào, tên CSDL ,khai báo bảng dữ liệu)
    public TruyVanDuLieuSQLite(Context context) {
        this.context = context;
        databaseSQLite = new DatabaseSQLite(context, "weekcalendarmvdragon.sqlite", null, 1);
        //Phần bảng của event
        databaseSQLite.WriteData("CREATE TABLE IF NOT EXISTS EventOfDay (id_event INTEGER PRIMARY KEY AUTOINCREMENT, event_name TEXT, event_date TEXT, event_time TEXT, date_time_record TEXT, event_status INTEGER, event_notes TEXT, event_notify INTEGER)");
        databaseSQLite.WriteData("CREATE TABLE IF NOT EXISTS ImageEvent (id_image_event INTEGER PRIMARY KEY AUTOINCREMENT, id_event INTEGER, event_image BLOB, date_time_save TEXT)");
        //Phần bảng của folder (chứa các note)
        databaseSQLite.WriteData("CREATE TABLE IF NOT EXISTS MyFolder (id_folder INTEGER PRIMARY KEY AUTOINCREMENT, folder_name TEXT, folder_notes TEXT, status INTEGER, folder_role INTEGER, create_datetime TEXT)");
        databaseSQLite.WriteData("CREATE TABLE IF NOT EXISTS MyNote (id_note INTEGER PRIMARY KEY AUTOINCREMENT, id_folder INTEGER, note_name TEXT, note_content TEXT, status INTEGER, role INTEGER, create_datetime TEXT)");
        databaseSQLite.WriteData("CREATE TABLE IF NOT EXISTS MyNoteAvatar (id_note_avatar INTEGER PRIMARY KEY AUTOINCREMENT, id_note INTEGER, note_avatar BLOB, status INTEGER)");
        databaseSQLite.WriteData("CREATE TABLE IF NOT EXISTS ImageNote (id_image_note INTEGER PRIMARY KEY AUTOINCREMENT, id_note INTEGER, note_image BLOB, date_time_save TEXT)");
    }

    /*
    I. TRUY VẤN  Folder
     */

    //1. Truy vấn danh sách thư mục hiện có (sắp xếp thứ tự)
    public ArrayList<MyFolder> getAllFolder(){

        String SQLite = "Select * from MyFolder order by folder_name ASC, create_datetime DESC";
        Cursor cursor = databaseSQLite.ReadData(SQLite);

        ArrayList<MyFolder> myFolderArrayList = new ArrayList<>();

        //Khi dùng cursor lấy về bảng tạm thì lấy từ 0 trở lên
        while (cursor.moveToNext()){

            MyFolder myFolder = new MyFolder();

            myFolder.setId_folder(cursor.getInt(0)); //id_folder là cột đầu tiên trong bảng (xem lệnh tạo bảng)
            myFolder.setFolder_name(cursor.getString(1));
            myFolder.setFolder_notes(cursor.getString(2));
            myFolder.setStatus(cursor.getInt(3));
            myFolder.setRole(cursor.getInt(4));
            myFolder.setCreate_datetime(cursor.getString(5));

            //Thêm vào list
            myFolderArrayList.add(myFolder);
        }

        //Đóng con trỏ lại, nếu không có thể sẽ bị lỗi khi dùng nhiều truy vấn
        cursor.close();

        return myFolderArrayList;
    }

    //2. Thêm một folder mới
    public void createNewFolder(MyFolder myFolder){

        //a. Chuẩn bị câu lệnh, hàm thực hiện (id tự động tăng nên để null)
        String SQLite = "Insert into MyFolder Values(null,?,?,?,?,?)";
        SQLiteStatement statement = databaseSQLite.getWritableDatabase().compileStatement(SQLite);
        statement.clearBindings(); //làm mới bộ nhớ

        //b. Khi dùng statement (để write) thì bind từ 1 trở đi (id để tự động nên không cần bind mà để null, create_datetime tạo tại đây nên không phụ thuộc đầu vào)
        //Tên folder
        statement.bindString(1, myFolder.getFolder_name());
        //Nội dung ghi chú mô tả nếu có
        statement.bindString(2, myFolder.getFolder_notes());
        //Trạng thái
        statement.bindLong(3, myFolder.getStatus());
        //role
        statement.bindLong(4, myFolder.getRole());
        //Thời gian (hiện tại) tạo folder
        LocalDateTime localDateTime = LocalDateTime.now();
        statement.bindString(5, localDateTime.toString());

        //c. Thực hiện và đóng luồng
        statement.executeInsert();
        statement.close();
    }

    //3. Lấy thông tin 1 folder
    public MyFolder getOneFolder(int id_folder){

        String SQLite = "Select * from MyFolder where id_folder = " + id_folder ;
        Cursor cursor = databaseSQLite.ReadData(SQLite);

        MyFolder myFolder = new MyFolder();

        //Khi dùng cursor lấy về bảng tạm thì lấy từ 0 trở lên
        while (cursor.moveToNext()){

            myFolder.setId_folder(cursor.getInt(0)); //id_image_event là cột đầu tiên trong bảng (xem lệnh tạo bảng)
            myFolder.setFolder_name(cursor.getString(1));
            myFolder.setFolder_notes(cursor.getString(2));
            myFolder.setStatus(cursor.getInt(3));
            myFolder.setRole(cursor.getInt(4));
            myFolder.setCreate_datetime(cursor.getString(5));

        }

        //Đóng con trỏ lại, nếu không có thể sẽ bị lỗi khi dùng nhiều truy vấn
        cursor.close();

        return myFolder;
    }

    //4. Update Folder
    public void updateFolder(MyFolder myFolder){

        //a. Chuẩn bị câu lệnh, hàm thực hiện (id tự động tăng nên để null)
        String SQLite = "Update MyFolder set folder_name = ?, create_datetime = ? where id_folder = ?";
        SQLiteStatement statement = databaseSQLite.getWritableDatabase().compileStatement(SQLite);
        statement.clearBindings(); //làm mới bộ nhớ

        //b. Đặt các thông số cho câu lệnh (bắt đầu từ dấu ? đầu tiên là 1)
        //Tên folder
        statement.bindString(1, myFolder.getFolder_name());

        //Thời gian (hiện tại) tạo folder
        LocalDateTime localDateTime = LocalDateTime.now();
        statement.bindString(2, localDateTime.toString());

        //id_folder
        statement.bindLong(3, myFolder.getId_folder());

        //c. Thực hiện và đóng luồng
        statement.executeInsert();
        statement.close();
    }

    //5.1 Xoá 1 folder (bao gồm tất cả các notes con bên trong và ảnh kèm notes)
    public void deleteOneFolder(int id_folder){

        //A. Xoá note bên trong folder và dữ liệu đi kèm (avatar, list image) trước
        deleteAllNoteOfFolder(id_folder);

        //B. Xoá dữ liệu ở bảng MyFolder (chỉ có 1 dòng tên)
        //a. Chuẩn bị câu lệnh, hàm thực hiện (id tự động tăng nên để null)
        String SQLite = "Delete from MyFolder where id_folder = ?";
        SQLiteStatement statement = databaseSQLite.getWritableDatabase().compileStatement(SQLite);
        statement.clearBindings(); //làm mới bộ nhớ

        //b. Đặt các thông số cho câu lệnh (bắt đầu từ dấu ? đầu tiên là 1)
        //Tên folder
        statement.bindLong(1, id_folder);

        //c. Thực hiện và đóng luồng
        statement.executeInsert();
        statement.close();
    }

    //5.2 Xoá tất cả note (bao gồm cả avatar và image đi kèm) của một folder
    public void deleteAllNoteOfFolder(int id_folder){

        //Lấy danh sách toàn bộ note của một id_folder và thực hiện xoá từng note
        ArrayList<MyNote> myNoteArrayList = getAllNoteOfFolder(id_folder);

        for(int i=0; i<myNoteArrayList.size(); i++){
            deleteOneMyNote(myNoteArrayList.get(i).getId_note());
        }
    }

    /*
    II. TRUY VẤN  note (Của folder)
     */

    //1.1 Tạo một note mới (không có avatar), có list ảnh đi kèm
    public void createNewNote(MyNote myNote, ArrayList<ImageNote> imageNoteArrayList){
        //1. Tạo note mới
        //a. Chuẩn bị câu lệnh, hàm thực hiện (id tự động tăng nên để null)
        String SQLite = "Insert into MyNote Values(null,?,?,?,?,?,?)";
        SQLiteStatement statement = databaseSQLite.getWritableDatabase().compileStatement(SQLite);
        statement.clearBindings(); //làm mới bộ nhớ

        //b. Khi dùng statement (để write) thì bind từ 1 trở đi là cho dấu ? đầu tiên (id để tự động nên không cần bind mà để null, create_datetime tạo tại đây nên không phụ thuộc đầu vào)
        //Tên folder
        statement.bindLong(1, myNote.getId_folder());
        //Nội dung ghi chú mô tả nếu có
        statement.bindString(2, myNote.getNote_name());
        //Trạng thái
        statement.bindString(3, myNote.getNote_content());
        //status
        statement.bindLong(4, myNote.getStatus());
        //role
        statement.bindLong(5, myNote.getRole());
        //Thời gian (hiện tại)
        LocalDateTime localDateTime = LocalDateTime.now();
        statement.bindString(6, localDateTime.toString());

        //c. Thực hiện và đóng luồng
        statement.executeInsert();
        statement.close();

        //2. Lưu list ảnh đi kèm nếu có
        if(imageNoteArrayList.size() >0){

            //Lấy id của new note vừa thêm (vừa được tạo) để tiếp tục thêm list ảnh
            String lastIdNote = "SELECT last_insert_rowid()";
            Cursor cursor = databaseSQLite.ReadData(lastIdNote);

            int last_id_note = 0;
            if (cursor.moveToNext()){
                last_id_note = cursor.getInt(0); //Lấy từ cột thứ 0
            }

            //Thực hiện thêm list ảnh vào bảng ImageNote
            for(int i=0; i< imageNoteArrayList.size(); i++){
                insertOneImageNote(imageNoteArrayList.get(i), last_id_note);
            }
        }
    }

    //1.2 Tạo một note mới (Có avatar) có list ảnh đi kèm
    public void createNewNote(MyNote myNote, MyNoteAvatar myNoteAvatar, ArrayList<ImageNote> imageNoteArrayList){
        //1. Thêm note mới
        //a. Chuẩn bị câu lệnh, hàm thực hiện (id tự động tăng nên để null)
        String SQLite = "Insert into MyNote Values(null,?,?,?,?,?,?)";
        SQLiteStatement statement = databaseSQLite.getWritableDatabase().compileStatement(SQLite);
        statement.clearBindings(); //làm mới bộ nhớ

        //b. Khi dùng statement (để write) thì bind từ 1 trở đi là cho dấu ? đầu tiên (id để tự động nên không cần bind mà để null, create_datetime tạo tại đây nên không phụ thuộc đầu vào)
        //Tên folder
        statement.bindLong(1, myNote.getId_folder());
        //Nội dung ghi chú mô tả nếu có
        statement.bindString(2, myNote.getNote_name());
        //Trạng thái
        statement.bindString(3, myNote.getNote_content());
        //status
        statement.bindLong(4, myNote.getStatus());
        //role
        statement.bindLong(5, myNote.getRole());
        //Thời gian (hiện tại)
        LocalDateTime localDateTime = LocalDateTime.now();
        statement.bindString(6, localDateTime.toString());

        //c. Thực hiện và đóng luồng
        statement.executeInsert();
        statement.close();

        //2. Thêm avatar cho note vừa thêm
        //Lấy id của note vừa thêm để tiếp tục thêm vào bảng MyNoteAvatar
        String lastIdNoteSQLite = "SELECT last_insert_rowid()";
        Cursor cursor = databaseSQLite.ReadData(lastIdNoteSQLite);

        int last_id_note = 0;
        if (cursor.moveToNext()){
            last_id_note = cursor.getInt(0); //Lấy từ cột thứ 0
        }

        //Thêm dữ liệu (id_note) cho myNoteAvatar và thêm vào bảng MyNoteAvatar
        myNoteAvatar.setId_note(last_id_note);
        insertMyNoteAvatar(myNoteAvatar);

        //3. Lưu list ảnh đi kèm nếu có
        if(imageNoteArrayList.size() >0){
            //Thực hiện thêm list ảnh vào bảng ImageNote
            for(int i=0; i< imageNoteArrayList.size(); i++){
                insertOneImageNote(imageNoteArrayList.get(i), last_id_note);
            }
        }
    }

    //1.3 Thêm avatar cho note
    public void insertMyNoteAvatar(MyNoteAvatar myNoteAvatar){
        //a. Chuẩn bị câu lệnh, hàm thực hiện (id tự động tăng nên để null)
        String SQLite = "Insert into MyNoteAvatar Values(null,?,?,?)";
        SQLiteStatement statement = databaseSQLite.getWritableDatabase().compileStatement(SQLite);
        statement.clearBindings(); //làm mới bộ nhớ

        //b. Khi dùng statement (để write) thì bind từ 1 trở đi là cho dấu ? đầu tiên (id để tự động nên không cần bind mà để null)
        //id_note
        statement.bindLong(1, myNoteAvatar.getId_note());
        //ảnh
        statement.bindBlob(2, myNoteAvatar.getNote_avatar());
        //status
        statement.bindLong(3, myNoteAvatar.getStatus());

        //c. Thực hiện và đóng luồng
        statement.executeInsert();
        statement.close();
    }

    //1.4 Update avatar cho note (đã tồn tại) | Chú ý: Nếu note trong CSDL chưa có avatar thì phải thêm mới, nếu có rồi thì mới dùng hàm này
    public void updateMyNoteAvatar(MyNoteAvatar myNoteAvatar){
        //a. Chuẩn bị câu lệnh, hàm thực hiện (id tự động tăng nên để null)
        String SQLite = "Update MyNoteAvatar set note_avatar = ?, status = ? where id_note_avatar = ?";
        SQLiteStatement statement = databaseSQLite.getWritableDatabase().compileStatement(SQLite);
        statement.clearBindings(); //làm mới bộ nhớ

        //b. Khi dùng statement (để write) thì bind từ 1 trở đi là cho dấu ? đầu tiên
        //avatar
        statement.bindBlob(1, myNoteAvatar.getNote_avatar());
        //status
        statement.bindLong(2, myNoteAvatar.getStatus());
        //id của chính nó
        statement.bindLong(3, myNoteAvatar.getId_note_avatar());

        //c. Thực hiện và đóng luồng
        statement.executeInsert();
        statement.close();
    }

    //2.1 Check xem một note có avatar không
    public boolean checkMyNoteAvatar(int id_note){

        String SQLite = "Select * from MyNoteAvatar where id_note = " + id_note;
        Cursor cursor = databaseSQLite.ReadData(SQLite);

        int count = 0;
        while (cursor.moveToNext()){
            count++;
        }

        //Đóng con trỏ lại, nếu không có thể sẽ bị lỗi khi dùng nhiều truy vấn
        cursor.close();

        return count > 0;
    }

    //2.2 Lấy avatar của một note
    public MyNoteAvatar getMyNoteAvatar(int id_note){

        String SQLite = "Select * from MyNoteAvatar where id_note = " + id_note;
        Cursor cursor = databaseSQLite.ReadData(SQLite);

        MyNoteAvatar myNoteAvatar = new MyNoteAvatar();

        //Khi dùng cursor lấy về bảng tạm thì lấy từ 0 trở lên
        while (cursor.moveToNext()){
            myNoteAvatar.setId_note_avatar(cursor.getInt(0)); //id_image_event là cột đầu tiên trong bảng (xem lệnh tạo bảng)
            myNoteAvatar.setId_note(cursor.getInt(1));
            myNoteAvatar.setNote_avatar(cursor.getBlob(2));
            myNoteAvatar.setStatus(cursor.getInt(3));
        }

        //Đóng con trỏ lại, nếu không có thể sẽ bị lỗi khi dùng nhiều truy vấn
        cursor.close();

        return myNoteAvatar;
    }


    //3. Delete hoàn toàn 1 note: Xoá dữ liệu trong bảng MyNote, đồng thời avatar của nó trong MyNoteAvatar và list ảnh đi kèm | Bảng riêng nên thứ tự nào cũng có thể thực hiện được, nhưng nên để đúng thứ tự tư duy
    public void deleteOneMyNote(int id_note){

        //a. Xoá avatar (trong bảng MyNoteAvatar)
        deleteOneMyNoteAvatar(id_note);

        //b. Xoá list ảnh ImageNote (Bảng ImageNote)
        deleteListImageNoteOfOneNote(id_note);

        //c. Xoá note (trong bảng MyNote)
        String SQLite = "Delete from MyNote where id_note = " + id_note;
        databaseSQLite.WriteData(SQLite);
    }

    //4.1 Delete avatar của một note
    public void deleteOneMyNoteAvatar(int id_note){
        String SQLite = "Delete from MyNoteAvatar where id_note = " + id_note;
        databaseSQLite.WriteData(SQLite);
    }

    //4.2 Delete toàn bộ ảnh của 1 note
    public void deleteListImageNoteOfOneNote(int id_note){
        String SQLite = "Delete from ImageNote where id_note = " + id_note;
        databaseSQLite.WriteData(SQLite);
    }

    //5. Xoá tất cả note (vẫn giữ folder): Xoá toàn bộ notes, avatar, list image (Chọn thứ tự xoá hợp lý)
    public void deleteAllNotes(){

        String SQLite3 = "Delete from ImageNote";
        databaseSQLite.WriteData(SQLite3);

        String SQLite2 = "Delete from MyNoteAvatar";
        databaseSQLite.WriteData(SQLite2);

        String SQLite1 = "Delete from MyNote";
        databaseSQLite.WriteData(SQLite1);
    }

    //6. Lấy danh sách toàn bộ note của 1 folder
    public ArrayList<MyNote> getAllNoteOfFolder(int id_folder){

        String SQLite = "Select * from MyNote where id_folder = " + id_folder + " order by note_name ASC, create_datetime DESC";
        Cursor cursor = databaseSQLite.ReadData(SQLite);

        ArrayList<MyNote> myNoteArrayList = new ArrayList<>();

        //Khi dùng cursor lấy về bảng tạm thì lấy từ 0 trở lên
        while (cursor.moveToNext()){

            MyNote myNote = new MyNote();

            myNote.setId_note(cursor.getInt(0)); //id_image_event là cột đầu tiên trong bảng (xem lệnh tạo bảng)
            myNote.setId_folder(cursor.getInt(1));
            myNote.setNote_name(cursor.getString(2));
            myNote.setNote_content(cursor.getString(3));
            myNote.setStatus(cursor.getInt(4));
            myNote.setRole(cursor.getInt(5));
            myNote.setCreate_datetime(cursor.getString(6));

            //Thêm vào list
            myNoteArrayList.add(myNote);
        }

        //Đóng con trỏ lại, nếu không có thể sẽ bị lỗi khi dùng nhiều truy vấn
        cursor.close();

        return myNoteArrayList;
    }

    //6. Lấy dữ liệu 1 note của 1 folder
    public MyNote getOneMyNote(int id_note){

        String SQLite = "Select * from MyNote where id_note = " + id_note;
        Cursor cursor = databaseSQLite.ReadData(SQLite);

        MyNote myNote = new MyNote();

        //Khi dùng cursor lấy về bảng tạm thì lấy từ 0 trở lên
        while (cursor.moveToNext()){
            myNote.setId_note(cursor.getInt(0)); //id_image_event là cột đầu tiên trong bảng (xem lệnh tạo bảng)
            myNote.setId_folder(cursor.getInt(1));
            myNote.setNote_name(cursor.getString(2));
            myNote.setNote_content(cursor.getString(3));
            myNote.setStatus(cursor.getInt(4));
            myNote.setRole(cursor.getInt(5));
            myNote.setCreate_datetime(cursor.getString(6));
        }

        //Đóng con trỏ lại, nếu không có thể sẽ bị lỗi khi dùng nhiều truy vấn
        cursor.close();

        return myNote;
    }

    //7. Lấy danh sách toàn bộ note của tất cả folder
    public ArrayList<MyNote> getAllNote(){

        String SQLite = "Select * from MyNote order by note_name ASC, create_datetime DESC";
        Cursor cursor = databaseSQLite.ReadData(SQLite);

        ArrayList<MyNote> myNoteArrayList = new ArrayList<>();

        //Khi dùng cursor lấy về bảng tạm thì lấy từ 0 trở lên
        while (cursor.moveToNext()){

            MyNote myNote = new MyNote();

            myNote.setId_note(cursor.getInt(0)); //id_image_event là cột đầu tiên trong bảng (xem lệnh tạo bảng)
            myNote.setId_folder(cursor.getInt(1));
            myNote.setNote_name(cursor.getString(2));
            myNote.setNote_content(cursor.getString(3));
            myNote.setStatus(cursor.getInt(4));
            myNote.setRole(cursor.getInt(5));
            myNote.setCreate_datetime(cursor.getString(6));

            //Thêm vào list
            myNoteArrayList.add(myNote);
        }

        //Đóng con trỏ lại, nếu không có thể sẽ bị lỗi khi dùng nhiều truy vấn
        cursor.close();

        return myNoteArrayList;
    }

    //8. Update dữ liệu của 1 note
    public void updateOneNote(MyNote myNote){

        //a. Chuẩn bị câu lệnh, hàm thực hiện (id tự động tăng nên để null)
        String SQLite = "Update MyNote set note_name = ?, note_content = ?, status = ?, role = ?, create_datetime = ? where id_note = ?";
        SQLiteStatement statement = databaseSQLite.getWritableDatabase().compileStatement(SQLite);
        statement.clearBindings(); //làm mới bộ nhớ

        //b. Khi dùng statement (để write) thì bind từ 1 trở đi là cho dấu ? đầu tiên
        //name
        statement.bindString(1, myNote.getNote_name());
        //Trạng thái
        statement.bindString(2, myNote.getNote_content());
        //status
        statement.bindLong(3, myNote.getStatus());
        //role
        statement.bindLong(4, myNote.getRole());
        //Thời gian (hiện tại)
        LocalDateTime localDateTime = LocalDateTime.now();
        statement.bindString(5, localDateTime.toString());
        //id_note
        statement.bindLong(6, myNote.getId_note());

        //c. Thực hiện và đóng luồng
        statement.executeInsert();
        statement.close();
    }

    /*
    III. TRUY VẤN PHẦN ẢNH CỦA 1 NOTE
     */

    //1.1 Thêm 1 ảnh cho 1 note (Thêm nhiều ảnh cho 1 note thì dùng hàm for cho list)
    public void insertOneImageNote(ImageNote imageNote, int id_note){

        //a. Chuẩn bị câu lệnh, hàm thực hiện (id tự động tăng nên để null)
        String SQLite = "Insert into ImageNote Values (null,?,?,?)"; //id tự động tăng nên không cần điền tham số (để null)
        SQLiteStatement statement = databaseSQLite.getWritableDatabase().compileStatement(SQLite);
        statement.clearBindings(); //làm mới bộ nhớ

        //b. Khi dùng statement (để write) thì bind từ 1 trở đi là cho dấu "?" đầu tiên
        //id_note
        statement.bindLong(1, id_note);
        //image
        statement.bindBlob(2, imageNote.getNote_image());
        //Thời gian (hiện tại)
        LocalDateTime localDateTime = LocalDateTime.now();
        statement.bindString(3, localDateTime.toString());

        //c. Thực hiện và đóng luồng
        statement.executeInsert();
        statement.close();
    }

    //1.2 Thêm list ảnh cho 1 note (Dùng hàm for cho list)
    public void insertListImageNote(ArrayList<ImageNote> imageNoteArrayList, int id_note){

        for (int i=0; i<imageNoteArrayList.size(); i++){
            insertOneImageNote(imageNoteArrayList.get(i), id_note);
        }

    }

    //2.1 Lấy danh sách toàn bộ ảnh của một note (bao gồm cả ảnh)
    public ArrayList<ImageNote> getListImageNote(int id_note){
        String SQLite = "Select * from ImageNote where id_note = " + id_note + " order by date_time_save ASC, id_image_note ASC";
        Cursor cursor = databaseSQLite.ReadData(SQLite);

        ArrayList<ImageNote> imageNoteArrayList = new ArrayList<>();

        //Khi dùng cursor lấy về bảng tạm thì lấy từ 0 trở lên
        while (cursor.moveToNext()){

            ImageNote imageNote = new ImageNote();

            imageNote.setId_image_note(cursor.getInt(0)); //id_image_event là cột đầu tiên trong bảng (xem lệnh tạo bảng)
            imageNote.setId_note(cursor.getInt(1));
            imageNote.setNote_image(cursor.getBlob(2));
            imageNote.setDate_time_save(cursor.getString(3));

            //Thêm vào list
            imageNoteArrayList.add(imageNote);
        }

        //Đóng con trỏ lại, nếu không có thể sẽ bị lỗi khi dùng nhiều truy vấn
        cursor.close();

        return imageNoteArrayList;
    }

    //2.2 Lấy danh sách thông tin toàn bộ ảnh của một note (KHÔNG bao gồm ảnh)
    public ArrayList<ImageNote> getListImageNoteProfile(int id_note){
        String SQLite = "Select * from ImageNote where id_note = " + id_note + " order by date_time_save ASC, id_image_note ASC";
        Cursor cursor = databaseSQLite.ReadData(SQLite);

        ArrayList<ImageNote> imageNoteArrayList = new ArrayList<>();

        //Khi dùng cursor lấy về bảng tạm thì lấy từ 0 trở lên
        while (cursor.moveToNext()){

            ImageNote imageNote = new ImageNote();

            imageNote.setId_image_note(cursor.getInt(0)); //id_image_event là cột đầu tiên trong bảng (xem lệnh tạo bảng)
            imageNote.setId_note(cursor.getInt(1));
//            imageNote.setNote_image(cursor.getBlob(2)); //Không lấy ảnh
            imageNote.setDate_time_save(cursor.getString(3));

            //Thêm vào list
            imageNoteArrayList.add(imageNote);
        }

        //Đóng con trỏ lại, nếu không có thể sẽ bị lỗi khi dùng nhiều truy vấn
        cursor.close();

        return imageNoteArrayList;
    }

    //2.2 Lấy 1 ảnh của một note (bao gồm ảnh)
    public ImageNote getOneImageNote(int id_image_note){
        String SQLite = "Select * from ImageNote where id_image_note = " + id_image_note;
        Cursor cursor = databaseSQLite.ReadData(SQLite);

        ImageNote imageNote = new ImageNote();

        //Khi dùng cursor lấy về bảng tạm thì lấy từ 0 trở lên
        while (cursor.moveToNext()){
            imageNote.setId_image_note(cursor.getInt(0)); //id_image_event là cột đầu tiên trong bảng (xem lệnh tạo bảng)
            imageNote.setId_note(cursor.getInt(1));
            imageNote.setNote_image(cursor.getBlob(2)); //ảnh
            imageNote.setDate_time_save(cursor.getString(3));
        }

        //Đóng con trỏ lại, nếu không có thể sẽ bị lỗi khi dùng nhiều truy vấn
        cursor.close();

        return imageNote;
    }

    //3. Xoá toàn bộ ảnh của một note
    public void deleteAllImageOneNote(int id_note){

        //a. Chuẩn bị câu lệnh, hàm thực hiện (Có thể dùng databaseSQLite.WriteData(SQLite) cho nhanh gọn, nhưng ở đây muốn sử dụng thêm câu lệnh kiểu hay dùng để insert nhiều tham số)
        String SQLite = "Delete from ImageNote where id_note = ?";
        SQLiteStatement statement = databaseSQLite.getWritableDatabase().compileStatement(SQLite);
        statement.clearBindings(); //làm mới bộ nhớ

        //b. Khi dùng statement (để write) thì bind từ 1 trở đi là cho dấu "?" đầu tiên
        //id_note
        statement.bindLong(1, id_note);

        //c. Thực hiện và đóng luồng
        statement.executeInsert();
        statement.close();
    }

    //4. Đếm số lượng ảnh của 1 note
    public int getCountImageOfNote(int id_note){
        String SQLite = "Select * from ImageNote where id_note = " + id_note;
        Cursor cursor = databaseSQLite.ReadData(SQLite);

        int count = 0;

        //Đếm số hàng (cho mỗi lần moveToNext)
        while (cursor.moveToNext()){
            count ++;
        }

        //Đóng con trỏ lại, nếu không có thể sẽ bị lỗi khi dùng nhiều truy vấn
        cursor.close();

        return count;
    }


    /*
    IV. TRUY VẤN PHẦN EVENT
     */
    //1.1 Thêm event vào bảng EventOfDay
    public void InsertEventToTable (@NonNull Event event, List<ImageEvent> imageEventList) throws IOException {

        String SQLite = "INSERT INTO EventOfDay VALUES (null, " +
                "'" + event.getEvent_name() + "', " +
                "'" + event.getEvent_date() + "', " +
                "'" + event.getEvent_time() + "', " +
                "'" + event.getDate_time_record() + "', " +
                event.getEvent_status() + ", " +
                "'" + event.getEvent_notes() + "', " +
                event.getEvent_notify() +  ")";

        databaseSQLite.WriteData(SQLite);

        //Lấy id của event vừa thêm để tiếp tục thêm vào bảng ImageEvent
        String SQLiteGetLastIdEvent = "SELECT last_insert_rowid()";
        Cursor cursor = databaseSQLite.ReadData(SQLiteGetLastIdEvent);

        int last_id_event = 0;
        if (cursor.moveToNext()){
            last_id_event = cursor.getInt(0); //Lấy từ cột thứ 0
        }

        //Thêm danh sách ảnh của event vào bảng ImageEvent (Nếu có ảnh trong list)
        if(!imageEventList.isEmpty()) {
            for (int i = 0; i < imageEventList.size(); i++) {
                InsertOneImageEvent(last_id_event, imageEventList.get(i));
            }
        }

        //Đóng con trỏ lại, nếu không có thể sẽ bị lỗi khi dùng nhiều
        cursor.close();

    }

    //1.2 Thêm 1 ảnh của event vào bảng ImageEvent
    public void InsertOneImageEvent(int id_event, ImageEvent imageEvent) throws IOException {

        //a. Chuẩn bị câu lệnh, hàm thực hiện
        String SQLite = "Insert into ImageEvent Values(null,?,?,?)";
        SQLiteStatement statement = databaseSQLite.getWritableDatabase().compileStatement(SQLite);
        statement.clearBindings(); //làm mới bộ nhớ

        //b. id của event đang được insert
        statement.bindLong(1, id_event);

        //c. Ảnh mảng byte array (Insert bằng bindBlob)
        statement.bindBlob(2, imageEvent.getEvent_image());

        //d. Thời gian (hiện tại) lưu hình ảnh
        LocalDateTime localDateTime = LocalDateTime.now();
        statement.bindString(3, localDateTime.toString());

        //e. Thực hiện và đóng luồng
        statement.executeInsert();
        statement.close();
    }

    //1.3 Lấy danh sách Ảnh của 1 event
    public ArrayList<ImageEvent> getListImageEvent(int id_event){
        String SQLite = "Select * from ImageEvent where id_event = " + id_event;
        Cursor cursor = databaseSQLite.ReadData(SQLite);

        ArrayList<ImageEvent> imageEventList = new ArrayList<>();

        while (cursor.moveToNext()){
            ImageEvent imageEvent = new ImageEvent();
            imageEvent.setId_image_event(cursor.getInt(0)); //id_image_event là cột đầu tiên trong bảng (xem lệnh tạo bảng)
            imageEvent.setId_event(cursor.getInt(1));

            //Lấy ảnh về chính là dạng byte array (dùng getBlob) - Nếu không dùng được thì chuyển blob -> array rồi set
            imageEvent.setEvent_image(cursor.getBlob(2));
            imageEvent.setDate_time_save(cursor.getString(3));

            //Thêm vào list
            imageEventList.add(imageEvent);
        }

        //Đóng con trỏ lại, nếu không có thể sẽ bị lỗi khi dùng nhiều
        cursor.close();

        return imageEventList;
    }

    //1.4 Lấy danh sách Ảnh của 1 event (Chỉ lấy thông tin, không lấy ảnh để giảm tải và sẽ lấy từng ảnh một sau)
    public ArrayList<ImageEvent> getListProfileImageEvent(int id_event){
        String SQLite = "Select * from ImageEvent where id_event = " + id_event;
        Cursor cursor = databaseSQLite.ReadData(SQLite);

        ArrayList<ImageEvent> imageEventList = new ArrayList<>();

        while (cursor.moveToNext()){
            ImageEvent imageEvent = new ImageEvent();
            imageEvent.setId_image_event(cursor.getInt(0)); //id_image_event là cột đầu tiên trong bảng (xem lệnh tạo bảng)
            imageEvent.setId_event(cursor.getInt(1));

            //Lấy ảnh về chính là dạng byte array (dùng getBlob) - Nếu không dùng được thì chuyển blob -> array rồi set
            imageEvent.setEvent_image(null);
            imageEvent.setDate_time_save(cursor.getString(3));

            //Thêm vào list
            imageEventList.add(imageEvent);
        }

        //Đóng con trỏ lại, nếu không có thể sẽ bị lỗi khi dùng nhiều
        cursor.close();

        return imageEventList;
    }

    //1.5 Lấy 1 Ảnh theo id_image_event
    public ImageEvent getOneImageEvent(int id_image_event){
        String SQLite = "Select * from ImageEvent where id_image_event = " + id_image_event;
        Cursor cursor = databaseSQLite.ReadData(SQLite);

        ImageEvent imageEvent = new ImageEvent();

        while (cursor.moveToNext()){
            imageEvent.setId_image_event(cursor.getInt(0)); //id_image_event là cột đầu tiên trong bảng (xem lệnh tạo bảng)
            imageEvent.setId_event(cursor.getInt(1));

            //Lấy ảnh về chính là dạng byte array (dùng getBlob)
            imageEvent.setEvent_image(cursor.getBlob(2));
            imageEvent.setDate_time_save(cursor.getString(3));
        }

        //Đóng con trỏ lại, nếu không có thể sẽ bị lỗi khi dùng nhiều
        cursor.close();

        return imageEvent;
    }

    //2. Xoá toàn bộ event và các thông tin đi kèm (ở bảng khác như: ảnh)
    public void DeleteAllEvent(){
        String SQLite1 = "Delete from EventOfDay";
        databaseSQLite.WriteData(SQLite1);

        String SQLite2 = "Delete from ImageEvent";
        databaseSQLite.WriteData(SQLite2);
    }

    //3.1 Xoá toàn bộ ảnh của một event (Để chuẩn bị chèn lại)
    public void DeleteAllImageEvent(int id_event){
        String SQLite = "Delete from ImageEvent where id_event = " + id_event;
        databaseSQLite.WriteData(SQLite);
    }

    //3.2 Update Ảnh của 1 event: Xoá toàn bộ ảnh -> Thêm ảnh mới
    public void UpdateImageEvent(int id_event, List<ImageEvent> imageEventList) throws IOException {
        //Xoá toàn bộ ảnh cũ
        DeleteAllImageEvent(id_event);

        //Thêm lại danh sách ảnh của event vào bảng ImageEvent (Nếu có ảnh trong list)
        if(!imageEventList.isEmpty()) {
            for (int i = 0; i < imageEventList.size(); i++) {
                InsertOneImageEvent(id_event, imageEventList.get(i));
            }
        }
    }

    //3.3 Xoá 1 ảnh (của một event)
    public void DeleteOneImageEvent(int id_image_event){
        String SQLite = "Delete from ImageEvent where id_image_event = " + id_image_event;
        databaseSQLite.WriteData(SQLite);
    }

    //3.4 Đếm số lượng ảnh của 1 event
    public int CountAllImageOfEvent(int id_event) throws SQLException {

        //Có thể dùng lệnh select count (*) nếu cần
        String SQLite = "Select * from ImageEvent where id_event = '" + id_event + "'";
        Cursor cursor = databaseSQLite.ReadData(SQLite);

        int count = 0;
        while (cursor.moveToNext()){
            count++;
        }

        //Đóng con trỏ lại
        cursor.close();

        //Trả về số lượng
        return count;
    }

    //4.1 Lấy danh sách Event của một ngày
    public List<Event> getListEventOfDay(String date){
        String SQLite = "Select * from EventOfDay where event_date = '" + date + "' order by event_time ASC, date_time_record ASC"; //date dạng String cần thêm dấu ''
        Cursor cursor = databaseSQLite.ReadData(SQLite);

        List<Event> listEventOfDay = new ArrayList<>();
        while (cursor.moveToNext()){
            int id_event = cursor.getInt(0); //id là cột đầu tiên trong bảng (xem lệnh tạo bảng)
            String event_name = cursor.getString(1);
            String event_date = cursor.getString(2);
            String event_time = cursor.getString(3);
            String date_time_record = cursor.getString(4);
            int event_status = cursor.getInt(5);
            String event_notes = cursor.getString(6);
            int event_notify = cursor.getInt(7);

            Event event = new Event(id_event, event_name, event_date, event_time, date_time_record, event_status, event_notes, event_notify);
            listEventOfDay.add(event);
        }

        //Đóng con trỏ lại
        cursor.close();

        return listEventOfDay;
    }

    //4.2 Lấy toàn bộ danh sách Event trong SQLite
    public List<Event> getListAllEvent(){
        String SQLite = "Select * from EventOfDay order by event_date DESC, event_time ASC";
        Cursor cursor = databaseSQLite.ReadData(SQLite);

        List<Event> listEventOfDay = new ArrayList<>();

        while (cursor.moveToNext()){
            int id_event = cursor.getInt(0); //id là cột đầu tiên trong bảng (xem lệnh tạo bảng)
            String event_name = cursor.getString(1);
            String event_date = cursor.getString(2);
            String event_time = cursor.getString(3);
            String date_time_record = cursor.getString(4);
            int event_status = cursor.getInt(5);
            String event_notes = cursor.getString(6);
            int event_notify = cursor.getInt(7);

            Event event = new Event(id_event, event_name, event_date, event_time, date_time_record, event_status, event_notes, event_notify);
            listEventOfDay.add(event);
        }

        //Đóng con trỏ lại, nếu không có thể sẽ bị lỗi khi dùng nhiều
        cursor.close();

        return listEventOfDay;
    }

    //4.3 Lấy danh sách Event theo status (trường hợp riêng)
    public List<Event> getCaseListEvent(int event_status){
        String SQLite = "Select * from EventOfDay where event_status = " + event_status + " order by event_date DESC, event_time ASC";
        Cursor cursor = databaseSQLite.ReadData(SQLite);

        List<Event> listEventOfDay = new ArrayList<>();

        while (cursor.moveToNext()){
            int id_event = cursor.getInt(0); //id là cột đầu tiên trong bảng (xem lệnh tạo bảng)
            String event_name = cursor.getString(1);
            String event_date = cursor.getString(2);
            String event_time = cursor.getString(3);
            String date_time_record = cursor.getString(4);
            int event_status_sqlite = cursor.getInt(5);
            String event_notes = cursor.getString(6);
            int event_notify = cursor.getInt(7);

            Event event = new Event(id_event, event_name, event_date, event_time, date_time_record, event_status_sqlite, event_notes, event_notify);
            listEventOfDay.add(event);
        }

        //Đóng con trỏ lại (nếu không có thể sẽ bị lỗi khi dùng nhiều)
        cursor.close();

        return listEventOfDay;
    }

    //4.4 Lấy một Event của một ngày (theo id_event)
    public Event getOneEventOfDay(int id_event){
        String SQLite = "Select * from EventOfDay where id_event = " + id_event; //date dạng String cần thêm dấu ''
        Cursor cursor = databaseSQLite.ReadData(SQLite);

        Event event = new Event();
        while (cursor.moveToNext()){
            int id_event_sqlite = cursor.getInt(0); //id là cột đầu tiên trong bảng (xem lệnh tạo bảng)
            String event_name = cursor.getString(1);
            String event_date = cursor.getString(2);
            String event_time = cursor.getString(3);
            String date_time_record = cursor.getString(4);
            int event_status = cursor.getInt(5);
            String event_notes = cursor.getString(6);
            int event_notify = cursor.getInt(7);

            event = new Event(id_event_sqlite, event_name, event_date, event_time, date_time_record, event_status, event_notes, event_notify);
        }

        //Đóng con trỏ lại, nếu không có thể sẽ bị lỗi khi dùng nhiều
        cursor.close();

        return event;
    }

    //5.1 Check xem một ngày có sự kiện hay không
    public int CountAllEventOfDay(String date_check) throws SQLException {

        //Có thể dùng lệnh select count (*) nếu cần
        String SQL = "Select * from EventOfDay where event_date = '" + date_check + "'";
        Cursor cursor = databaseSQLite.ReadData(SQL);

        int count = 0;
        while (cursor.moveToNext()){
            count++;
        }

        //Đóng con trỏ lại, nếu không có thể sẽ bị lỗi khi dùng nhiều
        cursor.close();

        //Trả về số lượng
        return count;
    }

    //5.2 Kiểm tra xem một ngày các sự kiện có check (status = 2,3,4,5) hết chưa (Nếu số lượng event checked = số lượng tổng event của ngày thì ngày đó đã check toàn bộ)
    public int CountEventCheckedOfDay(String dateCheck) throws SQLException {

        //Có thể dùng lệnh select count (*) nếu cần
        String SQL = "Select * from EventOfDay where event_date = '" + dateCheck + "' and event_status > 1 ";
        Cursor cursor = databaseSQLite.ReadData(SQL);

        int count = 0;
        while (cursor.moveToNext()){
            count++;
        }

        //Đóng con trỏ lại, nếu không có thể sẽ bị lỗi khi dùng nhiều
        cursor.close();

        //Trả về số lượng
        return count;
    }

    //5.3 Kiểm tra xem một ngày có status muốn kiểm tra (có emotion (sự kiện) đặc biệt) hay không (1: chưa check, 2: check bình thường, 3: like, 4: heart, 5: star)
    public boolean checkStatusOneDay(String dateCheck, int statusCheck) throws SQLException {

        //Có thể dùng lệnh select count (*) nếu cần
        String SQL = "Select * from EventOfDay where event_date = '" + dateCheck + "' and event_status = " + statusCheck;
        Cursor cursor = databaseSQLite.ReadData(SQL);

        int count = 0;
        if (cursor.moveToNext()){
            count++;
        }

        //Đóng con trỏ lại, nếu không có thể sẽ bị lỗi khi dùng nhiều
        cursor.close();

        //Trả về dạng rút gọn, nếu count <= 0 thì sẽ trả về false, > 0 thì đúng nên sẽ trả về true
        return count > 0;

    }

    //6.1 Sửa dữ liệu của một event, truyền: Event mới (không có id, chỉ chứa dữ liệu), id event cũ | id thì không cần đưa vào dấu '' như String
    public void UpdateEvent(@NonNull Event event, int id_event){
        String SQLite = "Update EventOfDay set event_name = '" + event.getEvent_name()
                + "', event_date = '" + event.getEvent_date()
                + "', event_time = '" + event.getEvent_time()
                + "', date_time_record = '" + event.getDate_time_record()
                + "', event_notes = '" + event.getEvent_notes()
                + "', event_notify = " + event.getEvent_notify()
                + " where id_event = " + id_event;
        databaseSQLite.WriteData(SQLite);
    }

    //6.2 Sửa tên của một event
    public void UpdateNameOfEvent(String event_name, int id_event){
        String SQLite = "Update EventOfDay set event_name = '" + event_name + "' where id_event = " + id_event;
        databaseSQLite.WriteData(SQLite);
    }

    //6.3 Sửa thời gian đặt của một event
    public void UpdateTimeOfEvent(String event_time, int id_event){
        String SQLite = "Update EventOfDay set event_time = '" + event_time + "' where id_event = " + id_event;
        databaseSQLite.WriteData(SQLite);
    }

    //6.4 Sửa ngày của một event
    public void UpdateDateOfEvent(String event_date, int id_event){
        String SQLite = "Update EventOfDay set event_date = '" + event_date + "' where id_event = " + id_event;
        databaseSQLite.WriteData(SQLite);
    }

    //6.5 Sửa trạng thái của một event (đặt quy ước, 1: là trạng thái bình thường, 2: đánh dấu đã được xử lý)
    public void UpdateStatusOfEvent(int event_status, int id_event){
        String SQLite = "Update EventOfDay set event_status = " + event_status + " where id_event = " + id_event;
        databaseSQLite.WriteData(SQLite);
    }

    //6.6 Sửa trạng thái thông báo notify của một event (đặt quy ước, 1: là không thông báo, 2: có thông báo lên màn hình và chuông)
    public void UpdateEventNotify(int event_notify, int id_event){
        String SQLite = "Update EventOfDay set event_notify = " + event_notify + " where id_event = " + id_event;
        databaseSQLite.WriteData(SQLite);
    }

    //7. Xoá một Event, dùng id_event
    public void DeleteEvent (int id_event){
        String SQLite = "Delete from EventOfDay where id_event = " + id_event;
        databaseSQLite.WriteData(SQLite);
    }

    //7. Thêm danh sách ảnh vào một event

}

//*. Thêm dữ liệu bằng code trực tiếp: id tự động tăng nên để null, điền đúng thứ tự (Sẽ thêm mỗi lần run nên có thể ẩn đi nếu đã insert đủ)
//        Event event = new Event("Sự kiện 2 SQLite", LocalDate.now().toString(), LocalTime.now().toString(), LocalDateTime.now().toString());
//        String SQLite = "INSERT INTO EventOfDay VALUES (null, '" + event.getEvent_name() + "', '" + event.getEvent_date() + "', '" + event.getEvent_time() + "', '" + event.getDate_time_record() + "')";
//        databaseSQLite.WriteData(SQLite);
