package com.wh2.foss.imageselector.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;

public class FilesHelper {

    private final String NO_MEDIA = ".nomedia";
    private final String IMAGES_FOLDER = "fingerprints";
    private final String IMAGES_EXTENSION = ".png";
    private Context context;

    public FilesHelper(Context context) {
        this.context = context;
    }

    public String getDirectoryPath(){
        if (!isExternalStorageWritable()) {
            return "";
        }
        return getAlbumStorageDir(IMAGES_FOLDER).getParent().concat("/");
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private File getAlbumStorageDir(String albumName) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("PIC_ERROR", "Directory not created");
        }
        return file;
    }

}
