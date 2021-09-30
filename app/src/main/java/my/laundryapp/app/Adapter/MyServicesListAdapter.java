package my.laundryapp.app.Adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Scheduler;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import my.laundryapp.app.Callback.IRecyclerClickListener;
import my.laundryapp.app.Common.Common;
import my.laundryapp.app.Database.CartDataSource;
import my.laundryapp.app.Database.CartDatabase;
import my.laundryapp.app.Database.CartItem;
import my.laundryapp.app.Database.LocalCartDataSource;
import my.laundryapp.app.EventBus.CounterCardEvent;
import my.laundryapp.app.EventBus.ServiceItemClick;
import my.laundryapp.app.Model.CustomerModel;
import my.laundryapp.app.Model.LaundryServicesModel;
import my.laundryapp.app.R;

public class MyServicesListAdapter extends RecyclerView.Adapter<MyServicesListAdapter.MyViewHolder> {

    //change
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userCustID;

    //change

    private Context context;
    private List<LaundryServicesModel> foodModelList;
    private CompositeDisposable compositeDisposable;
    private CartDataSource cartDataSource;

    public MyServicesListAdapter(Context context, List<LaundryServicesModel> foodModelList) {
        this.context = context;
        this.foodModelList = foodModelList;
        this.compositeDisposable = new CompositeDisposable();
        this.cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(context).cartDAO());

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

        holder.img_cart.setOnClickListener(v -> {

            //changes try
            user = FirebaseAuth.getInstance().getCurrentUser();
            reference = FirebaseDatabase.getInstance().getReference("Customer");
            userCustID = user.getUid();
            //

            //changes 3
            reference.child(userCustID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    CustomerModel userProfile = snapshot.getValue(CustomerModel.class);

                    CartItem cartItem = new CartItem();
                    cartItem.setCustUid(userProfile.getCustUid());
                    cartItem.setCustPhone(userProfile.getPhoneNumber());

                    cartItem.setCategoryId(Common.categorySelected.getCatalog_id());

                    cartItem.setServicesId(foodModelList.get(position).getId());
                    cartItem.setServicesName(foodModelList.get(position).getName());
                    cartItem.setServicesImage(foodModelList.get(position).getImage());
                    cartItem.setServicesPrice(Double.valueOf(String.valueOf(foodModelList.get(position).getPrice())));
                    cartItem.setServicesQuantity(1);
                    cartItem.setServicesExtraPrice(0.0); //becoz default we not choose size + addon so extra price 0
                    cartItem.setServicesAddon("Default");
                    cartItem.setServicesSize("Default");

                    cartDataSource.getItemWithAllOptionsInCart(userProfile.getCustUid(),
                            Common.categorySelected.getCatalog_id(),
                            cartItem.getServicesId(),
                            cartItem.getServicesSize(),
                            cartItem.getServicesAddon())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new SingleObserver<CartItem>() {
                                @Override
                                public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                                }

                                @Override
                                public void onSuccess(@io.reactivex.annotations.NonNull CartItem cartItemFromDB) {
                                    if(cartItemFromDB.equals(cartItem))
                                    {
                                        //Already in database, just update
                                        cartItemFromDB.setServicesExtraPrice(cartItem.getServicesExtraPrice());
                                        cartItemFromDB.setServicesAddon(cartItem.getServicesAddon());
                                        cartItemFromDB.setServicesSize(cartItem.getServicesSize());
                                        cartItemFromDB.setServicesQuantity(cartItemFromDB.getServicesQuantity() + cartItem.getServicesQuantity());

                                        cartDataSource.updateCartItems(cartItemFromDB)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new SingleObserver<Integer>() {
                                                    @Override
                                                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                                                    }

                                                    @Override
                                                    public void onSuccess(@io.reactivex.annotations.NonNull Integer integer) {
                                                        Toast.makeText(context,"update cart success",Toast.LENGTH_SHORT).show();
                                                        EventBus.getDefault().postSticky(new CounterCardEvent(true));
                                                    }

                                                    @Override
                                                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                                        Toast.makeText(context,"[UPDATE CART]"+e.getMessage(),Toast.LENGTH_SHORT).show();

                                                    }
                                                });

                                    }
                                    else
                                    {
                                        //item not available in cart before, insert new
                                        compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItem)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(()-> {
                                                    Toast.makeText(context,"Add to cart success",Toast.LENGTH_SHORT).show();
                                                    //here we will send a notify to mainactivity4 to update counter in cart
                                                    EventBus.getDefault().postSticky(new CounterCardEvent(true));
                                                },throwable -> {
                                                    Toast.makeText(context,"CART ERROR" +throwable.getMessage(),Toast.LENGTH_SHORT).show();
                                                }));

                                    }


                                }

                                @Override
                                public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                    if(e.getMessage().contains("empty"))
                                    {
                                        //default,if cart empty,this code run
                                        compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItem)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(()-> {
                                                    Toast.makeText(context,"Add to cart success",Toast.LENGTH_SHORT).show();
                                                    //here we will send a notify to mainactivity4 to update counter in cart
                                                    EventBus.getDefault().postSticky(new CounterCardEvent(true));
                                                },throwable -> {
                                                    Toast.makeText(context,"CART ERROR" +throwable.getMessage(),Toast.LENGTH_SHORT).show();
                                                }));
                                    }
                                    else
                                        Toast.makeText(context,"[GET CART]"+e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });

            /*
            compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItem)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(()-> {
                        Toast.makeText(context,"Add to cart success",Toast.LENGTH_SHORT).show();
                        //here we will send a notify to mainactivity4 to update counter in cart
                        EventBus.getDefault().postSticky(new CounterCardEvent(true));
                    },throwable -> {
                        Toast.makeText(context,"CART ERROR" +throwable.getMessage(),Toast.LENGTH_SHORT).show();
                    }));

             */



                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });







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
