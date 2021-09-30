package my.laundryapp.app.Common;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import my.laundryapp.app.Model.AddonModel;
import my.laundryapp.app.Model.BestDealModel;
import my.laundryapp.app.Model.CategoryModel;
import my.laundryapp.app.Model.CommentModel;
import my.laundryapp.app.Model.CustomerModel;
import my.laundryapp.app.Model.LaundryServicesModel;
import my.laundryapp.app.Model.ShippingOrderModel;
import my.laundryapp.app.Model.SizeModel;
import my.laundryapp.app.Model.TokenModel;
import my.laundryapp.app.R;
import my.laundryapp.app.services.MyFCMServices;

public class Common {
    //public static final String USER_REFERENCES = "Customer";
    
    public static final int DEFAULT_COLUMN_COUNT =0 ;
    public static final int FULL_WIDTH_COLUMN =1 ;
    public static final String CATEGORY_REF = "Category";
    public static final String COMMENT_REF = "Comments";
    public static final String ORDER_REF ="Order" ;
    public static final String NOTI_TITLE = "title" ;
    public static final String NOTI_CONTENT = "content";
    public static final String USER_REFERENCES = "Customer" ;
    public static final String REQUEST_REFUND_MODEL = "RequestRefund";
    public static final String IS_SUBSCRIBE_NEWS = "IS_SUBSCRIBE_NEWS";
    public static final String NEWS_TOPIC = "news";
    public static final String IS_SEND_IMAGE = "IS_SEND_IMAGE" ;
    public static final String IMAGE_URL = "IMAGE_URL" ;
    private static final String TOKEN_REF ="Tokens" ;
    public static final String SHIPPING_ORDER_REF = "ShippingOrder";
    public static CategoryModel categorySelected;
    public static LaundryServicesModel selectedService;
    //public static CustomerModel currentUser;

    public static CategoryModel selectedCategory;
    public static LaundryServicesModel selectedServiceRating;
    public static CustomerModel currentUser;
    public static ShippingOrderModel currentShippingOrder;

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
                return "Sunday";
            case 2:
                return "Monday";
            case 3:
                return "Tuesday";
            case 4:
                return "Wednesday";
            case 5:
                return "Thursday";
            case 6:
                return "Friday";
            case 7:
                return "Saturday";
            default:
                return "Unk";

        }
    }

    public static float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);


        return -1;
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

    public static void showNotification(Context context, int id, String title, String content, Intent intent) {

        PendingIntent pendingIntent = null;
        if(intent != null)
            pendingIntent = PendingIntent.getActivity(context,id,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        String NOTIFICATION_CHANNEL_ID = "edmt_dev_eat_v2";
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "Eat It V2", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Eat It V2");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000});
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_menu_camera));
        if(pendingIntent != null)
            builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        notificationManager.notify(id,notification);

    }

    public static void updateToken(Context context, String newToken) {
        if(Common.currentUser != null)
        {
            FirebaseDatabase.getInstance()
                    .getReference(Common.TOKEN_REF)
                    .child(Common.currentUser.getCustUid())
                    .setValue(new TokenModel(Common.currentUser.getPhoneNumber(),newToken))
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    });
        }


    }

    public static String createTopicOrder() {
        return new StringBuilder("/topics/new_order").toString();
    }
    public static List<LatLng> decodePoly(String encoded) {
        List poly = new ArrayList();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);

        }
        return poly;
    }

    public static String getListAddon(List<AddonModel> addonModels) {


        StringBuilder result = new StringBuilder();
        for(AddonModel addonModel:addonModels)
        {
            result.append(addonModel.getName()).append(",");
        }

        return result.substring(0,result.length()-1); //remove last","
    }

    public static LaundryServicesModel findFoodInListById(CategoryModel categoryModel, String servicesId) {
    if(categoryModel.getServices() != null && categoryModel.getServices().size() > 0)
    {
        for (LaundryServicesModel laundryServicesModel:categoryModel.getServices())
            if (laundryServicesModel.getId().equals(servicesId))
                return laundryServicesModel;
            return null;

    }
    else
        return null;
    }

    public static void showNotificationBigStyle(Context context, int id, String title, String content, Bitmap bitmap, Intent intent) {

        PendingIntent pendingIntent = null;
        if(intent != null)
            pendingIntent = PendingIntent.getActivity(context,id,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        String NOTIFICATION_CHANNEL_ID = "edmt_dev_eat_v2";
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "Eat It V2", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Eat It V2");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000});
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(bitmap)
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap));


        if(pendingIntent != null)
            builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        notificationManager.notify(id,notification);

    }
}
