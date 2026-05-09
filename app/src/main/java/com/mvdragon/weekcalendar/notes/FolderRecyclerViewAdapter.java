package com.mvdragon.weekcalendar.notes;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apachat.swipereveallayout.core.SwipeLayout;
import com.apachat.swipereveallayout.core.ViewBinder;
import com.mvdragon.weekcalendar.CalendarUtils;
import com.mvdragon.weekcalendar.R;
import com.mvdragon.weekcalendar.database.TruyVanDuLieuSQLite;
import com.mvdragon.weekcalendar.database.UserUltils;
import com.mvdragon.weekcalendar.model.MyFolder;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class FolderRecyclerViewAdapter extends RecyclerView.Adapter<FolderRecyclerViewAdapter.FolderViewHolder>{
    private Context context;
    private ArrayList<MyFolder> myFolderArrayList;
    private ViewBinder viewBinder = new ViewBinder(); //swipe layout (của apachat)
    private TruyVanDuLieuSQLite truyVanDuLieuSQLite;
    private IClickFolderItemListener clickViewFolderItem, iClickUpdateFolderItem;

    //I. Hàm khởi tạo
    public FolderRecyclerViewAdapter(Context context, ArrayList<MyFolder> myFolderArrayList, IClickFolderItemListener clickViewFolderItem, IClickFolderItemListener iClickUpdateFolderItem) {
        this.context = context;
        this.myFolderArrayList = myFolderArrayList;
        this.clickViewFolderItem = clickViewFolderItem;
        this.iClickUpdateFolderItem = iClickUpdateFolderItem;

        truyVanDuLieuSQLite = new TruyVanDuLieuSQLite(context);
    }

    //II. View
    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder, parent, false);
        return new FolderViewHolder(view);
    }

    //III. On item
    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder holder, int position) {
        MyFolder myFolder = myFolderArrayList.get(position);

        //1.1 Gán tên
        holder.txt_folder_name.setText(myFolder.getFolder_name());

        //2. Gán thời gian (Đổi định dạng từ SQLite -> LocalDateTime -> String) theo định dạng time của user
        LocalDateTime localDateTime = LocalDateTime.parse(myFolder.getCreate_datetime());
        String dateTimeString = CalendarUtils.formattedDateTimeOnlyDate(localDateTime, UserUltils.getUserLocal(context).getDateFormat());
        holder.txt_folder_name_datetime.setText(dateTimeString);

        //* Khai báo ViewBinder (cho swipe layout): truyền layout swipe, id của product
        viewBinder.bind(holder.swipeLayout, String.valueOf(myFolder.getId_folder()));

        //3. Swipe delete folder
        holder.layout_swipe_delete_folder.setOnClickListener(v -> {

            //Mở dialog hỏi xoá folder -> thực hiện hàm bên trong nếu chọn yes
            holder.dialogDeleteOneFolder(myFolder, Gravity.CENTER);
            //Đóng layout swipe sau khi click
            holder.swipeLayout.close(true);

        });

        //4. Hàm swipe edit folder name
        holder.layout_swipe_edit_folder.setOnClickListener(v -> {

            //Truyền dữ liệu ra hàm bên ngoài class adapter
            iClickUpdateFolderItem.onClickItemFolder(myFolder);

            //Đóng layout swipe sau khi click
            holder.swipeLayout.close(true);
        });

        //5. Click mở (fragment) view folder

        //a. Click vào icon -> mở viewFolder (hàm ngoài class adapter)
        holder.img_ic_folder.setOnClickListener(v -> {
            clickViewFolderItem.onClickItemFolder(myFolder);
        });

        //b. Click vào tên -> mở viewFolder (hàm ngoài class adapter)
        holder.txt_folder_name.setOnClickListener(v -> {
            clickViewFolderItem.onClickItemFolder(myFolder);
        });

        //c. Click vào text thời gian -> mở viewFolder (hàm ngoài class adapter)
        holder.txt_folder_name_datetime.setOnClickListener(v -> {
            clickViewFolderItem.onClickItemFolder(myFolder);
        });

    }

    //IV. Size
    @Override
    public int getItemCount() {
        return myFolderArrayList.size();
    }

    //V. ViewHolder
    class FolderViewHolder extends RecyclerView.ViewHolder{
        private ImageView img_ic_folder;
        private TextView txt_folder_name, txt_folder_name_datetime;
        private SwipeLayout swipeLayout;
        private RelativeLayout layout_swipe_edit_folder, layout_swipe_delete_folder;

        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);

            //1. Ánh xạ
            AnhXa();
        }

        //1. Ánh xạ
        private void AnhXa() {
            img_ic_folder = itemView.findViewById(R.id.img_ic_folder);
            txt_folder_name = itemView.findViewById(R.id.txt_folder_name);
            txt_folder_name_datetime = itemView.findViewById(R.id.txt_folder_name_datetime);

            swipeLayout = itemView.findViewById(R.id.layout_item_folder);
            layout_swipe_edit_folder = itemView.findViewById(R.id.layout_swipe_edit_folder);
            layout_swipe_delete_folder = itemView.findViewById(R.id.layout_swipe_delete_folder);
        }

        //2.1 Dialog hỏi xoá folder
        private void dialogDeleteOneFolder(MyFolder myFolder, int gravity) {

            //1. Tạo dialog (Dùng dialog cách này để tao Window với giao diện đẹp và như ý hơn)
            Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //Không tiêu đề mặc định
            dialog.setContentView(R.layout.dialog_delete_all_event); // layout của dialog

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

            //Tắt dialog khi click bên ngoài
            dialog.setCancelable(true); //false: không tắt

            //4. Khai báo các thành phần trong dialog
            TextView txt_title_delete = dialog.findViewById(R.id.textView_title_delete_all_event);
            Button btn_yes_delete = dialog.findViewById(R.id.btn_yes_delete);
            Button btn_no_delete = dialog.findViewById(R.id.btn_no_delete);

            //Đổi lại tiêu đề
            txt_title_delete.setText(R.string.delete_this_folder);

            //5. Click YES
            btn_yes_delete.setOnClickListener(v -> {
                //Gọi hàm xoá folder
                deleteOneFolder(myFolder);
                dialog.dismiss();
            });

            //6. Click NO
            btn_no_delete.setOnClickListener(v -> {
                dialog.cancel();
            });

            //7. show
            dialog.show();

        }

        //2.2 Delete one folder
        public void deleteOneFolder(MyFolder myFolder) {
            //Xoá vị trí trong list (đang dùng để hiển thị) tương ứng vị trí được chọn
            myFolderArrayList.remove(getLayoutPosition());
            //Xoá event và cập nhật trong rcv
            notifyItemRemoved(getLayoutPosition());
            //Xoá folder trong SQLite cùng các folder đi kèm (truyền mình id_folder là đủ thực hiện, không truyền cả myFolder để giảm tải khi truyền)
            truyVanDuLieuSQLite.deleteOneFolder(myFolder.getId_folder());
            //Xoá dữ liệu notes bên trong và ảnh của folder (sẽ cập nhật sau)
        }

    }

    //interface truyền dữ liệu
    public interface IClickFolderItemListener{
        void onClickItemFolder(MyFolder myFolder);
    }
}
