package my.laundryapp.app.ui.comments;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import my.laundryapp.app.Model.CommentModel;
import my.laundryapp.app.Model.LaundryServicesModel;

public class CommentViewModel extends ViewModel {
    private MutableLiveData<List<CommentModel>> mutableLiveDataFoodList;

    public CommentViewModel() {
        mutableLiveDataFoodList = new MutableLiveData<>();
    }

    public MutableLiveData<List<CommentModel>> getMutableLiveDataFoodList() {
        return mutableLiveDataFoodList;
    }

    public void setCommentList (List<CommentModel> commentList)
    {
        mutableLiveDataFoodList.setValue(commentList);
    }

}
