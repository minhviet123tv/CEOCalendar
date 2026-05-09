package com.mvdragon.weekcalendar.calendarview.event;

import static android.app.Activity.RESULT_OK;
import static com.mvdragon.weekcalendar.CalendarUtils.daysInMonthFollowWeek;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.ads.AdView;
import com.mvdragon.weekcalendar.CalendarUtils;
import com.mvdragon.weekcalendar.MainActivity;
import com.mvdragon.weekcalendar.R;
import com.mvdragon.weekcalendar.calendarview.CalendarMonthViewAdapter;
import com.mvdragon.weekcalendar.calendarview.CalendarViewPagerFragment;
import com.mvdragon.weekcalendar.calendarview.DayNameMonthDialogRecyclerViewAdapter;
import com.mvdragon.weekcalendar.calendarview.WeekViewFragment;
import com.mvdragon.weekcalendar.database.TruyVanDuLieuSQLite;
import com.mvdragon.weekcalendar.database.UserUltils;
import com.mvdragon.weekcalendar.model.Event;
import com.mvdragon.weekcalendar.model.ImageEvent;
import com.mvdragon.weekcalendar.model.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class AddEventFragment extends Fragment implements EasyPermissions.PermissionCallbacks{
    private View view;
    private EditText edt_event_name, edt_note_new_event;
    private TextView eventDateTV, eventTimeTV;
    private Button btn_save_event, btn_cancel_new_event;
    private LocalTime timeOfEvent = null;
    private TruyVanDuLieuSQLite truyVanDuLieuSQLite;
    private ImageButton ibtn_save_new_event_note, ibtn_copy_new_event_note, ibtn_clear_new_event_note, ic_back_to_calendar;
    private RelativeLayout layout_select_date_of_event, layout_select_time_add_event;
    private AdView adView;

    //Tham số của rcv event image
    private RecyclerView rcv_image_of_event;
    private ImageView img_add_event_delete_image, img_event_add_image_delete_all, img_event_add_image_check;
    private ArrayList<Uri> uriList;
    private ArrayList<ImageEvent> imageEventArrayList;
    private final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 100;
    private String requestPermissionSDK33 = Manifest.permission.READ_MEDIA_IMAGES;
    private int REQUEST_CODE_READ_MEDIA_IMAGES_SDK33 = 200;
    private int INTENT_CODE_READ_IMAGES_SDK33 = 300;
    private RelativeLayout layout_icon_control_image_list;
    private boolean showIconDeleteOneImage; //setup trạng thái cho icon delete

    public AddEventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_event, container, false);

        //1.1 Ánh xạ
        AnhXa();

        //1.3 Cài đặt ẩn hiện nút icon hỗ trợ ghi chép tại ghi chú (notes) của event
        SetupIconOnNotesEvent();

        //2. Set date and time (Tách riêng để sử dụng lại)
        setDate();
        setTime();

        //3. Chọn ngày khi click vào ngày
        eventDateTV.setOnClickListener(v -> {
            selectDateOnMonthView(Gravity.CENTER);
        });
        layout_select_date_of_event.setOnClickListener(v -> {
            selectDateOnMonthView(Gravity.CENTER);
        });

        //4. Chọn giờ khi click vào giờ
        eventTimeTV.setOnClickListener(v -> {
            selectTimeDialog();
        });
        layout_select_time_add_event.setOnClickListener(v -> selectTimeDialog());

        //5. Setup forcus
        SetupFocusEditText();

        //6. Load adapter, rcv ảnh của event khi mới vào (đã load trong onResume)
