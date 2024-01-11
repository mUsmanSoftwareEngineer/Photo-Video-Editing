package collagestudio.photocollage.collagemaker.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.firebase.crash.FirebaseCrash;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;

import collagestudio.photocollage.collagemaker.R;
import collagestudio.photocollage.collagemaker.cross_promotion.MoreAppsActivity;
import collagestudio.photocollage.collagemaker.fragment.MainFragment;
import collagestudio.photocollage.collagemaker.fragment.StoreFragment;
import collagestudio.photocollage.collagemaker.google_admob.NativeTemplateStyle;
import collagestudio.photocollage.collagemaker.google_admob.TemplateView;
import collagestudio.photocollage.collagemaker.utils.ALog;
import collagestudio.photocollage.collagemaker.utils.ResultContainer;
import dauroi.photoeditor.api.response.StoreItem;
import dauroi.photoeditor.database.DatabaseManager;
import dauroi.photoeditor.utils.StoreUtils;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

public class MainActivity extends AdsFragmentActivity {
    public static final String RATE_APP_PREF_NAME = "rateAppPref";
    public static final String RATED_APP_KEY = "ratedApp";
    public static final String OPEN_APP_COUNT_KEY = "openAppCount";
    final static int REQUEST_CODE = 333;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int ALL_PERMISSIONS_RESULT = 4545344;
    boolean doubleBackToExitPressedOnce = false;
    AlertDialog.Builder builder;
//    TemplateView templateViewmain;
    UnifiedNativeAd ad;
    FrameLayout mainlayout;
    Dialog dialog;
    private String mTitle;
    private ViewGroup mAdLayout;
    private ArrayList<String> permissions;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected;
    private PendingIntent pendingIntent;
    private final int promotionAdCounter = 0;
    private final int promotionAdCounterLimit = 3;
    AdLoader.Builder buildermain;
    private LinearLayout ads_layout;
    private UnifiedNativeAd nativeAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dialog = new Dialog(MainActivity.this);
        ads_layout = findViewById(R.id.download_completed_adHolder);
        refreshAd();
/*
        if (permission()) {
        } else {
            RequestPermission_Dialog();
        }*/
        mainlayout = findViewById(R.id.frame_container);
   /*     Intent alarmIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent, 0);
//        setting alarm here
        myAlarm();*/
//        AdLoader.Builder builder = new AdLoader.Builder(ContentsActivity.this, "ca-app-pub-3940256099942544/2247696110");
        AdLoader.Builder builderdialogue = new AdLoader.Builder(MainActivity.this, this.getResources().getString(R.string.native_ad_unit));
        builderdialogue.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
//                Toast.makeText(ContentsActivity.this, loadAdError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        builderdialogue.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                ad = unifiedNativeAd;
//                Toast.makeText(ContentsActivity.this, "Ad loaded", Toast.LENGTH_SHORT).show();
                if (isDestroyed()) {
                    ad.destroy();
                    return;
                }
            }

        });
        AdLoader adLoader = builderdialogue.build();
        adLoader.loadAd(new AdRequest.Builder().build());

//        templateViewmain = findViewById(R.id.my_template);
 /*        buildermain = new AdLoader.Builder(
                MainActivity.this, this.getResources().getString(R.string.native_ad_unit));

        buildermain.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                templateViewmain.setNativeAd(unifiedNativeAd);

                templateViewmain.setVisibility(View.VISIBLE);
                if (isDestroyed()) {
                    unifiedNativeAd.destroy();
                    return;
                }
            }
        });*/

      /*  final AdLoader adLoader2 = buildermain.build();
        adLoader2.loadAd(new AdRequest.Builder().build());*/

        if (!DatabaseManager.getInstance(this).isDbFileExisted()) {
            DatabaseManager.getInstance(this).createDb();
        } else {
            boolean isOpen = DatabaseManager.getInstance(this).openDb();
            ALog.d("MainActivity", "onCreate, database isOpen=" + isOpen);
        }
