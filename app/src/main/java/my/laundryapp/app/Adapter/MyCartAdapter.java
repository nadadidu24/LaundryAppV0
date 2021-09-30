package my.laundryapp.app.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import my.laundryapp.app.Common.Common;
import my.laundryapp.app.Database.CartItem;
import my.laundryapp.app.EventBus.UpdateItemInCart;
import my.laundryapp.app.Model.AddonModel;
import my.laundryapp.app.Model.SizeModel;
import my.laundryapp.app.R;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.MyViewHolder> {

    Context context;
    List<CartItem> cartItemList;
    Gson gson;

    public MyCartAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
        this.gson = new Gson();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_cart_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //Glide.with(context).load(cartItemList.get(position).getServicesImage())
                //.into(holder.img_cart);
        holder.txt_services_name.setText(new StringBuilder(cartItemList.get(position).getServicesName()));
        holder.txt_services_price.setText(new StringBuilder("")
        .append(cartItemList.get(position).getServicesPrice() + cartItemList.get(position).getServicesExtraPrice()));

        if(cartItemList.get(position).getServicesSize() != null)
        {
            if (cartItemList.get(position).getServicesSize().equals("Default"))
                holder.txt_services_size.setText(new StringBuilder("Size: ").append("Default"));
            else
            {
                SizeModel sizeModel  = gson.fromJson(cartItemList.get(position).getServicesSize(),new TypeToken<SizeModel>(){}.getType());
                holder.txt_services_size.setText(new StringBuilder("Size: ").append(sizeModel.getName()));
            }
        }

        if(cartItemList.get(position).getServicesAddon() != null)
        {
            if (cartItemList.get(position).getServicesAddon().equals("Default"))
                holder.txt_services_addon.setText(new StringBuilder("Addon: ").append("Default"));

            else {
                List<AddonModel> addonModels = gson.fromJson(cartItemList.get(position).getServicesAddon(),
                        new TypeToken<List<AddonModel>>(){}.getType());
                //adik edit 30/9
                //silap bawah ni
                holder.txt_services_addon.setText(new StringBuilder("Addon: ").append(Common.getListAddon(addonModels)));

            }
        }
        //adik edit 30/9
        /*
        else if (cartItemList.get(position).getServicesAddon() == null){
            Toast.makeText(context, "ini dia", Toast.LENGTH_SHORT).show();
            if (cartItemList.get(position).getServicesAddon().equals("Default"))
                holder.txt_services_addon.setText(new StringBuilder("Addon: ").append("Default"));
            else {
                List<AddonModel> addonModels = gson.fromJson(cartItemList.get(position).getServicesAddon(),
                        new TypeToken<List<AddonModel>>(){}.getType());
                holder.txt_services_addon.setText(new StringBuilder("Addon: ").append(Common.getListAddon(addonModels)));

            }

        }

         */

        holder.numberButton.setNumber(String.valueOf(cartItemList.get(position).getServicesQuantity()));

        //Event
        holder.numberButton.setOnValueChangeListener((view, oldValue, newValue) -> {
            //when user click this button,we will update database too
            cartItemList.get(position).setServicesQuantity(newValue);
            EventBus.getDefault().postSticky(new UpdateItemInCart(cartItemList.get(position)));
        });


    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public CartItem getItemAtPosition(int pos) {
        return cartItemList.get(pos);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private Unbinder unbinder;

        //@BindView(R.id.img_cart)
        //ImageView img_cart;
        @BindView(R.id.txt_services_price)
        TextView txt_services_price;
        @BindView(R.id.txt_services_size)
        TextView txt_services_size;
        @BindView(R.id.txt_services_addon)
        TextView txt_services_addon;
        @BindView(R.id.txt_services_name)
        TextView txt_services_name;
        @BindView(R.id.number_button)
        ElegantNumberButton numberButton;




        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this,itemView);
        }
    }
}
