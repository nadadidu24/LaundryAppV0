package my.laundryapp.app.ui.cart;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.schedulers.Schedulers;
import my.laundryapp.app.Database.CartDataSource;
import my.laundryapp.app.Database.CartDatabase;
import my.laundryapp.app.Database.CartItem;
import my.laundryapp.app.Database.LocalCartDataSource;
import my.laundryapp.app.Main4Activity;
import my.laundryapp.app.Model.CustomerModel;

public class CartViewModel extends ViewModel {

    //change
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userCustID;

    //change

    private CompositeDisposable compositeDisposable;
    private CartDataSource cartDataSource;
    private MutableLiveData<List<CartItem>> mutableLiveDataCartItem;




    public CartViewModel() {
        compositeDisposable = new CompositeDisposable();

    }

    public void initCartDataSource(Context context)
    {
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(context).cartDAO());
    }

    public void onStop(){
        compositeDisposable.clear();
    }


    public MutableLiveData<List<CartItem>> getMutableLiveDataCartItem() {
        if(mutableLiveDataCartItem == null)
            mutableLiveDataCartItem = new MutableLiveData<>();
        getAllCartItems();
        return mutableLiveDataCartItem;
    }

    private void getAllCartItems() {

        //changes try
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Customer");
        userCustID = user.getUid();
        //

        //changes
        reference.child(userCustID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CustomerModel userProfile = snapshot.getValue(CustomerModel.class);

                compositeDisposable.add(cartDataSource.getAllCart(userProfile.getCustUid())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(cartItems -> {
                            mutableLiveDataCartItem.setValue(cartItems);
                        }, throwable -> {
                            mutableLiveDataCartItem.setValue(null);

                        }));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });

        /*
        compositeDisposable.add(cartDataSource.getAllCart("Email")
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(cartItems -> {
            mutableLiveDataCartItem.setValue(cartItems);
        }, throwable -> {
            mutableLiveDataCartItem.setValue(null);

        }));

         */
    }
}
