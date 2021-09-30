package my.laundryapp.app.Database;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class LocalCartDataSource implements CartDataSource {

    private CartDAO cartDAO;

    public LocalCartDataSource(CartDAO cartDAO) {
        this.cartDAO = cartDAO;
    }

    @Override
    public Flowable<List<CartItem>> getAllCart(String custUid) {
        return cartDAO.getAllCart(custUid);
    }

    @Override
    public Single<Integer> countItemInCart(String custUid) {
        return cartDAO.countItemInCart(custUid);
    }

    @Override
    public Single<Double> sumPriceInCart(String custUid) {
        return cartDAO.sumPriceInCart(custUid);
    }

    @Override
    public Single<CartItem> getItemInCart(String servicesId, String custUid) {
        return cartDAO.getItemInCart(servicesId,custUid);
    }

    @Override
    public Completable insertOrReplaceAll(CartItem... cartItems) {
        return cartDAO.insertOrReplaceAll(cartItems);
    }

    @Override
    public Single<Integer> updateCartItems(CartItem cartItem) {
        return cartDAO.updateCartItems(cartItem);
    }

    @Override
    public Single<Integer> deleteCartItems(CartItem cartItem) {
        return cartDAO.deleteCartItems(cartItem);
    }

    @Override
    public Single<Integer> cleanCart(String custUid) {
        return cartDAO.cleanCart(custUid);
    }

    @Override
    public Single<CartItem> getItemWithAllOptionsInCart(String custUid, String categoryId, String servicesId, String servicesSize, String servicesAddon) {
        return cartDAO.getItemWithAllOptionsInCart(custUid,categoryId,servicesId,servicesSize,servicesAddon);
    }
}
