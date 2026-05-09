package com.mvdragon.weekcalendar.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

//Class sử dụng database (của máy cá nhân người dùng)
public class DatabaseSQLite extends SQLiteOpenHelper {
    //1. Constructor tạo Database: Context màn hình sử dụng, Tên database, con trỏ duyệt dữ liệu (null), bản database từ 1 trở lên
    public DatabaseSQLite(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //2. Truy vấn không trả về kết quả: CREATE tạo database hoặc bảng, INSERT thêm dữ liệu, UPDATE, DELETE ... | Truyền vào câu lệnh sql
    public void WriteData(String sql){
        //Gọi database của máy người dùng, chọn kiểu ghi dữ liệu (kiểu này vẫn có thể đọc dữ liệu)
        SQLiteDatabase database = getWritableDatabase();
        //Thực thi câu lệnh được truyền vào
        database.execSQL(sql);
    }

    //3. Truy vấn trả về kết quả: SELECT (Trả dữ liệu dạng bảng tạm - duyệt từng dòng dữ liệu trong database)
    public Cursor ReadData(String sql){
        //Gọi database, chọn kiểu chỉ đọc dữ liệu chữ không ghi
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql,null);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

}
