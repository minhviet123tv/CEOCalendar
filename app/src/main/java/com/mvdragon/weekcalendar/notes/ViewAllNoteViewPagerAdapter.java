package com.mvdragon.weekcalendar.notes;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewAllNoteViewPagerAdapter extends FragmentStateAdapter {

    public ViewAllNoteViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new ListAllNotesFragment();
            case 1:
                return new UpdateNoteViewAllFragment();
            default:
                return new ListAllNotesFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
