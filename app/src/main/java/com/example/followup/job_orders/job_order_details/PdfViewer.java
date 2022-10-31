package com.example.followup.job_orders.job_order_details;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.followup.R;

public class PdfViewer extends AppCompatActivity {
    WebView webView;
    ProgressBar loading;
    String url;
    ImageView back;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);
        webView = findViewById(R.id.webView);
        loading = findViewById(R.id.loading);
        back = findViewById(R.id.back);
        back.setOnClickListener(v -> onBackPressed());

        url = getIntent().getStringExtra("url");

        Log.e("Url : ", url );

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);

        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSupportMultipleWindows(false);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.getSettings().setSupportZoom(true);

        String myUA = "Android" + "Chrome/[.0-9]* Mobile";
        webView.getSettings().setUserAgentString(myUA);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("TAG", url);
                loading.setVisibility(View.VISIBLE);
                view.loadUrl(url);

                return true;
            }

            @Override
            public void onPageFinished(WebView view, final String url) {
                loading.setVisibility(View.GONE);
            }
        });


        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, android.os.Message resultMsg) {

                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(webView);
                resultMsg.sendToTarget();
                return true;
            }

            @Override
            public void onPermissionRequest(PermissionRequest request) {
                Log.d("Permission", "onPermissionRequest");
                PdfViewer.this.runOnUiThread(() -> {
                    Log.d("Permission", request.getOrigin().toString());
                    request.grant(request.getResources());
                });
            }
        });

        webView.loadUrl("file:///android_asset/pdfviewer.html?"+url);
//        webView.loadUrl(url);
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();
    }
}