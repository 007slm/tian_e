package com.android.browser.tests;

import com.android.browser.tests.utils.BP2TestCaseHelper;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.provider.BrowserContract.Bookmarks;
import android.provider.BrowserContract.History;
import android.test.suitebuilder.annotation.SmallTest;

import java.io.ByteArrayOutputStream;

@SmallTest
public class BP2UriObserverTests extends BP2TestCaseHelper {

    public void testInsertBookmark() {
        Uri insertedUri = insertBookmark("http://stub1.com", "Stub1");
        TriggeredObserver stubObs = new TriggeredObserver(insertedUri);
        assertObserversTriggered(false, stubObs);
        insertBookmark("http://stub2.com", "Stub2");
        perfIdeallyUntriggered(stubObs);
    }

    public void testUpdateBookmark() {
        Uri toUpdate = insertBookmark("http://stub1.com", "Stub1");
        Uri unchanged = insertBookmark("http://stub2.com", "Stub2");
        TriggeredObserver updateObs = new TriggeredObserver(toUpdate);
        TriggeredObserver unchangedObs = new TriggeredObserver(unchanged);
        assertObserversTriggered(false, updateObs, unchangedObs);
        assertTrue(updateBookmark(toUpdate, "http://stub1.com", "Stub1: Revenge of the stubs"));
        assertTrue("Update observer not notified!", updateObs.checkTriggered());
        perfIdeallyUntriggered(unchangedObs);
    }

    public void testUpdateBookmarkImages() {
        Uri toUpdate = insertBookmark("http://stub1.com", "Stub1");
        Uri unchanged = insertBookmark("http://stub2.com", "Stub2");
        Bitmap favicon = Bitmap.createBitmap(16, 16, Config.ARGB_8888);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        favicon.compress(Bitmap.CompressFormat.PNG, 100, os);
        byte[] rawFavicon = os.toByteArray();
        ContentValues values = new ContentValues();
        values.put(Bookmarks.FAVICON, rawFavicon);
        values.put(Bookmarks.TITLE, "Stub1");
        TriggeredObserver updateObs = new TriggeredObserver(toUpdate);
        TriggeredObserver unchangedObs = new TriggeredObserver(unchanged);
        assertTrue(updateBookmark(toUpdate, values));
        assertTrue("Update observer not notified!", updateObs.checkTriggered());
        perfIdeallyUntriggered(unchangedObs);
    }

    public void testInsertHistory() {
        Uri insertedUri = insertHistory("http://stub1.com", "Stub1");
        TriggeredObserver stubObs = new TriggeredObserver(insertedUri);
        assertObserversTriggered(false, stubObs);
        insertHistory("http://stub2.com", "Stub2");
        perfIdeallyUntriggered(stubObs);
    }

    public void testUpdateHistory() {
        Uri toUpdate = insertHistory("http://stub1.com", "Stub1");
        Uri unchanged = insertHistory("http://stub2.com", "Stub2");
        TriggeredObserver updateObs = new TriggeredObserver(toUpdate);
        TriggeredObserver unchangedObs = new TriggeredObserver(unchanged);
        assertObserversTriggered(false, updateObs, unchangedObs);
        assertTrue(updateHistory(toUpdate, "http://stub1.com", "Stub1: Revenge of the stubs"));
        assertTrue("Update observer not notified!", updateObs.checkTriggered());
        perfIdeallyUntriggered(unchangedObs);
    }

    public void testUpdateHistoryImages() {
        Uri toUpdate = insertHistory("http://stub1.com", "Stub1");
        Uri unchanged = insertHistory("http://stub2.com", "Stub2");
        Bitmap favicon = Bitmap.createBitmap(16, 16, Config.ARGB_8888);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        favicon.compress(Bitmap.CompressFormat.PNG, 100, os);
        byte[] rawFavicon = os.toByteArray();
        ContentValues values = new ContentValues();
        values.put(History.FAVICON, rawFavicon);
        values.put(History.TITLE, "Stub1");
        TriggeredObserver updateObs = new TriggeredObserver(toUpdate);
        TriggeredObserver unchangedObs = new TriggeredObserver(unchanged);
        assertTrue(updateHistory(toUpdate, values));
        assertTrue("Update observer not notified!", updateObs.checkTriggered());
        perfIdeallyUntriggered(unchangedObs);
    }
}
