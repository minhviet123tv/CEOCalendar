package com.mvdragon.weekcalendar.menu;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.mvdragon.weekcalendar.MainActivity;
import com.mvdragon.weekcalendar.R;
import com.mvdragon.weekcalendar.database.UserUltils;
import com.mvdragon.weekcalendar.model.User;

public class MenuFragment extends Fragment {
    private static View view;
    private CardView layout_menu_card_falundafa_posts, layout_menu_card_meditation, layout_menu_card_calendarView, layout_menu_card_ratingApp, layout_menu_card_privacy_policy, layout_menu_card_coin;
    private static RewardedAd rewardedAd;

    public MenuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_menu, container, false);

        //1. Ánh xạ
        AnhXa();
//        loadCoinMenu();

        //2. Mở fragment link bài viết "Vì sao có nhân loại"
        layout_menu_card_falundafa_posts.setOnClickListener(v -> {
            MenuViewPagerFragment.setCurrentItemMenu(1); //Mở fragment bài viết
//            MainActivity.adView.setVisibility(View.GONE); //Ẩn quảng cáo -> Để tăng sự tập trung
        });

        //3. Menu link falundafa.org
        layout_menu_card_meditation.setOnClickListener(v -> {
            MenuViewPagerFragment.setCurrentItemMenu(2); //Mở trang chọn ngôn ngữ
//            MainActivity.adView.setVisibility(View.GONE); //Ẩn quảng cáo -> Để tăng sự tập trung
        });

        //4. Mở menu setting calendar
        layout_menu_card_calendarView.setOnClickListener(v -> {
            //Mở fragment
            MenuViewPagerFragment.setCurrentItemMenu(3);
        });

        //5. Xem chính sách bảo mật (Có thể làm thành fragment có link dẫn -> Hạn chế thoát khỏi app (tăng trải nghiệm dùng app) và vẫn load được quảng cáo
        layout_menu_card_privacy_policy.setOnClickListener(v -> {
            MenuViewPagerFragment.setCurrentItemMenu(4);
        });

        //6. Mở CH play rating hoặc update app
        layout_menu_card_ratingApp.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.mvdragon.weekcalendar"));
            startActivity(intent);
        });




        return view;
    }

    //11.1 Open dialog hiện xem ad để nhận coin
    public void openDialogAddCoin(int gravity){
        //1. Tạo dialog (Dùng dialog cách này để tao Window với giao diện đẹp và như ý hơn)
        Dialog dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //Không tiêu đề mặc định
        dialog.setContentView(R.layout.dialog_add_coin); // layout của dialog

        //2. Khai báo cửa sổ hiển thị (
        Window window = dialog.getWindow();
        if(window == null){
            return;
        }

        //Set kích thước (theo xml dialog đã tạo), set background
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //Khai báo và gán thuộc tính cho window
        WindowManager.LayoutParams windowAttribute = window.getAttributes();
        windowAttribute.gravity = gravity;
        window.setAttributes(windowAttribute);

        //3. Tắt dialog khi click bên ngoài
        dialog.setCancelable(true); //false: không tắt

        //4. Khai báo các thành phần trong dialog
        TextView txt_title_add_coin = dialog.findViewById(R.id.textView_title_add_coin);
        ImageView img_play_ad = dialog.findViewById(R.id.img_play_ad);
        ImageView img_cancel_add_coin = dialog.findViewById(R.id.img_cancel_add_coin);
        TextView txt_ad_not_load_notify = dialog.findViewById(R.id.txt_ad_not_load_notify);

        //Tạo title (Có màu) (Riêng ở fragment menu có thể chia trường hợp xem quảng cáo theo số lượng phiếu hiện có như <= 10 thì +3, >10 thì + 2. Hiện tại đang để +3)
        String text  = getResources().getText(R.string.watch_an_ad) + "<font color=#FF0000> +3 </font>";
        txt_title_add_coin.setText(Html.fromHtml(text));

        //5. Các hàm thực hiện khi bấm play
        img_play_ad.setOnClickListener(v -> {

            //Load quảng cáo và nhận thưởng
            loadRewardAd();

            //Nếu load thành công thì load nhận thưởng và đóng dialog
            if(rewardedAd != null) {
                showAndReceiveAdReward();
                dialog.cancel();

            //Nếu chưa load được thì thông báo
            } else {
                //Ẩn xong rồi mới hiện để tạo trực quan chuyển động cho mỗi lần bấm
                txt_ad_not_load_notify.setVisibility(View.GONE);

                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    txt_ad_not_load_notify.setVisibility(View.VISIBLE);
                }, 150);

            }
        });

        img_cancel_add_coin.setOnClickListener(v -> {
            dialog.cancel();
        });

        //12. show
        dialog.show();
    }

    //11.2 Load RewardAd (load quảng cáo tặng thưởng)
    public static void loadRewardAd() {

        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(view.getContext(), "ca-app-pub-3814279888521323/8802737029",
                adRequest, new RewardedAdLoadCallback() {

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error (Xử lý khi lỗi, chưa load xong)
                        rewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        //Nếu load thành công, gán RewardedAd
                        rewardedAd = ad;
                    }
                });
    }

    //11.3 Hiện quảng cáo và nhận thưởng sau khi xem
    private void showAndReceiveAdReward() {

        //Nếu load được quảng cáo thì thực hiện
        if (rewardedAd != null) {
            Activity activityContext = (Activity) view.getContext();

            //Hiện quảng cáo và nhận thưởng sau khi xem
            rewardedAd.show(activityContext, rewardItem -> {

                //a. Số, tên phần thưởng được trả về của admob
                int rewardAmount = rewardItem.getAmount();
//                String rewardType = rewardItem.getType(); //Nhận string sau khi xem nếu cần (ví dụ như đơn vị của phần thưởng)

                //b. Xử lý nhận về
                Toast.makeText(activityContext, "Received: +" + rewardAmount + " Coin", Toast.LENGTH_SHORT).show();
                //add vào shared
//                addCoin(rewardAmount);

            });
        }
    }

    //10.1 loadCoin
//    public static void loadCoinMenu() {
//        txt_coin_number_menu.setText(UserUltils.getUserLocal(view.getContext()).getMy_coin() + "");
//    }

    //10.2 Thay đổi coin trong Shared -> load lại hiển thị coin
    private void addCoin(int coinAdd){
        //load user
        User user = UserUltils.getUserLocal(view.getContext());
        user.setMy_coin(user.getMy_coin() + coinAdd);
        //save user
        UserUltils.saveUserLocal(view.getContext(), user);

        //Load lại number coin
//        loadCoinMenu();
    }


    //1. Ánh xạ
    private void AnhXa() {
        layout_menu_card_falundafa_posts = view.findViewById(R.id.layout_menu_card_falundafa_posts);
        layout_menu_card_meditation = view.findViewById(R.id.layout_menu_card_meditation);
        layout_menu_card_calendarView = view.findViewById(R.id.layout_menu_card_calendarView);
        layout_menu_card_ratingApp = view.findViewById(R.id.layout_menu_card_ratingApp);
        layout_menu_card_privacy_policy = view.findViewById(R.id.layout_menu_card_privacy_policy);

    }

}