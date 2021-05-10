package my.laundryapp.app.Callback;

import my.laundryapp.app.Model.Order;

public interface ILoadTimeFromFirebaseListener {
    void onLoadTimeSuccess (Order order, long estimateTimeTaken);
    void onLoadTimeFailed (String message);
}
