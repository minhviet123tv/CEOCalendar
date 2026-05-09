package com.mvdragon.weekcalendar.calendarview.event;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.mvdragon.weekcalendar.MainActivity;
import com.mvdragon.weekcalendar.R;
import com.mvdragon.weekcalendar.calendarview.event.EventAdapter;
import com.mvdragon.weekcalendar.database.TruyVanDuLieuSQLite;
import com.mvdragon.weekcalendar.database.UserUltils;
import com.mvdragon.weekcalendar.menu.MenuViewPagerFragment;
import com.mvdragon.weekcalendar.model.Event;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ListAllEventFragment extends Fragment {
    private static View view;
    private ImageView btn_clear_list_event;
    private ImageView btn_0_menu_list_all_event;
    private ImageView btn_1_menu_list_event_star;
    private ImageView btn_2_menu_list_event_heart;
    private ImageView btn_3_menu_list_event_like;
    private ImageView btn_4_menu_list_event_checked;
    private ImageView btn_5_menu_list_event_uncheck;
    private static RecyclerView rcv_all_list_event;
    public static List<Event> listAllEventSQLite;
    public static int caseOfListEvent;
    private static TruyVanDuLieuSQLite truyVanDuLieuSQLite;

    public ListAllEventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_list_all_event, container, false);

        //1.1 Ánh xạ
        AnhXa();

        //1.2 Cài đặt google admob (onResume)
