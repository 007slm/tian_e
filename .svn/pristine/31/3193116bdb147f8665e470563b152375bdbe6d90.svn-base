package android.webkit;

/**
 * Interface to receive notifications when items are added to the
 * {@link WebBackForwardList}.
 * {@hide}
 */
public abstract class WebBackForwardListClient {

    /**
     * Notify the client that <var>item</var> has been added to the
     * WebBackForwardList.
     * @param item The newly created WebHistoryItem
     */
    public void onNewHistoryItem(WebHistoryItem item) { }

    /**
     * Notify the client that the <var>item</var> at <var>index</var> is now
     * the current history item.
     * @param item A WebHistoryItem
     * @param index The new history index
     */
    public void onIndexChanged(WebHistoryItem item, int index) { }
}
