package com.pigeonligh.facade.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.haohaohu.cachemanage.CacheUtil;
import com.pigeonligh.facade.R;
import com.pigeonligh.facade.common.Utils;
import com.pigeonligh.facade.data.store.DataStore;
import com.pigeonligh.facade.data.types.DataFavoriteItem;
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
    private ActivityViewBinding binding;
    private NavController navController;
    private boolean inited = false;

    private GestureDetector gestureListener;
    private Retrofit retrofit;
    private ResponseView.Service service;

    private Boolean favoriteMenuVisible;
    private Boolean unfavoriteMenuVisible;


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.view_menu, menu);

        menu.findItem(R.id.view_menu_favorite).setVisible(favoriteMenuVisible);
        menu.findItem(R.id.view_menu_unfavorite).setVisible(unfavoriteMenuVisible);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        ViewState current;
        switch (item.getItemId()) {
            case R.id.view_menu_refresh:
                this.refresh();
                return true;
            case R.id.view_menu_favorite:
                current = getCurrentView();
                DataFavoriteItem favoriteItem = new DataFavoriteItem(current.data.content.title, param.url, current.path);
                if (DataStore.getFavoriteStore().getList().addItem(favoriteItem)) {
                    DataStore.getFavoriteStore().saveData();
                }
                updateMenu();
                return true;

            case R.id.view_menu_unfavorite:
                current = getCurrentView();
                int favoriteId = DataStore.getFavoriteStore().findFavorite(param.url, current.path);
                if (favoriteId != -1) {
                    DataStore.getFavoriteStore().getList().remove(favoriteId);
                }
                updateMenu();
                return true;

            case R.id.view_menu_settings:
                // TODO: settings
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void fetchData(String path, ViewMode baseMode) {
        String cacheKey = Utils.hashPath(param.url, path);
        views.push(new ViewState());
        updateView();

        String cached = CacheUtil.get(cacheKey);
        ViewCallback callback = new ViewCallback(path, baseMode, cacheKey);
        if (cached.isEmpty()) {
            Call<ResponseView> respCall = service.get(path);
            respCall.enqueue(callback);
        } else {
            callback.callback(Utils.gson().fromJson(cached, ResponseView.class));
        }
    }

    public void refresh() {
        String path = getCurrentView().getPath();
        ViewMode baseMode = getCurrentView().getMode();

        ViewCallback callback = new ViewCallback(path, baseMode, Utils.hashPath(param.url, path));
        Call<ResponseView> respCall = service.get(path);
        respCall.enqueue(callback);
    }

    public void popView() {
        if (views.size() != 0) {
            views.pop();
        }

        if (views.size() == 0) {
            finish();
        } else {
            updateView();
        }
    }

    private void updateMenu() {
        State currentState = getCurrentView().getState();
        String currentPath = getCurrentView().getPath();

        favoriteMenuVisible = false;
        unfavoriteMenuVisible = false;

        if (currentState == State.loaded) {
            int favoriteId = DataStore.getFavoriteStore().findFavorite(param.url, currentPath);
            if (favoriteId == -1) {
                favoriteMenuVisible = true;
            } else {
                unfavoriteMenuVisible = true;
            }
        }

        invalidateOptionsMenu();
    }

    private void updateView() {
        ViewState current = getCurrentView();
        if (current == null) {
            return;
        }

        switch (current.getState()) {
            case loading:
                navController.navigate(R.id.view_navigation_loading);
                Log.d("view", "updateNav: loading");
                break;
            case failed:
                navController.navigate(R.id.view_navigation_failed);
                Log.d("view", "updateNav: failed");
                break;
            case loaded:
                switch (current.getMode()) {
                    case index:
                        navController.navigate(R.id.view_navigation_loaded_index);
                        Log.d("view", "updateNav: index");
                        break;
                    case content:
                        navController.navigate(R.id.view_navigation_loaded_content);
                        Log.d("view", "updateNav: content");
                        break;
                    case auto:
                        navController.navigate(R.id.view_navigation_loading);
                        Log.d("view", "updateNav: loading");
                        break;
                }
        }
        updateMenu();
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

    public String getMediaURL(String path) {
        String url = param.url;
        if (url.charAt(url.length() - 1) != '/') {
            url = url + '/';
        }
        return url + "media?path=" + path;
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
        private ResponseView data = null;
        private ViewMode mode = ViewMode.auto;
        private State state = State.loading;
        private String path;
        private int scalePos = 0;

        ViewState() {
        }

        public int getScalePos() {
            return scalePos;
        }

        public void setScalePos(int scalePos) {
            this.scalePos = scalePos;
        }

        public void set(String path, ResponseView data, ViewMode mode, State state) {
            this.path = path;
            this.data = data;
            this.mode = mode;
            this.state = state;
        }

        public ResponseView getData() {
            return data;
        }


        public ViewMode getMode() {
            return mode;
        }

        public String getPath() {
            return path;
        }

        public State getState() {
            return state;
        }

    }

    private class ViewCallback implements Callback<ResponseView> {
        private final String path;
        private final ViewMode baseMode;
        private final String cacheKey;

        ViewCallback(String path, ViewMode baseMode, String cacheKey) {
            this.path = path;
            this.baseMode = baseMode;
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

            getCurrentView().set(path, data, viewMode, State.loaded);
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

                getCurrentView().set(path, null, ViewMode.auto, State.failed);
                updateView();
            }
        }

        @Override
        public void onFailure(Call<ResponseView> call, Throwable t) {
            Log.d("view", "failed");

            getCurrentView().set(path, null, ViewMode.auto, State.failed);
            updateView();
        }
    }
}