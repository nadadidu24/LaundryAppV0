package my.laundryapp.app.ui.serviceslist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import my.laundryapp.app.Adapter.MyServicesListAdapter;
import my.laundryapp.app.Common.Common;
import my.laundryapp.app.Model.LaundryServicesModel;
import my.laundryapp.app.R;

public class ServicesListFragment extends Fragment {

    private ServicesListViewModel slideshowViewModel;

    Unbinder unbinder;
    @BindView(R.id.recycler_services_list)
    RecyclerView recycler_service_list;

    LayoutAnimationController layoutAnimationController;
    MyServicesListAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                new ViewModelProvider(this).get(ServicesListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_services_list, container, false);
unbinder= ButterKnife.bind(this,root);
initViews();
        slideshowViewModel.getMutableLiveDataFoodList().observe(getViewLifecycleOwner(), new Observer<List<LaundryServicesModel>>() {
            @Override
            public void onChanged(List<LaundryServicesModel> laundryServicesModels) {
                adapter= new MyServicesListAdapter(getContext(),laundryServicesModels);
                recycler_service_list.setAdapter(adapter);
                recycler_service_list.setLayoutAnimation(layoutAnimationController);

            }
        });
        return root;
    }

    private void initViews() {

        ((AppCompatActivity)getActivity())
                .getSupportActionBar()
                .setTitle(Common.categorySelected.getName());

        recycler_service_list.setHasFixedSize(true);
        recycler_service_list.setLayoutManager(new LinearLayoutManager(getContext()));

        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(),R.anim.layout_item_from_left);
    }
}