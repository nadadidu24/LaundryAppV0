package my.laundryapp.app.ui.servicesdetail;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import my.laundryapp.app.Common.Common;
import my.laundryapp.app.Model.CommentModel;
import my.laundryapp.app.Model.LaundryServicesModel;

public class ServiceDetailViewModel extends ViewModel {

    private MutableLiveData<LaundryServicesModel> mutableLiveDataFood;
    private MutableLiveData<CommentModel> mutableLiveDataComment;

    public void setCommentModel (CommentModel commentModel)
    {
        if(mutableLiveDataComment != null)
            mutableLiveDataComment.setValue(commentModel);
    }

    public MutableLiveData<CommentModel> getMutableLiveDataComment() {
        return mutableLiveDataComment;
    }

    public ServiceDetailViewModel() {
        mutableLiveDataComment = new MutableLiveData<>();
    }

    public MutableLiveData<LaundryServicesModel> getMutableLiveDataFood() {
        if(mutableLiveDataFood == null)
            mutableLiveDataFood = new MutableLiveData<>();
        mutableLiveDataFood.setValue(Common.selectedService);

        return mutableLiveDataFood;
    }

    public void setServiceModel(LaundryServicesModel laundryServicesModel) {
        if(mutableLiveDataFood != null)
        mutableLiveDataFood.setValue(laundryServicesModel);
    }
}
