package com.orange.browser.reflect;

import java.util.Set;

import android.os.Message;
import android.webkit.WebBackForwardListClient;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class InvokeWebviewMethod {
	public static void setNetworkType(WebView wv, String type, String subType){
		Invoker.invoke(wv, "setNetworkType", new Class[]{String.class, String.class}, new Object[]{type, subType});
	}
	public static void addPackageName(WebView wv, String name){
		Invoker.invoke(wv, "addPackageName", new Class[]{String.class} , new Object[]{name});
	}
	public static void removePackageName(WebView wv, String name){
		Invoker.invoke(wv, "removePackageName", new Class[]{String.class} , new Object[]{name});
	}
	public static int findIndex(WebView wv){
		return (Integer) Invoker.invoke(wv, "findIndex", new Class[]{} , new Object[]{});
	}
	public static int getContentWidth(WebView wv){
	    return (Integer) Invoker.invoke(wv, "getContentWidth", new Class[]{} , new Object[]{});
	}
	public static void onPause(WebView wv){
		Invoker.invoke(wv, "onPause", new Class[]{} , new Object[]{});		
	}
	public static void onResume(WebView wv){
		Invoker.invoke(wv, "onResume", new Class[]{} , new Object[]{});		
	}
	public static String getSelection(WebView wv){
		return (String) Invoker.invoke(wv, "getSelection", new Class[]{} , new Object[]{});
	}
	public static void selectAll(WebView wv){
		Invoker.invoke(wv, "selectAll", new Class[]{} , new Object[]{});
	}
	public static boolean copySelection(WebView wv){
		return (Boolean) Invoker.invoke(wv, "copySelection", new Class[]{} , new Object[]{});
	}
	public static WebViewClient getWebViewClient(WebView wv){
		return (WebViewClient) Invoker.invoke(wv, "getWebViewClient",  new Class[]{}, new Object[]{});
	}
	public static WebChromeClient getWebChromeClient(WebView wv){
		return (WebChromeClient) Invoker.invoke(wv, "getWebChromeClient",  new Class[]{}, new Object[]{});		
	}
	public static void notifyFindDialogDismissed(WebView wv){
		Invoker.invoke(wv, "notifyFindDialogDismissed", new Class[]{} , new Object[]{});
	}
	public static void notifySelectDialogDismissed(WebView wv){
		Invoker.invoke(wv, "notifySelectDialogDismissed", new Class[]{} , new Object[]{});
	}
	public static void sendMessageToWebcore(WebView wv, Message msg){
		Object webCoreObject = Invoker.getPrivateField(wv, "mWebViewCore");
		Invoker.invokePrivateMethod(webCoreObject, "sendMessage", new Class[]{Message.class}, new Object[]{msg});
	}
	public static void sendMessageToWebcore(WebView wv, int what, Object obj){
		Object webCoreObject = Invoker.getPrivateField(wv, "mWebViewCore");
		Invoker.invokePrivateMethod(webCoreObject, "sendMessage", new Class[]{int.class, Object.class}, new Object[]{what, obj});
	}
	public static void setWebBackForwardListClient(WebView wv, WebBackForwardListClient wc){
		Invoker.invoke(wv, "setWebBackForwardListClient", new Class[]{WebBackForwardListClient.class} , new Object[]{wc});
	}
	public static String getTouchIconUrl(WebView wv){
		return (String) Invoker.invoke(wv, "getTouchIconUrl", new Class[]{} , new Object[]{});
	}
	public static void setFindIsUp(WebView wv, boolean isUp){
//		Invoker.invokePrivateMethod(wv, "setFindIsUp", new Class[]{boolean.class}, new Object[]{isUp});
		Invoker.invoke(wv, "setFindIsUp", new Class[]{boolean.class}, new Object[]{isUp});
	}
	public static boolean showFindDialog(WebView wv, String text,boolean showIme){
        return (Boolean)Invoker.invoke(wv, "showFindDialog", new Class[]{String.class,boolean.class}, new Object[]{text,showIme});
    }
}
