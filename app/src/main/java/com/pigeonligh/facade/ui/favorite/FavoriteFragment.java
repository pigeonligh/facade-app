package com.pigeonligh.facade.ui.favorite;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.pigeonligh.facade.Activities.ViewActivity;
import com.pigeonligh.facade.MainActivity;
import com.pigeonligh.facade.R;
import com.pigeonligh.facade.common.list.BaseList;
import com.pigeonligh.facade.data.types.DataFavoriteItem;
import com.pigeonligh.facade.databinding.FragmentFavoriteBinding;

public class FavoriteFragment extends Fragment {
    private final ActivityResultLauncher<Intent> launcher;
    private MainActivity parentActivity;
    private FragmentFavoriteBinding binding;
    private FavoriteViewModel favoriteViewModel;

    public FavoriteFragment() {
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Intent intent = result.getData();
                if (intent != null) {
                    String name = intent.getStringExtra(ViewActivity.NAME_ID);
                    int i = favoriteViewModel.getFavoriteList().getIdByName(name);
                    if (i != -1) {
                        favoriteViewModel.getFavoriteList().pick(i);
                        favoriteViewModel.saveData();
                    }
                } else {
                    Log.e("dashboard", "no result");
                }

                favoriteViewModel.getAdapter().notifyDataSetChanged();
            }
        });
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        parentActivity = (MainActivity) getActivity();
        favoriteViewModel = new FavoriteViewModel(parentActivity.getApplicationContext());

        binding = FragmentFavoriteBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        BaseList list = root.findViewById(R.id.list_favorite);
        list.setAdapter(favoriteViewModel.getAdapter());

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                DataFavoriteItem item = favoriteViewModel.getFavoriteList().get(i);
                ViewActivity.Param param = new ViewActivity.Param(item.getName(), item.getSource(), item.getPath());
                launcher.launch(ViewActivity.getIndentForLaunch(parentActivity.getApplicationContext(), param));
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("source.click", String.format("long click: %d", i));

                favoriteViewModel.getFavoriteList().remove(i);
                favoriteViewModel.saveData();
                favoriteViewModel.getAdapter().notifyDataSetChanged();
                return true;
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