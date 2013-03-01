
package com.orange.browser;

import com.orange.browser.R;
import java.util.List;

/**
 * Defines the interaction between the browser/renderer and the page running on
 * a given WebView frame, if the page supports the chromium SearchBox API.
 *
 * http://dev.chromium.org/searchbox
 *
 * The browser or container app can query the page for search results using
 * SearchBox.query() and receive suggestions by registering a listener on the
 * SearchBox object.
 *
 * @hide pending API council approval.
 */
public interface SearchBox {
    /**
     * Sets the current searchbox query. Note that the caller must call
     * onchange() to ensure that the search page processes this query.
     */
    void setQuery(String query);

    /**
     * Verbatim is true if the caller suggests that the search page
     * treat the current query as a verbatim search query (as opposed to a
     * partially typed search query). As with setQuery, onchange() must be
     * called to ensure that the search page processes the query.
     */
    void setVerbatim(boolean verbatim);

    /**
     * These attributes must contain the offset to the characters that immediately
     * follow the start and end of the selection in the search box. If there is
     * no such selection, then both selectionStart and selectionEnd must be the offset
     * to the character that immediately follows the text entry cursor. In the case
     * that there is no explicit text entry cursor, the cursor is
     * implicitly at the end of the input.
     */
    void setSelection(int selectionStart, int selectionEnd);

    /**
     * Sets the dimensions of the view (if any) that overlaps the current
     * window object. This is to ensure that the page renders results in
     * a manner that allows them to not be obscured by such a view. Note
     * that a call to onresize() is required if these dimensions change.
     */
    void setDimensions(int x, int y, int width, int height);

    /**
     * Notify the search page of any changes to the searchbox. Such as
     * a change in the typed query (onchange), the user commiting a given query
     * (onsubmit), or a change in size of a suggestions dropdown (onresize).
     *
     * @param listener an optional listener to notify of the success of the operation,
     *      indicating if the javascript function existed and could be called or not.
     *      It will be called on the UI thread.
     */
    void onchange(SearchBoxListener listener);
    void onsubmit(SearchBoxListener listener);
    void onresize(SearchBoxListener listener);
    void oncancel(SearchBoxListener listener);

    /**
     * Add and remove listeners to the given Searchbox. Listeners are notified
     * of any suggestions to the query that the underlying search engine might
     * provide.
     */
    void addSearchBoxListener(SearchBoxListener l);
    void removeSearchBoxListener(SearchBoxListener l);

    /**
     * Indicates if the searchbox API is supported in the current page.
     */
    void isSupported(IsSupportedCallback callback);

    /**
     * Listeners (if any) will be called on the thread that created the
     * webview.
     */
    public abstract class SearchBoxListener {
        public void onSuggestionsReceived(String query, List<String> suggestions) {}
        public void onChangeComplete(boolean called) {}
        public void onSubmitComplete(boolean called) {}
        public void onResizeComplete(boolean called) {}
        public void onCancelComplete(boolean called) {}
    }

    interface IsSupportedCallback {
        void searchBoxIsSupported(boolean supported);
    }
}
