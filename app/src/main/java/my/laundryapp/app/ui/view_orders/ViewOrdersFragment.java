package my.laundryapp.app.ui.view_orders;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.androidwidgets.formatedittext.widgets.FormatEditText;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import my.laundryapp.app.Adapter.MyOrdersAdapter;
import my.laundryapp.app.Callback.ILoadOrderCallbackListener;
import my.laundryapp.app.Common.Common;
import my.laundryapp.app.Common.MySwipeHelper;
import my.laundryapp.app.Database.CartItem;
import my.laundryapp.app.EventBus.CounterCardEvent;
import my.laundryapp.app.Model.CustomerModel;
import my.laundryapp.app.Model.Order;
import my.laundryapp.app.Model.RefundRequestModel;
import my.laundryapp.app.Model.ShippingOrderModel;
import my.laundryapp.app.R;
import my.laundryapp.app.TrackingOrderActivity;
import my.laundryapp.app.ui.catalog.CatalogViewModel;

public class ViewOrdersFragment extends Fragment implements ILoadOrderCallbackListener {

    //change
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userCustID;

    //change

    @BindView(R.id.recycler_orders)
    RecyclerView recycler_orders;

    AlertDialog dialog;

    private Unbinder unbinder;

    private ViewOrdersViewModel viewOrdersViewModel;

    private ILoadOrderCallbackListener listener;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewOrdersViewModel =
                new ViewModelProvider(this).get(ViewOrdersViewModel.class);
        View root = inflater.inflate(R.layout.fragment_view_order, container, false);
        unbinder = ButterKnife.bind(this, root);

        initViews(root);
        loadOrdersFromFirebase();

        viewOrdersViewModel.getMutableLiveDataOrderList().observe(getViewLifecycleOwner(), orderList -> {
            Collections.reverse(orderList);
            MyOrdersAdapter adapter = new MyOrdersAdapter(getContext(), orderList);
            recycler_orders.setAdapter(adapter);
        });

