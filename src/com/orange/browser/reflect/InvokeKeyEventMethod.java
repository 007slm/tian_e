package com.orange.browser.reflect;

import android.view.KeyEvent;

public class InvokeKeyEventMethod {
	public static boolean isDown(KeyEvent event){
		return (Boolean) Invoker.invoke(event, "isDown", new Class[]{}, new Object[]{});
	}
}
