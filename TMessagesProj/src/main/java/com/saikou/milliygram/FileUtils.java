package com.saikou.milliygram;


import android.content.Context;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.concurrent.Callable;

public class FileUtils {
    // ——— Application context for static methods ———
    private static Context appContext;

    /** Call once from your Application (e.g. in onCreate) */
    public static void init(Context context) {
        appContext = context.getApplicationContext();
    }

    // ——— Generic tryWith helper ———
    public static <T> T tryWith(Callable<T> call) {
        try {
            return call.call();
        } catch (Throwable e) {
            // swallow or log
            e.printStackTrace();
            return null;
        }
    }

    // ——— Save an object to a private file ———
    public static void saveData(final String fileName,
                                final Object data,
                                Context context /* if null, uses the init()’d context */) {
        final Context ctx = (context != null ? context : appContext);
        tryWith(() -> {
            if (ctx != null) {
                try (FileOutputStream fos = ctx.openFileOutput(fileName, Context.MODE_PRIVATE);
                     ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                    oos.writeObject(data);
                }
            }
            return null;
        });
    }

    // ——— Read an object back (must cast on call site) ———
    @SuppressWarnings("unchecked")
    public static <T> T readData(final String fileName,
                                 Context context,
                                 boolean showToast) {
        final Context ctx = (context != null ? context : appContext);
        if (ctx == null) return null;

        try {
            String[] files = ctx.fileList();
            if (files != null && Arrays.asList(files).contains(fileName)) {
                try (FileInputStream fis = ctx.openFileInput(fileName);
                     ObjectInputStream ois = new ObjectInputStream(fis)) {
                    return (T) ois.readObject();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (showToast) {
                Toast.makeText(ctx,
                        "Error loading data " + fileName,
                        Toast.LENGTH_SHORT).show();
            }
        }
        return null;
    }
}