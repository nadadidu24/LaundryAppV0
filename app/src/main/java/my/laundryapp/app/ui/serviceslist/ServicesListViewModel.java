package my.laundryapp.app.ui.serviceslist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import my.laundryapp.app.Common.Common;
import my.laundryapp.app.Model.LaundryServicesModel;

public class ServicesListViewModel extends ViewModel {

    private MutableLiveData<List<LaundryServicesModel>> mutableLiveDataFoodList;

    public ServicesListViewModel() {

    }

    public MutableLiveData<List<LaundryServicesModel>> getMutableLiveDataFoodList() {
        if(mutableLiveDataFoodList == null)
            mutableLiveDataFoodList = new MutableLiveData<>();
        mutableLiveDataFoodList.setValue(Common.categorySelected.getServices());
        return mutableLiveDataFoodList;
    }
}