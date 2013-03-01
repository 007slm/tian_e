package com.orange.browser;

import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.Toast;

import dep.com.android.providers.downloads.Downloads;

import java.io.File;

/**
 * View showing the user's current downloads
 */
public class DownloadPage extends ExpandableListActivity {
    private ExpandableListView mListView;
    private Cursor mDownloadCursor;
    private DownloadAdapter mDownloadAdapter;
    private int mStatusColumnId;
    private int mIdColumnId;
    private int mTitleColumnId;
    private long mContextMenuPosition;
    // Used to update the ContextMenu if an item is being downloaded and the
    // user opens the ContextMenu.
    private ContentObserver mContentObserver;
    // Only meaningful while a ContentObserver is registered. The ContextMenu
    // will be reopened on this View.
    private View mSelectedView;

    private Context mContext;
    private CustomDialog mDialog;
    private static final int OPEN_CONTEXTMENU = 2;
    private final static String LOGTAG = DownloadPage.class.getSimpleName();

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        mContext = getApplicationContext();
        setContentView(R.layout.downloads_page);

        // setTitle(getText(R.string.download_title));

        mListView = (ExpandableListView) findViewById(android.R.id.list);
        FrameLayout.LayoutParams params= new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, Gravity.CENTER_HORIZONTAL);
        params.setMargins(getResources().getDimensionPixelSize(R.dimen.list_view_padding_left), 0, getResources().getDimensionPixelSize(R.dimen.reading_list_padding_hori), 0);
        mListView.setLayoutParams(params);
        mListView.setGroupIndicator(getResources().getDrawable(R.drawable.expander_group));
        mListView.setDivider(null);
        mListView.setChildDivider(null);
        mListView.setSelector(R.drawable.list_selector_transparent);
        mListView.setCacheColorHint(0);
        mListView.setEmptyView(findViewById(R.id.empty));
        mListView.setVerticalScrollBarEnabled(false);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.reading_item_slide_right);
        LayoutAnimationController controller = new LayoutAnimationController(animation);
        controller.setDelay(0.3f);
        mListView.setLayoutAnimation(controller);
        mDownloadCursor = managedQuery(Downloads.Impl.CONTENT_URI,
                new String[] {
                        Downloads.Impl._ID, Downloads.Impl.COLUMN_TITLE,
                        Downloads.Impl.COLUMN_STATUS, Downloads.Impl.COLUMN_TOTAL_BYTES,
                        Downloads.Impl.COLUMN_CURRENT_BYTES,
                        Downloads.Impl.COLUMN_DESCRIPTION,
                        Downloads.Impl.COLUMN_NOTIFICATION_PACKAGE,
                        Downloads.Impl.COLUMN_LAST_MODIFICATION,
                        Downloads.Impl.COLUMN_VISIBILITY,
                        Downloads.Impl._DATA,
                        Downloads.Impl.COLUMN_MIME_TYPE,
                        // Add by Percy,add control,20110409
                        Downloads.Impl.COLUMN_CONTROL
                },
                null, null, Downloads.Impl.COLUMN_LAST_MODIFICATION + " DESC");

        // only attach everything to the listbox if we can access
        // the download database. Otherwise, just show it empty
        if (mDownloadCursor != null) {
            mStatusColumnId =
                    mDownloadCursor.getColumnIndexOrThrow(Downloads.Impl.COLUMN_STATUS);
            mIdColumnId =
                    mDownloadCursor.getColumnIndexOrThrow(Downloads.Impl._ID);
            mTitleColumnId =
                    mDownloadCursor.getColumnIndexOrThrow(Downloads.Impl.COLUMN_TITLE);

            // Create a list "controller" for the data
            mDownloadAdapter = new DownloadAdapter(this,
                    mDownloadCursor, mDownloadCursor.getColumnIndexOrThrow(
                            Downloads.Impl.COLUMN_LAST_MODIFICATION));

            setListAdapter(mDownloadAdapter);
            mListView.setOnCreateContextMenuListener(this);

            Intent intent = getIntent();
            final int groupToShow = intent == null || intent.getData() == null
                    ? 0 : checkStatus(ContentUris.parseId(intent.getData()));
            if (mDownloadAdapter.getGroupCount() > groupToShow) {
                mListView.post(new Runnable() {
                    public void run() {
                        if (mDownloadAdapter.getGroupCount() > groupToShow) {
                            mListView.expandGroup(groupToShow);
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDownloadCursor != null) {
            String where = null;
            for (mDownloadCursor.moveToFirst(); !mDownloadCursor.isAfterLast(); mDownloadCursor
                    .moveToNext()) {
                if (!Downloads.Impl.isStatusCompleted(
                        mDownloadCursor.getInt(mStatusColumnId))) {
                    // Only want to check files that have completed.
                    continue;
                }
                int filenameColumnId = mDownloadCursor.getColumnIndexOrThrow(
                        Downloads.Impl._DATA);
                String filename = mDownloadCursor.getString(filenameColumnId);
                if (filename != null) {
                    File file = new File(filename);
                    if (!file.exists()) {
                        long id = mDownloadCursor.getLong(mIdColumnId);
                        if (where == null) {
                            where = Downloads.Impl._ID + " = '" + id + "'";
                        } else {
                            where += " OR " + Downloads.Impl._ID + " = '" + id
                                    + "'";
                        }
                    }
                }
            }
            if (where != null) {
                getContentResolver().delete(Downloads.Impl.CONTENT_URI, where,
                        null);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mDownloadCursor != null) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.downloadhistory, menu);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean showCancel = getCancelableCount() > 0;
        MenuItem mi = menu.findItem(R.id.download_menu_cancel_all);
        if (mi != null)
            mi.setEnabled(showCancel);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.download_menu_cancel_all:
                promptCancelAll();
                return true;
        }
        return false;
    }

    /**
     * Remove the file from the list of downloads.
     * 
     * @param id Unique ID of the download to remove.
     */
    private void clearFromDownloads(long id) {
        getContentResolver().delete(ContentUris.withAppendedId(
                Downloads.Impl.CONTENT_URI, id), null, null);
    }

    public void pauseDownload(long id) {
        // Utils.log(LOGTAG, "Pause download,id=" + id);
        pauseDownload(ContentUris.withAppendedId(Downloads.Impl.CONTENT_URI, id));
    }

    public void pauseDownload(Uri uri) {
        // Utils.log(LOGTAG, "Pause download,uri=" + uri);
        ContentValues values = new ContentValues();
        values
                .put(Downloads.Impl.COLUMN_CONTROL,
                        Downloads.Impl.CONTROL_PAUSED);
        mContext.getContentResolver().update(uri, values, null, null);
    }

    public void resumeDownload(long id) {
        // Utils.log(LOGTAG, "Resume download,id=" + id);
        resumeDownload(ContentUris.withAppendedId(Downloads.Impl.CONTENT_URI, id));
    }

    public void resumeDownload(Uri uri) {
        // Utils.log(LOGTAG, "Resume download,uri=" + uri);

        ContentValues values = new ContentValues();
        values.put(Downloads.Impl.COLUMN_CONTROL, Downloads.Impl.CONTROL_RUN);
        mContext.getContentResolver().update(uri, values, null, null);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (!mDownloadAdapter.moveCursorToPackedChildPosition(
                mContextMenuPosition)) {
            return false;
        }
        switch (item.getItemId()) {
            case R.id.download_menu_open:
                hideCompletedDownload();
                openOrDeleteCurrentDownload(false);
                return true;

            case R.id.download_menu_delete:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.download_delete_file)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage(mDownloadCursor.getString(mTitleColumnId))
                        .setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                            int whichButton) {
                                        openOrDeleteCurrentDownload(true);
                                    }
                                })
                        .show();
                break;

            case R.id.download_menu_clear:
            case R.id.download_menu_cancel:
                clearFromDownloads(mDownloadCursor.getLong(mIdColumnId));
                return true;

            case R.id.download_menu_pause: {
                pauseDownload(mDownloadCursor.getLong(mIdColumnId));
                break;
            }

            case R.id.download_menu_resume: {
                resumeDownload(mDownloadCursor.getLong(mIdColumnId));
                break;
            }
        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mContentObserver != null) {
            getContentResolver().unregisterContentObserver(mContentObserver);
            // Note that we do not need to undo this in onResume, because the
            // ContextMenu does not get reinvoked when the Activity resumes.
        }
    }

    /*
     * ContentObserver to update the ContextMenu if it is open when the
     * corresponding download completes.
     */
    private class ChangeObserver extends ContentObserver {
        private final Uri mTrack;

        public ChangeObserver(Uri track) {
            super(new Handler());
            mTrack = track;
        }

        @Override
        public boolean deliverSelfNotifications() {
            return false;
        }

        @Override
        public void onChange(boolean selfChange) {
            Cursor cursor = null;
            try {
                cursor = getContentResolver().query(mTrack,
                        new String[] {
                            Downloads.Impl.COLUMN_STATUS
                        }, null, null,
                        null);
                if (cursor.moveToFirst() && Downloads.Impl.isStatusSuccess(
                        cursor.getInt(0))) {
                    // Do this right away, so we get no more updates.
                    getContentResolver().unregisterContentObserver(
                            mContentObserver);
                    // Post a runnable in case this ContentObserver gets
                    // notified
                    // before the one that updates the ListView.
                    mListView.post(new Runnable() {
                        public void run() {
                            // Close the context menu, reopen with up to date
                            // data.
                            closeContextMenu();
                            openContextMenu(mSelectedView);
                        }
                    });
                }
            } catch (IllegalStateException e) {
                Log.e(LOGTAG, "onChange", e);
            } finally {
                if (cursor != null)
                    cursor.close();
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        if(mDialog!=null&&mDialog.isShowing()){
            mDialog.dismiss();
        }
        if (mDownloadCursor != null) {
            ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
            long packedPosition = info.packedPosition;
            // Only show a context menu for the child views
            if (!mDownloadAdapter.moveCursorToPackedChildPosition(
                    packedPosition)) {
                return;
            }
            mContextMenuPosition = packedPosition;
            final String title = mDownloadCursor.getString(mTitleColumnId);
            menu.setHeaderTitle(title);
            int status = mDownloadCursor.getInt(mStatusColumnId);
            final long id=mDownloadCursor.getLong(mIdColumnId);
            String[] menus;
            mDialog = new CustomDialog(this);
            if (Downloads.Impl.isStatusSuccess(status)) {
                menus = new String[] {
                        getString(R.string.download_menu_open),
                        getString(R.string.download_delete_file)
                };
                mDialog.setItems(menus, new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        mDialog.dismiss();
                        switch (arg2) {
                            case 0:
                                hideCompletedDownload();
                                openOrDeleteCurrentDownload(false);
                                break;
                            case 1:
                                new AlertDialog.Builder(DownloadPage.this)
                                        .setTitle(R.string.download_delete_file)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setMessage(title)
                                        .setNegativeButton(R.string.cancel, null)
                                        .setPositiveButton(R.string.ok,
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog,
                                                            int whichButton) {
                                                        openOrDeleteCurrentDownload(true);
                                                    }
                                                })
                                        .show();
                                break;

                        }
                    }
                });
            } else if (Downloads.Impl.isStatusError(status)) {
                menus = new String[] {
                        getString(R.string.download_menu_clear)
                };
                mDialog.setItems(menus, new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        mDialog.dismiss();
                        switch (arg2) {
                            case 0:
                                clearFromDownloads(mDownloadCursor.getLong(mIdColumnId));
                                break;

                        }
                    }
                });
            } else {
                // In this case, the download is in progress. Set a
                // ContentObserver so that we can know when it completes,
                // and if it does, we can then update the context menu
                Uri track = ContentUris.withAppendedId(
                        Downloads.Impl.CONTENT_URI,id);
                if (mContentObserver != null) {
                    getContentResolver().unregisterContentObserver(
                            mContentObserver);
                }
                mContentObserver = new ChangeObserver(track);
                mSelectedView = v;
                getContentResolver().registerContentObserver(track, false,
                        mContentObserver);
                int controlColumnId = mDownloadCursor
                        .getColumnIndexOrThrow(Downloads.Impl.COLUMN_CONTROL);
                int control = mDownloadCursor.getInt(controlColumnId);

                mDialog.setOnDismissListener(new OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (null != mContentObserver) {
                            getContentResolver().unregisterContentObserver(mContentObserver);
                        }
                    }
                });
                if (Downloads.Impl.CONTROL_PAUSED == control) {
                    menus = new String[] {
                            getString(R.string.download_menu_cancel),
                            getString(R.string.download_menu_resume)
                    };
                    mDialog.setItems(menus, new OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                            mDialog.dismiss();
                            switch (arg2) {
                                case 0:
                                    clearFromDownloads(id);
                                    break;
                                case 1:
                                    resumeDownload(id);
                                    break;

                            }
                        }
                    });
                } else {
                    menus = new String[] {
                            getString(R.string.download_menu_cancel),
                            getString(R.string.download_menu_pause)
                    };
                    mDialog.setItems(menus, new OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                            mDialog.dismiss();
                            switch (arg2) {
                                case 0:
                                    clearFromDownloads(id);
                                    break;
                                case 1:
                                    pauseDownload(id);
                                    break;

                            }
                        }
                    });
                }
            }
            mDialog.show();
            // Add by Percy,show or hide pause/resume item according to the
            // COLUMN_CONTROL,20110409
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    // Add by Percy,show or hide pause/resume item according to the
    // COLUMN_CONTROL,20110409
    /**
     * Switch pause mode or resume.
     */
    private void switchPauseOrHideMode(Cursor cursor, Menu menu) {
        // TODO Auto-generated method stub

        int controlColumnId = cursor.getColumnIndexOrThrow(Downloads.Impl.COLUMN_CONTROL);
        int control = cursor.getInt(controlColumnId);

        if (Downloads.Impl.CONTROL_PAUSED == control) {
            MenuItem resumeItem = menu.findItem(R.id.download_menu_resume);
            if (null != resumeItem) {
                resumeItem.setVisible(true);
            }

            MenuItem pauseItem = menu.findItem(R.id.download_menu_pause);
            if (null != pauseItem) {
                pauseItem.setVisible(false);
            }

        } else {
            MenuItem resumeItem = menu.findItem(R.id.download_menu_resume);
            if (null != resumeItem) {
                resumeItem.setVisible(false);
            }

            MenuItem pauseItem = menu.findItem(R.id.download_menu_pause);
            if (null != pauseItem) {
                pauseItem.setVisible(true);
            }
        }
    }

    /**
     * This function is called to check the status of the download and if it has
     * an error show an error dialog.
     * 
     * @param id Row id of the download to check
     * @return Group which contains the download
     */
    private int checkStatus(final long id) {
        int groupToShow = mDownloadAdapter.groupFromChildId(id);
        if (-1 == groupToShow)
            return 0;
        int status = mDownloadCursor.getInt(mStatusColumnId);
        if (!Downloads.Impl.isStatusError(status)) {
            return groupToShow;
        }
        if (status == Downloads.Impl.STATUS_FILE_ERROR) {
            String title = mDownloadCursor.getString(mTitleColumnId);
            if (title == null || title.length() == 0) {
                title = getString(R.string.download_unknown_filename);
            }
            String msg = getString(R.string.download_file_error_dlg_msg, title);
            new AlertDialog.Builder(this)
                    .setTitle(R.string.download_file_error_dlg_title)
                    .setIcon(android.R.drawable.ic_popup_disk_full)
                    .setMessage(msg)
                    .setPositiveButton(R.string.ok, null)
                    .setNegativeButton(R.string.retry,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int whichButton) {
                                    resumeDownload(id);
                                }
                            })
                    .show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.download_failed_generic_dlg_title)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(DownloadAdapter.getErrorText(status))
                    .setPositiveButton(R.string.ok, null)
                    .show();
        }
        return groupToShow;
    }

    /**
     * Resume a given download
     * 
     * @param id Row id of the download to resume
     */
    private void resumeDownload2(final long id) {
        // the relevant functionality doesn't exist in the download manager
    }

    /**
     * Return the number of items in the list that can be canceled.
     * 
     * @return count
     */
    private int getCancelableCount() {
        // Count the number of items that will be canceled.
        int count = 0;
        if (mDownloadCursor != null) {
            for (mDownloadCursor.moveToFirst(); !mDownloadCursor.isAfterLast(); mDownloadCursor
                    .moveToNext()) {
                int status = mDownloadCursor.getInt(mStatusColumnId);
                if (!Downloads.Impl.isStatusCompleted(status)) {
                    count++;
                }
            }
        }

        return count;
    }

    /**
     * Prompt the user if they would like to clear the download history
     */
    private void promptCancelAll() {
        int count = getCancelableCount();

        // If there is nothing to do, just return
        if (count == 0) {
            return;
        }

        // Don't show the dialog if there is only one download
        if (count == 1) {
            cancelAllDownloads();
            return;
        }
        String msg =
                getString(R.string.download_cancel_dlg_msg, count);
        new AlertDialog.Builder(this)
                .setTitle(R.string.download_cancel_dlg_title)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(msg)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                                cancelAllDownloads();
                            }
                        })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    /**
     * Cancel all downloads. As canceled downloads are not listed, we removed
     * them from the db. Removing a download record, cancels the download.
     */
    private void cancelAllDownloads() {
        if (mDownloadCursor.moveToFirst()) {
            StringBuilder where = new StringBuilder();
            boolean firstTime = true;
            while (!mDownloadCursor.isAfterLast()) {
                int status = mDownloadCursor.getInt(mStatusColumnId);
                if (!Downloads.Impl.isStatusCompleted(status)) {
                    if (firstTime) {
                        firstTime = false;
                    } else {
                        where.append(" OR ");
                    }
                    where.append("( ");
                    where.append(Downloads.Impl._ID);
                    where.append(" = '");
                    where.append(mDownloadCursor.getLong(mIdColumnId));
                    where.append("' )");
                }
                mDownloadCursor.moveToNext();
            }
            if (!firstTime) {
                getContentResolver().delete(Downloads.Impl.CONTENT_URI,
                        where.toString(), null);
            }
        }
    }

    private int getClearableCount() {
        int count = 0;
        if (mDownloadCursor.moveToFirst()) {
            while (!mDownloadCursor.isAfterLast()) {
                int status = mDownloadCursor.getInt(mStatusColumnId);
                if (Downloads.Impl.isStatusCompleted(status)) {
                    count++;
                }
                mDownloadCursor.moveToNext();
            }
        }
        return count;
    }

    /**
     * Open or delete content where the download db cursor currently is. Sends
     * an Intent to perform the action.
     * 
     * @param delete If true, delete the content. Otherwise open it.
     */
    private void openOrDeleteCurrentDownload(boolean delete) {
        if (mDownloadCursor.isClosed() || mDownloadCursor.getCount() == 0)
            return;
        int filenameColumnId = mDownloadCursor.getColumnIndexOrThrow(
                Downloads.Impl._DATA);
        String filename = mDownloadCursor.getString(filenameColumnId);
        int mimeId = mDownloadCursor.getColumnIndexOrThrow(
                Downloads.Impl.COLUMN_MIME_TYPE);
        String mimetype = mDownloadCursor.getString(mimeId);
        if (!delete) {
            Intent launchIntent = new Intent(Intent.ACTION_VIEW);
            Uri path = Uri.parse(filename);
            // If there is no scheme, then it must be a file
            if (path.getScheme() == null) {
                path = Uri.fromFile(new File(filename));
            }
            launchIntent.setDataAndType(path, mimetype);
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                mContext.startActivity(launchIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, R.string.can_not_open, Toast.LENGTH_SHORT).show();
            }
        } else {
            clearFromDownloads(mDownloadCursor.getLong(mIdColumnId));
            if (filename != null) {
                File file = new File(filename);
                if (file.exists()) {
                    file.delete();
                }
            }
        }
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v,
            int groupPosition, int childPosition, long id) {
        // Open the selected item
        mDownloadAdapter.moveCursorToChildPosition(groupPosition,
                childPosition);

        hideCompletedDownload();

        int status = mDownloadCursor.getInt(mStatusColumnId);
        if (Downloads.Impl.isStatusSuccess(status)) {
            // Open it if it downloaded successfully
            openOrDeleteCurrentDownload(false);
        } else {
            // Check to see if there is an error.
            checkStatus(id);
        }
        return true;
    }

    /**
     * hides the notification for the download pointed by mDownloadCursor if the
     * download has completed.
     */
    private void hideCompletedDownload() {
        int status = mDownloadCursor.getInt(mStatusColumnId);

        int visibilityColumn = mDownloadCursor.getColumnIndexOrThrow(
                Downloads.Impl.COLUMN_VISIBILITY);
        int visibility = mDownloadCursor.getInt(visibilityColumn);

        if (Downloads.Impl.isStatusCompleted(status) &&
                visibility == Downloads.Impl.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) {
            ContentValues values = new ContentValues();
            values.put(Downloads.Impl.COLUMN_VISIBILITY, Downloads.Impl.VISIBILITY_VISIBLE);
            getContentResolver().update(
                    ContentUris.withAppendedId(Downloads.Impl.CONTENT_URI,
                            mDownloadCursor.getLong(mIdColumnId)), values, null, null);
        }
    }
}
