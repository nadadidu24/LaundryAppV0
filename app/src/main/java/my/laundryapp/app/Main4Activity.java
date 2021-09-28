package my.laundryapp.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import my.laundryapp.app.Common.Common;
import my.laundryapp.app.Database.CartDataSource;
import my.laundryapp.app.Database.CartDatabase;
import my.laundryapp.app.Database.LocalCartDataSource;
import my.laundryapp.app.EventBus.BestDealItemClick;
import my.laundryapp.app.EventBus.CategoryClick;
import my.laundryapp.app.EventBus.CounterCardEvent;
import my.laundryapp.app.EventBus.HideFABCart;
import my.laundryapp.app.EventBus.MenuItemBack;
import my.laundryapp.app.EventBus.RecommendedCategoryClick;
import my.laundryapp.app.EventBus.ServiceItemClick;
import my.laundryapp.app.Model.CategoryModel;
import my.laundryapp.app.Model.CustomerModel;
import my.laundryapp.app.Model.LaundryServicesModel;

public class Main4Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Place placeSelected;
    private AutocompleteSupportFragment place_fragment;
    private PlacesClient placesClient;
    private List<Place.Field> placeFields = Arrays.asList(Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG);

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private NavController navController;
    NavigationView navigationView;

    private CartDataSource cartDataSource;

    android.app.AlertDialog dialog;

    int menuClickId=1;

    //change
    private FirebaseUser user;
    private DatabaseReference reference;
    private DatabaseReference userRef;
    private String userCustID;

    //change

    @BindView(R.id.fab)
    CounterFab fab;


    @Override
    protected void onResume() {
        super.onResume();
        countCartItem();
    }

    @Override
    @SuppressWarnings("DEPRECATION")
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            final WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.statusBars());
            }
        } else {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            );
        }

        initPlaceClient();

        //ini TESTT
        userRef = FirebaseDatabase.getInstance().getReference(Common.USER_REFERENCES);
        //INI TEST

        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();

        ButterKnife.bind(this);

        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(this).cartDAO());



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        centerTitle();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.nav_cart);
            }
        });
        drawer = findViewById(R.id.drawer_layout);
         navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_catalog, R.id.nav_services_list, R.id.nav_service_detail1, R.id.nav_cart,R.id.nav_view_orders)
                .setOpenableLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        /* i tambah bawah ni
         */
