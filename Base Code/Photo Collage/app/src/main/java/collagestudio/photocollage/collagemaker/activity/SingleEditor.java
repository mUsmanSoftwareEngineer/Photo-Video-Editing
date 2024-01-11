package collagestudio.photocollage.collagemaker.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import collagestudio.photocollage.collagemaker.R;
import collagestudio.photocollage.collagemaker.utils.ImageUtils;
import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import dauroi.photoeditor.config.ALog;
import dauroi.photoeditor.ui.activity.ImageProcessingActivity;
import dauroi.photoeditor.utils.DateTimeUtils;
import dauroi.photoeditor.utils.PhotoUtils;

import static collagestudio.photocollage.collagemaker.activity.BasePhotoActivity.CAPTURE_IMAGE_REQUEST_CODE;
import static collagestudio.photocollage.collagemaker.activity.BasePhotoActivity.PICK_IMAGE_REQUEST_CODE;
import static collagestudio.photocollage.collagemaker.activity.BasePhotoActivity.REQUEST_EDIT_IMAGE;
import static collagestudio.photocollage.collagemaker.activity.BasePhotoActivity.REQUEST_PHOTO_EDITOR_CODE;

public class SingleEditor extends AdsFragmentActivity {
    protected static final String CAPTURE_TITLE = "capture.jpg";
    ImageView img;
    Uri imgUr=null;
    private ArrayList<String> permissions;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected;
    private static final int ALL_PERMISSIONS_RESULT = 4545344;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_editor);
        img = findViewById(R.id.single_img);

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
        String key = getIntent().getExtras().get("key").toString();
        if (key!=null){
            if (key.equals("camera")){
                getImageFromCamera();
            }else if (key.equals("gallary")){
                pickImageFromGallery();
            }
        }
        addAdsView(R.id.adsLayout);
        initPhotoError();
    }

    public void getImageFromCamera() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, getImageUri());
            startActivityForResult(intent, CAPTURE_IMAGE_REQUEST_CODE);
        } catch (Exception ex) {
            ex.printStackTrace();
//            Toast.makeText(getApplicationContext(), getString(R.string.app_not_found), Toast.LENGTH_SHORT).show();
        }
    }
    protected Uri getImageUri() {
        // Store image in dcim
        File file = new File(Environment.getExternalStorageDirectory()
                + "/DCIM", CAPTURE_TITLE);
        Uri imgUri = Uri.fromFile(file);

        return imgUri;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_PHOTO_EDITOR_CODE:
                    // output image path
                    Uri uri = data.getData();
                    imgUr = uri;
                    Glide.with(SingleEditor.this).load(uri).into(img);
                    break;
                case PICK_IMAGE_REQUEST_CODE:
                    if (data != null && data.getData() != null) {
                        uri = data.getData();
                        startPhotoEditor(uri, false);
                    }
                    break;
                case CAPTURE_IMAGE_REQUEST_CODE:
                    uri = getImageUri();
                    if (uri != null) {
                        startPhotoEditor(uri, true);
                    }
                    break;
                case REQUEST_EDIT_IMAGE:
                    uri = data.getData();
                    //resultEditImage(uri);
                    break;


            }

        }
    }
    private void startPhotoEditor(Uri imageUri, boolean capturedFromCamera) {

        ALog.d("BaseFragment", "startPhotoEditor, imageUri=" + imageUri + ", capturedFromCamera=" + capturedFromCamera);
        Intent newIntent = new Intent(SingleEditor.this, ImageProcessingActivity.class);
        newIntent.putExtra(ImageProcessingActivity.IMAGE_URI_KEY, imageUri);
        if (capturedFromCamera) {
            newIntent.putExtra(ImageProcessingActivity.ROTATION_KEY, 90);
        }
        startActivityForResult(newIntent, REQUEST_PHOTO_EDITOR_CODE);
    }
    public void clickShareView() throws ExecutionException, InterruptedException {

        Activity  mActivity = SingleEditor.this;
        img.setDrawingCacheEnabled(true);
//        BitmapDrawable drawable = (BitmapDrawable) img.getDrawable();
        if (imgUr != null){

            // Bitmap bitmap = drawable.getBitmap();

            final Bitmap image = img.getDrawingCache();// Glide.with(SingleEditor.this).load(imgUr).asBitmap().into(500,500).get();//mPhotoView.getImage(ImageUtils.calculateOutputScaleFactor(mPhotoView.getWidth(), mPhotoView.getHeight()));

            AsyncTask<Void, Void, File> task = new AsyncTask<Void, Void, File>() {
                Dialog dialog;
                String errMsg;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    dialog = ProgressDialog.show(mActivity, getString(R.string.app_name), getString(R.string.creating));
                }

                @Override
                protected File doInBackground(Void... params) {
                    try {
                        String fileName = DateTimeUtils.getCurrentDateTime().replaceAll(":", "-").concat(".png");
                        File collageFolder = new File(ImageUtils.OUTPUT_COLLAGE_FOLDER);
//                        File collageFolder = new File(String.valueOf(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DCIM)));
                        if (!collageFolder.exists()) {
                            collageFolder.mkdirs();
                        }
                        File photoFile = new File(collageFolder, fileName);
                        image.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(photoFile));

                        PhotoUtils.addImageToGallery(photoFile.getAbsolutePath(), mActivity);
                        return photoFile;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        errMsg = ex.getMessage();
                    } catch (OutOfMemoryError err) {
                        err.printStackTrace();
                        errMsg = err.getMessage();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(File file) {
                    super.onPostExecute(file);
                    dialog.dismiss();
                    if (file != null) {
//                    if (mShareImageListener != null) {
//                        mShareImageListener.onShareImage(file.getAbsolutePath());
//                    }

                        Toast.makeText(mActivity, "Saved in Gallary", Toast.LENGTH_SHORT).show();
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("image/png");
                        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                        share.putExtra(Intent.EXTRA_TEXT,"Please download this amazing application to make your collage " + "https://play.google.com/store/apps/details?id=" + mActivity.getPackageName());

                        startActivity(Intent.createChooser(share, getString(R.string.photo_editor_share_image)));
                        img.setDrawingCacheEnabled(false);

                    } else if (errMsg != null) {
//                        Toast.makeText(mActivity, errMsg, Toast.LENGTH_LONG).show();
                    }

                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "share/create_freely");
                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "create_freely");
                }
            };
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

    }

    public void saveImg(View view) {
        try {
            clickShareView();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initPhotoError(){
//        / / android 7.0 system to solve the problem of taking pictures
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }

    public void retry(View view) {
        String key = getIntent().getExtras().get("key").toString();
        if (key!=null){
            if (key.equals("camera")){
                getImageFromCamera();
            }else if (key.equals("gallary")){
                pickImageFromGallery();
            }
        }
    }
    public void pickImageFromGallery() {
        Toast.makeText(this, "Select only gallary or local images", Toast.LENGTH_SHORT).show();
        try {
            List<Intent> targets = new ArrayList<Intent>();
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            photoPickerIntent.setType("image/*");

            startActivityForResult(photoPickerIntent, PICK_IMAGE_REQUEST_CODE);

        } catch (Exception ex) {
            ex.printStackTrace();
            try {

                Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                photoPickerIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, PICK_IMAGE_REQUEST_CODE);
            } catch (Exception e) {
                e.printStackTrace();
//                Toast.makeText(getApplicationContext(), getString(R.string.app_not_found), Toast.LENGTH_SHORT).show();
            }
        }
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
                            new AlertDialog.Builder(SingleEditor.this).
                                    setMessage("These permissions are mandatory to access your gallary photos. You need to allow them to edit.").
                                    setPositiveButton("Got it!", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.
                                                        toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    }) .setCancelable(false).create().show();

                            return;
                        }
                    }

                }

                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SingleEditor.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity( intent);
        finish();
    }
}
