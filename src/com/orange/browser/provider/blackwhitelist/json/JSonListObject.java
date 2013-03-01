
package com.orange.browser.provider.blackwhitelist.json;

public class JSonListObject {

    public String match_type;
    public String url_pattern;
    public String url_host;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(getClass().getSimpleName());

        builder.append(String.format(": (0x%08x)\n", hashCode()));
        builder.append(String.format("|- match_type      : %s\n", match_type));
        builder.append(String.format("|- url_pattern     : %s\n", url_pattern));
        builder.append(String.format("`- url_host        : %s\n", url_host));
        builder.append("\n");

        return builder.toString();
    }

}
