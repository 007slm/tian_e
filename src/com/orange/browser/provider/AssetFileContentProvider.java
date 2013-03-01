package com.orange.browser.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.IOException;

public class AssetFileContentProvider extends ContentProvider{

    public static final String URI_PREFIX = "content://com.orange.assetfile";

    public static String constructUri(String url) {
        Uri uri = Uri.parse(url);
        return uri.isAbsolute() ? url : URI_PREFIX + url;
    }

    @Override
    public AssetFileDescriptor openAssetFile(Uri uri, String mode)
            throws FileNotFoundException {
        // TODO Auto-generated method stub
        AssetManager am = getContext().getAssets();
        String path = uri.getPath().substring(1);
        try {
            AssetFileDescriptor afd = am.openFd(path);
            return afd;
        } catch (IOException e) {
        }
        return super.openAssetFile(uri, mode);
    }

    @Override
    public int delete(Uri arg0, String arg1, String[] arg2) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean onCreate() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

}
