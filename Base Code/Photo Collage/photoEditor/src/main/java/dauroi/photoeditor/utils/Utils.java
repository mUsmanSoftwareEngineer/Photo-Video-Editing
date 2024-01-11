package dauroi.photoeditor.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.widget.ImageView;

import dauroi.photoeditor.PhotoEditorApp;
import dauroi.photoeditor.api.FileService;
import dauroi.photoeditor.config.ALog;


public class Utils {
    private static final String TAG = Utils.class.getSimpleName();
    public static final String BIG_D_FOLDER =/* Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString()
            .concat("/Android/data/collagestudio.photocollage.collagemaker");*/
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString().concat("/").concat("PhotoCollage");
    public static final String TEMP_FOLDER = BIG_D_FOLDER.concat("/Temp");
    public static final String FILE_FOLDER = BIG_D_FOLDER.concat("/files");
    public static final String ROOT_EDITED_IMAGE_FOLDER = FILE_FOLDER.concat("/edited");
    public static final String EDITED_IMAGE_FOLDER = ROOT_EDITED_IMAGE_FOLDER.concat("/images");
    public static final String EDITED_IMAGE_THUMBNAIL_FOLDER = ROOT_EDITED_IMAGE_FOLDER.concat("/thumbnails");
    public static final String CROP_FOLDER = FILE_FOLDER.concat("/crop");
    public static final String FRAME_FOLDER = FILE_FOLDER.concat("/frame");
    public static final String FILTER_FOLDER = FILE_FOLDER.concat("/filter");
    public static final String BACKGROUND_FOLDER = FILE_FOLDER.concat("/background");
    public static final String STICKER_FOLDER = FILE_FOLDER.concat("/sticker");

    private Utils() {
    }

    public static File copyFileFromAsset(Context context, final String outFolder, final String assetFilePath, boolean override) {
        try {
            File file = new File(assetFilePath);
            final String outFilePath = outFolder.concat("/").concat(file.getName());
            file = new File(outFilePath);
            if (!file.exists() || file.length() == 0 || override) {
                InputStream is = context.getAssets().open(assetFilePath);
                file.getParentFile().mkdirs();
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buff = new byte[2048];
                int len = -1;
                while ((len = is.read(buff)) != -1) {
                    fos.write(buff, 0, len);
                }
                fos.flush();
                fos.close();
                is.close();
            }
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }



    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void displayImageAsync(final String cloudPath, final List<ImageView> imageViews,
                                         final boolean offline) {
        try {
            ALog.d(TAG, "displayImageAsync, cloudPath=" + cloudPath);
            String name = SecurityUtils.sha256s(cloudPath);
            final File file = new File(FILE_FOLDER, name);

            AsyncTask<Void, Void, File> task = new AsyncTask<Void, Void, File>() {
                Bitmap bitmap;

                @Override
                protected File doInBackground(Void... params) {
                    long time = System.currentTimeMillis();
                    if (file.exists()) {
                        bitmap = ImageDecoder.decodeFileToBitmap(file.getAbsolutePath());
                        ALog.d(TAG, "displayImageAsync, decode taken time=" + (System.currentTimeMillis() - time));
                        publishProgress();
                    }
                    time = System.currentTimeMillis();
                    try {
                        if (!offline && NetworkUtils.checkNetworkAvailable(PhotoEditorApp.getAppContext())) {
                            File file = FileService.downloadFile(ProfileCache.getToken(PhotoEditorApp.getAppContext()),
                                    cloudPath, null);
                            ALog.d(TAG, "displayImageAsync, download time=" + (System.currentTimeMillis() - time));
                            return file;
                        } else {
                            return null;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    return null;
                }

                @Override
                protected void onProgressUpdate(Void... values) {
                    super.onProgressUpdate(values);
                    ALog.d(TAG, "onProgressUpdate");
                    if (bitmap != null && imageViews != null && imageViews.size() > 0) {
                        for (ImageView imageView : imageViews)
                            imageView.setImageBitmap(bitmap);
                    }
                    ALog.d(TAG, "onProgressUpdate, end");
                }

                @Override
                protected void onPostExecute(File file) {
                    super.onPostExecute(file);
                    long time = System.currentTimeMillis();
                    if (file != null && imageViews != null && imageViews.size() > 0) {
                        Bitmap bitmap = ImageDecoder.decodeFileToBitmap(file.getAbsolutePath());
                        for (ImageView imageView : imageViews)
                            imageView.setImageBitmap(bitmap);
                    }

                    ALog.d(TAG, "onPostExecute, display image=" + (System.currentTimeMillis() - time));
                }
            };

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                task.execute();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }






}
