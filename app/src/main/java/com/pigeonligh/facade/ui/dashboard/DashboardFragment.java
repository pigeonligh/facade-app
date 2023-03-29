package com.pigeonligh.facade.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pigeonligh.facade.Activities.ViewActivity;
import com.pigeonligh.facade.MainActivity;
import com.pigeonligh.facade.R;
import com.pigeonligh.facade.common.list.BaseList;
import com.pigeonligh.facade.data.types.DataSourceItem;
import com.pigeonligh.facade.databinding.FragmentDashboardBinding;
import com.pigeonligh.facade.ui.source.EditSourceFragment;

public class DashboardFragment extends Fragment {
    private final ActivityResultLauncher<Intent> launcher;
    private MainActivity parentActivity;
    private FragmentDashboardBinding binding;
    private DashboardViewModel dashboardViewModel;

    public DashboardFragment() {
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Intent intent = result.getData();
                if (intent != null) {
                    String name = intent.getStringExtra(ViewActivity.NAME_ID);
                    int i = dashboardViewModel.getSourceList().getIdByName(name);
                    if (i != -1) {
                        dashboardViewModel.getSourceList().pick(i);
                        dashboardViewModel.saveData();
                    }
                } else {
                    Log.e("dashboard", "no result");
                }

                dashboardViewModel.getAdapter().notifyDataSetChanged();
            }
        });
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        parentActivity = (MainActivity) getActivity();
        dashboardViewModel = new DashboardViewModel(parentActivity.getApplicationContext());

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        BaseList list = root.findViewById(R.id.list_dashboard);
        list.setAdapter(dashboardViewModel.getAdapter());

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("source.click", String.format("click: %d", i));

                DataSourceItem item = dashboardViewModel.getSourceList().get(i);
                ViewActivity.Param param = new ViewActivity.Param(item.getName(), item.getUrl(), "");
                launcher.launch(ViewActivity.getIndentForLaunch(parentActivity.getApplicationContext(), param));
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("source.click", String.format("long click: %d", i));

                openEditSourceDialog(i);
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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.dashboard_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.dashboard_menu_add:
                openAddSourceDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openAddSourceDialog() {
        EditSourceFragment fragment = new EditSourceFragment(new EditSourceFragment.Answerer() {
            @Override
            public boolean toConfirm(DataSourceItem item) {
                if (!DataSourceItem.Validate(item)) {
                    Toast.makeText(parentActivity.getApplicationContext(), "source not allowed", Toast.LENGTH_SHORT).show();
                    return false;
                }

                if (!dashboardViewModel.getSourceList().addItem(item)) {
                    Toast.makeText(parentActivity.getApplicationContext(), "name duplicated", Toast.LENGTH_SHORT).show();
                    return false;
                }

                Toast.makeText(parentActivity.getApplicationContext(), String.format("add source %s", item.getName()), Toast.LENGTH_SHORT).show();
                dashboardViewModel.saveData();
                return true;
            }

            @Override
            public boolean toDelete() {
                return false;
            }

            @Override
            public boolean toCancel() {
                return true;
            }
        });
        fragment.show(getChildFragmentManager(), "add_source");
    }

    private void openEditSourceDialog(int i) {
        DataSourceItem cur = dashboardViewModel.getSourceList().get(i);

        EditSourceFragment fragment = new EditSourceFragment(new EditSourceFragment.Answerer() {
            @Override
            public boolean toConfirm(DataSourceItem item) {
                if (!DataSourceItem.Validate(item)) {
                    Toast.makeText(parentActivity.getApplicationContext(), "source not allowed", Toast.LENGTH_SHORT).show();
                    return false;
                }

                if (!dashboardViewModel.getSourceList().editItem(i, item)) {
                    Toast.makeText(parentActivity.getApplicationContext(), "name duplicated", Toast.LENGTH_SHORT).show();
                    return false;
                }

                dashboardViewModel.getSourceList().pick(i);
                dashboardViewModel.saveData();
                dashboardViewModel.getAdapter().notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean toDelete() {
                dashboardViewModel.getSourceList().remove(i);
                dashboardViewModel.saveData();
                dashboardViewModel.getAdapter().notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean toCancel() {
                return true;
            }
        }, cur);
        fragment.show(getChildFragmentManager(), "edit_source");
    }
}