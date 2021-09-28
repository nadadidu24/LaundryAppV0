package my.laundryapp.app.ui.serviceslist;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import my.laundryapp.app.Adapter.MyServicesListAdapter;
import my.laundryapp.app.Common.Common;
import my.laundryapp.app.EventBus.MenuItemBack;
import my.laundryapp.app.Model.CategoryModel;
import my.laundryapp.app.Model.LaundryServicesModel;
import my.laundryapp.app.R;

public class ServicesListFragment extends Fragment {

    private ServicesListViewModel servicesListViewModel;

    Unbinder unbinder;
    @BindView(R.id.recycler_services_list)
    RecyclerView recycler_service_list;

    LayoutAnimationController layoutAnimationController;
    MyServicesListAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        servicesListViewModel =
                new ViewModelProvider(this).get(ServicesListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_services_list, container, false);
        unbinder= ButterKnife.bind(this,root);
        initViews();
        servicesListViewModel.getMutableLiveDataFoodList().observe(getViewLifecycleOwner(), new Observer<List<LaundryServicesModel>>() {
            @Override
            public void onChanged(List<LaundryServicesModel> laundryServicesModels) {
                if(laundryServicesModels != null)
                {
                    adapter= new MyServicesListAdapter(getContext(),laundryServicesModels);
                    recycler_service_list.setAdapter(adapter);
                    recycler_service_list.setLayoutAnimation(layoutAnimationController);
                }

            }
        });
        return root;
    }

    private void initViews() {

        ((AppCompatActivity)getActivity())
                .getSupportActionBar()
                .setTitle(Common.categorySelected.getName());

        setHasOptionsMenu(true);

        recycler_service_list.setHasFixedSize(true);
        recycler_service_list.setLayoutManager(new LinearLayoutManager(getContext()));

        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(),R.anim.layout_item_from_left);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu,menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager)getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView)menuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        //event
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                startSearch(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        //clear text when click clear button in search view
        ImageView closeButton = (ImageView)searchView.findViewById(R.id.search_close_btn);
        closeButton.setOnClickListener(v -> {
            EditText ed = (EditText) searchView.findViewById(R.id.search_src_text);
            //clear text
            ed.setText("");
            //clear query
            searchView.setQuery("",false);
            //collapse the action view
            searchView.onActionViewCollapsed();
            //collapse the search widget
            menuItem.collapseActionView();
            //restore result to original
            servicesListViewModel.getMutableLiveDataFoodList();

        });
    }

    private void startSearch(String s) {
        List<LaundryServicesModel> resultList = new ArrayList<>();
        for(int i=0;i<Common.categorySelected.getServices().size();i++)
        {
            LaundryServicesModel laundryServicesModel = Common.categorySelected.getServices().get(i);
            if (laundryServicesModel.getName().toLowerCase().contains(s))
                resultList.add(laundryServicesModel);

        }
        servicesListViewModel.getMutableLiveDataFoodList().setValue(resultList);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().postSticky(new MenuItemBack());
        super.onDestroy();
    }

}