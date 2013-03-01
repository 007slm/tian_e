package com.orange.browser;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.net.http.SslCertificate;
import android.net.http.SslError;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.orange.browser.R;
import com.orange.browser.reflect.InvokeWebviewMethod;

import java.util.Date;

/**
 * Displays page info
 *
 */
public class PageDialogsHandler {

    private Context mContext;
    private Controller mController;
    private boolean mPageInfoFromShowSSLCertificateOnError;
    private String mUrlCertificateOnError;
    private Tab mPageInfoView;
    private AlertDialog mPageInfoDialog;

    // as SSLCertificateOnError has different style for landscape / portrait,
    // we have to re-open it when configuration changed
    private AlertDialog mSSLCertificateOnErrorDialog;
    private WebView mSSLCertificateOnErrorView;
    private SslErrorHandler mSSLCertificateOnErrorHandler;
    private SslError mSSLCertificateOnErrorError;

    // as SSLCertificate has different style for landscape / portrait, we
    // have to re-open it when configuration changed
    private AlertDialog mSSLCertificateDialog;
    private Tab mSSLCertificateView;
    private HttpAuthenticationDialog mHttpAuthenticationDialog;

    public PageDialogsHandler(Context context, Controller controller) {
        mContext = context;
        mController = controller;
    }

    public void onConfigurationChanged(Configuration config) {
        if (mPageInfoDialog != null) {
            mPageInfoDialog.dismiss();
            showPageInfo(mPageInfoView,
                         mPageInfoFromShowSSLCertificateOnError,
                         mUrlCertificateOnError);
        }
        if (mSSLCertificateDialog != null) {
            mSSLCertificateDialog.dismiss();
            showSSLCertificate(mSSLCertificateView);
        }
        if (mSSLCertificateOnErrorDialog != null) {
            mSSLCertificateOnErrorDialog.dismiss();
            showSSLCertificateOnError(mSSLCertificateOnErrorView,
                                      mSSLCertificateOnErrorHandler,
                                      mSSLCertificateOnErrorError);
        }
        if (mHttpAuthenticationDialog != null) {
            mHttpAuthenticationDialog.reshow();
        }
    }

    /**
     * Displays an http-authentication dialog.
     */
    void showHttpAuthentication(final Tab tab, final HttpAuthHandler handler, String host, String realm) {
        mHttpAuthenticationDialog = new HttpAuthenticationDialog(mContext, host, realm);
        mHttpAuthenticationDialog.setOkListener(new HttpAuthenticationDialog.OkListener() {
            public void onOk(String host, String realm, String username, String password) {
                setHttpAuthUsernamePassword(host, realm, username, password);
                handler.proceed(username, password);
                mHttpAuthenticationDialog = null;
            }
        });
        mHttpAuthenticationDialog.setCancelListener(new HttpAuthenticationDialog.CancelListener() {
            public void onCancel() {
                handler.cancel();
                mController.onUpdatedSecurityState(tab);
                mHttpAuthenticationDialog = null;
            }
        });
        mHttpAuthenticationDialog.show();
    }

    /**
     * Set HTTP authentication password.
     *
     * @param host The host for the password
     * @param realm The realm for the password
     * @param username The username for the password. If it is null, it means
     *            password can't be saved.
     * @param password The password
     */
    public void setHttpAuthUsernamePassword(String host, String realm,
                                            String username,
                                            String password) {
        WebView w = mController.getCurrentTopWebView();
        if (w != null) {
            w.setHttpAuthUsernamePassword(host, realm, username, password);
        }
    }

