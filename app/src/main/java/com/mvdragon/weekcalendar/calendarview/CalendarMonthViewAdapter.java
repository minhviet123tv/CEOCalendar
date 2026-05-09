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
public class CalendarMonthViewAdapter extends RecyclerView.Adapter<CalendarMonthViewAdapter.MonthViewHolder> {
    private final Context context;
    private ArrayList<LocalDate> days; //danh sách ngày
    private final OnItemListener onItemListener;
    private final TruyVanDuLieuSQLite truyVanDuLieuSQLite;

    //I. Hàm khởi tạo (Khai báo trực tiếp SQLite trong hàm khởi tạo với màn hình được truyền vào, vì trong hàm này thì context mới bắt đầu được sử dụng)
    public CalendarMonthViewAdapter(Context context, ArrayList<LocalDate> days, OnItemListener onItemListener) {
        this.context = context;
        this.truyVanDuLieuSQLite = new TruyVanDuLieuSQLite(context);
        this.days = days;
        this.onItemListener = onItemListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void changeData(){
        notifyDataSetChanged();
    }

    //II. ViewHolder (Class CalendarViewHolder đã tạo bên ngoài)
    @NonNull
    @Override
    public MonthViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //Khai báo view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_date_month_view, parent,false);

        return new MonthViewHolder(view);
    }

    //III. Xử lý trong từng item (ngày)
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull MonthViewHolder holder, int position){
        //1. Lấy ngày theo thứ tự trong danh sách
        LocalDate date = days.get(position);

        //2. Nếu ngày bị null thì set (text hiển thị ngày) trống
        if(date == null){
            holder.txt_day_month_view.setText("");

        //3.1 Nếu có ngày trong list thì set text hiển thị ngày
        } else {

            holder.txt_day_month_view.setText(String.valueOf(date.getDayOfMonth()));

            //3.2 Nếu ngày trong list (tháng) có tháng trùng với tháng của ngày đang được select thì set màu
            if (date.getMonthValue() == CalendarUtils.selectedDateOnMonthViewDialog.getMonthValue()) {
                holder.txt_day_month_view.setTextColor(Color.BLACK);
            } else {
                //Nếu không phải ngày trong tháng thì ẩn item hoặc đổi màu text
                holder.txt_day_month_view.setTextColor(Color.GRAY);
//                holder.itemView.setVisibility(View.GONE);
            }

            //3.3 Nếu ngày trong danh sách trùng với ngày hiện tại thì đổi màu text của ngày (luôn chỉ có 1 ngày trùng với ngày hiện tại trong lịch)
            LocalDate toDay = LocalDate.now(); //LocalDate.of(2023,11,19);
            if (date.equals(toDay)) {
//                holder.txt_day_month_view.setTextColor(Color.BLUE);
                holder.txt_day_month_view.setText(Html.fromHtml("<font color = #0954FD>" + holder.txt_day_month_view.getText().toString()));
                holder.txt_day_month_view.setTypeface(Typeface.DEFAULT_BOLD);
            }

            //3.4 Tăng chiều cao layout: nếu số ngày trong list > 7 (xem tháng) | Hiện tại cho thấy phải set bên ngoài ở rcv
//            if(days.size() > 7){
//                holder.itemView.getLayoutParams().height = 90;
//            }

            //4. Nếu ngày trùng với ngày được selected thì đổi màu ô, set kích thước chữ của ô được chọn to hơn (nếu muốn)
            if (date.equals(CalendarUtils.selectedDateOnMonthViewDialog)) {

                //Set màu ô ngày selected tương ứng cho ngày hôm nay (trong tuần mỗi ngày một màu)
                if (toDay.getDayOfWeek().equals(DayOfWeek.MONDAY)) {
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

            //5. Nếu ngày có chứa sự kiện -> hiển thị dấu chấm màu xám. Nếu toàn bộ status = 2 -> Chấm màu xanh
            try {

                //Đếm số lượng sự kiện trong ngày, và số lượng event đã checked (status = 2)
                int countAllEventOfDay = truyVanDuLieuSQLite.CountAllEventOfDay(date.toString());
                int countEventCheckedOfDay = truyVanDuLieuSQLite.CountEventCheckedOfDay(date.toString());
//                boolean hasLike = truyVanDuLieuSQLite.checkStatusOneDay(date.toString(), 3);
//                boolean hasHeart = truyVanDuLieuSQLite.checkStatusOneDay(date.toString(), 4);
//                boolean hasStar = truyVanDuLieuSQLite.checkStatusOneDay(date.toString(), 5);

                //Nếu ngày có chứa sự kiện -> hiển thị (image) dấu chấm đánh dấu màu xám | Nếu toàn bộ status >= 2 -> Chấm màu xanh | Nếu có sự kiện đặc biệt -> Chuyển sang icon của sự kiện đó cho cả ngày
                if (countAllEventOfDay > 0) {
                    holder.img_dots_note.setVisibility(View.VISIBLE);

                    //Nếu đồng thời số event checked = số lượng tổng event của ngày thì hiển thị dấu chấm màu xanh (coi như icon mặc định khi đã có bấm check hoặc chọn emotion)
                    if (countAllEventOfDay == countEventCheckedOfDay) {
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

            //6. Sự kiện khi click vào item: truyền dữ liệu và thực hiện hàm bên ngoài (cập nhật adapter bên ngoài có vẻ rõ ràng và chắc chắn hơn) khi click item
            holder.itemView.setOnClickListener(v -> {
                //Truyền dữ liệu ra hàm thực hiện bên ngoài adapter
                onItemListener.onItemClick(position, date);
            });
        }

    }

    //IV. Số lượng item có (Tương đương chiều dài mảng)
    @Override
    public int getItemCount(){
        return days.size();
    }

    //ViewHolder
    class MonthViewHolder extends RecyclerView.ViewHolder{
        public TextView txt_day_month_view;
        public ImageView img_dots_note, img_emotion_note;

        public MonthViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_day_month_view = itemView.findViewById(R.id.txt_date_month_view);
            img_dots_note = itemView.findViewById(R.id.img_dots_note_month_view);
            img_emotion_note = itemView.findViewById(R.id.img_emotion_note);

        }
    }

    //interface truyền, nhận vị trí định vị và ngày được chọn select (Hàm đang sử dụng trong onClick của ViewHolder để nhận vị trí click và LocalDate tương ứng trong list)
    public interface OnItemListener {
        void onItemClick(int position, LocalDate date);
    }
}
