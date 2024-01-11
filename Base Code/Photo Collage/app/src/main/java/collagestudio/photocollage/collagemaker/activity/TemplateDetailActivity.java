package collagestudio.photocollage.collagemaker.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import collagestudio.photocollage.collagemaker.R;

import collagestudio.photocollage.collagemaker.utils.Constant;
import collagestudio.photocollage.collagemaker.model.TemplateItem;
import collagestudio.photocollage.collagemaker.utils.CustomFilters;
import collagestudio.photocollage.collagemaker.views.ItemImageView;
import collagestudio.photocollage.collagemaker.utils.PhotoItem;
import collagestudio.photocollage.collagemaker.views.PhotoLayout;
import collagestudio.photocollage.collagemaker.views.TransitionImageView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;

import dauroi.photoeditor.utils.FileUtils;
import dauroi.photoeditor.utils.PhotoUtils;

import static collagestudio.photocollage.collagemaker.utils.CustomFilters.doBrightness;


public class TemplateDetailActivity extends BaseTemplateDetailActivity implements PhotoLayout.TemplatePhotoTapListener,PhotoLayout.OnQuickActionClickListener {

    public void goBack(View view) {
        startActivity( new Intent(TemplateDetailActivity.this,TemplateActivity.class));
        finish();
    }

    private PhotoLayout mPhotoLayout;
    public static ItemImageView mSelectedItemImageView;
    private TransitionImageView mBackgroundImageView;
    private ImageView rotateBtn,framesBtn,frameBtn, aspectBtn, addText, addSticker, bgColorBtn, bgImgBtn, deleteBtn, cropBtn, filtersBtn,
            changeBtn,brightnessBtn,contrastBtn,temperatureBtn;
    TextView backBtn,saveBtn;
    ConstraintLayout save,back;
    public static String action = "filters";
    ImageView share;
    TextView toptoshow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addText = findViewById(R.id.iv_text);
        addSticker = findViewById(R.id.iv_sticker);
        bgColorBtn = findViewById(R.id.iv_bgcolor);

        backBtn = findViewById(R.id.iv_back);
        saveBtn = findViewById(R.id.tv_save);

        back = findViewById(R.id.backtemplate);
        save = findViewById(R.id.savetemplate);

        filtersBtn = findViewById(R.id.iv_filters);
        cropBtn = findViewById(R.id.iv_crop);
        changeBtn = findViewById(R.id.iv_change);
        deleteBtn = findViewById(R.id.iv_delete);
        frameBtn = findViewById(R.id.iv_frame);
        brightnessBtn = findViewById(R.id.iv_brightness);
        contrastBtn = findViewById(R.id.iv_contrast);
        rotateBtn = findViewById(R.id.iv_rotate);
        share = findViewById(R.id.share);
//        toptoshow = findViewById(R.id.texttoshow);

        temperatureBtn = findViewById(R.id.iv_temperature);
        framesBtn = findViewById(R.id.iv_frames);


        for (PhotoItem item : mSelectedTemplateItem.getPhotoItemList())
            if (item.imagePath != null && item.imagePath.length() > 0) {
                mSelectedPhotoPaths.add(item.imagePath);
            }
        
