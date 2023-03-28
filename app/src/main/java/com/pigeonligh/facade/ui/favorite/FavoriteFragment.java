package com.pigeonligh.facade.ui.favorite;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.pigeonligh.facade.MainActivity;
import com.pigeonligh.facade.R;
import com.pigeonligh.facade.common.list.BaseList;
import com.pigeonligh.facade.databinding.FragmentFavoriteBinding;

public class FavoriteFragment extends Fragment {
    private MainActivity parentActivity;

    private FragmentFavoriteBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        parentActivity = (MainActivity) getActivity();
        FavoriteViewModel favoriteViewModel = new FavoriteViewModel(parentActivity.getApplicationContext());

        binding = FragmentFavoriteBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        BaseList list = root.findViewById(R.id.list_favorite);
        list.setAdapter(favoriteViewModel.getAdapter());

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                favoriteViewModel.getFavoriteList().pick(i);
                favoriteViewModel.saveData();
                favoriteViewModel.getAdapter().notifyDataSetChanged();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}