        return root;

    }

    private void loadOrdersFromFirebase() {

        //changes try
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Customer");
        userCustID = user.getUid();
        //

        reference.child(userCustID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CustomerModel userProfile = snapshot.getValue(CustomerModel.class);

                List<Order> orderList = new ArrayList<>();
                FirebaseDatabase.getInstance().getReference(Common.ORDER_REF)
                        .orderByChild("custUserId")
                        .equalTo(userProfile.getCustUid())
                        .limitToLast(100)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                                    Order order = orderSnapshot.getValue(Order.class);
                                    order.setOrderNumber(orderSnapshot.getKey());  //must set
                                    orderList.add(order);
                                }
                                listener.onLoadOrderSuccess(orderList);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                listener.onLoadOrderFailed(error.getMessage());

                            }
                        });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void initViews(View root) {

        listener = this;

        dialog = new SpotsDialog.Builder().setCancelable(false).setContext(getContext()).build();

        recycler_orders.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler_orders.setLayoutManager(layoutManager);
        recycler_orders.addItemDecoration(new DividerItemDecoration(getContext(), layoutManager.getOrientation()));

        MySwipeHelper mySwipeHelper = new MySwipeHelper(getContext(), recycler_orders, 250) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buf) {
                buf.add(new MyButton(getContext(), "Cancel Order", 30, 0, Color.parseColor("#FF3C30"),
                        pos -> {
                            Order orderModel = ((MyOrdersAdapter) recycler_orders.getAdapter()).getItemAtPosition(pos);
                            if (orderModel.getOrderStatus() == 0) {
                                if (orderModel.isCod()) {
                                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
                                    builder.setTitle("Cancel Order")
                                            .setMessage("Do you really want to cancel this order?")
                                            .setNegativeButton("NO", (dialog, which) -> dialog.dismiss())
                                            .setPositiveButton("YES", (dialog, which) -> {

                                                //cancel order in database
                                                Map<String, Object> update_data = new HashMap<>();
                                                update_data.put("orderStatus", -1);
                                                FirebaseDatabase.getInstance()
                                                        .getReference(Common.ORDER_REF)
                                                        .child(orderModel.getOrderNumber())
                                                        .updateChildren(update_data)
                                                        .addOnFailureListener(e -> Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show())
                                                        .addOnSuccessListener(aVoid -> {
                                                            orderModel.setOrderStatus(-1); //local update
                                                            ((MyOrdersAdapter) recycler_orders.getAdapter()).setItemAtPosition(pos, orderModel);
                                                            recycler_orders.getAdapter().notifyItemChanged(pos);
                                                            Toast.makeText(getContext(), "Cancel order success", Toast.LENGTH_SHORT).show();

                                                        });

                                            });
                                    androidx.appcompat.app.AlertDialog dialog = builder.create();
                                    dialog.show();
                                } else //online payment
                                {
                                    //show layout
                                    View layout_refund_request = LayoutInflater.from(getContext())
                                            .inflate(R.layout.layout_refund_request, null);
                                    //
                                    EditText edt_name = (EditText) layout_refund_request.findViewById(R.id.edt_card_name);
                                    FormatEditText edt_card_number = (FormatEditText) layout_refund_request.findViewById(R.id.edt_card_number);
                                    FormatEditText edt_card_exp = (FormatEditText) layout_refund_request.findViewById(R.id.edt_exp);

                                    //credit card format
                                    edt_card_number.setFormat("---- ---- ---- ----");
                                    edt_card_exp.setFormat("--/--");


                                    //show dialog for button
                                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
                                    builder.setTitle("Cancel Order")
                                            .setMessage("Do you really want to cancel this order?")
                                            .setView(layout_refund_request) //show layout
                                            .setNegativeButton("NO", (dialog, which) -> dialog.dismiss())
                                            .setPositiveButton("YES", (dialog, which) -> {

                                                RefundRequestModel refundRequestModel = new RefundRequestModel();
                                                refundRequestModel.setName(Common.currentUser.getName());
                                                refundRequestModel.setPhone(Common.currentUser.getPhoneNumber());
                                                refundRequestModel.setCardName(edt_name.getText().toString());
                                                refundRequestModel.setCardNumber(edt_card_number.getText().toString());
                                                refundRequestModel.setCardExp(edt_card_exp.getText().toString());
                                                refundRequestModel.setAmount(orderModel.getFinalPayment());

                                                //cancel order req refund in database
                                                FirebaseDatabase.getInstance()
                                                        .getReference(Common.REQUEST_REFUND_MODEL)
                                                        .child(orderModel.getOrderNumber())
                                                        .setValue(refundRequestModel)
                                                        .addOnFailureListener(e -> Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show())
                                                        .addOnSuccessListener(aVoid -> {
                                                            //update cancel order in database
                                                            Map<String, Object> update_data = new HashMap<>();
                                                            update_data.put("orderStatus", -1);
                                                            FirebaseDatabase.getInstance()
                                                                    .getReference(Common.ORDER_REF)
                                                                    .child(orderModel.getOrderNumber())
                                                                    .updateChildren(update_data)
                                                                    .addOnFailureListener(e -> Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show())
                                                                    .addOnSuccessListener(a -> {
                                                                        orderModel.setOrderStatus(-1); //local update
                                                                        ((MyOrdersAdapter) recycler_orders.getAdapter()).setItemAtPosition(pos, orderModel);
                                                                        recycler_orders.getAdapter().notifyItemChanged(pos);
                                                                        Toast.makeText(getContext(), "Cancel order success", Toast.LENGTH_SHORT).show();

                                                                    });
                                                        });

                                            });
                                    androidx.appcompat.app.AlertDialog dialog = builder.create();
                                    dialog.show();
                                }

                            } else {
                                Toast.makeText(getContext(), new StringBuilder("Your order was changed to")
                                        .append(Common.convertStatusToText(orderModel.getOrderStatus()))
                                        .append(",so you can't cancel"), Toast.LENGTH_SHORT).show();

                            }


                        }));

                buf.add(new MyButton(getContext(), "Track Order", 30, 0, Color.parseColor("#001970"),
                        pos -> {
                            Order orderModel = ((MyOrdersAdapter) recycler_orders.getAdapter()).getItemAtPosition(pos);

                            //fetch from firebase
                            FirebaseDatabase.getInstance()
                                    .getReference(Common.SHIPPING_ORDER_REF)
                                    .child(orderModel.getOrderNumber())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(snapshot.exists())
                                            {

                                                Common.currentShippingOrder = snapshot.getValue(ShippingOrderModel.class);
                                                Common.currentShippingOrder.setKey(snapshot.getKey());
                                                if(Common.currentShippingOrder.getCurrentLat() != -1 &&
                                                Common.currentShippingOrder.getCurrentLng() != -1)
                                                {
                                                    startActivity(new Intent(getContext(), TrackingOrderActivity.class));

                                                }
                                                else
                                                {
                                                    Toast.makeText(getContext(), "Your order not on the way to pickup yet,pls wait", Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                            else
                                            {
                                                Toast.makeText(getContext(), "Your order just placed, must be wait for pick up", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(getContext(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });


                        }));
            }
        };
    }

    @Override
    public void onLoadOrderSuccess(List<Order> orderList) {
        dialog.dismiss();
        viewOrdersViewModel.setMutableLiveDataOrderList(orderList);

    }

    @Override
    public void onLoadOrderFailed(String message) {
        dialog.dismiss();
        Toast.makeText(getContext(), "" + message, Toast.LENGTH_SHORT).show();

    }
}