package com.mvdragon.weekcalendar.menu;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mvdragon.weekcalendar.R;

public class MenuViewPagerFragment extends Fragment {
    public static View viewFmMenu;
    public static ViewPager2 viewPager;

    public MenuViewPagerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewFmMenu = inflater.inflate(R.layout.fragment_menu_view_pager, container, false);

        //1. Ánh xạ
        AnhXa();

        //2. load viewPager menu
        loadMenuViewPagerFragment();

        return viewFmMenu;
    }


    //1. Ánh xạ
    private void AnhXa() {
        viewPager = viewFmMenu.findViewById(R.id.viewPager_menu);
    }

    //2. load viewPager menu
    public static void loadMenuViewPagerFragment() {
        MenuViewPagerAdapter menuViewPagerAdapter = new MenuViewPagerAdapter((FragmentActivity) viewFmMenu.getContext());
        viewPager.setAdapter(menuViewPagerAdapter);
        viewPager.setUserInputEnabled(false); //swipe
        viewPager.setOffscreenPageLimit(1); //load trước số lượng fragment
    }

    //3. set current item
    public static void setCurrentItemMenu(int item){
        viewPager.setCurrentItem(item);
    }

}