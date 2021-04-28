package my.laundryapp.app.Database;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class LocalCartDataSource implements CartDataSource {

    private CartDAO cartDao;

    public LocalCartDataSource(CartDAO cartDao) {
        this.cartDao = cartDao;
    }

    @Override
    public Flowable<List<CartItem>> getAllCart(String Email) {
        return cartDao.getAllCart(Email);
    }

    @Override
    public Single<Integer> countItemInCart(String Email) {
        return cartDao.countItemInCart(Email);
    }

    @Override
    public Single<Double> sumPriceInCart(String Email) {
        return cartDao.sumPriceInCart(Email);
    }

    @Override
    public Single<CartItem> getItemInCart(String servicesId, String Email) {
        return cartDao.getItemInCart(servicesId,Email);
    }

    @Override
    public Completable insertOrReplaceAll(CartItem... cartItems) {
        return cartDao.insertOrReplaceAll(cartItems);
    }

    @Override
    public Single<Integer> updateCartItems(CartItem cartItem) {
        return cartDao.updateCartItems(cartItem);
    }

    @Override
    public Single<Integer> deleteCartItems(CartItem cartItem) {
        return cartDao.deleteCartItems(cartItem);
    }

    @Override
    public Single<Integer> cleanCart(String Email) {
        return cartDao.cleanCart(Email);
    }

    @Override
    public Single<CartItem> getItemWithAllOptionsInCart(String Email, String servicesId, String servicesSize, String servicesAddon) {
        return cartDao.getItemWithAllOptionsInCart(Email,servicesId,servicesSize,servicesAddon);
    }
}
