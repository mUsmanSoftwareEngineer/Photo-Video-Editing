package collagestudio.photocollage.collagemaker.activity;

import android.content.ContextWrapper;
import android.os.Build;
import android.webkit.WebView;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import collagestudio.photocollage.collagemaker.AppOpenManager;
import dauroi.photoeditor.PhotoEditorApp;


public class PhotoCollageApp extends PhotoEditorApp{
    private static AppOpenManager appOpenManager;
    @Override
    public void onCreate() {
        super.onCreate();
        //Prefs lib

//        AudienceNetworkAds.initialize(this);
        MobileAds.initialize(
                this,
                new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(InitializationStatus initializationStatus) {}
                });

        appOpenManager = new AppOpenManager(this);

    }
}
