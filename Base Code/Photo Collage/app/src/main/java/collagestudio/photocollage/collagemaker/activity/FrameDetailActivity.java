package collagestudio.photocollage.collagemaker.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import collagestudio.photocollage.collagemaker.R;
import collagestudio.photocollage.collagemaker.utils.Constant;
import collagestudio.photocollage.collagemaker.frame.FrameImageView;
import collagestudio.photocollage.collagemaker.frame.FramePhotoLayout;
import collagestudio.photocollage.collagemaker.model.TemplateItem;
import collagestudio.photocollage.collagemaker.utils.CustomFilters;
import collagestudio.photocollage.collagemaker.utils.ImageUtils;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;

import dauroi.photoeditor.PhotoEditorApp;
import dauroi.photoeditor.colorpicker.ColorPickerDialog;
import dauroi.photoeditor.utils.FileUtils;
import dauroi.photoeditor.utils.ImageDecoder;

import static collagestudio.photocollage.collagemaker.utils.CustomFilters.doBrightness;


public class FrameDetailActivity extends BaseTemplateDetailActivity implements FramePhotoLayout.PhotoTapListner,FramePhotoLayout.OnQuickActionClickListener, ColorPickerDialog.OnColorChangedListener {
    private static final int REQUEST_SELECT_PHOTO = 99;
    private static final float MAX_SPACE = ImageUtils.pxFromDp(PhotoEditorApp.getAppContext(), 30);
    private static final float MAX_CORNER = ImageUtils.pxFromDp(PhotoEditorApp.getAppContext(), 60);
    private static final float DEFAULT_SPACE = ImageUtils.pxFromDp(PhotoEditorApp.getAppContext(), 2);
    private static final float MAX_SPACE_PROGRESS = 300.0f;
    private static final float MAX_CORNER_PROGRESS = 200.0f;

    public static String action = null;
    public static FrameImageView mSelectedFrameImageView;
    private FramePhotoLayout mFramePhotoLayout;
    private ViewGroup mSpaceLayout;
    private SeekBar mSpaceBar;
    private SeekBar mCornerBar;

    Dialog dialog;
    static String actionValue = null;
    ImageView share;
    TextView toptoshow;

    private ImageView rotateBtn,framesBtn,frameBtn, aspectBtn, addText, addSticker, bgColorBtn, bgImgBtn, deleteBtn, cropBtn, filtersBtn,
            changeBtn,brightnessBtn,contrastBtn,temperatureBtn;
    TextView backBtn,saveBtn;
    private float mSpace = DEFAULT_SPACE;
    private float mCorner = 0;

    private int mBackgroundColor = Color.WHITE;
    private Bitmap mBackgroundImage;
    private Uri mBackgroundUri = null;
    private ColorPickerDialog mColorPickerDialog;

    private Bundle mSavedInstanceState;
    public static boolean isRecyclerShown = true;


