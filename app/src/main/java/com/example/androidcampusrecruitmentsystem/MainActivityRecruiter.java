package com.example.androidcampusrecruitmentsystem;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivityRecruiter extends AppCompatActivity {
    FrameLayout container;
    LinearLayout homelayout,maillayout, applicationslayout,settingslayout;
    private FirebaseUser currentUser;
    public static final int REQUEST_CODE = 2;

    private DocumentReference userRef;
    static final User recruiter = new User();
    private FirebaseFirestore firestore;
    Drawable bottom_selected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_recruiter);
        container = findViewById(R.id.container_recruiter);
        homelayout = findViewById(R.id.homelayout_recruiter);
        maillayout = findViewById(R.id.maillayout_recruiter);
        applicationslayout = findViewById(R.id.applicationslayout_recruiter);
        settingslayout = findViewById(R.id.settingslayout_recruiter);
        ScrollView scrollView = findViewById(R.id.scrollView);
        bottom_selected = ResourcesCompat.getDrawable(getResources(),R.drawable.bottom_selected,null);

        Home_recruiter home_recruiter = new Home_recruiter();
        MailFragment mailFragment = new MailFragment();
        RecentFragment recentFragment = new RecentFragment();
        Recruiter_Settings_fragment recruiterSettingsFragment = new Recruiter_Settings_fragment();
        setFragment(home_recruiter);

        firestore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        userRef = firestore.collection("Recruiters").document(userId);


        if(currentUser != null) {
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String name = documentSnapshot.getString("name");
                    recruiter.setName(name);
                    if (documentSnapshot.contains("profilePicture")) {
                        recruiter.setProfilePictureUri(Uri.parse(documentSnapshot.getString("profilePicture")));
                    }
                }
            });

        }
        homelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(home_recruiter);
                homelayout.setBackground(bottom_selected);
                maillayout.setBackground(null);
                applicationslayout.setBackground(null);
                settingslayout.setBackground(null);

            }
        });
        maillayout.setOnClickListener(v -> {
            setFragment(mailFragment);
            maillayout.setBackground(bottom_selected);
            homelayout.setBackground(null);
            applicationslayout.setBackground(null);
            settingslayout.setBackground(null);

        });
        applicationslayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(recentFragment);
                applicationslayout.setBackground(bottom_selected);
                homelayout.setBackground(null);
                maillayout.setBackground(null);
                settingslayout.setBackground(null);
            }
        });
        settingslayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(recruiterSettingsFragment);
                settingslayout.setBackground(bottom_selected);
                applicationslayout.setBackground(null);
                homelayout.setBackground(null);
                maillayout.setBackground(null);
            }
        });
        scrollView.scrollTo(0,0);
    }
    public void setFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_recruiter,fragment)
                .commit();

    }


}