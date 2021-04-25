package my.laundryapp.app.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.asksira.loopingviewpager.LoopingViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import my.laundryapp.app.Adapter.MyBestDealAdapter;
import my.laundryapp.app.Adapter.MyRecommendedCategoriesAdapter;
import my.laundryapp.app.R;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    Unbinder unbinder;


    @BindView(R.id.recycler_servives)
    RecyclerView recycler_services;
    @BindView(R.id.viewpager)
    LoopingViewPager viewPager;

    LayoutAnimationController layoutAnimationController;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
unbinder = ButterKnife.bind(this,root);
init();
homeViewModel.getRecommendedList().observe(getViewLifecycleOwner(),recommendedServicesModels ->{
 //create adapter
    MyRecommendedCategoriesAdapter adapter = new MyRecommendedCategoriesAdapter(getContext(),recommendedServicesModels);
    recycler_services.setAdapter(adapter);
    recycler_services.setLayoutAnimation(layoutAnimationController);

} );

homeViewModel.getBestDealList().observe(getViewLifecycleOwner(),bestDealModels -> {
    MyBestDealAdapter adapter = new MyBestDealAdapter(getContext(),bestDealModels,true);
    viewPager.setAdapter(adapter);
});


return root;
    }

    private void init() {
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(),R.anim.layout_item_from_left);
        recycler_services.setHasFixedSize(true);
        recycler_services.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false));
    }

    @Override
    public void onResume() {
        super.onResume();
        viewPager.resumeAutoScroll();
    }

    @Override
    public void onPause() {
        viewPager.pauseAutoScroll();
        super.onPause();
    }
}