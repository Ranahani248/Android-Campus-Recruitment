package com.example.androidcampusrecruitmentsystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchBar extends AppCompatActivity implements JobSearchAdapter.OnItemClickListener {
    static List<JobItem> jobList;
    static String searched;
    TextView noData;
    private FirebaseUser currentUser;
    private DocumentReference userRef;
    private FirebaseFirestore firestore;

    ImageView back, search_btn;
    Button titleSearch, companySearch, locationSearch, Selected;
    EditText search_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_bar);

        titleSearch = findViewById(R.id.TitlefilterBtn);
        companySearch = findViewById(R.id.CompanyfilterBtn);
        locationSearch = findViewById(R.id.LocationfilterBtn);
        Selected = titleSearch;
        search_bar = findViewById(R.id.search_bar);
        search_btn = findViewById(R.id.search_btn);
        noData = findViewById(R.id.nodata);
        back = findViewById(R.id.backbutton);
        firestore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        String userId = currentUser.getUid();
        userRef = firestore.collection("Students").document(userId);


        if(searched != null){
            search_bar.setText(searched);
        }
        search_btn.setOnClickListener(v -> {

            if(search_bar.getText().toString().length() > 0){
                searchJobdetails(search_bar.getText().toString());
            }
            else {
                search_bar.setError("Search field cannot be empty");
            }

        });
        search_bar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // Perform search when the "Enter" key is pressed
                if (search_bar.getText().toString().length() > 0) {
                    searchJobdetails(search_bar.getText().toString());
                }
                return true; // Consume the event
            }
            return false; // Let other listeners handle the event
        });
        back.setOnClickListener(view -> {
           Intent intent = new Intent(SearchBar.this, MainActivity.class);
           startActivity(intent);
        });
        companySearch.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.button_default_tint));
        locationSearch.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.button_default_tint));

        titleSearch.setOnClickListener(view -> {
            titleSearch.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.button_clicked_tint));
            companySearch.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.button_default_tint));
            locationSearch.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.button_default_tint));
            Selected = titleSearch;
            if(search_bar.getText().toString().length() > 0){
                searchJobdetails(search_bar.getText().toString());
            }
        });

        companySearch.setOnClickListener(view -> {
            titleSearch.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.button_default_tint));
            companySearch.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.button_clicked_tint));
            locationSearch.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.button_default_tint));
            Selected = companySearch;
            if(search_bar.getText().toString().length() > 0){
                searchJobdetails(search_bar.getText().toString());
            }
        });

        locationSearch.setOnClickListener(view -> {
            titleSearch.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.button_default_tint));
            companySearch.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.button_default_tint));
            locationSearch.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.button_clicked_tint));
            Selected = locationSearch;
            if(search_bar.getText().toString().length() > 0){
                searchJobdetails(search_bar.getText().toString());
            }
        });


        if (jobList != null) {
            if(jobList.size() > 0) {
                noData.setVisibility(View.GONE);

                fillData(jobList);
            }
            else {
                noData.setVisibility(View.VISIBLE);
            }
        }


    }
    public  void fillData(List<JobItem> jobList ) {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewSearch);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        JobSearchAdapter jobAdapter = new JobSearchAdapter(jobList, this);
        recyclerView.setAdapter(jobAdapter);
    }

    @Override
    public void onItemClick(JobItem jobItem) {
        Job_Details_student.jobid = jobItem.getJobid();
        Intent intent = new Intent(this, Job_Details_student.class);
        startActivity(intent);

    }
    private void searchJobdetails(String searchText) {
        firestore.collection("Jobs").limit(10).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<JobItem> jobList = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        if(Selected == titleSearch || Selected == null){

                        if (documentSnapshot.getString("jobTitle") != null && Objects.requireNonNull(documentSnapshot.getString("jobTitle")).toLowerCase().contains(searchText.toLowerCase())) {
                            String jobId = documentSnapshot.getId(); // Retrieve the job ID
                            String jobTitle = documentSnapshot.getString("jobTitle");
                            String companyName = documentSnapshot.getString("companyName");
                            String location = documentSnapshot.getString("location");
                            String salary = documentSnapshot.getString("Salary");
                            String description = documentSnapshot.getString("description");
                            String recruiterId = documentSnapshot.getString("recruiterId");

                            jobList.add(new JobItem(jobId, jobTitle, description, companyName,salary, location, recruiterId));
                        }
                    } else if (Selected == companySearch) {
                        if (documentSnapshot.getString("companyName") != null && Objects.requireNonNull(documentSnapshot.getString("companyName")).toLowerCase().contains(searchText.toLowerCase())) {
                            String jobId = documentSnapshot.getId(); // Retrieve the job ID
                            String jobTitle = documentSnapshot.getString("jobTitle");
                            String companyName = documentSnapshot.getString("companyName");
                            String location = documentSnapshot.getString("location");
                            String salary = documentSnapshot.getString("Salary");
                            String description = documentSnapshot.getString("description");
                            String recruiterId = documentSnapshot.getString("recruiterId");
                            jobList.add(new JobItem(jobId, jobTitle, description, companyName,salary, location,recruiterId));

                        }
                        } else if (Selected == locationSearch) {
                            if (documentSnapshot.getString("location") != null && Objects.requireNonNull(documentSnapshot.getString("location")).toLowerCase().contains(searchText.toLowerCase())) {
                                String jobId = documentSnapshot.getId(); // Retrieve the job ID
                                String jobTitle = documentSnapshot.getString("jobTitle");
                                String companyName = documentSnapshot.getString("companyName");
                                String location = documentSnapshot.getString("location");
                                String salary = documentSnapshot.getString("Salary");
                                String description = documentSnapshot.getString("description");
                                String recruiterId = documentSnapshot.getString("recruiterId");
                                jobList.add(new JobItem(jobId, jobTitle, description, companyName,salary, location,recruiterId));
                            }

                        }

                    }
                    if(jobList.isEmpty()){
                        noData.setVisibility(View.VISIBLE);
                        Toast.makeText(this, "No Jobs Found", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        noData.setVisibility(View.GONE);
                    }
                    fillData(jobList);
                })
                .addOnFailureListener(e -> {
                    Log.e("Error fetching job list", String.valueOf(e));
                });
    }
}