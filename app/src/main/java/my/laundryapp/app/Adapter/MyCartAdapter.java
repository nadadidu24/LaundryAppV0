package my.laundryapp.app.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import my.laundryapp.app.Database.CartItem;
import my.laundryapp.app.EventBus.UpdateItemInCart;
import my.laundryapp.app.R;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.MyViewHolder> {

    Context context;
    List<CartItem> cartItemList;

    public MyCartAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_cart_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(cartItemList.get(position).getServicesImage())
                .into(holder.img_cart);
        holder.txt_services_name.setText(new StringBuilder(cartItemList.get(position).getServicesName()));
        holder.txt_services_price.setText(new StringBuilder("")
        .append(cartItemList.get(position).getServicesPrice() + cartItemList.get(position).getServicesExtraPrice()));

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

        @BindView(R.id.img_cart)
        ImageView img_cart;
        @BindView(R.id.txt_services_price)
        TextView txt_services_price;
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
