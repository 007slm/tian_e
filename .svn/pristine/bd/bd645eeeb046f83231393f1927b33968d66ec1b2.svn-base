
package com.orange.browser.provider.blackwhitelist.json;

import android.content.Context;

import com.dailystudio.development.Logger;
import com.google.gson.Gson;

public class JSonWhiteBlackListParser {

    public static JSonList parseFromString(Context context, String content) {
        if (context == null) {
            return null;
        }

        if (content == null) {
            return null;
        }

        Gson gson = new Gson();

        JSonList jsonList = null;
        try {
            jsonList = gson.fromJson(content, JSonList.class);
        } catch (Exception e) {
            Logger.warnning("parse white list failure: %s", e.toString());
            jsonList = null;
        }

        return jsonList;
    }

}
