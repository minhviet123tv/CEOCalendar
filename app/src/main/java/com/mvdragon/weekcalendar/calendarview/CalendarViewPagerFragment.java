package com.mvdragon.weekcalendar.calendarview;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mvdragon.weekcalendar.R;

public class CalendarViewPagerFragment extends Fragment {
    public static View viewCalendar;
    public static ViewPager2 viewPager_calendar;

    public CalendarViewPagerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewCalendar = inflater.inflate(R.layout.fragment_calendar_view_pager, container, false);

        //1. Ánh xạ
        AnhXa();

        //2. load ViewPager calendar
        loadViewPagerCalendar();


        return viewCalendar;
    }


    //1. Ánh xạ
    private void AnhXa() {
        viewPager_calendar = viewCalendar.findViewById(R.id.viewPager_calendar);
    }

    //2. load ViewPager calendar
    public static void loadViewPagerCalendar() {

        //a. Cài đặt ViewPager
        CalendarViewPagerAdapter calendarViewPagerAdapter = new CalendarViewPagerAdapter((FragmentActivity) viewCalendar.getContext());
        viewPager_calendar.setAdapter(calendarViewPagerAdapter);
        viewPager_calendar.setUserInputEnabled(false); //swipe màn hình để chuyển trang ViewPager

        //b. Cài đặt số lượng fagment sẽ load trước (theo chiều hướng click sử dụng) | Mặc định là 1 hoặc 2 (không thể để 0, có thể không vượt quá fragment có trong adapter)
        viewPager_calendar.setOffscreenPageLimit(1);

        //c. Hướng chuyển
//        viewPager_calendar.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

        //Hiệu ứng chuyển (nếu có - phải tạo thêm class)
//        viewPager_calendar.setPageTransformer(null);

    }

    //3. Load item của ViewPager calendar
    public static void setCurrentItemCalendarViewPager(int number){
        viewPager_calendar.setCurrentItem(number);
    }

}