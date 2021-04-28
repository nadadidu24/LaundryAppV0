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

    @Query("SELECT * FROM Cart WHERE custUid=:Email")
    Flowable<List<CartItem>> getAllCart(String Email);

    @Query("SELECT SUM(servicesQuantity) from Cart WHERE custUid=:Email")
    Single<Integer> countItemInCart(String Email);

    @Query("SELECT SUM((servicesPrice*servicesExtraPrice) * servicesQuantity) FROM Cart WHERE custUid=:Email")
    Single<Double> sumPriceInCart(String Email);

    @Query("SELECT * FROM Cart WHERE servicesId=:servicesId AND custUid=:Email")
    Single<CartItem> getItemInCart(String servicesId, String Email);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertOrReplaceAll(CartItem... cartItems);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Single<Integer> updateCartItems(CartItem cartItem);

    @Delete
    Single<Integer> deleteCartItems(CartItem cartItem);

    @Query("DELETE FROM Cart WHERE custUid=:Email")
    Single<Integer> cleanCart(String Email);

    @Query("SELECT * FROM Cart WHERE servicesId=:servicesId AND custUid=:Email AND servicesSize=:servicesSize AND servicesAddon=:servicesAddon")
    Single<CartItem> getItemWithAllOptionsInCart( String Email,String servicesId,String servicesSize,String servicesAddon);


}
