package my.laundryapp.app.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import my.laundryapp.app.Database.CartItem;
import my.laundryapp.app.Model.AddonModel;
import my.laundryapp.app.Model.SizeModel;
import my.laundryapp.app.R;

public class MyOrderDetailAdapter extends RecyclerView.Adapter<MyOrderDetailAdapter.MyViewHolder> {

    Context context;
    List<CartItem> cartItemList;
    Gson gson;

    public MyOrderDetailAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
        gson = new Gson();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_order_detail_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //holder.txt_service_quantity.setText(new StringBuilder("Quantity:")
        // .append(cartItemList.get(position).getServicesQuantity()));
        holder.txt_service_name.setText(new StringBuilder().append(cartItemList.get(position).getServicesName()));

        //holder.txt_size.setText(new StringBuilder("Size: ").append(cartItemList.get(position).getServicesSize()));
        //holder.txt_service_add_on.setText(new StringBuilder("Addon service: ").append(cartItemList.get(position).getServicesAddon()));


        SizeModel sizeModel = gson.fromJson(cartItemList.get(position).getServicesSize(), new TypeToken<SizeModel>() {
        }.getType());

        if (sizeModel != null)
            holder.txt_size.setText(new StringBuilder("Dobi Bag Size: ").append(sizeModel.getName()));

        if (!cartItemList.get(position).getServicesAddon().equals("Default")) {
            List<AddonModel> addonModels = gson.fromJson(cartItemList.get(position).getServicesAddon(), new TypeToken<List<AddonModel>>() {
            }.getType());
            StringBuilder addonString = new StringBuilder();
            if (addonModels != null) {
                for (AddonModel addonModel : addonModels)
                    addonString.append(addonModel.getName())
                            .append(",");
                addonString.delete(addonString.length() - 1, addonString.length()); //remove lat "," character
                holder.txt_service_add_on.setText(new StringBuilder("Addon:").append(addonString));
            }

        } else
            //holder.txt_service_add_on.setText(new StringBuilder("Addon: Default"));
            holder.txt_service_add_on.setVisibility(View.GONE);


    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_service_name)
        TextView txt_service_name;
        @BindView(R.id.txt_service_add_on)
        TextView txt_service_add_on;
        @BindView(R.id.txt_size)
        TextView txt_size;


        private Unbinder unbinder;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
        }


    }
}