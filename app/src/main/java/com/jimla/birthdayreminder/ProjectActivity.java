package com.jimla.birthdayreminder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProjectActivity extends AppCompatActivity implements ProjectAdapter.OnItemClickListener {

    private ProjectDao projectDao;
    private RecyclerView recyclerView;

    private final HashMap<Integer, Integer> items = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        initDB();
        initRecyclerView();
        initUI();
    }

    private void initDB() {
        AppDatabase db = AppDatabase.getDatabaseInstance(getApplicationContext(), getString(R.string.db_name));

        projectDao = db.projectDao();
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                DividerItemDecoration.VERTICAL));
        ProjectAdapter.ViewHolder.setOnItemClickListener(this);
    }

    private void initUI() {
        Button buttonAdd = findViewById(R.id.button_add_new);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProjectActivity.this, ProjectDetails.class);

                startActivity(intent);
            }
        });
    }

    @Override
    public void onItemClick(int position) {

        Intent intent = new Intent(ProjectActivity.this, ProjectDetails.class);
        intent.putExtra("projectId", items.get(position));

        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("debug", "onResume/ProjectActivity");
        setAdapter();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //finish();
        Log.e("debug", "onStop/ProjectActivity");
    }

    private void setAdapter() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                List<ProjectEntry> entries = projectDao.getAll();
                ArrayList<String> names = new ArrayList<>();
                ArrayList<String> extra = new ArrayList<>();
                int listIndex = 0;
                for (ProjectEntry e : entries) {
                    items.put(listIndex++, e.id);
                    names.add(e.name);
                    extra.add("");
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setAdapter(new ProjectAdapter(names, extra));
                    }
                });
            }
        });
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}