//        AddGoogleAdmob();

        //2. Load rcv, adapter khi mới vào activity
        try {
            //a. Lấy danh sách toàn bộ sự kiện: Truyền ngày đang được select
            listAllEventSQLite = truyVanDuLieuSQLite.getListAllEvent();
            caseOfListEvent = 0;
            setEventAdpater();

            //set itemDecoration bên ngoài hàm load adapter (nếu để bên trong thì khi bấm nhiều lần sẽ bị giãn dòng)
            RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL);
            rcv_all_list_event.addItemDecoration(itemDecoration);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        //3. Clear list event
        btn_clear_list_event.setOnClickListener(v -> {
            deleteAllEventDialog(Gravity.CENTER);
        });

        //4. Chọn tất cả event để xem
        btn_0_menu_list_all_event.setOnClickListener(v -> {
            try {
                listAllEventSQLite = truyVanDuLieuSQLite.getListAllEvent();
                caseOfListEvent = 0;
                setEventAdpater();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        //5. Các trường hợp chọn kiểu event (status 5: star, 4: heart, 3: like, 2:checked, 1: uncheck)
        btn_1_menu_list_event_star.setOnClickListener(v -> {
            try {
                listAllEventSQLite = truyVanDuLieuSQLite.getCaseListEvent(5);
                caseOfListEvent = 5;
                setEventAdpater();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        btn_2_menu_list_event_heart.setOnClickListener(v -> {
            try {
                listAllEventSQLite = truyVanDuLieuSQLite.getCaseListEvent(4);
                caseOfListEvent = 4;
                setEventAdpater();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        btn_3_menu_list_event_like.setOnClickListener(v -> {
            try {
                listAllEventSQLite = truyVanDuLieuSQLite.getCaseListEvent(3);
                caseOfListEvent = 3;
                setEventAdpater();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        btn_4_menu_list_event_checked.setOnClickListener(v -> {
            try {
                listAllEventSQLite = truyVanDuLieuSQLite.getCaseListEvent(2);
                caseOfListEvent = 2;
                setEventAdpater();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        btn_5_menu_list_event_uncheck.setOnClickListener(v -> {
            try {
                listAllEventSQLite = truyVanDuLieuSQLite.getCaseListEvent(1);
                caseOfListEvent = 1;
                setEventAdpater();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });



        return view;
    }

    //1. Ánh xạ
    private void AnhXa() {
        btn_clear_list_event = view.findViewById(R.id.btn_clear_list_event);

        btn_0_menu_list_all_event = view.findViewById(R.id.btn_0_menu_list_all_event);
        btn_1_menu_list_event_star = view.findViewById(R.id.btn_1_menu_list_event_star);
        btn_2_menu_list_event_heart = view.findViewById(R.id.btn_2_menu_list_event_heart);
        btn_3_menu_list_event_like = view.findViewById(R.id.btn_3_menu_list_event_like);
        btn_4_menu_list_event_checked = view.findViewById(R.id.btn_4_menu_list_event_checked);
        btn_5_menu_list_event_uncheck = view.findViewById(R.id.btn_5_menu_list_event_uncheck);

        rcv_all_list_event = view.findViewById(R.id.rcv_all_list_event);

        truyVanDuLieuSQLite = new TruyVanDuLieuSQLite(view.getContext());
        listAllEventSQLite = new ArrayList<>();
        caseOfListEvent = 0;
    }

    //2. Load rcv, adapter: Lấy toàn bộ danh sách event của tài khoản (tương đương toàn bộ event trong SQLite) và hiển thị
    public static void setEventAdpater() throws SQLException {

        //a. Khai báo adapter cho rcv: context, list event, boolean hiện dòng date time, Sự kiện cập nhật rcv sau khi chọn giờ | sự kiện cập nhật ảnh check trong ngày khi bấm swipe check
        EventAdapter eventAdapter = new EventAdapter(view.getContext(), listAllEventSQLite, true, () -> {

            //Sự kiện cập nhật rcv sau khi chọn giờ: Đặt độ trễ 200 milisecon để có thể cảm nhận rõ sự thay đổi trong rcv
            Handler handler = new Handler();
            handler.postDelayed(() -> {

                //Gọi lại chính setEventAdpater() để cập nhật rcv theo dữ liệu thời gian vừa chỉnh | ở fragment này list chỉ gọi được 1 lần load adapter (cũng có thể do static) nên để load lại adapter thì cần lưu case lại để lấy lại list event đã gọi
                try {
                    if(caseOfListEvent == 0){
                        listAllEventSQLite = truyVanDuLieuSQLite.getListAllEvent();
                    } else {
                        listAllEventSQLite = truyVanDuLieuSQLite.getCaseListEvent(caseOfListEvent);
                    }
                    setEventAdpater();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            }, 200);

        }, () -> {

            //Sự kiện khi bấm icon emotion -> Gọi lại chính setEventAdpater() để cập nhật rcv theo dữ liệu thời gian vừa chỉnh
            try {
                if(caseOfListEvent == 0){
                    listAllEventSQLite = truyVanDuLieuSQLite.getListAllEvent();
                } else {
                    listAllEventSQLite = truyVanDuLieuSQLite.getCaseListEvent(caseOfListEvent);
                }
                setEventAdpater();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        //b. Kiểu hiển thị của rcv
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        rcv_all_list_event.setLayoutManager(linearLayoutManager);
        rcv_all_list_event.setAdapter(eventAdapter);

    }

    //3. Dialog delete all event
    private void deleteAllEventDialog(int gravity) {

        //1. Tạo dialog (Dùng dialog cách này để tao Window với giao diện đẹp và như ý hơn)
        Dialog dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //Không tiêu đề mặc định
        dialog.setContentView(R.layout.dialog_delete_all_event); // layout của dialog

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
        Button btn_yes_delete = dialog.findViewById(R.id.btn_yes_delete);
        Button btn_no_delete = dialog.findViewById(R.id.btn_no_delete);

        //5. Các hàm thực hiện trong dialog
        btn_yes_delete.setOnClickListener(v -> {
            //Thực hiện xoá
            try {
                truyVanDuLieuSQLite.DeleteAllEvent();
                listAllEventSQLite = truyVanDuLieuSQLite.getListAllEvent();
                setEventAdpater();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            dialog.cancel();
        });

        btn_no_delete.setOnClickListener(v -> {
            dialog.cancel();
        });

        //12. show
        dialog.show();

    }

    //A. onResume
    @Override
    public void onResume() {
        super.onResume();

        //Load lại danh sách toàn bộ sự kiện khi vào activity (khi trở về từ update event)
        try {
            listAllEventSQLite = truyVanDuLieuSQLite.getListAllEvent();
            caseOfListEvent = 0;
            setEventAdpater();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}