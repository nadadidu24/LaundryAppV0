package my.laundryapp.app.Database;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface CartDataSource {


    Flowable<List<CartItem>> getAllCart(String Email);


    Single<Integer> countItemInCart(String Email);


    Single<Double> sumPriceInCart(String Email);


    Single<CartItem> getItemInCart(String servicesId, String Email);


    Completable insertOrReplaceAll(CartItem... cartItems);


    Single<Integer> updateCartItems(CartItem cartItem);


    Single<Integer> deleteCartItems(CartItem cartItem);


    Single<Integer> cleanCart(String Email);

    Single<CartItem> getItemWithAllOptionsInCart( String Email,String servicesId,String servicesSize,String servicesAddon);



}
