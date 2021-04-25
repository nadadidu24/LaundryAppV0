package my.laundryapp.app.ui.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import my.laundryapp.app.Callback.IBestDealCallbackListener;
import my.laundryapp.app.Callback.IRecommendedCallbackListener;
import my.laundryapp.app.Model.BestDealModel;
import my.laundryapp.app.Model.RecommendedServicesModel;

public class HomeViewModel extends ViewModel implements IRecommendedCallbackListener, IBestDealCallbackListener {

private MutableLiveData<List<RecommendedServicesModel>> recommendedList;
    private MutableLiveData<List<BestDealModel>> bestDealList;

    private MutableLiveData<String> messageError;
private IRecommendedCallbackListener recommendedCallbackListener;
    private IBestDealCallbackListener bestDealCallbackListener;

    public HomeViewModel() {

        recommendedCallbackListener = this;
        bestDealCallbackListener = this;
    }

    public MutableLiveData<List<BestDealModel>> getBestDealList() {
        if (bestDealList == null)
        {
            bestDealList = new MutableLiveData<>();
            messageError = new MutableLiveData<>();
            loadBestDealList();
        }
        return bestDealList;
    }

    private void loadBestDealList() {
        List<BestDealModel> tempList = new ArrayList<>();
        DatabaseReference bestDealRef = FirebaseDatabase.getInstance().getReference("BestDeals");
        bestDealRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemSnapShot:snapshot.getChildren())
                {
                    BestDealModel model = itemSnapShot.getValue(BestDealModel.class);
                    tempList.add(model);
                }
                bestDealCallbackListener.onBestDealLoadSuccess(tempList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            bestDealCallbackListener.onBestDealLoadFailed(error.getMessage());
            }
        });
    }

    public MutableLiveData<List<RecommendedServicesModel>> getRecommendedList() {
        if(recommendedList == null){
            recommendedList = new MutableLiveData<>();
            messageError = new MutableLiveData<>();
            loadRecommendedList();
        }
        return recommendedList;
    }

    private void loadRecommendedList() {
        List<RecommendedServicesModel> tempList = new ArrayList<>();
        DatabaseReference recommendedRef = FirebaseDatabase.getInstance().getReference("MostPopular");
    recommendedRef.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
for (DataSnapshot itemSnapShot:snapshot.getChildren())
{
    RecommendedServicesModel model = itemSnapShot.getValue(RecommendedServicesModel.class);
    tempList.add(model);
}
recommendedCallbackListener.onRecommendedLoadSuccess(tempList);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
recommendedCallbackListener.onRecommendedLoadFailed(error.getMessage());
        }
    });
    }

    public MutableLiveData<String> getMessageError() {
        return messageError;
    }

    @Override
    public void onRecommendedLoadSuccess(List<RecommendedServicesModel> recommendedCategoryModels) {
        recommendedList.setValue(recommendedCategoryModels);
    }

    @Override
    public void onRecommendedLoadFailed(String message) {
messageError.setValue(message);
    }

    @Override
    public void onBestDealLoadSuccess(List<BestDealModel> bestDealModels) {
        bestDealList.setValue(bestDealModels);
    }

    @Override
    public void onBestDealLoadFailed(String message) {
messageError.setValue(message);
    }
}