    /**
     * Displays a page-info dialog.
     * @param tab The tab to show info about
     * @param fromShowSSLCertificateOnError The flag that indicates whether
     * this dialog was opened from the SSL-certificate-on-error dialog or
     * not. This is important, since we need to know whether to return to
     * the parent dialog or simply dismiss.
     * @param urlCertificateOnError The URL that invokes SSLCertificateError.
     * Null when fromShowSSLCertificateOnError is false.
     */
    void showPageInfo(final Tab tab,
            final boolean fromShowSSLCertificateOnError,
            final String urlCertificateOnError) {
        final LayoutInflater factory = LayoutInflater.from(mContext);

        final View pageInfoView = factory.inflate(R.layout.page_info, null);

        final WebView view = tab.getWebView();

        String url = fromShowSSLCertificateOnError ? urlCertificateOnError : tab.getUrl();
        String title = tab.getTitle();

        if (url == null) {
            url = "";
        }
        if (title == null) {
            title = "";
        }

        ((TextView) pageInfoView.findViewById(R.id.address)).setText(url);
        ((TextView) pageInfoView.findViewById(R.id.title)).setText(title);

        mPageInfoView = tab;
        mPageInfoFromShowSSLCertificateOnError = fromShowSSLCertificateOnError;
        mUrlCertificateOnError = urlCertificateOnError;

//        AlertDialog.Builder alertDialogBuilder =
//            new AlertDialog.Builder(mContext)
//            .setTitle(R.string.page_info)
//            .setIcon(android.R.drawable.ic_dialog_info)
//            .setView(pageInfoView)
//            .setPositiveButton(
//                R.string.ok,
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog,
//                                        int whichButton) {
//                        mPageInfoDialog = null;
//                        mPageInfoView = null;
//
//                        // if we came here from the SSL error dialog
//                        if (fromShowSSLCertificateOnError) {
//                            // go back to the SSL error dialog
//                            showSSLCertificateOnError(
//                                mSSLCertificateOnErrorView,
//                                mSSLCertificateOnErrorHandler,
//                                mSSLCertificateOnErrorError);
//                        }
//                    }
//                })
//            .setOnCancelListener(
//                new DialogInterface.OnCancelListener() {
//                    public void onCancel(DialogInterface dialog) {
//                        mPageInfoDialog = null;
//                        mPageInfoView = null;
//
//                        // if we came here from the SSL error dialog
//                        if (fromShowSSLCertificateOnError) {
//                            // go back to the SSL error dialog
//                            showSSLCertificateOnError(
//                                mSSLCertificateOnErrorView,
//                                mSSLCertificateOnErrorHandler,
//                                mSSLCertificateOnErrorError);
//                        }
//                    }
//                });
//
//        // if we have a main top-level page SSL certificate set or a certificate
//        // error
//        if (fromShowSSLCertificateOnError ||
//                (view != null && view.getCertificate() != null)) {
//            // add a 'View Certificate' button
//            alertDialogBuilder.setNeutralButton(
//                R.string.view_certificate,
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog,
//                                        int whichButton) {
//                        mPageInfoDialog = null;
//                        mPageInfoView = null;
//
//                        // if we came here from the SSL error dialog
//                        if (fromShowSSLCertificateOnError) {
//                            // go back to the SSL error dialog
//                            showSSLCertificateOnError(
//                                mSSLCertificateOnErrorView,
//                                mSSLCertificateOnErrorHandler,
//                                mSSLCertificateOnErrorError);
//                        } else {
//                            // otherwise, display the top-most certificate from
//                            // the chain
//                            showSSLCertificate(tab);
//                        }
//                    }
//                });
//        }
//
//        mPageInfoDialog = alertDialogBuilder.show();
        CustomDialog dialog = new CustomDialog(mContext);
        dialog.setTitle(R.string.page_info);
        dialog.setConfirmButton(null);
        dialog.setCustomView(pageInfoView);
        dialog.show();
    }

