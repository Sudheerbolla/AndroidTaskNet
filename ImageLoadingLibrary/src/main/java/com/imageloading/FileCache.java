package com.imageloading;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class FileCache {

    private final File cacheDir;

    public FileCache(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            cacheDir = new File(context.getExternalCacheDir(), "cacheImages");
        else
            cacheDir = context.getExternalCacheDir();
//            cacheDir = context.getCacheDir();
        if (!cacheDir.exists())
            cacheDir.mkdirs();
    }

    public File getFile(String url) {
        String filename = String.valueOf(url.hashCode());
        return new File(cacheDir, filename);

    }

    public void clear() {
        File[] files = cacheDir.listFiles();
        if (files == null)
            return;
        for (File f : files)
            f.delete();

        if (cacheDir.exists()) cacheDir.delete();
    }

}