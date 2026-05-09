package com.mvdragon.weekcalendar.notes;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mvdragon.weekcalendar.R;
import com.mvdragon.weekcalendar.database.TruyVanDuLieuSQLite;
import com.mvdragon.weekcalendar.database.UserUltils;
import com.mvdragon.weekcalendar.model.ImageNote;
import com.mvdragon.weekcalendar.model.MyNote;
import com.mvdragon.weekcalendar.model.MyNoteAvatar;
import com.mvdragon.weekcalendar.notes.imagenote.ImageNoteAdapter;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class UpdateNoteViewAllFragment extends Fragment implements EasyPermissions.PermissionCallbacks{
    private static View view;
    private static TextView txt_title_note_activity;
    private static EditText edt_new_note_name;
    private static EditText edt_content_new_note;
    private ImageButton ibtn_clear_content_new_note, ibtn_copy_note_content, ibtn_check_content_new_note, ic_back_activity_update_note;
    private RecyclerView rcv_image_of_note;
    private ProgressBar pb_list_image_note_update;
    private ImageView img_delete_all_image_new_note;
    private static ImageView img_check_add_image_new_note;
    private static ImageView img_view_avatar_new_note;
    private RelativeLayout layout_icon_control_image_new_note;
    private static Button btn_save_new_note;
    private static Button btn_cancel_update_note;
    private static boolean hasAvatar;
    private static TruyVanDuLieuSQLite truyVanDuLieuSQLite;
    public static MyNote myNoteShared;
    private boolean showIconDeleteOneImage; //setup trạng thái cho icon delete
    public static ArrayList<ImageNote> imageNoteArrayList; //list ảnh duy nhất của một note
    private ArrayList<Uri> uriList;
    private final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 100;
    private String requestPermissionSDK33 = Manifest.permission.READ_MEDIA_IMAGES;
    private int REQUEST_CODE_READ_MEDIA_IMAGES_SDK33 = 200;
    private int INTENT_CODE_READ_IMAGES_SDK33 = 300;
    private int INTENT_CODE_PICK_IMAGE_AVATAR_SDK33 = 400;
    //Xử lý chọn 1 image (chọn avatar)
    private boolean isClickSelectAvatar, isClickSelectImageList;

    public UpdateNoteViewAllFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_update_note_view_all, container, false);

        //1. Ánh xạ
        AnhXa();

        //2. Thêm avatar: Hỏi quyền truy cập folder ảnh và thực hiện theo code
        img_view_avatar_new_note.setOnClickListener(v -> {

            //A. Đặt trạng thái 2 boolean chọn ảnh
            isClickSelectAvatar = true;
            isClickSelectImageList = false;

            //B. Ẩn các layout control khác
            HiddenLayoutControlNoteEditText();
            HiddenLayoutControlImageList();
            edt_new_note_name.clearFocus();
            edt_content_new_note.clearFocus();

            //C. Hàm xin quyền truy cập -> Sau đó mở màn hình chọn ảnh
            //Nếu version 33 trở lên (tên là TIRAMISU) phải hỏi quyền truy cập ảnh riêng READ_MEDIA_IMAGES (không truy cập cả bộ nhớ chung như Version trước)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionSDK33();
            } else {
                requestPermissions();
            }

        });

        //3. Load dữ liệu của note (nếu được gửi lên để view và update)
        loadMyNoteShared();
        loadNoteData();

        //4. Lưu notes (note mới hoặc update note)
        btn_save_new_note.setOnClickListener(v -> {
            updateNote();
        });

        //5. Load icon and focus editText
        LoadIconAndFocusEditText();

        //6. back
        ic_back_activity_update_note.setOnClickListener(v -> {
            backUpdateNote();
        });

        btn_cancel_update_note.setOnClickListener(v -> {
            backUpdateNote();
        });



        return view;
    }



    //8. Load icon and focus editText
    private void LoadIconAndFocusEditText() {

        //1. Mới vào thì ẩn các icon control
        HiddenLayoutControlNoteEditText();
        HiddenLayoutControlImageList();

        //2. Nếu edt name có focus -> Hiện bàn phím (Tự động) | Ẩn các icon control
        edt_new_note_name.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                HiddenLayoutControlImageList();
                HiddenLayoutControlNoteEditText();
            } else {
                HiddenKeyboard();
            }
        });

        //3. Nếu edt note có focus -> Hiện bàn phím (Tự động), hiện icon control của note | Ẩn icon control của image
        edt_content_new_note.setOnFocusChangeListener((v, hasFocus) -> {

            if(hasFocus){
                ShowLayoutControlNoteEditText();
                HiddenLayoutControlImageList();

            } else {
                HiddenLayoutControlNoteEditText();
                HiddenKeyboard();
            }

        });

        //4. Set tính năng cụ thể cho icon control của edt note
        ibtn_clear_content_new_note.setOnClickListener(v -> {
            //clear text trong editText
            edt_content_new_note.getText().clear();
        });
        ibtn_check_content_new_note.setOnClickListener(v -> {
            //out focus -> sẽ kích hoạt hàm ẩn icon control và ẩn bàn phím của setOnFocusChangeListener
            edt_content_new_note.clearFocus();
        });
        ibtn_copy_note_content.setOnClickListener(v -> {
            String inputText = edt_content_new_note.getText().toString().trim();

            //Kiểm tra đầu vào vì nếu empty thì copy cũng không paste được
            if(TextUtils.isEmpty(inputText)){
                Toast.makeText(view.getContext(), R.string.please_type_text, Toast.LENGTH_SHORT).show();

            } else {
                ClipboardManager clipboardManager = (ClipboardManager) view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("MyCopyData", inputText);
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(view.getContext(), R.string.copied, Toast.LENGTH_SHORT).show();
            }
        });

        //5.1 Set tính năng cụ thể cho icon control của image (icon check, save)
        img_check_add_image_new_note.setOnClickListener(v -> {

            showIconDeleteOneImage = false;
            HiddenLayoutControlImageList();

            //Xoá toàn bộ ảnh cũ và thay bằng ảnh mới
            int id_note = myNoteShared.getId_note();
            if(id_note > 0) {
                truyVanDuLieuSQLite.deleteAllImageOneNote(id_note);
                truyVanDuLieuSQLite.insertListImageNote(imageNoteArrayList, id_note);
            }

            //Lấy lại list ảnh từ CSDL và load lại rcv
            imageNoteArrayList = truyVanDuLieuSQLite.getListImageNote(id_note);
            loadRecyclerViewNoteImage(imageNoteArrayList);
        });

        //5.2 Set tính năng cụ thể cho icon control của image (icon clear list image)
        img_delete_all_image_new_note.setOnClickListener(v -> {
            //Xoá hết ảnh trong list và load lại rcv
            deleteAllImageNoteDialog(Gravity.CENTER);
        });
    }

    //7.1 Show Keyboard
    public void ShowKeyboard(){
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }

    //7.2 HiddenKeyboard
    public void HiddenKeyboard(){
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    //7.3 Show layout icon editText of note
    public void ShowLayoutControlNoteEditText(){
        ibtn_clear_content_new_note.setVisibility(View.VISIBLE);
        ibtn_copy_note_content.setVisibility(View.VISIBLE);
        ibtn_check_content_new_note.setVisibility(View.VISIBLE);
    }

    //7.4 Hidden layout icon editText of note
    public void HiddenLayoutControlNoteEditText(){
        ibtn_clear_content_new_note.setVisibility(View.GONE);
        ibtn_copy_note_content.setVisibility(View.GONE);
        ibtn_check_content_new_note.setVisibility(View.GONE);
    }

    //7.5 Show layout control image list
    public void ShowLayoutControlImageList(){
        layout_icon_control_image_new_note.setVisibility(View.VISIBLE);
    }

    //7.6 Hidden layout control image list
    public void HiddenLayoutControlImageList(){
        layout_icon_control_image_new_note.setVisibility(View.GONE);
    }

    //7.7 Dialog hỏi xoá list ImageNote của một note
    private void deleteAllImageNoteDialog(int gravity) {

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
        TextView txt_title_delete_all_image_note = dialog.findViewById(R.id.textView_title_delete_all_event);
        Button btn_yes_delete = dialog.findViewById(R.id.btn_yes_delete);
        Button btn_no_delete = dialog.findViewById(R.id.btn_no_delete);

        //set title
        txt_title_delete_all_image_note.setText(R.string.delete_all_imageNote);

        //5. Các hàm thực hiện trong dialog
        btn_yes_delete.setOnClickListener(v -> {
            //Thực hiện xoá clear list ảnh đang hiển thị (chưa xoá trong CSDL)
            imageNoteArrayList.clear();
            //Load lại rcv ImageNote
            loadRecyclerViewNoteImage(imageNoteArrayList);
            dialog.dismiss();
        });

        btn_no_delete.setOnClickListener(v -> {
            dialog.cancel();
        });

        //12. show
        dialog.show();

    }


    //6. Update note (Khi đã có dữ liệu gửi lên)
    private void updateNote() {
        //1. Lấy dữ liệu id_note được gửi lên (nếu có)
        int id_note = myNoteShared.getId_note();

        //2. Kiểm tra id_note sẽ chính xác hơn (vì intent có thể là luôn != null)
        if(id_note > 0) {

            //a. Lấy dữ liệu note gốc để update
            MyNote myNote = truyVanDuLieuSQLite.getOneMyNote(id_note);

            //b. Gán dữ liệu note để update (Hiện tại chỉ 4 thông số: name, notes, create_datetime (trong hàm update SQLite), avatar (bảng riêng) | Còn id_note, id_folder, status, role giữ nguyên)
            //Name, kiểm tra nếu tên bị rỗng
            if(TextUtils.isEmpty(edt_new_note_name.getText())){
                Toast.makeText(view.getContext(), R.string.type_name_please, Toast.LENGTH_SHORT).show();
                return;
            }

            myNote.setNote_name(edt_new_note_name.getText().toString().trim());
            //Gán notes (ghi chép của note)
            myNote.setNote_content(edt_content_new_note.getText().toString().trim());

            //c. Thực hiện update dữ liệu MyNote (Vì update là đã có id_note nên có thể tách riêng so với tạo mới thì phải gộp chung) | Hiện tại không để chức năng xoá avatar trong activity này nên không cần tính đến xoá avatar cũ trong CSDL
            truyVanDuLieuSQLite.updateOneNote(myNote);

            //d. Update MyNote theo tình trạng avatar: Nếu có theo 2 trường hợp avatar mới và update avatar cũ (Khi load dữ liệu và khi bấm select ảnh đã xác định được hasAvatar)
            if(hasAvatar){

                //Lấy ảnh từ ImageView (Đã điều chỉnh kích thước) -> bitmap -> Array
                BitmapDrawable bitmapDrawable = (BitmapDrawable) img_view_avatar_new_note.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArrayAvatar = stream.toByteArray();

                //Tạo khuôn avatar
                MyNoteAvatar myNoteAvatar = new MyNoteAvatar();

                //Check xem đã có ảnh avatar cũ chưa -> update
                boolean checkAvatar = truyVanDuLieuSQLite.checkMyNoteAvatar(id_note);

                if(checkAvatar){

                    myNoteAvatar = truyVanDuLieuSQLite.getMyNoteAvatar(myNote.getId_note());
                    //Cập nhật mình note_avatar cho model (Phải kiểm tra null, nếu không có thể bị lỗi ảnh)
                    if(byteArrayAvatar != null){
                        myNoteAvatar.setNote_avatar(byteArrayAvatar);
                    }

                    //update avatar cũ trong CSDL
                    truyVanDuLieuSQLite.updateMyNoteAvatar(myNoteAvatar);

                //Nếu không có avatar cũ thì tạo avatar mới (không tạo id avatar ở đây)
                } else {

                    myNoteAvatar.setId_note(id_note);

                    if(byteArrayAvatar != null) {
                        myNoteAvatar.setNote_avatar(byteArrayAvatar);
                    }
                    myNoteAvatar.setStatus(1);

                    //Tạo avatar mới trong CSDL
                    truyVanDuLieuSQLite.insertMyNoteAvatar(myNoteAvatar);
                }

            } else {
                img_view_avatar_new_note.setImageResource(R.drawable.ic_add_photo_4);
            }

            //e. Xoá toàn bộ ảnh cũ của note và thay bằng list ảnh mới (có thể chứa ảnh hoặc không)
            //Xoá
            truyVanDuLieuSQLite.deleteAllImageOneNote(myNote.getId_note());
            //Thêm ảnh mới
            truyVanDuLieuSQLite.insertListImageNote(imageNoteArrayList, id_note);

            //f. Thông báo
//            Toast.makeText(view.getContext(), getResources().getText(R.string.saved), Toast.LENGTH_SHORT).show();

        }

        //3. Back về activity trước sau khi thực hiện xong
        backUpdateNote();
    }

    //5.1.1 Hàm mở màn hình chọn ảnh (Kết quả cũng trong hàm onActivityResult cùng với tính năng nhận kết quả ảnh avatar)
    private void imagePickerList(){

        //id của theme (có thể đổi theme tự tạo)
        int theme = R.style.CustomTheme;

        //Làm trống list ảnh uri trước khi chọn
        uriList.clear();

        //Open picker
        FilePickerBuilder.getInstance()
                .setActivityTitle("Select Image")
                .setSpan(FilePickerConst.SPAN_TYPE.FOLDER_SPAN, 3) //Số cột (album) sẽ sắp xếp hiển thị, nên dùng 3
                .setSpan(FilePickerConst.SPAN_TYPE.DETAIL_SPAN, 3) //Số cột (ảnh) sẽ sắp xếp hiển thị, (nên dùng 3 hoặc 4) | Số lượng cột ảnh nên ít hơn cột album (Để kích thước album hiện to hơn list ảnh sẽ chọn)
                .setMaxCount(20) //Số lượng ảnh max được select (nếu không cài thì chỉ hiện số đếm)
                .setSelectedFiles(uriList) //Nơi chứa list (uri) ảnh đã chọn
                .setActivityTheme(theme) //setup style trong theme
                .enableCameraSupport(false) // Có hiện và dùng camera hay không
                .enableImagePicker(true) // Cho phép chọn ảnh
                .showFolderView(true) //Có hiện chọn thư mục hay không (Nếu không sẽ show tất cả ảnh)
//                .setImageSizeLimit(2200) ////Giới hạn kích thước hình ảnh có thể tìm thấy
                .pickPhoto(this);
    }

    //5.1.2 pick image SDK 33
    private void imagePickerSDK33(){

        //Làm trống list ảnh uri trước khi chọn
        uriList.clear();

        //Hàm chọn ảnh cho SDK > 19 (đến cả 34)
        Intent intent = new Intent();
        intent.setType("image/*");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }

        //Run để nhận kết quả chọn ảnh về
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), INTENT_CODE_READ_IMAGES_SDK33);
    }

    //5.1.3 pick avatar SDK 33
    private void imagePickerAvatarSDK33(){

        //Hàm chọn ảnh cho SDK > 19 (đến cả 34)
        Intent intent = new Intent();
        intent.setType("image/*");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            intent.putExtra(Intent.ACTION_PICK, true);
        }

        //Run để nhận kết quả chọn ảnh về
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), INTENT_CODE_PICK_IMAGE_AVATAR_SDK33);
    }

    //5.2 Hàm mở màn hình chọn avatar (Kết quả cũng trong hàm onActivityResult cùng với tính năng nhận kết quả ảnh chọn list)
    private void imagePickerAvatar(){

        //id của theme (có thể đổi theme tự tạo)
        int theme = R.style.CustomTheme;

        //Làm trống list ảnh uri trước khi chọn
        uriList.clear();

        //Open picker
        FilePickerBuilder.getInstance()
                .setActivityTitle("Select Image")
                .setSpan(FilePickerConst.SPAN_TYPE.FOLDER_SPAN, 3) //Số cột (album) sẽ sắp xếp hiển thị, nên dùng 3
                .setSpan(FilePickerConst.SPAN_TYPE.DETAIL_SPAN, 3) //Số cột (ảnh) sẽ sắp xếp hiển thị, (nên dùng 3 hoặc 4) | Số lượng cột ảnh nên ít hơn cột album (Để kích thước album hiện to hơn list ảnh sẽ chọn)
                .setMaxCount(1) //Số lượng ảnh max được select (nếu không cài thì chỉ hiện số đếm)
                .setSelectedFiles(uriList) //Nơi chứa list (uri) ảnh đã chọn
                .setActivityTheme(theme) //setup style trong theme
                .enableCameraSupport(false) // Có hiện và dùng camera hay không
                .enableImagePicker(true) // Cho phép chọn ảnh
                .showFolderView(true) //Có hiện chọn thư mục hay không (Nếu không sẽ show tất cả ảnh)
//                .setImageSizeLimit(2200) ////Giới hạn kích thước hình ảnh có thể tìm thấy
                .pickPhoto(this);
    }

    //4.1.1 Hàm xin quyền truy cập (Tự tạo) đọc ảnh | Nếu cấp quyền -> Thực hiện hàm chọn ảnh imagePicker()
    private void requestPermissions() {

        //Mảng quyền truy cập
        String [] stringPermission = {Manifest.permission.READ_EXTERNAL_STORAGE}; //Manifest.permission.CAMERA

        //Nếu đã cấp quyền -> Thực hiện hàm chọn ảnh
        if(EasyPermissions.hasPermissions(view.getContext(), stringPermission)){

            //Thực hiện hàm theo trạng thái chọn ảnh
            if(isClickSelectAvatar){
                imagePickerAvatar();
            }

            if(isClickSelectImageList) {
                imagePickerList();
            }
        }

        //Nếu chưa cấp quyền -> Thông báo và hỏi lại quyền, lưu kết quả theo CODE
        else {
            EasyPermissions.requestPermissions(this, getString(R.string.app_needs_connect), REQUEST_CODE_READ_EXTERNAL_STORAGE, stringPermission);
        }
    }

    //4.1.2 Hỏi quyền truy cập và thực hiện hàm đi kèm (SDK >= 33)
    private void requestPermissionSDK33() {

        //Nếu đã có quyền truy cập
        if(ActivityCompat.checkSelfPermission(view.getContext(), requestPermissionSDK33) == PackageManager.PERMISSION_GRANTED){

            //Thực hiện hàm theo trạng thái chọn ảnh
            if(isClickSelectAvatar){
                imagePickerAvatarSDK33();
            }

            if(isClickSelectImageList) {
                imagePickerSDK33();
            }

            //Thông báo và hỏi lại lần 2 quyền truy cập
        } else if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) view.getContext(), requestPermissionSDK33)) {

            //Cửa sổ thông báo
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(view.getContext());
            builder.setMessage(R.string.permission_to_read_images_content)
                    .setTitle(R.string.permission_to_read_images)
                    .setCancelable(false)
                    .setPositiveButton(R.string.show_request_permission, (dialog, which) -> {
                        ActivityCompat.requestPermissions((Activity) view.getContext(), new String[]{requestPermissionSDK33}, REQUEST_CODE_READ_MEDIA_IMAGES_SDK33);
                        dialog.dismiss();
                    })
                    .setNegativeButton(R.string.cancel_request_permission, ((dialog, which) -> dialog.dismiss()));

            //Hiển thị
            builder.show();

        } else {
            ActivityCompat.requestPermissions((Activity) view.getContext(), new String[]{requestPermissionSDK33}, REQUEST_CODE_READ_MEDIA_IMAGES_SDK33);
        }
    }

    //4.2 Nhận kết quả trả lời quyền truy cập
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //a. Xác nhận quyền (dùng cho SDK <=32)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);

        //b. SDK33
        } else {
            if (requestCode == REQUEST_CODE_READ_MEDIA_IMAGES_SDK33) {

                //Thực hiện hàm nếu cấp quyền
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Thực hiện hàm mở ảnh để chọn
                    if(isClickSelectAvatar){
                        imagePickerAvatarSDK33();
                    }
                    if(isClickSelectImageList){
                        imagePickerSDK33();
                    }

                    //Thông báo và hướng dẫn nếu đã từ chối
                } else if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) view.getContext(), requestPermissionSDK33)) {

                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(view.getContext());
                    builder.setMessage(R.string.permission_to_read_images_content)
                            .setTitle(R.string.permission_to_read_images)
                            .setCancelable(false)
                            .setNegativeButton(R.string.cancel_request_permission, ((dialog, which) -> dialog.dismiss()))
                            .setPositiveButton(R.string.setting_request_permission, (dialog, which) -> {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", view.getContext().getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);

                                dialog.dismiss();
                            });
                    builder.show();

                } else {
                    //Hỏi lại từ đầu quyền truy cập
                    requestPermissionSDK33();
                }
            }
        }
    }

    //4.3 Hàm implement 1: Khi chấp nhận quyền truy cập máy (2 quyền read and write) | (Hàm này bổ sung cho phần hỏi lại quyền)
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

        //A. Hàm của SDK <= 32
        if(requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE){

            //Gọi hàm mở chọn ảnh theo trạng thái chọn avatar hoặc chọn list
            if(isClickSelectAvatar){
                imagePickerAvatar();
            }

            if(isClickSelectImageList) {
                imagePickerList();
            }
        }

        //B. Hàm của SDK 19 -> 34 ...
        if(requestCode == REQUEST_CODE_READ_MEDIA_IMAGES_SDK33){

            //Gọi hàm mở chọn ảnh theo trạng thái chọn avatar hoặc chọn list
            if(isClickSelectAvatar){
                imagePickerAvatarSDK33();
            }

            if(isClickSelectImageList) {
                imagePickerSDK33();
            }
        }
    }
    //4.4 Hàm implement 2: Khi không chấp nhận quyền truy cập lần 2 (hàm chọn ảnh của ninja)
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        //Khi bị từ chối nhiều lần
        if(EasyPermissions.somePermissionDenied(this)){
            //Open app setting
            new AppSettingsDialog.Builder(this).build().show();
        }
        //Khi bị từ chối 1 lần
        else {
            Toast.makeText(view.getContext(), "Permission denied!", Toast.LENGTH_SHORT).show();
        }
    }

    //3.1.1 load myNoteShared
    public static void loadMyNoteShared(){
        myNoteShared = UserUltils.getNoteShared(view.getContext());
    }

    //3.1.2 Load dữ liệu note được gửi lên nếu có (để view và update)
    public static void loadNoteData() {
        //A. Lấy dữ liệu id_note được gửi lên
        int id_note = myNoteShared.getId_note();

        //B. Kiểm tra id_note hoặc dữ liệu cụ thể (vì intent có thể là luôn != null)
        if(id_note > 0) {

            //1. Lấy dữ liệu note
            MyNote myNote = truyVanDuLieuSQLite.getOneMyNote(id_note);

            //2. Gán dữ liệu note cho activity
            edt_new_note_name.setText(myNote.getNote_name());

            //2.1 Check xem đã có ảnh avatar chưa -> Lấy ảnh -> Gán avatar vào img hiển thị
            boolean checkAvatar = truyVanDuLieuSQLite.checkMyNoteAvatar(id_note);

            if(checkAvatar){
                MyNoteAvatar myNoteAvatar = truyVanDuLieuSQLite.getMyNoteAvatar(myNote.getId_note());
                Bitmap bitmap = BitmapFactory.decodeByteArray(myNoteAvatar.getNote_avatar(), 0, myNoteAvatar.getNote_avatar().length);
                img_view_avatar_new_note.setImageBitmap(bitmap);

                //Báo có avatar
                hasAvatar = true;

            } else {
                //Nếu không có avatar thì phải báo false (nếu không sẽ bị lỗi ảnh avatar)
                hasAvatar = false;
                img_view_avatar_new_note.setImageResource(R.drawable.ic_add_photo_4);
            }

            //2.2 gán note (ghi chép)
            edt_content_new_note.setText(myNote.getNote_content());

            //2.3 Lấy danh sách ảnh của note
            imageNoteArrayList = truyVanDuLieuSQLite.getListImageNote(id_note);

            //2.4 đổi hình icon từ check -> save của icon control image
            img_check_add_image_new_note.setImageResource(R.drawable.ic_save_update_event_1);

            //2.5 set title, text, text button
            txt_title_note_activity.setText(R.string.update_note);
            btn_cancel_update_note.setText(R.string.back);
            btn_save_new_note.setText(R.string.btn_update_note);

        }

        //3. Load rcv ảnh (dù có dữ liệu gửi lên hay chưa) | Hiện tại đang load kiểu postdelay theo số lượng (Ở onResume) nên không load sẵn nữa
//        loadRecyclerViewNoteImage(imageNoteArrayList);

    }

    //3.2 Load rcv ảnh
    public void loadRecyclerViewNoteImage(ArrayList<ImageNote> imageNoteArrayList){

        //1. Load rcv ảnh của note
        ImageNoteAdapter imageNoteAdapter = new ImageNoteAdapter(view.getContext(), imageNoteArrayList, showIconDeleteOneImage, (ImageNoteAdapter.IClickAddImageIcon) () -> {
            //Click button add image

            //Hiện button delete 1 ảnh, show layout control image
            showIconDeleteOneImage = true;
            ShowLayoutControlImageList();

            //Trạng thái 2 nút chọn ảnh
            isClickSelectAvatar = false;
            isClickSelectImageList = true;

            //Nếu version 33 trở lên (tên là TIRAMISU) phải hỏi quyền truy cập ảnh riêng READ_MEDIA_IMAGES (không truy cập cả bộ nhớ chung như Version trước)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionSDK33();
            } else {
                requestPermissions();
            }

            HiddenLayoutControlNoteEditText(); //Ẩn layout control edt của note
            edt_new_note_name.clearFocus(); //Ẩn focus edt (nếu không khi bấm lưu nó sẽ tự động hiện bàn phím)
            edt_content_new_note.clearFocus();

        }, (id_image_note, id_note, position) -> {

            //(Sự kiện khi click vào item của rcv - một ảnh)

            if(!showIconDeleteOneImage){
//                Intent intent = new Intent(view.getContext(), ViewImageNoteActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putInt("id_image_note", id_image_note);
//                bundle.putInt("id_note", id_note);
//                bundle.putInt("position", position);
//
//                intent.putExtra("NoteFromUpdateEventActivity", bundle);
//                startActivity(intent);

            } else {
                if(UserUltils.getUserLocal(requireActivity()).getCountLogin() <= 30) {
                    Toast.makeText(view.getContext(), R.string.save_image_to_view, Toast.LENGTH_SHORT).show();
                }
            }

            HiddenLayoutControlNoteEditText(); //Ẩn layout control edt của note
            edt_new_note_name.clearFocus(); //Ẩn focus edt (nếu không khi bấm lưu nó sẽ tự động hiện bàn phím)
            edt_content_new_note.clearFocus();

        });

        //2. set cho rcv
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
        rcv_image_of_note.setLayoutManager(linearLayoutManager);
        rcv_image_of_note.setAdapter(imageNoteAdapter);

        //3. Đóng pb
        pb_list_image_note_update.setVisibility(View.GONE);
    }

    //2.4 Thêm list ảnh dạng Uri vào ImageNote list: Convert size -> lưu
    private void AddUriListToImageNoteList(ArrayList<Uri> uriList) {

        for(int i=0; i<uriList.size(); i++){

            //1. Tạo 1 ảnh ImageNote (chỉ cần lưu mình ảnh vì những tham số khác sẽ thực hiện trong hàm lưu)
            ImageNote imageNote = new ImageNote();

            //2. Chuyển Uri về bytes array (giảm kích thước trước khi cho vào list lưu cuối)
            try {
                //A. Lấy ảnh uri -> bimap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(view.getContext().getContentResolver(), uriList.get(i));

                //B. Thực hiện giảm kích thước ảnh (getResizedBitmap() hàm tự tạo). Kích thước tối đa cho phép 1500 (Theo sử dụng cho thấy đây là kích thước mà có thể lưu mà không bị lỗi và vừa tầm sử dụng)
                Bitmap resizedImage = getResizedBitmap(bitmap, 1500);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                resizedImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

                //Chuyển về bytes []
                byte [] imageBytes = outputStream.toByteArray();

                //C. Set ảnh cho ImageEvent
                imageNote.setNote_image(imageBytes);

                //D. Clear bitmap sau khi đã dùng xong
                resizedImage.recycle();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //Thêm vào ImageNote list
            imageNoteArrayList.add(imageNote);
        }
    }

    //2.3 Đưa ảnh về một kích thước lớn nhất có thể cho trước
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {

        //1. Lấy kích thước gốc của ảnh bitmap
        int width = image.getWidth();
        int height = image.getHeight();

        //2. Lấy tỷ lệ ảnh gốc của chiều rộng / chiều cao
        float bitmapRatio = (float) width / (float) height;

        //3. Nếu chiều rộng > chiều cao (tỷ lệ >1) -> Chuyển chiều RỘNG về kích thước cho trước => suy ra chiều CAO theo tỷ lệ
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);

            //4. Nếu chiều rộng < chiều cao (tỷ lệ < 1) -> Chuyển chiều CAO về kích thước cho trước => suy ra chiều RỘNG theo tỷ lệ
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        //5. Trả về ảnh đã đưa về kích thước như mong muốn
        return Bitmap.createScaledBitmap(image, width, height, false);
    }


    //2.2 Nhận kết quả trả về của: intent chọn ảnh avatar (intent implicit folder) và list pick image (Theo 2 trường hợp SDK <33 và trường hợp SDK >=33)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Nhận kết quả trả ảnh về theo avatar hoặc image list
        if(resultCode == RESULT_OK && data != null){

            //A.1 Trường hợp chọn list image (đã setup phân biệt riêng khi click vào button). REQUEST_CODE_PHOTO: Code có sẵn để nhận ảnh | Trường hợp dùng picker image của ninja
            if(requestCode == FilePickerConst.REQUEST_CODE_PHOTO && isClickSelectImageList){
                //a. Lấy list ảnh (uri) đã chọn
                uriList = data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA);
                //b. Thêm vào list imageNoteArrayList (hàm tự tạo)
                AddUriListToImageNoteList(uriList);
                //c. Hiển thị layout control image và tín hiệu show nút delete
                showIconDeleteOneImage = true;

                //d. Load lại adapter, rcv (Tự động trong onResume)
            }

            //A.2 Nếu dùng picker có sẵn của SDK > 19
            if(requestCode == INTENT_CODE_READ_IMAGES_SDK33){

                //1. Thêm dữ liệu vào URI list
                if(data.getClipData() != null){
                    for(int i=0; i< data.getClipData().getItemCount(); i++){
                        uriList.add(data.getClipData().getItemAt(i).getUri());
                    }
                }

                //2. Thêm list Uri vào list ImageNote
                AddUriListToImageNoteList(uriList);

                //3. Hiển thị tín hiệu show nút delete và layout control image và
                showIconDeleteOneImage = true;
                ShowLayoutControlImageList();

                //4. Load lại adapter, rcv (Tự động trong onResume)

            }

            //B.1 Trường hợp chọn avatar SDK <33 kiểu ninja (đã setup phân biệt riêng khi click vào button)
            if(requestCode == FilePickerConst.REQUEST_CODE_PHOTO && isClickSelectAvatar){

                //1. Lấy list ảnh (uri) đã chọn (list uri này chỉ cho chọn 1 ảnh làm avatar)
                uriList = data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA);

                //2. Lấy ảnh uri -> bimap
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(view.getContext().getContentResolver(), uriList.get(0));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                //3. Thực hiện giảm kích thước ảnh (getResizedBitmap() hàm tự tạo). Kích thước tối đa cho phép 1500 (Theo sử dụng cho thấy đây là kích thước mà có thể lưu mà không bị lỗi và vừa tầm sử dụng)
                Bitmap resizedImage = getResizedBitmap(bitmap, 500);

                //4. Set ảnh hiển thị của avatar (làm nơi chứa ảnh luôn)
                img_view_avatar_new_note.setImageBitmap(resizedImage);

                //5. Nếu có ảnh thì xác định là sẽ có avatar, không ảnh là false (phải xác điịnh rõ, tránh ảnh hưởng liên quan thao tác khác)
                if(resizedImage != null){
                    hasAvatar = true;
                } else {
                    hasAvatar = false;
                }

            }

            //B.2 Trường hợp chọn avatar SDK >= 33
            if(requestCode == INTENT_CODE_PICK_IMAGE_AVATAR_SDK33 && isClickSelectAvatar){

                //1. Lấy đường dẫn URI (nơi chứa tài nguyên)
                Uri uri = data.getData();

                //2. Tạo luồng đọc dữ liệu, đọc uri -> Lấy hình ảnh -> Gán hình ảnh cho ImageView
                Bitmap bitmap = null;

                try {
                    InputStream inputStream = view.getContext().getContentResolver().openInputStream(uri);
                    bitmap = BitmapFactory.decodeStream(inputStream);

                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }

                //3. Thực hiện giảm kích thước ảnh (getResizedBitmap() hàm tự tạo). Kích thước tối đa cho phép 1500 (Theo sử dụng cho thấy đây là kích thước mà có thể lưu mà không bị lỗi và vừa tầm sử dụng)
                Bitmap resizedImage = getResizedBitmap(bitmap, 500);

                //4. Set ảnh hiển thị của avatar (làm nơi chứa ảnh luôn)
                img_view_avatar_new_note.setImageBitmap(resizedImage);

                //5. Nếu có ảnh thì xác định là sẽ có avatar, không là false
                if(resizedImage != null){
                    hasAvatar = true;
                } else {
                    hasAvatar = false;
                }

            }
        }
    }

    //1. Ánh xạ
    private void AnhXa() {
        txt_title_note_activity = view.findViewById(R.id.txt_title_note_activity);
        edt_new_note_name = view.findViewById(R.id.edt_new_note_name);
        img_view_avatar_new_note = view.findViewById(R.id.img_view_avatar_new_note);
        edt_content_new_note = view.findViewById(R.id.edt_content_new_note);

        ibtn_clear_content_new_note = view.findViewById(R.id.ibtn_clear_content_new_note);
        ibtn_copy_note_content = view.findViewById(R.id.ibtn_copy_note_content);
        ibtn_check_content_new_note = view.findViewById(R.id.ibtn_check_content_new_note);
        ic_back_activity_update_note = view.findViewById(R.id.ic_back_new_note);

        rcv_image_of_note = view.findViewById(R.id.rcv_image_new_note);
        img_delete_all_image_new_note = view.findViewById(R.id.img_delete_all_image_new_note);
        img_check_add_image_new_note = view.findViewById(R.id.img_check_add_image_new_note);
        layout_icon_control_image_new_note = view.findViewById(R.id.layout_icon_control_image_new_note);
        pb_list_image_note_update = view.findViewById(R.id.pb_list_image_note_update);

        btn_save_new_note = view.findViewById(R.id.btn_save_new_note);
        btn_cancel_update_note = view.findViewById(R.id.btn_cancel_new_note);

        truyVanDuLieuSQLite =  new TruyVanDuLieuSQLite(view.getContext());

        imageNoteArrayList = new ArrayList<>();
        uriList = new ArrayList<>();

        //avatar ban đầu xác định là không có (nếu có select ảnh thì sẽ chuyển thành true trong hàm select ảnh)
        hasAvatar = false;
        //Trạng thái show icon delete mặc định là false
        showIconDeleteOneImage = false;

        //Khai báo trạng thái ban đầu là đang chưa click vào ảnh nào
        isClickSelectAvatar = false;
        isClickSelectImageList = false;

    }

    //A.1 Clear fragment
    public void cleanFragment(){
        //edit text
        edt_new_note_name.getText().clear();
        edt_content_new_note.getText().clear();

        //Tín hiệu true false
        showIconDeleteOneImage = false;
        isClickSelectAvatar = false;
        isClickSelectImageList = false;

        //avatar và list ảnh
        img_view_avatar_new_note.setImageResource(R.drawable.ic_add_photo_4);
        imageNoteArrayList.clear();
        loadRecyclerViewNoteImage(imageNoteArrayList);

        //Layout control icon
        HiddenLayoutControlNoteEditText();
        HiddenLayoutControlImageList();
    }

    //A.2 Huỷ bỏ tạo note mới
    public void backUpdateNote() {
        //Clean trước khi back
        cleanFragment();

        //back về fragment 0 tương ứng menu của main
        if(UserUltils.getUserLocal(view.getContext()).getFragment_user() == 2) {
            NotesViewPagerFragment.setCurrentItemNotes(1);
        } else if (UserUltils.getUserLocal(view.getContext()).getFragment_user() == 3) {
            ViewAllNoteViewPagerFragment.setCurrentItemAllNoteViewPager(0);
        }
    }

    //B. Load lại mỗi lần truy cập activity này
    @Override
    public void onResume() {
        super.onResume();

        //1. load rcv ảnh của note (Trường hợp view update và tạo note mới)
        //id_note gửi lên
        int id_note = myNoteShared.getId_note();
        int countImage = truyVanDuLieuSQLite.getCountImageOfNote(id_note);

        //Nếu hơn 15 ảnh thì chờ 1s
        if(countImage > 15 && id_note > 0){

            //Hiện pb load
            pb_list_image_note_update.setVisibility(View.VISIBLE);

            //delay load list image
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                loadRecyclerViewNoteImage(imageNoteArrayList);
            }, 1000);

        //Nếu <=10 thì load luôn
        } else {
            loadRecyclerViewNoteImage(imageNoteArrayList);
        }
    }

    //C. Delete One Image Of final List: Xoá ảnh trong list chính (Sử dụng bên màn hình ViewImageNoteActivity)
    public static void DeleteOneImageOfFinalList(int position){
        imageNoteArrayList.remove(position);
    }

}