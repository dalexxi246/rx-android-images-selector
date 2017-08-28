package com.wh2.foss.imageselector.ui.activities;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.wh2.foss.imageselector.R;
import com.wh2.foss.imageselector.databinding.ActivityMainBinding;
import com.wh2.foss.imageselector.ui.adapters.ImagesAdapter;
import com.wh2.foss.imageselector.ui.viewmodels.ActivityViewModel;
import com.wh2.foss.imageselector.ui.viewmodels.ImageViewModel;

public class MainActivity extends AppCompatActivity implements ImagesAdapter.OnItemClickListener{

    ActivityViewModel viewModel;
    ActivityMainBinding binding;

    ImagesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        viewModel = new ActivityViewModel(this);
        binding.setVm(viewModel);

        setRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setRecyclerView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new ImagesAdapter(this);
        adapter.setListener(this);
        binding.recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(ImageViewModel item) {

    }

    @Override
    public void onSelect(ImageViewModel item) {

    }

    @Override
    public void onIgnore(ImageViewModel item) {

    }
}
