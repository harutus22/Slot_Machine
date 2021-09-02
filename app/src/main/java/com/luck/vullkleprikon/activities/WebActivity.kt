package com.luck.vullkleprikon.activities

import android.Manifest
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Message
import android.provider.MediaStore
import android.util.Log
import android.webkit.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.luck.vullkleprikon.App
import com.luck.vullkleprikon.R
import com.luck.vullkleprikon.utils.*
import kotlinx.android.synthetic.main.activity_web.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class WebActivity : AppCompatActivity(), ConnectionService.ConnectivityReceiveListener {
    companion object {
        private const val CAMERA_REQUEST_CODE = 113
        private const val REQUEST_SELECT_FILE = 1
        private const val INTENT_FILE_TYPE = "image/*"
        private const val CAMERA_PHOTO_PATH_POSTFIX = "file:"
        private const val PHOTO_NAME_POSTFIX = "JPEG_"
        private const val PHOTO_FORMAT = ".jpg"
        var firstUrl = ""
        var isFirst = true
        var isDomain = false
    }

    private lateinit var receiver: ConnectionService
    private var mUploadMessage: ValueCallback<Array<Uri>>? = null
    private var cameraImagePath: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)

        home_btn.setOnClickListener {
            startActivity(Intent(this, ShowcaseActivity::class.java).putExtra(HOME_CLICK, true))
            finish()
        }

        checkInternetConnection()
        Log.e("WEB_VIEW", "onCreate")
        getCustomWebChromeClient()
        webView.webViewClient = object : WebViewClient() {
            override fun onReceivedSslError(
                view: WebView?, handler: SslErrorHandler,
                error: SslError
            ) {
                val builder = AlertDialog.Builder(this@WebActivity)
                var message = "SSL Certificate error."
                when (error.primaryError) {
                    SslError.SSL_UNTRUSTED -> {
                        Log.d(
                            "TEST_OF_ORDER_LINK",
                            "SslError : The certificate authority is not trusted."
                        )
                        message = "The certificate authority is not trusted.";
                    }
                    SslError.SSL_EXPIRED -> {
                        Log.d(
                            "TEST_OF_ORDER_LINK",
                            "SslError : The certificate has expired."
                        )
                        message = "The certificate has expired.";
                    }
                    SslError.SSL_IDMISMATCH -> {
                        Log.d(
                            "TEST_OF_ORDER_LINK",
                            "The certificate Hostname mismatch."
                        )
                        message = "The certificate Hostname mismatch."
                    }
                    SslError.SSL_NOTYETVALID -> {
                        Log.d(
                            "TEST_OF_ORDER_LINK",
                            "The certificate is not yet valid."
                        )
                        message = "The certificate is not yet valid."
                    }
                    SslError.SSL_DATE_INVALID -> {
                        Log.d(
                            "TEST_OF_ORDER_LINK",
                            "The certificate date is not valid."
                        )
                        message = "The certificate date is not valid."
                    }
                    SslError.SSL_INVALID -> {
                        Log.d(
                            "TEST_OF_ORDER_LINK",
                            "The certificate is not valid."
                        )
                        message = "The certificate is not valid."
                    }
                }
                message += " Do you want to continue anyway?"

                builder.setTitle("SSL Certificate Error")
                builder.setMessage(message)

                builder.setPositiveButton(
                    "continue"
                ) { dialog, which -> handler.proceed() }
                builder.setNegativeButton(
                    "cancel"
                ) { dialog, which -> handler.cancel() }
                val dialog = builder.create()
                dialog.show()
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                Log.e("Success", "URL $url")

                firstUrl = PreferenceManager.getDefaultSharedPreferences(this@WebActivity)
                    .getString("URL_FIRST", "")!!
                Log.e("Success", "URL First $firstUrl")

                if (url!!.contains(firstUrl)) {
                    if (isFirst)
                        PreferenceManager.getDefaultSharedPreferences(this@WebActivity).edit()
                            .putString("URL_FIRST", url!!.substringAfter("://")).apply()
                    if (url.contains("http")) {
                        Log.e("LOG_TAG", "shouldOverrideUrlLoading URL1: $url ")
                        view?.loadUrl(url)
                    }
                } else {

                        Log.e("LOG_TAG", "shouldOverrideUrlLoading URL1: $url ")
                        view?.loadUrl(url)
                }
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {

                super.onPageFinished(view, url)
            }
        }
        configureWebViewSettings()
        val url = getURL()
        webView.loadUrl(url)


        webView.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            //filename of downloading file 
            var filename = URLUtil.guessFileName(url, contentDisposition, mimetype)

            //alertdialog builder created
            val builder = AlertDialog.Builder(this)
            //alertdialog title set
            builder.setTitle("Download")
            //alertdialog message set
            builder.setMessage("Do you want to save $filename")
            //if yes clicks,following code will executed
            builder.setPositiveButton("Yes") { dialog, which ->
                //DownloadManager request created based on url
                val request = DownloadManager.Request(Uri.parse(url))
                //get cookie
                val cookie = CookieManager.getInstance().getCookie(url)
                //add cookie to request
                request.addRequestHeader("Cookie", cookie)
                //add User-agent to request
                request.addRequestHeader("User-Agent", userAgent)
                //Files are scanned before downloading
                request.allowScanningByMediaScanner()
                //download notification is visible while downloading and after download completion
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                //DownloadManager Service created
                val downloadmanager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                //Files are downloaded to Download folder
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename)
                //download starts
                downloadmanager.enqueue(request)
            }
            builder.setNegativeButton("Cancel")
            { dialog, which ->
                //dialog cancels
                dialog.cancel()
            }
            //alertdialog created
            val dialog: AlertDialog = builder.create()
            //shows alertdialog
            dialog.show()
        }


    }

    private fun checkInternetConnection() = ConnectionService.isConnected()

    override fun onResume() {
        super.onResume()
        val intFilter = IntentFilter()
        intFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)

        receiver = ConnectionService()
        registerReceiver(receiver, intFilter)
        App.getInstance().setConnectivityManager(this)
    }

    private fun configureWebViewSettings() {
        webView.settings.javaScriptEnabled = true
        webView.settings.setAppCacheEnabled(true)
        webView.settings.cacheMode = WebSettings.LOAD_DEFAULT
        webView.settings.setSupportZoom(true)
        webView.settings.builtInZoomControls = false
        webView.settings.blockNetworkImage = false
        webView.settings.loadsImagesAutomatically = true
        webView.settings.useWideViewPort = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.javaScriptCanOpenWindowsAutomatically = true
        webView.settings.allowFileAccess = true
        webView.settings.domStorageEnabled = true
        webView.settings.setSupportMultipleWindows(true)
        webView.settings.loadWithOverviewMode = true
        webView.settings.allowContentAccess = true
        webView.settings.setGeolocationEnabled(true)
        webView.webChromeClient = getCustomWebChromeClient()
    }

    private fun getCustomWebChromeClient() = object : WebChromeClient() {

        override fun onCreateWindow(
            view: WebView?,
            isDialog: Boolean,
            isUserGesture: Boolean,
            resultMsg: Message?
        ): Boolean {
            val result = view!!.hitTestResult
            val data = result.extra
            view.loadUrl(data!!)
            return false
        }

        @RequiresApi(Build.VERSION_CODES.M)
        override fun onShowFileChooser(
            view: WebView?,
            filePath: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams?
        ): Boolean {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
            mUploadMessage?.onReceiveValue(null)
            mUploadMessage = null
            mUploadMessage = filePath

            val takePictureIntent = createImageCaptureIntent()

            val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
            contentSelectionIntent.type = INTENT_FILE_TYPE

            val intentArray: Array<Intent?>
            intentArray = arrayOf(takePictureIntent)

            val chooserIntent = Intent(Intent.ACTION_CHOOSER)
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
            chooserIntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.file_chooser_title))
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)

            try {
                startActivityForResult(chooserIntent, REQUEST_SELECT_FILE)
            } catch (e: ActivityNotFoundException) {
                mUploadMessage = null
                cameraImagePath = null

                Toast.makeText(
                    this@WebActivity,
                    getString(R.string.cannot_open_file_chooser_txt),
                    Toast.LENGTH_LONG
                ).show()

                return false
            }

            return true
        }
    }

    private fun createImageCaptureIntent(): Intent? {
        var captureImageIntent: Intent? = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (captureImageIntent?.resolveActivity(packageManager) != null) {
            var imageFile: File? = null

            try {
                imageFile = createImageFile()
                captureImageIntent.putExtra("CameraImagePath", cameraImagePath)
            } catch (ex: IOException) {
                ex.printStackTrace()
            }

            if (imageFile != null) {
                cameraImagePath = CAMERA_PHOTO_PATH_POSTFIX + imageFile.absolutePath
                captureImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile))
            } else {
                captureImageIntent = null
            }
        }

        return captureImageIntent
    }

    private fun createImageFile(): File? {
        val timeStamp = SimpleDateFormat.getDateInstance().format(Date())
        val imageFileName = PHOTO_NAME_POSTFIX + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, PHOTO_FORMAT, storageDir)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != REQUEST_SELECT_FILE || mUploadMessage == null) return

        var results: Array<Uri>? = null

        if (resultCode == RESULT_OK) {
            if (data == null) {
                if (cameraImagePath != null) results = arrayOf(Uri.parse(cameraImagePath))
            } else {
                val dataString = data.dataString
                if (dataString != null) results = arrayOf(Uri.parse(dataString))
            }
        }
        mUploadMessage?.onReceiveValue(results)
        mUploadMessage = null
    }

    override fun onStop() {
        super.onStop()
        setURL(webView.url)
    }

    private fun setURL(url: String?) {
        val editor = PreferenceManager.getDefaultSharedPreferences(this).edit()
        editor.putString(LAST_URL, url)
        editor.apply()
    }

    private fun getURL(): String {
        return PreferenceManager.getDefaultSharedPreferences(this)
            .getString(LAST_URL, "")!! + "?hash=" + getSharedString(UUID, this) + "&app=" + packageName
    }

    private fun doBack(): Boolean {
        if (webView.canGoBack()) {
            webView.goBack()
            return true
        }
        return false
    }

    override fun onBackPressed() {
        doBack()
    }

    override fun onNetworkConnection(isConnected: Boolean) {
        if (!isConnected) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Internet is not connected, please connect to internet")
                .setTitle("Connection Error")
                .setCancelable(false).setPositiveButton("Try Again") { dialog, numb ->
                    if (checkInternetConnection()) {
                        dialog.dismiss()
                        webView.reload()
                    }
                    else
                        builder.show()
                }
            builder.show()
        }
    }

    override fun onDestroy() {
        unregisterReceiver(receiver)
        super.onDestroy()
    }
}