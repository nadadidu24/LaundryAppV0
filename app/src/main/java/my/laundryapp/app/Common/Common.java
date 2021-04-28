package my.laundryapp.app.Common;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

import my.laundryapp.app.Model.AddonModel;
import my.laundryapp.app.Model.BestDealModel;
import my.laundryapp.app.Model.CategoryModel;
import my.laundryapp.app.Model.CommentModel;
import my.laundryapp.app.Model.CustomerModel;
import my.laundryapp.app.Model.LaundryServicesModel;
import my.laundryapp.app.Model.SizeModel;

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

    public static String formatPRice(double price) {
        if(price != 0)
        {
            DecimalFormat df = new DecimalFormat("#,##0.00");
            df.setRoundingMode(RoundingMode.UP);
            String finalPrice = new StringBuilder(df.format(price)).toString();
            return  finalPrice.replace(".",",");
        }
        else
        return "0,00";

    }

    public static Double calculateExtraPrice(SizeModel userSelectedSize, List<AddonModel> userSelectedAddon) {
        Double result = 0.0;
                if(userSelectedSize == null && userSelectedAddon == null)
                    return 0.0;
                else  if (userSelectedSize==null)
                {
                    //if userselectedaddon !=null, we need sum price
                    for(AddonModel addonModel : userSelectedAddon)
                        result+=addonModel.getPrice();
                    return result;
                }
                else if(userSelectedAddon==null)
                {
                    return userSelectedSize.getPrice()*1.0;
                }
                else
                {
                    //if both size and addon selected
                   result = userSelectedSize.getPrice()*1.0;
                    for(AddonModel addonModel : userSelectedAddon)
                        result+=addonModel.getPrice();
                    return result;
                }

    }
}
