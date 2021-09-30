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


    Flowable<List<CartItem>> getAllCart(String custUid);


    Single<Integer> countItemInCart(String custUid);


    Single<Double> sumPriceInCart(String custUid);


    Single<CartItem> getItemInCart(String servicesId, String custUid);


    Completable insertOrReplaceAll(CartItem... cartItems);


    Single<Integer> updateCartItems(CartItem cartItem);


    Single<Integer> deleteCartItems(CartItem cartItem);


    Single<Integer> cleanCart(String custUid);

    Single<CartItem> getItemWithAllOptionsInCart( String custUid,String categoryId, String servicesId,String servicesSize,String servicesAddon);



}
