package my.laundryapp.app.ui.servicesdetail;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import my.laundryapp.app.Common.Common;
import my.laundryapp.app.Database.CartDataSource;
import my.laundryapp.app.Database.CartDatabase;
import my.laundryapp.app.Database.CartItem;
import my.laundryapp.app.Database.LocalCartDataSource;
import my.laundryapp.app.EventBus.CounterCardEvent;
import my.laundryapp.app.EventBus.MenuItemBack;
import my.laundryapp.app.Model.AddonModel;
import my.laundryapp.app.Model.CommentModel;
import my.laundryapp.app.Model.CustomerModel;
import my.laundryapp.app.Model.LaundryServicesModel;
import my.laundryapp.app.Model.SizeModel;
import my.laundryapp.app.R;
import my.laundryapp.app.ui.comments.CommentFragment;

public class serviceDetailFragment extends Fragment implements TextWatcher {

    //change
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userCustID;

    //change

    private ServiceDetailViewModel serviceDetailViewModel;

    private CartDataSource cartDataSource;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private Unbinder unbinder;
    private android.app.AlertDialog waitingDialog;
    private BottomSheetDialog addonBottomSheetDialog;

    //View need inflate
    ChipGroup chip_group_addon;
    EditText edt_search;


    @BindView(R.id.img_services)
    ImageView img_services;
    @BindView(R.id.btnCart)
    CounterFab btnCart;
    @BindView(R.id.btn_rating)
    FloatingActionButton btn_rating;
    @BindView (R.id.services_name)
    TextView services_name;
    @BindView(R.id.services_description)
    TextView services_description;
    @BindView(R.id.service_price)
    TextView service_price;
    @BindView(R.id.number_button)
    ElegantNumberButton numberButton;
    @BindView(R.id.ratingBar)
    RatingBar ratingBar;
    @BindView(R.id.btnShowComment)
    Button btnShowComment;
    @BindView(R.id.rdi_group_size)
    RadioGroup rdi_group_size;
    @BindView(R.id.img_add_addon)
    ImageView img_add_on;
    @BindView(R.id.chip_group_user_selected_addon)
    ChipGroup chip_group_user_selected_addon;

    @OnClick(R.id.img_add_addon)
    void onAddonClick()
    {
        if(Common.selectedService.getAddon() != null)
        {
            displayAddonList(); //show all addon option
            addonBottomSheetDialog.show();
        }
    }