        boolean show = mPreferences.getBoolean(Constant.SHOW_GUIDE_CREATE_TEMPLATE_KEY, true);
        if (show) {
            clickInfoView();
            mPreferences.edit().putBoolean(Constant.SHOW_GUIDE_CREATE_TEMPLATE_KEY, false)
                    .commit();
        }
        addAdsView(R.id.adsLayout);

//        showMyDialog();
        filtersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (mSelectedItemImageView != null) {
                    action = "filters";
                    onEditActionClick(mSelectedItemImageView);

                } else {
                    Toast.makeText(TemplateDetailActivity.this, "Please select the image first!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        cropBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectedItemImageView != null) {
                    action = "crop";
                    onEditActionClick(mSelectedItemImageView);

                } else {
                    Toast.makeText(TemplateDetailActivity.this, "Please select the image first!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        rotateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectedItemImageView != null) {
                    action = "rotate";
                    onEditActionClick(mSelectedItemImageView);

                } else {
                    Toast.makeText(TemplateDetailActivity.this, "Please select the image first!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        frameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectedItemImageView != null) {
                    action = "frame";
                    onEditActionClick(mSelectedItemImageView);

                } else {
                    Toast.makeText(TemplateDetailActivity.this, "Please select the image first!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectedItemImageView != null) {
                    onChangeActionClick(mSelectedItemImageView);
                } else {
                    Toast.makeText(TemplateDetailActivity.this, "Please select the image first!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectedItemImageView != null) {
                    onDeleteActionClick(mSelectedItemImageView);//deleteFile(mSelectedItemImageView);
                } else {
                    Toast.makeText(TemplateDetailActivity.this, "Please select the image first!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TemplateDetailActivity.this, MainActivity.class));
                finish();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getAdsHelper() != null) {
                    getAdsHelper().showInterstitialAds();
                }
                saveImage();
                Animation animation = AnimationUtils.loadAnimation(TemplateDetailActivity.this,R.anim.photo_editor_slice_in_right);
                share.setAnimation(animation);
//                share.show();
//                toptoshow.setVisibility(View.GONE);
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


        brightnessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectedItemImageView !=null){

//                    Bitmap newBitMap  =   doBrightness(mSelectedItemImageView.getImage(),15);
//                    mSelectedItemImageView.setImage(newBitMap);
//                    mSelectedItemImageView.invalidate();
                    new ApplyFilterBackground().execute(0);

                }else {
                    Toast.makeText(TemplateDetailActivity.this, "Please select photo first!", Toast.LENGTH_SHORT).show();
                }

            }
        });
        temperatureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectedItemImageView !=null){

//                    Bitmap newBitMap  =   doBrightness(mSelectedItemImageView.getImage(),15);
//                    mSelectedItemImageView.setImage(newBitMap);
//                    mSelectedItemImageView.invalidate();
                    new ApplyFilterBackground().execute(2);

                }else {
                    Toast.makeText(TemplateDetailActivity.this, "Please select photo first!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        contrastBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectedItemImageView !=null){
//                    Bitmap newBitMap  =   applyContrast(mSelectedItemImageView.getImage(),10);
//                    mSelectedItemImageView.setImage(newBitMap);
//                    mSelectedItemImageView.invalidate();
                    new ApplyFilterBackground().execute(1);

                }else {
                    Toast.makeText(TemplateDetailActivity.this, "Please select photo first!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

 /*   private void showMyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TemplateDetailActivity.this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(TemplateDetailActivity.this).inflate(R.layout.customview, viewGroup, false);
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
    protected int getLayoutId() {
        return R.layout.activity_template_detail;
    }

    @Override
    public Bitmap createOutputImage() {
        Bitmap template = mPhotoLayout.createImage();
        Bitmap result = Bitmap.createBitmap(template.getWidth(), template.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        canvas.drawBitmap(template, 0, 0, paint);
        template.recycle();
        template = null;
        Bitmap stickers = mPhotoView.getImage(mOutputScale);
        canvas.drawBitmap(stickers, 0, 0, paint);
        stickers.recycle();
        stickers = null;
        System.gc();
        return result;
    }

    @Override
    public void onEditActionClick(ItemImageView v) {
        mSelectedItemImageView = v;
        if (v.getImage() != null && v.getPhotoItem().imagePath != null && v.getPhotoItem().imagePath.length() > 0) {
            Uri uri = Uri.fromFile(new File(v.getPhotoItem().imagePath));
            requestEditingImage(uri,action);
        }
    }

    @Override
    public void onChangeActionClick(ItemImageView v) {
        mSelectedItemImageView = v;
        final Dialog dialog = getBackgroundImageDialog();
        if (dialog != null)
            dialog.findViewById(R.id.alterBackgroundView).setVisibility(View.GONE);
        requestPhoto();
    }

    @Override
    public void onChangeBackgroundActionClick(TransitionImageView v) {
        mBackgroundImageView = v;
        final Dialog dialog = getBackgroundImageDialog();
        if (dialog != null)
            dialog.findViewById(R.id.alterBackgroundView).setVisibility(View.VISIBLE);
        requestPhoto();
    }

    @Override
    public void onDeleteActionClick(ItemImageView v) {
        v.clearMainImage();
    }

    @Override
    protected void resultEditImage(Uri uri) {
        if (mBackgroundImageView != null) {
            mBackgroundImageView.setImagePath(FileUtils.getPath(this, uri));
            mBackgroundImageView = null;
        } else if (mSelectedItemImageView != null) {
            mSelectedItemImageView.setImagePath(FileUtils.getPath(this, uri));
            buildLayout(mSelectedTemplateItem);
        }
    }

    @Override
    protected void resultFromPhotoEditor(Uri image) {
        if (mBackgroundImageView != null) {
            mBackgroundImageView.setImagePath(FileUtils.getPath(this, image));
            mBackgroundImageView = null;
        } else if (mSelectedItemImageView != null) {
            mSelectedItemImageView.setImagePath(FileUtils.getPath(this, image));
//            for(int i =0;i<mTemplateItemList.size();i++){
//                if (mTemplateItemList.get(i).getPhotoItemList().get(i).equals(mSelectedItemImageView)){
//
//                }
//            }
            mSelectedItemImageView.invalidate();
        }
    }

    @Override
    protected void resultBackground(Uri uri) {
        if (mBackgroundImageView != null) {
            mBackgroundImageView.setImagePath(FileUtils.getPath(this, uri));
            mBackgroundImageView = null;
        } else if (mSelectedItemImageView != null) {
            mSelectedItemImageView.setImagePath(FileUtils.getPath(this, uri));
        }
    }

    @Override
    protected void buildLayout(TemplateItem templateItem) {
        Bitmap backgroundImage = null;
        if (mPhotoLayout != null) {
            backgroundImage = mPhotoLayout.getBackgroundImage();
           // mPhotoLayout.recycleImages(false);
        }

        final Bitmap frameImage = PhotoUtils.decodePNGImage(this, templateItem.getTemplate());
        int[] size = calculateThumbnailSize(frameImage.getWidth(), frameImage.getHeight());

        mPhotoLayout = new PhotoLayout(this, templateItem.getPhotoItemList(), frameImage,this);
        mPhotoLayout.setBackgroundImage(backgroundImage);
        mPhotoLayout.setQuickActionClickListener(this);
        mPhotoLayout.build(size[0], size[1], mOutputScale);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(size[0], size[1]);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mContainerLayout.removeAllViews();
        mContainerLayout.addView(mPhotoLayout, params);

        mContainerLayout.removeView(mPhotoView);
        mContainerLayout.addView(mPhotoView, params);
    }

    @Override
    public void finish() {
//        if (mPhotoLayout != null) {
//            mPhotoLayout.recycleImages(true);
//        }
        super.finish();
    }





    @Override
    public void onTemplateTap(ItemImageView v) {
        mSelectedItemImageView = v;

    }

    @Override
    public void onTemplateDoubleTap(ItemImageView v) {
        if (v.getImage() != null && v.getPhotoItem().imagePath != null && v.getPhotoItem().imagePath.length() > 0) {
            Uri uri = Uri.fromFile(new File(v.getPhotoItem().imagePath));
            requestEditingImage(uri,action);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(TemplateDetailActivity.this,TemplateActivity.class));
        finish();
       //

    }

    private class ApplyFilterBackground extends AsyncTask<Integer, Integer, Bitmap> {

        private Bitmap resp;
        ProgressDialog progressDialog;


        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            progressDialog.dismiss();
            if (mSelectedItemImageView !=null && bitmap != null){

                mSelectedItemImageView.setImage(bitmap);
                mSelectedItemImageView.invalidate();

            }else {
                Toast.makeText(TemplateDetailActivity.this, "Please select photo first!", Toast.LENGTH_SHORT).show();
            }
        }




        @Override
        protected Bitmap doInBackground(Integer... bitmaps) {
            // Calls onProgressUpdate()
            Bitmap newBitMap=null;
            if (mSelectedItemImageView !=null){

                if (bitmaps[0].equals(0)){
                    newBitMap  =   doBrightness(mSelectedItemImageView.getImage(),20);
                }else if (bitmaps[0].equals(1)){
                    newBitMap  =   CustomFilters.applyContrast(mSelectedItemImageView.getImage(),10);
                }else if (bitmaps[0].equals(2)){
                    newBitMap  =   CustomFilters.applySaturationFilter(mSelectedItemImageView.getImage(),2);
                }

            }else {
                Toast.makeText(TemplateDetailActivity.this, "Please select photo first!", Toast.LENGTH_SHORT).show();
            }
            return newBitMap;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(TemplateDetailActivity.this,
                    "Please Wait",
                    "Applying filter");
            if (mSelectedItemImageView ==null){

                Toast.makeText(TemplateDetailActivity.this, "Please select photo first!", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }



    }

}
