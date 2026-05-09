package com.mvdragon.weekcalendar.notes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apachat.swipereveallayout.core.SwipeLayout;
import com.apachat.swipereveallayout.core.ViewBinder;
import com.mvdragon.weekcalendar.CalendarUtils;
import com.mvdragon.weekcalendar.R;
import com.mvdragon.weekcalendar.database.TruyVanDuLieuSQLite;
import com.mvdragon.weekcalendar.database.UserUltils;
import com.mvdragon.weekcalendar.model.MyNote;
import com.mvdragon.weekcalendar.model.MyNoteAvatar;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ViewFolderRecyclerViewAdapter extends RecyclerView.Adapter<ViewFolderRecyclerViewAdapter.ViewFolderViewHolder>{
    private Context context;
    private ArrayList<MyNote> myNoteArrayList;
    private TruyVanDuLieuSQLite truyVanDuLieuSQLite;
    private ViewBinder viewBinder =  new ViewBinder();

    //I. Hàm khởi tạo
    public ViewFolderRecyclerViewAdapter(Context context, ArrayList<MyNote> myNoteArrayList) {
        this.context = context;
        this.myNoteArrayList = myNoteArrayList;

        truyVanDuLieuSQLite = new TruyVanDuLieuSQLite(context);
    }

    //II. Khai báo view layout
    @NonNull
    @Override
    public ViewFolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new ViewFolderViewHolder(view);
    }

    //III. Set item
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull ViewFolderViewHolder holder, int position) {
        MyNote myNote = myNoteArrayList.get(position);

        //1. Gán giá trị cho item
        holder.setItemNote(myNote);

        //2. Layout swipe
        viewBinder.bind(holder.swipeLayout, String.valueOf(myNote.getId_note()));

        //2.1 Xoá avatar của một note
        holder.layout_swipe_delete_note_avatar.setOnClickListener(v -> {
            //Xoá trong CSDL
            truyVanDuLieuSQLite.deleteOneMyNoteAvatar(myNote.getId_note());
            //Cập nhật lại rcv (Tương đương set lại item | Vì note này vẫn tồn tại nên vẫn có trong list và chỉ mất đi avatar, mà avatar đã tự lấy trong setItem)
            notifyDataSetChanged();

            //Đóng layout swipe
            holder.swipeLayout.close(true);
        });

        //2.2 Xoá một item note
        holder.layout_swipe_delete_note_item.setOnClickListener(v -> {
            //Xoá vị trí trong list (đang dùng) tương ứng vị trí được chọn
            myNoteArrayList.remove(holder.getLayoutPosition());
            //Cập nhật trong rcv đã hiển thị (với vị trí đã tương tác xoá)
            notifyItemRemoved(holder.getLayoutPosition());
            //Xoá note trong SQLite (bao gồm cả avatar trong hàm)
            truyVanDuLieuSQLite.deleteOneMyNote(myNote.getId_note());

            //Không cần notifyDataSetChanged nữa vì đã làm xong hết các thứ cần làm
        });

        //3. Sự kiện khi click vào text, avatar, text time ở item -> Mở view note
        holder.txt_note_name.setOnClickListener(v -> {
            holder.openUpdateNote(myNote);
        });
        //Khi click vào ảnh avatar -> Mở view update note
        holder.img_note_avatar.setOnClickListener(v -> {
            holder.openUpdateNote(myNote);
        });
        //Khi click vào ảnh text time -> Mở view update note
        holder.txt_note_time.setOnClickListener(v -> {
            holder.openUpdateNote(myNote);
        });

    }

    //IV. Số lượng item
    @Override
    public int getItemCount() {
        return myNoteArrayList.size();
    }

    //V. ViewHolder
    class ViewFolderViewHolder extends RecyclerView.ViewHolder{
        private TextView txt_note_name, txt_note_time;
        private ImageView img_note_avatar;
        private RelativeLayout layout_swipe_delete_note_avatar, layout_swipe_delete_note_item;
        private SwipeLayout swipeLayout;

        public ViewFolderViewHolder(@NonNull View itemView) {
            super(itemView);

            //1. Ánh xạ
            AnhXa(itemView);
        }

        //1. Ánh xạ
        private void AnhXa(View itemView) {
            txt_note_name = itemView.findViewById(R.id.textView_note_name);
            txt_note_time = itemView.findViewById(R.id.txt_note_time);
            img_note_avatar = itemView.findViewById(R.id.img_note_avatar);
            layout_swipe_delete_note_avatar = itemView.findViewById(R.id.layout_swipe_delete_note_avatar);
            layout_swipe_delete_note_item = itemView.findViewById(R.id.layout_swipe_delete_note_item);
            swipeLayout = itemView.findViewById(R.id.layout_item_note);
        }

        //2. Gán giá trị cho item
        public void setItemNote(MyNote myNote) {

            //a. Tên của note
            txt_note_name.setText(myNote.getNote_name());

            //b. Lấy avatar của Note từ SQLite. Nếu có avatar thì set cho ảnh của item, nếu không thì ẩn widget ảnh
            MyNoteAvatar myNoteAvatar = truyVanDuLieuSQLite.getMyNoteAvatar(myNote.getId_note());

            byte [] avatar = myNoteAvatar.getNote_avatar();
            if(avatar != null){
                Bitmap bitmap = BitmapFactory.decodeByteArray(avatar, 0, avatar.length);
                img_note_avatar.setImageBitmap(bitmap);
            } else {
                //set null nếu không có avatar (không đặt ẩn visibility vì có thể bị lag ẩn cả item khác)
//                img_note_avatar.setImageResource(0);
                img_note_avatar.setImageResource(android.R.color.transparent);
//                img_note_avatar.setImageBitmap(null);
            }

            //c. time: Chuyển String datetime từ CSDL -> LocalDateTime -> lấy String theo format của user
            LocalDateTime localDateTime = LocalDateTime.parse(myNote.getCreate_datetime());
            String time = CalendarUtils.formattedDateTimeOnlyDate(localDateTime, UserUltils.getUserLocal(context).getDateFormat());
            txt_note_time.setText(time);
        }

        //3. Mở fragment update của ViewPager
        public void openUpdateNote(MyNote myNote){

//            Intent intent =  new Intent(context, CreateNewNoteActivity.class);
//            intent.putExtra("id_note_for_view_and_update", myNote.getId_note());
//            context.startActivity(intent);

            //A. Lưu note sẽ xem, update
            UserUltils.saveNoteLocal(context, myNote);

            //B. Mở fragment cho các menu ở main tương ứng: 2-notes, 3-view all note
            if(UserUltils.getUserLocal(context).getFragment_user() == 2){

                //Chuyển item current của phần notes
                NotesViewPagerFragment.setCurrentItemNotes(3);

                //Load dữ liệu cho fm 3. Phải delay để chờ lên mới load (vì ở fm 1 nó chưa load fm 3)
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    UpdateNoteFragment.loadMyNoteShared();
                    UpdateNoteFragment.loadNoteData();
                }, 300);

            } else if (UserUltils.getUserLocal(context).getFragment_user() == 3) {

                //Chuyển item current của phần notes
                ViewAllNoteViewPagerFragment.setCurrentItemAllNoteViewPager(1);

                //Load luôn dữ liệu vì ở fm 0 (đang mở) thì nó đã load cả trước fm1
                UpdateNoteViewAllFragment.loadMyNoteShared();
                UpdateNoteViewAllFragment.loadNoteData();
            }

        }
    }
}
