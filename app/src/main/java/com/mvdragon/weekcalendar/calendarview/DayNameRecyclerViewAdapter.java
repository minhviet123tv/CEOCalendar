package com.mvdragon.weekcalendar.calendarview;

import static com.mvdragon.weekcalendar.CalendarUtils.daysInMonthFollowWeek;
import static com.mvdragon.weekcalendar.CalendarUtils.daysInWeekArray;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mvdragon.weekcalendar.CalendarUtils;
import com.mvdragon.weekcalendar.R;
import com.mvdragon.weekcalendar.database.UserUltils;
import com.mvdragon.weekcalendar.model.User;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;

public class DayNameRecyclerViewAdapter extends RecyclerView.Adapter<DayNameRecyclerViewAdapter.DayNameViewHolder>{
    private Context context;
    private ArrayList<String> dayNameList;

    public DayNameRecyclerViewAdapter(Context context, ArrayList<String> dayNameList) {
        this.context = context;
        this.dayNameList = dayNameList;
    }

    @NonNull
    @Override
    public DayNameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_day_name, parent, false);
        return new DayNameViewHolder(view);
    }

    //set item
    @Override
    public void onBindViewHolder(@NonNull DayNameViewHolder holder, int position) {

        //1. Gán giá trị cho ô tên ngày
        String dayName = dayNameList.get(position);
        holder.txt_day_name_on_week.setText(dayName);

        //Lấy dữ liệu để cài đặt: Ngày hôm nay, dữ liệu user
        LocalDate toDay = LocalDate.now(); // LocalDate.of(2023,11,19); // LocalDate.now(); //
        User user = UserUltils.getUserLocal(context);

        //Hiển thị tên ngày hiện tại (theo thời gian thực) trong tuần | 1: one week, 2: three week, 3: one month
        //2. Cài đặt màu của ngày nếu là dùng cho view trong tuần (showNameOnWeek = true)
        if(user.getViewCalendar() == 1 || user.getViewCalendar() == 2) {

            //Lấy danh sách ngày trong tuần hiện tại (dựa vào toDay) | 1: CN, 2: T2
            ArrayList<LocalDate> daysCurrent = daysInWeekArray(toDay, user.getStartWeek());
            //Danh sách ngày đang được select
            ArrayList<LocalDate> daysSelected = daysInWeekArray(CalendarUtils.selectedDate, user.getStartWeek());

            //Nếu ngày trong tuần theo ngày hiện tại = tuần của ngày được select -> Đổi màu ngày
            if (daysCurrent.equals(daysSelected)) {
                holder.BeginSetDayName7Color(toDay);
            }
            //Nếu không thuộc tuần hiện tại thì không set color (mặc định màu xám)
            else {
                holder.setDayNameNoneColor();
            }

        //3. Nếu không phải dùng cho view tuần (thì là dùng cho tháng)
        } else if (user.getViewCalendar() == 3) {
            //Lấy danh sách ngày trong tháng hiện tại (dựa vào toDay) | 1: CN, 2: T2
            ArrayList<LocalDate> daysCurrent = daysInMonthFollowWeek(toDay, user.getStartWeek());
            //Danh sách ngày đang được select trong fragment chính
            ArrayList<LocalDate> daysSelected = daysInMonthFollowWeek(CalendarUtils.selectedDate, user.getStartWeek());

            //Nếu ngày trong tháng theo ngày hiện tại = tháng của ngày được select -> Đổi màu ngày
            if (daysCurrent.equals(daysSelected)) {
                holder.BeginSetDayName7Color(toDay);
            }
            //Nếu không thuộc tháng hiện tại thì không set color (mặc định màu xám)
            else {
                holder.setDayNameNoneColor();
            }

        }
    }

    @Override
    public int getItemCount() {
        return dayNameList.size();
    }

    //Class ViewHolder
    class DayNameViewHolder extends RecyclerView.ViewHolder{
        private TextView txt_day_name_on_week;

        public DayNameViewHolder(@NonNull View itemView) {
            super(itemView);

            //1. Ánh xạ
            txt_day_name_on_week = itemView.findViewById(R.id.txt_day_name_on_week);
        }

        //2.
        public void BeginSetDayName7Color(LocalDate toDay){
            if(toDay.getDayOfWeek().equals(DayOfWeek.MONDAY) && txt_day_name_on_week.getText().equals("Mon")){
                txt_day_name_on_week.setTextColor(context.getResources().getColor(R.color.monday_color_title, null));
                txt_day_name_on_week.setTypeface(Typeface.DEFAULT_BOLD);
            }
            if(toDay.getDayOfWeek().equals(DayOfWeek.TUESDAY) && txt_day_name_on_week.getText().equals("Tue")){
                txt_day_name_on_week.setTextColor(context.getResources().getColor(R.color.tuesday_color_title, null));
                txt_day_name_on_week.setTypeface(Typeface.DEFAULT_BOLD);
            }
            if(toDay.getDayOfWeek().equals(DayOfWeek.WEDNESDAY) && txt_day_name_on_week.getText().equals("Wed")){
                txt_day_name_on_week.setTextColor(context.getResources().getColor(R.color.wednesday_color_title, null));
                txt_day_name_on_week.setTypeface(Typeface.DEFAULT_BOLD);
            }
            if(toDay.getDayOfWeek().equals(DayOfWeek.THURSDAY) && txt_day_name_on_week.getText().equals("Thur")){
                txt_day_name_on_week.setTextColor(context.getResources().getColor(R.color.thursday_color_title, null));
                txt_day_name_on_week.setTypeface(Typeface.DEFAULT_BOLD);
            }
            if(toDay.getDayOfWeek().equals(DayOfWeek.FRIDAY) && txt_day_name_on_week.getText().equals("Fri")){
                txt_day_name_on_week.setTextColor(context.getResources().getColor(R.color.friday_color_title, null));
                txt_day_name_on_week.setTypeface(Typeface.DEFAULT_BOLD);
            }
            if(toDay.getDayOfWeek().equals(DayOfWeek.SATURDAY) && txt_day_name_on_week.getText().equals("Sat")){
                txt_day_name_on_week.setTextColor(context.getResources().getColor(R.color.saturday_color_title, null));
                txt_day_name_on_week.setTypeface(Typeface.DEFAULT_BOLD);
            }
            if(toDay.getDayOfWeek().equals(DayOfWeek.SUNDAY) && txt_day_name_on_week.getText().equals("Sun")){
                txt_day_name_on_week.setTextColor(context.getResources().getColor(R.color.sunday_color_title, null));
                txt_day_name_on_week.setTypeface(Typeface.DEFAULT_BOLD);
            }
        }

        //3. setDayNameNoneColor
        public void setDayNameNoneColor(){
            txt_day_name_on_week.setTextColor(context.getResources().getColor(R.color.gray, null));
        }
    }
}
