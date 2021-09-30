package my.laundryapp.app.Callback;

import my.laundryapp.app.Database.CartItem;
import my.laundryapp.app.Model.CategoryModel;
import my.laundryapp.app.Model.LaundryServicesModel;

public interface ISearchCategoryCallbackListener {
    void onSearchCategoryFound(CategoryModel categoryModel, CartItem cartItem);
    void onSearchCategoryNotFound(String message);
}
