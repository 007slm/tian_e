
package com.orange.browser.provider.blackwhitelist.json;

public class JSonList {
    public int version;
    public JSonListObject[] list;
    public JSonListObject[] exclusion;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(getClass().getSimpleName());

        builder.append(String.format(": (0x%08x)\n", hashCode()));

        if (list != null) {
            builder.append(String.format("list          : %d\n", list.length));
            for (JSonListObject wlObject : list) {
                builder.append(wlObject.toString());
            }
        } else {
            builder.append(String.format("list          : <none>\n"));
        }

        if (exclusion != null) {
            builder.append(String.format("exclusion           : %d\n", exclusion.length));
            for (JSonListObject wlObject : exclusion) {
                builder.append(wlObject.toString());
            }
        } else {
            builder.append(String.format("images           : <none>\n"));
        }

        return builder.toString();
    }
}
