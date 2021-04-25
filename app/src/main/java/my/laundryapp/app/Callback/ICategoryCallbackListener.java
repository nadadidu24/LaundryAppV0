package my.laundryapp.app.Callback;

import java.util.List;

import my.laundryapp.app.Model.BestDealModel;
import my.laundryapp.app.Model.CategoryModel;

public interface ICategoryCallbackListener {
    void onCategoryLoadSuccess(List<CategoryModel> categoryModelList);
    void onCategoryLoadFailed(String message);
}
