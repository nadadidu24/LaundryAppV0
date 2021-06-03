package my.laundryapp.app.uiProvider.categoryProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import my.laundryapp.app.R;

public class CategoryProviderFragment extends Fragment {

    private CategoryProviderViewModel categoryProviderViewModel;

    private Unbinder unbinder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        categoryProviderViewModel =
                new ViewModelProvider(this).get(CategoryProviderViewModel.class);
        View root = inflater.inflate(R.layout.fragment_category_provider, container, false);
        unbinder = ButterKnife.bind(this,root);

        return root;
    }
}