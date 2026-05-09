package com.mvdragon.weekcalendar.notes.imagenote;

import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.mvdragon.weekcalendar.calendarview.event.viewimage.ViewImageFragment;
import com.mvdragon.weekcalendar.model.ImageEvent;
import com.mvdragon.weekcalendar.model.ImageNote;

import java.util.List;

public class ViewImageNoteFragmentViewPagerAdapter extends FragmentStateAdapter {

    private List<ImageNote> imageNoteList;

    public ViewImageNoteFragmentViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<ImageNote> imageNoteList) {
        super(fragmentActivity);
        this.imageNoteList = imageNoteList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        //Tạo ViewImageNoteFragment
        ViewImageNoteFragment fragment = new ViewImageNoteFragment();

        //Truyền thông tin ImageEvent qua bundle (Sẽ lấy để hiển thị tại fragment), position đúng vị trí đang view | Ở đây đang truyền ImageNote chứa mình thông tin chứ không có ảnh, để tránh OverLoad dữ liệu
        Bundle bundle = new Bundle();
        bundle.putSerializable("ImageNote", imageNoteList.get(position));
        bundle.putInt("position", position);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public int getItemCount() {
        return imageNoteList.size();
    }
}
