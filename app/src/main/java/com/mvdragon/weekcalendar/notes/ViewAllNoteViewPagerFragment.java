package com.mvdragon.weekcalendar.notes;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mvdragon.weekcalendar.R;

public class ViewAllNoteViewPagerFragment extends Fragment {
    private View view;
    public static ViewPager2 viewPager_ViewAllNotes;

    public ViewAllNoteViewPagerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_view_all_note_view_pager, container, false);

        //1. Ánh xạ
        AnhXa();

        //2. Load ViewPager
        loadViewPagerAllNotes();



        return view;
    }



    //1. Ánh xạ
    private void AnhXa() {
        viewPager_ViewAllNotes = view.findViewById(R.id.viewPager_ViewAllNotes);
    }

    //2. Load ViewPager
    private void loadViewPagerAllNotes() {
        ViewAllNoteViewPagerAdapter viewAllNoteViewPagerAdapter = new ViewAllNoteViewPagerAdapter((FragmentActivity) view.getContext());
        viewPager_ViewAllNotes.setAdapter(viewAllNoteViewPagerAdapter);
        viewPager_ViewAllNotes.setOffscreenPageLimit(1);
        viewPager_ViewAllNotes.setUserInputEnabled(false);
    }

    //3. set Current Item
    public static void setCurrentItemAllNoteViewPager(int item){
        viewPager_ViewAllNotes.setCurrentItem(item);
    }
}