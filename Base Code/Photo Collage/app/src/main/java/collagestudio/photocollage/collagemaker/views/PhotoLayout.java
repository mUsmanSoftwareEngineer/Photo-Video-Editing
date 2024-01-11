package collagestudio.photocollage.collagemaker.views;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import collagestudio.photocollage.collagemaker.R;

import collagestudio.photocollage.collagemaker.utils.ALog;
import collagestudio.photocollage.collagemaker.quickaction.QuickAction;
import collagestudio.photocollage.collagemaker.quickaction.QuickActionItem;
import collagestudio.photocollage.collagemaker.utils.PhotoItem;
import collagestudio.photocollage.collagemaker.utils.ImageDecoder;
import collagestudio.photocollage.collagemaker.utils.ImageUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dauroi.photoeditor.model.ImageTemplate;
import dauroi.photoeditor.utils.PhotoUtils;


public class PhotoLayout extends RelativeLayout implements ItemImageView.OnImageClickListener{
    private static final String TAG = PhotoLayout.class.getSimpleName();
    float width,height;
    TemplatePhotoTapListener templatePhotoTapListener;

    public interface TemplatePhotoTapListener{
        void onTemplateTap(ItemImageView v);
        void onTemplateDoubleTap(ItemImageView v);
    }


    public interface OnQuickActionClickListener {
        void onEditActionClick(ItemImageView v);

        void onChangeActionClick(ItemImageView v);

        void onChangeBackgroundActionClick(TransitionImageView v);
        void onDeleteActionClick(ItemImageView v);
    }

