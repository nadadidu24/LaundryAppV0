package my.laundryapp.app.Callback;

import java.util.List;

import my.laundryapp.app.Model.Order;

public interface ILoadOrderCallbackListener {
    void onLoadOrderSuccess(List<Order> orderList);
    void onLoadOrderFailed (String message);


}
