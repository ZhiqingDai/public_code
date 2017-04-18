package com.xiao4r;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.GeolocationPermissions;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.xiao4r.utils.CommonUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final int INPUT_FILE_REQUEST_CODE = 1;

    private WebView mWebView;

    /**
     * 适配低版本
     */
    private ValueCallback<Uri> mUploadMessage;
    private final static int FILECHOOSER_RESULTCODE=2;
    /**
     * 适配Android5.0+
     */
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebView = (WebView) this.findViewById(R.id.webview);
        setUpWebViewDefaults(mWebView);
        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore the previous URL and history stack
            mWebView.restoreState(savedInstanceState);
        }
        mWebView.loadUrl(getString(R.string.main_url));
        setImmerseLayout(mWebView);

        mWebView.setWebChromeClient(new WebChromeClient() {

            // For Android 2.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                if (mUploadMessage != null) { return;}
                mUploadMessage = uploadMsg;
                startActivityForResult(Intent.createChooser(createDefaultOpenableIntent(), "File Chooser"), FILECHOOSER_RESULTCODE);
            }
            // For Android 3.0+
            public void openFileChooser( ValueCallback uploadMsg, String acceptType ) {
                openFileChooser(uploadMsg);
            }
            //For Android 4.1+
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture){
                openFileChooser(uploadMsg);
            }
            // Android 5.0+ 以上文件选择
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = filePathCallback;
                startActivityForResult(createDefaultOpenableIntent(), INPUT_FILE_REQUEST_CODE);
                return true;
            }
            /**
             * 获取文件选择的Intent
             * @return
             */
            private Intent createDefaultOpenableIntent(){

                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("image/*");

                Intent[] intentArray;
                Intent pictureIntent = null;
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH){
                    pictureIntent = createCameraIntentUp();
                }else{
                    pictureIntent = createCameraIntentLow();
                }
                if (pictureIntent != null) {
                    intentArray = new Intent[]{pictureIntent};
                } else {
                    intentArray = new Intent[0];
                }

                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "File Chooser");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

                return chooserIntent;
            }

            /**
             * 低版本相机拍照选取
             */
            private Intent createCameraIntentLow()  {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                try{
                    File externalDataDir = Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                    System.out.println("externalDataDir:" + externalDataDir);
                    File cameraDataFile = createImageFile();
                    mCameraPhotoPath = cameraDataFile.getAbsolutePath();
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(cameraDataFile));
                }catch (IOException e){
                    cameraIntent = null;
                }
                return cameraIntent;
            }

            /**
             * 5.0+版本相机拍照选取
             */
            private Intent createCameraIntentUp(){
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Log.e(TAG, "Unable to create Image File", ex);
                    }

                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                    } else {
                        takePictureIntent = null;
                    }
                }
                return takePictureIntent;
            }
        });

        if(mWebView.getUrl() == null) {
            mWebView.loadUrl(getString(R.string.main_url));
        }
    }

    /**
     * More info this method can be found at
     * http://developer.android.com/training/camera/photobasics.html
     *
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return imageFile;
    }
    /**
     * 用于API 19以上的沉浸式布局显示
     * @param view
     */
    protected void setImmerseLayout(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            int statusBarHeight = CommonUtils.getStatusBarHeight(this.getBaseContext());
            view.setPadding(0, statusBarHeight, 0, 0);
        }
    }


    /**
     * Convenience method to set some generic defaults for a
     * given WebView
     *
     * @param webView
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setUpWebViewDefaults(WebView webView) {
        WebSettings settings = webView.getSettings();

        // Enable Javascript
        settings.setJavaScriptEnabled(true);

        // Use WideViewport and Zoom out if there is no viewport defined
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        // Enable pinch to zoom without the zoom buttons
        settings.setBuiltInZoomControls(true);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            // Hide the zoom controls for HONEYCOMB+
            settings.setDisplayZoomControls(false);
        }

        // Enable remote debugging via chrome://inspect
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        // We set the WebViewClient to ensure links are consumed by the WebView rather
        // than passed to a browser if it can
        webView.setWebViewClient(new WebViewClient());
    }


    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {

        switch (requestCode){
            case INPUT_FILE_REQUEST_CODE:  //5.0以上系统处理
                if( mFilePathCallback == null) {
                    super.onActivityResult(requestCode, resultCode, data);
                    return;
                }
                Uri[] results = null;
                // Check that the response is a good one
                if(resultCode == Activity.RESULT_OK) {
                    if(data == null) {
                        // If there is not data, then we may have taken a photo
                        if(mCameraPhotoPath != null) {
                            results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                        }
                    } else {
                        String dataString = data.getDataString();
                        if (dataString != null) {
                            results = new Uri[]{Uri.parse(dataString)};
                        }
                    }
                }
                mFilePathCallback.onReceiveValue(results);
                mFilePathCallback = null;
                break;
            case FILECHOOSER_RESULTCODE:   //低版本处理
                if (null == mUploadMessage) {
                    super.onActivityResult(requestCode, resultCode, data);
                    return;
                };
                Uri result = data == null || resultCode != Activity.RESULT_OK ? null : data.getData();
                if (result == null && data == null && resultCode == Activity.RESULT_OK) {
                    File cameraFile = new File(mCameraPhotoPath);
                    if (cameraFile.exists()) {
                        result = Uri.fromFile(cameraFile);
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, result));
                    }
                }
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // 框架回调函数 onConfigurationChanged 出自 android.content.res.Configuration 包。
    // 参数 newConfig - 新设备的配备。
    // 当设备配置信息有改动（比如屏幕方向的改变，实体键盘的推开或合上等）时，
    // 并且如果此时有 Activity 正在运行，系统会调用这个函数。
    // 注意：onConfigurationChanged 只会响应应用程序在 AnroidMainifest.xml 中
    // 　　　通过 android:configChanges="配置类型" 指定的配置类型的改动；
    // 　　　而对于其他配置的更改，则系统会先销毁当前屏幕的 Activity ，
    // 　　　然后重新开启一个新的适应屏幕改变的 Activity 实例。
    @Override
    public void onConfigurationChanged( Configuration newConfig )
    {
        String url = mWebView.getUrl();

        System.out.println("========================"+url);

        // 一定要先调用父类的同名函数，让框架默认函数先处理
        // 下面这句一定不能省去，否则将引发：android.app.SuperNotCalledException 异常。
        super.onConfigurationChanged( newConfig );
        // 检测屏幕的方向：纵向或横向
//        if ( this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE )
//        {
//            mWebView.loadUrl(url);
//        }
//        else if ( this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT )
//        {
//            //当前为竖屏， 在此处添加额外的处理代码
//            mWebView.loadUrl(url);
//        }
//        //检测实体键盘的状态：推出或者合上
//        if ( newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO )
//        {
//            // 实体键盘处于推出状态，在此处添加额外的处理代码
//        }
//        else if ( newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES )
//        {
//            // 实体键盘处于合上状态，在此处添加额外的处理代码
//        }
    }
}

