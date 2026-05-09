package com.mvdragon.weekcalendar;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.mvdragon.weekcalendar.calendarview.CalendarViewPagerFragment;
import com.mvdragon.weekcalendar.calendarview.event.ViewAllEventViewPagerFragment;
import com.mvdragon.weekcalendar.menu.MenuViewPagerFragment;
import com.mvdragon.weekcalendar.notes.NotesViewPagerFragment;
import com.mvdragon.weekcalendar.notes.ViewAllNoteViewPagerFragment;

public class ViewPagerMainAdapter extends FragmentStatePagerAdapter {

    public ViewPagerMainAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new CalendarViewPagerFragment();
            case 1:
                return new ViewAllEventViewPagerFragment();
            case 2:
                return new NotesViewPagerFragment();
            case 3:
                return new MenuViewPagerFragment();
            default:
                return new CalendarViewPagerFragment();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    //Xoá bộ nhớ đệm cho ViewPager (khỏi bị quá tải gây lag khi sử dụng nhiều ViewPager, nhiều fragment)
    @Override
    public Parcelable saveState() {
        Bundle bundle = (Bundle) super.saveState();

        assert bundle != null;
        bundle.putParcelableArray("states", null); // Never maintain any states from the base class, just null it out
        return bundle;
    }
}
