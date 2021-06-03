package my.laundryapp.app.providerCommon;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.widget.TextView;

import my.laundryapp.app.providerModel.providerLaundryProviderUserModel;
import my.laundryapp.app.providerModel.providerServerUserModel;

public class providercommon {
    public static final String SERVER_REF = "LaundryProvider"; //nnti tukar ke Server kalau silap
    public static providerServerUserModel currentServerUser;
    public static providerLaundryProviderUserModel currentLaundryProviderUser;


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

}
