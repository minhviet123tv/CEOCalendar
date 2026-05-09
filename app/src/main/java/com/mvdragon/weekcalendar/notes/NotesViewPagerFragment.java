package com.mvdragon.weekcalendar.notes;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mvdragon.weekcalendar.R;

public class NotesViewPagerFragment extends Fragment {
    public static View viewFmNote;
    public static ViewPager2 viewPager;

    public NotesViewPagerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewFmNote = inflater.inflate(R.layout.fragment_notes_view_pager, container, false);

        //1. Ánh xạ
        AnhXa();

        //2. Load ViewPager
        loadNotesViewPagerFragment();

        return viewFmNote;
    }


    //1. Ánh xạ
    private void AnhXa() {
        viewPager = viewFmNote.findViewById(R.id.viewPager_notes);
    }

    //2. Load ViewPager
    public static void loadNotesViewPagerFragment() {

        NotesViewPagerAdapter notesViewPagerAdapter = new NotesViewPagerAdapter((FragmentActivity) viewFmNote.getContext());
        viewPager.setAdapter(notesViewPagerAdapter);
        viewPager.setUserInputEnabled(false); // off swipe
        viewPager.setOffscreenPageLimit(1); // load trước fragment
    }

    //3. set current item
    public static void setCurrentItemNotes(int item){
        viewPager.setCurrentItem(item);
    }

}