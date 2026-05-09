package com.mvdragon.weekcalendar.calendarview.event.viewimage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mvdragon.weekcalendar.MainActivity;
import com.mvdragon.weekcalendar.R;
import com.mvdragon.weekcalendar.calendarview.CalendarViewPagerFragment;
import com.mvdragon.weekcalendar.calendarview.event.ViewAllEventViewPagerFragment;
import com.mvdragon.weekcalendar.database.TruyVanDuLieuSQLite;
import com.mvdragon.weekcalendar.database.UserUltils;
import com.mvdragon.weekcalendar.model.ImageEvent;
import com.ortiz.touchview.TouchImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/*
Fragment hiển thị 1 ảnh (Sẽ hiển thị từng ảnh 1 trong list thông tin từ ViewPager truyền đến -> lấy dữ liệu ảnh tại fragment này)
Có 2 fragment dạng ViewPager sử dụng fragment này để view ảnh: update bên calendar và update bên view all event
 */
public class ViewImageFragment extends Fragment {
    public static View view;
    private TouchImageView img_image_fragment;
    private RelativeLayout layout_menu_view_event_image;
    private boolean show_layout_menu_view_event_image;
    private ImageView icon_back_view_full_image, icon_delete_view_full_image, icon_save_view_full_image;
    private TruyVanDuLieuSQLite truyVanDuLieuSQLite;
    public static Bitmap bitmap;

    public ViewImageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_view_image, container, false);

        //1. Ánh xạ
        AnhXa();

        //2. Lấy dữ liệu setup (gửi từ ImageFragmentViewPagerAdapter đến) qua fragment.setArguments(bundle)
        if(getArguments() != null) {

            //Lấy Ảnh view, position (từ ImageFragmentViewPagerAdapter)
            ImageEvent imageEvent = (ImageEvent) getArguments().getSerializable("ImageEvent");
            int position = getArguments().getInt("position");

            if(imageEvent != null) {
                //Chuyển định dạng byte [] về bitmap -> Set ảnh cho ImageView
                try {
                    //Lấy ảnh riêng của fragment (này) từ SQLite theo id ImageEvent (Vì list trong ViewPager chỉ lưu thông tin chứ chưa có ảnh, mà lấy tại đây để tránh bị nặng dữ liệu của ViewPager)
                    byte [] imageHere = truyVanDuLieuSQLite.getOneImageEvent(imageEvent.getId_image_event()).getEvent_image();

                    bitmap = BitmapFactory.decodeByteArray(imageHere, 0, imageHere.length);
                    img_image_fragment.setImageBitmap(bitmap);

                } catch (Exception e){
                    throw new RuntimeException(e);
                }
            }

            //3. Setup show or hidden menu image
//            img_image_fragment.setOnClickListener(v -> {
//                setupShowOrHiddenMenuImage(container);
//            });

            //4. Tải ảnh về máy
            icon_save_view_full_image.setOnClickListener(v -> {
                saveBitmapToPhone();
            });

            //5. Delete Image (Theo menu đang view của Main)
            icon_delete_view_full_image.setOnClickListener(v -> {
                //A. Xoá ảnh trong list của update event khi ở menu 0 (calendar)
                assert imageEvent != null;
                if(UserUltils.getUserLocal(view.getContext()).getFragment_user() == 0) {
                    ViewImageEventViewPagerFragment.deleteOneImageOfEvent(imageEvent.getId_image_event(), position);

                //B. Xoá ảnh trong list của update event khi ở menu 1 (View all event)
                } else if (UserUltils.getUserLocal(view.getContext()).getFragment_user() == 1) {
                    ViewImageAllEventViewPagerFragment.deleteOneImageOfEvent(imageEvent.getId_image_event(), position);
                }

                //Chú ý cập nhật lại list image ở UpdateEvent (onResume)
            });

        }

        //Hàm Back
        icon_back_view_full_image.setOnClickListener(v -> {
            backFragment();
        });



        return view;
    }

    //4. Hàm lưu ảnh về máy
    public static void saveBitmapToPhone() {

        FileOutputStream fileOutputStream = null;

        File sdCard = Environment.getExternalStorageDirectory();
        File Directory = new File(sdCard.getAbsolutePath()+ view.getContext().getString(R.string.download_folder_name)); //Download
        Directory.mkdir();

        @SuppressLint("DefaultLocale") String filename=String.format("%d.jpg",System.currentTimeMillis());
        File outfile = new File(Directory,filename);

        Toast.makeText(view.getContext(), "Saved!", Toast.LENGTH_SHORT).show();

        try {
            fileOutputStream = new FileOutputStream(outfile);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

            Intent intent=new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(outfile));
            view.getContext().sendBroadcast(intent);

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    //3. Setup Show Or Hidden Menu Image
    private void setupShowOrHiddenMenuImage(ViewGroup container) {

        show_layout_menu_view_event_image = !show_layout_menu_view_event_image;

        Transition transition = new Fade();
        transition.setDuration(200);
        transition.addTarget(layout_menu_view_event_image);

        TransitionManager.beginDelayedTransition(container, transition);
        layout_menu_view_event_image.setVisibility(show_layout_menu_view_event_image ? View.VISIBLE : View.GONE);

    }

    //2. Trả về fragment view update event (Theo trường hợp menu ở main)
    public void backFragment() {
        //A. Nếu là đang ở menu calendar (menu 0 của main)
        if(UserUltils.getUserLocal(view.getContext()).getFragment_user() == 0) {
            CalendarViewPagerFragment.setCurrentItemCalendarViewPager(2); //Về fragment update event

        //B. Nếu là đang ở menu View All Event (menu 1 của main)
        } else if (UserUltils.getUserLocal(view.getContext()).getFragment_user() == 1) {
            ViewAllEventViewPagerFragment.setCurrentItemCalendarViewPager(1); //Về fragment update event
        }

        MainActivity.bottom_navigation.setVisibility(View.VISIBLE); //Hiện menu bottom
    }

    //1. Ánh xạ
    private void AnhXa() {
        img_image_fragment = view.findViewById(R.id.imageView_image_fragment);
        layout_menu_view_event_image = view.findViewById(R.id.layout_menu_view_event_image);
        show_layout_menu_view_event_image = false;

        icon_back_view_full_image = view.findViewById(R.id.icon_back_view_full_image);
        icon_delete_view_full_image = view.findViewById(R.id.icon_delete_view_full_image);
        icon_save_view_full_image = view.findViewById(R.id.icon_save_view_full_image);

        truyVanDuLieuSQLite = new TruyVanDuLieuSQLite(getContext());
    }

}