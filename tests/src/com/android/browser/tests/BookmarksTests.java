package com.android.browser.tests;

import com.android.browser.Bookmarks;
import com.android.browser.tests.utils.BP2TestCaseHelper;

import android.content.ContentResolver;
import android.database.Cursor;
import android.test.suitebuilder.annotation.SmallTest;

/**
 * Extends from BP2TestCaseHelper for the helper methods
 * and to get the mock database
 */
@SmallTest
public class BookmarksTests extends BP2TestCaseHelper {

    public void testQueryCombinedForUrl() {
        // First, add some bookmarks
        assertNotNull(insertBookmark(
                "http://google.com/search?q=test", "Test search"));
        assertNotNull(insertBookmark(
                "http://google.com/search?q=mustang", "Mustang search"));
        assertNotNull(insertBookmark(
                "http://google.com/search?q=aliens", "Aliens search"));
        ContentResolver cr = getMockContentResolver();

        Cursor c = null;
        try {
            // First, search for a match
            String url = "http://google.com/search?q=test";
            c = Bookmarks.queryCombinedForUrl(cr, null, url);
            assertEquals(1, c.getCount());
            assertTrue(c.moveToFirst());
            assertEquals(url, c.getString(0));
            c.close();

            // Next, search for no match
            url = "http://google.com/search";
            c = Bookmarks.queryCombinedForUrl(cr, null, url);
            assertEquals(0, c.getCount());
            assertFalse(c.moveToFirst());
            c.close();
        } finally {
            if (c != null) c.close();
        }
    }

}
