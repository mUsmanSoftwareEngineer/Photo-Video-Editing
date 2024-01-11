package collagestudio.photocollage.collagemaker.utils;

import collagestudio.photocollage.collagemaker.BuildConfig;
import collagestudio.photocollage.collagemaker.activity.PhotoCollageApp;


public class DebugOptions {
    public static final boolean ENABLE_LOG = BuildConfig.DEBUG;

    public static final boolean ENABLE_DEBUG = BuildConfig.DEBUG;

    public static final boolean ENABLE_FOR_DEV = false;

    public static boolean isProVersion() {
        return dauroi.photoeditor.config.DebugOptions.isProVersion(PhotoCollageApp.getAppContext());
    }
}
