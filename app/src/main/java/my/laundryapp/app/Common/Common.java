package my.laundryapp.app.Common;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import my.laundryapp.app.Model.BestDealModel;
import my.laundryapp.app.Model.CategoryModel;
import my.laundryapp.app.Model.CommentModel;
import my.laundryapp.app.Model.CustomerModel;
import my.laundryapp.app.Model.LaundryServicesModel;

public class Common {
    public static final String USER_REFERENCES = "Customer";
    
    public static final int DEFAULT_COLUMN_COUNT =0 ;
    public static final int FULL_WIDTH_COLUMN =1 ;
    public static final String CATEGORY_REF = "Category";
    public static final String COMMENT_REF = "Comments";
    public static CategoryModel categorySelected;
    public static LaundryServicesModel selectedService;
    public static CustomerModel currentUser;

    public static CategoryModel selectedCategory;
}
