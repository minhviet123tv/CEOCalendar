package com.mvdragon.weekcalendar.notes.imagenote;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.mvdragon.weekcalendar.MainActivity;
import com.mvdragon.weekcalendar.R;
import com.mvdragon.weekcalendar.database.TruyVanDuLieuSQLite;
import com.mvdragon.weekcalendar.database.UserUltils;
import com.mvdragon.weekcalendar.model.ImageNote;
import com.mvdragon.weekcalendar.model.ViewImageShared;
import com.mvdragon.weekcalendar.notes.NotesViewPagerFragment;
import com.mvdragon.weekcalendar.notes.UpdateNoteFragment;

import java.util.ArrayList;

public class ViewImageNoteViewPagerFragment extends Fragment {
    @SuppressLint("StaticFieldLeak")
    public static View view;
    private static ViewPager2 viewPager_image;
    @SuppressLint("StaticFieldLeak")
    private static TruyVanDuLieuSQLite truyVanDuLieuSQLite;
    private static ArrayList<ImageNote> imageArrayList;

    public ViewImageNoteViewPagerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_view_image_event, container, false);

        //1.1 Ánh xạ
        AnhXa();

        //2. Lấy thông tin lưu trong shared -> lấy ảnh và gán hiển thị (Khi mở update event thì chắc chắn đã có event được chọn để mở -> đã lưu event ở shared)
        loadViewPagerViewImage();

        return view;
    }


    //1. Ánh xạ
    private void AnhXa() {
        viewPager_image = view.findViewById(R.id.viewPager_image);
        truyVanDuLieuSQLite = new TruyVanDuLieuSQLite(requireActivity());
        imageArrayList = new ArrayList<>();
    }

    //2. Lấy dữ liệu đã lưu trong shared -> gán hiển thị (Khi mở update event thì chắc chắn đã có event được chọn để mở -> đã lưu event ở shared)
    public static void loadViewPagerViewImage() {
        //A. Lấy thông tin của event đã truyền vào shared (Cùng sử dụng một cách lưu Shared với bên Event)
        ViewImageShared viewImageShared = UserUltils.getViewImageShared(view.getContext());

        //B. Lấy dữ liệu và hiển thị ảnh
        if(viewImageShared != null){

            //Thông tin truyền đến
            int id_image = viewImageShared.getId_image();
            int id_event_or_note = viewImageShared.getId_event_or_note();
            int position = viewImageShared.getPosition(); //Vị trí ảnh được click (trong list ảnh)

            //Lấy thông tin list ảnh của event từ CSDL (chỉ lấy thông tin, chưa có ảnh) theo id_event | id_image_vent có thể dùng khi xoá ảnh
            imageArrayList = truyVanDuLieuSQLite.getListImageNoteProfile(id_event_or_note);
//            imageArrayList = UpdateNoteFragment.imageNoteArrayList; //Có thể dùng list ở fragment update nhưng vẫn phải truyền position nên truyền thông tin list image luôn

            //set thông tin list ảnh cho ViewPagerAdapter | Vị trí cần view được gửi lên cũng là vị trí đã click view (vẫn là list ảnh và event đó)
            ViewImageNoteFragmentViewPagerAdapter adapter = new ViewImageNoteFragmentViewPagerAdapter((FragmentActivity) view.getContext(), imageArrayList);
            viewPager_image.setAdapter(adapter);
            //set current item đang xem là vị trí được click (truyền đến đây bằng position)
            viewPager_image.setCurrentItem(position);

        }
    }

    //3.1 Delete ảnh và load lại rcv: id ảnh, vị trí ảnh trong list (để load lại)
    public static void deleteOneImageOfNote(int id_image_event, int positionCurrent){

        //Xoá trong list đang dùng
        imageArrayList.remove(positionCurrent);

        //Sau khi xoá thì giữ nguyên vị trí ảnh đang xem (ảnh sau sẽ lùi về vị trí này) -> Load lại ViewPager với list ảnh đã xoá bớt ảnh
        reloadViewPagerViewImage(positionCurrent);

        //* Xoá cho list ở UpdateNote (Ở đây đang xem list ảnh của note đang view update nên phải xoá trong list đang xem thì phải xoá cả ở gốc | Chú ý: tuỳ menu main đang dùng)
        UpdateNoteFragment.DeleteOneImageOfFinalList(positionCurrent);

        //Xoá ảnh trong CSDL (Hiện tại không xoá luôn, mà vẫn có thể cancel event ra sẽ vẫn giữ nguyên, đề phòng xoá nhầm | Hay giữ nguyên tính chất: Chỉ khi bấm update hay save mới thực sự lưu dữ liệu)
//        truyVanDuLieuSQLite.DeleteOneImageEvent(id_image_event);

        //* Nếu đã xoá hết ảnh thì back về update (Chú ý: Phân theo menu của Main)
        if(imageArrayList.isEmpty()){
            //Trả về update của menu notes -> Hiện menu bottom
            NotesViewPagerFragment.setCurrentItemNotes(3); //fragment update
            MainActivity.bottom_navigation.setVisibility(View.VISIBLE);
            //Thông báo
            Toast.makeText(view.getContext(), "Empty image!", Toast.LENGTH_SHORT).show();
        }

    }

    //3.2 load lại ViewPager (với list ảnh mới đã xoá bớt), setCurrent
    public static void reloadViewPagerViewImage(int positionCurrent){

        //a. Điều chỉnh khi ảnh được xoá là ảnh cuối list (do tính chất giữ nguyên vị trí đang xem khi xoá ảnh)
        if(positionCurrent >= imageArrayList.size()){
            positionCurrent = imageArrayList.size() - 1;
        }

        //b. list ảnh được load lại chính là list ảnh của 1 note (Lưu ở fragment update) nhưng đã xoá bớt ảnh khi bấm delete

        //c. set lại thông tin list ảnh cho ViewPagerAdapter | Vị trí current chính là vị trí vừa xoá
        ViewImageNoteFragmentViewPagerAdapter adapter = new ViewImageNoteFragmentViewPagerAdapter((FragmentActivity) view.getContext(), imageArrayList);
        viewPager_image.setAdapter(adapter);
        viewPager_image.setCurrentItem(positionCurrent);
    }
}