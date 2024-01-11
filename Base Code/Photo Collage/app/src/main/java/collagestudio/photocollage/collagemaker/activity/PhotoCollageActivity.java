package collagestudio.photocollage.collagemaker.activity;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;

import android.view.ViewGroup;
import android.widget.Toast;

import collagestudio.photocollage.collagemaker.R;
import collagestudio.photocollage.collagemaker.listener.OnChooseColorListener;
import collagestudio.photocollage.collagemaker.listener.OnShareImageListener;
import collagestudio.photocollage.collagemaker.fragment.BaseFragment;
import collagestudio.photocollage.collagemaker.fragment.PhotoCollageFragment;

import java.util.ArrayList;


public class PhotoCollageActivity extends AdsFragmentActivity implements
        OnShareImageListener, OnChooseColorListener {
    public static final int PHOTO_TYPE = 1;
    public static final int FRAME_TYPE = 2;
    public static final String EXTRA_CREATED_METHOD_TYPE = "methodType";
    private static final int PERMISSION_REQUEST_CODE = 1;
    private int mSelectedColor = Color.GREEN;
    private boolean mClickedShareButton = false;
    private ArrayList<String> permissions;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected;
    private static final int ALL_PERMISSIONS_RESULT = 4545344;

    @Override
    protected void preCreateAdsHelper() {
        mLoadedData = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photocollage);
//        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
//        this.setSupportActionBar(toolbar);
//        ActionBar actionBar = this.getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            //actionBar.setTitle(R.string.app_name);
//            actionBar.setLogo(R.drawable.logo);
//        }

        permissions= new ArrayList<>();
        permissionsRejected= new ArrayList<>();
        permissionsToRequest= new ArrayList<>();
        permissions.add(Manifest.permission.CAMERA);
        permissions.add(Manifest.permission.WAKE_LOCK);
        permissions.add(Manifest.permission.INTERNET);
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.VIBRATE);


        permissionsToRequest = permissionsToRequest(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.
                        toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }
        }
        final ViewGroup adsLayout = (ViewGroup) findViewById(R.id.adsLayout);
        if (getAdsHelper() != null)
            getAdsHelper().addAdsBannerView(adsLayout);
        if (savedInstanceState == null) {
            BaseFragment fragment = null;
            int type = getIntent().getIntExtra(EXTRA_CREATED_METHOD_TYPE, PHOTO_TYPE);
            if (type == PHOTO_TYPE) {
                fragment = new PhotoCollageFragment();
            }
//            } else {
//                fragment = new SelectFrameFragment();
//            }

            getFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, fragment)
                    .commit();
        } else {
            mClickedShareButton = savedInstanceState.getBoolean("mClickedShareButton", false);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("mClickedShareButton", mClickedShareButton);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mClickedShareButton) {
            mClickedShareButton = false;
        /*    if (getAdsHelper() != null) {
                getAdsHelper().showInterstitialAds();
            }*/
        }
    }

    @Override
    public void onBackPressed() {
//        BaseFragment fragment = (BaseFragment) getVisibleFragment();
//        if (fragment instanceof PhotoCollageFragment || fragment instanceof SelectFrameFragment) {
//            super.onBackPressed();
//        } else {
//            FragmentManager fragmentManager = getFragmentManager();
//            fragmentManager.popBackStack();
//        }
        startActivity( new Intent(PhotoCollageActivity.this,MainActivity.class));
        finish();
    }


    @Override
    public void onShareImage(String imagePath) {
        mClickedShareButton = true;
    }

    public Fragment getVisibleFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        return fragmentManager.findFragmentById(R.id.frame_container);
    }

    @Override
    public void onShareFrame(String imagePath) {
        
        Toast.makeText(this, "Shared image frame: " + imagePath,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setSelectedColor(int color) {
        mSelectedColor = color;
    }

    @Override
    public int getSelectedColor() {
        return mSelectedColor;
    }




    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }
    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perm : permissionsToRequest) {
                    if (!hasPermission(perm)) {
                        permissionsRejected.add(perm);
                    }
                }


                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            new android.app.AlertDialog.Builder(PhotoCollageActivity.this).
                                    setMessage("These permissions are mandatory to access your gallary photos. You need to allow them to edit.").
                                    setPositiveButton("Got it!", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.
                                                        toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    })
                                    .setCancelable(false).create().show();

                            return;
                        }
                    }

                }

                break;
        }
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}

