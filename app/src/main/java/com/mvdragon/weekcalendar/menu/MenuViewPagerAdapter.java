package com.mvdragon.weekcalendar.menu;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MenuViewPagerAdapter extends FragmentStateAdapter {

    public MenuViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position){
            case 0:
                return new MenuFragment();
            case 1:
                return new MenuHowHumankindCameToBeFragment();
            case 2:
                return new MenuMeditationFragment();
            case 3:
                return new SettingCalendarFragment();
            case 4:
                return new MenuPrivacyPolicyFragment();
            default:
                return new MenuFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