    OnDragListener mOnDragListener = new OnDragListener() {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            int dragEvent = event.getAction();

            switch (dragEvent) {
                case DragEvent.ACTION_DRAG_ENTERED:
                    ALog.i("Drag Event", "Entered: x=" + event.getX() + ", y=" + event.getY());
                    break;

                case DragEvent.ACTION_DRAG_EXITED:
                    ALog.i("Drag Event", "Exited: x=" + event.getX() + ", y=" + event.getY());
                    break;

                case DragEvent.ACTION_DROP:
                    ItemImageView target = (ItemImageView) v;
                    ItemImageView dragged = (ItemImageView) event.getLocalState();
                    String targetPath = "", draggedPath = "";
                    if (target.getPhotoItem() != null)
                        targetPath = target.getPhotoItem().imagePath;
                    if (dragged.getPhotoItem() != null)
                        draggedPath = dragged.getPhotoItem().imagePath;
                    if (targetPath == null) targetPath = "";
                    if (draggedPath == null) draggedPath = "";
                    if (!targetPath.equals(draggedPath))
                        target.swapImage(dragged);
                    break;
            }

            return true;
        }
    };

    
    private static final int ID_EDIT = 1;
    private static final int ID_CHANGE = 2;
    private static final int ID_DELETE = 3;
    private static final int ID_CANCEL = 4;

    private QuickAction mQuickAction;
    private QuickAction mBackgroundQuickAction;

    private List<PhotoItem> mPhotoItems;
    private int mImageWidth, mImageHeight;
    private List<ItemImageView> mItemImageViews;
    private TransitionImageView mBackgroundImageView;
    private int mViewWidth, mViewHeight;
    private float mInternalScaleRatio = 1;
    private float mOutputScaleRatio = 1;
    private Bitmap mTemplateImage;
    private OnQuickActionClickListener mQuickActionClickListener;

    private ProgressBar mProgressBar;
    private Bitmap mBackgroundImage;

    public static List<PhotoItem> parseImageTemplate(ImageTemplate template) {
        List<PhotoItem> photoItems = new ArrayList<>();
        try {
            String[] childTexts = template.getChild().split(";");
            if (childTexts != null) {
                for (String child : childTexts) {
                    String[] properties = child.split(",");
                    if (properties != null) {
                        PhotoItem item = new PhotoItem();
                        item.index = Integer.parseInt(properties[0]);
                        item.x = Integer.parseInt(properties[1]);
                        item.y = Integer.parseInt(properties[2]);
                        item.maskPath = properties[3];
                        photoItems.add(item);
                    }
                }
                
                Collections.sort(photoItems, new Comparator<PhotoItem>() {
                    @Override
                    public int compare(PhotoItem lhs, PhotoItem rhs) {
                        return rhs.index - lhs.index;
                    }
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return photoItems;
    }

    public PhotoLayout(Context context, ImageTemplate template) {
        super(context);
        Bitmap templateImage = PhotoUtils.decodePNGImage(context, template.getTemplate());
        List<PhotoItem> photoItems = parseImageTemplate(template);
        init(photoItems, templateImage);
    }
    public PhotoLayout(Context context, List<PhotoItem> photoItems, Bitmap templateImage,TemplatePhotoTapListener listener) {
        super(context);
        init(photoItems, templateImage);
        templatePhotoTapListener = listener;
    }
    public PhotoLayout(Context context, List<PhotoItem> photoItems, Bitmap templateImage) {
        super(context);
        init(photoItems, templateImage);
    }

    private void init(List<PhotoItem> photoItems, Bitmap templateImage) {
        mPhotoItems = photoItems;
        mTemplateImage = templateImage;
        mImageWidth = mTemplateImage.getWidth();
        mImageHeight = mTemplateImage.getHeight();
        mItemImageViews = new ArrayList<>();
        setLayerType(LAYER_TYPE_HARDWARE, null);
        createQuickAction();
    }

    public void setQuickActionClickListener(OnQuickActionClickListener quickActionClickListener) {
        mQuickActionClickListener = quickActionClickListener;
    }

    private void createQuickAction() {
        QuickActionItem editItem = new QuickActionItem(ID_EDIT, getContext().getString(R.string.edit), getResources().getDrawable(R.drawable.menu_edit));
        QuickActionItem changeItem = new QuickActionItem(ID_CHANGE, getContext().getString(R.string.change), getResources().getDrawable(R.drawable.menu_change));
        QuickActionItem deleteItem = new QuickActionItem(ID_DELETE, getContext().getString(R.string.delete), getResources().getDrawable(R.drawable.menu_delete));
        QuickActionItem cancelItem = new QuickActionItem(ID_CANCEL, getContext().getString(R.string.cancel), getResources().getDrawable(R.drawable.menu_cancel));

        



        
        
        mQuickAction = new QuickAction(getContext(), QuickAction.HORIZONTAL);

        
        mQuickAction.addActionItem(changeItem);
        mQuickAction.addActionItem(editItem);
        mQuickAction.addActionItem(deleteItem);
        mQuickAction.addActionItem(cancelItem);

        
        mQuickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
            @Override
            public void onItemClick(QuickAction source, int pos, int actionId) {
                QuickActionItem quickActionItem = mQuickAction.getActionItem(pos);
                mQuickAction.dismiss();
                
                if (actionId == ID_DELETE) {
                    ItemImageView v = (ItemImageView) mQuickAction.getAnchorView();
                    v.clearMainImage();
                } else if (actionId == ID_EDIT) {
                    if (mQuickActionClickListener != null) {
                        mQuickActionClickListener.onEditActionClick((ItemImageView) mQuickAction.getAnchorView());
                    }
                } else if (actionId == ID_CHANGE) {
                    if (mQuickActionClickListener != null) {
                        mQuickActionClickListener.onChangeActionClick((ItemImageView) mQuickAction.getAnchorView());
                    }
                }
            }
        });

        
        
        mQuickAction.setOnDismissListener(new QuickAction.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });
        
        changeItem = new QuickActionItem(ID_CHANGE, getContext().getString(R.string.change), getResources().getDrawable(R.drawable.menu_change));
        deleteItem = new QuickActionItem(ID_DELETE, getContext().getString(R.string.delete), getResources().getDrawable(R.drawable.menu_delete));
        cancelItem = new QuickActionItem(ID_CANCEL, getContext().getString(R.string.cancel), getResources().getDrawable(R.drawable.menu_cancel));
        
        
        mBackgroundQuickAction = new QuickAction(getContext(), QuickAction.HORIZONTAL);

        
        mBackgroundQuickAction.addActionItem(changeItem);
        mBackgroundQuickAction.addActionItem(deleteItem);
        mBackgroundQuickAction.addActionItem(cancelItem);

        
        mBackgroundQuickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
            @Override
            public void onItemClick(QuickAction source, int pos, int actionId) {
                QuickActionItem quickActionItem = mBackgroundQuickAction.getActionItem(pos);
                mBackgroundQuickAction.dismiss();
                
                if (actionId == ID_DELETE) {
                    TransitionImageView v = (TransitionImageView) mBackgroundQuickAction.getAnchorView();
                    v.recycleImages();
                } else if (actionId == ID_CHANGE) {
                    if (mQuickActionClickListener != null) {
                        mQuickActionClickListener.onChangeBackgroundActionClick((TransitionImageView) mBackgroundQuickAction.getAnchorView());
                    }
                }
            }
        });
    }

    public Bitmap getTemplateImage() {
        return mTemplateImage;
    }

    public TransitionImageView getBackgroundImageView() {
        return mBackgroundImageView;
    }

    public void setBackgroundImage(Bitmap image) {
        mBackgroundImage = image;
    }

    public Bitmap getBackgroundImage() {
        return mBackgroundImageView.getImage();
    }

    private void asyncCreateBackgroundImage(final String path) {
        ALog.d(TAG, "asyncCreateBackgroundImage");
        AsyncTask<Void, Void, Bitmap> task = new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressBar.setVisibility(VISIBLE);
            }

            @Override
            protected Bitmap doInBackground(Void... params) {
                try {
                    Bitmap image = ImageDecoder.decodeFileToBitmap(path);
                    if (image != null) {

                        Bitmap result = blur(image);//PhotoUtils.blurImage(image, 10);
                        if (image != result) {
                            image.recycle();
                            image = null;
                            System.gc();
                        }
                        return result;
                    }
                } catch (OutOfMemoryError err) {
                    err.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                super.onPostExecute(result);
                mProgressBar.setVisibility(GONE);
                if (result != null)
                    mBackgroundImageView.init(result, mViewWidth, mViewHeight, mOutputScaleRatio);
            }
        };

        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void build(final int viewWidth, final int viewHeight, final float outputScaleRatio) {
        if (viewWidth < 1 || viewHeight < 1) {
            return;
        }
        
        mViewWidth = viewWidth;
        mViewHeight = viewHeight;
        mOutputScaleRatio = outputScaleRatio;
        mItemImageViews.clear();
        mInternalScaleRatio = 1.0f / PhotoUtils.calculateScaleRatio(mImageWidth, mImageHeight, viewWidth, viewHeight);
        for (PhotoItem item : mPhotoItems) {
            mItemImageViews.add(addPhotoItemView(item, mInternalScaleRatio, mOutputScaleRatio));
            //invalidate();
            //addPhotoItemView(item, mInternalScaleRatio, mOutputScaleRatio);
        }
        
        final ImageView templateImageView = new ImageView(getContext());
        if (Build.VERSION.SDK_INT >= 16) {
            templateImageView.setBackground(new BitmapDrawable(getResources(), mTemplateImage));
        } else {
            templateImageView.setBackgroundDrawable(new BitmapDrawable(getResources(), mTemplateImage));
        }

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(templateImageView, params);
        
        mProgressBar = new ProgressBar(getContext());
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mProgressBar.setVisibility(View.GONE);
        addView(mProgressBar, params);
        
        mBackgroundImageView = new TransitionImageView(getContext());
        params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(mBackgroundImageView, 0, params);
        
        mBackgroundImageView.setOnImageClickListener(new TransitionImageView.OnImageClickListener() {
            @Override
            public void onLongClickImage(TransitionImageView view) {

            }

            @Override
            public void onDoubleClickImage(TransitionImageView v) {
                if ((v.getImage() == null || v.getImage().isRecycled()) && mQuickActionClickListener != null) {
                    mQuickActionClickListener.onChangeBackgroundActionClick(v);
                } else {
                    mBackgroundQuickAction.show(v, (int) (v.getWidth() / 2.0), (int) (v.getHeight() / 2.0));
                    mBackgroundQuickAction.setAnimStyle(QuickAction.ANIM_REFLECT);
                }
            }
        });

        if (mBackgroundImage == null || mBackgroundImage.isRecycled()) {
            if (mPhotoItems.size() > 0 && mPhotoItems.get(0).imagePath != null && mPhotoItems.get(0).imagePath.length() > 0) {
                asyncCreateBackgroundImage(mPhotoItems.get(0).imagePath);
            }
        } else {
            mBackgroundImageView.init(mBackgroundImage, mViewWidth, mViewHeight, mOutputScaleRatio);
        }
    }