navigationView.setNavigationItemSelectedListener(this);
navigationView.bringToFront(); //fixed



        userNameInBar();

        countCartItem();


    }

    private void initPlaceClient() {
        Places.initialize(this,getString(R.string.google_maps_key));
        placesClient = Places.createClient(this);    }

    private void centerTitle() {
        ArrayList<View> textViews = new ArrayList<>();

        getWindow().getDecorView().findViewsWithText(textViews, getTitle(), View.FIND_VIEWS_WITH_TEXT);

        if(textViews.size() > 0) {
            AppCompatTextView appCompatTextView = null;
            if(textViews.size() == 1) {
                appCompatTextView = (AppCompatTextView) textViews.get(0);
            } else {
                for(View v : textViews) {
                    if(v.getParent() instanceof Toolbar) {
                        appCompatTextView = (AppCompatTextView) v;
                        break;
                    }
                }
            }

            if(appCompatTextView != null) {
                ViewGroup.LayoutParams params = appCompatTextView.getLayoutParams();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;

                appCompatTextView.setLayoutParams(params);
                //appCompatTextView.setTextColor(getResources().getColor(R.color.greyblack));
                appCompatTextView.setTypeface(ResourcesCompat.getFont(this, R.font.mulliregular));
                appCompatTextView.setPadding(0,0,50,0);
                appCompatTextView.setGravity(Gravity.CENTER);
                appCompatTextView.setGravity(Gravity.CENTER_HORIZONTAL);

            }
        }
    }

    private void userNameInBar() {
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

                View headerView = navigationView.getHeaderView(0);
                TextView txt_user = (TextView) headerView.findViewById(R.id.txt_user);
                Common.setSpanString("Hi ",userProfile.getName(),txt_user,"!");


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main4, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);
        drawer.closeDrawers ();
        switch (item.getItemId())
        {
            case R.id.nav_home:
                if(item.getItemId() != menuClickId)
                    navController.navigate(R.id.nav_home);
                break;
            case R.id.nav_catalog:
                if(item.getItemId() != menuClickId)
                    navController.navigate(R.id.nav_catalog);
                break;
            case R.id.nav_sign_out:

                    signOut();
                break;
            case R.id.nav_update_info:

                showUpdateInfoDialogue();
                break;
            case R.id.nav_cart:
                if(item.getItemId() != menuClickId)
                    navController.navigate(R.id.nav_cart);
                break;
            case R.id.nav_view_orders:
                if(item.getItemId() != menuClickId)
                    navController.navigate(R.id.nav_view_orders);
                break;

        }
        menuClickId = item.getItemId();
        return true;
    }

    private void showUpdateInfoDialogue() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this, R.style.myFullscreenAlertDialogStyle);
        //builder.setTitle("Register");
        //builder.setMessage("Please Fill infromation . \nAdmin will accept sooner");

        View itemView = LayoutInflater.from(this).inflate(R.layout.layout_update_profile, null);
        EditText edt_name = (EditText) itemView.findViewById(R.id.edt_name);
        EditText edt_phone = (EditText) itemView.findViewById(R.id.edt_phone);
        //EditText edt_address = (EditText) itemView.findViewById(R.id.edt_address);
        TextView txt_address_detail = (TextView) itemView.findViewById(R.id.txt_address_detail);

        edt_name.requestFocus();
        edt_name.setCursorVisible(true);
        edt_name.setTextIsSelectable(true);
        edt_name.requestFocusFromTouch();
        edt_name.clearFocus();

        place_fragment = (AutocompleteSupportFragment)getSupportFragmentManager()
                .findFragmentById(R.id.places_autocomplete_fragment);
        place_fragment.setPlaceFields(placeFields);
        place_fragment.setHint("Home");
        place_fragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                placeSelected = place;
                txt_address_detail.setText(place.getAddress());

            }

            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(Main4Activity.this, ""+status.getStatusMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        //set data
        edt_name.setText(Common.currentUser.getName());
        txt_address_detail.setText(Common.currentUser.getAddress());
        edt_phone.setText(user.getPhoneNumber());
        builder.setNegativeButton("CANCEL", (dialog, which) -> {
            dialog.dismiss();
        })
                .setPositiveButton("UPDATE", (dialog, which) -> {
                   if(placeSelected != null)
                   {
                       if (TextUtils.isEmpty(edt_name.getText().toString())) {
                           Toast.makeText(Main4Activity.this, "Please enter your name", Toast.LENGTH_SHORT).show();
                           return;
                       }

                       Map<String,Object> update_data = new HashMap<>();
                       update_data.put("name",edt_name.getText().toString());
                       update_data.put("address",txt_address_detail.getText().toString());
                       update_data.put("lat",placeSelected.getLatLng().latitude);
                       update_data.put("lng",placeSelected.getLatLng().longitude);
                       
                       FirebaseDatabase.getInstance()
                               .getReference(Common.USER_REFERENCES)
                               .child(Common.currentUser.getCustUid())
                               .updateChildren(update_data)
                               .addOnFailureListener(new OnFailureListener() {
                                   @Override
                                   public void onFailure(@NonNull Exception e) {
                                       dialog.dismiss();
                                       Toast.makeText(Main4Activity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                       
                                   }
                               })
                               .addOnSuccessListener(new OnSuccessListener<Void>() {
                                   @Override
                                   public void onSuccess(Void aVoid) {
                                       dialog.dismiss();
                                       Toast.makeText(Main4Activity.this, "Update info success", Toast.LENGTH_SHORT).show();
                                       Common.currentUser.setName(update_data.get("name").toString());
                                       Common.currentUser.setAddress(update_data.get("address").toString());
                                       Common.currentUser.setLat(Double.parseDouble(update_data.get("lat").toString()));
                                       Common.currentUser.setLng(Double.parseDouble(update_data.get("lng").toString()));
                                   }
                               });

                   }
                   else
                   {
                       Toast.makeText(Main4Activity.this, "Please select address", Toast.LENGTH_SHORT).show();

                   }

                });


        builder.setView(itemView);

        androidx.appcompat.app.AlertDialog resgisterDialog = builder.create();
        resgisterDialog.setOnDismissListener(dialog -> {
            FragmentTransaction fragmentTransaction= getSupportFragmentManager().beginTransaction();
            fragmentTransaction.remove(place_fragment);
            fragmentTransaction.commit();

        });
        resgisterDialog.show();
        // Buttons
        Button buttonOK = resgisterDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        buttonOK.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));

        Button buttonNo = resgisterDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        buttonNo.setTextColor(ContextCompat.getColor(this, R.color.grey));
    }

    //eventbus


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onCategorySelected (CategoryClick event)
    {
        if(event.isSuccess())
        {
            navController.navigate(R.id.nav_services_list);
            //Toast.makeText(this,"Click to "+event.getCategoryModel().getName(),Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onServicesItemClick (ServiceItemClick event)
    {
        if(event.isSuccess())
        {
            navController.navigate(R.id.nav_service_detail);
            //Toast.makeText(this,"Click to "+event.getCategoryModel().getName(),Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onHideFABEvent (HideFABCart event)
    {
        if(event.isHidden())
        {
            fab.hide();
        }
        else
            fab.show();
    }


    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onCartCounter (CounterCardEvent event)
    {
        if(event.isSuccess())
        {
            countCartItem();
        }
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onBestDealItemClick (BestDealItemClick event)
    {
        if(event.getBestDealModel() != null)
        {
            dialog.show();

            FirebaseDatabase.getInstance()
                    .getReference("Category")
                    .child(event.getBestDealModel().getCatalog_id())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists())
                            {
                                Common.categorySelected = snapshot.getValue(CategoryModel.class);
                                Common.categorySelected.setCatalog_id(snapshot.getKey());
                                //load services
                                FirebaseDatabase.getInstance()
                                        .getReference("Category")
                                        .child(event.getBestDealModel().getCatalog_id())
                                        .child("services")
                                        .orderByChild("id")
                                        .equalTo(event.getBestDealModel().getServices_id())
                                        .limitToLast(1)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if(snapshot.exists())
                                                {
                                                    for(DataSnapshot itemSnapshot:snapshot.getChildren())
                                                    {
                                                        Common.selectedService = itemSnapshot.getValue(LaundryServicesModel.class);
                                                        Common.selectedService.setKey(itemSnapshot.getKey());
                                                    }
                                                    navController.navigate(R.id.nav_service_detail);

                                                }
                                                else
                                                {

                                                    Toast.makeText(Main4Activity.this, "Item not exist", Toast.LENGTH_SHORT).show();
                                                }
                                                dialog.dismiss();

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                dialog.dismiss();
                                                Toast.makeText(Main4Activity.this, "Item not exist", Toast.LENGTH_SHORT).show();
                                            }
                                        });


                            }
                            else
                            {
                                dialog.dismiss();
                                Toast.makeText(Main4Activity.this, "Item not exist", Toast.LENGTH_SHORT).show();
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            dialog.dismiss();
                            Toast.makeText(Main4Activity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        }
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onRecommendedItemClick (RecommendedCategoryClick event)
    {
        if(event.getRecommendedServicesModel() != null)
        {
            dialog.show();

            FirebaseDatabase.getInstance()
                    .getReference("Category")
                    .child(event.getRecommendedServicesModel().getCatalog_id())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists())
                            {
                                Common.categorySelected = snapshot.getValue(CategoryModel.class);
                                Common.categorySelected.setCatalog_id(snapshot.getKey());
                                //load services
                                FirebaseDatabase.getInstance()
                                        .getReference("Category")
                                        .child(event.getRecommendedServicesModel().getCatalog_id())
                                        .child("services")
                                        .orderByChild("id")
                                        .equalTo(event.getRecommendedServicesModel().getServices_id())
                                        .limitToLast(1)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if(snapshot.exists())
                                                {
                                                    for(DataSnapshot itemSnapshot:snapshot.getChildren())
                                                    {
                                                        Common.selectedService = itemSnapshot.getValue(LaundryServicesModel.class);
                                                        Common.selectedService.setKey(itemSnapshot.getKey());
                                                    }
                                                    navController.navigate(R.id.nav_service_detail);

                                                }
                                                else
                                                {

                                                    Toast.makeText(Main4Activity.this, "Item not exist", Toast.LENGTH_SHORT).show();
                                                }
                                                dialog.dismiss();

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                dialog.dismiss();
                                                Toast.makeText(Main4Activity.this, "Item not exist", Toast.LENGTH_SHORT).show();
                                            }
                                        });


                            }
                            else
                            {
                                dialog.dismiss();
                                Toast.makeText(Main4Activity.this, "Item not exist", Toast.LENGTH_SHORT).show();
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            dialog.dismiss();
                            Toast.makeText(Main4Activity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        }
    }

    private void countCartItem() {

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

                cartDataSource.countItemInCart(userProfile.getCustUid())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<Integer>() {
                            @Override
                            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                            }

                            @Override
                            public void onSuccess(@io.reactivex.annotations.NonNull Integer integer) {
                                fab.setCount(integer);

                            }

                            @Override
                            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                if(!e.getMessage().contains("Query returned empty")) {

                                    Toast.makeText(Main4Activity.this, "[COUNT CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                else
                                    fab.setCount(0);

                            }
                        });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }






    private void signOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sign out")
                .setMessage("Do you really want to sign out?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Common.selectedService = null;
                Common.categorySelected =null;

                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(Main4Activity.this,second.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                startActivity(intent);
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void countCartAgain (CounterCardEvent event)
    {
        if(event.isSuccess())
            countCartItem();


    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onMenuItemBack (MenuItemBack event)
    {
        menuClickId = -1;
        if(getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStack();
        //navController.popBackStack(R.id.nav_home,true);
    }

}