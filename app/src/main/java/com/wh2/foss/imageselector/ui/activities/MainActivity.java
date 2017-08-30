package com.wh2.foss.imageselector.ui.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;

import com.wh2.foss.imageselector.R;
import com.wh2.foss.imageselector.databinding.ActivityMainBinding;
import com.wh2.foss.imageselector.model.Company;
import com.wh2.foss.imageselector.model.Config;
import com.wh2.foss.imageselector.model.downloads.Progress;
import com.wh2.foss.imageselector.ui.adapters.ImagesAdapter;
import com.wh2.foss.imageselector.ui.viewmodels.ActivityViewModel;
import com.wh2.foss.imageselector.ui.viewmodels.ImageViewModel;
import com.wh2.foss.imageselector.utils.FilesHelper;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

public class MainActivity extends AppCompatActivity {

    ActivityViewModel activityViewModel;
    ActivityMainBinding binding;

    private String dirPath;
    private String fileName;

    ImagesAdapter adapter;

    CompositeDisposable subscriptions = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        activityViewModel = new ActivityViewModel(this);
        binding.setVm(activityViewModel);
        dirPath = new FilesHelper(this).getDirectoryPath();
        fileName = "foss_company.jpg";
    }

    @Override
    protected void onResume() {
        super.onResume();
        subscriptions.add(activityViewModel.getConfigurations().subscribe(
                config -> subscriptions.add(activityViewModel.getCompanies().subscribe(
                        companies -> setRecyclerView(config, companies),
                        this::onError)),
                this::onError)
        );
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

    private void companySelected(ImageViewModel imageViewModel) {
        subscriptions.add(imageViewModel.performDownload(dirPath, fileName).subscribe(
                this::setupProgress,
                this::onError,
                this::onDownloadComplete,
                disposable -> activityViewModel.progressShowing.set(true)
        ));
    }

    private void onError(Throwable throwable) {
        if (!TextUtils.isEmpty(throwable.getLocalizedMessage())){
            Snackbar.make(binding.getRoot(), throwable.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    private void setupProgress(Progress progress) {
        activityViewModel.setProgress(progress.getPercentDownloaded());
    }

    private void onDownloadComplete() {
        activityViewModel.progressShowing.set(false);
        Snackbar.make(binding.getRoot(), "Download Complete", Snackbar.LENGTH_LONG).show();
    }

    private void companyIgnored(ImageViewModel imageViewModel) {
        // TODO: 8/30/17 Enviar por servicio un PATCH con el campo "ignored:true"
    }
}