    @OnClick(R.id.btnCart)
    void onCartItemAdd()
    {
        //changes try
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Customer");
        userCustID = user.getUid();
        //

        //changes 3
        reference.child(userCustID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CustomerModel userProfile = snapshot.getValue(CustomerModel.class);

                CartItem cartItem = new CartItem();
                cartItem.setCustUid(userProfile.getCustUid());
                cartItem.setCustPhone(userProfile.getPhoneNumber());

                cartItem.setServicesId(Common.selectedService.getId());
                cartItem.setServicesName(Common.selectedService.getName());
                cartItem.setServicesImage(Common.selectedService.getImage());
                cartItem.setServicesPrice(Double.valueOf(String.valueOf(Common.selectedService.getPrice())));
                cartItem.setServicesQuantity(Integer.valueOf(numberButton.getNumber()));
                cartItem.setServicesExtraPrice(Common.calculateExtraPrice(Common.selectedService.getUserSelectedSize(),Common.selectedService.getUserSelectedAddon())); //becoz default we not choose size + addon so extra price 0
                if(Common.selectedService.getUserSelectedAddon() !=null)
                    cartItem.setServicesAddon(new Gson().toJson(Common.selectedService.getUserSelectedAddon()));
                else

                    cartItem.setServicesAddon("Default");
                if(Common.selectedService.getUserSelectedSize() !=null)
                    cartItem.setServicesSize(new Gson().toJson(Common.selectedService.getUserSelectedSize()));
                else
                    cartItem.setServicesSize("Default");

                cartDataSource.getItemWithAllOptionsInCart(userProfile.getCustUid(),
                        cartItem.getServicesId(),
                        cartItem.getServicesSize(),
                        cartItem.getServicesAddon())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<CartItem>() {
                            @Override
                            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                            }

                            @Override
                            public void onSuccess(@io.reactivex.annotations.NonNull CartItem cartItemFromDB) {
                                if(cartItemFromDB.equals(cartItem))
                                {
                                    //Already in database, just update
                                    cartItemFromDB.setServicesExtraPrice(cartItem.getServicesExtraPrice());
                                    cartItemFromDB.setServicesAddon(cartItem.getServicesAddon());
                                    cartItemFromDB.setServicesSize(cartItem.getServicesSize());
                                    cartItemFromDB.setServicesQuantity(cartItemFromDB.getServicesQuantity() + cartItem.getServicesQuantity());

                                    cartDataSource.updateCartItems(cartItemFromDB)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new SingleObserver<Integer>() {
                                                @Override
                                                public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                                                }

                                                @Override
                                                public void onSuccess(@io.reactivex.annotations.NonNull Integer integer) {
                                                    Toast.makeText(getContext(),"update cart success",Toast.LENGTH_SHORT).show();
                                                    EventBus.getDefault().postSticky(new CounterCardEvent(true));
                                                }

                                                @Override
                                                public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                                    Toast.makeText(getContext(),"[UPDATE CART]"+e.getMessage(),Toast.LENGTH_SHORT).show();

                                                }
                                            });

                                }
                                else
                                {
                                    //item not available in cart before, insert new
                                    compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItem)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(()-> {
                                                Toast.makeText(getContext(),"Add to cart success",Toast.LENGTH_SHORT).show();
                                                //here we will send a notify to mainactivity4 to update counter in cart
                                                EventBus.getDefault().postSticky(new CounterCardEvent(true));
                                            },throwable -> {
                                                Toast.makeText(getContext(),"CART ERROR" +throwable.getMessage(),Toast.LENGTH_SHORT).show();
                                            }));

                                }


                            }

                            @Override
                            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                if(e.getMessage().contains("empty"))
                                {
                                    //default,if cart empty,this code run
                                    compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItem)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(()-> {
                                                Toast.makeText(getContext(),"Add to cart success",Toast.LENGTH_SHORT).show();
                                                //here we will send a notify to mainactivity4 to update counter in cart
                                                EventBus.getDefault().postSticky(new CounterCardEvent(true));
                                            },throwable -> {
                                                Toast.makeText(getContext(),"CART ERROR" +throwable.getMessage(),Toast.LENGTH_SHORT).show();
                                            }));
                                }
                                else
                                    Toast.makeText(getContext(),"[GET CART]"+e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    private void displayAddonList() {
        if(Common.selectedService.getAddon().size() > 0)
        {
            chip_group_addon.clearCheck(); //clear check all views
            chip_group_addon.removeAllViews();

            edt_search.addTextChangedListener(this);

            //add all view
            for(AddonModel addonModel:Common.selectedService.getAddon())
            {
                
                    Chip chip = (Chip)getLayoutInflater().inflate(R.layout.layout_addon_item,null);
                    chip.setText(new StringBuilder(addonModel.getName()).append("(+$")
                            .append(addonModel.getPrice()).append(")"));
                    chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if(isChecked)
                        {
                            if(Common.selectedService.getUserSelectedAddon() == null)
                                Common.selectedService.setUserSelectedAddon(new ArrayList<>());
                            Common.selectedService.getUserSelectedAddon().add(addonModel);
                        }
                    });
                    chip_group_addon.addView(chip);

            }

        }
    }


    @OnClick(R.id.btn_rating)
    void OnRatingButtonClick()
    {
        showDialogRating();
    }

    @OnClick(R.id.btnShowComment)
    void onShowCommentButtonClick(){
        CommentFragment commentFragment = CommentFragment.getInstance();
        commentFragment.show(getActivity().getSupportFragmentManager(),"CommentFragment");
    }


    private void showDialogRating() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Rating Services");
        builder.setMessage("Please rate the services");

        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.layout_rating,null);

        RatingBar ratingBar= (RatingBar) itemView.findViewById(R.id.rating_bar);
        EditText edit_comment= (EditText) itemView.findViewById(R.id.edit_comment);

        builder.setView(itemView);

        builder.setNegativeButton("CANCEL", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.setPositiveButton("Okay", (dialog, which) -> {
            //changes try
            user = FirebaseAuth.getInstance().getCurrentUser();
            reference = FirebaseDatabase.getInstance().getReference("Customer");
            userCustID = user.getUid();
            //

            //changes 3
            reference.child(userCustID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    CustomerModel userProfile = snapshot.getValue(CustomerModel.class);
                    if(userProfile != null){
                        CommentModel commentModel = new CommentModel();
                        commentModel.setName(userProfile.getName());
                        commentModel.setUid(userProfile.getEmail());
                        commentModel.setComment(edit_comment.getText().toString());
                        commentModel.setRatingValue(ratingBar.getRating());
                        Map<String,Object> serverTimeStamp = new HashMap<>();
                        serverTimeStamp.put("timeStamp", ServerValue.TIMESTAMP);
                        commentModel.setCommentTimeStamp(serverTimeStamp);

                        serviceDetailViewModel.setCommentModel(commentModel);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(),"Thank you!",Toast.LENGTH_SHORT).show();

                }
            });


            /*
            CommentModel commentModel = new CommentModel();
            commentModel.setName("custName");
            commentModel.setUid("Email");
            commentModel.setComment(edit_comment.getText().toString());
            commentModel.setRatingValue(ratingBar.getRating());
            Map<String,Object> serverTimeStamp = new HashMap<>();
            serverTimeStamp.put("timeStamp", ServerValue.TIMESTAMP);
            commentModel.setCommentTimeStamp(serverTimeStamp);

            serviceDetailViewModel.setCommentModel(commentModel);

             */


        });

        AlertDialog dialog =builder.create();
        dialog.show();

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        serviceDetailViewModel =
                new ViewModelProvider(this).get(ServiceDetailViewModel.class);
        View root = inflater.inflate(R.layout.fragment_service_detail, container, false);
        unbinder = ButterKnife.bind(this,root);
        initViews();
        serviceDetailViewModel.getMutableLiveDataFood().observe(getViewLifecycleOwner(), laundryServicesModel -> {
            displayInfo(laundryServicesModel);
        });
        serviceDetailViewModel.getMutableLiveDataComment().observe(getViewLifecycleOwner(),commentModel -> {
           submitRatingToFirebase(commentModel);
        });
        return root;

    }

    private void initViews() {

        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(getContext()).cartDAO());

        waitingDialog = new SpotsDialog.Builder().setCancelable(false).setContext(getContext()).build();
        addonBottomSheetDialog = new BottomSheetDialog(getContext(),R.style.DialogStyle);
        View layout_addon_display = getLayoutInflater().inflate(R.layout.layout_addon_display,null);
        chip_group_addon = (ChipGroup)layout_addon_display.findViewById(R.id.chip_group_addon);
        edt_search = (EditText)layout_addon_display.findViewById(R.id.edt_search);
        addonBottomSheetDialog.setContentView(layout_addon_display);

        addonBottomSheetDialog.setOnDismissListener(dialog -> {
            displayUserSelectedAddon();
            calculateTotalPrice();

        });

    }

    private void displayUserSelectedAddon() {
        if(Common.selectedService.getUserSelectedAddon() !=null &&
        Common.selectedService.getUserSelectedAddon().size() >0)
        {
            chip_group_user_selected_addon.removeAllViews(); //clear all view already addded
            for(AddonModel addonModel : Common.selectedService.getUserSelectedAddon()) //add all available addon to list
            {
                Chip chip = (Chip)getLayoutInflater().inflate(R.layout.layout_chip_with_delete_icon,null);
                chip.setText(new StringBuilder(addonModel.getName()).append("(+$")
                .append(addonModel.getPrice()).append(")"));
                chip.setClickable(false);
                chip.setOnCloseIconClickListener(v -> {
                    //remove when user select delet
                    chip_group_user_selected_addon.removeView(v);
                    Common.selectedService.getUserSelectedAddon().remove(addonModel);
                    calculateTotalPrice();
                });
                chip_group_user_selected_addon.addView(chip);
            }
        }else
            chip_group_user_selected_addon.removeAllViews();
    }



    private void submitRatingToFirebase(CommentModel commentModel) {
        waitingDialog.show();
        //first,submit to comment ref
        FirebaseDatabase.getInstance()
                .getReference("Comments")
                .child(Common.selectedService.getId())
                .push()
                .setValue(commentModel)
                .addOnCompleteListener(task -> {
                      if(task.isSuccessful())
                      {


                          //after submit to commentref,update value rating average in services
                          addRatingToServices(commentModel.getRatingValue());



                      }
                      waitingDialog.dismiss();
                });

    }

    private void addRatingToServices(float ratingValue) {
        FirebaseDatabase.getInstance()
                .getReference("Category")
                .child(Common.categorySelected.getCatalog_id()) //select category
        .child("services") //select array list
        .child(Common.selectedService.getKey())
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    LaundryServicesModel laundryServicesModel = snapshot.getValue(LaundryServicesModel.class);
                    laundryServicesModel.setKey(Common.selectedService.getKey());

                    //apply rating
                    if(laundryServicesModel.getRatingValue() == null)
                        laundryServicesModel.setRatingValue(0d);
                    if(laundryServicesModel.getRatingCount()==null)
                        laundryServicesModel.setRatingCount(0l);
                    double sumRating = laundryServicesModel.getRatingValue()+ratingValue;
                    long ratingCount = laundryServicesModel.getRatingCount()+1;


                    Map<String,Object> updateData = new HashMap<>();
                    updateData.put("ratingValue",sumRating);
                    updateData.put("ratingCount",ratingCount);

                    //update data in variable
                    laundryServicesModel.setRatingValue(sumRating);
                    laundryServicesModel.setRatingCount(ratingCount);

                    snapshot.getRef()
                            .updateChildren(updateData)
                            .addOnCompleteListener(task -> {
                                waitingDialog.dismiss();
                                if(task.isSuccessful())
                                {

                                    /*Size
                                    for(SizeModel sizeModel: Common.selectedService.getSize())
                                    {
                                        RadioButton radioButton = new RadioButton(getContext());
                                        radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                                            if(isChecked)
                                                Common.selectedService.setUserSelectedSize(sizeModel);
                                            calculateTotalPrice(); //update Price


                                        });

                                     */
                                    Toast.makeText(getContext(),"Thank you!",Toast.LENGTH_SHORT).show();

                                    displayRating(laundryServicesModel);


                                    //Common.selectedService = laundryServicesModel;
                                    //serviceDetailViewModel.setServiceModel(laundryServicesModel);


                                }
                            });
                }
                else
                    waitingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                waitingDialog.dismiss();
                Toast.makeText(getContext(),""+error.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });//Because services items is array list so key is index of arraylist

    }

    private void displayRating(LaundryServicesModel laundryServicesModel) {

        if(laundryServicesModel.getRatingValue() != null)
            ratingBar.setRating(laundryServicesModel.getRatingValue().floatValue() / laundryServicesModel.getRatingCount());

    }

    private void displayInfo(LaundryServicesModel laundryServicesModel) {
        Glide.with(getContext()).load(laundryServicesModel.getImage()).into(img_services);
        services_name.setText(new StringBuilder(laundryServicesModel.getName()));
        services_description.setText(new StringBuilder(laundryServicesModel.getDescription()));
        service_price.setText(new StringBuilder(laundryServicesModel.getPrice().toString()));

        if(laundryServicesModel.getRatingValue() != null)
        ratingBar.setRating(laundryServicesModel.getRatingValue().floatValue() / laundryServicesModel.getRatingCount());

        ((AppCompatActivity)getActivity())
                .getSupportActionBar()
                .setTitle(Common.selectedService.getName());

        //Size
        for(SizeModel sizeModel: Common.selectedService.getSize())
        {
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(isChecked)
                    Common.selectedService.setUserSelectedSize(sizeModel);
                    calculateTotalPrice(); //update Price


            });

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1.0f);

            /*LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT,
                    1.0f); */
            radioButton.setLayoutParams(params);
            radioButton.setText(sizeModel.getName());
            radioButton.setTag(sizeModel.getPrice());

            rdi_group_size.addView(radioButton);
        }

        if(rdi_group_size.getChildCount() > 0)
        {
            RadioButton radioButton = (RadioButton)rdi_group_size.getChildAt(0);
            radioButton.setChecked(true);  //default first select
        }

        calculateTotalPrice();

    }

    private void calculateTotalPrice() {
        double totalPrice = Double.parseDouble(Common.selectedService.getPrice().toString()),displayPrice=0.0;

        //addon
        if(Common.selectedService.getUserSelectedAddon() != null && Common.selectedService.getUserSelectedAddon().size()>0)
            for(AddonModel addonModel : Common.selectedService.getUserSelectedAddon())
                totalPrice+= Double.parseDouble(addonModel.getPrice().toString());


        //size
        if(Common.selectedService.getUserSelectedSize() != null)
        totalPrice += Double.parseDouble(Common.selectedService.getUserSelectedSize().getPrice().toString());

        displayPrice = totalPrice * (Integer.parseInt(numberButton.getNumber()));
        displayPrice = Math.round(displayPrice*100.0/100.0);

        service_price.setText(new StringBuilder("").append(Common.formatPRice(displayPrice)).toString());




    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        chip_group_addon.clearCheck();
        chip_group_addon.removeAllViews();

        for(AddonModel addonModel:Common.selectedService.getAddon())
        {
            if(addonModel.getName().toLowerCase().contains(s.toString().toLowerCase()))
            {
                Chip chip = (Chip)getLayoutInflater().inflate(R.layout.layout_addon_item,null);
                chip.setText(new StringBuilder(addonModel.getName()).append("(+$")
                .append(addonModel.getPrice()).append(")"));
                chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if(isChecked)
                    {
                        if(Common.selectedService.getUserSelectedAddon() == null)
                            Common.selectedService.setUserSelectedAddon(new ArrayList<>());
                        Common.selectedService.getUserSelectedAddon().add(addonModel);
                    }
                });
                chip_group_addon.addView(chip);
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        //nothing
    }

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().postSticky(new MenuItemBack());
        super.onDestroy();
    }

}