package com.github.skyfe79.android.filedownloadwithdownloadmanager

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.webkit.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 외부 스토리지에 쓰기 권한을 요청해야 한다.
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }

        // 웹뷰가 파일 액세스가 가능토록하고 webViewClient에서 파일 다운로드를 가능토록 한다.
        webView.settings.allowFileAccess = true
        webView.webViewClient = MyWebViewClinet(this)

        // 사이트를 로드한다.
        loadSite()
    }

    private fun loadSite() {
        webView.loadUrl("https://www.thinkbroadband.com/download")
    }
}


class MyWebViewClinet(private val context: Context): WebViewClient(), DownloadListener {
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        view.loadUrl(url)
        view.setDownloadListener(this)
        return true
    }

    override fun onDownloadStart(url: String, userAgent: String, contentDisposition: String, mimeType: String, contentLength: Long) {
        val request = DownloadManager.Request(Uri.parse(url))

        request.setMimeType(mimeType)
        //------------------------COOKIE!!(딱히 없어도 된다)------------------------
        val cookies = CookieManager.getInstance().getCookie(url)
        request.addRequestHeader("cookie", cookies)
        //------------------------COOKIE!!--------------------------------------
        request.addRequestHeader("User-Agent", userAgent)
        request.setDescription("Downloading file...")
        request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType))
        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimeType))
        val downloadManager = context.getSystemService(AppCompatActivity.DOWNLOAD_SERVICE) as? DownloadManager
        downloadManager?.apply {
            enqueue(request)
            Toast.makeText(context, "Downloading File", Toast.LENGTH_LONG).show()
        }
    }
}
