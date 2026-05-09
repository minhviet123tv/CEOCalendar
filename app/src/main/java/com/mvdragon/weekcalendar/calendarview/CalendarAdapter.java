package com.mvdragon.weekcalendar.calendarview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mvdragon.weekcalendar.CalendarUtils;
import com.mvdragon.weekcalendar.R;
import com.mvdragon.weekcalendar.database.TruyVanDuLieuSQLite;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;

/*
Adapter hiển thị danh sách ngày trong tuần
Mỗi khi click vào ngày -> set hiển thị và list event của ngày
Đối tượng chính là date (LocalDate có sẵn của Android)
 */
public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
    private final Context context;
    private ArrayList<LocalDate> days; //danh sách ngày
    private final OnItemListener onItemListener;
    private final TruyVanDuLieuSQLite truyVanDuLieuSQLite;

    //I. Hàm khởi tạo (Khai báo trực tiếp SQLite trong hàm khởi tạo với màn hình được truyền vào, vì trong hàm này thì context mới bắt đầu được sử dụng)
    public CalendarAdapter(Context context, ArrayList<LocalDate> days, OnItemListener onItemListener) {
        this.context = context;
        this.truyVanDuLieuSQLite = new TruyVanDuLieuSQLite(context);
        this.days = days;
        this.onItemListener = onItemListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void changeData(ArrayList<LocalDate> days){
        this.days = days;
        notifyDataSetChanged();
    }

    //II. ViewHolder (Class CalendarViewHolder đã tạo bên ngoài)
    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Khai báo view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_cell, parent,false);
        return new CalendarViewHolder(view);
    }

    //III. Xử lý trong từng item (ngày)
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position){
        //1. Lấy ngày theo thứ tự trong danh sách
        final LocalDate date = days.get(position);

        //2. Nếu ngày bị null thì set (text hiển thị ngày) trống
        if(date == null) {
            holder.txt_day_number.setText("");

        //3. Nếu có ngày trong list thì set text hiển thị ngày
        } else {
            holder.txt_day_number.setText(String.valueOf(date.getDayOfMonth()));

            //4. Lấy ngày hiện tại (LocalDate.now() là ngày hiện tại, lấy của thiết bị sử dụng)
            LocalDate toDay = LocalDate.now(); // LocalDate.of(2023,11,19);

            //5.1 Nếu ngày trong list (dùng trong month view days > 27 ngày) có tháng khác với tháng của ngày đang được select thì set màu
            if(days.size() > 27 && !(date.getMonthValue() == CalendarUtils.selectedDate.getMonthValue())){
                holder.txt_day_number.setTextColor(Color.GRAY);
            }

            //5.1 Nếu ngày trong danh sách tuần trùng với ngày hiện tại thì đổi màu text của ngày (luôn chỉ có 1 ngày trùng với ngày hiện tại trong lịch)
            if(date.equals(toDay)){
//                holder.txt_day_number.setTextColor(Color.BLUE);
                holder.txt_day_number.setText(Html.fromHtml("<font color = #0954FD>" + holder.txt_day_number.getText().toString()));
                holder.txt_day_number.setTypeface(Typeface.DEFAULT_BOLD);
            }

            //5.3 Tăng chiều cao layout: nếu số ngày trong list < 7
            if(days.size() <= 7){
                holder.itemView.getLayoutParams().height = 180; //150: Đang hơi vừa đẹp trên máy a Tấn, hơi bé trên máy mẹ

//                ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
//                params.height = 200;
//                holder.itemView.setLayoutParams(params);

                //Tăng padding dưới bottom cho đối tượng date number và layout emotion
                holder.txt_day_number.setPadding(0, 0, 0, 15);
                holder.layout_check_emotion_day.setPadding(0,0,0,15);

            }


            //6. Nếu ngày trùng với ngày được selected thì đổi màu ô, set kích thước chữ của ô được chọn to hơn (nếu muốn)
            if(date.equals(CalendarUtils.selectedDate)) {

                //Để một màu mặc định
                holder.itemView.setBackgroundResource(R.drawable.bg_date_selected_thursday);

                //Set màu ô ngày selected tương ứng cho ngày hôm nay (trong tuần mỗi ngày một màu)
                if(toDay.getDayOfWeek().equals(DayOfWeek.MONDAY)){
                    holder.itemView.setBackgroundResource(R.drawable.bg_date_selected_monday);
                } else if (toDay.getDayOfWeek().equals(DayOfWeek.TUESDAY)) {
                    holder.itemView.setBackgroundResource(R.drawable.bg_date_selected_tuesday);
                } else if (toDay.getDayOfWeek().equals(DayOfWeek.WEDNESDAY)) {
                    holder.itemView.setBackgroundResource(R.drawable.bg_date_selected_wednesday);
                } else if (toDay.getDayOfWeek().equals(DayOfWeek.THURSDAY)) {
                    holder.itemView.setBackgroundResource(R.drawable.bg_date_selected_thursday);
                } else if (toDay.getDayOfWeek().equals(DayOfWeek.FRIDAY)) {
                    holder.itemView.setBackgroundResource(R.drawable.bg_date_selected_friday);
                } else if (toDay.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
                    holder.itemView.setBackgroundResource(R.drawable.bg_date_selected_saturday);
                } else if (toDay.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
                    holder.itemView.setBackgroundResource(R.drawable.bg_date_selected_sunday);
                }

            }

            //7. Nếu ngày có chứa sự kiện thì hiển thị (image) dấu chấm đánh dấu màu xám. Nếu toàn bộ status >= 2 -> Chấm màu xanh. Nếu có status >3 -> Hiển thị emotion
            try {

                //Đếm số lượng sự kiện trong ngày, và số lượng event đã checked (status = 2)
                int countAllEventOfDay = truyVanDuLieuSQLite.CountAllEventOfDay(date.toString());
                int countEventCheckedOfDay = truyVanDuLieuSQLite.CountEventCheckedOfDay(date.toString());
//                boolean hasLike = truyVanDuLieuSQLite.checkStatusOneDay(date.toString(), 3);
//                boolean hasHeart = truyVanDuLieuSQLite.checkStatusOneDay(date.toString(), 4);
//                boolean hasStar = truyVanDuLieuSQLite.checkStatusOneDay(date.toString(), 5);

                //Nếu ngày có chứa sự kiện thì hiển thị (image) dấu chấm đánh dấu màu xám. Nếu toàn bộ status >= 2 -> Chấm màu xanh
                if(countAllEventOfDay > 0) {
                    holder.img_dots_note.setVisibility(View.VISIBLE);

                    //Nếu đồng thời số event checked = số lượng tổng event của ngày thì hiển thị dấu chấm màu xanh (coi như icon mặc định khi đã có bấm check hoặc chọn emotion)
                    if(countAllEventOfDay == countEventCheckedOfDay){
                        holder.img_dots_note.setImageResource(R.drawable.ic_dots_green);

                        //Nếu có sự kiện đặc biệt thì lại hiển thị emotion đó cho ngày
//                        if(hasLike){
//                            holder.img_dots_note.setImageResource(R.drawable.ic_check_event_2_like);
//                        }
//                        if(hasHeart){
//                            holder.img_dots_note.setImageResource(R.drawable.ic_check_event_3_heart);
//                        }
//                        if(hasStar){
//                            holder.img_dots_note.setImageResource(R.drawable.ic_check_event_4_star);
//                        }
                    }
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            //8. Sự kiện click item -> Truyền dữ liệu và thực hiện hàm bên ngoài
            holder.itemView.setOnClickListener(v -> {
                onItemListener.onItemClick(position, date);
            });

        }
    }

    //IV. Số lượng item có (Tương đương chiều dài mảng)
    @Override
    public int getItemCount(){
        return days.size();
    }

    //Class ViewHolder
    class CalendarViewHolder extends RecyclerView.ViewHolder{
        private TextView txt_day_number;
        private ImageView img_dots_note, img_emotion_note;
        private LinearLayout layout_check_emotion_day;
        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_day_number = itemView.findViewById(R.id.cellDayText);
            img_dots_note = itemView.findViewById(R.id.img_dots_note);
            img_emotion_note = itemView.findViewById(R.id.img_emotion_note);

            layout_check_emotion_day = itemView.findViewById(R.id.layout_check_emotion_day);
        }
    }

    //interface truyền, nhận vị trí định vị và ngày được chọn select (Hàm đang sử dụng trong onClick của ViewHolder để nhận vị trí click và LocalDate tương ứng trong list)
    public interface OnItemListener {
        void onItemClick(int position, LocalDate date);
    }
}
