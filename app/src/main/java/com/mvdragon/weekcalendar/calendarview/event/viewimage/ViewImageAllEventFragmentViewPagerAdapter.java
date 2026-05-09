package com.mvdragon.weekcalendar.calendarview.event.viewimage;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.mvdragon.weekcalendar.model.ImageEvent;

import java.util.List;

public class ViewImageAllEventFragmentViewPagerAdapter extends FragmentStateAdapter {

    private final List<ImageEvent> imageEventList;

    public ViewImageAllEventFragmentViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<ImageEvent> imageEventList) {
        super(fragmentActivity);
        this.imageEventList = imageEventList;
    }

    //Fragment của từng ViewPager
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        //Tạo fragment
        ViewImageAllEventFragment fragment = new ViewImageAllEventFragment();

        //Truyền thông tin ImageEvent qua bundle (Sẽ lấy để hiển thị tại fragment), position đúng vị trí đang view | Ở đây đang truyền ImageEvent chứa mình thông tin chứ không có ảnh, để tránh OverLoad dữ liệu
        Bundle bundle = new Bundle();
        bundle.putSerializable("ImageEvent", imageEventList.get(position));
        bundle.putInt("position", position);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public int getItemCount() {
        return imageEventList.size();
    }
}