    @Override
    protected boolean isShowingAllTemplates() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        {
//            System.loadLibrary("NativeImageProcessor");
//        }
        if (savedInstanceState != null) {
            mSpace = savedInstanceState.getFloat("mSpace");
            mCorner = savedInstanceState.getFloat("mCorner");
            mBackgroundColor = savedInstanceState.getInt("mBackgroundColor");
            mBackgroundUri = savedInstanceState.getParcelable("mBackgroundUri");
            mSavedInstanceState = savedInstanceState;
            if (mBackgroundUri != null)
                mBackgroundImage = ImageDecoder.decodeUriToBitmap(this, mBackgroundUri);
        }

         addAdsView(R.id.adsLayout);

        mAddImageDialog.findViewById(R.id.dividerTextView).setVisibility(View.VISIBLE);
        mAddImageDialog.findViewById(R.id.alterBackgroundView).setVisibility(View.VISIBLE);
        mAddImageDialog.findViewById(R.id.dividerBackgroundPhotoView).setVisibility(View.VISIBLE);
        mAddImageDialog.findViewById(R.id.alterBackgroundColorView).setVisibility(View.VISIBLE);
        mSpaceLayout = (ViewGroup) findViewById(R.id.spaceLayout);
        mSpaceBar = (SeekBar) findViewById(R.id.spaceBar);

        aspectBtn = findViewById(R.id.iv_aspect_ratio);
        addText = findViewById(R.id.iv_text);
        addSticker = findViewById(R.id.iv_sticker);
        bgColorBtn = findViewById(R.id.iv_bgcolor);
        bgImgBtn = findViewById(R.id.iv_bgimg);
        backBtn = findViewById(R.id.iv_back);
        saveBtn = findViewById(R.id.tv_save);
        rotateBtn = findViewById(R.id.iv_rotate);
        share = findViewById(R.id.share);
//        toptoshow = findViewById(R.id.texttoshow);

        filtersBtn = findViewById(R.id.iv_filters);
        cropBtn = findViewById(R.id.iv_crop);
        changeBtn = findViewById(R.id.iv_change);
        deleteBtn = findViewById(R.id.iv_delete);
        frameBtn = findViewById(R.id.iv_frame);
        brightnessBtn = findViewById(R.id.iv_brightness);
        contrastBtn = findViewById(R.id.iv_contrast);


        temperatureBtn = findViewById(R.id.iv_temperature);
        framesBtn = findViewById(R.id.iv_frames);
//        showMyDialog();
        framesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideShowRecyclerView();
            }
        });
        filtersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectedFrameImageView != null) {
                    action = "filters";
                    onEditActionClick(mSelectedFrameImageView);

                } else {
                    Toast.makeText(FrameDetailActivity.this, "Please select the image first!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cropBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectedFrameImageView != null) {
                    action = "crop";
                    onEditActionClick(mSelectedFrameImageView);

                } else {
                    Toast.makeText(FrameDetailActivity.this, "Please select the image first!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        rotateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectedFrameImageView != null) {
                    action = "rotate";
                    onEditActionClick(mSelectedFrameImageView);

                } else {
                    Toast.makeText(FrameDetailActivity.this, "Please select the image first!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        frameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectedFrameImageView != null) {
                    action = "frame";
                    onEditActionClick(mSelectedFrameImageView);

                } else {
                    Toast.makeText(FrameDetailActivity.this, "Please select the image first!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectedFrameImageView != null) {
                    onChangeActionClick(mSelectedFrameImageView);
                } else {
                    Toast.makeText(FrameDetailActivity.this, "Please select the image first!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectedFrameImageView != null) {
                    onDeleteActionClick(mSelectedFrameImageView);
                } else {
                    Toast.makeText(FrameDetailActivity.this, "Please select the image first!", Toast.LENGTH_SHORT).show();
                }
            }
        });
//        editBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (editorLayar.getVisibility() == View.INVISIBLE){
//                    editorLayar.setVisibility(View.VISIBLE);
//                   // hideShowRecyclerView(false);
//                    //isRecyclerShown = false;
//                }else {
//                    editorLayar.setVisibility(View.INVISIBLE);
//                }
//            }
//        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FrameDetailActivity.this, MainActivity.class));
                finish();
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveImage();
                if (getAdsHelper() != null) {
                    getAdsHelper().showInterstitialAds();
                }
                Animation animation = AnimationUtils.loadAnimation(FrameDetailActivity.this,R.anim.photo_editor_slice_in_right);
                share.setAnimation(animation);
//                toptoshow.setVisibility(View.GONE);
//                share.show();
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareImage();
//                share.hide();
//                toptoshow.setVisibility(View.VISIBLE);
//
            }
        });
        aspectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAsppectRatioDialoge();
            }
        });
        addText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTextButtonClick();

            }
        });
        addSticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStickerButtonClick();
            }
        });
        bgColorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackgroundColorButtonClick();
            }
        });
        bgImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackgroundPhotoButtonClick();
            }
        });
        temperatureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectedFrameImageView !=null){

//                    Bitmap newBitMap  =   applySaturationFilter(mSelectedFrameImageView.getImage(),2);
//                    mSelectedFrameImageView.setImage(newBitMap);
//                    mSelectedFrameImageView.invalidate();
                    new ApplyFilterBackground().execute(2);


                }else {
                    Toast.makeText(FrameDetailActivity.this, "Please select photo first!", Toast.LENGTH_SHORT).show();
                }

            }
        });
        brightnessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mSelectedFrameImageView !=null){

//                    Bitmap newBitMap  =   doBrightness(mSelectedFrameImageView.getImage(),15);
//                    mSelectedFrameImageView.setImage(newBitMap);
//                    mSelectedFrameImageView.invalidate();
                    new ApplyFilterBackground().execute(0);

               }else {
                   Toast.makeText(FrameDetailActivity.this, "Please select photo first!", Toast.LENGTH_SHORT).show();
               }

            }
        });

        contrastBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectedFrameImageView !=null){
//
//                    Bitmap newBitMap  =   applyContrast(mSelectedFrameImageView.getImage(),10);
//                    mSelectedFrameImageView.setImage(newBitMap);
//                    mSelectedFrameImageView.invalidate();
                    new ApplyFilterBackground().execute(1);

                }else {
                    Toast.makeText(FrameDetailActivity.this, "Please select photo first!", Toast.LENGTH_SHORT).show();
                }

            }
        });
        mSpaceBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSpace = MAX_SPACE * seekBar.getProgress() / MAX_SPACE_PROGRESS;
                if (mFramePhotoLayout != null)
                    mFramePhotoLayout.setSpace(mSpace, mCorner);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        mCornerBar = (SeekBar) findViewById(R.id.cornerBar);
        mCornerBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mCorner = MAX_CORNER * seekBar.getProgress() / MAX_CORNER_PROGRESS;
                if (mFramePhotoLayout != null)
                    mFramePhotoLayout.setSpace(mSpace, mCorner);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        boolean show = mPreferences.getBoolean(Constant.SHOW_GUIDE_CREATE_FRAME_KEY, true);
        if (show) {
            clickInfoView();
            mPreferences.edit().putBoolean(Constant.SHOW_GUIDE_CREATE_FRAME_KEY, false)
                    .commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putFloat("mSpace", mSpace);
        outState.putFloat("mCornerBar", mCorner);
        outState.putInt("mBackgroundColor", mBackgroundColor);
        outState.putParcelable("mBackgroundUri", mBackgroundUri);
        if (mFramePhotoLayout != null) {
            mFramePhotoLayout.saveInstanceState(outState);
        }
    }
