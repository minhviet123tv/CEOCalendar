package com.mvdragon.weekcalendar.calendarview;

import static com.mvdragon.weekcalendar.CalendarUtils.daysIn3Week;
import static com.mvdragon.weekcalendar.CalendarUtils.daysInMonthFollowWeek;
import static com.mvdragon.weekcalendar.CalendarUtils.daysInWeekArray;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.rewarded.RewardedAd;
import com.mvdragon.weekcalendar.CalendarUtils;
import com.mvdragon.weekcalendar.R;
import com.mvdragon.weekcalendar.database.TruyVanDuLieuSQLite;
import com.mvdragon.weekcalendar.model.Event;
import com.mvdragon.weekcalendar.calendarview.event.EventAdapter;
import com.mvdragon.weekcalendar.database.UserUltils;
import com.mvdragon.weekcalendar.model.User;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WeekViewFragment extends Fragment {
    private static View view;
    private static TextView txt_date_title;
    private static TextView txt_coin_number_calendar;
    private ImageButton ibtn_back_to_day, btn_previous_weekOrMonth, btn_forward_weekOrMonth;
    public static ImageView btn_add_event;
    public static TextView txt_notify_empty_event;
    private static RecyclerView rcv_day_number;
    private static RecyclerView rcv_list_day_name;
    private static RecyclerView rcv_event_of_day;
    private static TruyVanDuLieuSQLite truyVanDuLieuSQLite;
    private static RewardedAd rewardedAd;

    public WeekViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_week_view,null);

        if(UserUltils.getUserLocal(view.getContext()).getRole() == 1) {
            view = inflater.inflate(R.layout.fragment_week_view_top, container, false);
        } else if (UserUltils.getUserLocal(view.getContext()).getRole() == 2) {
            view = inflater.inflate(R.layout.fragment_week_view, container, false);
        }

        //1.1 Ánh xạ luôn nên để ở đầu tiên (mới có wiget để mà sử dụng)
        AnhXa();

        //1.4 Tiến thêm 1 tuần hoặc 1 tháng
        btn_forward_weekOrMonth.setOnClickListener(v -> {
            try {
                nextWeekAction();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        //1.5 Lùi 1 tuần hoặc 1 tháng
        btn_previous_weekOrMonth.setOnClickListener(v -> {
            try {
                previousWeekAction();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        //4. Set hiển thị week
        try {
            setWeekView();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        //5. Click add new event
        btn_add_event.setOnClickListener(v -> {
            //Mở fragment
            CalendarViewPagerFragment.setCurrentItemCalendarViewPager(1);
        });

        //6. Sự kiện back today
        ibtn_back_to_day.setOnClickListener(v -> {
            returnToDay();
        });

        //7. Sự kiện chọn ngày (khi click vào textView hiển thị ngày)
        txt_date_title.setOnClickListener(v -> {
            selectDateOnMonthView(Gravity.CENTER);
        });


        return view;
    }


    //7.1 selectDateOnMonthView
    private void selectDateOnMonthView(int gravity) {

        //1. Tạo dialog (Dùng dialog cách này để tao Window với giao diện đẹp và như ý hơn)
        Dialog dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //Không tiêu đề mặc định
        dialog.setContentView(R.layout.dialog_month_view); // layout của dialog

        //2. Khai báo cửa sổ hiển thị (
        Window window = dialog.getWindow();
        if(window == null){
            return;
        }

        //Set kích thước (theo xml dialog đã tạo), set background
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //Khai báo và gán thuộc tính cho window
        WindowManager.LayoutParams windowAttribute = window.getAttributes();
        windowAttribute.gravity = gravity;
        window.setAttributes(windowAttribute);

        //3. Tắt dialog khi click bên ngoài
        dialog.setCancelable(true); //false: không tắt

        //4. Khai báo các thành phần trong dialog
        TextView txt_month_view = dialog.findViewById(R.id.txt_date_title_month_view_dialog);
        ImageButton btn_previous_month = dialog.findViewById(R.id.btn_previous_month_view_dialog);
        ImageButton btn_forward_month = dialog.findViewById(R.id.btn_forward_month_view_dialog);
        RecyclerView rcv_list_day_name_dialog = dialog.findViewById(R.id.rcv_list_day_name_dialog);
        RecyclerView rcv_dialog_month_view = dialog.findViewById(R.id.rcv_dialog_month_view);
        ImageButton ibtn_return_to_day_month_dialog = dialog.findViewById(R.id.ibtn_return_to_day_month_dialog);
        ImageButton btn_add_event_month_dialog = dialog.findViewById(R.id.btn_add_event_month_dialog);
        Button btn_cancel = dialog.findViewById(R.id.button_cancel_month_view);
        Button btn_ok = dialog.findViewById(R.id.button_ok_month_view);

        //5. Load rcv month view
        //Tạo ngày chọn riêng của month view
        CalendarUtils.selectedDateOnMonthViewDialog = CalendarUtils.selectedDate;

        //Hiển thị ngày chọn khi mới hiện dialog
        User user = UserUltils.getUserLocal(view.getContext());
        String monthViewText = CalendarUtils.onlyMonthYearFromDate(CalendarUtils.selectedDateOnMonthViewDialog, user.getDateFormat());
        txt_month_view.setText(monthViewText); //Gán text date cho dialog khi mới mở lên
        //set màu cho tháng hiện tại
        if(CalendarUtils.selectedDateOnMonthViewDialog.getMonth() == LocalDate.now().getMonth() && CalendarUtils.selectedDateOnMonthViewDialog.getYear() == LocalDate.now().getYear()){
            txt_month_view.setText(Html.fromHtml("<font color = #0046E4>" + txt_month_view.getText().toString()));
        }

        //Load riêng tên thứ ngày trong tuần cho dialog
        LoadDayNameMonthDialog(rcv_list_day_name_dialog);
        //Load rcv days month
        LoadRcvOnDialog(txt_month_view, rcv_dialog_month_view, rcv_list_day_name_dialog);

        //6. Click button
        btn_cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        //7. Button OK -> Đổi ngày và thực hiện load weekview
        btn_ok.setOnClickListener(v -> {
            CalendarUtils.selectedDate = CalendarUtils.selectedDateOnMonthViewDialog;
            try {
                setWeekView();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            dialog.dismiss();
        });

        //8. Trở về ngày hôm nay trong month view
        ibtn_return_to_day_month_dialog.setOnClickListener(v -> {
            CalendarUtils.selectedDateOnMonthViewDialog = LocalDate.now();

            LoadRcvOnDialog(txt_month_view, rcv_dialog_month_view, rcv_list_day_name_dialog);
            LoadDayNameMonthDialog(rcv_list_day_name_dialog);
        });

        //9. Mở activity Add event mới với ngày được chọn trong month view
        btn_add_event_month_dialog.setOnClickListener(v -> {
            //Lấy ngày được chọn là ngày chọn của month view
            CalendarUtils.selectedDate = CalendarUtils.selectedDateOnMonthViewDialog;
            //Mở fragment tạo event
            CalendarViewPagerFragment.setCurrentItemCalendarViewPager(1);

            //Đóng dialog
            dialog.dismiss();
        });

        //10. Previous 1 tháng
        btn_previous_month.setOnClickListener(v -> {

            //Cập nhật lại ngày select và textView hiển thị
            CalendarUtils.selectedDateOnMonthViewDialog = CalendarUtils.selectedDateOnMonthViewDialog.minusMonths(1);

            //Cập nhật lại rcv
            LoadRcvOnDialog(txt_month_view, rcv_dialog_month_view, rcv_list_day_name_dialog);
            LoadDayNameMonthDialog(rcv_list_day_name_dialog);
        });

        //11. Forward 1 tháng
        btn_forward_month.setOnClickListener(v -> {

            //Cập nhật lại ngày select
            CalendarUtils.selectedDateOnMonthViewDialog = CalendarUtils.selectedDateOnMonthViewDialog.plusMonths(1);

            //Cập nhật lại rcv
            LoadRcvOnDialog(txt_month_view, rcv_dialog_month_view, rcv_list_day_name_dialog);
            LoadDayNameMonthDialog(rcv_list_day_name_dialog);
        });

        //12. show
        dialog.show();

    }

    //7.2 Load recyclerView của dialog (tạo bên ngoài để gọi lại nhiều lần)
    public void LoadRcvOnDialog (TextView txt_month_view, RecyclerView rcv_dialog_month_view , RecyclerView rcv_list_day_name_dialog){
        //Lấy user để đặt format date
        User user = UserUltils.getUserLocal(view.getContext());

        //1.1 set hiển thị cho title
        String dateSelectOnMonthView = CalendarUtils.onlyMonthYearFromDate(CalendarUtils.selectedDateOnMonthViewDialog, UserUltils.getUserLocal(view.getContext()).getDateFormat());
        txt_month_view.setText(dateSelectOnMonthView);

        //Hiển thị màu cho tháng hiện tại
        if(CalendarUtils.selectedDateOnMonthViewDialog.getMonth() == LocalDate.now().getMonth() && CalendarUtils.selectedDateOnMonthViewDialog.getYear() == LocalDate.now().getYear()){
            txt_month_view.setText(Html.fromHtml("<font color = #0046E4>" + txt_month_view.getText().toString()));
        }

        //2. Lấy danh sách ngày trong tháng theo ngày đang được select của month view -> load rcv
        ArrayList<LocalDate> monthArray = daysInMonthFollowWeek(CalendarUtils.selectedDateOnMonthViewDialog, user.getStartWeek());

        //Adapter
        CalendarMonthViewAdapter monthViewAdapter = new CalendarMonthViewAdapter(view.getContext(), monthArray, (position, date) -> {
            //Ngày select trong month sẽ thành ngày được click
            CalendarUtils.selectedDateOnMonthViewDialog = date;

            //Dùng để set hiển thị title của month view
            String dateSelectOnMonthView1 = CalendarUtils.onlyMonthYearFromDate(CalendarUtils.selectedDateOnMonthViewDialog, user.getDateFormat());
            txt_month_view.setText(dateSelectOnMonthView1);

            //Cập nhật lại rcv ngày và tên ngày trong tuần sau khi được click
            LoadRcvOnDialog(txt_month_view, rcv_dialog_month_view, rcv_list_day_name_dialog);
            LoadDayNameMonthDialog(rcv_list_day_name_dialog);

        });

        //setup layout manager và gán adapter cho rcv
        GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(), 7);
        rcv_dialog_month_view.setLayoutManager(gridLayoutManager);
        rcv_dialog_month_view.setAdapter(monthViewAdapter);

    }

    //7.3 LoadDayName (cho  monthView Dialog)
    public void LoadDayNameMonthDialog(RecyclerView rcv){
        //Lấy user từ Shared
        User user = UserUltils.getUserLocal(view.getContext());

        //Lấy danh sách tên thứ ngày theo user
        ArrayList<String> listDayNameOfWeek = new ArrayList<>();
        if(user.getStartWeek() == 1){
            listDayNameOfWeek = new ArrayList<>(Arrays.asList("Sun", "Mon", "Tue", "Wed", "Thur", "Fri", "Sat"));
        } else if (user.getStartWeek() == 2) {
            listDayNameOfWeek = new ArrayList<>(Arrays.asList("Mon", "Tue", "Wed", "Thur", "Fri", "Sat", "Sun"));
        }

        //Load rcv tên thứ ngày
        DayNameMonthDialogRecyclerViewAdapter dayNameAdapter = new DayNameMonthDialogRecyclerViewAdapter(view.getContext(), listDayNameOfWeek);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(), 7);
        rcv.setLayoutManager(gridLayoutManager);
        rcv.setAdapter(dayNameAdapter);
    }

    //6. Sự kiện back today
    private void returnToDay() {
        CalendarUtils.selectedDate = LocalDate.now();

        try {
            setWeekView();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //4.1 Set hiển thị week theo ngày đang được select, và danh sách event (sử dụng mỗi khi cần load week)
    public static void setWeekView() throws SQLException {

        //Cài đặt ngày trong tuần
        setOnlyWeekView();
        //Lấy danh sách event của 1 ngày và hiển thị
        setEventAdpater();
        //Load rcv day name (Cài đặt màu ngày trong adapter), showNameOnWeek = true (Dùng cho tuần)
        LoadDayName(rcv_list_day_name);
    }

    //4.2 Set hiển thị ngày trong tuần (theo ngày đang được select)
    @SuppressLint("ResourceAsColor")
    public static void setOnlyWeekView() throws SQLException {

        //1. Giá trị hiển thị tiêu đề ngày tháng theo user
        User user = UserUltils.getUserLocal(view.getContext());
        txt_date_title.setText(CalendarUtils.onlyMonthYearFromDate(CalendarUtils.selectedDate, user.getDateFormat()));

        //2. Nếu ngày được select nằm trong tháng hiện tại thì đổi màu title
//        LocalDate toDay =  LocalDate.now(); //LocalDate.of(2023,10,23);
//        if(CalendarUtils.selectedDate.getMonth() == toDay.getMonth() && CalendarUtils.selectedDate.getYear() == toDay.getYear()){
//            txt_date_title.setText(Html.fromHtml("<font color = #0046E4>" + txt_date_title.getText().toString())); //Tương ứng trong color.xml
//        } else {
//            txt_date_title.setTextColor(Color.BLACK);
//        }

        //3. Lấy danh sách ngày trong tuần (dựa vào ngày đang được select) | Cài đặt theo setting trong user (getViewCalendar: 1: one week, 2: three week, 3: one month | getStartWeek: 1 CN, 2 Monday )
        ArrayList<LocalDate> dayList;

        if(user.getViewCalendar() == 1){
            dayList = daysInWeekArray(CalendarUtils.selectedDate, user.getStartWeek());
        } else if (user.getViewCalendar() == 2) {
            dayList = daysIn3Week(CalendarUtils.selectedDate, user.getStartWeek());
        } else if (user.getViewCalendar() == 3) {
            dayList = daysInMonthFollowWeek(CalendarUtils.selectedDate, user.getStartWeek());
        } else {
            dayList = daysInWeekArray(CalendarUtils.selectedDate, user.getStartWeek());
        }

        //Khai báo adapter: context (màn hình), list ngày trong tuần, màn hình chứa sự kiện interface (lấy vị trí và ngày tương ứng)
        CalendarAdapter calendarAdapter = new CalendarAdapter(view.getContext(), dayList, (position, date) -> {
            //Set ngày được chọn thành ngày được click
            CalendarUtils.selectedDate = date;
            //Load lại danh sách ngày trong tuần và sự kiện ngày
            try {
                setWeekView();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            //Nếu dùng fragment ViewPager không nhận được bên Add event fragment thì lưu Shared: date dạng String
//            UserUltils.saveSelectDate(view.getContext(), date.toString());
        });

        //Lưới hiển thị ngày cho rcv (7 ngày)
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(view.getContext().getApplicationContext(), 7);
        rcv_day_number.setLayoutManager(layoutManager);
        rcv_day_number.setAdapter(calendarAdapter);

    }

    //4.3. Lấy danh sách event của 1 ngày và hiển thị
    @SuppressLint("NotifyDataSetChanged")
    public static void setEventAdpater() throws SQLException {

        //a. Lấy danh sách sự kiện của 1 ngày: Truyền ngày đang được select
        List<Event> eventListOfDateSQLite = truyVanDuLieuSQLite.getListEventOfDay(CalendarUtils.selectedDate.toString());

        //b. Khai báo adapter cho rcv | Sự kiện cập nhật rcv sau khi chọn giờ | sự kiện cập nhật ảnh check trong ngày khi bấm swipe check
        EventAdapter eventAdapter = new EventAdapter(view.getContext(), eventListOfDateSQLite, false, () -> {

            //Đặt độ trễ 200 milisecon để có thể cảm nhận rõ sự thay đổi trong rcv
            Handler handler = new Handler();
            handler.postDelayed(() -> {

                //Sự kiện gọi lại chính setEventAdpater() để cập nhật rcv theo dữ liệu ngày vừa chỉnh (khi time ở item được chọn lại trong Adapter)
                try {
                    setEventAdpater();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }, 200);

        }, () -> {
            //Bấm nút check -> load lại danh sách ngày (cập nhật nút đánh dấu sự kiện trong ngày là all status event = 2 hay chưa)
            try {
                setOnlyWeekView();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        //Kiểu hiển thị của rcv
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        rcv_event_of_day.setLayoutManager(linearLayoutManager);
        rcv_event_of_day.setAdapter(eventAdapter);
        eventAdapter.notifyDataSetChanged();

        //Load text notify event
        loadNotifyEmptyEvent();

    }

    //4.4 LoadDayName (cho fragment setWeekView)
    public static void LoadDayName(RecyclerView rcv){
        //Lấy user từ Shared
        User user = UserUltils.getUserLocal(view.getContext());

        //Lấy danh sách tên thứ ngày theo user
        ArrayList<String> listDayNameOfWeek = new ArrayList<>();
        if(user.getStartWeek() == 1){
            listDayNameOfWeek = new ArrayList<>(Arrays.asList("Sun", "Mon", "Tue", "Wed", "Thur", "Fri", "Sat"));
        } else if (user.getStartWeek() == 2) {
            listDayNameOfWeek = new ArrayList<>(Arrays.asList("Mon", "Tue", "Wed", "Thur", "Fri", "Sat", "Sun"));
        }

        //Load rcv tên thứ ngày
        DayNameRecyclerViewAdapter dayNameAdapter = new DayNameRecyclerViewAdapter(view.getContext(), listDayNameOfWeek);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(), 7);
        rcv.setLayoutManager(gridLayoutManager);
        rcv.setAdapter(dayNameAdapter);
    }

    //4.5 loadButtonNewEvent
    public static void loadNotifyEmptyEvent() throws SQLException {

        //Đếm sự kiện trong ngày
        int countEvent = truyVanDuLieuSQLite.CountAllEventOfDay(CalendarUtils.selectedDate.toString());
        //Cài đặt nút new event theo số lượng sự kiện
        if(countEvent > 0){
            txt_notify_empty_event.setVisibility(View.GONE);
        } else if (countEvent == 0 && UserUltils.getUserLocal(view.getContext()).getCountLogin() <= 10) {
            txt_notify_empty_event.setVisibility(View.VISIBLE);
        }
    }

    //1.3 Back -> Lùi 1 tuần (Từ ngày được select trừ đi 1 tuần) -> set lại hiển thị tuần
    public void previousWeekAction() throws SQLException {
        //Set theo viewCalendar của user: 1 và 2 là tuần, 3 là tháng
        User user = UserUltils.getUserLocal(view.getContext());
        if(user.getViewCalendar() == 1 || user.getViewCalendar() == 2) {
            CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
        } else if (user.getViewCalendar() == 3) {
            CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusMonths(1);
        }

        //Cập nhật giao diện calendar
        setWeekView();
    }

    //1.2 Forward -> Tiến 1 tuần (Từ ngày được select cộng thêm 1 tuần) -> set lại hiển thị tuần
    public void nextWeekAction() throws SQLException {
        //Set theo viewCalendar của user: 1 và 2 là tuần, 3 là tháng
        User user = UserUltils.getUserLocal(view.getContext());
        if(user.getViewCalendar() == 1 || user.getViewCalendar() == 2) {
            CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
        } else if (user.getViewCalendar() == 3) {
            CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusMonths(1);
        }

        //Cập nhật giao diện calendar
        setWeekView();
    }

    //1.1 Ánh xạ
    private void AnhXa() {
        rcv_day_number = view.findViewById(R.id.rcv_day_number);
        txt_date_title = view.findViewById(R.id.txt_date_title);
        btn_add_event = view.findViewById(R.id.btn_add_event);
        txt_notify_empty_event = view.findViewById(R.id.txt_notify_empty_event);
        rcv_event_of_day = view.findViewById(R.id.rcv_event_list);
        rcv_list_day_name = view.findViewById(R.id.rcv_list_day_name);

        btn_previous_weekOrMonth = view.findViewById(R.id.btn_previous_weekOrMonth);
        ibtn_back_to_day = view.findViewById(R.id.ibtn_select_to_day);
        btn_forward_weekOrMonth = view.findViewById(R.id.btn_forward_weekOrMonth);

        //Gán ngày được select là ngày hôm nay khi mới vào app
        CalendarUtils.selectedDate = LocalDate.now();

        //Khai báo SQLite ở đây để còn dùng Context
        truyVanDuLieuSQLite = new TruyVanDuLieuSQLite(view.getContext());

        txt_coin_number_calendar = view.findViewById(R.id.txt_coin_number_calendar);
    }

    //I* Cập nhật hiển thị, danh sách ngày trong tuần, event của ngày mỗi lần trở lại activity
    @SuppressLint("ResourceType")
    @Override
    public void onResume() {
        super.onResume();

        //1. Load lịch
        try {
            setWeekView();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}