package dep.com.android.providers.downloads;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Config;
import android.util.Log;
import android.widget.Toast;

import com.orange.browser.R;

import java.io.File;


/**
 * Receives system broadcasts (boot, network connectivity)
 */
public class DownloadReceiver extends BroadcastReceiver {
    
    
    private void openOrDeleteCurrentDownload(Context context, Cursor cursor) {
        int filenameColumnId = cursor.getColumnIndexOrThrow(
                Downloads.Impl._DATA);
        String filename = cursor.getString(filenameColumnId);
        if(filename == null) {
            return;
        }
        int mimeId = cursor.getColumnIndexOrThrow(
                Downloads.Impl.COLUMN_MIME_TYPE);
        String mimetype = cursor.getString(mimeId);
        int titleId = cursor.getColumnIndexOrThrow(
                Downloads.Impl.COLUMN_TITLE);
        String title = cursor.getString(titleId);
        if (title == null){
            title = context.getString(R.string.application_name);
        }
       
            Intent launchIntent = new Intent(Intent.ACTION_VIEW);
            Uri path = Uri.parse(filename);
            // If there is no scheme, then it must be a file
            if (path.getScheme() == null) {
                path = Uri.fromFile(new File(filename));
            }
            launchIntent.setDataAndType(path, mimetype);
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            try {
//                context.startActivity(launchIntent);
//            } catch (ActivityNotFoundException e) {
//                Toast.makeText(context, R.string.can_not_open, Toast.LENGTH_SHORT).show();
//            }
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            NotificationManager notMgr = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            if (notMgr != null) {
                Notification notification = new Notification();
                notification.icon = android.R.drawable.stat_sys_download_done;
                notification.flags = Notification.FLAG_AUTO_CANCEL;
                notification.setLatestEventInfo(context, title, context.getString(R.string.notification_download_complete), pendingIntent);
                notMgr.notify(filename.hashCode(), notification);
            }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //popup a toast if download completed
        if (intent.getAction().equals(Downloads.Impl.ACTION_DOWNLOAD_COMPLETED)) {
            // Cursor cursor=context.getContentResolver()
            Cursor cursor = context.getContentResolver().query(
                    intent.getData(), null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    Toast.makeText(context, R.string.notification_download_complete,
                            Toast.LENGTH_LONG)
                            .show();
                    openOrDeleteCurrentDownload(context, cursor);
                }
            }
        }
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            if (Constants.LOGVV) {
                Log.v(Constants.TAG, "Receiver onBoot");
            }
            
            if (Constants.START_WHEN_BOOT_COMPLETE) {
            	 context.startService(new Intent(context, DownloadService.class));				
			}
           
        } else if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            if (Constants.LOGVV) {
                Log.v(Constants.TAG, "Receiver onConnectivity");
            }
            NetworkInfo info = (NetworkInfo)
                    intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (info != null && info.isConnected()) {
                if (Constants.LOGX) {
                    if (Helpers.isNetworkAvailable(context)) {
                        Log.i(Constants.TAG, "Broadcast: Network Up");
                    } else {
                        Log.i(Constants.TAG, "Broadcast: Network Up, Actually Down");
                    }
                }
                if (Constants.START_WHEN_NETWORK_AVAILABLE) {
                	context.startService(new Intent(context, DownloadService.class));
				}
                
            } else {
                if (Constants.LOGX) {
                    if (Helpers.isNetworkAvailable(context)) {
                        Log.i(Constants.TAG, "Broadcast: Network Down, Actually Up");
                    } else {
                        Log.i(Constants.TAG, "Broadcast: Network Down");
                    }
                }
            }
        } else if (intent.getAction().equals(Constants.ACTION_RETRY)) {
            if (Constants.LOGVV) {
                Log.v(Constants.TAG, "Receiver retry");
            }
            context.startService(new Intent(context, DownloadService.class));
        } else if (intent.getAction().equals(Constants.ACTION_OPEN)
                || intent.getAction().equals(Constants.ACTION_LIST)) {
            if (Constants.LOGVV) {
                if (intent.getAction().equals(Constants.ACTION_OPEN)) {
                    Log.v(Constants.TAG, "Receiver open for " + intent.getData());
                } else {
                    Log.v(Constants.TAG, "Receiver list for " + intent.getData());
                }
            }
            Cursor cursor = context.getContentResolver().query(
                    intent.getData(), null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int statusColumn = cursor.getColumnIndexOrThrow(Downloads.Impl.COLUMN_STATUS);
                    int status = cursor.getInt(statusColumn);
                    int visibilityColumn =
                            cursor.getColumnIndexOrThrow(Downloads.Impl.COLUMN_VISIBILITY);
                    int visibility = cursor.getInt(visibilityColumn);
                    if (Downloads.Impl.isStatusCompleted(status)
                            && visibility == Downloads.Impl.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) {
                        ContentValues values = new ContentValues();
                        values.put(Downloads.Impl.COLUMN_VISIBILITY,
                                Downloads.Impl.VISIBILITY_VISIBLE);
                        context.getContentResolver().update(intent.getData(), values, null, null);
                    }

                    if (intent.getAction().equals(Constants.ACTION_OPEN)) {
                        int filenameColumn = cursor.getColumnIndexOrThrow(Downloads.Impl._DATA);
                        int mimetypeColumn =
                                cursor.getColumnIndexOrThrow(Downloads.Impl.COLUMN_MIME_TYPE);
                        String filename = cursor.getString(filenameColumn);
                        String mimetype = cursor.getString(mimetypeColumn);
                        Uri path = Uri.parse(filename);
                        // If there is no scheme, then it must be a file
                        if (path.getScheme() == null) {
                            path = Uri.fromFile(new File(filename));
                        }
                        Intent activityIntent = new Intent(Intent.ACTION_VIEW);
                        activityIntent.setDataAndType(path, mimetype);
                        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        try {
                            context.startActivity(activityIntent);
                        } catch (ActivityNotFoundException ex) {
                            if (Config.LOGD) {
                                Log.d(Constants.TAG, "no activity for " + mimetype, ex);
                            }
                            // nothing anyone can do about this, but we're in a clean state,
                            //     swallow the exception entirely
                        }
                    } else {
                        int packageColumn = cursor.getColumnIndexOrThrow(
                                Downloads.Impl.COLUMN_NOTIFICATION_PACKAGE);
                        int classColumn = cursor.getColumnIndexOrThrow(
                                Downloads.Impl.COLUMN_NOTIFICATION_CLASS);
                        String pckg = cursor.getString(packageColumn);
                        String clazz = cursor.getString(classColumn);
                        if (pckg != null && clazz != null) {
                            Intent appIntent =
                                    new Intent(Downloads.Impl.ACTION_NOTIFICATION_CLICKED);
                            appIntent.setClassName(pckg, clazz);
                            if (intent.getBooleanExtra("multiple", true)) {
                                appIntent.setData(Downloads.Impl.CONTENT_URI);
                            } else {
                                appIntent.setData(intent.getData());
                            }
                            context.sendBroadcast(appIntent);
                        }
                    }
                }
                cursor.close();
            }
            NotificationManager notMgr = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            if (notMgr != null) {
                notMgr.cancel((int) ContentUris.parseId(intent.getData()));
            }
        } else if (intent.getAction().equals(Constants.ACTION_HIDE)) {
            if (Constants.LOGVV) {
                Log.v(Constants.TAG, "Receiver hide for " + intent.getData());
            }
            Cursor cursor = context.getContentResolver().query(
                    intent.getData(), null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int statusColumn = cursor.getColumnIndexOrThrow(Downloads.Impl.COLUMN_STATUS);
                    int status = cursor.getInt(statusColumn);
                    int visibilityColumn =
                            cursor.getColumnIndexOrThrow(Downloads.Impl.COLUMN_VISIBILITY);
                    int visibility = cursor.getInt(visibilityColumn);
                    if (Downloads.Impl.isStatusCompleted(status)
                            && visibility == Downloads.Impl.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) {
                        ContentValues values = new ContentValues();
                        values.put(Downloads.Impl.COLUMN_VISIBILITY,
                                Downloads.Impl.VISIBILITY_VISIBLE);
                        context.getContentResolver().update(intent.getData(), values, null, null);
                    }
                }
                cursor.close();
            }
        }
    }
}
