package com.wh2.foss.imageselector.ui.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.wh2.foss.imageselector.R;
import com.wh2.foss.imageselector.databinding.ActivityMainBinding;
import com.wh2.foss.imageselector.model.Company;
import com.wh2.foss.imageselector.model.Config;
import com.wh2.foss.imageselector.ui.adapters.ImagesAdapter;
import com.wh2.foss.imageselector.ui.viewmodels.ActivityViewModel;
import com.wh2.foss.imageselector.ui.viewmodels.ImageViewModel;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

public class MainActivity extends AppCompatActivity {

    ActivityViewModel viewModel;
    ActivityMainBinding binding;

    ImagesAdapter adapter;

    CompositeDisposable subscriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        viewModel = new ActivityViewModel(this);
        binding.setVm(viewModel);
    }

    @Override
    protected void onResume() {
        super.onResume();
        subscriptions.add(
                viewModel.getConfigurations()
                .subscribe(config -> viewModel.getCompanies().subscribe(companies -> setRecyclerView(config, companies))));
    }

    @Override
    protected void onPause() {
        super.onPause();
        subscriptions.clear();
    }

    @Override
    protected void onStop() {
        super.onStop();
        subscriptions.dispose();
    }

    private void setRecyclerView(Config config, List<Company> companies) {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new ImagesAdapter(companies, config, this);
        subscriptions.add(adapter.companySelected().subscribe(this::companySelected));
        subscriptions.add(adapter.companyIgnored().subscribe(this::companyIgnored));
        binding.recyclerView.setAdapter(adapter);
    }

    private void companySelected(ImageViewModel viewModel) {

    }

    private void companyIgnored(ImageViewModel viewModel) {

    }
}

