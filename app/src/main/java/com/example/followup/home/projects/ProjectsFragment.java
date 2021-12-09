package com.example.followup.home.projects;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.followup.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ProjectsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_projects, container, false);
    }

    FloatingActionButton fab_addProject;
    RecyclerView recyclerView;
    ProgressBar loading;

    ArrayList<Project_item> projects_list;
    Projects_adapter projects_adapter;

    int currentPageNum = 1;
    int lastPageNum;
    boolean mHasReachedBottomOnce = false;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFields(view);
        fab_addProject.setOnClickListener(v -> startActivity(new Intent(getActivity(), AddProjectActivity.class)));
    }

    private void initFields(View view){
        fab_addProject = view.findViewById(R.id.fab_add_project);
        loading = view.findViewById(R.id.loading);
        recyclerView = view.findViewById(R.id.recycler_view);
        projects_list = new ArrayList<>();
    }
}