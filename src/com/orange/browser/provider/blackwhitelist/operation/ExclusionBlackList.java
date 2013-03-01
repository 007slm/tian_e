
package com.orange.browser.provider.blackwhitelist.operation;

import android.content.Context;

import com.orange.browser.provider.blackwhitelist.databaseObject.BlackListExclusiveObject;
import com.orange.browser.provider.blackwhitelist.databaseObject.ListObject;
import com.orange.browser.provider.blackwhitelist.json.JSonList;
import com.orange.browser.provider.blackwhitelist.json.JSonListObject;

class ExclusionBlackList extends AbsList<ListObject> {

    @Override
    public Class<? extends ListObject> getListObjectClass() {
        return BlackListExclusiveObject.class;
    }

    @Override
    public ListObject createListObject(Context context) {
        return new BlackListExclusiveObject(context);
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
        return WhiteBlackList.BLACKLIST_VERSION;
    }
}
