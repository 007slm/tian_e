
package com.orange.browser;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.widget.Toast;

import dep.com.android.providers.downloads.Downloads;

import java.io.File;

public class NewPackageDownloadedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Downloads.Impl.ACTION_DOWNLOAD_COMPLETED)) {
            Uri uri = intent.getData();
            if (uri != null) {
                Cursor cursor = context.getContentResolver().query(uri,
                        null, null, null, BaseColumns._ID);
                if (cursor == null) {
                    return;
                }
                if (cursor.moveToFirst()) {
                    String filename = cursor.getString(cursor
                            .getColumnIndexOrThrow(Downloads.Impl._DATA));
                    String mimetype = cursor.getString(cursor
                            .getColumnIndexOrThrow(Downloads.Impl.COLUMN_MIME_TYPE));
                    installPackage(context, filename, mimetype);
                }
                cursor.close();

            }
        }

    }
    
    public static void installPackage(Context context, String filename, String mimetype) {
        
        if (filename == null) {
            return;
        }
        if (mimetype == null) {
            mimetype = "application/vnd.android.package-archive";
        }
        Uri path = Uri.parse(filename);
        if (path.getScheme() == null) {
            path = Uri.fromFile(new File(filename));
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(path, mimetype);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(i);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, R.string.can_not_open, Toast.LENGTH_SHORT).show();
        }
    }

}
