package com.mvdragon.weekcalendar.notes;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.mvdragon.weekcalendar.notes.CreateNewNoteFragment;
import com.mvdragon.weekcalendar.notes.FolderFragment;
import com.mvdragon.weekcalendar.notes.UpdateNoteFragment;
import com.mvdragon.weekcalendar.notes.ViewFolderFragment;
import com.mvdragon.weekcalendar.notes.imagenote.ViewImageNoteViewPagerFragment;

public class NotesViewPagerAdapter extends FragmentStateAdapter {

    public NotesViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new FolderFragment();
            case 1:
                return new ViewFolderFragment();
            case 2:
                return new CreateNewNoteFragment();
            case 3:
                return new UpdateNoteFragment();
            case 4:
                return new ViewImageNoteViewPagerFragment();
            default:
                return new FolderFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
