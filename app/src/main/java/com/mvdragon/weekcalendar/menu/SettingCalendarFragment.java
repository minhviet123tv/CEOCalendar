package com.mvdragon.weekcalendar.menu;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.content.IntentCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.mvdragon.weekcalendar.MainActivity;
import com.mvdragon.weekcalendar.R;
import com.mvdragon.weekcalendar.calendarview.CalendarViewPagerFragment;
import com.mvdragon.weekcalendar.calendarview.WeekViewFragment;
import com.mvdragon.weekcalendar.database.UserUltils;
import com.mvdragon.weekcalendar.model.User;

import java.sql.SQLException;

public class SettingCalendarFragment extends Fragment {
    private View view;
    private ImageButton ic_back_menu_setting_calendar_fm;
    private RadioButton rdb_1_one_week, rdb_2_three_week, rdb_3_monday, rdb_4_sunday, rdb_5_month_view, rdb_6_date_format_1, rdb_7_date_format_2, rdb_8_date_format_3;
    private RadioButton rdb_layout_bottom, rdb_layout_top;
    private Button btn_save_setting;
    private User user;

    public SettingCalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_setting_calendar, container, false);

        //1. Ánh xạ
        AnhXa();

        //2. Set sẵn theo user
        LoadSettingFollowUser();

        //3. Save setting
        btn_save_setting.setOnClickListener(v -> {
            try {
                SaveSettingCalendar();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        //4. back
        ic_back_menu_setting_calendar_fm.setOnClickListener(v -> {
            try {
                backMenuSettingCalendar();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });


        return view;
    }


    //1.1 Ánh xạ
    private void AnhXa() {
        ic_back_menu_setting_calendar_fm = view.findViewById(R.id.ic_back_menu_setting_calendar_fm);
        rdb_1_one_week = view.findViewById(R.id.rdb_1_one_week);
        rdb_2_three_week = view.findViewById(R.id.rdb_2_three_week);
        rdb_5_month_view = view.findViewById(R.id.rdb_5_month_view);
        rdb_3_monday = view.findViewById(R.id.rdb_3_monday);
        rdb_4_sunday = view.findViewById(R.id.rdb_4_sunday);
        rdb_6_date_format_1 = view.findViewById(R.id.rdb_6_date_format_1);
        rdb_7_date_format_2 = view.findViewById(R.id.rdb_7_date_format_2);
        rdb_8_date_format_3 = view.findViewById(R.id.rdb_8_date_format_3);
        btn_save_setting = view.findViewById(R.id.btn_save_setting_calendar_view);

        rdb_layout_bottom = view.findViewById(R.id.rdb_layout_bottom);
        rdb_layout_top = view.findViewById(R.id.rdb_layout_top);

        //Lấy user đã lưu Shared
        user = UserUltils.getUserLocal(view.getContext());
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

    }

    //3. Save setting
    private void SaveSettingCalendar() throws SQLException {

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

        //Cập nhật, lưu user vào Shared
        UserUltils.saveUserLocal(view.getContext(), user);

        //back về menu (của ViewPager menu)
        backMenuSettingCalendar();

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            //Back to calendar (Đưa về view calendar và lưu luôn menu bottom)
            MainActivity.setCurrentItemViewPager(0);
            updateFragmentUser(0);

            //Load lại ViewPager của Calendar (Để load lại weekcalendar fragment) (Mỗi menu bottom là một ViewPager khác)
            CalendarViewPagerFragment.loadViewPagerCalendar();
        }, 200);

    }

    //4. restart app
    public static void restartApp(Context context){
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        // Required for API 34 and later
        // Ref: https://developer.android.com/about/versions/14/behavior-changes-14#safer-intents
        mainIntent.setPackage(context.getPackageName());
        context.startActivity(mainIntent);
        Runtime.getRuntime().exit(0);

    }

    //5. Lưu fragment sử dụng cuối của user
    private void updateFragmentUser(int fragment_user){

        //Lưu user với thứ tự fragment khi bấm vào menu tương ứng
        User user = UserUltils.getUserLocal(view.getContext());
        user.setFragment_user(fragment_user);
        UserUltils.saveUserLocal(view.getContext(), user);
    }

    //A. Hàm back
    public void backMenuSettingCalendar() throws SQLException {
        MenuViewPagerFragment.setCurrentItemMenu(0);
    }

    //B. Load mỗi lần vào
    @Override
    public void onResume() {
        super.onResume();

        LoadSettingFollowUser();
    }
}