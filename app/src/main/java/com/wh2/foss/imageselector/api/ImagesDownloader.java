package com.wh2.foss.imageselector.api;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.wh2.foss.imageselector.model.downloads.DownloadPaths;
import com.wh2.foss.imageselector.model.downloads.Progress;

import io.reactivex.Observable;
import io.reactivex.subjects.ReplaySubject;

public class ImagesDownloader {

    private ReplaySubject<Progress> progressStream = ReplaySubject.create();

    public ImagesDownloader(DownloadPaths downloadPaths) {
        downloadAndSaveImage(downloadPaths);
    }

    private void downloadAndSaveImage(DownloadPaths downloadPaths) {
        AndroidNetworking.download(downloadPaths.getUrl(), downloadPaths.getDirPath(), downloadPaths.getFileName())
                .setPriority(Priority.MEDIUM)
                .build()
                .setDownloadProgressListener((bytesDownloaded, totalBytes) -> progressStream.onNext(new Progress(bytesDownloaded, totalBytes)))
                .startDownload(new DownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        progressStream.onComplete();
                    }

                    @Override
                    public void onError(ANError anError) {
                        progressStream.onError(anError);
                    }
                });
    }

    public Observable<Progress> getProgressStream() {
        return progressStream.hide();
    }

}
