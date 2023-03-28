package com.pigeonligh.facade.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.haohaohu.cachemanage.CacheUtil;
import com.pigeonligh.facade.R;
import com.pigeonligh.facade.common.Utils;
import com.pigeonligh.facade.data.types.ResponseView;
import com.pigeonligh.facade.databinding.ActivityViewBinding;

import java.util.Stack;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ViewActivity extends AppCompatActivity {
    public static final String NAME_ID = "name_id";
    public static final String URL_ID = "url_id";
    public static final String PATH_ID = "path_id";
    public static final String MODE_ID = "mode_id";
    private static final int FLING_MIN_DISTANCE = 200;
    private final Stack<ViewState> views = new Stack<>();
    private ViewMode initViewMode = ViewMode.auto;
    private Param param;
    private State state = State.loading;
    private ActivityViewBinding binding;
    private NavController navController;
    private boolean inited = false;

    private GestureDetector gestureListener;

    private Retrofit retrofit;
    private ResponseView.Service service;


    public static Intent getIndentForLaunch(Context context, Param param) {
        Intent intent = new Intent(context, ViewActivity.class);
        param.putIntent(intent);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityViewBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        navController = Navigation.findNavController(this, R.id.nav_view_fragment_activity_view);

        param = new Param(getIntent());
        retrofit = new Retrofit.Builder().baseUrl(param.url).addConverterFactory(GsonConverterFactory.create()).build();
        service = retrofit.create(ResponseView.Service.class);

        // merge param.mode
        initViewMode = initViewMode.merge(param.mode);

        Intent result = new Intent();
        param.putIntent(result);
        setResult(RESULT_OK, result);

        gestureListener = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
                if (e1 != null && e2 != null) {
                    float moveX = e2.getX() - e1.getX();
                    float moveY = e2.getY() - e1.getY();
                    if (moveX > FLING_MIN_DISTANCE && Math.abs(moveY) < Math.abs(moveX) / 2) {
                        Log.d("view", String.format("onFling: fling right - %f %f %f %f", e1.getX(), e2.getX(), e1.getY(), e2.getY()));
                        popView();
                        return true;
                    }
                }

                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });

    }

    public void fetchData(String path, ViewMode baseMode) {
        String cacheKey = param.url + path;

        String cached = CacheUtil.get(cacheKey);
        ViewCallback callback = new ViewCallback(baseMode, false, cacheKey);
        if (cached.isEmpty()) {
            Call<ResponseView> respCall = service.get(path);
            respCall.enqueue(callback);
        } else {
            callback.callback(Utils.gson().fromJson(cached, ResponseView.class));
        }
    }

    public void fresh(String path, ViewMode baseMode) {
        String cacheKey = param.url + path;
        ViewCallback callback = new ViewCallback(baseMode, true, cacheKey);
        Call<ResponseView> respCall = service.get(path);
        respCall.enqueue(callback);
    }

    public void popView() {
        if (views.size() == 1) {
            finish();
            return;
        }
        views.pop();
        state = State.loaded;

        updateView();
    }

    private void updateView() {
        switch (state) {
            case loading:
                navController.navigate(R.id.view_navigation_loading);
                Log.d("view", "updateNav: loading");
                break;
            case failed:
                navController.navigate(R.id.view_navigation_failed);
                Log.d("view", "updateNav: failed");
                break;
            case loaded:
                switch (getCurrentView().mode) {
                    case index:
                        navController.navigate(R.id.view_navigation_loaded_index);
                        Log.d("view", "updateNav: index");
                        break;
                    case content:
                        // TODO: change to view_navigation_loaded_content
                        navController.navigate(R.id.view_navigation_loaded_index);
                        Log.d("view", "updateNav: content");
                        break;
                    case auto:
                        navController.navigate(R.id.view_navigation_loading);
                        Log.d("view", "updateNav: loading");
                        break;
                }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return gestureListener.onTouchEvent(ev) || super.dispatchTouchEvent(ev);
    }

    public ViewState getCurrentView() {
        if (views.empty()) {
            return null;
        }
        return views.peek();
    }

    @Override
    protected void onResume() {
        if (!inited) {
            inited = true;
            fetchData(param.path, initViewMode);
        }

        super.onResume();
    }

    public enum State {
        loading,
        loaded,
        failed
    }

    public enum ViewMode {
        auto,
        index,
        content;

        public ViewMode merge(ViewMode target) {
            if (this == auto) {
                return target;
            }
            return this;
        }
    }

    public static class Param {
        private final String name;
        private final String url;
        private final String path;
        private final ViewMode mode;

        public Param(String name, String url, String path, ViewMode mode) {
            this.name = name;
            this.url = url;
            this.path = path;
            this.mode = mode;
        }

        public Param(String name, String url, String path) {
            this.name = name;
            this.url = url;
            this.path = path;
            this.mode = ViewMode.auto;
        }

        private Param(Intent intent) {
            name = intent.getStringExtra(NAME_ID);
            url = intent.getStringExtra(URL_ID);
            path = intent.getStringExtra(PATH_ID);
            mode = ViewMode.valueOf(intent.getStringExtra(MODE_ID));
            Log.d("view", String.format("Param: %s %s", name, url));
        }

        private void putIntent(Intent intent) {
            intent.putExtra(NAME_ID, name);
            intent.putExtra(URL_ID, url);
            intent.putExtra(PATH_ID, path);
            intent.putExtra(MODE_ID, mode.name());
        }
    }

    public class ViewState {
        private final ResponseView data;
        private final ViewMode mode;

        ViewState(ResponseView data, ViewMode mode) {
            this.data = data;
            this.mode = mode;
        }

        public ResponseView getData() {
            return data;
        }

        public ViewMode getMode() {
            return mode;
        }
    }

    private class ViewCallback implements Callback<ResponseView> {
        private final ViewMode baseMode;
        private final boolean isRefresh;
        private final String cacheKey;

        ViewCallback(ViewMode baseMode, boolean isRefresh, String cacheKey) {
            this.baseMode = baseMode;
            this.isRefresh = isRefresh;
            this.cacheKey = cacheKey;
        }

        public void callback(ResponseView data) {
            ViewMode viewMode = baseMode;
            // merge mode from response
            try {
                ViewMode defaultMode = ViewMode.valueOf(data.defaultMode);
                viewMode = viewMode.merge(defaultMode);
            } catch (NullPointerException e) {
                // Do nothing
            }

            // merge mode according to children
            ViewMode suggest;
            if (data.children.size() == 0) {
                suggest = ViewMode.content;
            } else {
                suggest = ViewMode.index;
            }
            viewMode = viewMode.merge(suggest);
            
            if (isRefresh) {
                views.pop();
            }
            views.push(new ViewState(data, viewMode));

            state = State.loaded;
            updateView();
        }

        @Override
        public void onResponse(Call<ResponseView> call, Response<ResponseView> response) {
            if (response.code() == 200) {
                ResponseView data = response.body();

                callback(data);

                CacheUtil.put(cacheKey, Utils.gson().toJson(data));
            } else {
                Log.d("view", String.format("code: %d", response.code()));
                state = State.failed;
                updateView();
            }
        }

        @Override
        public void onFailure(Call<ResponseView> call, Throwable t) {
            Log.d("view", "failed");
            state = State.failed;
            updateView();
        }
    }
}