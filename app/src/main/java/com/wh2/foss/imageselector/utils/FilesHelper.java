package com.wh2.foss.imageselector.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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
        return getAlbumStorageDir(IMAGES_FOLDER).getParent();
    }

    public String saveFingerprintImage(String newFileName, Bitmap path) throws IOException {
        if (!isExternalStorageWritable()) {
            return "";
        }
        File dirImages = getAlbumStorageDir(IMAGES_FOLDER);
        File noMedia = new File(dirImages, NO_MEDIA);
        File myPath = new File(dirImages, newFileName + IMAGES_EXTENSION);

        FileOutputStream fosNoMedia = new FileOutputStream(noMedia);
        fosNoMedia.flush();

        FileOutputStream fosImg = new FileOutputStream(myPath);
        path.compress(Bitmap.CompressFormat.PNG, 60, fosImg);
        fosImg.flush();

        return myPath.getAbsolutePath();
    }

    /* Checks if external storage is available for read and write */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("PIC_ERROR", "Directory not created");
        }
        return file;
    }

}
