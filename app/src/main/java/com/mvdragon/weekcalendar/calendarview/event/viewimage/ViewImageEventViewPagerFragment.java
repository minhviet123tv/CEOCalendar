package com.mvdragon.weekcalendar.calendarview.event.viewimage;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.mvdragon.weekcalendar.MainActivity;
import com.mvdragon.weekcalendar.R;
import com.mvdragon.weekcalendar.calendarview.CalendarViewPagerFragment;
import com.mvdragon.weekcalendar.calendarview.event.UpdateEventFragment;
import com.mvdragon.weekcalendar.calendarview.event.UpdateEventViewAllFragment;
import com.mvdragon.weekcalendar.database.TruyVanDuLieuSQLite;
import com.mvdragon.weekcalendar.database.UserUltils;
import com.mvdragon.weekcalendar.model.ImageEvent;
import com.mvdragon.weekcalendar.model.ViewImageShared;

import java.util.ArrayList;

public class ViewImageEventViewPagerFragment extends Fragment {
    public static View view;
    private static ViewPager2 viewPager_image;
    private static TruyVanDuLieuSQLite truyVanDuLieuSQLite;

    private static ArrayList<ImageEvent> imageEventArrayList;

    public ViewImageEventViewPagerFragment() {
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
    }

    //2. Lấy dữ liệu đã lưu trong shared -> gán hiển thị (Khi mở update event thì chắc chắn đã có event được chọn để mở -> đã lưu event ở shared)
    public static void loadViewPagerViewImage() {
        //A. Lấy thông tin của event đã truyền vào shared
        ViewImageShared viewImageShared = UserUltils.getViewImageShared(view.getContext());

        //B. Lấy dữ liệu và hiển thị ảnh
        if(viewImageShared != null){

            //Thông tin truyền đến
            int id_image_event = viewImageShared.getId_image();
            int id_event = viewImageShared.getId_event_or_note();
            int position = viewImageShared.getPosition(); //Vị trí ảnh được click (trong list ảnh)

            //Lấy thông tin list ảnh của event từ CSDL (chỉ lấy thông tin, chưa có ảnh) theo id_event | id_image_vent có thể dùng khi xoá ảnh
            imageEventArrayList = truyVanDuLieuSQLite.getListProfileImageEvent(id_event);

            //set thông tin list ảnh cho ViewPagerAdapter | Vị trí cần view được gửi lên cũng là vị trí đã click view (vẫn là list ảnh và event đó)
            ViewImageFragmentViewPagerAdapter adapter = new ViewImageFragmentViewPagerAdapter((FragmentActivity) view.getContext(), imageEventArrayList);
            viewPager_image.setAdapter(adapter);
            viewPager_image.setCurrentItem(position);

        }
    }

    //3.1 Delete ảnh và load lại rcv: id ảnh, vị trí ảnh trong list (để load lại)
    public static void deleteOneImageOfEvent(int id_image_event, int positionCurrent){

        //Xoá trong list đang dùng
        imageEventArrayList.remove(positionCurrent);

        //Sau khi xoá thì giữ nguyên vị trí ảnh đang xem (ảnh sau sẽ lùi về vị trí này) -> Load lại ViewPager với list ảnh mới (đã xoá bớt ảnh)
        reloadViewPagerViewImage(positionCurrent);

        //* Xoá cho list ở UpdateEvent (tuỳ menu: 0-menu calendar, 1-menu view all event)
        if(UserUltils.getUserLocal(view.getContext()).getFragment_user() == 0){
            UpdateEventFragment.DeleteOneImageOfFinalList(positionCurrent);
        } else if (UserUltils.getUserLocal(view.getContext()).getFragment_user() == 1) {
            UpdateEventViewAllFragment.DeleteOneImageOfFinalList(positionCurrent);
        }


        //Xoá ảnh trong CSDL (Hiện tại không xoá luôn, mà vẫn có thể cancel event ra sẽ vẫn giữ nguyên, đề phòng xoá nhầm | Hay giữ nguyên tính chất: Chỉ khi bấm update hay save mới thực sự lưu dữ liệu)
//        truyVanDuLieuSQLite.DeleteOneImageEvent(id_image_event);

        //* Nếu đã xoá hết ảnh thì back về event đang view update (Chú ý: Phân theo menu của Main)
        if(imageEventArrayList.isEmpty()){

            //Trả về update event của menu Calendar
            CalendarViewPagerFragment.setCurrentItemCalendarViewPager(2);
            MainActivity.bottom_navigation.setVisibility(View.VISIBLE); //Hiện menu bottom

            //Thông báo (phải thông báo ở activity chứa ViewPager)
            Toast.makeText(view.getContext(), "Empty image!", Toast.LENGTH_SHORT).show();
        }

    }

    //3.2 load lại ViewPager (với list ảnh mới đã xoá bớt), setCurrent
    public static void reloadViewPagerViewImage(int positionCurrent){

        //a. Điều chỉnh khi ảnh được xoá là ảnh cuối list (do tính chất giữ nguyên vị trí đang xem khi xoá ảnh)
        if(positionCurrent >= imageEventArrayList.size()){
            positionCurrent = imageEventArrayList.size() - 1;
        }

        //b. list ảnh được load lại chính là list ảnh của event (Lưu ở fragment update) nhưng đã xoá bớt ảnh khi bấm delete

        //c. set lại thông tin list ảnh cho ViewPagerAdapter | Vị trí current chính là vị trí vừa xoá
        ViewImageFragmentViewPagerAdapter adapter = new ViewImageFragmentViewPagerAdapter((FragmentActivity) view.getContext(), imageEventArrayList);
        viewPager_image.setAdapter(adapter);
        viewPager_image.setCurrentItem(positionCurrent);
    }
}