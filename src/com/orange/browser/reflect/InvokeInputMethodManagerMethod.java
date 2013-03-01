
package com.orange.browser.reflect;

import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class InvokeInputMethodManagerMethod {
    public static void focusIn(InputMethodManager imm, View view) {
        Invoker.invoke(imm, "focusIn", new Class[] {
                View.class
        }, new Object[] {
                view
        });
    }
}
