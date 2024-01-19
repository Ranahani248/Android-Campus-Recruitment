package com.example.androidcampusrecruitmentsystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {
    FrameLayout container;
    LinearLayout homelayout,maillayout,recentlayout,settingslayout;
    Drawable bottom_selected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        container = findViewById(R.id.container);
        homelayout = findViewById(R.id.homelayout);
        maillayout = findViewById(R.id.maillayout);
        recentlayout = findViewById(R.id.recentlayout);
        settingslayout = findViewById(R.id.settingslayout);

        bottom_selected = ResourcesCompat.getDrawable(getResources(),R.drawable.bottom_selected,null);

        Homefragment homefragment = new Homefragment();
        MailFragment mailFragment = new MailFragment();
        RecentFragment recentFragment = new RecentFragment();
        SettingsFragment settingsFragment = new SettingsFragment();
        setFragment(homefragment);


        homelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(homefragment);
                homelayout.setBackground(bottom_selected);
                maillayout.setBackground(null);
                recentlayout.setBackground(null);
                settingslayout.setBackground(null);

            }
        });
        maillayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(mailFragment);
                maillayout.setBackground(bottom_selected);
                homelayout.setBackground(null);
                recentlayout.setBackground(null);
                settingslayout.setBackground(null);

            }
        });
        recentlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(recentFragment);
                recentlayout.setBackground(bottom_selected);
                homelayout.setBackground(null);
                maillayout.setBackground(null);
                settingslayout.setBackground(null);
            }
        });
        settingslayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(settingsFragment);
                settingslayout.setBackground(bottom_selected);
                recentlayout.setBackground(null);
                homelayout.setBackground(null);
                maillayout.setBackground(null);
            }
        });
    }
    public void setFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container,fragment)
                .commit();

    }
}