/*

        mAdLayout = (ViewGroup) findViewById(R.id.adsLayout);
        if (getAdsHelper() != null)
            getAdsHelper().addAdsBannerView(mAdLayout);
*/


        if (savedInstanceState == null) {
            ResultContainer.getInstance().clearAll();
            getFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, new MainFragment(), "MainFragment")
                    .commit();
        }

        try {
            StoreUtils.redownloadItems();
        } catch (Exception ex) {
            ex.printStackTrace();
            FirebaseCrash.report(ex);
        }

        if (getIntent().getExtras() != null) {
            String itemType = getIntent().getExtras().getString("type");
            if (itemType != null && itemType.length() > 0) {
                itemType = itemType.trim();
                if (itemType.equalsIgnoreCase("update")) {
                    try {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse("market://details?id=" + getPackageName()));
                        startActivity(i);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Toast.makeText(MainActivity.this, getString(R.string.app_not_found), Toast.LENGTH_SHORT).show();
                    }
                } else if (itemType.equalsIgnoreCase("ad")) {
                    ALog.d("MainActivity", "show ad");
                } else {
                    try {
                        StoreFragment fragment = new StoreFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(StoreItem.EXTRA_ITEM_TYPE_KEY, itemType);
                        fragment.setArguments(bundle);
                        getFragmentManager().beginTransaction()
                                .replace(R.id.frame_container, fragment, "StoreFragment")
                                .commit();
                        mLoadedData = true;
                        if (getAdsHelper() != null)
                            getAdsHelper().showInterstitialAds();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        FirebaseCrash.report(ex);
                    }
                }
            }
        }

    }
    private void refreshAd() {

        buildermain = new AdLoader.Builder(this, getString(R.string.native_ad_unit));

        buildermain.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                // You must call destroy on old ads when you are done with them,
                // otherwise you will have a memory leak.

                ads_layout.setVisibility(View.VISIBLE);

                if (nativeAd != null) {
                    nativeAd.destroy();
                }

                nativeAd = unifiedNativeAd;

                NativeTemplateStyle styles = new
                        NativeTemplateStyle.Builder().build();
                collagestudio.photocollage.collagemaker.google_admob.TemplateView adView = findViewById(R.id.adview);
                adView.setVisibility(View.VISIBLE);
                adView.setStyles(styles);
                adView.setNativeAd(nativeAd);
            }
        });

        VideoOptions videoOptions = new VideoOptions.Builder()
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();

        buildermain.withNativeAdOptions(adOptions);

        AdLoader adLoader = buildermain.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.w("aaasssd", "error code" + errorCode);
            }
        }).build();

        adLoader.loadAd(new AdRequest.Builder()
                .build());
    }



    private void customCheckPermissions() {
        String[] permissions = {Manifest.permission.CAMERA, WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET,
                READ_EXTERNAL_STORAGE};
        String rationale = "Please provide location permission so that you can enjoy all the features of app";
        Permissions.Options options = new Permissions.Options()
                .setRationaleDialogTitle("Info")
                .setSettingsDialogTitle("Warning");
        Permissions.check(this, permissions, rationale, options, new com.nabinbhandari.android.permissions.PermissionHandler() {
            @Override
            public boolean onBlocked(Context context, ArrayList<String> blockedList) {
                Log.d("5757", "onBlocked: ");
                return super.onBlocked(context, blockedList);
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                Log.d("5757", "onDenied: ");
                super.onDenied(context, deniedPermissions);
                customCheckPermissions();
            }

            @Override
            public void onGranted() {
                Log.d("5757", "onGranted: ");
            }

            @Override
            public void onJustBlocked(Context context, ArrayList<String> justBlockedList, ArrayList<String> deniedPermissions) {
                Log.d("5757", "onJustBlocked: ");
                super.onJustBlocked(context, justBlockedList, deniedPermissions);
            }
        });
    }

