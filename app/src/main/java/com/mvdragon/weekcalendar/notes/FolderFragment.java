package com.mvdragon.weekcalendar.notes;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.mvdragon.weekcalendar.model.User;

import java.util.ArrayList;

public class FolderFragment extends Fragment {
    private static View view;
    private static TextView txt_coin_number_folder;
    private ImageView btn_add_folder;
    private static RecyclerView rcv_list_folder_notes;
    private static TruyVanDuLieuSQLite truyVanDuLieuSQLite;
    private static RewardedAd rewardedAd;

    public FolderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_notes, container, false);

        //1.1 Ánh xạ
        AnhXa();

        //1.2 Nếu chưa có folder nào thì tạo 1 cái khi mới dùng app (CountLogin = 1)
        CreateFolderForNewUser();

        //2. Load rcv danh sách các folder hiện có. Nếu chưa có thư mục nào thì tạo sẵn một thư mục
        LoadRecyclerViewAllFolder();
        //set dòng kẻ mỗi hàng
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL);
        rcv_list_folder_notes.addItemDecoration(itemDecoration);

        //3. Mở dialog tạo tên thư mục mới
        btn_add_folder.setOnClickListener(v -> {
            OpenDialogCreateNewFolder(Gravity.CENTER);
        });


        return view;
    }


    //4. Mở dialog update folder
    private static void OpenDialogUpdateFolder(MyFolder myFolder, int gravity) {

        //1. Tạo dialog (Dùng dialog cách này để tao Window với giao diện đẹp và như ý hơn)
        Dialog dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //Không tiêu đề mặc định
        dialog.setContentView(R.layout.dialog_create_new_folder); // layout của dialog

        //2. Khai báo cửa sổ hiển thị
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
        TextView txt_title_new_folder = dialog.findViewById(R.id.textView_title_new_folder);
        EditText edt_new_event = dialog.findViewById(R.id.edt_new_folder);
        Button btn_ok_new_folder = dialog.findViewById(R.id.btn_ok_new_folder);
        Button btn_cancel_new_folder = dialog.findViewById(R.id.btn_cancel_new_folder);

        //Set thông số sẵn
        txt_title_new_folder.setText(R.string.update_folder_name);
        edt_new_event.setText(myFolder.getFolder_name());

        //Set focus và hiện bàn phím
        edt_new_event.requestFocus();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        //5. Huỷ bỏ dialog
        btn_cancel_new_folder.setOnClickListener(v -> {
            dialog.cancel();
        });

        //6. Update folder mới với tên đã điền
        btn_ok_new_folder.setOnClickListener(v -> {

            //Thông báo nếu chưa có tên
            if(TextUtils.isEmpty(edt_new_event.getText().toString())){
                Toast.makeText(view.getContext(), R.string.notify_create_folder, Toast.LENGTH_SHORT).show();
            } else {

                //Update folder: Tên, avatar chưa sử dụng đến, notes chưa dùng đến, status mặc định là 1, role mặc định là 1 (tuỳ sử dụng sau)
                myFolder.setFolder_name(edt_new_event.getText().toString());

                //Thực hiện lưu vào CSDL
                truyVanDuLieuSQLite.updateFolder(myFolder);
                //load lại rcv
                LoadRecyclerViewAllFolder();

                //Đóng dialog
                dialog.dismiss();
            }
        });

        //12. show
        dialog.show();
    }

    //3. Mở dialog tạo tên thư mục mới
    private void OpenDialogCreateNewFolder(int gravity) {

        //1. Tạo dialog (Dùng dialog cách này để tao Window với giao diện đẹp và như ý hơn)
        Dialog dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //Không tiêu đề mặc định
        dialog.setContentView(R.layout.dialog_create_new_folder); // layout của dialog

        //2. Khai báo cửa sổ hiển thị
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
        EditText edt_new_event = dialog.findViewById(R.id.edt_new_folder);
        Button btn_ok_new_folder = dialog.findViewById(R.id.btn_ok_new_folder);
        Button btn_cancel_new_folder = dialog.findViewById(R.id.btn_cancel_new_folder);

        //5. setup sẵn edt và show bàn phím
        edt_new_event.requestFocus();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        //6. Huỷ bỏ việc tạo folder mới
        btn_cancel_new_folder.setOnClickListener(v -> {
            dialog.cancel();
        });

        //7. Tạo folder mới với tên đã điền
        btn_ok_new_folder.setOnClickListener(v -> {

            //a. Thông báo nếu chưa có tên
            if(TextUtils.isEmpty(edt_new_event.getText().toString())){
                Toast.makeText(view.getContext(), R.string.notify_create_folder, Toast.LENGTH_SHORT).show();
            } else {

                //b. Tạo new folder: Tên, avatar chưa sử dụng đến, notes chưa dùng đến, status mặc định là 1, role mặc định là 1 (tuỳ sử dụng sau)
                MyFolder newFolder = new MyFolder();
                newFolder.setFolder_name(edt_new_event.getText().toString());
                newFolder.setFolder_notes("");
                newFolder.setStatus(1);
                newFolder.setRole(1);

                //c. Thực hiện lưu vào CSDL
                truyVanDuLieuSQLite.createNewFolder(newFolder);
                //d. Load lại rcv
                LoadRecyclerViewAllFolder();

                //f. Đóng dialog
                dialog.dismiss();
            }
        });

        //12. show
        dialog.show();
    }

    //2. Load rcv danh sách các folder hiện có. Nếu chưa có thư mục nào thì tạo sẵn một thư mục
    public static void LoadRecyclerViewAllFolder() {

        //Lấy danh sách folder
        ArrayList<MyFolder> myFolderArrayList = truyVanDuLieuSQLite.getAllFolder();

        //Tạo adapter: context, arrayList, event when click item, event when click layout delete item
        FolderRecyclerViewAdapter folderAdapter = new FolderRecyclerViewAdapter(view.getContext(), myFolderArrayList, myFolder -> {

            //Hàm click text name và time của item -> Lưu id_folder vào Shared (rồi lấy ra ở bên ViewFolder hoặc bất kỳ)
            UserUltils.saveId_folder(view.getContext(), myFolder.getId_folder());
            //Chuyển ViewPager đến ViewFolderFragment
            NotesViewPagerFragment.setCurrentItemNotes(1);

        }, myFolder -> {

            //Sự kện click update folder
            OpenDialogUpdateFolder(myFolder, Gravity.CENTER);
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        rcv_list_folder_notes.setLayoutManager(linearLayoutManager);
        rcv_list_folder_notes.setAdapter(folderAdapter);

    }

    //1.2 Nếu chưa có folder nào thì tạo 1 cái khi mới dùng app (CountLogin = 1)
    private void CreateFolderForNewUser() {

        if(truyVanDuLieuSQLite.getAllFolder().size() == 0 && UserUltils.getUserLocal(view.getContext()).getCountLogin() <= 1){
            //Tạo new folder với 4 dữ liệu: Tên, notes chưa dùng đến, status mặc định là 1, role mặc định là 1 (tuỳ sử dụng sau) | Những dữ liệu khác tạo trong hàm createNewFolder
            MyFolder newFolder1 = new MyFolder();
            newFolder1.setFolder_name("Folder 1");
            newFolder1.setFolder_notes(""); // isEmpty
            newFolder1.setStatus(1);
            newFolder1.setRole(1);

            //Thực hiện lưu vào CSDL
            truyVanDuLieuSQLite.createNewFolder(newFolder1);
        }
    }

    //1.1 Ánh xạ
    private void AnhXa() {

        btn_add_folder = view.findViewById(R.id.btn_add_folder);
        rcv_list_folder_notes = view.findViewById(R.id.rcv_list_folder_notes);
        txt_coin_number_folder = view.findViewById(R.id.txt_coin_number_folder);

        truyVanDuLieuSQLite = new TruyVanDuLieuSQLite(view.getContext());
    }

    @Override
    public void onResume() {
        super.onResume();

        //1. Load rcv các folder
        LoadRecyclerViewAllFolder();

    }
}