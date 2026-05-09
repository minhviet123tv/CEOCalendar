package com.mvdragon.weekcalendar.menu;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mvdragon.weekcalendar.R;

public class MenuPrivacyPolicyFragment extends Fragment {
    private View view;
    private TextView privacy_policy_content;
    private ImageButton ic_back_menu_privacy_policy_fm;

    public MenuPrivacyPolicyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_menu_privacy_policy, container, false);

        //1. Ánh xạ
        AnhXa();

        //2. set Content
        privacy_policy_content.setText(Html.fromHtml("" + privacy_policy_content.getText()));
        privacy_policy_content.setMovementMethod(LinkMovementMethod.getInstance());

        //3. Back
        ic_back_menu_privacy_policy_fm.setOnClickListener(v -> {
            MenuViewPagerFragment.setCurrentItemMenu(0);
        });

        return view;
    }


    //1. Ánh xạ
    private void AnhXa() {
        privacy_policy_content = view.findViewById(R.id.privacy_policy_content);
        ic_back_menu_privacy_policy_fm = view.findViewById(R.id.ic_back_menu_privacy_policy_fm);
    }
}