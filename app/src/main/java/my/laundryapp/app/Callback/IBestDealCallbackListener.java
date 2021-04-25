package my.laundryapp.app.Callback;

import java.util.List;

import my.laundryapp.app.Model.BestDealModel;
import my.laundryapp.app.Model.RecommendedServicesModel;

public interface IBestDealCallbackListener {
    void onBestDealLoadSuccess(List<BestDealModel> bestDealModels);
    void onBestDealLoadFailed(String message);
}
