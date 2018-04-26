package com.pointters.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

/**
 * Created by mac on 1/11/18.
 */

public class CommonUtils {

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static String getDateDuration(long originTime) {
        long unit_sec = 1000;
        long unit_min = 60 * unit_sec;
        long unit_hour = 60 * unit_min;
        long unit_day = 24 * unit_hour;
        long unit_week = 7 * unit_day;
        long unit_month = 30 * unit_day;
        long unit_year = 365 * unit_day;

        long curTime = System.currentTimeMillis();
        long duration = curTime - originTime;

        long years = (long) Math.floor(duration/unit_year);
        long months = (long) Math.floor(duration/unit_month);
        long weeks = (long) Math.floor(duration/unit_week);
        long days = (long) Math.floor(duration/unit_day);
        long hours = (long) Math.floor(duration/unit_hour);
        long mins = (long) Math.floor(duration/unit_min);
        long secs = (long) Math.floor(duration/unit_sec);

        if (years > 0)       { return years + "Y"; }
        else if (months > 0) { return months + "M"; }
        else if (weeks > 0)  { return weeks + "W"; }
        else if (days > 0)   { return days + "d"; }
        else if (hours > 0)  { return hours + "hr"; }
        else if (mins > 0)   { return mins + "m"; }
        else                 { return secs + "s"; }
    }
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static String getVideoFilePathFromURI(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        if (Build.VERSION.SDK_INT >= 23) {
            // DocumentProvider
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }

                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[] {
                            split[1]
                    };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {

                // Return the remote address
                if (isGooglePhotosUri(uri))
                    return uri.getLastPathSegment();

                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }

        } else {
            Cursor cursor = null;
            try {
                String[] proj = { MediaStore.Video.Media.DATA };
                cursor = context.getContentResolver().query(uri,  proj, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        return null;
    }
}
