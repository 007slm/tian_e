
package com.orange.browser.provider.blackwhitelist.operation;

import android.content.Context;

import com.orange.browser.provider.blackwhitelist.databaseObject.BlackListInclusiveObject;
import com.orange.browser.provider.blackwhitelist.databaseObject.ListObject;
import com.orange.browser.provider.blackwhitelist.json.JSonList;
import com.orange.browser.provider.blackwhitelist.json.JSonListObject;

class InclusionBlackList extends AbsList<ListObject> {

    @Override
    public Class<? extends ListObject> getListObjectClass() {
        return BlackListInclusiveObject.class;
    }

    @Override
    public ListObject createListObject(Context context) {
        return new BlackListInclusiveObject(context);
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
        return WhiteBlackList.BLACKLIST_VERSION;
    }
}
