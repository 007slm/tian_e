
package com.orange.browser.provider.blackwhitelist.operation;

import android.content.Context;

import com.orange.browser.provider.blackwhitelist.databaseObject.ListObject;
import com.orange.browser.provider.blackwhitelist.databaseObject.WhiteListExclusiveObject;
import com.orange.browser.provider.blackwhitelist.json.JSonList;
import com.orange.browser.provider.blackwhitelist.json.JSonListObject;

class ExclusionWhiteList extends AbsList<ListObject> {

    @Override
    public Class<? extends ListObject> getListObjectClass() {
        return WhiteListExclusiveObject.class;
    }

    @Override
    public ListObject createListObject(Context context) {
        return new WhiteListExclusiveObject(context);
    }

    @Override
    public JSonListObject[] pickJSonObjects(JSonList jsonWl) {
        if (jsonWl == null) {
            return null;
        }

        return jsonWl.exclusion;
    }

    @Override
    public String getVersionKey() {
        return WhiteBlackList.WHITELIST_VERSION;
    }
}
