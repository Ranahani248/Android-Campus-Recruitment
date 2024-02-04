package com.example.androidcampusrecruitmentsystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
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
    LinearLayout homelayout,maillayout,recentlayout,settingslayout;
    private FirebaseUser currentUser;
    private DocumentReference userRef;
    static final User student = new User();
    public static final int REQUEST_CODE = 1;

    private FirebaseFirestore firestore;
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

        firestore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        userRef = firestore.collection("Students").document(userId);



        if(currentUser != null) {
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String name = documentSnapshot.getString("name");
                    student.setName(name);
                    setImage();
                }
            });

        }
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
        maillayout.setOnClickListener(v -> {
            setFragment(mailFragment);
            maillayout.setBackground(bottom_selected);
            homelayout.setBackground(null);
            recentlayout.setBackground(null);
            settingslayout.setBackground(null);

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