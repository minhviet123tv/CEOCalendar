package com.mvdragon.weekcalendar.menu;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.mvdragon.weekcalendar.MainActivity;
import com.mvdragon.weekcalendar.R;

public class MenuHowHumankindCameToBeFragment extends Fragment {
    private View view;
    private TextView how_humankind_came_to_be_content;
    private ImageButton ic_back_menu_privacy_policy_fm;
    private ImageButton ibtn_copy_content_post;

    public MenuHowHumankindCameToBeFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_menu_why_there_is_mankind, container, false);

        //1. Ánh xạ
        AnhXa();

        //2. set Content
        how_humankind_came_to_be_content.setText(Html.fromHtml("" + how_humankind_came_to_be_content.getText()));
        how_humankind_came_to_be_content.setMovementMethod(LinkMovementMethod.getInstance());

        //3. Back
        ic_back_menu_privacy_policy_fm.setOnClickListener(v -> {
            MainActivity.adView.setVisibility(View.VISIBLE); //Hiện lại quảng cáo
            MenuViewPagerFragment.setCurrentItemMenu(0); // Chuyển về fragment 0
        });

        //4. Copy content
        ibtn_copy_content_post.setOnClickListener(v -> {
            String inputText = how_humankind_came_to_be_content.getText().toString().trim(); //"https://en.minghui.org/html/articles/2023/1/21/206699.html"
            ClipboardManager clipboardManager = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("MyCopyData", inputText);
            clipboardManager.clearPrimaryClip();
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(view.getContext(), R.string.copied_content, Toast.LENGTH_SHORT).show();
        });

        return view;
    }


    //1. Ánh xạ
    private void AnhXa() {
        how_humankind_came_to_be_content = view.findViewById(R.id.how_humankind_came_to_be_content);
        ic_back_menu_privacy_policy_fm = view.findViewById(R.id.ic_back_menu_privacy_policy_fm);
        ibtn_copy_content_post = view.findViewById(R.id.ibtn_copy_content_post);
    }
}