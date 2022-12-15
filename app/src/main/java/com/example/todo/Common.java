package com.example.todo;

import android.content.Context;
import android.provider.BaseColumns;

import java.io.File;

public abstract class Common {
    private static class subfolders implements BaseColumns
    {
        private static final String printFolder = "PDFS";
        private static final String exportListFolder = "Export Lists";
    }
    public static String getAppPath(Context context) {
        // Fetch external storage's app path.
        File dir = new File(android.os.Environment.getExternalStorageDirectory()
                + File.separator
                + "Download"
                + File.separator
                + context.getResources().getString(R.string.app_name)
                + File.separator
        );

        if(!dir.exists())
            dir.mkdir();

        return dir.getPath() + File.separator; //Returns app path.
    }

    public static String getPrintPath(Context context) {
        // Fetch external storage's app path.
        File dir = new File(getAppPath(context)
                + subfolders.printFolder
                + File.separator
        );

        if(!dir.exists())
            dir.mkdir();

        return dir.getPath() + File.separator; //Returns app path.
    }

    public static String getExportPath(Context context) {
        // Fetch external storage's app path.
        File dir = new File(getAppPath(context)
                + subfolders.exportListFolder
                + File.separator
        );

        if(!dir.exists())
            dir.mkdir();

        return dir.getPath() + File.separator; //Returns app path.
    }
}
