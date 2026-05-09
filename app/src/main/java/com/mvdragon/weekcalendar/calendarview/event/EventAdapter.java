package com.mvdragon.weekcalendar.calendarview.event;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apachat.swipereveallayout.core.SwipeLayout;
import com.apachat.swipereveallayout.core.ViewBinder;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.mvdragon.weekcalendar.CalendarUtils;
import com.mvdragon.weekcalendar.MainActivity;
import com.mvdragon.weekcalendar.R;
import com.mvdragon.weekcalendar.calendarview.CalendarViewPagerAdapter;
import com.mvdragon.weekcalendar.calendarview.CalendarViewPagerFragment;
import com.mvdragon.weekcalendar.database.TruyVanDuLieuSQLite;
import com.mvdragon.weekcalendar.database.UserUltils;
import com.mvdragon.weekcalendar.model.Event;
import com.mvdragon.weekcalendar.model.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private Context context; //Truyền màn hình sử dụng Adapter này, đồng thời có thể sử dụng context luôn cho các hàm khác
    private List<Event> eventList;
    private TruyVanDuLieuSQLite truyVanDuLieuSQLite;
    private ViewBinder viewBinder = new ViewBinder(); //Dùng để setup swipe cho RecyclerView, dùng hàm của swipe
    private IClickEventItem iClickEventItem, iClickCheckEventItem;
    private boolean showEventTime;

    //IV. Constructor: context, list, sự kiện click một đối tượng trong item (nếu muốn thêm đối tượng thì tạo thêm hàm)
    public EventAdapter(Context context, List<Event> eventList, boolean showEventTime, IClickEventItem iClickEventItem, IClickEventItem iClickCheckEventItem) {
        this.context = context;
        this.eventList = eventList;
        this.showEventTime = showEventTime;
        this.truyVanDuLieuSQLite = new TruyVanDuLieuSQLite(context);
        this.iClickEventItem = iClickEventItem;
        this.iClickCheckEventItem = iClickCheckEventItem;
    }

    //III. Set view for item layout
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    //V. Xử lý trên mỗi item của list event (Có thể tạo hàm trước trong ViewHolder để chỉ gọi hàm trong này, và có thể truyền dữ liệu)
    @SuppressLint({"ResourceAsColor", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);

        //1. Gán giá trị hiển thị
        holder.txt_event_name.setText(event.getEvent_name());

        //2.1 Nếu time empty thì hiển thị ảnh clock, ẩn text time. Nếu có time thì ngược lại, và set thời gian cho textView time
        holder.SetClockViewAndSetTime(event);

        //2.2 Hiển thị text thời gian và ngày tháng (sử dụng trong activity: All List Event)
        if(showEventTime){

            //Chuyển định dạng ngày của event để hiển thị (theo user)
            LocalDate localDate = LocalDate.parse(event.getEvent_date());
            User user = UserUltils.getUserLocal(context);
            String showDate = CalendarUtils.monthYearFromDate(localDate, user.getDateFormat());

            holder.textView_date_event.setVisibility(View.VISIBLE);
            holder.textView_date_event.setText(showDate);

        } else {
            holder.textView_date_event.setVisibility(View.GONE);
        }

        //3.1 Mở activity update event khi bấm click text name event
        holder.txt_event_name.setOnClickListener(v -> {
            holder.OpenUpdateEvent(event);
        });

        //3.2 Focus EditText: Khi long click vào textView thì hiển thị editText để sửa
        holder.txt_event_name.setOnLongClickListener(v -> {
            holder.FocusEditText(event);

            //Không thực hiện hàm click đơn
            return true;
        });

        //3.3 Khi click vào ảnh check -> Ẩn editText (tự out focus), set giá trị cho textView, hiện textView
        holder.img_check_edt_event.setOnClickListener(v -> {
            holder.CheckEditTextOfEventName(event);
        });

        //3.4 Sự kiện clear editText
        holder.img_clear_editText.setOnClickListener(v -> {
            holder.edt_event_name.getText().clear();
        });

        //3.5 Sự kiện khi out focus -> tương đương tự động bấm check editText của event
        holder.edt_event_name.setOnFocusChangeListener((v, hasFocus) -> {

            if(!hasFocus){
                holder.CheckEditTextOfEventName(event);

                //Ẩn bàn phím (Chỉ ẩn tại hàm này mà không cần trong hàm CheckEdt, vì khi bấm check cũng đồng nghĩa với out focus)
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

            }
        });

        //4.1 Sự kiện bấm chọn giờ (Thực hiện chọn giờ tại Dialog, và chỉ set được textView luôn trong dialog nên cần có hàm tiếp theo nữa để set data sau khi textView được cập nhật)
        holder.txt_event_time.setOnClickListener(v -> {
            holder.SelectTimeForEventOnRecyclerView(event);
        });

        //4.2 Sự kiện bấm chọn giờ (tương tự nhưng click vào ảnh clock)
        holder.img_clock_rcv_event.setOnClickListener(v -> {
            holder.SelectTimeForEventOnRecyclerView(event);
        });

        //5. SetLayoutMarkEvent (Đánh dấu trạng thái status của event)
        holder.SetLayoutMarkEvent(event);

        /*
        Các sự kiện của swipe layout
         */

        //Khai báo ViewBinder (cho swipe layout): truyền layout swipe, id của product
        viewBinder.bind(holder.swipeLayout, String.valueOf(event.getId_event()));

        //6. Sự kiện xoá event: trong list đang dùng, danh sách đang hiển thị và trong CSDL (Bảng tên và bảng ảnh)
        holder.layout_swipe_delete_event.setOnClickListener(v -> {

            //Xoá vị trí trong list (đã load hiển thị) tương ứng vị trí được chọn
            eventList.remove(holder.getLayoutPosition());
            //Xoá event và cập nhật trong rcv (với vị trí đã tương tác xoá)
            notifyItemRemoved(holder.getLayoutPosition());
            //Xoá event trong SQLite
            truyVanDuLieuSQLite.DeleteEvent(event.getId_event());
            //Xoá dữ liệu ảnh của event
            truyVanDuLieuSQLite.DeleteAllImageEvent(event.getId_event());

            //Thực hiện hàm tại màn hình sử dụng -> cập nhật nút đánh dấu sự kiện trong ô ngày chứa sự kiện đó (load lại danh sách ngày)
            iClickCheckEventItem.onClickItemEvent();

        });

        //7.1 Đánh dấu event sang mark (status = 2) -> Ẩn hiện ảnh và layout tương ứng -> Cập nhật dữ liệu
        holder.layout_swipe_checked_event.setOnClickListener(v -> {

            //Disable các layout swipe emotion
            holder.DisableAllLayoutSwipeEmotion();

            //Hiện icon ở rcv event
            holder.img_event_checked.setVisibility(View.VISIBLE);
            holder.img_event_checked.setImageResource(R.drawable.ic_check_event_1_check_1);

            //layout check
            holder.layout_swipe_checked_event.setVisibility(View.GONE); //Ẩn đánh dấu sang status = 2
            holder.layout_swipe_checked_disable_event.setVisibility(View.VISIBLE); //Hiện đánh dấu sang status = 1

            //Cập nhật dữ liệu cho event hiện tại trong list và trong SQLite
            event.setEvent_status(2);
            truyVanDuLieuSQLite.UpdateStatusOfEvent(2, event.getId_event());

            //Bấm nút check -> thực hiện hàm tại màn hình sử dụng -> load lại danh sách ngày (cập nhật nút đánh dấu sự kiện trong ngày là all status event = 2 hay chưa)
            iClickCheckEventItem.onClickItemEvent();

            //Đóng swipeLayout
            holder.swipeLayout.close(true);

        });

        //7.2. Bỏ đánh dấu check event (status 2 -> 1) -> Ẩn hiện ảnh và layout tương ứng -> Cập nhật dữ liệu
        holder.layout_swipe_checked_disable_event.setOnClickListener(v -> {

            //Disable các layout swipe emotion và icon emotion ở rcv
            holder.DisableAllLayoutSwipeEmotion();
            //Cập nhật dữ liệu cho event hiện tại trong list và trong SQLite
            event.setEvent_status(1);
            truyVanDuLieuSQLite.UpdateStatusOfEvent(1, event.getId_event());
            //Bấm nút check -> thực hiện hàm tại màn hình sử dụng -> load lại danh sách ngày (cập nhật nút đánh dấu sự kiện trong ngày là all status event = 2 hay chưa)
            iClickCheckEventItem.onClickItemEvent();

            //Đóng swipeLayout
            holder.swipeLayout.close(true);

        });

        //8.1 Đánh dấu event sang mark like (status = 3) -> Ẩn hiện ảnh và layout tương ứng -> Cập nhật dữ liệu
        holder.layout_swipe_like_event.setOnClickListener(v -> {

            //Disable các layout swipe emotion và icon emotion ở rcv
            holder.DisableAllLayoutSwipeEmotion();

            //Hiện icon ở rcv event
            holder.img_event_checked.setVisibility(View.VISIBLE);
            holder.img_event_checked.setImageResource(R.drawable.ic_check_event_2_like);

            //layout like
            holder.layout_swipe_like_event.setVisibility(View.GONE); //Ẩn layout đánh dấu sang status = 3
            holder.layout_swipe_like_disable_event.setVisibility(View.VISIBLE); //Hiện layout để đánh dấu disable

            //Cập nhật dữ liệu cho event hiện tại trong list và trong SQLite
            event.setEvent_status(3);
            truyVanDuLieuSQLite.UpdateStatusOfEvent(3, event.getId_event());

            //Bấm nút check -> thực hiện hàm tại màn hình sử dụng -> load lại danh sách ngày (cập nhật nút đánh dấu sự kiện trong ngày)
            iClickCheckEventItem.onClickItemEvent();

            //Đóng swipeLayout
            holder.swipeLayout.close(true);

        });

        //8.2. Bỏ đánh dấu like event (status -> 1) -> Ẩn hiện ảnh và layout tương ứng -> Cập nhật dữ liệu
        holder.layout_swipe_like_disable_event.setOnClickListener(v -> {

            //Disable các layout swipe emotion và icon emotion ở rcv
            holder.DisableAllLayoutSwipeEmotion();
            //Cập nhật dữ liệu cho event hiện tại trong list và trong SQLite
            event.setEvent_status(1);
            truyVanDuLieuSQLite.UpdateStatusOfEvent(1, event.getId_event());
            //Bấm nút check -> thực hiện hàm tại màn hình sử dụng -> load lại danh sách ngày (cập nhật nút đánh dấu sự kiện trong ngày là all status event = 2 hay chưa)
            iClickCheckEventItem.onClickItemEvent();

            //Đóng swipeLayout
            holder.swipeLayout.close(true);

        });

        //9.1 Đánh dấu event sang mark heart (status = 4) -> Ẩn hiện ảnh và layout tương ứng -> Cập nhật dữ liệu
        holder.layout_swipe_heart_event.setOnClickListener(v -> {

            //Disable các layout swipe emotion và icon emotion ở rcv
            holder.DisableAllLayoutSwipeEmotion();

            //Hiện icon ở rcv event
            holder.img_event_checked.setVisibility(View.VISIBLE);
            holder.img_event_checked.setImageResource(R.drawable.ic_check_event_3_heart);

            //layout heart
            holder.layout_swipe_heart_event.setVisibility(View.GONE);
            holder.layout_swipe_heart_disable_event.setVisibility(View.VISIBLE);

            //Cập nhật dữ liệu cho event hiện tại trong list và trong SQLite
            event.setEvent_status(4);
            truyVanDuLieuSQLite.UpdateStatusOfEvent(4, event.getId_event());

            //Bấm nút check -> thực hiện hàm tại màn hình sử dụng -> load lại danh sách ngày (cập nhật nút đánh dấu sự kiện trong ngày)
            iClickCheckEventItem.onClickItemEvent();

            //Đóng swipeLayout
            holder.swipeLayout.close(true);

        });

        //9.2 Đánh dấu event sang mark heart disable (status 4 -> status 1) -> Ẩn hiện ảnh và layout tương ứng -> Cập nhật dữ liệu
        holder.layout_swipe_heart_disable_event.setOnClickListener(v -> {

            //Disable các layout swipe emotion và icon emotion ở rcv
            holder.DisableAllLayoutSwipeEmotion();
            //Cập nhật dữ liệu cho event hiện tại trong list và trong SQLite
            event.setEvent_status(1);
            truyVanDuLieuSQLite.UpdateStatusOfEvent(1, event.getId_event());
            //Bấm nút check -> thực hiện hàm tại màn hình sử dụng -> load lại danh sách ngày (cập nhật nút đánh dấu sự kiện trong ngày)
            iClickCheckEventItem.onClickItemEvent();

            //Đóng swipeLayout
            holder.swipeLayout.close(true);

        });

        //10.1 Đánh dấu event sang mark star (status = 5) -> Ẩn hiện ảnh và layout tương ứng -> Cập nhật dữ liệu
        holder.layout_swipe_star_event.setOnClickListener(v -> {

            //Disable các layout swipe emotion và icon emotion ở rcv
            holder.DisableAllLayoutSwipeEmotion();

            //Hiện icon ở rcv event
            holder.img_event_checked.setVisibility(View.VISIBLE);
            holder.img_event_checked.setImageResource(R.drawable.ic_check_event_4_star);

            //layout star
            holder.layout_swipe_star_event.setVisibility(View.GONE); //Ẩn layout để đánh dấu sang status = 5
            holder.layout_swipe_star_disable_event.setVisibility(View.VISIBLE); //Hiện đánh dấu sang status = 1

            //Cập nhật dữ liệu cho event hiện tại trong list và trong SQLite
            event.setEvent_status(5);
            truyVanDuLieuSQLite.UpdateStatusOfEvent(5, event.getId_event());

            //Bấm nút check -> thực hiện hàm tại màn hình sử dụng -> load lại danh sách ngày (cập nhật nút đánh dấu sự kiện trong ngày)
            iClickCheckEventItem.onClickItemEvent();

            //Đóng swipeLayout
            holder.swipeLayout.close(true);

        });

        //10.2 Đánh dấu event sang mark heart disable (status 4 -> status 1) -> Ẩn hiện ảnh và layout tương ứng -> Cập nhật dữ liệu
        holder.layout_swipe_star_disable_event.setOnClickListener(v -> {

            //Disable các layout swipe emotion và icon emotion ở rcv
            holder.DisableAllLayoutSwipeEmotion();
            //Cập nhật dữ liệu cho event hiện tại trong list và trong SQLite
            event.setEvent_status(1);
            truyVanDuLieuSQLite.UpdateStatusOfEvent(1, event.getId_event());
            //Bấm nút check -> thực hiện hàm tại màn hình sử dụng -> load lại danh sách ngày (cập nhật nút đánh dấu sự kiện trong ngày)
            iClickCheckEventItem.onClickItemEvent();

            //Đóng swipeLayout
            holder.swipeLayout.close(true);

        });

        //9. Click vào image nút đánh dấu sự kiện -> Ẩn nút checked trong rcv event ở màn hình chính, chuyển trạng thái event về 1 (Hiện tại không dùng để có cảm giác chắc chắn sau khi đánh dấu)
//        holder.img_event_checked.setOnClickListener(v -> {
//
//            //Ẩn checked disable, hiện checked
//            holder.img_event_checked.setVisibility(View.GONE);
//            holder.layout_swipe_checked_disable_event.setVisibility(View.GONE);
//            holder.layout_swipe_checked_event.setVisibility(View.VISIBLE);
//
//            //Cập nhật dữ liệu cho event hiện tại trong list và trong SQLite
//            event.setEvent_status(1);
//            truyVanDuLieuSQLite.UpdateStatusOfEvent(1, event.getId_event());
//        });

        //11. Mở activity mới sửa toàn bộ event (truyền event của item): trong các đối tượng: layout trong swipe, text name event, text time event, icon checked
        holder.layout_swipe_edit_event.setOnClickListener(v -> {
            holder.OpenUpdateEvent(event);
        });

        holder.img_event_checked.setOnLongClickListener(v -> {
            holder.OpenUpdateEvent(event);

            //Không thực hiện hàm click đơn
            return true;
        });

        holder.txt_event_time.setOnLongClickListener(v -> {
            holder.OpenUpdateEvent(event);

            //Không thực hiện hàm click đơn
            return true;
        });

        holder.img_clock_rcv_event.setOnLongClickListener(v -> {
            holder.OpenUpdateEvent(event);

            //Không thực hiện hàm click đơn
            return true;
        });

    }

    //II. length of list
    @Override
    public int getItemCount() {
        return eventList.size();
    }


    //I. ViewHolder: Khai báo các thành phần, hàm của một item
    class EventViewHolder extends RecyclerView.ViewHolder{
        private TextView txt_event_name, txt_event_time, textView_date_event;
        private EditText edt_event_name;
        private ImageView img_check_edt_event, img_clear_editText, img_event_checked, img_clock_rcv_event;
        private LinearLayout layout_editText_event, layout_textView_event;
        private String timeOfEvent; //Tham số riêng để chọn thời gian
        private RelativeLayout layout_swipe_edit_event, layout_swipe_checked_event, layout_swipe_delete_event, layout_swipe_checked_disable_event, layout_swipe_like_event, layout_swipe_like_disable_event;
        private RelativeLayout layout_swipe_heart_event, layout_swipe_heart_disable_event, layout_swipe_star_event, layout_swipe_star_disable_event;
        private SwipeLayout swipeLayout;

        @SuppressLint("NotifyDataSetChanged")
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);

            //1. Ánh xạ
            AnhXa();

            //2. Đặt hàm sẽ thực hiện của item nếu cần hoặc muốn
        }

        //1. Ánh xạ
        private void AnhXa() {
            txt_event_name = itemView.findViewById(R.id.textView_event_name);
            edt_event_name = itemView.findViewById(R.id.editText_event_name);
            img_check_edt_event = itemView.findViewById(R.id.image_check_event_name);
            txt_event_time = itemView.findViewById(R.id.textView_time_of_event);
            textView_date_event = itemView.findViewById(R.id.textView_date_event);

            layout_textView_event = itemView.findViewById(R.id.layout_textView_event);
            layout_editText_event = itemView.findViewById(R.id.layout_editText_event);
            timeOfEvent = "";

            img_clear_editText = itemView.findViewById(R.id.image_clear_editText);
            img_event_checked = itemView.findViewById(R.id.image_event_checked);
            img_clock_rcv_event = itemView.findViewById(R.id.image_clock_rcv_event);

            //Các layout trong swipe
            swipeLayout = itemView.findViewById(R.id.layout_all_event_item);
            layout_swipe_edit_event = itemView.findViewById(R.id.layout_swipe_edit_event);
            layout_swipe_checked_event = itemView.findViewById(R.id.layout_swipe_checked_event);
            layout_swipe_checked_disable_event = itemView.findViewById(R.id.layout_swipe_checked_disable_event);
            layout_swipe_delete_event = itemView.findViewById(R.id.layout_swipe_delete_event);

            layout_swipe_like_event = itemView.findViewById(R.id.layout_swipe_like_event);
            layout_swipe_like_disable_event = itemView.findViewById(R.id.layout_swipe_like_disable_event);
            layout_swipe_heart_event = itemView.findViewById(R.id.layout_swipe_heart_event);
            layout_swipe_heart_disable_event = itemView.findViewById(R.id.layout_swipe_heart_disable_event);
            layout_swipe_star_event = itemView.findViewById(R.id.layout_swipe_star_event);
            layout_swipe_star_disable_event = itemView.findViewById(R.id.layout_swipe_star_disable_event);

        }

        //2.1 setViewOfClock
        public void setViewOfClock(Event event){

            //Nếu time empty thì hiển thị ảnh clock, ẩn text time
            if(event.getEvent_time().isEmpty()){
                txt_event_time.setVisibility(View.GONE);
                img_clock_rcv_event.setVisibility(View.VISIBLE);

            //Nếu có time thì hiển thị time, ẩn ảnh
            } else {
                txt_event_time.setVisibility(View.VISIBLE);
                img_clock_rcv_event.setVisibility(View.GONE);
            }
        }

        //2.2 Nếu time empty thì hiển thị ảnh clock, ẩn text time. Nếu có time thì ngược lại | set thời gian cho textView time
        public void SetClockViewAndSetTime(Event event){
            if(event.getEvent_time().isEmpty()){
                txt_event_time.setVisibility(View.GONE);
                img_clock_rcv_event.setVisibility(View.VISIBLE);

            //Nếu có time thì hiển thị time, ẩn ảnh
            } else {
                txt_event_time.setText(event.getEvent_time());
                txt_event_time.setVisibility(View.VISIBLE);
                img_clock_rcv_event.setVisibility(View.GONE);
            }
        }

        //3.1 Focus EditText: Khi click vào textView thì hiển thị editText để sửa
        @SuppressLint("ResourceAsColor")
        public void FocusEditText(Event event){

            layout_editText_event.setVisibility(View.VISIBLE); //Hiển thị layout edt
            edt_event_name.requestFocus(); //trỏ chuột vào edt

            //Gán giá trị
            edt_event_name.setText(event.getEvent_name());
            edt_event_name.setSelection(edt_event_name.getText().length()); //Vị trí trỏ chuột
            edt_event_name.setTextColor(R.color.gray_4_black); //Đổi màu thành kiểu edit (chưa nhận resour mà thành màu mờ vẫn dùng được)

            //Ẩn textView tên sự kiện, ảnh clock và text giờ
            layout_textView_event.setVisibility(View.GONE);
            txt_event_time.setVisibility(View.GONE);
            img_clock_rcv_event.setVisibility(View.GONE);

            //Hiện bàn phím
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        }

        //3.2 Bấm ảnh check của EditText
        public void CheckEditTextOfEventName(Event event){

            //a. Lấy giá trị hiện tại trước khi click
            String textEventNameEdited = edt_event_name.getText().toString().trim();
            //gán giá trị đã sửa cho event hiện tại trong list
            event.setEvent_name(textEventNameEdited);

            //b. gán lại hiển thị cho textView
            txt_event_name.setText(textEventNameEdited);
            //cập nhật SQLite cho event theo id_event
            truyVanDuLieuSQLite.UpdateNameOfEvent(textEventNameEdited, event.getId_event());

            //c. Ẩn layout editText và hiện layout textView
            layout_editText_event.setVisibility(View.GONE);
            layout_textView_event.setVisibility(View.VISIBLE);

            //d. Nếu time empty thì hiển thị ảnh clock, ẩn text time
            setViewOfClock(event);

        }

        //4.1 Sự kiện bấm chọn giờ (Thực hiện chọn giờ tại Dialog, và chỉ set được textView luôn trong dialog nên cần có hàm tiếp theo nữa để set data sau khi textView được cập nhật)
        public void SelectTimeForEventOnRecyclerView(Event event){

            int hour, minute;

            //Nếu chưa có thời gian thì set sẵn một mốc cụ thể
            if(event.getEvent_time().isEmpty()){
                hour = 12;
                minute = 0;

            //Nếu đã có thời gian rồi thì lấy để đặt cho dialog
            } else {
                String timeEvent = event.getEvent_time();
                DateTimeFormatter formatterHour = DateTimeFormatter.ofPattern("HH:mm");
                LocalTime localTimeHour = LocalTime.parse(timeEvent, formatterHour);

                //Lấy giờ, phút
                hour = localTimeHour.getHour();
                minute = localTimeHour.getMinute();
            }

            //Gọi Dialog với thời gian set sẵn của event
            selectTimeDialog(hour, minute, event);
        }

        //4.2 Hàm tạo cửa sổ chọn giờ, Lấy sẵn giờ phút theo dữ liệu
        public void selectTimeDialog(int hourOfDaySetup, int minuteSetup, Event event){

            //A. Tạo ngày tháng sẵn để cài đặt hiển thị mẫu cho cửa sổ
            boolean is24HourView = true;

            TimePickerDialog timePickerDialog = new TimePickerDialog(context, android.R.style.Theme_Holo_Light_Dialog, (view, hourOfDay, minute) -> {
                //B. Lấy thời gian đã chọn ở cửa sổ (định dạng LocalTime, chuyển về string)
                timeOfEvent = LocalTime.of(hourOfDay, minute).toString();

                //C. set hiển thị cho textView
                txt_event_time.setText(timeOfEvent);

                //D. Set dữ liệu cho event hiện tại trong list (được truyền vào hàm này), cập nhật cho SQLite
                event.setEvent_time(timeOfEvent);
                truyVanDuLieuSQLite.UpdateTimeOfEvent(timeOfEvent, event.getId_event());

                //E. Ẩn image chờ time, hiện text time
                img_clock_rcv_event.setVisibility(View.GONE);
                txt_event_time.setVisibility(View.VISIBLE);

                //F. Thực hiện hàm bên ngoài sự kiện, (Cập nhật adapter danh sách event) | Hiện tại ở đây không truyền dữ liệu
                iClickEventItem.onClickItemEvent();

            }, hourOfDaySetup, minuteSetup, is24HourView );

            timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            timePickerDialog.setTitle("Select time"); //Tiêu đề
            timePickerDialog.show(); //Hiển thị cửa sổ

        }

        //5.1 SetLayoutMarkEvent
        public void SetLayoutMarkEvent(Event event){

            //Nếu event_status = 1 -> Ẩn hiện ảnh và layout tương ứng
            if(event.getEvent_status() == 1){

                DisableAllLayoutSwipeEmotion();

            //Nếu event_status = 2 -> Ẩn hiện ảnh và layout tương ứng
            } else if (event.getEvent_status() == 2){

                //Disable các layout Emotion
                DisableAllLayoutSwipeEmotion();

                //Hiện đánh dấu event ở rcv
                img_event_checked.setVisibility(View.VISIBLE);
                img_event_checked.setImageResource(R.drawable.ic_check_event_1_check_1);

                //Hiện layout check
                layout_swipe_checked_event.setVisibility(View.GONE); //Ẩn layout bấm check sang status = 2
                layout_swipe_checked_disable_event.setVisibility(View.VISIBLE); //Hiện layout bấm check disable để chuyển về status = 1 (vì đã =1 rồi)

            //Nếu event_status = 3 -> Ẩn hiện ảnh và layout tương ứng
            } else if (event.getEvent_status() == 3){

                DisableAllLayoutSwipeEmotion();

                //Hiện đánh dấu event ở rcv
                img_event_checked.setVisibility(View.VISIBLE);
                img_event_checked.setImageResource(R.drawable.ic_check_event_2_like);

                //layout like
                layout_swipe_like_event.setVisibility(View.GONE); //Ẩn layout bấm check sang status = 3
                layout_swipe_like_disable_event.setVisibility(View.VISIBLE); //Hiện layout bấm disable like sang status = 1

            } else if (event.getEvent_status() == 4){

                DisableAllLayoutSwipeEmotion();

                //Hiện đánh dấu event ở rcv
                img_event_checked.setVisibility(View.VISIBLE);
                img_event_checked.setImageResource(R.drawable.ic_check_event_3_heart);

                //layout heart
                layout_swipe_heart_event.setVisibility(View.GONE); //Ẩn layout bấm check sang status = 4
                layout_swipe_heart_disable_event.setVisibility(View.VISIBLE); //Hiện layout bấm disable like sang status = 1

            } else if (event.getEvent_status() == 5) {
                DisableAllLayoutSwipeEmotion();

                //Hiện đánh dấu event ở rcv
                img_event_checked.setVisibility(View.VISIBLE);
                img_event_checked.setImageResource(R.drawable.ic_check_event_4_star);

                //layout star
                layout_swipe_star_event.setVisibility(View.GONE); //Ẩn layout để đánh dấu sang status = 5
                layout_swipe_star_disable_event.setVisibility(View.VISIBLE); //Hiện đánh dấu sang status = 1
            }
        }

        //5.2 Disable hết các layout và nút check trên rcv (Hiển thị layout sẵn sàng bấm check) dùng để tái sử dụng
        public void DisableAllLayoutSwipeEmotion(){
            //Ẩn icon ở rcv event
            img_event_checked.setVisibility(View.GONE);

            //layout check
            layout_swipe_checked_event.setVisibility(View.VISIBLE); //Hiện đánh dấu sang status = 2
            layout_swipe_checked_disable_event.setVisibility(View.GONE); //Ẩn đánh dấu sang status = 1

            //layout like
            layout_swipe_like_event.setVisibility(View.VISIBLE); //Hiện layout để đánh dấu sang status = 3
            layout_swipe_like_disable_event.setVisibility(View.GONE); //Ẩn layout để đánh dấu disable

            //layout heart
            layout_swipe_heart_event.setVisibility(View.VISIBLE); //Hiện layout để đánh dấu sang status = 4
            layout_swipe_heart_disable_event.setVisibility(View.GONE); //Ẩn đánh dấu sang status = 1

            //layout star
            layout_swipe_star_event.setVisibility(View.VISIBLE); //Hiện layout để đánh dấu sang status = 5
            layout_swipe_star_disable_event.setVisibility(View.GONE); //Ẩn đánh dấu sang status = 1
        }

        //11. Mở fragment Update event: truyền, lưu event được update -> Mở fragment
        public void OpenUpdateEvent(Event event){
//            Intent intent = new Intent(context, UpdateEventActivity.class);
//            intent.putExtra("event", event);
//            context.startActivity(intent);
//            Animatoo.INSTANCE.animateSlideLeft(context);

            //A. Mở fragment update event nếu đang ở Main Calendar (menu 0)
            if(UserUltils.getUserLocal(context).getFragment_user() == 0){
                UserUltils.saveEventLocal(context, event); //Lưu event vào shared
                CalendarViewPagerFragment.setCurrentItemCalendarViewPager(2); //2: Số thứ tự của fragment update event trong ViewPager

                //Load dữ liệu event trong Fragment update event khi bấm vào event (Chỉ cần load lần đầu 1 lần ở đây) | Đặt delay để tránh lỗi không load kịp (load list ảnh để tự động trong fragment để khi mở ảnh trả về thì load luôn)
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    UpdateEventFragment.loadEventData();
                    UpdateEventFragment.loadDataForActivity();
                }, 300);

            //B. Nếu đang ở Main View All Event (menu 1)
            } else if (UserUltils.getUserLocal(context).getFragment_user() == 1) {
                UserUltils.saveEventLocal(context, event); //Lưu event vào shared
                ViewAllEventViewPagerFragment.setCurrentItemCalendarViewPager(1); //1: Số thứ tự của fragment update event trong ViewPager

                //Load dữ liệu event trong Fragment update event khi bấm vào event (Chỉ cần load lần đầu 1 lần ở đây) | Không cần đặt delay vì khi ViewPager 0 được mở thì cũng đã load săn của Pager 1
                UpdateEventViewAllFragment.loadEventData();
                UpdateEventViewAllFragment.loadDataForActivity();

            }
        }
    }


    //Interface truyền dữ liệu, thực hiện một hàm cụ thể bên ngoài adapter (truyền dữ liệu từ adapter ra ngoài activity (fragment) trong hàm đó nếu cần)
    public interface IClickEventItem {
        void onClickItemEvent();
    }

}