//    private void askPermissions() {
//        permissions= new ArrayList<>();
//        permissionsRejected= new ArrayList<>();
//        permissionsToRequest= new ArrayList<>();
//        permissions.add(Manifest.permission.INTERNET);
//        permissions.add(Manifest.permission.CAMERA);
//        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
//        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//
//        permissionsToRequest = permissionsToRequest(permissions);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (permissionsToRequest.size() > 0) {
//                requestPermissions(permissionsToRequest.
//                        toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
//            }
//        }
//    }

    //    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
//        ArrayList<String> result = new ArrayList<>();
//
//        for (String perm : wantedPermissions) {
//            if (!hasPermission(perm)) {
//                result.add(perm);
//
//            }
//        }
//
//        return result;
//    }
//    private boolean hasPermission(String permission) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED){
//                PermissionHandler.setShouldShowStatus(this, permission,false);
//                return true;
//            }else {
//                PermissionHandler.setShouldShowStatus(this, permission,true);
//                if (PermissionHandler.neverAskAgainSelected(this, permission)) {
//                    displayNeverAskAgainDialog();
//                }
//                return false;
//            }
//
//
//        }
//
//        return true;
//    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        customCheckPermissions();
        super.onResume();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean storage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean read = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (storage && read) {
                        //next activity
                    } else {
                        //show msg kai permission allow nahi havai
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2000) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    //msg show karo
                    //move to next activity
                } else {

                }
            }
        }
        if (requestCode == 1552 && resultCode == RESULT_CANCELED) {
            disconnectAlert();

        }
    }

    public void RequestPermission_Dialog() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
            /*    ACTION_MANAGE_WRITE_SETTINGS
                        ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION*/
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                startActivityForResult(intent, 2000);
            } catch (Exception e) {
                Intent obj = new Intent();
                obj.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(obj, 2000);
            }
        } else {
            customCheckPermissions();
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
    }

    public boolean permission() {
        if (SDK_INT >= Build.VERSION_CODES.R) { // R is Android 11
            return Environment.isExternalStorageManager();
        } else {
            int write = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
            int read = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);

            return write == PackageManager.PERMISSION_GRANTED
                    && read == PackageManager.PERMISSION_GRANTED;
        }
    }

    @Override
    public void onBackPressed() {
        startActivityForResult(new Intent(MainActivity.this, MoreAppsActivity.class), 1552);

//        disconnectAlert();
   /*     if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);*/
    }


/*    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1552 && resultCode == RESULT_CANCELED) {
            disconnectAlert();

        }

    }*/

    protected void disconnectAlert() {
//        Toast.makeText(this, "inside", Toast.LENGTH_SHORT).show();
//        work here   ------------------------------------->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        try {
//            templateViewmain.setVisibility(View.GONE);
            ads_layout.setVisibility(View.GONE);
            mainlayout.setVisibility(View.GONE);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setDimAmount(0);

            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;

//            topbarwala.setVisibility(View.VISIBLE);
          /*  templateViewmain.setVisibility(View.GONE);
            uploadDownloadLAyout.setVisibility(View.GONE);*/
            dialog.setContentView(R.layout.exitdialogue);
            TextView Yes, no;
            TemplateView templateView = dialog.findViewById(R.id.adview);
            if (this.ad == null) {
//                Toast.makeText(this, "true", Toast.LENGTH_SHORT).show();
                templateView.setVisibility(View.GONE);
            } else {
                templateView.setVisibility(View.VISIBLE);
                templateView.setNativeAd(this.ad);
            }
            Yes = dialog.findViewById(R.id.cancel);
            no = dialog.findViewById(R.id.close);


            // if button is clicked, close the custom dialog

            Yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    mainlayout.setVisibility(View.VISIBLE);
                    buildermain.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                        @Override
                        public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                            ads_layout.setVisibility(View.VISIBLE);

                            if (nativeAd != null) {
                                nativeAd.destroy();
                            }

                            nativeAd = unifiedNativeAd;

                            NativeTemplateStyle styles = new
                                    NativeTemplateStyle.Builder().build();
                            collagestudio.photocollage.collagemaker.google_admob.TemplateView adView = findViewById(R.id.adview);
                            adView.setVisibility(View.VISIBLE);
                            adView.setStyles(styles);
                            adView.setNativeAd(nativeAd);

                        }
                    });

                    final AdLoader adLoader2 = buildermain.build();
                    adLoader2.loadAd(new AdRequest.Builder().build());
