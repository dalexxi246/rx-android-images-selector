package com.wh2.foss.imageselector.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;

import com.wh2.foss.imageselector.R;
import com.wh2.foss.imageselector.api.ApiService;
import com.wh2.foss.imageselector.databinding.ActivityMainBinding;
import com.wh2.foss.imageselector.model.Config;
import com.wh2.foss.imageselector.model.Image;
import com.wh2.foss.imageselector.model.downloads.Progress;
import com.wh2.foss.imageselector.ui.adapters.ImagesAdapter;
import com.wh2.foss.imageselector.ui.viewmodels.ActivityViewModel;
import com.wh2.foss.imageselector.ui.viewmodels.ImageViewModel;
import com.wh2.foss.imageselector.utils.FilesHelper;

import java.util.ArrayList;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 100;

    ActivityViewModel activityViewModel;

    ActivityMainBinding activityMainBinding;

    ImagesAdapter adapter;
    CompositeDisposable subscriptions = new CompositeDisposable();

    private boolean firstCall;

    private String dirPath;
    private String fileName;
    private String endpointURL;

    private int writeStoragePermissionCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        activityViewModel = new ActivityViewModel(this);
        activityMainBinding.setVm(activityViewModel);

        dirPath = new FilesHelper(this).getDirectoryPath();
        fileName = "foss_image.jpg";

        endpointURL = ApiService.BASE_URL;

        // checkPermissions();
    }

    private void checkPermissions() {
        writeStoragePermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (!hasPermissionsGranted()) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDataAtFirstTime();
    }

    private void loadDataAtFirstTime() {
        if (!firstCall) {
            loadData();
        }
        firstCall = true;
    }

    private boolean hasPermissionsGranted() {
        return writeStoragePermissionCheck == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscriptions.dispose();
    }

    private void loadData() {
        Disposable loadDataSubscription = activityViewModel.getConfigurations(endpointURL).subscribe(
                config -> {
                    setRecyclerView(config);
                    subscriptions.add(activityViewModel.getPictures(endpointURL).subscribe(
                            this::updateRecyclerView,
                            this::onError)
                    );
                }, throwable -> getNewHost());
        if (!loadDataSubscription.isDisposed()) {
            subscriptions.delete(loadDataSubscription);
        }
        subscriptions.add(loadDataSubscription);
    }

    private void getNewHost() {
        startActivityForResult(HostActivity.newIntent(this), HostActivity.ACTION_RETRIEVE_HOST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == HostActivity.ACTION_RETRIEVE_HOST) {
            if (resultCode == RESULT_OK) {
                endpointURL = newURLFromExtras(data);
                if (!TextUtils.isEmpty(endpointURL)) {
                    loadData();
                } else {
                    finish();
                }
            } else {
                finish();
            }
        }
    }

    private String newURLFromExtras(Intent extras) {
        return extras != null && extras.getExtras() != null ? extras.getExtras().getString(HostActivity.RETURN_HOST) : "";
    }

    private void setRecyclerView(Config config) {
        activityMainBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new ImagesAdapter(new ArrayList<>(), config, this);
        subscriptions.add(adapter.imageSelected().subscribe(this::pictureSelected));
        subscriptions.add(adapter.imageIgnored().subscribe(this::pictureIgnored));
        activityMainBinding.recyclerView.setAdapter(adapter);
    }

    private void updateRecyclerView(Image image) {
        if (adapter != null) {
            adapter.addItem(image);
        }
    }

    private void pictureSelected(ImageViewModel imageViewModel) {
        Disposable downloadPicture = imageViewModel.performDownload(dirPath, fileName).subscribe(
                this::setupProgress,
                this::onError,
                this::onDownloadComplete,
                disposable -> showMessage(getString(R.string.message_download_started))
        );
        if (!downloadPicture.isDisposed()) {
            subscriptions.delete(downloadPicture);
        }
        subscriptions.add(downloadPicture);
    }

    private void onError(Throwable throwable) {
        if (!TextUtils.isEmpty(throwable.getLocalizedMessage())) {
            Snackbar.make(activityMainBinding.getRoot(), throwable.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    private void setupProgress(Progress progress) {
        activityViewModel.setProgress(progress.getPercentDownloaded());
    }

    private void onDownloadComplete() {
        activityViewModel.progressShowing.set(false);
        showMessage(getString(R.string.message_download_complete));
    }

    private void pictureIgnored(ImageViewModel imageViewModel) {
        subscriptions.add(imageViewModel.ignorePicture(endpointURL).subscribe(() -> {
            loadData();
            showMessage(getString(R.string.message_picture_ignored));
        }, this::onError));
    }

    private void showMessage(String message) {
        Snackbar.make(activityMainBinding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }
}