/*    private void showMyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FrameDetailActivity.this);
        ViewGroup viewGroup = findViewById(R.id.content);
        View dialogView = LayoutInflater.from(FrameDetailActivity.this).inflate(R.layout.customview, viewGroup, false);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(true);
        TextView gotBtn = (TextView) dialogView.findViewById(R.id.hide_btn);
        gotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.hide();
            }
        });
        alertDialog.show();
    }*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.action_ratio).setVisible(true);
        return result;
    }

    @Override
    public void onBackgroundColorButtonClick() {
        if (mColorPickerDialog == null) {
            mColorPickerDialog = new ColorPickerDialog(this, mBackgroundColor);
            mColorPickerDialog.setOnColorChangedListener(this);
        }

        mColorPickerDialog.setOldColor(mBackgroundColor);
        if (!mColorPickerDialog.isShowing()) {
            mColorPickerDialog.show();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_frame_detail;
    }

    @Override
    public Bitmap createOutputImage() throws OutOfMemoryError {
        try {
            Bitmap template = mFramePhotoLayout.createImage();
            Bitmap result = Bitmap.createBitmap(template.getWidth(), template.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            if (mBackgroundImage != null && !mBackgroundImage.isRecycled()) {
                canvas.drawBitmap(mBackgroundImage, new Rect(0, 0, mBackgroundImage.getWidth(), mBackgroundImage.getHeight()),
                        new Rect(0, 0, result.getWidth(), result.getHeight()), paint);
            } else {
                canvas.drawColor(mBackgroundColor);
            }

            canvas.drawBitmap(template, 0, 0, paint);
            template.recycle();
            template = null;
            Bitmap stickers = mPhotoView.getImage(mOutputScale);
            canvas.drawBitmap(stickers, 0, 0, paint);
            stickers.recycle();
            stickers = null;
            System.gc();
            return result;
        } catch (OutOfMemoryError error) {
            throw error;
        }
    }

    @Override
    protected void buildLayout(TemplateItem item) {
        mFramePhotoLayout = new FramePhotoLayout(this, item.getPhotoItemList(), this);
        mFramePhotoLayout.setQuickActionClickListener(this);
        if (mBackgroundImage != null && !mBackgroundImage.isRecycled()) {
            if (Build.VERSION.SDK_INT >= 16)
                mContainerLayout.setBackground(new BitmapDrawable(getResources(), mBackgroundImage));
            else
                mContainerLayout.setBackgroundDrawable(new BitmapDrawable(getResources(), mBackgroundImage));
        } else {
            mContainerLayout.setBackgroundColor(mBackgroundColor);
        }

        int viewWidth = mContainerLayout.getWidth();
        int viewHeight = mContainerLayout.getHeight();
        if (mLayoutRatio == RATIO_SQUARE) {
            if (viewWidth > viewHeight) {
                viewWidth = viewHeight;
            } else {
                viewHeight = viewWidth;
            }
        } else if (mLayoutRatio == RATIO_GOLDEN) {
            final double goldenRatio = 1.61803398875;
            if (viewWidth <= viewHeight) {
                if (viewWidth * goldenRatio >= viewHeight) {
                    viewWidth = (int) (viewHeight / goldenRatio);
                } else {
                    viewHeight = (int) (viewWidth * goldenRatio);
                }
            } else if (viewHeight <= viewWidth) {
                if (viewHeight * goldenRatio >= viewWidth) {
                    viewHeight = (int) (viewWidth / goldenRatio);
                } else {
                    viewWidth = (int) (viewHeight * goldenRatio);
                }
            }
        }
        mOutputScale = ImageUtils.calculateOutputScaleFactor(viewWidth, viewHeight);
        mFramePhotoLayout.build(viewWidth, viewHeight, mOutputScale, mSpace, mCorner);
        if (mSavedInstanceState != null) {
            mFramePhotoLayout.restoreInstanceState(mSavedInstanceState);
            mSavedInstanceState = null;
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(viewWidth, viewHeight);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mContainerLayout.removeAllViews();
        mContainerLayout.addView(mFramePhotoLayout, params);

        mContainerLayout.removeView(mPhotoView);
        mContainerLayout.addView(mPhotoView, params);

        mSpaceBar.setProgress((int) (MAX_SPACE_PROGRESS * mSpace / MAX_SPACE));
        mCornerBar.setProgress((int) (MAX_CORNER_PROGRESS * mCorner / MAX_CORNER));

    }

    @Override
    public void onEditActionClick(FrameImageView v) {
        mSelectedFrameImageView = v;
        if (v.getImage() != null && v.getPhotoItem().imagePath != null && v.getPhotoItem().imagePath.length() > 0) {

            Uri uri = Uri.fromFile(new File(v.getPhotoItem().imagePath));
            requestEditingImage(uri, action);
//            v.setImage(doBrightness(v.getImage(),50));
//            v.invalidate();
        }
    }

    @Override
    public void onChangeActionClick(FrameImageView v) {
        mSelectedFrameImageView = v;
        requestPhoto();


    }

    @Override
    public void onDeleteActionClick(FrameImageView v) {
        v.clearMainImage();
    }

    @Override
    protected void resultEditImage(Uri uri) {
        if (mSelectedFrameImageView != null) {
            mSelectedFrameImageView.setImagePath(FileUtils.getPath(this, uri));
            buildLayout(mSelectedTemplateItem);
        }

    }

    @Override
    protected void resultFromPhotoEditor(Uri image) {
        if (mSelectedFrameImageView != null) {
            mSelectedFrameImageView.setImagePath(FileUtils.getPath(this, image));
        }
    }

    private void recycleBackgroundImage() {
        if (mBackgroundImage != null && !mBackgroundImage.isRecycled()) {
            mBackgroundImage.recycle();
            mBackgroundImage = null;
            System.gc();
        }
    }

    @Override
    protected void resultBackground(Uri uri) {
        recycleBackgroundImage();
        mBackgroundUri = uri;
        mBackgroundImage = ImageDecoder.decodeUriToBitmap(this, uri);
        if (Build.VERSION.SDK_INT >= 16)
            mContainerLayout.setBackground(new BitmapDrawable(getResources(), mBackgroundImage));
        else
            mContainerLayout.setBackgroundDrawable(new BitmapDrawable(getResources(), mBackgroundImage));
    }

    @Override
    public void onColorChanged(int color) {
        recycleBackgroundImage();
        mBackgroundColor = color;
        mContainerLayout.setBackgroundColor(mBackgroundColor);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_PHOTO && resultCode == RESULT_OK) {
            ArrayList<String> mSelectedImages = data.getStringArrayListExtra(SelectPhotoActivity.EXTRA_SELECTED_IMAGES);
            if (mSelectedFrameImageView != null && mSelectedImages != null && !mSelectedImages.isEmpty()) {
                mSelectedFrameImageView.setImagePath(mSelectedImages.get(0));
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void finish() {
//        recycleBackgroundImage();
//        if (mFramePhotoLayout != null) {
//            mFramePhotoLayout.recycleImages();
//        }
        super.finish();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(FrameDetailActivity.this,MainActivity.class));
        finish();
        //startActivity(new Intent(FrameDetailActivity.this,MainActivity.class));
    }

    @Override
    public void onPhotoTapListner(FrameImageView view) {
        //editorLayar.setVisibility(View.VISIBLE);
        mSelectedFrameImageView = view;

//        if (isRecyclerShown){
//            hideShowRecyclerView(false);
//            isRecyclerShown = false;
//        }else {
//            hideShowRecyclerView(true);
//            isRecyclerShown = true;
//        }
    }

    @Override
    public void onPhotoDoubleTapListner(FrameImageView v) {
        if (v.getImage() != null && v.getPhotoItem().imagePath != null && v.getPhotoItem().imagePath.length() > 0) {

            Uri uri = Uri.fromFile(new File(v.getPhotoItem().imagePath));
            requestEditingImage(uri, action);
//            v.setImage(doBrightness(v.getImage(),50));
//            v.invalidate();
        }
    }

    private class ApplyFilterBackground extends AsyncTask<Integer, Integer, Bitmap> {

        private Bitmap resp;
        ProgressDialog progressDialog;


        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            progressDialog.dismiss();
            if (mSelectedFrameImageView !=null && bitmap != null){

                mSelectedFrameImageView.setImage(bitmap);
                mSelectedFrameImageView.invalidate();

            }else {
                Toast.makeText(FrameDetailActivity.this, "Please select photo first!", Toast.LENGTH_SHORT).show();
            }
        }




        @Override
        protected Bitmap doInBackground(Integer... bitmaps) {
            // Calls onProgressUpdate()
            Bitmap newBitMap=null;
            if (mSelectedFrameImageView !=null){

                 if (bitmaps[0].equals(0)){
                     newBitMap  =   doBrightness(mSelectedFrameImageView.getImage(),20);
                 }else if (bitmaps[0].equals(1)){
                     newBitMap  =   CustomFilters.applyContrast(mSelectedFrameImageView.getImage(),10);
                 }else if (bitmaps[0].equals(2)){
                     newBitMap  =   CustomFilters.applySaturationFilter(mSelectedFrameImageView.getImage(),2);
                 }

            }else {
                Toast.makeText(FrameDetailActivity.this, "Please select photo first!", Toast.LENGTH_SHORT).show();
            }
            return newBitMap;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(FrameDetailActivity.this,
                    "Please Wait",
                    "Applying filter");
            if (mSelectedFrameImageView ==null){

                Toast.makeText(FrameDetailActivity.this, "Please select photo first!", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }



    }
}

