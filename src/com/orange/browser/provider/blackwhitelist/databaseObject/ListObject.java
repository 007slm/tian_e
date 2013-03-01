
package com.orange.browser.provider.blackwhitelist.databaseObject;

import android.content.Context;

import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.IntegerColumn;
import com.dailystudio.dataobject.Template;
import com.dailystudio.dataobject.TextColumn;

public class ListObject extends DatabaseObject {

    public static final Column COLUMN_ID = new IntegerColumn("_id", false, true);
    public static final Column COLUMN_HOST = new TextColumn("host", false);
    public static final Column COLUMN_URL_PATTERN = new TextColumn("url", false);
    public static final Column COLUMN_MATCH_TYPE = new TextColumn("match_type", false);

    private final static Column[] sColumns = {
            COLUMN_ID,
            COLUMN_HOST,
            COLUMN_URL_PATTERN,
            COLUMN_MATCH_TYPE,
    };

    public ListObject(Context context) {
        super(context);

        final Template templ = getTemplate();

        templ.addColumns(sColumns);
    }

    public void setId(int id) {
        setValue(COLUMN_ID, id);
    }

    public int getId() {
        return getIntegerValue(COLUMN_ID);
    }

    public void setHost(String host) {
        setValue(COLUMN_HOST, host);
    }

    public String getHost() {
        return getTextValue(COLUMN_HOST);
    }

    public void setUrlPattern(String pattern) {
        setValue(COLUMN_URL_PATTERN, pattern);
    }

    public String getUrlPattern() {
        return getTextValue(COLUMN_URL_PATTERN);
    }

    public void setMatchType(String pattern) {
        setValue(COLUMN_MATCH_TYPE, pattern);
    }

    public String getMatchType() {
        return getTextValue(COLUMN_MATCH_TYPE);
    }

    @Override
    public String toString() {
        return String.format("%s(0x%08x): _id(%s), host(%s), url_pattern(%s), match_type(%s)",
                getClass().getSimpleName(),
                hashCode(),
                getId(),
                getHost(),
                getUrlPattern(),
                getMatchType());
    }

}
