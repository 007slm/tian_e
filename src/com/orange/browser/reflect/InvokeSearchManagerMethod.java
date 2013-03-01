package com.orange.browser.reflect;


import android.app.SearchManager;
import android.app.SearchableInfo;


public class InvokeSearchManagerMethod {
	
	public static Object getWebSearchActivity(SearchManager sm ){
		return Invoker.invoke(sm, "getWebSearchActivity", new Class[]{}, new Object[]{});
	}
	public static Object getSuggestions(SearchManager sm, SearchableInfo searchable, String query){
		return Invoker.invoke(sm, "getSuggestions", new Class[]{SearchableInfo.class, String.class}, new Object[]{searchable, query});
	}

}
