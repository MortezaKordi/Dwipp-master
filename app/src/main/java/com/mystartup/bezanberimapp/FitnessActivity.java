package com.mystartup.bezanberimapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.florent37.viewanimator.ViewAnimator;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mystartup.bezanberimapp.mediaplayer.MediaPlayerMain;
import com.nightonke.boommenu.BoomMenuButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class FitnessActivity extends AppCompatActivity implements View.OnClickListener, HomeRecyclerViewAdapter.ItemClickListener, EasyPermissions.PermissionCallbacks, NavigationView.OnNavigationItemSelectedListener {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mToggle;

    private BoomMenuButton bmb;
    private FloatingActionButton homeFTB;
    private View goView;
    private ResponsiveScrollView mScrollView;
    private Spinner mViewMore;
    private TextView temperatureText;
    private HomeRecyclerViewAdapter adapter;
    private WebView mWebView;
    private boolean firstTime = true;

    //double_click_prevent
    public static long mLastClickTime = 0;

    ImageView goDoctor;
    ImageView goCoach;
    ImageView goEat;
    ImageView goBuy;
    ImageView goWorkout;
    ImageView goCycle;
    ImageView goRun;
    ImageView goWalk;
    FloatingActionButton closeGoViewButton;


    public static ProgressDialog pd;
    private static final String PREFERENCES = "SHARED_PREF";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitness);

        initUIComponents();
        initOnClickListeners();
        sharedPreferences = getApplicationContext().getSharedPreferences(PREFERENCES, MODE_PRIVATE);

        mNavigationView.setItemIconTintList(null);

        int bg = Color.parseColor("#0099cc");
        int sbc = Color.parseColor("#0099cc");

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(bg);
        }

