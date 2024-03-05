package com.example.androidcampusrecruitmentsystem;


import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
    LinearLayout homelayout,maillayout, applicationslayout,settingslayout,testlayout;
    private FirebaseUser currentUser;
    public static final int REQUEST_CODE = 2;

    private DocumentReference userRef;
    static final User recruiter = new User();
    static boolean application = false;
    private FirebaseFirestore firestore;
    Drawable bottom_selected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_recruiter);
        container = findViewById(R.id.container_recruiter);
        homelayout = findViewById(R.id.homelayout_recruiter);
        maillayout = findViewById(R.id.maillayout_recruiter);
        testlayout = findViewById(R.id.tests_Recruiter);
        applicationslayout = findViewById(R.id.applicationslayout_recruiter);
        settingslayout = findViewById(R.id.settingslayout_recruiter);
        ScrollView scrollView = findViewById(R.id.scrollView);
        Log.d("TAG", "onCreate: "+scrollView.getScrollY());
        bottom_selected = ResourcesCompat.getDrawable(getResources(),R.drawable.bottom_selected,null);
        Home_recruiter home_recruiter = new Home_recruiter();
        MailFragment mailFragment = new MailFragment();
        ApplicationsFragment applicationsFragment = new ApplicationsFragment();
        Recruiter_Settings_fragment recruiterSettingsFragment = new Recruiter_Settings_fragment();
        TestFragment_recruiter testFragment = new TestFragment_recruiter();

        setFragment(home_recruiter,homelayout);
        if(application){
            setFragment(applicationsFragment,applicationslayout);
            application = false;
        }
        scrollView.scrollTo(0,1);




        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
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
        homelayout.setOnClickListener(v -> {
                setFragment(home_recruiter,homelayout);
        });
        maillayout.setOnClickListener(v -> {
            setFragment(mailFragment,maillayout);
            maillayout.setBackground(bottom_selected);
        });
        applicationslayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(applicationsFragment,applicationslayout);
            }
        });
        settingslayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(recruiterSettingsFragment,settingslayout);
            }
        });
        testlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(testFragment,testlayout);
            }
        });


    }
    public void setFragment(Fragment fragment, LinearLayout layout){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_recruiter, fragment)
                .commit();
        maillayout.setBackground(null);
        applicationslayout.setBackground(null);
        settingslayout.setBackground(null);
        homelayout.setBackground(null);
        testlayout.setBackground(null);
        layout.setBackground(bottom_selected);

        ScrollView scrollView = findViewById(R.id.scrollView);
        scrollView.scrollTo(0,1);

    }


}