//   method to laod server images
//    private void addPhotoItemView(PhotoItem item, float internalScale, float outputScaleRatio) {
//        if (item == null || item.maskPath == null) {
//            //return null;
//        }else {
//            Log.d("firebase7676", "addPhotoItemView: "+ item.imagePath+", mask: "+item.maskPath);
//            ALog.d("PhotoLayout", "addPhotoItemView, item.x=" + item.x + ", item.y=" + item.y + ", scale=" + internalScale);
//            Picasso.with(getContext()).load(item.maskPath).into(new Target() {
//                @Override
//                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//
//                    final ItemImageView imageView = new ItemImageView(getContext(), item,bitmap);
//                    final float viewWidth = internalScale * imageView.getMaskImage().getWidth();
//                    final float viewHeight = internalScale * imageView.getMaskImage().getHeight();
//                    imageView.init(viewWidth,viewHeight,outputScaleRatio);
//                    imageView.setOnImageClickListener(PhotoLayout.this);
//                    if (mPhotoItems.size() > 1)
//                        imageView.setOnDragListener(mOnDragListener);
//
//                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) viewWidth, (int) viewHeight);
//                    params.leftMargin = (int) (internalScale * item.x);
//                    params.topMargin = (int) (internalScale * item.y);
//                    imageView.setOriginalLayoutParams(params);
//                    addView(imageView, params);
//                    mItemImageViews.add(imageView);
//
//                }
//
//                @Override
//                public void onBitmapFailed(Drawable errorDrawable) {
//
//
//                }
//
//                @Override
//                public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//                }
//            });
//
//        }
//        //boolean isLoaded = false;
//
//
//
//    }
    private ItemImageView addPhotoItemView(PhotoItem item, float internalScale, float outputScaleRatio) {
        if (item == null || item.maskPath == null) {
            return null;
        }
        ALog.d("PhotoLayout", "addPhotoItemView, item.x=" + item.x + ", item.y=" + item.y + ", scale=" + internalScale);
        final ItemImageView imageView = new ItemImageView(getContext(), item);
        final float viewWidth = internalScale * imageView.getMaskImage().getWidth();
        final float viewHeight = internalScale * imageView.getMaskImage().getHeight();
        imageView.init(viewWidth, viewHeight, outputScaleRatio);
        imageView.setOnImageClickListener(this);
        if (mPhotoItems.size() > 1)
            imageView.setOnDragListener(mOnDragListener);

        LayoutParams params = new LayoutParams((int) viewWidth, (int) viewHeight);
        params.leftMargin = (int) (internalScale * item.x);
        params.topMargin = (int) (internalScale * item.y);
        imageView.setOriginalLayoutParams(params);
        imageView.setForeground(getResources().getDrawable(R.drawable.rec1));
        addView(imageView, params);
        return imageView;
    }

    public Bitmap createImage() {
        Bitmap template = Bitmap.createBitmap((int) (mOutputScaleRatio * mViewWidth), (int) (mOutputScaleRatio * mViewHeight), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(template);
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        if (mBackgroundImageView.getImage() != null && !mBackgroundImageView.getImage().isRecycled()) {
            canvas.drawBitmap(mBackgroundImageView.getImage(), mBackgroundImageView.getScaleMatrix(), paint);
        }

        canvas.saveLayer(0, 0, template.getWidth(), template.getHeight(), paint, Canvas.ALL_SAVE_FLAG);

        for (ItemImageView view : mItemImageViews)
            if (view.getImage() != null && !view.getImage().isRecycled()) {
                final int left = (int) (view.getLeft() * mOutputScaleRatio);
                final int top = (int) (view.getTop() * mOutputScaleRatio);
                final int width = (int) (view.getWidth() * mOutputScaleRatio);
                final int height = (int) (view.getHeight() * mOutputScaleRatio);
                canvas.saveLayer(left, top, left + width, top + height, paint, Canvas.ALL_SAVE_FLAG);
                
                canvas.save();
                canvas.translate(left, top);
                canvas.clipRect(0, 0, width, height);
                canvas.drawBitmap(view.getImage(), view.getScaleMatrix(), paint);
                canvas.restore();
                
                canvas.save();
                canvas.translate(left, top);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
                canvas.drawBitmap(view.getMaskImage(), view.getScaleMaskMatrix(), paint);
                paint.setXfermode(null);
                canvas.restore();
                canvas.restore();
            }
        
        if (mTemplateImage != null) {
            canvas.drawBitmap(mTemplateImage,
                    ImageUtils.createMatrixToDrawImageInCenterView(mOutputScaleRatio * mViewWidth, mOutputScaleRatio * mViewHeight,
                            mTemplateImage.getWidth(), mTemplateImage.getHeight()), paint);
        }

        canvas.restore();

        return template;
    }

    public void recycleImages(final boolean recycleBackground) {
        ALog.d(TAG, "recycleImages, recycleBackground=" + recycleBackground);
        if (recycleBackground) {
            mBackgroundImageView.recycleImages();
        }

        for (ItemImageView view : mItemImageViews) {
            view.recycleImages(recycleBackground);
        }
        if (mTemplateImage != null && !mTemplateImage.isRecycled()) {
            mTemplateImage.recycle();
            mTemplateImage = null;
        }
        System.gc();
    }

    @Override
    public void onLongClickImage(ItemImageView v) {
        if (mPhotoItems.size() > 1) {
            v.setTag("x=" + v.getPhotoItem().x + ",y=" + v.getPhotoItem().y + ",path=" + v.getPhotoItem().imagePath);
            ClipData.Item item = new ClipData.Item((CharSequence) v.getTag());
            String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
            ClipData dragData = new ClipData(v.getTag().toString(), mimeTypes, item);
            DragShadowBuilder myShadow = new DragShadowBuilder(v);
            v.startDrag(dragData, myShadow, v, 0);
        }
    }

    @Override
    public void onDoubleClickImage(ItemImageView v) {
      templatePhotoTapListener.onTemplateDoubleTap(v);
//            mQuickAction.show(v);
//            mQuickAction.setAnimStyle(QuickAction.ANIM_REFLECT);

    }

    @Override
    public void onSingleeClickImage(ItemImageView v) {
        if ((v.getImage() == null ) && mQuickActionClickListener != null) {
            mQuickActionClickListener.onChangeActionClick(v);
        } else {
            for (int i=0;i<mItemImageViews.size();i++){
                if (mItemImageViews.get(i).equals(v)){
                    try {
                        mItemImageViews.get(i).setForeground(getResources().getDrawable(R.drawable.rec));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    try {
                        mItemImageViews.get(i).setForeground(getResources().getDrawable(R.drawable.rec1));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            templatePhotoTapListener.onTemplateTap(v);
        }
    }

    public Bitmap blur(Bitmap image) {
        if (null == image) return null;
        Bitmap outputBitmap = Bitmap.createBitmap(image);
        final RenderScript renderScript = RenderScript.create(getContext());
        Allocation tmpIn = Allocation.createFromBitmap(renderScript, image);
        Allocation tmpOut = Allocation.createFromBitmap(renderScript, outputBitmap);
        //Intrinsic Gausian blur filter
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        theIntrinsic.setRadius(6f);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);

//        Matrix m = new Matrix();
//        m.preScale(-1, 1);
//        return Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), m, false);

        return outputBitmap;
    }
}