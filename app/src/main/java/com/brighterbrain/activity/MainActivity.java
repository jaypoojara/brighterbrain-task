package com.brighterbrain.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.brighterbrain.R;
import com.brighterbrain.adapter.ItemListAdapter;
import com.brighterbrain.database.DBHandler;
import com.brighterbrain.model.Item;
import com.brighterbrain.util.widget.RecyclerViewEmptySupport;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    FloatingActionButton fab;
    RecyclerViewEmptySupport rvListOfItem;
    ItemListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        findViews();
        ArrayList<Item> items = new DBHandler(this).getAllItems();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ArrayList<Item> items = new DBHandler(this).getAllItems();
        if (adapter == null) {
            adapter = new ItemListAdapter(items, this);
            rvListOfItem.setAdapter(adapter);
        }
        else
            adapter.updateAdapter(items);

    }

    private void findViews() {
        rvListOfItem = findViewById(R.id.rvListOfItem);
        rvListOfItem.setLayoutManager(new LinearLayoutManager(this));
        rvListOfItem.setEmptyView(findViewById(R.id.tvItems));
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == fab) {
            startActivity(new Intent(this, CreateEditItemActivity.class));
        }
    }
}
