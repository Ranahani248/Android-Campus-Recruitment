package com.example.androidcampusrecruitmentsystem;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    FrameLayout container;
    LinearLayout homelayout,maillayout,recentlayout,settingslayout,testlayout;
    private FirebaseUser currentUser;
    private DocumentReference userRef;
    static final User student = new User();
    public static final int REQUEST_CODE = 1;
    static boolean backRecent = false;
    private FirebaseFirestore firestore;
    Drawable bottom_selected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        container = findViewById(R.id.container);
        homelayout = findViewById(R.id.homelayout);
        maillayout = findViewById(R.id.maillayout);
        recentlayout = findViewById(R.id.recentlayout);
        settingslayout = findViewById(R.id.settingslayout);
        testlayout = findViewById(R.id.tests);


        bottom_selected = ResourcesCompat.getDrawable(getResources(),R.drawable.bottom_selected,null);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
        Homefragment homefragment = new Homefragment();
        mailStudentFragment mailFragment = new mailStudentFragment();
        RecentFragment recentFragment = new RecentFragment();
        SettingsFragment settingsFragment = new SettingsFragment();
        TestFragment testFragment = new TestFragment();
        setFragment(homefragment,homelayout);
        if(backRecent) {
            setFragment(recentFragment, recentlayout);
            backRecent = false;
        }

        firestore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        userRef = firestore.collection("Students").document(userId);
        student.setProfilePictureUri(null);


        if(currentUser != null) {
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String name = documentSnapshot.getString("name");
                    student.setName(name);
                    setImage();
                }
            });

        }
        homelayout.setOnClickListener(v -> setFragment(homefragment,homelayout));
        maillayout.setOnClickListener(v -> {
            setFragment(mailFragment,maillayout);


        });
        testlayout.setOnClickListener(v -> {
            setFragment(testFragment,testlayout);
        });
        recentlayout.setOnClickListener(v -> setFragment(recentFragment,recentlayout));
        settingslayout.setOnClickListener(v -> setFragment(settingsFragment, settingslayout));
    }
    public void setFragment(Fragment fragment, LinearLayout layout){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container,fragment)
                .commit();
        testlayout.setBackground(null);
        settingslayout.setBackground(null);
        homelayout.setBackground(null);
        maillayout.setBackground(null);
        recentlayout.setBackground(null);
        layout.setBackground(bottom_selected);
    }
    private void setImage() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

            String uid = currentUser.getUid();

            firestore.collection("Students").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Check if the 'profilePicture' field exists
                            if (documentSnapshot.contains("profilePicture")) {
                                student.setProfilePictureUri(Uri.parse(documentSnapshot.getString("profilePicture")));
                            }

                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(MainActivity.this, "Error loading profile picture: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

    }

}