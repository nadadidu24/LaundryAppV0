package my.laundryapp.app.Common;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

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
    public static final String ORDER_REF ="Order" ;
    public static CategoryModel categorySelected;
    public static LaundryServicesModel selectedService;
    public static CustomerModel currentUser;

    public static CategoryModel selectedCategory;
    public static LaundryServicesModel selectedServiceRating;

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

    public static void setSpanString(String welcome, String name, TextView textView, String s) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(welcome);

        SpannableString spannableString = new SpannableString(name);
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        spannableString.setSpan(boldSpan,0,name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(spannableString);
        builder.append(s);
        textView.setText(builder,TextView.BufferType.SPANNABLE);

    }

    public static String createOrderNumber() {
        return new StringBuilder()
                .append(System.currentTimeMillis())
                .append(Math.abs(new Random().nextInt()))
                .toString();
    }

    public static String getDateOfWeek(int i) {
        switch (i)
        {
            case 1:
                return "Monday";
            case 2:
                return "Tuesday";
            case 3:
                return "Wednesday";
            case 4:
                return "Thursday";
            case 5:
                return "Friday";
            case 6:
                return "Saturday";
            case 7:
                return "Sunday";
            default:
                return "Unk";

        }
    }

    public static String convertStatusToText(int orderStatus) {
        switch (orderStatus)
        {
            case 0:
                return "Placed";
            case 1:
                return "On the way for pick up";
            case 2:
                return "Laundry in progress";
            case 3:
                return "On the way for delivery";
            case 4:
                return "Delivered";
            case -1:
                return "Cancelled";
            default:
                return "Unk";
        }
    }
}