//        setImageEventAdapter();

        //7. Show or hidden layout control image
        setupLayoutControlImage();

        //8. Save event
        btn_save_event.setOnClickListener(v -> {
            try {
                saveEventAction();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        //9. cancel
        btn_cancel_new_event.setOnClickListener(v -> {
            finishFragmentAddEvent();
        });

        //10. Back
        ic_back_to_calendar.setOnClickListener(v -> {
            finishFragmentAddEvent();
        });



        return view;
    }


    //1.1 Ánh xạ
    private void AnhXa(){
        edt_event_name = view.findViewById(R.id.edt_event_name);
        eventDateTV = view.findViewById(R.id.eventDateTV);
        eventTimeTV = view.findViewById(R.id.eventTimeTV);
        btn_save_event = view.findViewById(R.id.btn_save_event);
        btn_cancel_new_event = view.findViewById(R.id.btn_cancel_new_event);

        edt_note_new_event = view.findViewById(R.id.edt_note_new_event);

        ibtn_save_new_event_note = view.findViewById(R.id.ibtn_save_new_event_note);
        ibtn_copy_new_event_note = view.findViewById(R.id.ibtn_copy_new_event_note);
        ibtn_clear_new_event_note = view.findViewById(R.id.ibtn_clear_new_event_note);
        ic_back_to_calendar = view.findViewById(R.id.ic_back_to_calendar);

        layout_select_date_of_event = view.findViewById(R.id.layout_select_date_of_event);
        layout_select_time_add_event = view.findViewById(R.id.layout_select_time_add_event);

        //Ảnh của event
        rcv_image_of_event = view.findViewById(R.id.rcv_image_add_event);
        uriList = new ArrayList<>();
        imageEventArrayList = new ArrayList<>();

        img_add_event_delete_image = view.findViewById(R.id.imageView_add_event_delete_image);
        img_event_add_image_check = view.findViewById(R.id.imageView_event_add_image_check);
        layout_icon_control_image_list = view.findViewById(R.id.layout_icon_control_image_list);
        img_event_add_image_delete_all = view.findViewById(R.id.imageView_event_add_image_delete_all);

        //SQLite
        truyVanDuLieuSQLite = new TruyVanDuLieuSQLite(view.getContext());

    }

    //1.2 Clean fragment: Làm sạch fragment trước khi tạo mới (Vì bình thường nó là ViewPager nên vẫn lưu dữ liệu trước đó)
    private void cleanFragment(){

        // Đặt lại trạng thái dữ liệu như chưa có gì
        timeOfEvent = null;
        edt_event_name.setText(null);
        edt_note_new_event.setText(null);
        imageEventArrayList.clear();
        setImageEventAdapter();


        //Load lại ngày giờ
        setDate();
        setTime();

    }

    //1.3 Cài đặt ẩn hiện nút icon hỗ trợ ghi chép tại ghi chú (notes) của event
    private void SetupIconOnNotesEvent() {
        //a. Clear edt notes
        ibtn_clear_new_event_note.setOnClickListener(v -> edt_note_new_event.getText().clear());

        //b. Check save note
        ibtn_save_new_event_note.setOnClickListener(v -> {
            //out focus
            edt_note_new_event.clearFocus();

            //hidden icon
            HiddenLayoutEditEventNotes();
        });

        //c. copy nội dung
        ibtn_copy_new_event_note.setOnClickListener(v -> {

            String inputText = edt_note_new_event.getText().toString().trim();

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
    }

    //1.4 Hidden layout edit event notes
    private void HiddenLayoutEditEventNotes(){
        ibtn_clear_new_event_note.setVisibility(View.GONE);
        ibtn_copy_new_event_note.setVisibility(View.GONE);
        ibtn_save_new_event_note.setVisibility(View.GONE);
    }
    //1.5 Show layout edit event notes
    private void ShowLayoutEditEventNotes(){
        ibtn_clear_new_event_note.setVisibility(View.VISIBLE);
        ibtn_copy_new_event_note.setVisibility(View.VISIBLE);
        ibtn_save_new_event_note.setVisibility(View.VISIBLE);
    }

    //2.1 setDate
    private void setDate(){

        eventDateTV.setText(CalendarUtils.monthYearFromDate(CalendarUtils.selectedDate, UserUltils.getUserLocal(view.getContext()).getDateFormat()));
//        Toast.makeText(view.getContext(), "Ngày nhận: " + CalendarUtils.selectedDate, Toast.LENGTH_SHORT).show();
    }

    //2.2 setTime
    private void setTime(){
        String time = " --:--";
        eventTimeTV.setText("Time " + time);
    }

    //4. Hàm tạo cửa sổ chọn giờ
    private void selectTimeDialog(){

        //Tạo ngày tháng sẵn để cài đặt hiển thị mẫu cho cửa sổ
        int hourOfDaySetup = 12;
        int minuteSetup = 0;
        boolean is24HourView = true;

        TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(), android.R.style.Theme_Holo_Light_Dialog, (view, hourOfDay, minute) -> {
            //Lấy thời gian đã chọn ở cửa sổ (định dạng LocalTime)
            timeOfEvent = LocalTime.of(hourOfDay, minute);

            //set cho textView
            eventTimeTV.setText(timeOfEvent.toString());

        }, hourOfDaySetup, minuteSetup, is24HourView );

        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        timePickerDialog.setTitle("Select time"); //Tiêu đề

        timePickerDialog.show(); //Hiển thị cửa sổ
    }

    //5.1 Setup forcus
    private void SetupFocusEditText() {

        //Cài đặt focus của event name
        edt_event_name.setOnFocusChangeListener((v, hasFocus) -> {

            //Nếu out focus -> thì ẩn bàn phím | Hiện admob
            if(!hasFocus){
                HiddenKeyboard();
            } else {
                //Có focust thì ẩn các layout control còn lại
                layout_icon_control_image_list.setVisibility(View.GONE);
                HiddenLayoutEditEventNotes();
            }
        });

        //Cài đặt focus của event notes
        edt_note_new_event.setOnFocusChangeListener((v, hasFocus) -> {

            //Nếu out focus -> Ẩn bàn phím, icon
            if(!hasFocus){
                HiddenKeyboard();

                //hidden icon
                HiddenLayoutEditEventNotes();

                //Có focus edt note -> thì hiện bàn phím, hiện icon (bàn phím tự hiện)
            } else {
                //show icon
                ShowLayoutEditEventNotes();

                //Ẩn layout control image
                layout_icon_control_image_list.setVisibility(View.GONE);
            }
        });
    }

    //5.2 Ẩn bàn phím
    public void HiddenKeyboard(){
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }


    //6.1 Load adapter, rcv ảnh của event luôn để hiển thị list ảnh và show nút cuối add image
    private void setImageEventAdapter() {

//        imageEventArrayList = truyVanDuLieuSQLite.getListImageEvent(1);

        //Khai báo adapter: màn hình, list, hàm thực hiện khi click icon Add (cuối list), Hàm khi click item thông thường - được khai báo trong adapter
        ImageEventAdapter imageEventAdapter = new ImageEventAdapter(view.getContext(), imageEventArrayList, showIconDeleteOneImage, () -> {

            //hàm thực hiện khi click icon Add (cuối list)
            //Hàm xin quyền truy cập -> Sau đó mở màn hình chọn ảnh
            //Nếu version 33 trở lên (tên là TIRAMISU) phải hỏi quyền truy cập ảnh riêng READ_MEDIA_IMAGES (không truy cập cả bộ nhớ chung như Version trước)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionSDK33();
            } else {
                requestPermissions();
            }

            //Ẩn hiện các control đi kèm
            showIconDeleteOneImage = true; //Hiện button delete 1 ảnh
            layout_icon_control_image_list.setVisibility(View.VISIBLE); //show layout control image
            HiddenLayoutEditEventNotes(); //Ẩn layout edit notes
            edt_event_name.clearFocus(); //Clear focus edt (nếu không khi bấm lưu nó sẽ tự động hiện bàn phím)
            edt_note_new_event.clearFocus(); //Clear focus edt (nếu không khi bấm lưu nó sẽ tự động hiện bàn phím)

        }, (id_event, id_image_vent, position) -> {
            //Sự kiện click item (ảnh) -> Chỉ thông báo, không view ảnh
            /* Trong màn hình này chỉ có mình position, vì chưa lưu vào SQLite nên chưa có id */
            //Hiện tại chưa thể mở xem ảnh ở màn hình AddEvent vì chưa có ảnh trong CSDL -> Có thể tạo thêm và truyền list Uri nếu muốn dùng tính năng này (Và có thể là mở màn hình khác để xem list Uri) do không truyền được mảng bytes kích thước lớn
            if(UserUltils.getUserLocal(requireActivity()).getCountLogin() <= 30){
                //Chỉ hiện thông báo cho 30 lần sử dụng đầu tiên
                Toast.makeText(view.getContext(), "Please save event!", Toast.LENGTH_SHORT).show();
            }

            HiddenLayoutEditEventNotes(); //Ẩn layout edit notes
            edt_event_name.clearFocus(); //Clear focus edt (nếu không khi bấm lưu nó sẽ tự động hiện bàn phím)
            edt_note_new_event.clearFocus(); //Clear focus edt (nếu không khi bấm lưu nó sẽ tự động hiện bàn phím)
        });

        //Cài đặt layout, adapter cho rcv
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false); //true: đảo ngược thứ tự (Ảnh cuối list sẽ hiển thị đầu tiên)
        rcv_image_of_event.setLayoutManager(linearLayoutManager);
        rcv_image_of_event.setFocusable(false); //Chưa hiểu, có vẻ không liên quan hay ảnh hưởng rcv
        rcv_image_of_event.setAdapter(imageEventAdapter);

    }

    //6.2.1 Hàm xin quyền truy cập (Tự tạo) đọc ảnh | Nếu cấp quyền -> Thực hiện hàm chọn ảnh
    private void requestPermissions() {

        //a. Mảng quyền truy cập
        String [] stringPermission = {Manifest.permission.READ_EXTERNAL_STORAGE}; //Manifest.permission.WRITE_EXTERNAL_STORAGE

        //c. Nếu đã cấp quyền -> Thực hiện hàm chọn ảnh
        if(EasyPermissions.hasPermissions(view.getContext(), stringPermission)){
            //Thực hiện hàm
            imagePicker();
        }

        //d. Nếu chưa cấp quyền (khi click button) -> Thông báo và (có thể là) hỏi lại quyền vào lần sau trong hàm implement
        else {
            EasyPermissions.requestPermissions(this, getString(R.string.app_needs_connect), REQUEST_CODE_READ_EXTERNAL_STORAGE, stringPermission);
        }
    }

    //6.2.2 Hỏi quyền truy cập và thực hiện hàm đi kèm (SDK >= 33)
    private void requestPermissionSDK33() {

        //Nếu đã có quyền truy cập
        if(ActivityCompat.checkSelfPermission(view.getContext(), requestPermissionSDK33) == PackageManager.PERMISSION_GRANTED){
            //Thực hiện hàm mở ảnh để chọn
            imagePickerSDK33();

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

    //6.3 Nhận kết quả trả lời quyền truy cập
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
                    imagePickerSDK33();

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

    //6.4 Hàm implement 1: Khi chấp nhận quyền truy cập máy (2 quyền) | (Hàm này giống như bổ sung cho phần hỏi lại quyền)
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

        //Hàm của SDK <= 32
        if(requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE){
            //Gọi hàm mở chọn ảnh
            imagePicker();
        }

        //Hàm của SDK 19 -> 34 ...
        if(requestCode == REQUEST_CODE_READ_MEDIA_IMAGES_SDK33){
            imagePickerSDK33();
        }
    }
    //6.5 Hàm implement 2: Khi không chấp nhận quyền truy cập (SDK <33, hàm chọn ảnh của ninja)
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

    //6.6.1 Hàm mở màn hình chọn ảnh
    private void imagePicker() {

        //id của theme (có thể đổi theme 7 ngày tự tạo)
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
                .enableImagePicker(true) // cho phép chọn ảnh
                .showFolderView(true) //Có hiện chọn thư mục hay không (Nếu không sẽ show tất cả ảnh)
//                .setImageSizeLimit(2200) ////Giới hạn kích thước hình ảnh có thể tìm thấy
                .pickPhoto(this);
    }

    //6.6.2 pick image SDK 33
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

    //6.7. Xử lý sau khi nhận kết quả chọn ảnh -> Cài đặt recyclerview
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Nếu có kết quả trả về
        if(resultCode == RESULT_OK && data != null){

            //A. Nếu dùng picker image của ninja
            if(requestCode == FilePickerConst.REQUEST_CODE_PHOTO){

                //a. Lấy list ảnh (uri) đã chọn (Có vẻ như là mới chỉ trong album, còn ảnh mới chụp thì chưa có key lấy)
                uriList = data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA);

                //b. Thêm vào list ImageEvent
                AddUriListToImageEventList(uriList);

                //c. Hiển thị layout control image và tín hiệu show nút delete
                showIconDeleteOneImage = true;
                layout_icon_control_image_list.setVisibility(View.VISIBLE);

                //d. Load lại adapter, rcv (Tự động trong onResume)
//                setImageEventAdapter();

            }

            //B. Nếu dùng picker có sẵn của SDK > 19
            if(requestCode == INTENT_CODE_READ_IMAGES_SDK33){

                //1. Thêm dữ liệu vào URI list
                if(data.getClipData() != null){
                    for(int i=0; i< data.getClipData().getItemCount(); i++){
                        uriList.add(data.getClipData().getItemAt(i).getUri());
                    }
                }

                //2. Thêm vào list ImageEvent
                AddUriListToImageEventList(uriList);

                //3. Hiển thị tín hiệu show nút delete và layout control image và
                showIconDeleteOneImage = true;
                layout_icon_control_image_list.setVisibility(View.VISIBLE);

            }
        }
    }

    //6.8 Thêm Uri list vào ImageEvent list: Convert size -> lưu
    private void AddUriListToImageEventList(ArrayList<Uri> uriList) {

        for(int i=0; i<uriList.size(); i++){

            //1. Tạo 1 ImageEvent
            ImageEvent imageEvent = new ImageEvent();

            //2. Chuyển Uri về bytes array (giảm kích thước trước khi cho vào list lưu cuối)
            try {
                //A Đưa ảnh về mảng bytes luôn (Hiện tại không sử dụng vì lỗi nếu ảnh kích thước lớn - dự đoán tầm > 4100)
//                        InputStream iStream = getContentResolver().openInputStream(uriList.get(i));
//                        byte [] inputData = getBytes(iStream);

                //B. Lấy ảnh uri -> bimap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(view.getContext().getContentResolver(), uriList.get(i));

                //C. Thực hiện giảm kích thước ảnh (getResizedBitmap() hàm tự tạo). Kích thước tối đa cho phép 1500 (Theo sử dụng cho thấy đây là kích thước mà có thể lưu mà không bị lỗi và vừa tầm sử dụng)
                Bitmap resizedImage = getResizedBitmap(bitmap, 1500);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                resizedImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

                //Chuyển về bytes []
                byte[] imageBytes = outputStream.toByteArray();

                //D. Set ảnh cho ImageEvent
                imageEvent.setEvent_image(imageBytes);

                //E. tránh rò rỉ bộ nhớ (Sau khi đã xong bitmap)
                resizedImage.recycle();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //Thêm vào ImageEvent list
            imageEventArrayList.add(imageEvent);
        }
    }

    //6.9 Đưa ảnh về một kích thước cố định
    public Bitmap getResizedBitmap(Bitmap image, int maxWidth) {

        //1. Lấy kích thước gốc của ảnh bitmap
        int width = image.getWidth();
        int height = image.getHeight();

        //2. Lấy tỷ lệ ảnh gốc của chiều rộng / chiều cao
        float bitmapRatio = (float) width / (float) height;

        //3. Nếu chiều rộng > chiều cao (tỷ lệ >1) -> Chuyển chiều RỘNG về kích thước cho trước => suy ra chiều CAO theo tỷ lệ
        if (bitmapRatio > 1) {
            width = maxWidth;
            height = (int) (width / bitmapRatio);

            //4. Nếu chiều rộng < chiều cao (tỷ lệ < 1) -> Chuyển chiều CAO về kích thước cho trước => suy ra chiều RỘNG theo tỷ lệ
        } else {
            height = maxWidth;
            width = (int) (height * bitmapRatio);
        }

        //5. Trả về ảnh đã đưa về kích thước như mong muốn
        return Bitmap.createScaledBitmap(image, width, height, false);
    }

    //6.10 Convert Uri -> byte [] (Hiện tại không dùng)
//    public byte[] getBytes(InputStream inputStream) throws IOException {
//        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
//        int bufferSize = 1024;
//        byte[] buffer = new byte[bufferSize];
//
//        int len = 0;
//        while ((len = inputStream.read(buffer)) != -1) {
//            byteBuffer.write(buffer, 0, len);
//        }
//        return byteBuffer.toByteArray();
//    }


    //7.1 Show or hidden layout control image
    private void setupLayoutControlImage() {

        //Show or hidden button delete image
        img_add_event_delete_image.setOnClickListener(v -> {

            //Đổi tín hiệu true cho button delete trong adapter -> load lại rcv
            showIconDeleteOneImage = true;
            setImageEventAdapter();

        });

        //Clear rcv image
        img_event_add_image_delete_all.setOnClickListener(v -> {
            deleteAllImageEventDialog(Gravity.CENTER);
        });

        //Click nút check -> Hidden layout control Image list
        img_event_add_image_check.setOnClickListener(v -> {

            layout_icon_control_image_list.setVisibility(View.GONE);
            //Đổi tín hiệu ẩn hiện button delete trong adapter -> load lại rcv
            showIconDeleteOneImage = false;
            setImageEventAdapter();
        });
    }

    //7.2 Dialog hỏi xoá list ImageEvent của một Event
    private void deleteAllImageEventDialog(int gravity) {

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
        TextView txt_title_delete = dialog.findViewById(R.id.textView_title_delete_all_event);
        Button btn_yes_delete = dialog.findViewById(R.id.btn_yes_delete);
        Button btn_no_delete = dialog.findViewById(R.id.btn_no_delete);

        //set title
        txt_title_delete.setText(R.string.delete_all_image_event);

        //5. Hàm thực hiện trong dialog khi bấm nút Yes
        btn_yes_delete.setOnClickListener(v -> {

            //Thực hiện xoá clear list ảnh đang hiển thị (chưa xoá trong CSDL)
            imageEventArrayList.clear();
            //Load lại rcv ImageNote
            setImageEventAdapter();

            //đóng dialog
            dialog.dismiss();
        });

        btn_no_delete.setOnClickListener(v -> {
            dialog.cancel();
        });

        //12. show
        dialog.show();

    }

    //7.3 Hàm xác nhận XOÁ tất cả ảnh của 1 event : Sử dụng Alert với các nút chọn nhanh gọn (hoặc tự tạo dialog tương tự phần sửa)
    public void DialogClearListImageOfEvent(){
        //a. Tạo Alert, tiêu đề
        AlertDialog.Builder dialogXoa = new AlertDialog.Builder(view.getContext());
        dialogXoa.setMessage("Clear All Image?");

        //b. Tạo nút xoá
        dialogXoa.setPositiveButton("Yes", (dialogInterface, i) -> {
            //Thực hiện xoá
            imageEventArrayList.clear();
            setImageEventAdapter();
        });

        //c. Tạo nút huỷ (Thực hiện hàm bên trong nếu có, nếu không thì chỉ bỏ qua)
        dialogXoa.setNegativeButton("Cancel", (dialogInterface, i) -> {

        });

        //d. Thực hiện dialog
        dialogXoa.show();
    }

    //11.1 selectDateOnMonthView
    private void selectDateOnMonthView(int gravity) {
        //Tạo mở dialog là một fragment
//        FragmentManager fragmentManager = getFragmentManager();
//        MonthViewSelectFragment monthFragment = new MonthViewSelectFragment();
//        monthFragment.show(fragmentManager, "MonthFragment");

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

        //Ẩn nút tạo sự kiện mới
        btn_add_event_month_dialog.setVisibility(View.GONE);

        //5. Load rcv month view
        //Tạo ngày chọn riêng của month view
        CalendarUtils.selectedDateOnMonthViewDialog = CalendarUtils.selectedDate;

        //Hiển thị ngày chọn khi mới hiện dialog
        User user = UserUltils.getUserLocal(view.getContext());
        String monthViewText = CalendarUtils.onlyMonthYearFromDate(CalendarUtils.selectedDateOnMonthViewDialog, user.getDateFormat());
        txt_month_view.setText(monthViewText); //Gán text date cho dialog khi mới mở lên
        //Hiển thị màu cho tháng hiện tại
        if(CalendarUtils.selectedDateOnMonthViewDialog.getMonth() == LocalDate.now().getMonth() && CalendarUtils.selectedDateOnMonthViewDialog.getYear() == LocalDate.now().getYear()){
            txt_month_view.setText(Html.fromHtml("<font color = #0046E4>" + txt_month_view.getText().toString()));
        }

        //Load rcv day name (Dùng ở activity, fragment nào thì dùng hàm ở đó)
        LoadDayNameMonthDialog(rcv_list_day_name_dialog);
        //Load rcv days month
        LoadRcvOnDialog(txt_month_view, rcv_dialog_month_view);

        //6. Click button
        btn_cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        //7. Button OK -> Đổi ngày và thực hiện load weekview
        btn_ok.setOnClickListener(v -> {
            CalendarUtils.selectedDate = CalendarUtils.selectedDateOnMonthViewDialog;

            //Gán hiển thị ngày cho textView theo user (Chỉ có hiển thị TextView là dạng dd/MM/yyyy, còn lưu event và trong SQLite là dạng LocalDate yyyy-MM-dd)
            setDate();

            dialog.dismiss();
        });

        //8. Trở về ngày hôm nay trong month view
        ibtn_return_to_day_month_dialog.setOnClickListener(v -> {
            CalendarUtils.selectedDateOnMonthViewDialog = LocalDate.now();

            LoadRcvOnDialog(txt_month_view, rcv_dialog_month_view);
            LoadDayNameMonthDialog(rcv_list_day_name_dialog);
        });

        //10. Previous 1 tháng
        btn_previous_month.setOnClickListener(v -> {

            //Cập nhật lại ngày select
            CalendarUtils.selectedDateOnMonthViewDialog = CalendarUtils.selectedDateOnMonthViewDialog.minusMonths(1);

            //Cập nhật lại rcv
            LoadRcvOnDialog(txt_month_view, rcv_dialog_month_view);
            LoadDayNameMonthDialog(rcv_list_day_name_dialog);
        });

        //11. Forward 1 tháng
        btn_forward_month.setOnClickListener(v -> {

            //Cập nhật lại ngày select
            CalendarUtils.selectedDateOnMonthViewDialog = CalendarUtils.selectedDateOnMonthViewDialog.plusMonths(1);

            //Cập nhật lại rcv
            LoadRcvOnDialog(txt_month_view, rcv_dialog_month_view);
            LoadDayNameMonthDialog(rcv_list_day_name_dialog);
        });

        //12. show
        dialog.show();

    }

    //11.2 Load recyclerView của dialog (tạo bên ngoài để gọi lại nhiều lần)
    public void LoadRcvOnDialog (TextView txt_month_view, RecyclerView rcv_dialog_month_view){

        //1.1 set hiển thị cho title
        String dateSelectOnMonthView = CalendarUtils.onlyMonthYearFromDate(CalendarUtils.selectedDateOnMonthViewDialog, UserUltils.getUserLocal(view.getContext()).getDateFormat());
        txt_month_view.setText(dateSelectOnMonthView);

        //1.2 Hiển thị màu cho tháng hiện tại
        if(CalendarUtils.selectedDateOnMonthViewDialog.getMonth() == LocalDate.now().getMonth() && CalendarUtils.selectedDateOnMonthViewDialog.getYear() == LocalDate.now().getYear()){
            txt_month_view.setText(Html.fromHtml("<font color = #0046E4>" + txt_month_view.getText().toString()));
        }

        //2. Lấy danh sách ngày trong tháng theo ngày đang được select của month view -> load rcv
        ArrayList<LocalDate> monthArray = daysInMonthFollowWeek(CalendarUtils.selectedDateOnMonthViewDialog, UserUltils.getUserLocal(view.getContext()).getStartWeek());

        //Adapter
        CalendarMonthViewAdapter monthViewAdapter = new CalendarMonthViewAdapter(view.getContext(), monthArray, (position, date) -> {
            //Ngày select trong month sẽ thành ngày được click
            CalendarUtils.selectedDateOnMonthViewDialog = date;

            //Dùng để set hiển thị title của month view
            String dateSelectOnMonthView1 = CalendarUtils.onlyMonthYearFromDate(CalendarUtils.selectedDateOnMonthViewDialog, UserUltils.getUserLocal(view.getContext()).getDateFormat());
            txt_month_view.setText(dateSelectOnMonthView1);

            //Cập nhật lại rcv sau khi được click
            LoadRcvOnDialog(txt_month_view, rcv_dialog_month_view);

        });

        //setup layout manager và gán adapter cho rcv
        GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(), 7);
        rcv_dialog_month_view.setLayoutManager(gridLayoutManager);
        rcv_dialog_month_view.setAdapter(monthViewAdapter);
    }

    //11.3 LoadDayName (cho monthView Dialog)
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


    //A. Hàm click vào phím save -> Thêm event vào danh sách tổng event
    public void saveEventAction() throws IOException {

        //1. Lấy text đã ghi vào editText, datetime và các dữ liệu của event
        String eventName = edt_event_name.getText().toString().trim();
        String eventNotes = edt_note_new_event.getText().toString().trim();
        int event_notify = 1; //Đang để mặc định trạng thái thông báo là 1 (NO - không thông báo)

        // Xử lý nếu event name trống
        if(TextUtils.isEmpty(eventName)){
            Toast.makeText(view.getContext(), R.string.notification_event_name_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        // Thời gian tại thời điểm save (Cả ngày và giờ, chuyển string để lưu)
        LocalDateTime dateTimeNow = LocalDateTime.now();

        //2. Tạo event để lưu vào CSDL: Tên lấy tại edt, ngày là ngày đang select, thời gian nếu có (biến toàn cục), ngày giờ hiện tại, trạng thái 1 (đang tự đặt là chưa check), ghi chú trong edt, thông báo để là 1 (chưa thông báo, hiện tại chưa dùng tính năng này)
        Event newEvent;

        //Xử lý khi có đặt và không đặt thời gian | Trạng thái mới thêm vào của event là 1 (bình thường)
        if(timeOfEvent == null) {
            //Đặt thời gian là "" (không phải null) thì khi kiểm tra phải dùng isEmpty để kiểm tra
            newEvent = new Event(eventName, CalendarUtils.selectedDate.toString(), "", dateTimeNow.toString(), 1, eventNotes, event_notify);
        } else {
            //tên event, ngày của event (là ngày đang select), thời gian đặt cho event (tại dialog), thời điểm hiện tại lưu event
            newEvent = new Event(eventName, CalendarUtils.selectedDate.toString(), timeOfEvent.toString(), dateTimeNow.toString(), 1, eventNotes, event_notify);
        }

        //Thêm event và list ảnh bytes [] của event vào bảng EventOfDay của SQLite
        truyVanDuLieuSQLite.InsertEventToTable(newEvent, imageEventArrayList);

        //4. Clean trước khi trở về màn hình
        cleanFragment();

        //5. Kết thúc fragment
        finishFragmentAddEvent();
    }

    //B.Hàm click cancel -> Back activity
    public void finishFragmentAddEvent() {
        //Clean trước khi trở về màn hình
        cleanFragment();
        HiddenLayoutEditEventNotes();
        layout_icon_control_image_list.setVisibility(View.GONE);

        //Trả về fragment calendar
        CalendarViewPagerFragment.setCurrentItemCalendarViewPager(0);

        //set hướng
//        MainActivity.loadViewPager(1);
    }

    //C. Load lại mỗi khi vào, trở lại activity
    @Override
    public void onResume() {
        super.onResume();

        //1. load rcv ảnh của event (phục vụ cho load ảnh chọn sau khi mở chọn ảnh)
        setImageEventAdapter();

        //2. Set date (Vì fragment ở ViewPager sẽ được load trước nên cần đặt load lại khi mở, khi mở activity chọn ảnh trả về sẽ load lại nhưng selectDate vẫn vậy), không set time lại
        setDate();
    }
}