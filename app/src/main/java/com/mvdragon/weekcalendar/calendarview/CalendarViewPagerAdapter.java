package com.mvdragon.weekcalendar.calendarview;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.mvdragon.weekcalendar.calendarview.event.AddEventFragment;
import com.mvdragon.weekcalendar.calendarview.event.UpdateEventFragment;
import com.mvdragon.weekcalendar.calendarview.event.viewimage.ViewImageEventViewPagerFragment;

public class CalendarViewPagerAdapter extends FragmentStateAdapter {

    public CalendarViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new WeekViewFragment();
            case 1:
                return new AddEventFragment();
            case 2:
                return new UpdateEventFragment();
            case 3:
                return new ViewImageEventViewPagerFragment();
            default:
                return new WeekViewFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }

}
