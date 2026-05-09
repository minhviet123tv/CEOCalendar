package com.mvdragon.weekcalendar.calendarview.event;

import static android.app.Activity.RESULT_OK;

import static com.mvdragon.weekcalendar.CalendarUtils.daysInMonthFollowWeek;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.mvdragon.weekcalendar.CalendarUtils;
import com.mvdragon.weekcalendar.MainActivity;
import com.mvdragon.weekcalendar.R;
import com.mvdragon.weekcalendar.calendarview.CalendarMonthViewAdapter;
import com.mvdragon.weekcalendar.calendarview.CalendarViewPagerFragment;
import com.mvdragon.weekcalendar.calendarview.DayNameMonthDialogRecyclerViewAdapter;
import com.mvdragon.weekcalendar.calendarview.WeekViewFragment;
import com.mvdragon.weekcalendar.calendarview.event.viewimage.ViewImageEventViewPagerFragment;
import com.mvdragon.weekcalendar.database.TruyVanDuLieuSQLite;
import com.mvdragon.weekcalendar.database.UserUltils;
import com.mvdragon.weekcalendar.model.Event;
import com.mvdragon.weekcalendar.model.ImageEvent;
import com.mvdragon.weekcalendar.model.User;
import com.mvdragon.weekcalendar.model.ViewImageShared;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class UpdateEventFragment extends Fragment implements EasyPermissions.PermissionCallbacks{
    private static View view;
    private static EditText edt_update_event_name;
    private static EditText edt_update_event_note;
    private static TextView txt_update_event_date;
    private static TextView txt_update_event_time;
    private Button btn_update_event, btn_cancel_update_event;
    private ImageButton ic_back_fm_update_event, ibtn_save_update_event_note, ibtn_copy_note_content, ibtn_clear_update_event_note;
    private RelativeLayout layout_update_day_of_event, layout_update_time_event;
    private static TruyVanDuLieuSQLite truyVanDuLieuSQLite;
    private AdView adView;

    //Tham số của rcv event image
    private RecyclerView rcv_image_of_event;
    private ImageView img_update_event_delete_image, img_event_update_image_delete_all, img_event_update_image_check;
    private ProgressBar pb_list_image_event_update;
    private ArrayList<Uri> uriList;
    public static ArrayList<ImageEvent> imageEventArrayList;
    private final int REQUEST_CODE_CAMERA_AND_STORAGE = 100;
    private String requestPermissionSDK33 = Manifest.permission.READ_MEDIA_IMAGES;
    private int REQUEST_CODE_READ_MEDIA_IMAGES_SDK33 = 200;
    private int INTENT_CODE_READ_IMAGES_SDK33 = 300;
    private RelativeLayout layout_icon_control_image_list_update_event;
    private boolean showIconDeleteOneImage; //setup trạng thái cho icon delete
    private static Event event;

    public UpdateEventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_update_event, container, false);

        //1.1 Ánh xạ, load quảng cáo admob
        AnhXa();
//        AddGoogleAdmob();

        //1.2 Lấy dữ liệu gửi lên
//        Intent intent = getIntent();
//        event = (Event) intent.getSerializableExtra("event");
        loadEventData();

        //1.3 SetupIconOnNotesEvent()
        SetupIconOnNotesEvent();

        //2. Gán sẵn dữ liệu cho acitity
        loadDataForActivity();

        //3. Chọn ngày khi click vào layout chọn ngày
        layout_update_day_of_event.setOnClickListener(v -> {
//            selectDateDialog();
            selectDateOnMonthView(Gravity.CENTER);
        });

        //4. Chọn giờ khi click vào layout chọn giờ
        layout_update_time_event.setOnClickListener(v -> {
            selectTimeDialog();
        });

        //5. Setup focus of EditText
        SetupFocusEditText();

        //6. Delay Load adapter, rcv ảnh của event khi mới vào (đã load trong onResume): Nhằm rút gọn thời gian mở activity vì chờ load ảnh
