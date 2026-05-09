package com.mvdragon.weekcalendar.menu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.mvdragon.weekcalendar.MainActivity;
import com.mvdragon.weekcalendar.R;
import com.mvdragon.weekcalendar.calendarview.CalendarViewPagerFragment;
import com.mvdragon.weekcalendar.database.UserUltils;
import com.mvdragon.weekcalendar.model.User;
/*
Hiện tại chỉ dùng Activity này để setting khi mới vào app
 */
public class SettingCalendarActivity extends AppCompatActivity {
    private RadioButton rdb_1_one_week, rdb_2_three_week, rdb_3_monday, rdb_4_sunday, rdb_5_month_view, rdb_6_date_format_1, rdb_7_date_format_2, rdb_8_date_format_3;
    private RadioButton rdb_layout_bottom, rdb_layout_top;
    private Button btn_save_setting;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_setting_calendar);

        //1. Ánh xạ
        AnhXa();

        //2. Set sẵn theo user
        LoadSettingFollowUser();

        //3. Save setting
        btn_save_setting.setOnClickListener(v -> {
            SaveSettingCalendar();
        });

    }

    //4. Lưu fragment sử dụng cuối của user
    public void updateFragmentUser(int fragment_user){

        //Lưu user với thứ tự fragment khi bấm vào menu tương ứng
        User user = UserUltils.getUserLocal(this);
        user.setFragment_user(fragment_user);
        UserUltils.saveUserLocal(this, user);
    }

    //3. Save setting
    private void SaveSettingCalendar() {

        //1. Lưu Calendar view (Vào user đã gọi từ Shared)
        if(rdb_1_one_week.isChecked()){
            //Lưu vào user
            user.setViewCalendar(1); //Tự lưu và nhớ mã 1: one week
        }

        if(rdb_2_three_week.isChecked()){
            //Lưu vào user
            user.setViewCalendar(2); //Mã 2: three week
        }

        if(rdb_5_month_view.isChecked()){
            user.setViewCalendar(3);
        }

        //2. Lưu Calendar style
        if(rdb_layout_bottom.isChecked()){
            user.setRole(2);
        }
        if(rdb_layout_top.isChecked()){
            user.setRole(1);
        }

        //3. Lưu First day of week | 1: CN, 2: monday
        if(rdb_3_monday.isChecked()){
            user.setStartWeek(2);
        }
        if (rdb_4_sunday.isChecked()){
            user.setStartWeek(1);
        }

        //4. Lưu date format
        if(rdb_6_date_format_1.isChecked()){
            user.setDateFormat(1);
        }
        if(rdb_7_date_format_2.isChecked()){
            user.setDateFormat(2);
        }
        if(rdb_8_date_format_3.isChecked()){
            user.setDateFormat(3);
        }

        //4. Cập nhật, lưu user vào Shared
        UserUltils.saveUserLocal(this, user);

        //Back to calendar (Đưa về view calendar và lưu luôn menu bottom cho ViewPager Main)
        updateFragmentUser(0);

        //Kết thúc activity
        finish();

    }

    //2. Set sẵn theo user
    private void LoadSettingFollowUser() {

        //Đặt sẵn calendar view theo user (1: one week, 2: three week)

        //1. Nếu mã view calendar là 1 thì đặt sẵn nút one week
        if(user.getViewCalendar() == 1){
            rdb_1_one_week.setChecked(true); //setChecked là đã bấm vào, còn setEnable là cho phép bấm, sử dụng

        //Nếu mã view calendar là 2 thì đặt sẵn nút three week
        } else if(user.getViewCalendar() == 2){
            rdb_2_three_week.setChecked(true);

        } else if (user.getViewCalendar() == 3) {
            rdb_5_month_view.setChecked(true);
        }

        //2. Đặt sẵn style
        if(user.getRole() == 1){
            rdb_layout_top.setChecked(true);
        } else if (user.getRole() == 2) {
            rdb_layout_bottom.setChecked(true);
        }

        //3. Đặt sẵn First day of week | 1: CN, 2: monday
        if(user.getStartWeek() == 2){
            rdb_3_monday.setChecked(true);
        } else if (user.getStartWeek() == 1) {
            rdb_4_sunday.setChecked(true);
        }

        //4. Đặt sẵn date format
        if(user.getDateFormat() == 1){
            rdb_6_date_format_1.setChecked(true);
        } else if (user.getDateFormat() == 2) {
            rdb_7_date_format_2.setChecked(true);
        } else if (user.getDateFormat() == 3) {
            rdb_8_date_format_3.setChecked(true);
        }

        //5. Ẩn nút back nếu vào lần 1 (mới mở app)
//        if(user.getCountLogin() == 1){
//            ic_back_activity_menu_list_event.setVisibility(View.GONE);
//        }
    }

    //1.1 Ánh xạ
    private void AnhXa() {

        rdb_1_one_week = findViewById(R.id.rdb_1_one_week);
        rdb_2_three_week = findViewById(R.id.rdb_2_three_week);
        rdb_5_month_view = findViewById(R.id.rdb_5_month_view);
        rdb_3_monday = findViewById(R.id.rdb_3_monday);
        rdb_4_sunday = findViewById(R.id.rdb_4_sunday);
        rdb_6_date_format_1 = findViewById(R.id.rdb_6_date_format_1);
        rdb_7_date_format_2 = findViewById(R.id.rdb_7_date_format_2);
        rdb_8_date_format_3 = findViewById(R.id.rdb_8_date_format_3);
        btn_save_setting = findViewById(R.id.btn_save_setting_calendar_view);

        rdb_layout_bottom = findViewById(R.id.rdb_layout_bottom);
        rdb_layout_top = findViewById(R.id.rdb_layout_top);

        //Lấy user đã lưu Shared
        user = UserUltils.getUserLocal(this);

    }

    //A. Hàm click trực tiếp cancel -> back activity
    public void CancelMenuSettingCalendar(View view) {
        getOnBackPressedDispatcher().onBackPressed();
    }

    //B. Load mỗi lần vào
    @Override
    protected void onResume() {
        super.onResume();

        LoadSettingFollowUser();
    }
}