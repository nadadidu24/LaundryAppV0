package my.laundryapp.app.ui.view_orders;

import android.app.AlertDialog;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
import my.laundryapp.app.Adapter.MyOrdersAdapter;
import my.laundryapp.app.Callback.ILoadOrderCallbackListener;
import my.laundryapp.app.Common.Common;
import my.laundryapp.app.Model.CustomerModel;
import my.laundryapp.app.Model.Order;
import my.laundryapp.app.R;
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
        unbinder = ButterKnife.bind(this,root);

        initViews(root);
        loadOrdersFromFirebase();

        viewOrdersViewModel.getMutableLiveDataOrderList().observe(getViewLifecycleOwner(),orderList -> {
            MyOrdersAdapter adapter = new MyOrdersAdapter(getContext(),orderList);
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

                                for(DataSnapshot orderSnapshot:snapshot.getChildren())
                                {
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
        recycler_orders.addItemDecoration(new DividerItemDecoration(getContext(),layoutManager.getOrientation()));

    }

    @Override
    public void onLoadOrderSuccess(List<Order> orderList) {
        dialog.dismiss();
        viewOrdersViewModel.setMutableLiveDataOrderList(orderList);
        
    }

    @Override
    public void onLoadOrderFailed(String message) {
        dialog.dismiss();
        Toast.makeText(getContext(), ""+message, Toast.LENGTH_SHORT).show();

    }
}