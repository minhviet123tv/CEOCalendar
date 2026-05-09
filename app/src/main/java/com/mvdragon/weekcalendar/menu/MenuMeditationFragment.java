package com.mvdragon.weekcalendar.menu;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.mvdragon.weekcalendar.MainActivity;
import com.mvdragon.weekcalendar.R;

public class MenuMeditationFragment extends Fragment {
    private View view;
    private TextView how_humankind_came_to_be_content;
    private ImageButton ic_back_menu_privacy_policy_fm;

    public MenuMeditationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_menu_meditation, container, false);

        //1. Ánh xạ
        AnhXa();

        //2. set Content
        how_humankind_came_to_be_content.setText(Html.fromHtml("" + how_humankind_came_to_be_content.getText()));
        how_humankind_came_to_be_content.setMovementMethod(LinkMovementMethod.getInstance());

        //3. Back về list menu
        ic_back_menu_privacy_policy_fm.setOnClickListener(v -> {
            MainActivity.adView.setVisibility(View.VISIBLE); //Hiện lại quảng cáo
            MenuViewPagerFragment.setCurrentItemMenu(0);
        });

        return view;
    }


    //1. Ánh xạ
    private void AnhXa() {
        how_humankind_came_to_be_content = view.findViewById(R.id.how_humankind_came_to_be_content);
        ic_back_menu_privacy_policy_fm = view.findViewById(R.id.ic_back_menu_privacy_policy_fm);
    }
}