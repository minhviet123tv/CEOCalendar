package com.mvdragon.weekcalendar.notes.imagenote;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
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

import androidx.fragment.app.Fragment;

import com.mvdragon.weekcalendar.MainActivity;
import com.mvdragon.weekcalendar.R;
import com.mvdragon.weekcalendar.database.TruyVanDuLieuSQLite;
import com.mvdragon.weekcalendar.model.ImageNote;
import com.mvdragon.weekcalendar.notes.NotesViewPagerFragment;
import com.ortiz.touchview.TouchImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ViewImageNoteFragment extends Fragment {
    private View view;
    private TouchImageView img_image_fragment;
    private RelativeLayout layout_menu_view_event_image;
    private boolean show_layout_menu_view_event_image;
    private ImageView icon_back_view_full_image, icon_delete_view_full_image, icon_save_view_full_image;
    private TruyVanDuLieuSQLite truyVanDuLieuSQLite;

    public ViewImageNoteFragment() {
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
            ImageNote imageNote = (ImageNote) getArguments().getSerializable("ImageNote");
            int position = getArguments().getInt("position");

            Bitmap bitmap = null;

            if(imageNote != null) {
                //Chuyển định dạng byte [] về bitmap -> Set ảnh cho ImageView
                try {
                    //Lấy ảnh riêng của fragment (này) từ SQLite theo id ImageEvent (Vì list trong ViewPager chỉ lưu thông tin chứ chưa có ảnh, mà lấy tại đây để tránh bị nặng dữ liệu của ViewPager)
                    byte [] imageHere = truyVanDuLieuSQLite.getOneImageNote(imageNote.getId_image_note()).getNote_image();

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

            //4. Delete Image
            icon_delete_view_full_image.setOnClickListener(v -> {

                //Xoá ảnh trong list đang xem (Trong hàm đó bao gồm cả xoá bên update)
                assert imageNote != null;
                ViewImageNoteViewPagerFragment.deleteOneImageOfNote(imageNote.getId_image_note(), position);

            });

            //5. Tải ảnh về máy
            Bitmap finalBitmap = bitmap;
            icon_save_view_full_image.setOnClickListener(v -> {
                assert finalBitmap != null;
                SaveBitmapToPhone(finalBitmap);
            });
        }

        //Hàm Back
        icon_back_view_full_image.setOnClickListener(v -> {
            backFragment();
        });



        return view;
    }

    //4. Hàm lưu ảnh về máy
    private void SaveBitmapToPhone(Bitmap bitmap) {

        FileOutputStream fileOutputStream;

        File sdCard = Environment.getExternalStorageDirectory();
        File Directory = new File(sdCard.getAbsolutePath() + view.getContext().getString(R.string.download_folder_name)); //Download
        Directory.mkdir();

        @SuppressLint("DefaultLocale") String filename=String.format("%d.jpg",System.currentTimeMillis());
        File outfile = new File(Directory,filename);

        Toast.makeText(getActivity(), "Saved!", Toast.LENGTH_SHORT).show();

        try {
            fileOutputStream = new FileOutputStream(outfile);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

            Intent intent=new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(outfile));
            getActivity().sendBroadcast(intent);

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

    //2. Back về fragment update note (Thứ tự theo Adapter)
    public void backFragment() {
        NotesViewPagerFragment.setCurrentItemNotes(3);
        MainActivity.bottom_navigation.setVisibility(View.VISIBLE); //Hiện menu bottom
    }

    //1. Ánh xạ
    private void AnhXa() {
        img_image_fragment = view.findViewById(R.id.imageView_image_fragment);
        layout_menu_view_event_image = view.findViewById(R.id.layout_menu_view_event_image);
        show_layout_menu_view_event_image = false;

        icon_back_view_full_image = view.findViewById(R.id.icon_back_view_full_image);
        icon_save_view_full_image = view.findViewById(R.id.icon_save_view_full_image);
        icon_delete_view_full_image = view.findViewById(R.id.icon_delete_view_full_image);

        truyVanDuLieuSQLite = new TruyVanDuLieuSQLite(view.getContext());
    }

}