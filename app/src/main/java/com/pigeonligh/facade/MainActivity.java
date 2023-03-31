package com.pigeonligh.facade;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.haohaohu.cachemanage.CacheUtil;
import com.haohaohu.cachemanage.CacheUtilConfig;
import com.pigeonligh.facade.data.store.DataStore;
import com.pigeonligh.facade.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "main";
    private static final int FLING_MIN_DISTANCE = 200;

    private ActivityMainBinding binding;

    private GestureDetector gestureListener;

    private void init() {
        DataStore.initContext(getApplicationContext());

        CacheUtilConfig cc = CacheUtilConfig.builder(getApplication())
                .allowMemoryCache(true)
                .allowEncrypt(false)
                .allowKeyEncrypt(true)
                .preventPowerDelete(true)
                .build();
        CacheUtil.init(cc);
        // CacheUtil.clearAll();

        DisplayMetrics dm = getApplication().getResources().getDisplayMetrics();
        Configuration config = getApplication().getResources().getConfiguration();
        config.uiMode &= ~Configuration.UI_MODE_NIGHT_MASK;
        config.uiMode |= Configuration.UI_MODE_NIGHT_YES;
        getApplication().getResources().updateConfiguration(config, dm);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_favorite, R.id.navigation_dashboard, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        gestureListener = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
                if (e1 != null && e2 != null) {
                    float moveX = e2.getX() - e1.getX();
                    float moveY = e2.getY() - e1.getY();
                    if (Math.abs(moveY) < Math.abs(moveX) / 2) {
                        if (moveX > FLING_MIN_DISTANCE) {
                            // go left
                            switch (navController.getCurrentDestination().getId()) {
                                case R.id.navigation_dashboard:
                                    navController.navigate(R.id.navigation_favorite);
                                    break;
                                case R.id.navigation_settings:
                                    navController.navigate(R.id.navigation_dashboard);
                                    break;
                            }
                            return true;
                        }
                        if (-moveX > FLING_MIN_DISTANCE) {
                            switch (navController.getCurrentDestination().getId()) {
                                case R.id.navigation_favorite:
                                    navController.navigate(R.id.navigation_dashboard);
                                    break;
                                case R.id.navigation_dashboard:
                                    navController.navigate(R.id.navigation_settings);
                                    break;
                            }
                            return true;
                        }
                    }
                }

                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return gestureListener.onTouchEvent(ev) || super.dispatchTouchEvent(ev);
    }
}