    /**
     * Displays the main top-level page SSL certificate dialog
     * (accessible from the Page-Info dialog).
     * @param tab The tab to show certificate for.
     */
    private void showSSLCertificate(final Tab tab) {

        SslCertificate cert = tab.getWebView().getCertificate();
        if (cert == null) {
            return;
        }

        mSSLCertificateView = tab;
        mSSLCertificateDialog = createSslCertificateDialog(cert, tab.getSslCertificateError())
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                                mSSLCertificateDialog = null;
                                mSSLCertificateView = null;

                                showPageInfo(tab, false, null);
                            }
                        })
                .setOnCancelListener(
                        new DialogInterface.OnCancelListener() {
                            public void onCancel(DialogInterface dialog) {
                                mSSLCertificateDialog = null;
                                mSSLCertificateView = null;

                                showPageInfo(tab, false, null);
                            }
                        })
                .show();
    }

    /**
     * Displays the SSL error certificate dialog.
     * @param view The target web-view.
     * @param handler The SSL error handler responsible for cancelling the
     * connection that resulted in an SSL error or proceeding per user request.
     * @param error The SSL error object.
     */
    void showSSLCertificateOnError(
            final WebView view, final SslErrorHandler handler,
            final SslError error) {

        SslCertificate cert = error.getCertificate();
        if (cert == null) {
            return;
        }

        mSSLCertificateOnErrorHandler = handler;
        mSSLCertificateOnErrorView = view;
        mSSLCertificateOnErrorError = error;
        mSSLCertificateOnErrorDialog = createSslCertificateDialog(cert, error)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                                mSSLCertificateOnErrorDialog = null;
                                mSSLCertificateOnErrorView = null;
                                mSSLCertificateOnErrorHandler = null;
                                mSSLCertificateOnErrorError = null;
//                                view.getWebViewClient().onReceivedSslError(
//                                                view, handler, error);
                                InvokeWebviewMethod.getWebViewClient(view).
                                        onReceivedSslError(view, handler, error);
                            }
                        })
                 .setNeutralButton(R.string.page_info_view,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                                mSSLCertificateOnErrorDialog = null;

                                // do not clear the dialog state: we will
                                // need to show the dialog again once the
                                // user is done exploring the page-info details

                                showPageInfo(mController.getTabControl()
                                        .getTabFromView(view),
                                        true,
                                        error.toString());
                            }
                        })
                .setOnCancelListener(
                        new DialogInterface.OnCancelListener() {
                            public void onCancel(DialogInterface dialog) {
                                mSSLCertificateOnErrorDialog = null;
                                mSSLCertificateOnErrorView = null;
                                mSSLCertificateOnErrorHandler = null;
                                mSSLCertificateOnErrorError = null;
//                                view.getWebViewClient().onReceivedSslError(
//                                                view, handler, error);
                                InvokeWebviewMethod.getWebViewClient(view).
                                        onReceivedSslError(view, handler, error);
                            }
                        })
                .show();
    }

    /*
     * Creates an AlertDialog to display the given certificate. If error is
     * null, text is added to state that the certificate is valid and the icon
     * is set accordingly. If error is non-null, it must relate to the supplied
     * certificate. In this case, error is used to add text describing the
     * problems with the certificate and a different icon is used.
     */
    private AlertDialog.Builder createSslCertificateDialog(SslCertificate certificate,
            SslError error) {

        final View certificateView =
                inflateCertificateView(certificate);
        if (certificateView == null) {
            return null;
        }

        final LinearLayout placeholder =
                (LinearLayout) certificateView.findViewById(R.id.placeholder);

        LayoutInflater factory = LayoutInflater.from(mContext);
        int iconId;

        if (error == null) {
            iconId = R.drawable.ic_dialog_browser_certificate_secure;
            LinearLayout table = (LinearLayout)factory.inflate(R.layout.ssl_success, placeholder);
            TextView successString = (TextView)table.findViewById(R.id.success);
//            successString.setText(com.android.internal.R.string.ssl_certificate_is_valid);
        } else {
            iconId = R.drawable.ic_dialog_browser_certificate_partially_secure;
            if (error.hasError(SslError.SSL_UNTRUSTED)) {
                addError(factory, placeholder, R.string.ssl_untrusted);
            }
            if (error.hasError(SslError.SSL_IDMISMATCH)) {
                addError(factory, placeholder, R.string.ssl_mismatch);
            }
            if (error.hasError(SslError.SSL_EXPIRED)) {
                addError(factory, placeholder, R.string.ssl_expired);
            }
            if (error.hasError(SslError.SSL_NOTYETVALID)) {
                addError(factory, placeholder, R.string.ssl_not_yet_valid);
            }
            // The SslError should always have at least one type of error and we
            // should explicitly handle every type of error it supports. We
            // therefore expect the condition below to never be hit. We use it
            // as as safety net in case a new error type is added to SslError
            // without the logic above being updated accordingly.
            if (placeholder.getChildCount() == 0) {
                addError(factory, placeholder, R.string.ssl_unknown);
            }
        }

        return new AlertDialog.Builder(mContext)
                .setTitle("ssl_certificate")
                .setIcon(iconId)
                .setView(certificateView);
    }
    
    /**
     * Inflates the SSL certificate view (helper method).
     * 
     * @param certificate The SSL certificate.
     * @return The resultant certificate view with issued-to, issued-by,
     *         issued-on, expires-on, and possibly other fields set. If the
     *         input certificate is null, returns null.
     */
    private View inflateCertificateView(SslCertificate certificate) {
        if (certificate == null) {
            return null;
        }

        LayoutInflater factory = LayoutInflater.from(mContext);

        View certificateView = factory.inflate(
                R.layout.ssl_certificate, null);

        // issued to:
        SslCertificate.DName issuedTo = certificate.getIssuedTo();
        if (issuedTo != null) {
            ((TextView) certificateView.findViewById(R.id.to_common))
                    .setText(issuedTo.getCName());
            ((TextView) certificateView.findViewById(R.id.to_org))
                    .setText(issuedTo.getOName());
            ((TextView) certificateView.findViewById(R.id.to_org_unit))
                    .setText(issuedTo.getUName());
        }

        // issued by:
        SslCertificate.DName issuedBy = certificate.getIssuedBy();
        if (issuedBy != null) {
            ((TextView) certificateView.findViewById(R.id.by_common))
                    .setText(issuedBy.getCName());
            ((TextView) certificateView.findViewById(R.id.by_org))
                    .setText(issuedBy.getOName());
            ((TextView) certificateView.findViewById(R.id.by_org_unit))
                    .setText(issuedBy.getUName());
        }

        // issued on:
        String issuedOn = formatCertificateDate(
                certificate.getValidNotBeforeDate());
        ((TextView) certificateView.findViewById(R.id.issued_on))
                .setText(issuedOn);

        // expires on:
        String expiresOn = formatCertificateDate(
                certificate.getValidNotAfterDate());
        ((TextView) certificateView.findViewById(R.id.expires_on))
                .setText(expiresOn);

        return certificateView;
    }

    /**
     * Formats the certificate date to a properly localized date string.
     * @return Properly localized version of the certificate date string and
     * the "" if it fails to localize.
     */
    private String formatCertificateDate(Date certificateDate) {
      if (certificateDate == null) {
          return "";
      }
      String formattedDate = DateFormat.getDateFormat(mContext).format(certificateDate);
      if (formattedDate == null) {
          return "";
      }
      return formattedDate;
    }
    
    private void addError(LayoutInflater inflater, LinearLayout parent, int error) {
        TextView textView = (TextView) inflater.inflate(R.layout.ssl_warning,
                parent, false);
        textView.setText(error);
        parent.addView(textView);
    }
}
