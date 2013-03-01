
package com.orange.browser.provider.blackwhitelist.operation;

import android.content.Context;

import com.orange.browser.provider.blackwhitelist.databaseObject.ListObject;
import com.orange.browser.provider.blackwhitelist.databaseObject.WhiteListInclusiveObject;
import com.orange.browser.provider.blackwhitelist.databaseObject.WhiteListObject;
import com.orange.browser.provider.blackwhitelist.json.JSonList;
import com.orange.browser.provider.blackwhitelist.json.JSonListObject;

class InclusionWhiteList extends AbsList<ListObject> {

    @Override
    public Class<? extends WhiteListObject> getListObjectClass() {
        return WhiteListInclusiveObject.class;
    }

    @Override
    public ListObject createListObject(Context context) {
        return new WhiteListInclusiveObject(context);
    }

    @Override
    public JSonListObject[] pickJSonObjects(JSonList jsonWl) {
        if (jsonWl == null) {
            return null;
        }

        return jsonWl.list;
    }

    @Override
    public String getVersionKey() {
        return WhiteBlackList.WHITELIST_VERSION;
    }
}
