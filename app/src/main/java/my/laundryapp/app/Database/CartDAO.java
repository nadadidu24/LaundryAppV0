package my.laundryapp.app.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface CartDAO {

    @Query("SELECT * FROM Cart WHERE custUid=:custUid")
    Flowable<List<CartItem>> getAllCart(String custUid);

    @Query("SELECT SUM(servicesQuantity) from Cart WHERE custUid=:custUid")
    Single<Integer> countItemInCart(String custUid);

    @Query("SELECT SUM((servicesPrice+servicesExtraPrice) * servicesQuantity) FROM Cart WHERE custUid=:custUid")
    Single<Double> sumPriceInCart(String custUid);

    @Query("SELECT * FROM Cart WHERE servicesId=:servicesId AND custUid=:custUid")
    Single<CartItem> getItemInCart(String servicesId, String custUid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertOrReplaceAll(CartItem... cartItems);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Single<Integer> updateCartItems(CartItem cartItem);

    @Delete
    Single<Integer> deleteCartItems(CartItem cartItem);

    @Query("DELETE FROM Cart WHERE custUid=:custUid")
    Single<Integer> cleanCart(String custUid);

    @Query("SELECT * FROM Cart WHERE categoryId=:categoryId AND servicesId=:servicesId AND custUid=:custUid AND servicesSize=:servicesSize AND servicesAddon=:servicesAddon")
    Single<CartItem> getItemWithAllOptionsInCart( String custUid,String categoryId,String servicesId,String servicesSize,String servicesAddon);


}
