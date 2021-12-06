package com.example.followup.home.projects;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.followup.R;
import com.example.followup.requests.printing.AddPrintingActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ProjectsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_projects, container, false);
    }

    FloatingActionButton fab_addProject;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fab_addProject = view.findViewById(R.id.fab_add_project);

        fab_addProject.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AddProjectActivity.class));
        });
    }
}