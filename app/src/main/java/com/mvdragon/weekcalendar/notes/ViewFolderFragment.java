package com.mvdragon.weekcalendar.notes;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.mvdragon.weekcalendar.MainActivity;
import com.mvdragon.weekcalendar.R;
import com.mvdragon.weekcalendar.database.TruyVanDuLieuSQLite;
import com.mvdragon.weekcalendar.database.UserUltils;
import com.mvdragon.weekcalendar.model.MyFolder;
import com.mvdragon.weekcalendar.model.MyNote;
import com.mvdragon.weekcalendar.model.User;

import java.util.ArrayList;

public class ViewFolderFragment extends Fragment {
    private static View view;
    private TextView txt_view_folder_title, txt_coin_number_note;
    private ImageView btn_add_new_note;
    private ImageButton ic_back_view_folder;
    private static RecyclerView rcv_list_view_folder;
    private static TruyVanDuLieuSQLite truyVanDuLieuSQLite;
    private static int id_folder; //id_folder được gửi đến để view (list note)
    private MyFolder myFolder; //folder được gửi đến để view (list note)
    private static ArrayList<MyNote> myNoteArrayList; //danh sách note của folder
    private static RewardedAd rewardedAd;
    public ViewFolderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_view_folder, container, false);

        //1. Ánh xạ
        AnhXa();

        //1.2 Load dữ liệu của folder
        LoadFolder();

        //2. Load rcv all notes of this folder
        LoadRecyclerViewAllNotes();
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL);
        rcv_list_view_folder.addItemDecoration(itemDecoration);

        //3. Click to add new note
        btn_add_new_note.setOnClickListener(v -> {

            //Mở fragment -> Add new note fragment
            NotesViewPagerFragment.setCurrentItemNotes(2);
        });

        //Back
        ic_back_view_folder.setOnClickListener(v -> {
            backToFolderFragment();
        });


        return view;
    }



    //2. Load rcv all notes of this folder
    public static void LoadRecyclerViewAllNotes() {

        //Lấy danh sách note
        myNoteArrayList = truyVanDuLieuSQLite.getAllNoteOfFolder(id_folder);

        //set rcv
        ViewFolderRecyclerViewAdapter viewFolderAdapter = new ViewFolderRecyclerViewAdapter(view.getContext(), myNoteArrayList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        rcv_list_view_folder.setLayoutManager(linearLayoutManager);
        rcv_list_view_folder.setAdapter(viewFolderAdapter);
    }

    //1.2 Load dữ liệu của folder
    private void LoadFolder() {

        //a. Lấy id_folder gửi đến (Đã lưu vào Shared)
        id_folder = UserUltils.getId_folder(view.getContext());

        //b. Lấy thông tin của folder ở SQLite
        myFolder = truyVanDuLieuSQLite.getOneFolder(id_folder);

        //c. Kiểm tra (có dữ liệu cụ thể là chắc chắn nhất, vì id_folder có giá trị default) trước khi load (Nếu không sẽ bị lỗi không load trước khi mở được ViewPager fragment đứng trước nó)
        if(myFolder.getFolder_name() != null) {
            //c. Set thông tin cho textView (Giới hạn độ dài)
            if (myFolder.getFolder_name().length() > 28) {
                txt_view_folder_title.setText(myFolder.getFolder_name().substring(0, 28) + " ...");
            } else {
                txt_view_folder_title.setText(myFolder.getFolder_name());
            }
        }
    }

    //1.1 Ánh xạ
    private void AnhXa() {
        txt_view_folder_title = view.findViewById(R.id.txt_view_folder_fragment_title);
        btn_add_new_note = view.findViewById(R.id.btn_add_new_note);
        rcv_list_view_folder = view.findViewById(R.id.rcv_list_view_folder);
        ic_back_view_folder = view.findViewById(R.id.ic_back_from_fragment_view_folder);

        myNoteArrayList = new ArrayList<>();

        truyVanDuLieuSQLite = new TruyVanDuLieuSQLite(view.getContext());

        txt_coin_number_note = view.findViewById(R.id.txt_coin_number_note);
    }

    //A. Back fragment -> Trở về ViewPager 1 (notes)
    public void backToFolderFragment() {
        //Chuyển ViewPager đến ViewFolder
        NotesViewPagerFragment.setCurrentItemNotes(0);
    }

    //B. Load on Resume
    @Override
    public void onResume() {
        super.onResume();

        //1. Load lại dữ liệu folder mỗi lần vào fragment
        LoadFolder();
        //2. Sau đó mới Load hiển thị list các note
        LoadRecyclerViewAllNotes();

    }
}