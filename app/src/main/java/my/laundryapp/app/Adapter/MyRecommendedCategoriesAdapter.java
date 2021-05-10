package my.laundryapp.app.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import my.laundryapp.app.Callback.IRecyclerClickListener;
import my.laundryapp.app.EventBus.RecommendedCategoryClick;
import my.laundryapp.app.Model.RecommendedServicesModel;
import my.laundryapp.app.R;

public class MyRecommendedCategoriesAdapter extends RecyclerView.Adapter<MyRecommendedCategoriesAdapter.MyViewHolder> {

    Context context;
    List<RecommendedServicesModel> recommendedServicesModelList;

    public MyRecommendedCategoriesAdapter(Context context, List<RecommendedServicesModel> recommendedServicesModelList) {
        this.context = context;
        this.recommendedServicesModelList = recommendedServicesModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context)
        .inflate(R.layout.layout_recommended_services_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Glide.with(context).load(recommendedServicesModelList.get(position).getImage())
                .into(holder.category_image);
        holder.txt_category_name.setText(recommendedServicesModelList.get(position).getName());

        holder.setListener((view, pos) -> {
           // Toast.makeText(context, ""+recommendedServicesModelList.get(pos).getName(), Toast.LENGTH_SHORT).show();
            EventBus.getDefault().postSticky(new RecommendedCategoryClick(recommendedServicesModelList.get(pos)));
        });

    }

    @Override
    public int getItemCount() {
        return recommendedServicesModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Unbinder unbinder;

        @BindView(R.id.txt_category_name)
        TextView txt_category_name;
        @BindView(R.id.category_image)
        CircleImageView category_image;

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
            listener.onItemClickListener(v,getAdapterPosition());
        }
    }
}
