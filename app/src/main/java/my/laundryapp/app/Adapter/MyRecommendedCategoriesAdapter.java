package my.laundryapp.app.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
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

    }

    @Override
    public int getItemCount() {
        return recommendedServicesModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        Unbinder unbinder;

        @BindView(R.id.txt_category_name)
        TextView txt_category_name;
        @BindView(R.id.category_image)
        CircleImageView category_image;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this,itemView);
        }
    }
}
