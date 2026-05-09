package com.mvdragon.weekcalendar.calendarview.event;

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

import java.util.ArrayList;

public class ImageEventAdapter extends RecyclerView.Adapter<ImageEventAdapter.ImageEventViewHolder>{
    private final Context context;
    private ArrayList<ImageEvent> imageEventArrayList;
    private final IClickAddImageIcon iClickAddImageIcon;
    private final IClickImageEvent iClickImageEvent;
    private final boolean showIconDeleteOneImage; //Tín hiệu để show hoặc ẩn nút delete 1 image
    private final TruyVanDuLieuSQLite truyVanDuLieuSQLite;

    //III. Hàm khởi tạo: màn hình, list, sự kiện của item cuối, item bình thường
    public ImageEventAdapter(Context context, ArrayList<ImageEvent> imageEventArrayList, boolean showIconDeleteOneImage, IClickAddImageIcon iClickAddImageIcon, IClickImageEvent iClickImageEvent) {
        this.context = context;
        this.imageEventArrayList = imageEventArrayList;
        this.showIconDeleteOneImage = showIconDeleteOneImage;
        this.iClickAddImageIcon = iClickAddImageIcon;
        this.iClickImageEvent = iClickImageEvent;

        truyVanDuLieuSQLite = new TruyVanDuLieuSQLite(context);


    }

    //IV. Khai báo View cho ViewHolder (Theo hàm getItemViewType() chọn layout của item theo vị trí, kích thước list)
    @NonNull
    @Override
    public ImageEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //Cài đặt view tương ứng layout item trong getItemViewType()
        View view;

        if(viewType == R.layout.item_event_image){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_image, parent, false);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ic_add_image, parent, false);
        }

        return new ImageEventViewHolder(view);
    }

    //V. Khai báo cụ thể trong mỗi item
    @Override
    public void onBindViewHolder(@NonNull ImageEventViewHolder holder, int position) {

        //A. Nếu không phải vị trí cuối +1 (Vẫn nằm trong list) thì set ảnh cho list như bình thường
        if (position < imageEventArrayList.size()) {

            //1. Lấy ImageEvent theo thứ tự tương ứng trong list -> Chuyển ảnh về bitmap từ byte array
            ImageEvent imageEvent = imageEventArrayList.get(position);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageEvent.getEvent_image(), 0, imageEvent.getEvent_image().length);

            //2. Set ảnh bitmap cho item trong rcv
            if(bitmap != null){
                holder.img_event_image.setImageBitmap(bitmap);
            }

            //3. Khai báo ẩn hiện nút delete
            if(showIconDeleteOneImage){
                holder.ic_delete_image.setVisibility(View.VISIBLE);
            } else {
                holder.ic_delete_image.setVisibility(View.GONE);
            }

            //4. Xoá ảnh trong list
            holder.ic_delete_image.setOnClickListener(v -> {

                //Xoá ảnh trong list đang dùng (ở Activity) tương ứng vị trí được chọn (Dùng getLayoutPosition() nếu không sẽ bị lỗi)
                imageEventArrayList.remove(holder.getLayoutPosition());
                //Xoá ảnh và cập nhật trong rcv (với vị trí đã tương tác xoá)
                notifyItemRemoved(holder.getLayoutPosition());

            });

            //5. Sự kiện khi click vào ảnh (item) -> Thực hiện hàm bên ngoài, đồng thời truyền vị trí position trong list ra
            holder.img_event_image.setOnClickListener(v -> {
                iClickImageEvent.onClickImageEvent(imageEvent.getId_event(), imageEvent.getId_image_event(), position);
            });
        }

        //B. Nếu là vị trí cuối +1 (Nằm ngoài list -> Cài đặt riêng cụ thể cho nút cuối (Vẫn theo hàm getItemViewType của android))
        else if(position == imageEventArrayList.size()){
                //Set sự kiện cho item cuối này (đang dùng ảnh set sẵn)
                holder.img_event_add_image.setOnClickListener(v -> {
                    //Thực hiện sự kiện ngoài event khi click vào icon cuối +1
                    iClickAddImageIcon.onClickAddImageIcon();
                });
            }
    }

    //II.1 Số lượng item sẽ có (thông thường sẽ bằng số lượng trong list. Nếu thêm 2 item cuối thì +2. Ở đây thêm 1 nên +1)
    @Override
    public int getItemCount() {
        return imageEventArrayList.size() + 1;
    }

    //II.2 Hàm chọn layout sẽ sử dụng làm item (Theo kích thước, vị trí list)
    @Override
    public int getItemViewType(int position) {
        //Dùng hàm if rút gọn: Nếu vị trí khi set adapter >= vị trí cuối của list thì chọn button add image làm layout của item (Nếu không thì sử dụng layout item_event_image để hiện ảnh)
        return (position >= imageEventArrayList.size()) ? R.layout.item_ic_add_image : R.layout.item_event_image;
    }

    //I. Khai báo ViewHolder (của item)
    class ImageEventViewHolder extends RecyclerView.ViewHolder{
        private final ImageView img_event_image;
        private final ImageView img_event_add_image;
        private final ImageView ic_delete_image;

        public ImageEventViewHolder(@NonNull View itemView) {
            super(itemView);

            //Ánh xạ cho cả 2 layout item
            //Layout 1 (dùng cho rcv như bình thường)
            img_event_image = itemView.findViewById(R.id.imageView_event_image);
            ic_delete_image = itemView.findViewById(R.id.ic_delete_image);

            //Layout 2: dùng cho nút cuối
            img_event_add_image = itemView.findViewById(R.id.imageView_event_add_image);
        }
    }

    //interface truyền dữ liệu: Thực hiện hàm bên ngoài adapter
    public interface IClickAddImageIcon{
        void onClickAddImageIcon();
    }

    public interface IClickImageEvent{
        void onClickImageEvent(int id_event, int id_image_event, int position);
    }
}
