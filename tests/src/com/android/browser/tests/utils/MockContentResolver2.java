package com.android.browser.tests.utils;

import com.google.android.collect.Maps;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.IContentProvider;
import android.database.ContentObserver;
import android.net.Uri;

import java.util.Map;

public class MockContentResolver2 extends ContentResolver {

    Map<String, ContentProvider> mProviders;
    private final MockObserverNode mRootNode = new MockObserverNode("");

    /*
     * Creates a local map of providers. This map is used instead of the global map when an
     * API call tries to acquire a provider.
     */
    public MockContentResolver2() {
        super(null);
        mProviders = Maps.newHashMap();
    }

    /**
     * Adds access to a provider based on its authority
     *
     * @param name The authority name associated with the provider.
     * @param provider An instance of {@link android.content.ContentProvider} or one of its
     * subclasses, or null.
     */
    public void addProvider(String name, ContentProvider provider) {

        /*
         * Maps the authority to the provider locally.
         */
        mProviders.put(name, provider);
    }

    /** @hide */
    @Override
    protected IContentProvider acquireProvider(Context context, String name) {
        return acquireExistingProvider(context, name);
    }

    /** @hide */
    @Override
    protected IContentProvider acquireExistingProvider(Context context, String name) {

        /*
         * Gets the content provider from the local map
         */
        final ContentProvider provider = mProviders.get(name);

        if (provider != null) {
            return provider.getIContentProvider();
        } else {
            return null;
        }
    }

    /** @hide */
    @Override
    public boolean releaseProvider(IContentProvider provider) {
        return true;
    }

    @Override
    public void notifyChange(Uri uri, ContentObserver observer,
            boolean syncToNetwork) {
        mRootNode.notifyMyObservers(uri, 0, observer, false);
    }

    public void safeRegisterContentObserver(Uri uri, boolean notifyForDescendents,
            ContentObserver observer) {
        mRootNode.addObserver(uri, observer, notifyForDescendents);
    }

    public void safeUnregisterContentObserver(ContentObserver observer) {
        mRootNode.removeObserver(observer);
    }

}
