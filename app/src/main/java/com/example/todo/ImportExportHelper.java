package com.example.todo;

import android.Manifest;
import android.app.Instrumentation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.loader.content.CursorLoader;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.stream.Collectors;

public abstract class ImportExportHelper {

    public static class fileData implements BaseColumns {
        public static final String type = ".txt";
    }

    public static void askForWritePerm(String fileName, Context context, ArrayList<TasksCategory> lists) {
        Dexter.withContext(context)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        String downloadPath = Common.getExportPath(context);
                        String path = downloadPath + fileName + fileData.type;
                        Export(path, context, lists);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                    }
                })
                .check();
    }

    private static void Export(String path, Context context, ArrayList<TasksCategory> lists) {
        if (new File(path).exists()) {
            new File(path).delete();
        }

        try {
            // Request write access to file.
            new File(path).createNewFile();
            OutputStream ops = new FileOutputStream(path);

            // Convert lists to JSON data and write to file.

            //Remove the default lists, at indexes 0,1,2,3.
            for (int i = lists.size() - 1; i >= 0; i--) {
                TasksCategory list = lists.get(i);
                if (list.getId() == -1) {
                    lists.remove(list);
                }
            }


            Gson gson = new Gson();
            String json = gson.toJson(lists);

            byte[] b = json.getBytes(StandardCharsets.UTF_8);
            ops.write(b, 0, b.length);
            ops.close();

            scanFile(context, new File(path), null);

            // Show notification
            NotificationAdapter na = new NotificationAdapter("1", context);
            String title = context.getResources().getString(R.string.app_name);
            String content = "Saved successfully at: " + path;
            Intent intent = new Intent(context, LoginScreenActivity.class);
            na.showNotification(title, content, intent);
            // Toast.makeText(context, "Saved successfully at: " + path, Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch(JsonParseException e)
        {
            // Conversion to type ArrayList<TasksCategory> failed.
            Toast.makeText(context, "Bad file.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private static void scanFile(Context context, File f, @Nullable String mimeType) {
        // Force phone to discover new file and index it.
        MediaScannerConnection
                .scanFile(context, new String[]{f.getAbsolutePath()},
                        new String[]{mimeType}, null);
    }

    public static String readHTMLFile(ActivityResult result, ContentResolver cr) throws IOException, NullPointerException {
        // Read an HTML file and return its contents

        Uri uri = result.getData().getData();
        InputStream is = cr.openInputStream(uri);
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String json = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            json += br.lines().collect(Collectors.joining()).toString();
        }
        return json;
    }
}
