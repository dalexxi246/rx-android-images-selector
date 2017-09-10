package com.wh2.foss.imageselector.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;

import com.kingfisher.easy_sharedpreference_library.SharedPreferencesManager;
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
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 100;

    private static final String KEY_PERMISSION_WRTIE_STORAGE = "permission_write_storage";
    private static final String FILENAME = "com.wh2.fingerprint.TAKE.FILENAME";

    private boolean firstCall;
    private boolean requestingPermissions;

    private String dirPath;
    private String fileName;
    private String endpointURL;

    private ActivityViewModel viewModel;
    private ActivityMainBinding binding;
    private Bundle extras;
    private ImagesAdapter adapter;
    private SharedPreferencesManager prefsManager;

    private CompositeDisposable subscriptions = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = new ActivityViewModel(this);
        binding.setVm(viewModel);

        Intent intent = getIntent();
        extras = intent.getExtras();

        FilesHelper filesHelper = new FilesHelper(this);
        dirPath = filesHelper.getDirectoryPath();
        fileName = getImageFileName();

        endpointURL = ApiService.BASE_URL;

        prefsManager = SharedPreferencesManager.getInstance();

        requestingPermissions = checkingPermissions();
    }

    private int getIdForFilename() {
        return extras != null ? extras.getInt(FILENAME) : 0;
    }

    @NonNull
    private String getImageFileName() {
        return String.valueOf(getIdForFilename()).concat(".jpg");
    }

    private boolean checkingPermissions() {
        List<String> permissionsNeeded = new ArrayList<>();
        if (!hasGrantedWriteStoragePermission()) {
            permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        return requestPermissionsNeeded(permissionsNeeded);
    }

    private boolean requestPermissionsNeeded(List<String> permissionsNeeded) {
        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toArray
                    (new String[permissionsNeeded.size()]), MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
            return true;
        }
        return false;
    }

    private boolean hasGrantedWriteStoragePermission() {
        int writeStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        prefsManager.putValue(KEY_PERMISSION_WRTIE_STORAGE, writeStorage == PackageManager.PERMISSION_GRANTED);
        return prefsManager.getValue(KEY_PERMISSION_WRTIE_STORAGE, Boolean.class);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE:
                prefsManager.putValue(KEY_PERMISSION_WRTIE_STORAGE, grantResults[0] == PackageManager.PERMISSION_GRANTED);
                break;
        }
        requestingPermissions = false;
        loadData();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscriptions.dispose();
    }

    private void loadData() {
        if (!requestingPermissions) {
            Disposable loadDataSubscription = viewModel.getConfigurations(endpointURL).subscribe(
                    config -> {
                        setRecyclerView(config);
                        subscriptions.add(viewModel.getPictures(endpointURL).subscribe(
                                this::updateRecyclerView,
                                this::onError)
                        );
                    }, throwable -> getNewHost());
            if (!loadDataSubscription.isDisposed()) {
                subscriptions.delete(loadDataSubscription);
            }
            subscriptions.add(loadDataSubscription);
        }
    }

    private void getNewHost() {
        startActivityForResult(HostActivity.newIntent(this), HostActivity.ACTION_RETRIEVE_HOST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == HostActivity.ACTION_RETRIEVE_HOST) {
            manageHostRetrieved(resultCode, data);
        }
    }

    private void manageHostRetrieved(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            endpointURL = newURLFromExtras(data);
            if (!TextUtils.isEmpty(endpointURL)) {
                loadData();
            } else {
                returnCanceled();
            }
        } else {
            returnCanceled();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        returnCanceled();
    }

    private String newURLFromExtras(Intent intent) {
        return intent != null && intent.getExtras() != null ? intent.getExtras().getString(HostActivity.RETURN_HOST) : "";
    }

    private void setRecyclerView(Config config) {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new ImagesAdapter(new ArrayList<>(), config, this);
        subscriptions.add(adapter.imageSelected().subscribe(this::pictureSelected));
        subscriptions.add(adapter.imageIgnored().subscribe(this::onPictureIgnored));
        binding.recyclerView.setAdapter(adapter);
    }

    private void updateRecyclerView(Image image) {
        if (adapter != null) {
            adapter.addItem(image);
        }
    }

    private void pictureSelected(ImageViewModel imageViewModel) {
        if (prefsManager.getValue(KEY_PERMISSION_WRTIE_STORAGE, Boolean.class)) {
            Disposable downloadPicture = imageViewModel.performDownload(dirPath, fileName).subscribe(
                    this::setupProgress,
                    this::onError,
                    this::onDownloadComplete,
                    disposable -> onDownloadStarted()
            );
            if (!downloadPicture.isDisposed()) {
                subscriptions.delete(downloadPicture);
            }
            subscriptions.add(downloadPicture);
        } else {
            Snackbar.make(binding.getRoot(), getString(R.string.permission_write_storage_denied), Snackbar.LENGTH_LONG).show();
        }
    }

    private void onDownloadStarted() {
        showMessage(getString(R.string.message_download_started));
    }

    private void onError(Throwable throwable) {
        if (!TextUtils.isEmpty(throwable.getLocalizedMessage())) {
            Snackbar.make(binding.getRoot(), throwable.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    private void setupProgress(Progress progress) {
        viewModel.setProgress(progress.getPercentDownloaded());
    }

    private void onDownloadComplete() {
        viewModel.progressShowing.set(false);
        returnFilenamePath();
    }

    private void onPictureIgnored(ImageViewModel imageViewModel) {
        subscriptions.add(imageViewModel.ignorePicture(endpointURL).subscribe(() -> {
            loadData();
            showMessage(getString(R.string.message_picture_ignored));
        }, this::onError));
    }

    private void showMessage(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }

    private void returnFilenamePath() {
        Intent i = new Intent();
        i.putExtra(Intent.EXTRA_RETURN_RESULT, getSavedImageAbsolutePath());
        setResult(RESULT_OK, i);
        finish();
    }

    private void returnCanceled() {
        setResult(RESULT_CANCELED);
        finish();
    }

    private String getSavedImageAbsolutePath() {
        return dirPath.concat(getImageFileName());
    }

}

