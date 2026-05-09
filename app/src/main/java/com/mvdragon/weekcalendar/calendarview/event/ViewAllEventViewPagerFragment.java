package com.mvdragon.weekcalendar.calendarview.event;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mvdragon.weekcalendar.R;
import com.mvdragon.weekcalendar.model.Event;
import com.mvdragon.weekcalendar.wiget.ThietKeViewPager;

import java.sql.SQLException;
import java.util.List;

public class ViewAllEventViewPagerFragment extends Fragment {
    private static View view;
    private static ViewPager2 viewPager_ViewAllEvent;

    public ViewAllEventViewPagerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_view_all_event_view_pager, container, false);

        //1. Ánh xạ
        AnhXa();

        //2. load ViewPager
        loadViewPagerAllEvent();


        return view;
    }

    //1. Ánh xạ
    private void AnhXa() {
        viewPager_ViewAllEvent = view.findViewById(R.id.viewPager_ViewAllEvent);
    }

    //2. load ViewPager
    public static void loadViewPagerAllEvent() {
        ViewAllEventViewPagerAdapter viewAllEventViewPagerAdapter = new ViewAllEventViewPagerAdapter((FragmentActivity) view.getContext());
        viewPager_ViewAllEvent.setAdapter(viewAllEventViewPagerAdapter);
        viewPager_ViewAllEvent.setOffscreenPageLimit(1);
        viewPager_ViewAllEvent.setUserInputEnabled(false); //swipe
    }

    //3. Load item của ViewPager
    public static void setCurrentItemCalendarViewPager(int number){
        viewPager_ViewAllEvent.setCurrentItem(number);
    }

}