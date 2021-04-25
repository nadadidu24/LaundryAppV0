package my.laundryapp.app.Callback;

import java.util.List;

import my.laundryapp.app.Model.RecommendedServicesModel;

public interface IRecommendedCallbackListener {

    void onRecommendedLoadSuccess(List<RecommendedServicesModel> recommendedCategoryModels);
    void onRecommendedLoadFailed(String message);

}
