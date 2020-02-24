package io.moonshard.moonshard;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.OpenableColumns;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtil {

    public static final String PREFIX = "stream2file";
    public static final String SUFFIX = ".tmp";

    public static File stream2file (InputStream in) throws IOException {
        final File tempFile = File.createTempFile(PREFIX, SUFFIX);
        tempFile.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            IOUtils.copy(in, out);
        }
        return tempFile;
    }


    public static File stream2file (Context context,Uri uri,InputStream in) throws IOException {

        String fileNameWithFormat = getFileName(context,uri);
        String fileName = fileNameWithFormat.split("\\.")[0]; // "\\. equals "." "
        String formatFile = "."+fileNameWithFormat.split("\\.")[1];



        final File tempFile = File.createTempFile(fileName, formatFile);
        tempFile.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            IOUtils.copy(in, out);
        }
        return tempFile;
    }

    public static File getTempFile(Context context, String url) {
        url = "https://upload.moonshard.tech/upload/mjEq_VXHJOv5Ongs/stream2file1320186155637012917.tmp";
        try {
            String fileName = Uri.parse(url).getLastPathSegment();
            File file = File.createTempFile(fileName, null, context.getCacheDir());
            return file;
        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String getFileName(Context context,Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}
