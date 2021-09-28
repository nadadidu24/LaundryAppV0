package my.laundryapp.app.ui.cart;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.os.Parcelable;
import android.telecom.TelecomManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import my.laundryapp.app.Adapter.MyCartAdapter;
import my.laundryapp.app.Callback.ILoadTimeFromFirebaseListener;
import my.laundryapp.app.Common.Common;
import my.laundryapp.app.Common.MySwipeHelper;
import my.laundryapp.app.Database.CartDataSource;
import my.laundryapp.app.Database.CartDatabase;
import my.laundryapp.app.Database.CartItem;
import my.laundryapp.app.Database.LocalCartDataSource;
import my.laundryapp.app.EventBus.CounterCardEvent;
import my.laundryapp.app.EventBus.HideFABCart;
import my.laundryapp.app.EventBus.MenuItemBack;
import my.laundryapp.app.EventBus.UpdateItemInCart;
import my.laundryapp.app.Main4Activity;
import my.laundryapp.app.Model.CustomerModel;
import my.laundryapp.app.Model.FCMResponse;
import my.laundryapp.app.Model.FCMSendData;
import my.laundryapp.app.Model.Order;
import my.laundryapp.app.R;
import my.laundryapp.app.Remote.IFCMService;
import my.laundryapp.app.Remote.RetrofitFCMClient;
import my.laundryapp.app.accountmenu;
import my.laundryapp.app.braintree;
import my.laundryapp.app.customerregister;
import my.laundryapp.app.providerregisternew;
import my.laundryapp.app.second;
import my.laundryapp.app.ui.cart.CartViewModel;
import my.laundryapp.app.usermenu;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class CartFragment extends Fragment implements ILoadTimeFromFirebaseListener {

    private Place placeSelected;
    private AutocompleteSupportFragment place_fragment;
    private PlacesClient placesClient;
    private List<Place.Field> placeFields = Arrays.asList(Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG);

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    //change
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userCustID;

    //change

    private Parcelable recyclerViewState;
    private CartDataSource cartDataSource;

    LocationRequest locationRequest;
    LocationCallback locationCallback;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location currentLocation;

    IFCMService ifcmService;
    ILoadTimeFromFirebaseListener listener;


    @BindView(R.id.recycler_cart1)
    RecyclerView recycler_cart1;
    @BindView(R.id.txt_total_price)
    TextView txt_total_price;
    @BindView(R.id.txt_empty_cart)
    TextView txt_empty_cart;
    @BindView(R.id.group_place_holder)
    CardView group_place_holder;

    @OnClick(R.id.btn_place_order)
    void onPlaceOrderClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.myFullscreenAlertDialogStyle);


        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_place_order, null);



        //EditText edt_address = (EditText) view.findViewById(R.id.edt_address);
        EditText edt_comment = (EditText) view.findViewById(R.id.edt_comment);
        TextView txt_address = (TextView) view.findViewById(R.id.txt_address_detail);
        RadioButton rdi_home = (RadioButton) view.findViewById(R.id.rdi_home_address);
        RadioButton rdi_other_address = (RadioButton) view.findViewById(R.id.rdi_other_address);
        RadioButton rdi_ship_to_this = (RadioButton) view.findViewById(R.id.rdi_ship_this_address);
        RadioButton rdi_cod = (RadioButton) view.findViewById(R.id.rdi_ood);
        RadioButton rdi_braintree = (RadioButton) view.findViewById(R.id.rdi_braintree);

        place_fragment = (AutocompleteSupportFragment)getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.places_autocomplete_fragment);
        place_fragment.setPlaceFields(placeFields);
        place_fragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                placeSelected = place;
                txt_address.setText(place.getAddress());

            }

            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(getContext(), ""+status.getStatusMessage(), Toast.LENGTH_SHORT).show();

            }
        });


        //Data
        txt_address.setText(Common.currentUser.getAddress());
        place_fragment.setHint(Common.currentUser.getAddress());

        //event
        rdi_home.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                place_fragment.setText("");
                place_fragment.setHint("");
                txt_address.setText(Common.currentUser.getAddress()); //nanti tukar
                txt_address.setVisibility(View.VISIBLE);
                place_fragment.setHint(Common.currentUser.getAddress());

            }

        });
        rdi_other_address.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                /*
                txt_address.setText(""); //clear
                edt_address.setHint("Enter your pick-up and delivery address");
                edt_address.requestFocus();
                edt_address.setCursorVisible(true);
                edt_address.setTextIsSelectable(true);
                edt_address.requestFocusFromTouch();
                edt_address.clearFocus();

                 */
                txt_address.setText(""); //clear
                place_fragment.setText("");
                place_fragment.setHint("Search");
                txt_address.setVisibility(View.VISIBLE);


            }

        });
        rdi_ship_to_this.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                //Toast.makeText(getContext(), "Coming soon with Google API", Toast.LENGTH_SHORT).show();
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                fusedLocationProviderClient.getLastLocation()
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            txt_address.setVisibility(View.GONE);
                        })
                        .addOnCompleteListener(task -> {
                            String coordinates = new StringBuilder()
                                    .append(task.getResult().getLatitude())
                                    .append("/")
                                    .append(task.getResult().getLongitude()).toString();

                            Single<String> singleAddress = Single.just(getAddressFromLatLng(task.getResult().getLatitude(),
                                    task.getResult().getLongitude()));

                            Disposable disposable = singleAddress.subscribeWith(new DisposableSingleObserver<String>() {
                                @Override
                                public void onSuccess(@io.reactivex.annotations.NonNull String s) {
                                    //edt_address.setText(coordinates);
                                    //edt_address.setText(s);
                                    place_fragment.setText("");
                                    place_fragment.setHint("");
                                    txt_address.setText(s);
                                    txt_address.setVisibility(View.VISIBLE);
                                    place_fragment.setHint(s);
                                }

                                @Override
                                public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                   // edt_address.setText(coordinates);
                                    txt_address.setText(e.getMessage());
                                    txt_address.setVisibility(View.VISIBLE);
                                }
                            });

                        });
            }

        });

        //builder.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.Yellow));

        builder.setView(view);

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();

        }).setPositiveButton("Proceed order", (dialog, which) -> {
            //Toast.makeText(getContext(), "Coming soon features!", Toast.LENGTH_SHORT).show();

            if (rdi_cod.isChecked())
                paymentCOD(txt_address.getText().toString(), edt_comment.getText().toString());
            if (rdi_braintree.isChecked())
                paymentBraintree();


        });

        AlertDialog dialog = builder.create();
        dialog.show();

        // Buttons
        Button buttonOK = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        buttonOK.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));

        Button buttonNo = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        buttonNo.setTextColor(ContextCompat.getColor(getContext(), R.color.grey));

        //AlertDialog button = dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));

    }

    private String getAddressFromLatLng(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        String result="";
        try {
            List<Address> addressList = geocoder.getFromLocation(latitude,longitude,1);
            if(addressList != null && addressList.size() > 0)
            {
                Address address = addressList.get(0); //always get first item
                StringBuilder sb = new StringBuilder(address.getAddressLine(0));
                result = sb.toString();
            }
            else
                result = "Adress not found";
        } catch (IOException e) {
            e.printStackTrace();
            result = e.getMessage();
        }
        return result;
    }

    private void paymentBraintree() {

        compositeDisposable.add(cartDataSource.getAllCart(Common.currentUser.getCustUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cartItems -> {
                    //when we have all cart items, we have total price
                    cartDataSource.sumPriceInCart(Common.currentUser.getCustUid())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new SingleObserver<Double>() {
                                @Override
                                public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                                }

                                @Override
                                public void onSuccess(@io.reactivex.annotations.NonNull Double totalPrice) {
                                    double finalPrice = totalPrice; // will modify later for discount

                                    Order order = new Order();
                                    order.setCustUserId(Common.currentUser.getCustUid());
                                    order.setCustUserName(Common.currentUser.getName());
                                    order.setCustUserPhone(Common.currentUser.getPhoneNumber());
                                    //order.setShippingAddress(address);
                                    //order.setComment(comment);


                                    //ada if actually
                                    order.setLat(-0.1f);
                                    order.setLng(-0.1f);

                                    order.setCartItemList(cartItems);
                                    order.setTotalPayment(totalPrice);
                                    order.setDiscount(0); // modify later with discount
                                    order.setFinalPayment(finalPrice);
                                    order.setCod(false);
                                    order.setTransactionId("Card Payment");

                                    //submit this order object to firebase
                                    //amount(order);
                                    syncLocalTimeWithGlobalTime(order);


                                    double contoh = order.getFinalPayment();
                                    double harga = order.getFinalPayment();
                                    Intent b = new Intent(getView().getContext(), braintree.class);
                                    Bundle c = new Bundle();
                                    //b.putExtra("harga", harga);
                                    c.putDouble("contoh", contoh);
                                    b.putExtras(c);
                                    startActivity(b);


                                }

                                @Override
                                public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                    Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();


                                }
                            });


                }, throwable -> {
                    Toast.makeText(getContext(), "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();

                }));

        //Intent intent = new Intent(getView().getContext(),braintree.class);
        //this.startActivity(intent);
    }


    private void paymentCOD(String address, String comment) {

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

                compositeDisposable.add(cartDataSource.getAllCart(userProfile.getCustUid())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(cartItems -> {
                            //when we have all cart items, we have total price
                            cartDataSource.sumPriceInCart(userProfile.getCustUid())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new SingleObserver<Double>() {
                                        @Override
                                        public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                                        }

                                        @Override
                                        public void onSuccess(@io.reactivex.annotations.NonNull Double totalPrice) {
                                            double finalPrice = totalPrice; // will modify later for discount
                                            Order order = new Order();
                                            order.setCustUserId(userProfile.getCustUid());
                                            order.setCustUserName(userProfile.getName());
                                            order.setCustUserPhone(userProfile.getPhoneNumber());
                                            order.setShippingAddress(address);
                                            order.setComment(comment);

                                            //newly added
                                            if(currentLocation != null)
                                            {
                                                order.setLat(currentLocation.getLatitude());
                                                order.setLng(currentLocation.getLongitude());
                                            }
                                            else
                                            {
                                                //ada if actually
                                                order.setLat(-0.1f);
                                                order.setLng(-0.1f);
                                            }

                                            order.setCartItemList(cartItems);
                                            order.setTotalPayment(totalPrice);
                                            order.setDiscount(0); // modify later with discount
                                            order.setFinalPayment(finalPrice);
                                            order.setCod(true);
                                            order.setTransactionId("Cash on Delivery");

                                            //submit this order object to firebase
                                            syncLocalTimeWithGlobalTime(order);


                                        }

                                        @Override
                                        public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                            if (!e.getMessage().contains("Query returned empty result set"))
                                                Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                                        }
                                    });


                        }, throwable -> {
                            Toast.makeText(getContext(), "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();

                        }));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /*
        compositeDisposable.add(cartDataSource.getAllCart("user")
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(cartItems -> {
            //when we have all cart items, we have total price
            cartDataSource.sumPriceInCart("user")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Double>() {
                        @Override
                        public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                        }

                        @Override
                        public void onSuccess(@io.reactivex.annotations.NonNull Double totalPrice) {
                            double finalPrice = totalPrice; // will modify later for discount
                            Order order = new Order();
                            order.setCustUserId("user");
                            order.setCustUserName("user");
                            order.setCustUserPhone("user");
                            order.setShippingAddress("user");
                            order.setComment("user");


                            //ada if actually
                            order.setLat(-0.1f);
                            order.setLng(-0.1f);

                            order.setCartItemList(cartItems);
                            order.setTotalPayment(totalPrice);
                            order.setDiscount(0); // modify later with discount
                            order.setFinalPayment(finalPrice);
                            order.setCod(true);
                            order.setTransactionId("Cash on Delivery");

                            //submit this order object to firebase
                            writeOrdertoFirebase(order);




                        }

                        @Override
                        public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                            Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });


        }, throwable -> {
            Toast.makeText(getContext(), ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();

        }));

         */


    }

    private void amount(Order order) {
    }

    private void syncLocalTimeWithGlobalTime(Order order) {
        final DatabaseReference offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
        offsetRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long offset = snapshot.getValue(Long.class);
                long estimatedServerTimeMs = System.currentTimeMillis() + offset;
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
                Date resultDate = new Date(estimatedServerTimeMs);
                Log.d("TEST_DATE", "" + sdf.format(resultDate));

                listener.onLoadTimeSuccess(order, estimatedServerTimeMs);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onLoadTimeFailed(error.getMessage());

            }
        });
    }

    private void writeOrdertoFirebase(Order order) {

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

                FirebaseDatabase.getInstance()
                        .getReference(Common.ORDER_REF)
                        .child(Common.createOrderNumber())
                        .setValue(order)
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }).addOnCompleteListener(task -> {
                    //write success
                    cartDataSource.cleanCart(userProfile.getCustUid())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new SingleObserver<Integer>() {
                                @Override
                                public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                                }

                                @Override
                                public void onSuccess(@io.reactivex.annotations.NonNull Integer integer) {

                                    Map<String, String> notiData = new HashMap<>();
                                    notiData.put(Common.NOTI_TITLE, "New Order");
                                    notiData.put(Common.NOTI_CONTENT, "You have new order from" + Common.currentUser.getPhoneNumber());

                                    FCMSendData sendData = new FCMSendData(Common.createTopicOrder(), notiData);

                                    compositeDisposable.add(ifcmService.sendNotification(sendData)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(fcmResponse -> {
                                                //clean succes
                                                Toast.makeText(getContext(), "Congratulation,order place successfully!noti sent", Toast.LENGTH_SHORT).show();
                                                EventBus.getDefault().postSticky(new CounterCardEvent(true));


                                            }, throwable -> {
                                                Toast.makeText(getContext(), "Order sent but noti not sent", Toast.LENGTH_SHORT).show();
                                                EventBus.getDefault().postSticky(new CounterCardEvent(true));

                                            }));


                                }

                                @Override
                                public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                    Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private MyCartAdapter adapter;

    private Unbinder unbinder;

    private CartViewModel cartViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        cartViewModel =
                new ViewModelProvider(this).get(CartViewModel.class);
        View root = inflater.inflate(R.layout.fragment_cart, container, false);

        ifcmService = RetrofitFCMClient.getInstance().create(IFCMService.class);
        listener = this;

        cartViewModel.initCartDataSource(getContext());
        cartViewModel.getMutableLiveDataCartItem().observe(getViewLifecycleOwner(), new Observer<List<CartItem>>() {
            @Override
            public void onChanged(List<CartItem> cartItems) {
                if (cartItems == null || cartItems.isEmpty()) {
                    group_place_holder.setVisibility(View.GONE);
                    txt_empty_cart.setVisibility(View.VISIBLE);
                    recycler_cart1.setVisibility(View.GONE);


                } else {
                    recycler_cart1.setVisibility(View.VISIBLE);
                    group_place_holder.setVisibility(View.VISIBLE);
                    txt_empty_cart.setVisibility(View.GONE);

                    adapter = new MyCartAdapter(getContext(), cartItems);
                    recycler_cart1.setAdapter(adapter);

                }
            }
        });
        unbinder = ButterKnife.bind(this, root);
        InitViews();
        initLocation();

        return root;

    }

    private void initLocation() {
        buildLocationRequest();
        buildLocationCallback();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void buildLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                currentLocation = locationResult.getLastLocation();
            }
        };
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10f);
    }

    private void InitViews() {
        initPlaceClient();

        setHasOptionsMenu(true);

        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(getContext()).cartDAO());

        EventBus.getDefault().postSticky(new HideFABCart(true));

        recycler_cart1.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler_cart1.setLayoutManager(layoutManager);
        recycler_cart1.addItemDecoration(new DividerItemDecoration(getContext(), layoutManager.getOrientation()));

        MySwipeHelper mySwipeHelper = new MySwipeHelper(getContext(), recycler_cart1, 200) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buf) {
                buf.add(new MyButton(getContext(), "Delete", 30, 0, Color.parseColor("#FF3C30"),
                        pos -> {
                            //Toast.makeText(getContext(),"Delete item Click!",Toast.LENGTH_SHORT).show();

                            CartItem cartItem = adapter.getItemAtPosition(pos);
                            cartDataSource.deleteCartItems(cartItem)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new SingleObserver<Integer>() {
                                        @Override
                                        public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                                        }

                                        @Override
                                        public void onSuccess(@io.reactivex.annotations.NonNull Integer integer) {
                                            adapter.notifyItemRemoved(pos);
                                            sumAllItemInCart();
                                            EventBus.getDefault().postSticky(new CounterCardEvent(true)); //upadate fab
                                            Toast.makeText(getContext(), "Delete item from cart success", Toast.LENGTH_SHORT).show();

                                        }

                                        @Override
                                        public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                            Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                                        }
                                    });


                        }));
            }
        };

        sumAllItemInCart();

    }

    private void initPlaceClient() {
        Places.initialize(getContext(),getString(R.string.google_maps_key));
        placesClient = Places.createClient(getContext());
    }

    private void sumAllItemInCart() {

        //changes try
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Customer");
        userCustID = user.getUid();
        //

        //changes
        reference.child(userCustID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CustomerModel userProfile = snapshot.getValue(CustomerModel.class);

                cartDataSource.sumPriceInCart(userProfile.getCustUid())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<Double>() {
                            @Override
                            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                            }

                            @Override
                            public void onSuccess(@io.reactivex.annotations.NonNull Double aDouble) {
                                txt_total_price.setText(new StringBuilder("Total: RM").append(aDouble));

                            }

                            @Override
                            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                if (!e.getMessage().contains("Query returned empty"))
                                    Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.action_settings).setVisible(false); //hide home menu alrady inflate
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.cart_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.action_clear_cart) {
            custcurrentuser();

            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void custcurrentuser() {

        //changes try
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Customer");
        userCustID = user.getUid();
        //

        reference.child(userCustID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CustomerModel userProfile = snapshot.getValue(CustomerModel.class);

                cartDataSource.cleanCart(userProfile.getCustUid())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<Integer>() {
                            @Override
                            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                            }

                            @Override
                            public void onSuccess(@io.reactivex.annotations.NonNull Integer integer) {
                                Toast.makeText(getContext(), "Clear Cart Success", Toast.LENGTH_SHORT).show();
                                EventBus.getDefault().postSticky(new CounterCardEvent(true));
                            }

                            @Override
                            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

    }

    @Override
    public void onStop() {
        EventBus.getDefault().postSticky(new HideFABCart(false));
        cartViewModel.onStop();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        if (fusedLocationProviderClient != null)
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        compositeDisposable.clear();


        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (fusedLocationProviderClient != null)
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onUpdateItemInCartEvent(UpdateItemInCart event) {
        if (event.getCartItem() != null) {
            //first, save state of recycler view
            recyclerViewState = recycler_cart1.getLayoutManager().onSaveInstanceState();
            cartDataSource.updateCartItems(event.getCartItem())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Integer>() {
                        @Override
                        public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                        }

                        @Override
                        public void onSuccess(@io.reactivex.annotations.NonNull Integer integer) {
                            calculateTotalPrice();
                            recycler_cart1.getLayoutManager().onRestoreInstanceState(recyclerViewState); //fix error refresh recycler view after update

                        }

                        @Override
                        public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                            Toast.makeText(getContext(), "[UPDATE CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void calculateTotalPrice() {

        //changes try
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Customer");
        userCustID = user.getUid();
        //

        //changes
        reference.child(userCustID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CustomerModel userProfile = snapshot.getValue(CustomerModel.class);

                cartDataSource.sumPriceInCart(userProfile.getCustUid())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<Double>() {
                            @Override
                            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                            }

                            @Override
                            public void onSuccess(@io.reactivex.annotations.NonNull Double price) {
                                txt_total_price.setText(new StringBuilder("Total: RM")
                                        .append(Common.formatPRice(price)));
                            }

                            @Override
                            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                if (!e.getMessage().contains("Query returned empty result set"))
                                    Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                //Toast.makeText(getContext(),"[SUM CART]"+e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /*
        cartDataSource.sumPriceInCart("Email")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Double>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@io.reactivex.annotations.NonNull Double price) {
                        txt_total_price.setText(new StringBuilder("Total: ")
                        .append(Common.formatPRice(price)));
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Toast.makeText(getContext(),"[SUM CART]"+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

         */
    }


    @Override
    public void onLoadTimeSuccess(Order order, long estimateTimeTaken) {
        order.setCreateDate(estimateTimeTaken);
        order.setOrderStatus(0);
        writeOrdertoFirebase(order);

    }


    @Override
    public void onLoadTimeFailed(String message) {
        Toast.makeText(getContext(), "" + message, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().postSticky(new MenuItemBack());
        super.onDestroy();
    }
}