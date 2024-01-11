package dauroi.photoeditor.actions;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.PermissionRequest;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;

import java.io.File;
import java.io.FileOutputStream;

import dauroi.photoeditor.listener.OnDoneActionsClickListener;
import dauroi.photoeditor.ui.activity.ImageProcessingActivity;
import dauroi.photoeditor.utils.PhotoUtils;
import dauroi.photoeditor.utils.Utils;

public abstract class BaseAction implements OnDoneActionsClickListener {
    protected ImageProcessingActivity mActivity;
    protected View mRootActionView;
    private boolean mAttached = false;

    public BaseAction(ImageProcessingActivity activity) {
        mActivity = activity;
        onInit();
        mRootActionView = inflateMenuView();
    }

    private void logToFacebook() {
        Bundle parameters = new Bundle();
        parameters.putString("actionName", getActionName());
    }

    protected void onInit() {

    }

    public void attach() {
//        logToFacebook();
        mActivity.setDoneActionsClickListener(this);
        if (mRootActionView != null) {
            BaseAction action = mActivity.getCurrentAction();
            if (action != null) {
                action.onDetach();
                action.mAttached = false;
            }

            mActivity.attachBottomMenu(mRootActionView);
            mActivity.setCurrentAction(this);
            mAttached = true;
        }
    }

    public void onDetach() {

    }

    public boolean isAttached() {
        return mAttached;
    }

    public void done() {
        AsyncTask<Void, Void, File> task = new AsyncTask<Void, Void, File>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mActivity.hideAllMenus();
                mActivity.showProgress(true);
            }

            @Override
            protected File doInBackground(Void... params) {
                //Delete old file
                if (mActivity.isEditingImage() && mActivity.getEditingImagePath() != null && mActivity.getEditingImagePath().length() > 0) {
                    String path = mActivity.getEditingImagePath();
                    File oldFile = new File(path);
                    oldFile.delete();
                    File oldWhiteFile = new File(path.substring(0, path.length() - 4).concat(PhotoUtils.EDITED_WHITE_IMAGE_SUFFIX));
                    oldWhiteFile.delete();
                    File thumbnail = new File(Utils.EDITED_IMAGE_THUMBNAIL_FOLDER, oldFile.getName());
                    thumbnail.delete();
                }
                long time = System.currentTimeMillis();

       /*         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    try {
                        File out = new File(
                                Utils.EDITED_IMAGE_FOLDER.concat("/edited_image_") + time + ".png");
                        FileOutputStream fOut = new FileOutputStream(out);
                        mActivity.getImage().compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                        fOut.flush();
                        fOut.close();
                        MediaStore.Images.Media.insertImage(mActivity.getContentResolver(),
                                mActivity.getImage(), "Design", null);
                        return out;

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("message1:", e.getMessage());
                    }
                } else {*/
                    File out = new File(
                            Utils.EDITED_IMAGE_FOLDER.concat("/edited_image_") + time + ".png");
                    File parentFile = out.getParentFile();
                    if (!parentFile.exists()) {
                        parentFile.mkdirs();
                    }

                    try {
                        mActivity.getImage().compress(CompressFormat.PNG, 100, new FileOutputStream(out));
                        mActivity.getImage().recycle();
                /*        MediaStore.Images.Media.insertImage(mActivity.getContentResolver(),
                                String.valueOf(out), "Design", null);*/
                        System.gc();
                        return out;
                    } catch (Exception e) {
                        Log.d("message:", e.getMessage());
                        e.printStackTrace();
                        FirebaseCrash.report(e);
                    }
                Log.d("check","inside bg");

//                }

                return null;
            }

            @Override
            protected void onPostExecute(File result) {
                super.onPostExecute(result);
                try {
                    Log.d("checkddd",result.getAbsolutePath());
                    if (result != null) {
                        Intent data = new Intent();
                        data.setData(Uri.fromFile(result));
                        data.putExtra(ImageProcessingActivity.EXTRA_RETURN_EDITED_IMAGE_PATH, result.getAbsolutePath());
                        data.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        mActivity.setResult(Activity.RESULT_OK, data);
                        mActivity.finish();
                    }
                } catch (Exception e) {
                    Log.d("messaged:", e.getMessage());

                }

            }
        };

        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public abstract void apply(final boolean finish);

    /**
     * Create root action view and find child widgets
     */
    public abstract View inflateMenuView();

    public abstract String getActionName();

    /**
     * Should be call to save instance state
     *
     * @param bundle
     */
    public void saveInstanceState(Bundle bundle) {
        bundle.putBoolean("dauroi.photoeditor.actions.".concat(getActionName()).concat(".mAttached"), mAttached);
    }

    /**
     * Should be call before calling attach() method if has saved instance
     * state.
     *
     * @param bundle
     */
    public void restoreInstanceState(Bundle bundle) {
        mAttached = bundle.getBoolean("dauroi.photoeditor.actions.".concat(getActionName()).concat(".mAttached"), mAttached);
    }

    public void onActivityResume() {

    }

    public void onActivityPause() {

    }

    public void onActivityDestroy() {

    }

    @Override
    public void onDoneButtonClick() {
        apply(true);
    }

    @Override
    public void onApplyButtonClick() {
        apply(false);
    }

    public void onClicked() {
        if (mActivity.getAdCreator() != null)
            mActivity.getAdCreator().onClicked();
    }
}