//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);

        Window window = this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(sbc);
        }

        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.Open, R.string.Close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        temperatureText.setText(38 + "\u00B0");

        setupRecyclerView();
        mNavigationView.bringToFront();

        setNavigationViewListner();
        setUpWebView();
        setUpScrollView();


    }

    private void setupRecyclerView() {

        ArrayList<HomeKashi> kashiList;

        Gson gson = new Gson();
        String response = sharedPreferences.getString("key", "");

        if (sharedPreferences.contains("key")) {
            kashiList = gson.fromJson(response, new TypeToken<List<HomeKashi>>() {
            }.getType());
        } else {


            kashiList = new ArrayList<>();
            kashiList.add(new HomeKashi(HomeKashi.images[0], HomeKashi.titles[0] + "", HomeKashi.subTitles[0] + "", "#993366"));
            kashiList.add(new HomeKashi(HomeKashi.images[1], HomeKashi.titles[1] + "", HomeKashi.subTitles[1] + "", "#339966"));
            kashiList.add(new HomeKashi(HomeKashi.images[2], HomeKashi.titles[2] + "", HomeKashi.subTitles[2] + "", "#336699"));
            kashiList.add(new HomeKashi(HomeKashi.images[3], HomeKashi.titles[3] + "", HomeKashi.subTitles[3] + "", "#cccc33"));
            kashiList.add(new HomeKashi(HomeKashi.images[4], HomeKashi.titles[4] + "", HomeKashi.subTitles[4] + "", "#99cc33"));
            kashiList.add(new HomeKashi(HomeKashi.images[5], HomeKashi.titles[5] + "", HomeKashi.subTitles[5] + "", "#009999"));
            kashiList.add(new HomeKashi(HomeKashi.images[6], HomeKashi.titles[6] + "", HomeKashi.subTitles[6] + "", "#666699"));
            kashiList.add(new HomeKashi(HomeKashi.images[7], HomeKashi.titles[7] + "", HomeKashi.subTitles[7] + "", "#660033"));
            kashiList.add(new HomeKashi(HomeKashi.images[8], HomeKashi.titles[8] + "", HomeKashi.subTitles[8] + "", "#cc3333"));
            kashiList.add(new HomeKashi(HomeKashi.images[9], HomeKashi.titles[9] + "", HomeKashi.subTitles[9] + "", "#ff9933"));
            kashiList.add(new HomeKashi(HomeKashi.images[10], HomeKashi.titles[10] + "", HomeKashi.subTitles[10] + "", "#ff6699"));
            kashiList.add(new HomeKashi(HomeKashi.images[11], HomeKashi.titles[11] + "", HomeKashi.subTitles[11] + "", "#336633"));

        }
        RecyclerView recyclerView = findViewById(R.id.homeRV);


        int mNoOfColumns = Utility.calculateNoOfColumns(this, 120);

        recyclerView.setLayoutManager(new GridLayoutManager(this, mNoOfColumns));

        adapter = new HomeRecyclerViewAdapter(this, kashiList);
        adapter.setClickListener(FitnessActivity.this);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.Callback _ithCallback = new ItemTouchHelper.Callback() {

            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                Collections.swap(kashiList, viewHolder.getAdapterPosition(), target.getAdapterPosition());

                adapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                //shared_pref
                Gson gson = new Gson();
                String json = gson.toJson(kashiList);
                editor = sharedPreferences.edit();
                editor.remove("key").commit();
                editor.putString("key", json);
                editor.commit();
                return true;
            }

            @Override
            public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
                if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {

                    viewHolder.itemView.animate().setDuration(100).scaleX(1.2f);
                    viewHolder.itemView.animate().setDuration(100).scaleY(1.2f);


                }

            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);

                viewHolder.itemView.animate().setDuration(100).scaleX(1);
                viewHolder.itemView.animate().setDuration(100).scaleY(1);

            }


            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                //TODO
            }

            //defines the enabled move directions in each state (idle, swiping, dragging).
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,
                        ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END);
            }
        };

        ItemTouchHelper ith = new ItemTouchHelper(_ithCallback);
        ith.attachToRecyclerView(recyclerView);

    }

    @Override
    public void onItemClick(View view, int position) {

        Intent intent = new Intent(this, HealthTabLayoutActivity.class);
        startActivity(intent);

    }

    private void initOnClickListeners() {

        homeFTB.setOnClickListener(this);
        mScrollView.setOnClickListener(this);

    }


    public void homeMenuSelected(View view) {

        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            mDrawerLayout.openDrawer(Gravity.LEFT);
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {



            case R.id.homeFTB:

                if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                    return;
                }

                mLastClickTime = SystemClock.elapsedRealtime();

                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                goView = inflater.inflate(R.layout.go_view, null);
                mDrawerLayout.addView(goView);

                goDoctor = goView.findViewById(R.id.goDoctor);
                goCoach = goView.findViewById(R.id.goCoach);
                goEat = goView.findViewById(R.id.goEat);
                goBuy = goView.findViewById(R.id.goBuy);
                goWorkout = goView.findViewById(R.id.goWorkout);
                goCycle = goView.findViewById(R.id.goCycle);
                goRun = goView.findViewById(R.id.goRun);
                goWalk = goView.findViewById(R.id.goWalk);


                closeGoViewButton = goView.findViewById(R.id.closeGoViewButton);


                ViewAnimator
                        .animate(goView)
                        .fadeIn()
                        .duration(350)
                        .start();
                ViewAnimator
                        .animate(closeGoViewButton)
                        .rollIn()
                        .duration(200)
                        .start();

                ViewAnimator
                        .animate(goDoctor, goCoach, goEat, goBuy)
                        .zoomIn()
                        .duration(350)
                        .start();
                ViewAnimator
                        .animate(goWorkout, goCycle, goRun, goWalk)
                        .fadeIn()
                        .slideLeftIn()
                        .slideBottomIn()
                        .duration(350)
                        .start();


                closeGoViewButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        ViewAnimator
                                .animate(goDoctor, goCoach, goEat, goBuy)
                                .zoomOut()
                                .duration(350)
                                .start();


                        YoYo.with(Techniques.RotateOut)
                                .duration(350)
                                .playOn(closeGoViewButton);

                        ViewAnimator.animate(goView)
                                .fadeOut()
                                .duration(350)
                                .onStart(() -> {


                                    YoYo.with(Techniques.SlideOutDown)
                                            .duration(350)
                                            .playOn(goWorkout);
                                    YoYo.with(Techniques.SlideOutLeft)
                                            .duration(350)
                                            .playOn(goWorkout);
                                    YoYo.with(Techniques.SlideOutLeft)
                                            .duration(350)
                                            .playOn(goCycle);
                                    YoYo.with(Techniques.SlideOutDown)
                                            .duration(350)
                                            .playOn(goCycle);
                                    YoYo.with(Techniques.SlideOutLeft)
                                            .duration(350)
                                            .playOn(goRun);
                                    YoYo.with(Techniques.SlideOutDown)
                                            .duration(350)
                                            .playOn(goRun);
                                    YoYo.with(Techniques.SlideOutLeft)
                                            .duration(350)
                                            .playOn(goWalk);
                                    YoYo.with(Techniques.SlideOutDown)
                                            .duration(350)
                                            .playOn(goWalk);


                                }).onStop(() -> {

                            mDrawerLayout.removeView(goView);
                            goView = null;


                        }).start();


                    }
                });


                goView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        ViewAnimator
                                .animate(goDoctor, goCoach, goEat, goBuy)
                                .zoomOut()
                                .duration(350)
                                .start();

                        YoYo.with(Techniques.RotateOut)
                                .duration(350)
                                .playOn(closeGoViewButton);

                        ViewAnimator.animate(goView)
                                .fadeOut()
                                .duration(350)
                                .onStart(() -> {


                                    YoYo.with(Techniques.SlideOutDown)
                                            .duration(350)
                                            .playOn(goWorkout);
                                    YoYo.with(Techniques.SlideOutLeft)
                                            .duration(350)
                                            .playOn(goCycle);
                                    YoYo.with(Techniques.SlideOutDown)
                                            .duration(350)
                                            .playOn(goCycle);
                                    YoYo.with(Techniques.SlideOutLeft)
                                            .duration(350)
                                            .playOn(goRun);
                                    YoYo.with(Techniques.SlideOutDown)
                                            .duration(350)
                                            .playOn(goRun);
                                    YoYo.with(Techniques.SlideOutLeft)
                                            .duration(350)
                                            .playOn(goWalk);
                                    YoYo.with(Techniques.SlideOutDown)
                                            .duration(350)
                                            .playOn(goWalk);
                                    YoYo.with(Techniques.SlideOutLeft)
                                            .duration(350)
                                            .playOn(goWorkout);


                                }).onStop(() -> {

                            mDrawerLayout.removeView(goView);
                            goView = null;


                        }).start();


                    }
                });


                break;

            case R.id.viewMore:

                mScrollView.scrollTo(0, mScrollView.getBottom());

                break;
        }
    }

    private void showBoomMenu() {

    }

    // Helper methods

    private void initUIComponents() {

        mDrawerLayout = findViewById(R.id.drawer);
        mNavigationView = findViewById(R.id.navView);
        homeFTB = findViewById(R.id.homeFTB);
        mViewMore = findViewById(R.id.viewMore);
        temperatureText = findViewById(R.id.txtTemperatureValue);
        mWebView = findViewById(R.id.homeWebView);
        mScrollView = findViewById(R.id.homeScrollView);
    }


    @Override
    public void onBackPressed() {

        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
            return;
        }

        if (goView != null) {

            //mDrawerLayout.removeView(goView);
            ViewAnimator
                    .animate(goDoctor, goCoach, goEat, goBuy)
                    .zoomOut()
                    .duration(350)
                    .start();

            YoYo.with(Techniques.RotateOut)
                    .duration(350)
                    .playOn(closeGoViewButton);

            ViewAnimator.animate(goView)
                    .fadeOut()
                    .duration(350)
                    .onStart(() -> {


                        YoYo.with(Techniques.SlideOutDown)
                                .duration(350)
                                .playOn(goWorkout);
                        YoYo.with(Techniques.SlideOutLeft)
                                .duration(350)
                                .playOn(goCycle);
                        YoYo.with(Techniques.SlideOutDown)
                                .duration(350)
                                .playOn(goCycle);
                        YoYo.with(Techniques.SlideOutLeft)
                                .duration(350)
                                .playOn(goRun);
                        YoYo.with(Techniques.SlideOutDown)
                                .duration(350)
                                .playOn(goRun);
                        YoYo.with(Techniques.SlideOutLeft)
                                .duration(350)
                                .playOn(goWalk);
                        YoYo.with(Techniques.SlideOutDown)
                                .duration(350)
                                .playOn(goWalk);
                        YoYo.with(Techniques.SlideOutLeft)
                                .duration(350)
                                .playOn(goWorkout);


                    }).onStop(() -> {

                mDrawerLayout.removeView(goView);
                goView = null;


            }).start();

            return;

        }

        super.onBackPressed();


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        switch (menuItem.getItemId()) {

            case R.id.drawerSettings: {
                //do somthing
                Intent intent = new Intent(FitnessActivity.this, SettingsActivity.class);
                startActivity(intent);

                break;

            }
            case R.id.dwippMedia:
                askForStoragePerm();
                break;
        }
        //close navigation drawer
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setNavigationViewListner() {

        mNavigationView.setNavigationItemSelectedListener(this);
    }

    // Set up WebView
    private void setUpWebView() {

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                mViewMore.setVisibility(View.VISIBLE);
                ViewAnimator
                        .animate(mViewMore)
                        .flash()
                        .repeatCount(2)
                        .duration(700)
                        .start();


            }
        });
        mWebView.loadUrl("https://news.fitshape.ir/%D9%81%DB%8C%D8%AA%D9%86%D8%B3-%D8%AA%D9%86%D8%A7%D8%B3%D8%A8-%D8%A7%D9%86%D8%AF%D8%A7%D9%85/");
    }

    // Setup scrollView
    private void setUpScrollView() {

        mScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                mViewMore.setVisibility(View.GONE);
                if (firstTime) {
                    firstTime = false;
                    return;
                } else {
                    homeFTB.hide();
                }
            }
        });

        mScrollView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {

                    mScrollView.startScrollerTask();
                }

                return false;
            }
        });

        mScrollView.setOnScrollStoppedListener(new ResponsiveScrollView.OnScrollStoppedListener() {

            public void onScrollStopped() {

                Log.i("MYAndroidApp", "stopped");
                homeFTB.show();
            }
        });
    }

    private void askForStoragePerm() {
        String[] permsTorage = {Manifest.permission.READ_EXTERNAL_STORAGE};

        if (EasyPermissions.hasPermissions(this, permsTorage)) {
            pd = new ProgressDialog(this, ProgressDialog.THEME_HOLO_DARK);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setTitle("Loading songs...");
            pd.show();
            startActivity(new Intent(getApplicationContext(), MediaPlayerMain.class));

        } else {
            EasyPermissions.requestPermissions(this, "Permission needed for accessing music files",
                    14, permsTorage);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            if (EasyPermissions.hasPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE})) {
                pd = new ProgressDialog(this);
                pd.setTitle("Loading songs...");
                pd.show();
                startActivity(new Intent(getApplicationContext(), MediaPlayerMain.class));
            } else {
                Toast.makeText(this, "Permissions are needed for using Dwipp media", Toast.LENGTH_SHORT).show();

            }
        }


    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode == 14) {
            pd = new ProgressDialog(this);
            pd.setTitle("Loading songs...");
            pd.show();
            startActivity(new Intent(getApplicationContext(), MediaPlayerMain.class));
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (requestCode == 14) {
            if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
                new AppSettingsDialog.Builder(this).build().show();
            } else {
                Toast.makeText(this, "Permissions are needed for using Dwipp media", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

