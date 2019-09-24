package com.mystartup.bezanberimapp;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity implements SettingsRVAdapter.ItemClickListener {

    SettingsRVAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        int bg = Color.parseColor("#0099cc");

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(bg);
        }

        Window window = this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            window.setStatusBarColor(bg);

        }

        ArrayList<String> settingItems = new ArrayList<>();

                settingItems.add(getString(R.string.daryaftKhabarname));
                settingItems.add(getString(R.string.emailoSefareshat));
                settingItems.add(getString(R.string.zaban));
                settingItems.add(getString(R.string.keshvar));
                settingItems.add(getString(R.string.eelanha));
                settingItems.add(getString(R.string.vahedhayeAndazehgiri));
                settingItems.add(getString(R.string.bazkhordSoti));
                settingItems.add(getString(R.string.bazkhordLarzeshi));
                settingItems.add(getString(R.string.servishayeMotasel));
                settingItems.add(getString(R.string.ramzGozari));
                settingItems.add(getString(R.string.tashkhisZamanKhab));
                settingItems.add(getString(R.string.shakhehayeFaaliat));
                settingItems.add(getString(R.string.mogheiateMakani));
                settingItems.add(getString(R.string.shomaresheMaakoos));
                settingItems.add(getString(R.string.roshanMandaneSafheNamayesh));
                settingItems.add(getString(R.string.tavaghofeKhodkar));
                settingItems.add(getString(R.string.tavaghoMoosighi));
                settingItems.add(getString(R.string.baresiNoskheJadid));



        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.settings_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SettingsRVAdapter(this, settingItems);
        adapter.setClickListener(this);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }


    }