//                    loadinterstitial();
                    try {
                        if (getAdsHelper() != null)
                            getAdsHelper().showInterstitialAds();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        FirebaseCrash.report(ex);
                    }
                }
            });

            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    mainlayout.setVisibility(View.GONE);
                    ads_layout.setVisibility(View.GONE);

                    finishAffinity();

                }
            });
            dialog.show();

        } catch (Exception e) {
//            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d("catched", e.getMessage());
        }
    }


//

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(MainActivity.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case ALL_PERMISSIONS_RESULT:
//                for (String perm : permissionsToRequest) {
//                    if (!hasPermission(perm)) {
//                        permissionsRejected.add(perm);
//                    }
//                }
//
//                if (permissionsRejected.size() > 0) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
//                            new AlertDialog.Builder(MainActivity.this).
//                                    setMessage("These permissions are mandatory to access your gallary photos. You need to allow them to edit.").
//                                    setPositiveButton("Got it!", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialogInterface, int i) {
//                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                                                requestPermissions(permissionsRejected.
//                                                        toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
//                                            }
//                                        }
//                                    }) .setCancelable(false).create().show();
//
//                            return;
//                        }
//                    }
//
//                }
//
//                break;
//        }
//    }


    public void openCamera(View view) {
        Intent intent = new Intent(MainActivity.this, SingleEditor.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("key", "camera");
        startActivity(intent);
        finish();
    }

    public void openGallary(View view) {
        Intent intent = new Intent(MainActivity.this, SingleEditor.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("key", "gallary");
        startActivity(intent);
        finish();
    }
//    private void displayNeverAskAgainDialog() {
//
//         builder = new AlertDialog.Builder(this);
//        builder.setMessage("We need to acess camera and gallary for performing necessary task. Please permit the permission through "
//                + "Settings screen.\n\nSelect Permissions -> Enable permission");
//        builder.setCancelable(false);
//        builder.setPositiveButton("Permit Manually", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                Intent intent = new Intent();
//                intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                Uri uri = Uri.fromParts("package", getPackageName(), null);
//                intent.setData(uri);
//                dialog.dismiss();
//                startActivityForResult(intent,23);
//            }
//        });
//
//        builder.show();
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 23){
//            checkAndAskPermissions();
//        }
//    }

//    public void checkAndAskPermissions(){
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager
//                .PERMISSION_GRANTED) {
//            //You can show permission rationale if shouldShowRequestPermissionRationale() returns true.
//            //I will skip it for this demo
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                if (PermissionHandler.neverAskAgainSelected(this, Manifest.permission.CAMERA)) {
//                    displayNeverAskAgainDialog();
//                }else if (PermissionHandler.neverAskAgainSelected(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                    displayNeverAskAgainDialog();
//                }else if (PermissionHandler.neverAskAgainSelected(this, Manifest.permission.WAKE_LOCK)) {
//                    displayNeverAskAgainDialog();
//                }else if (PermissionHandler.neverAskAgainSelected(this, Manifest.permission.VIBRATE)) {
//                    displayNeverAskAgainDialog();
//                }else if (PermissionHandler.neverAskAgainSelected(this, Manifest.permission.INTERNET)) {
//                    displayNeverAskAgainDialog();
//                } else {
//                    askPermissions();
//                }
//
//            }
//
//        }
//    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        dialog.dismiss();
        ads_layout.setVisibility(View.GONE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        dialog.dismiss();
        ads_layout.setVisibility(View.GONE);

    }
}