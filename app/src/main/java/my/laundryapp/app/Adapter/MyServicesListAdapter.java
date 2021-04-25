package my.laundryapp.app.Adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import my.laundryapp.app.Callback.IRecyclerClickListener;
import my.laundryapp.app.Common.Common;
import my.laundryapp.app.EventBus.ServiceItemClick;
import my.laundryapp.app.Model.LaundryServicesModel;
import my.laundryapp.app.R;

public class MyServicesListAdapter extends RecyclerView.Adapter<MyServicesListAdapter.MyViewHolder> {

    private Context context;
    private List<LaundryServicesModel> foodModelList;

    public MyServicesListAdapter(Context context, List<LaundryServicesModel> foodModelList) {
        this.context = context;
        this.foodModelList = foodModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context)
        .inflate(R.layout.layout_services_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(foodModelList.get(position).getImage()).into(holder.img_services_image);
        holder.txt_services_price.setText(new StringBuilder("RM")
        .append(foodModelList.get(position).getPrice()));
        holder.txt_services_name.setText(new StringBuilder("")
        .append(foodModelList.get(position).getName()));

        //Event
        holder.setListener((view, pos) -> {
            Common.selectedService = foodModelList.get(pos);
            Common.selectedService.setKey(String.valueOf(pos));
            EventBus.getDefault().postSticky(new ServiceItemClick(true,foodModelList.get(pos)));
        });

    }

    @Override
    public int getItemCount() {
        return foodModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Unbinder unbinder;
        @BindView(R.id.txt_services_name)
        TextView txt_services_name;
        @BindView(R.id.txt_services_price)
        TextView txt_services_price;
        @BindView(R.id.img_services_image)
        ImageView img_services_image;
        @BindView(R.id.img_fav)
        ImageView img_fav;
        @BindView(R.id.img_quick_cart)
        ImageView img_cart;

        IRecyclerClickListener listener;

        public void setListener(IRecyclerClickListener listener) {
            this.listener = listener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClickListener(itemView,getAdapterPosition());
        }
    }
}