//        Handler handler = new Handler();
//        handler.postDelayed(() -> setImageEventAdapter(), 500);

        //7. Show or hidden layout control image
        setupLayoutControlImage();

        //8. Update event, back trở về Main
        btn_update_event.setOnClickListener(v -> {
            try {
                UpdateEvent();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        //9. Back button
        btn_cancel_update_event.setOnClickListener(v -> {
            cancelUpdateEvent();
        });
        //10. Back arrow
        ic_back_fm_update_event.setOnClickListener(v -> {
            cancelUpdateEvent();
        });



        return view;
    }



    //1.1 Ánh xạ
    private void AnhXa() {
        edt_update_event_name = view.findViewById(R.id.edt_update_event_name);
        txt_update_event_date = view.findViewById(R.id.textView_update_event_date);
        txt_update_event_time = view.findViewById(R.id.textView_update_event_time);

        btn_update_event = view.findViewById(R.id.btn_update_event);
        btn_cancel_update_event = view.findViewById(R.id.btn_cancel_update_event);
        edt_update_event_note = view.findViewById(R.id.edt_update_event_note);

        layout_update_day_of_event = view.findViewById(R.id.layout_update_day_of_event);
        layout_update_time_event = view.findViewById(R.id.layout_update_time_event);

        ic_back_fm_update_event = view.findViewById(R.id.ic_back_fm_update_event);
        ibtn_save_update_event_note = view.findViewById(R.id.ibtn_save_update_event_note);
        ibtn_copy_note_content = view.findViewById(R.id.ibtn_copy_note_content);
        ibtn_clear_update_event_note = view.findViewById(R.id.ibtn_clear_update_event_note);

        //Các thành phần ảnh của update event
        rcv_image_of_event = view.findViewById(R.id.rcv_image_update_event);
        img_update_event_delete_image = view.findViewById(R.id.imageView_update_event_delete_image);
        img_event_update_image_delete_all = view.findViewById(R.id.imageView_update_event_image_delete_all);
        img_event_update_image_check = view.findViewById(R.id.imageView_update_event_image_check);
        pb_list_image_event_update = view.findViewById(R.id.pb_list_image_event_update);

        event = new Event();
        uriList = new ArrayList<>();
        imageEventArrayList = new ArrayList<>();

        layout_icon_control_image_list_update_event = view.findViewById(R.id.layout_icon_control_image_list_update_event);

        //Khai báo truy cập SQLite tại đây để truyền context (màn hình)
        truyVanDuLieuSQLite = new TruyVanDuLieuSQLite(view.getContext());

    }

    public static void loadEventData(){
        event = UserUltils.getEventShared(view.getContext());
    }

    //1.3 Setup icon on notes event
    private void SetupIconOnNotesEvent() {
        //a. Clear edt notes
        ibtn_clear_update_event_note.setOnClickListener(v -> {
            edt_update_event_note.getText().clear();
        });

        //b. Check save note
        ibtn_save_update_event_note.setOnClickListener(v -> {
            //out focus
            edt_update_event_note.clearFocus();

            //hidden icon
            HiddenLayoutEditEventNotes();
        });

        //c. Copy note content
        ibtn_copy_note_content.setOnClickListener(v -> {

            String inputText = edt_update_event_note.getText().toString().trim();

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

    //2. Gán sẵn dữ liệu cho acitity (Xác định kỹ lưỡng và tạo giá trị ban đầu khi mới vào app, vì ở update cần lấy dữ liệu sẵn có của event -> Đối với những dữ liệu không lấy và có giá trị mặc định từ Shared)
    public static void loadDataForActivity() {

        edt_update_event_name.setText(event.getEvent_name());
        edt_update_event_note.setText(event.getEvent_notes());

        //A. Set date. Xử lý null khi mới vào app chưa có dữ liệu sẵn
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd"); //Lấy đúng định dạng của LocalDate
        LocalDate dateOfEvent;

        if(event.getEvent_date() != null) {
            dateOfEvent = LocalDate.parse(event.getEvent_date(), formatterDate);
        }  else {
            dateOfEvent = LocalDate.now();
        }

        User user = UserUltils.getUserLocal(view.getContext());
        txt_update_event_date.setText(CalendarUtils.monthYearFromDate(dateOfEvent, user.getDateFormat())); //Chuyển về định dạng sử dụng mong muốn

        //B. Set time. Nếu có thời gian thì lấy về định dạng và đặt cho TextView (đang xác định bằng chiều dài ký tự text length > 2 số, vì giờ phút thì có 3-5 ký tự) | Bên Calendar không cần xác định vì không phải load trước
        DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH:mm");

        //Kiểm tra isEmpty vì khi tạo đã không để null mà để trống "" (với trường hợp không đặt thời gian) | Có thể set thời gian cụ thể cho default dạng String
        if(!event.getEvent_time().isEmpty()) {
            LocalTime timeOfEvent = LocalTime.parse(event.getEvent_time(), formatterTime);
            txt_update_event_time.setText(timeOfEvent + "");

        //C. Nếu thời gian null thì set hiển thị tượng trưng
        } else if (event.getEvent_time() == null){
            txt_update_event_time.setText(R.string.time_of_event);
        }

        //D. Lấy list ảnh trong SQLite
        imageEventArrayList = truyVanDuLieuSQLite.getListImageEvent(event.getId_event());

        //Thông báo tạm để xem lấy đuợc list chưa
//        Toast.makeText(this, imageEventArrayList.size() + "", Toast.LENGTH_SHORT).show();
    }

    //4. Hàm tạo cửa sổ chọn giờ
    private void selectTimeDialog(){

        //Đặt thời gian
        int hourOfEvent, minuteOfEvent;

        //Nếu chưa có thời gian thì set sẵn một mốc cụ thể
        if(event.getEvent_time().isEmpty()){
            hourOfEvent = 12;
            minuteOfEvent = 0;

            //Nếu đã có thời gian rồi thì lấy để đặt cho dialog
        } else {
            String timeEvent = event.getEvent_time();
            DateTimeFormatter formatterHour = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime localTimeHour = LocalTime.parse(timeEvent, formatterHour);

            //Lấy giờ
            hourOfEvent = localTimeHour.getHour();
            //Lấy phút
            minuteOfEvent = localTimeHour.getMinute();
        }

        //Định dạng 24h
        boolean is24HourView = true;

        //Cửa sổ chọn giờ
        TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(), android.R.style.Theme_Holo_Light_Dialog, (view, hourOfDay, minute) -> {

            //Lấy thời gian đã chọn ở cửa sổ (định dạng LocalTime)
            LocalTime timeSelected = LocalTime.of(hourOfDay, minute);
            //Gán cho event đang update
            event.setEvent_time(timeSelected.toString());
            //set cho textView
            txt_update_event_time.setText(timeSelected.toString());

        }, hourOfEvent, minuteOfEvent, is24HourView );

        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        timePickerDialog.setTitle("Select time"); //Tiêu đề
        timePickerDialog.show(); //Hiển thị cửa sổ
    }

    //5.1 Setup forcus
    private void SetupFocusEditText() {

        //Cài đặt event name
        edt_update_event_name.setOnFocusChangeListener((v, hasFocus) -> {
            //Nếu out focus -> thì ẩn bàn phím
            if(!hasFocus){
                HiddenKeyboard();
            } else {
                //Có focus thì ẩn các layout control còn lại
                HiddenLayoutEditEventNotes();
                layout_icon_control_image_list_update_event.setVisibility(View.GONE);
            }
        });

        //Cài đặt focus của event notes
        edt_update_event_note.setOnFocusChangeListener((v, hasFocus) -> {

            //Nếu out focus -> Ẩn bàn phím, các icon edit
            if(!hasFocus){
                HiddenKeyboard();
                //hidden icon
                HiddenLayoutEditEventNotes();
            }
            //Có focus edt note -> thì hiện bàn phím, hiện icon (bàn phím tự hiện), ẩn layout edit image
            else {
                //show icon
                ShowLayoutEditEventNotes();
                //ẩn control image
                layout_icon_control_image_list_update_event.setVisibility(View.GONE);
            }
        });
    }

    //5.2 Ẩn bàn phím
    public void HiddenKeyboard(){
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    //5.3 HiddenLayoutEditEventNotes
    private void HiddenLayoutEditEventNotes(){
        ibtn_clear_update_event_note.setVisibility(View.GONE);
        ibtn_copy_note_content.setVisibility(View.GONE);
        ibtn_save_update_event_note.setVisibility(View.GONE);
    };

    //5.4 HiddenLayoutEditEventNotes
    private void ShowLayoutEditEventNotes(){
        ibtn_clear_update_event_note.setVisibility(View.VISIBLE);
        ibtn_copy_note_content.setVisibility(View.VISIBLE);
        ibtn_save_update_event_note.setVisibility(View.VISIBLE);
    };

    //6.1 Load adapter, rcv ảnh của event, Sự kiện nút cuối add image, sự kiện khi click vào ảnh (view image)
    private void setImageEventAdapter() {

        //I. Khai báo adapter: màn hình, list, hàm thực hiện khi click icon Add (cuối list), Hàm khi click item (chính là click vào ảnh) được khai báo trong adapter
        ImageEventAdapter imageEventAdapter = new ImageEventAdapter(view.getContext(), imageEventArrayList, showIconDeleteOneImage, () -> {

            //A. Hàm thực hiện khi click icon Add (cuối list) -> Mở hàm xin quyền truy cập tự tạo -> Sau đó mở activity chọn ảnh
            //Nếu version 33 trở lên (tên là TIRAMISU) phải hỏi quyền truy cập ảnh riêng READ_MEDIA_IMAGES (không truy cập cả bộ nhớ chung như Version trước)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionSDK33();
            } else {
                requestPermissions();
            }

            //Ẩn button delete 1 ảnh, show layout control image
            showIconDeleteOneImage = true;
            layout_icon_control_image_list_update_event.setVisibility(View.VISIBLE);

            HiddenLayoutEditEventNotes(); //Ẩn layout edit notes
            edt_update_event_name.clearFocus(); //Ẩn focus edt (nếu không khi bấm lưu nó sẽ tự động hiện bàn phím)
            edt_update_event_note.clearFocus();

        }, (id_event, id_image_event, position) -> {
            //B. Hàm khi click item (là click vào ảnh) được khai báo trong adapter

            //Chỉ thông báo khi click vào image (Trường hợp chưa bấm save -> Ngược tín hiệu với bên showIconDeleteOneImage)
            if(showIconDeleteOneImage){
                if(UserUltils.getUserLocal(requireActivity()).getCountLogin() <= 30) {
                    Toast.makeText(view.getContext(), "Save to view image!", Toast.LENGTH_SHORT).show();
                }
            } else {

                //Gửi dữ liệu vào shared
                ViewImageShared viewImageShared = new ViewImageShared(id_image_event, id_event, position);
                UserUltils.saveViewImageLocal(view.getContext(), viewImageShared);

                //Mở fragment view ảnh, load lại viewPager của fragment này (nếu không load lại thì sẽ hiện cái cũ)
                CalendarViewPagerFragment.setCurrentItemCalendarViewPager(3);
                ViewImageEventViewPagerFragment.loadViewPagerViewImage();
                MainActivity.bottom_navigation.setVisibility(View.GONE); //Ẩn menu bottom
            }

            //Ẩn các control khác
            HiddenLayoutEditEventNotes(); //Ẩn layout edit notes
            edt_update_event_name.clearFocus(); //Ẩn focus edt (nếu không khi bấm lưu nó sẽ tự động hiện bàn phím)
            edt_update_event_note.clearFocus();

        });

        //II. Cài đặt layout, adapter cho rcv
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false); //true: đảo ngược thứ tự (Ảnh cuối list sẽ hiển thị đầu tiên)
        rcv_image_of_event.setLayoutManager(linearLayoutManager);
        rcv_image_of_event.setFocusable(false); //Chưa hiểu
        rcv_image_of_event.setAdapter(imageEventAdapter);

        //III. Ẩn progressbar chờ load rcv ảnh sau khi đã tải và cài đặt xong
        pb_list_image_event_update.setVisibility(View.GONE);


    }

    //6.2.1 Hàm xin quyền truy cập (Tự tạo) camera, đọc ảnh | Nếu cấp quyền -> Thực hiện hàm chọn ảnh
    private void requestPermissions() {
        //Yêu cầu quyền truy cập
        String [] stringPermission = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}; //Manifest.permission.CAMERA,

        //Nếu cấp quyền -> Thực hiện hàm chọn ảnh
        if(EasyPermissions.hasPermissions(view.getContext(), stringPermission)){
            //Thực hiện hàm
            imagePicker();
        }
        //Không cấp quyền -> (khi click button) Thông báo và (có thể là) hỏi lại quyền vào lần sau trong hàm implement
        else {
            EasyPermissions.requestPermissions(this, getString(R.string.app_needs_connect), REQUEST_CODE_CAMERA_AND_STORAGE, stringPermission);
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

    //6.4 Hàm implement 1 : Khi chấp nhận quyền truy cập máy (2 quyền) | (Hàm này giống như bổ sung thêm cho chắc chắn)
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if(requestCode == REQUEST_CODE_CAMERA_AND_STORAGE && perms.size() == 2){
            //Gọi hàm mở chọn ảnh
            imagePicker();
        }
    }
    //6.5 Hàm implement 2: Khi không chấp nhận quyền truy cập máy (2 quyền)
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        //Khi bị từ chối nhiều lần
        if(EasyPermissions.somePermissionDenied(this)){
            //Open app setting
            new AppSettingsDialog.Builder(this).build().show();
        }
        //Khi bị từ chối 1 lần
        else {
            Toast.makeText(view.getContext().getApplicationContext(), "Permission denied!", Toast.LENGTH_SHORT).show();
        }
    }

    //6.6.1  Hàm mở màn hình chọn ảnh
    private void imagePicker() {

        //a. id của theme (có thể đổi theme 7 ngày tự tạo)
        int theme = R.style.CustomTheme;

        //b. Làm trống list ảnh uri trước khi chọn
        uriList.clear();

        //c. Open picker
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
//                .setImageSizeLimit(2200) //Giới hạn kích thước hình ảnh có thể tìm thấy
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

        //Nếu có dữ liệu trả về
        if(resultCode == RESULT_OK && data != null){

            //a. Nếu dùng picker image của ninja (đúng mã lấy ảnh của ninja)
            if(requestCode == FilePickerConst.REQUEST_CODE_PHOTO){

                //1. Lấy list ảnh (uri) đã chọn
                uriList = data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA);

                //2. Thêm vào list ImageEvent
                try {
                    AddUriListToImageEventList(uriList);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                //c. Hiển thị layout control image và tín hiệu show nút delete
                showIconDeleteOneImage = true;
                layout_icon_control_image_list_update_event.setVisibility(View.VISIBLE);

                //d. Load lại adapter, rcv (Tự động trong onResume)
//                setImageEventAdapter();
            }

            //b. Nếu dùng picker có sẵn của SDK > 19
            if(requestCode == INTENT_CODE_READ_IMAGES_SDK33){

                //1. Thêm dữ liệu vào URI list
                if(data.getClipData() != null){
                    for(int i=0; i< data.getClipData().getItemCount(); i++){
                        uriList.add(data.getClipData().getItemAt(i).getUri());
                    }
                }

                //2. Thêm vào list ImageEvent
                try {
                    AddUriListToImageEventList(uriList);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                //3. Hiển thị tín hiệu show nút delete và layout control image và
                showIconDeleteOneImage = true;
                layout_icon_control_image_list_update_event.setVisibility(View.VISIBLE);

            }
        }
    }

    //6.8 Add Uri List To ImageEvent List
    private void AddUriListToImageEventList(ArrayList<Uri> uriListConvert) throws IOException {

        for(int i=0; i<uriListConvert.size(); i++){

            //a. Tạo 1 ImageEvent
            ImageEvent imageEvent = new ImageEvent();

            //b. Lấy ảnh uri -> bimap -> giảm kích thước nếu kích thước lớn, nếu quá lớn thì thông báo -> Chuyển ảnh về bytes array và set cho ImageEvent
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(view.getContext().getContentResolver(), uriListConvert.get(i));

            //c. Thực hiện giảm kích thước ảnh
            Bitmap resizedImage = getResizedBitmap(bitmap, 1500);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            resizedImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            //d. Chuyển về bytes []
            byte[] imageBytes = outputStream.toByteArray();

            //e. Set ảnh cho ImageEvent
            imageEvent.setEvent_image(imageBytes);

            //f. tránh rò rỉ bộ nhớ (Sau khi đã xong bitmap)
            resizedImage.recycle();

            //g. Thêm vào ImageEvent list
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

    //7.1 Show or hidden layout control image
    private void setupLayoutControlImage() {

        //1. Show or hidden button delete image
        img_update_event_delete_image.setOnClickListener(v -> {

            //Đổi tín hiệu true cho button delete trong adapter -> load lại rcv
            showIconDeleteOneImage = true;
            setImageEventAdapter();

        });

        //2. Clear rcv image
        img_event_update_image_delete_all.setOnClickListener(v -> {
            deleteAllImageEventDialog(Gravity.CENTER);
        });

        //3. Click nút check -> Close Menu điều chỉnh Image list
        img_event_update_image_check.setOnClickListener(v -> {

            layout_icon_control_image_list_update_event.setVisibility(View.GONE);
            //Đổi tín hiệu ẩn hiện button delete trong adapter -> load lại rcv
            showIconDeleteOneImage = false;

            //Cập nhật ảnh của event: (Chỉ xoá hết các ảnh cũ nếu list mới là trống không, nếu list mới có ảnh thì vừa xoá tất cả ảnh cũ vừa lưu list ảnh mới - Hàm UpdateImageEvent là 2 trong 1)
            if(imageEventArrayList.isEmpty()){
                truyVanDuLieuSQLite.DeleteAllImageEvent(event.getId_event());
            } else {
                try {
                    truyVanDuLieuSQLite.UpdateImageEvent(event.getId_event(), imageEventArrayList);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            //Lấy lại danh sách ảnh
            imageEventArrayList = truyVanDuLieuSQLite.getListImageEvent(event.getId_event());
            //Cập nhật adapter rcv
            setImageEventAdapter();
            //Thông báo
            Toast.makeText(view.getContext(), "Saved!", Toast.LENGTH_SHORT).show();
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

    //8. Update event: Lưu dữ liệu -> Trở về main
    private void UpdateEvent() throws IOException {

        //Đặt lại các thông số của event đang update (Date và Time đã update trong hàm riêng, nếu không update thì vẫn giữ nguyên)
        event.setEvent_name(edt_update_event_name.getText().toString().trim());
        event.setEvent_notes(edt_update_event_note.getText().toString().trim());

        //Xử lý nếu event name trống
        if(TextUtils.isEmpty(edt_update_event_name.getText().toString().trim())){
            Toast.makeText(view.getContext(), R.string.notification_event_name_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        //Cập nhật SQLite: event với dữ liệu đã update, id_event
        truyVanDuLieuSQLite.UpdateEvent(event, event.getId_event());

        //Cập nhật ảnh của event: (Xoá hết các ảnh cũ nếu list mới là trống không, nếu có ảnh thì vừa xoá tất cả ảnh cũ vừa lưu list ảnh mới (trong 1 hàm) | Đã bấm update thì kiểu gì cũng sẽ xoá hết ảnh cũ)
        if(imageEventArrayList.isEmpty()){
            truyVanDuLieuSQLite.DeleteAllImageEvent(event.getId_event());
        } else {
            truyVanDuLieuSQLite.UpdateImageEvent(event.getId_event(), imageEventArrayList);
        }

        //Thông báo và trở về fragment calendar
        if(UserUltils.getUserLocal(requireActivity()).getCountLogin() <= 30) {
            Toast.makeText(view.getContext(), R.string.update_success, Toast.LENGTH_SHORT).show();
        }

        cancelUpdateEvent();
    }

    //9. Delete One Image Of final List: Xoá ảnh trong list chính (Sử dụng bên màn hình ViewImageActivity)
    public static void DeleteOneImageOfFinalList(int position){
        imageEventArrayList.remove(position);
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

        //Ẩn nút tạo sự kiện mới ở Update Activity
        btn_add_event_month_dialog.setVisibility(View.GONE);

        //5. Load rcv month view, ngày được select (của month view) là ngày của event
        CalendarUtils.selectedDateOnMonthViewDialog = LocalDate.parse(event.getEvent_date());

        //Hiển thị ngày title khi mới hiện dialog
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

            //Cập nhật cho event
            event.setEvent_date(CalendarUtils.selectedDate.toString());

            //Gán hiển thị ngày cho textView theo user (Chỉ có hiển thị TextView là dạng dd/MM/yyyy, còn lưu event và trong SQLite là dạng LocalDate yyyy-MM-dd)
            txt_update_event_date.setText(CalendarUtils.monthYearFromDate(CalendarUtils.selectedDate, user.getDateFormat()));

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

            //Cập nhật lại ngày select và textView hiển thị
            CalendarUtils.selectedDateOnMonthViewDialog = CalendarUtils.selectedDateOnMonthViewDialog.minusMonths(1);

            //Cập nhật lại rcv
            LoadRcvOnDialog(txt_month_view, rcv_dialog_month_view);
            LoadDayNameMonthDialog(rcv_list_day_name_dialog);
        });

        //11. Forward 1 tháng
        btn_forward_month.setOnClickListener(v -> {

            //Cập nhật lại ngày select và textView hiển thị
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

    //A. Hàm click cancel -> Back
    private void cancelUpdateEvent() {

        //Clean trước khi trở về màn hình
        cleanFragment();

        //Trở về fragment 0 của ViewPager Calendar (Menu 0 của Main)
        if(UserUltils.getUserLocal(view.getContext()).getFragment_user() == 0){
            CalendarViewPagerFragment.setCurrentItemCalendarViewPager(0);

        //Trở về fragment 0 của ViewPager View All Event (Menu 1 của Main)
        } else if(UserUltils.getUserLocal(view.getContext()).getFragment_user() == 1){
            ViewAllEventViewPagerFragment.setCurrentItemCalendarViewPager(0);
        }

    }

    //B.1 làm sạch dữ liệu fragment
    public void cleanFragment(){

        edt_update_event_name.setText(null);
        edt_update_event_note.setText(null);
        showIconDeleteOneImage = false;

        imageEventArrayList.clear();
        setImageEventAdapter();

        HiddenLayoutEditEventNotes();
        layout_icon_control_image_list_update_event.setVisibility(View.GONE);

        //Load lại ngày giờ
        clearDate();
        clearTime();

    }

    //B.2 setDate
    private void clearDate() {
        txt_update_event_date.setText(CalendarUtils.monthYearFromDate(CalendarUtils.selectedDate, UserUltils.getUserLocal(view.getContext()).getDateFormat()));
    }

    //B.3 setTime
    private void clearTime(){
        String time = " --:--";
        txt_update_event_time.setText("Time " + time);
    }

    //C. Load tự động mỗi khi vào hoặc trở lại activity
    @Override
    public void onResume() {
        super.onResume();

        //1. Setup load list image (load lại ảnh khi mới mở fragment update và khi chọn ảnh xong)
        try {
            //Nếu số lượng ảnh của event > 10 thì delay thời gian load ảnh (để load activity trước)
            int count = truyVanDuLieuSQLite.CountAllImageOfEvent(event.getId_event());

            if(count > 10){
                Handler handler = new Handler();
                pb_list_image_event_update.setVisibility(View.VISIBLE);
                handler.postDelayed(this::setImageEventAdapter, 1000);

            //Nếu số lượng <10 thì load luôn không cần delay
            } else {
                setImageEventAdapter();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}