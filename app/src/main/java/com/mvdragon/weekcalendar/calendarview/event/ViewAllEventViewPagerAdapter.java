package com.mvdragon.weekcalendar.calendarview.event;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.mvdragon.weekcalendar.calendarview.event.viewimage.ViewImageAllEventViewPagerFragment;
import com.mvdragon.weekcalendar.calendarview.event.viewimage.ViewImageEventViewPagerFragment;

public class ViewAllEventViewPagerAdapter extends FragmentStateAdapter {

    public ViewAllEventViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position){
            case 0:
                return new ListAllEventFragment();
            case 1:
                return new UpdateEventViewAllFragment();
            case 2:
                return new ViewImageAllEventViewPagerFragment();
            default:
                return new ListAllEventFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
