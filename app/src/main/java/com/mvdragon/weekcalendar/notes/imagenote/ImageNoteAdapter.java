package com.mvdragon.weekcalendar.notes.imagenote;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mvdragon.weekcalendar.R;
import com.mvdragon.weekcalendar.database.TruyVanDuLieuSQLite;
import com.mvdragon.weekcalendar.model.ImageEvent;
import com.mvdragon.weekcalendar.model.ImageNote;

import java.util.ArrayList;

public class ImageNoteAdapter extends RecyclerView.Adapter<ImageNoteAdapter.ImageNoteViewHolder>{
    private final Context context;
    private ArrayList<ImageNote> imageNoteArrayList;
    private final IClickAddImageIcon iClickAddImageIcon;
    private final IClickImageNote iClickImageNote;
    private final boolean showIconDeleteOneImage; //Tín hiệu để show hoặc ẩn nút delete 1 image
    private final TruyVanDuLieuSQLite truyVanDuLieuSQLite;

    //III. Hàm khởi tạo: màn hình, list, sự kiện của item cuối, item bình thường
    public ImageNoteAdapter (Context context, ArrayList<ImageNote> imageNoteArrayList, boolean showIconDeleteOneImage, IClickAddImageIcon iClickAddImageIcon, IClickImageNote iClickImageNote) {
        this.context = context;
        this.imageNoteArrayList = imageNoteArrayList;
        this.showIconDeleteOneImage = showIconDeleteOneImage;
        this.iClickAddImageIcon = iClickAddImageIcon;
        this.iClickImageNote = iClickImageNote;

        truyVanDuLieuSQLite = new TruyVanDuLieuSQLite(context);

    }

    //IV. Khai báo View cho ViewHolder (Theo hàm getItemViewType() chọn layout của item theo vị trí, kích thước list)
    @NonNull
    @Override
    public ImageNoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //Cài đặt view tương ứng layout item trong getItemViewType() | ImageNote vẫn dùng layout của ImageEvent
        View view;

        if(viewType == R.layout.item_event_image){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_image, parent, false);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ic_add_image, parent, false);
        }

        return new ImageNoteViewHolder(view);
    }

    //V. Khai báo cụ thể trong mỗi item
    @Override
    public void onBindViewHolder(@NonNull ImageNoteViewHolder holder, int position) {

        //A. Nếu không phải vị trí cuối +1 (Vẫn nằm trong list) thì set ảnh cho list như bình thường
        if (position < imageNoteArrayList.size()) {

            //1. Lấy ImageEvent theo thứ tự tương ứng trong list -> Chuyển ảnh về bitmap từ byte array
            ImageNote imageNote = imageNoteArrayList.get(position);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageNote.getNote_image(), 0, imageNote.getNote_image().length);

            //2. Set ảnh bitmap cho item trong rcv
            if(bitmap != null){
                holder.img_note_image.setImageBitmap(bitmap);
            }

            //3. Khai báo ẩn hiện nút delete
            if(showIconDeleteOneImage){
                holder.ic_delete_image.setVisibility(View.VISIBLE);
            } else {
                holder.ic_delete_image.setVisibility(View.GONE);
            }

            //4. Xoá ảnh trong list (list ảnh đang hiển thị trên activity, chứ chưa xoá ở CSDL)
            holder.ic_delete_image.setOnClickListener(v -> {

                //Xoá ảnh trong list đang dùng (ở Activity) tương ứng vị trí được chọn (Dùng getLayoutPosition() nếu không sẽ bị lỗi)
                imageNoteArrayList.remove(holder.getLayoutPosition());
                //Xoá ảnh và cập nhật trong rcv
                notifyItemRemoved(holder.getLayoutPosition());

            });

            //5. Sự kiện khi click vào ảnh (item) -> Thực hiện hàm bên ngoài, đồng thời truyền vị trí position trong list ra
            holder.img_note_image.setOnClickListener(v -> {
                iClickImageNote.onClickImageNote(imageNote.getId_image_note(), imageNote.getId_note(), position);
            });
        }
        //B. Nếu là vị trí cuối +1 (Nằm ngoài list -> Cài đặt riêng cụ thể cho nút cuối (Vẫn theo hàm getItemViewType của android))
        else if (position == imageNoteArrayList.size()){
                //Set sự kiện cho item cuối này (đang dùng ảnh set sẵn)
                holder.img_note_add_image.setOnClickListener(v -> {
                    //Thực hiện sự kiện ngoài event khi click vào icon cuối +1
                    iClickAddImageIcon.onClickAddImageIcon();
                });
            }
    }

    //II.1 Số lượng item sẽ có (thông thường sẽ bằng số lượng trong list. Nếu thêm 2 item cuối thì +2. Ở đây thêm 1 nên +1)
    @Override
    public int getItemCount() {
        return imageNoteArrayList.size() + 1;
    }

    //II.2 Hàm chọn layout sẽ sử dụng làm item (Theo kích thước, vị trí list)
    @Override
    public int getItemViewType(int position) {
        //Dùng hàm if rút gọn: Nếu vị trí khi set adapter >= vị trí cuối của list thì chọn button add image làm layout của item (Nếu không thì sử dụng layout item_event_image để hiện ảnh)
        return (position >= imageNoteArrayList.size()) ? R.layout.item_ic_add_image : R.layout.item_event_image;
    }

    //I. Khai báo ViewHolder (của item)
    class ImageNoteViewHolder extends RecyclerView.ViewHolder{
        private final ImageView img_note_image;
        private final ImageView img_note_add_image;
        private final ImageView ic_delete_image;

        public ImageNoteViewHolder(@NonNull View itemView) {
            super(itemView);

            //Ánh xạ cho cả 2 layout item
            //Layout 1 (dùng cho rcv như bình thường)
            img_note_image = itemView.findViewById(R.id.imageView_event_image);
            ic_delete_image = itemView.findViewById(R.id.ic_delete_image);

            //Layout 2: dùng cho nút cuối
            img_note_add_image = itemView.findViewById(R.id.imageView_event_add_image);
        }
    }

    //interface truyền dữ liệu: Thực hiện hàm bên ngoài adapter
    public interface IClickAddImageIcon{
        void onClickAddImageIcon();
    }

    public interface IClickImageNote{
        void onClickImageNote(int id_image_note, int id_note, int position);
    }
}
