package com.pigeonligh.facade.ui.view.index;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.pigeonligh.facade.Activities.ViewActivity;
import com.pigeonligh.facade.R;
import com.pigeonligh.facade.common.list.BaseList;
import com.pigeonligh.facade.components.view.ViewRefListItem;
import com.pigeonligh.facade.data.adapter.DataRefAdapter;
import com.pigeonligh.facade.data.types.DataRefItem;
import com.pigeonligh.facade.data.types.ResponseView;
import com.pigeonligh.facade.databinding.FragmentViewIndexBinding;

public class ViewIndexFragment extends Fragment {
    private static final String TAG = "ViewIndexFragment";

    private TextView showContentView;
    private TextView descriptionView;
    private BaseList list;
    private FragmentViewIndexBinding binding;
    private DataRefAdapter dataList;
    private NestedScrollView scrollView;

    private ViewActivity parentActivity;

    public ViewIndexFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentActivity = (ViewActivity) getActivity();

        binding = FragmentViewIndexBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        scrollView = root.findViewById(R.id.view_index_scroll_view);
        showContentView = root.findViewById(R.id.view_index_show_content);
        descriptionView = root.findViewById(R.id.view_index_description);
        list = root.findViewById(R.id.view_index_children);

        DataRefItem.ViewFactory refFactory = new DataRefItem.ViewFactory() {
            @Override
            public ViewRefListItem create(DataRefItem item) {
                return new ViewRefListItem(item, getContext());
            }

            @Override
            public Context getContext() {
                return parentActivity.getApplicationContext();
            }
        };
        dataList = new DataRefAdapter(refFactory);
        list.setAdapter(dataList);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                parentActivity.fetchData(dataList.get(i).getPath(), ViewActivity.ViewMode.auto);
            }
        });

        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int x, int y, int ox, int oy) {
                parentActivity.getCurrentView().setScalePos(y);
            }
        });

        return root;
    }


    @Override
    public void onResume() {
        ViewActivity.ViewState current = parentActivity.getCurrentView();
        ResponseView data = new ResponseView(current.getData());

        if (data.description.isEmpty()) {
            data.description = data.content.content;
            data.content.content = "";
        }

        parentActivity.setTitle(data.content.title);
        if (data.content.content.isEmpty()) {
            showContentView.setVisibility(View.GONE);
            showContentView.setCursorVisible(false);
        } else {
            showContentView.setVisibility(View.VISIBLE);
        }
        if (data.description.isEmpty()) {
            descriptionView.setVisibility(View.GONE);
        } else {
            descriptionView.setText(data.description);
            descriptionView.setVisibility(View.VISIBLE);
        }
        dataList.setData(data.children);

        // set list height
        int totalHeight = 0;
        for (int i = 0; i < dataList.getCount(); i++) {
            View item = dataList.getView(i, null, list);
            item.measure(0, 0);
            totalHeight += item.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = list.getLayoutParams();
        params.height = totalHeight + (list.getDividerHeight() * (dataList.getCount() - 1));
        list.setLayoutParams(params);

        Log.d(TAG, String.format("scale to %d", current.getScalePos()));
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, current.getScalePos());
            }
        });

        super.onResume();
    }
}