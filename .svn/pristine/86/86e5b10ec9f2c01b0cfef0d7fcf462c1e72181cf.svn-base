package com.orange.browser.reflect;

import android.content.ContentResolver;
import android.webkit.WebIconDatabase;

public class InvokeWebIconDatabaseMethod {
	public static void bulkRequestIconForPageUrl(WebIconDatabase widb, ContentResolver cr, String where,
            WebIconDatabase.IconListener listener){
		Invoker.invoke(widb, "bulkRequestIconForPageUrl", new Class[]{ContentResolver.class, String.class, 
				WebIconDatabase.IconListener.class}, new Object[]{cr, where, listener});
	}
}
