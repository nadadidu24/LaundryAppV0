package my.laundryapp.app.ui.view_orders;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bumptech.glide.load.model.Model;

import java.util.List;

import my.laundryapp.app.Model.Order;


public class ViewOrdersViewModel extends ViewModel {

    private MutableLiveData<List<Order>> mutableLiveDataOrderList;



    public ViewOrdersViewModel() {
        mutableLiveDataOrderList = new MutableLiveData<>();

    }

    public MutableLiveData<List<Order>> getMutableLiveDataOrderList() {
        return mutableLiveDataOrderList;
    }

    public void setMutableLiveDataOrderList(List<Order> orderList) {
        mutableLiveDataOrderList.setValue(orderList);
    }
}