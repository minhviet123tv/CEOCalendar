package com.mvdragon.weekcalendar.notes;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.mvdragon.weekcalendar.MainActivity;
import com.mvdragon.weekcalendar.R;
import com.mvdragon.weekcalendar.database.TruyVanDuLieuSQLite;
import com.mvdragon.weekcalendar.menu.MenuViewPagerFragment;
import com.mvdragon.weekcalendar.model.MyNote;
import com.mvdragon.weekcalendar.notes.ViewFolderRecyclerViewAdapter;

import java.sql.SQLException;
import java.util.ArrayList;

public class ListAllNotesFragment extends Fragment {
    private static View view;
    private ImageView btn_clear_list_all_note;
    private static RecyclerView rcv_list_all_note;
    private static ArrayList<MyNote> allMyNoteArrayList;
    private static TruyVanDuLieuSQLite truyVanDuLieuSQLite;

    public ListAllNotesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_list_all_notes, container, false);

        //1.1 Ánh xạ
        AnhXa();

        //2. Load rcv all notes, add decoration
        LoadRecyclerViewAllNotes();
        //set decoration
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL);
        rcv_list_all_note.addItemDecoration(decoration);

        //3. Delete all note
        btn_clear_list_all_note.setOnClickListener(v -> {
            deleteAllNotesDialog(Gravity.CENTER);
        });


        return view;
    }



    //1. Ánh xạ
    private void AnhXa() {
        btn_clear_list_all_note = view.findViewById(R.id.btn_clear_list_all_note);
        rcv_list_all_note = view.findViewById(R.id.rcv_list_all_note);

        truyVanDuLieuSQLite = new TruyVanDuLieuSQLite(view.getContext());
    }

    //2. Load rcv all notes
    public static void LoadRecyclerViewAllNotes() {
        allMyNoteArrayList = truyVanDuLieuSQLite.getAllNote();

        //adapter
        ViewFolderRecyclerViewAdapter adapter = new ViewFolderRecyclerViewAdapter(view.getContext(), allMyNoteArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        rcv_list_all_note.setLayoutManager(linearLayoutManager);
        rcv_list_all_note.setAdapter(adapter);

    }

    //3.1 Dialog delete all event
    private void deleteAllNotesDialog(int gravity) {

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
        TextView txt_title_delete_all_notes = dialog.findViewById(R.id.textView_title_delete_all_event);
        Button btn_yes_delete = dialog.findViewById(R.id.btn_yes_delete);
        Button btn_no_delete = dialog.findViewById(R.id.btn_no_delete);

        //set title
        txt_title_delete_all_notes.setText(R.string.delete_all_notes);

        //5. Các hàm thực hiện trong dialog: Gọi hàm xoá all notes, dismiss
        btn_yes_delete.setOnClickListener(v -> {
            deleteAllNotes();
            dialog.dismiss();
        });

        btn_no_delete.setOnClickListener(v -> {
            dialog.cancel();
        });

        //12. show
        dialog.show();

    }

    //3.2 Delete all note
    private void deleteAllNotes() {
        //Xoá hết notes và dữ liệu liên quan
        truyVanDuLieuSQLite.deleteAllNotes();
        //Load lại rcv
        LoadRecyclerViewAllNotes();
    }


    //A. Back
    public void backFragment(){
        MenuViewPagerFragment.setCurrentItemMenu(0);
    }

    //B. onResume
    @Override
    public void onResume() {
        super.onResume();

        //Load rcv (Để load lại khi mở activity view note)
        LoadRecyclerViewAllNotes();
    }
}