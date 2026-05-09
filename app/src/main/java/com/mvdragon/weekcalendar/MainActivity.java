package com.mvdragon.weekcalendar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mvdragon.weekcalendar.calendarview.WeekViewFragment;
import com.mvdragon.weekcalendar.database.TruyVanDuLieuSQLite;
import com.mvdragon.weekcalendar.database.UserUltils;
import com.mvdragon.weekcalendar.calendarview.event.ListAllEventFragment;
import com.mvdragon.weekcalendar.menu.SettingCalendarActivity;
import com.mvdragon.weekcalendar.model.User;
import com.mvdragon.weekcalendar.notes.FolderFragment;
import com.mvdragon.weekcalendar.notes.ViewFolderFragment;
import com.mvdragon.weekcalendar.wiget.ThietKeViewPager;

import java.sql.SQLException;
import java.time.LocalDate;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout layout_banner_view;
    private static ThietKeViewPager viewPager_main;
    public static BottomNavigationView bottom_navigation;
    private int loginNumber;
    private TruyVanDuLieuSQLite truyVanDuLieuSQLite;
    public static AdView adView;
    private InAppUpdate inAppUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //1. Ánh xạ
        AnhXa();

        //2. Count login (Thực hiện tại main để đảm bảo đúng số lần vào app chứ không phải ở fragment có thể sẽ load lại nhiều lần trong khi sử dụng)
        CountLogin();

        //3.1 load ViewPager
        loadViewPagerMain();

        //3.2 Load activity: Load dữ liệu khi mở app
        loadActivity();

        //4. Set Google Admob And Banner
        setGoogleAdmobAndBanner();

        //5. Các sự kiện khi click vào icon menu bottom
        setupMenuBottomClick();

        /* Initialize the Google Mobile Ads SDK on a background thread. */
        new Thread(() -> {
            MobileAds.initialize(this, initializationStatus -> {
            });
        }).start();

    }


    //5. Set Current item of ViewPager
    public static void setCurrentItemViewPager(int numberFragment) {
        viewPager_main.setCurrentItem(numberFragment);
        setMenuFolowFragment(numberFragment);
    }

    //4.3 Lưu fragment sử dụng cuối của user
    public void updateFragmentUser(int fragment_user) {

        //Lưu user với thứ tự fragment khi bấm vào menu tương ứng
        User user = UserUltils.getUserLocal(this);
        user.setFragment_user(fragment_user);
        UserUltils.saveUserLocal(this, user);
    }

    //4.2 Set menu tương ứng với vị trí của fragment
    public static void setMenuFolowFragment(int numberFragment) {

        switch (numberFragment) {
            case 0: //case 0: Menu calendar
                bottom_navigation.getMenu().findItem(R.id.menu_calendar).setChecked(true);
                break;
            case 1: //case 1: Menu View All Event
                bottom_navigation.getMenu().findItem(R.id.menu_view_all_event).setChecked(true);
                break;
            case 2: //case 2: Menu notes
                bottom_navigation.getMenu().findItem(R.id.menu_notes).setChecked(true);
                break;
            case 3: //case 4: Menu menu
                bottom_navigation.getMenu().findItem(R.id.menu_menu).setChecked(true);
                break;
            default:
                bottom_navigation.getMenu().findItem(R.id.menu_calendar).setChecked(true);
                break;
        }

    }

    //4.1 Các sự kiện khi click vào icon menu bottom -> Khớp fragment với menu, set hiệu ứng (chuyển lại ngay khi tới trang đích), lưu shared fragment cuối
    private void setupMenuBottomClick() {

        Handler handler = new Handler();

        bottom_navigation.setOnItemSelectedListener(item -> {

            //Hiện quảng cáo khi bấm lại vào menu bất kỳ
            adView.setVisibility(View.VISIBLE);

            //A. Khi click vào menu calendar
            if (item.getItemId() == R.id.menu_calendar) {

                //set current của ViewPager Main
                viewPager_main.setCurrentItem(0);

                //set lại ViewPager của menu này (Delay để chờ nó load main trước, nếu không sẽ bị lỗi)
                handler.postDelayed(() -> {
//                    CalendarViewPagerFragment.setCurrentItemCalendarViewPager(0);
                    try {
                        WeekViewFragment.setWeekView();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }, 300);

                //Update fragment_user (fragment trong ViewPager sử dụng cuối)
                updateFragmentUser(0);

            }

            //B. Khi click vào menu view All Event
            else if (item.getItemId() == R.id.menu_view_all_event) {

                //set current của ViewPager Main
                viewPager_main.setCurrentItem(1);

                //set lại list event của fragment trang chủ (Delay để chờ nó load main trước, nếu không có thể sẽ bị lỗi)
                handler.postDelayed(() -> {
                    //Khai báo list sẽ sử dụng của fragment sẽ mở
                    ListAllEventFragment.listAllEventSQLite = truyVanDuLieuSQLite.getListAllEvent();
                    try {
                        ListAllEventFragment.setEventAdpater();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }, 200);

                //Update fragment_user (fragment trong ViewPager sử dụng cuối)
                updateFragmentUser(1);
            }

            //C. Khi click vào menu notes
            else if (item.getItemId() == R.id.menu_notes) {

                //set current của ViewPager Main
                viewPager_main.setCurrentItem(2);

                //set lại ViewPager của menu này (Delay để chờ nó load main trước, nếu không sẽ bị lỗi)
                handler.postDelayed(() -> {
//                    NotesViewPagerFragment.setCurrentItemNotes(0);
                    FolderFragment.LoadRecyclerViewAllFolder();
                    ViewFolderFragment.LoadRecyclerViewAllNotes();
                }, 300);

                //Update fragment_user (fragment trong ViewPager sử dụng cuối)
                updateFragmentUser(2);
            }

            //D. Khi click menu menu -> set current của viewPager là 5
            else if (item.getItemId() == R.id.menu_menu) {
                //set current của ViewPager Main
                viewPager_main.setCurrentItem(3);

                //Update fragment_user (fragment trong ViewPager sử dụng cuối)
                updateFragmentUser(3);
            }

            return true;
        });
    }

    //3.2 Load activity: Load dữ liệu khi mở app
    private void loadActivity() {
        //a. Luôn cài đặt ngày đang được chọn là ngày hôm nay khi mở app
        CalendarUtils.selectedDate = LocalDate.now();

        //b. Load ViewPager tương ứng fragment_user lưu local (khi mới mở app)
        User user = UserUltils.getUserLocal(this);
        setCurrentItemViewPager(user.getFragment_user());

        //c. Chống lag khi chuyển màn hình nhiều theo stackoverflow
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    //3.1 Load ViewPager: Cài đặt ViewPager và các thông số (như hiệu ứng, chiều chuyển động) -> Cần cài đặt lại adapter cho mỗi lần thay đổi
    public void loadViewPagerMain() {

        //a. Cài đặt ViewPager
        ViewPagerMainAdapter viewPagerMainAdapter = new ViewPagerMainAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager_main.setAdapter(viewPagerMainAdapter);
        viewPager_main.setEnableSwipe(false); //swipe màn hình để chuyển trang ViewPager

        //b. Cài đặt số lượng fagment sẽ load trước (theo chiều hướng click sử dụng) | Mặc định là 1 (Hình như ít nhất là 1, không thể để 0)
        viewPager_main.setOffscreenPageLimit(1);

        //c. Hiệu ứng chuyển giữa các fragment
//        viewPager_main.setPageTransformer(new ZoomOutPageTransformer());

    }

    //2. CountLogin
    private void CountLogin() {

        //a. Lấy user local để lấy số đếm hiện tại: truyền context
        User user = UserUltils.getUserLocal(this);

        //b. Khai báo số countLogin, lấy giá trị (Vì có chỗ đặt giá trị mặc định khi lấy từ Shared nên cũng tương đương khai báo)
        if (user != null) {

            loginNumber = user.getCountLogin();

            //Tăng 1 lần đếm cho loginNumber
            loginNumber++;

            //Thông báo số lần đăng nhập (Dùng để test)
//            Toast.makeText(this, loginNumber + "", Toast.LENGTH_SHORT).show();

            //Cập nhật user vào Shared (Có thể + cập nhật chính nó luôn nếu muốn)
            user.setCountLogin(loginNumber);

            //Mở setting nếu vào lần 1 (Mới mở app)
            if (loginNumber == 1) {
                Intent intent = new Intent(this, SettingCalendarActivity.class);
                startActivity(intent);
            }

        } else {
            loginNumber = 10;
        }

        //c. Cập nhật user vào Shared
        UserUltils.saveUserLocal(this, user);

        //remove (dùng để test)
//       UserUltils.resetUserLocal(this);

    }

    //1.1 Ánh xạ
    private void AnhXa() {
        viewPager_main = findViewById(R.id.viewPager_main);
        bottom_navigation = findViewById(R.id.bottom_navigation);

        layout_banner_view = findViewById(R.id.layout_banner_view);

        adView = findViewById(R.id.adView_main);

        truyVanDuLieuSQLite = new TruyVanDuLieuSQLite(this);

        inAppUpdate = new InAppUpdate(this);
        inAppUpdate.checkForAppUpdate();
    }

    //A. Đặt các hàm cập nhật app khi có phiên bản mới (Có thể hiện nay CH Play tự có hiện update trong app, nhưng ở đây là đang tự đặt hàm)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        inAppUpdate.onActivityResult(requestCode, resultCode);
    }

    //onResume activity
    @Override
    protected void onResume() {
        super.onResume();

        //In app update
        inAppUpdate.onResume();

        //Load google admod
        AddGoogleAdmob();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        inAppUpdate.onDestroy();
    }

    //B.1 setGoogleAdmobAndBanner
    private void setGoogleAdmobAndBanner() {
        //Khai báo admob và banner tự cài
        AddGoogleAdmob();
        ShowOnlyAdmob();

        //Hiện banner view (để chụp màn hình cho store)
//        if(loginNumber < 2){
//            ShowBannerView();
//        }
    }

    //B.2. Cài đặt banner quảng cáo admob
    private void AddGoogleAdmob() {
        MobileAds.initialize(this, initializationStatus -> {
        });

        @SuppressLint("VisibleForTests") AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    //B.3 Show banner Admob
    private void ShowOnlyAdmob() {
        adView.setVisibility(View.VISIBLE); //Hiện admob
        layout_banner_view.setVisibility(View.GONE); //Ẩn banner view
    }

    //B.4 Show only banner view
    private void ShowBannerView() {
        adView.setVisibility(View.GONE); //Hiện admob
        layout_banner_view.setVisibility(View.VISIBLE); //Ẩn banner view
    }

    //C. Kiểm tra xem đang có mạng internet hay không
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

}