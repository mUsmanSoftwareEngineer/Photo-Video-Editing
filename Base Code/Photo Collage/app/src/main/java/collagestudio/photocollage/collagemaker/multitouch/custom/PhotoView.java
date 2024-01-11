package collagestudio.photocollage.collagemaker.multitouch.custom;

import java.util.ArrayList;
import java.util.List;

import collagestudio.photocollage.collagemaker.R;
import collagestudio.photocollage.collagemaker.frame.FrameTouch;
import collagestudio.photocollage.collagemaker.multitouch.controller.ImageEntity;
import collagestudio.photocollage.collagemaker.multitouch.controller.MultiTouchController;
import collagestudio.photocollage.collagemaker.multitouch.controller.MultiTouchEntity;
import collagestudio.photocollage.collagemaker.views.DoubleClickDetector;
import collagestudio.photocollage.collagemaker.listener.OnFrameTouchListener;
import collagestudio.photocollage.collagemaker.utils.ImageDecoder;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

public class PhotoView extends View implements
        MultiTouchController.MultiTouchObjectCanvas<MultiTouchEntity> {
    private static final long DOUBLE_CLICK_TIME_INTERVAL = 700;

    private ArrayList<MultiTouchEntity> mImages = new ArrayList<MultiTouchEntity>();

    private MultiTouchController<MultiTouchEntity> multiTouchController = new MultiTouchController<MultiTouchEntity>(
            this);

    private MultiTouchController.PointInfo currTouchPoint = new MultiTouchController.PointInfo();

    private boolean mShowDebugInfo = false;

    private static final int UI_MODE_ROTATE = 1, UI_MODE_ANISOTROPIC_SCALE = 2;

    private int mUIMode = UI_MODE_ROTATE;



    private Paint mLinePaintTouchPointCircle = new Paint();

    private static final float SCREEN_MARGIN = 100;

    private int width, height, displayWidth, displayHeight;

    private MultiTouchEntity mCurrentSelectedObject = null;
    private int mSelectedCount = 0;
    private long mSelectedTime = System.currentTimeMillis();
    private OnDoubleClickListener mClickListener = null;
    private Uri mPhotoBackgroundUri = null;
    private float mOldX = 0;
    private float mOldY = 0;
    private float mTouchAreaInterval = 10;

    private OnFrameTouchListener mFrameTouchListener;
    private MultiTouchEntity mTouchedObject = null;
    private DoubleClickDetector mDoubleClickDetector;



    public PhotoView(Context context) {
        this(context, null);

        init(context);
    }

    public PhotoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

        init(context);
    }

    public PhotoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(context);
    }

    private void init(Context context) {
        Resources res = context.getResources();

        mLinePaintTouchPointCircle.setColor(Color.YELLOW);
        mLinePaintTouchPointCircle.setStrokeWidth(5);
        mLinePaintTouchPointCircle.setStyle(Style.STROKE);
        mLinePaintTouchPointCircle.setAntiAlias(true);

        DisplayMetrics metrics = res.getDisplayMetrics();
        this.displayWidth = res.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? Math
                .max(metrics.widthPixels, metrics.heightPixels) : Math.min(
                metrics.widthPixels, metrics.heightPixels);
        this.displayHeight = res.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? Math
                .min(metrics.widthPixels, metrics.heightPixels) : Math.max(
                metrics.widthPixels, metrics.heightPixels);

        mTouchAreaInterval = res.getDimension(R.dimen.touch_area_interval);
        mDoubleClickDetector = new DoubleClickDetector();
        mDoubleClickDetector.setTouchAreaInterval(mTouchAreaInterval);
    }


    public void loadImages(Context context) {
        if (mImages == null) {
            return;
        }

        int n = mImages.size();
        for (int i = 0; i < n; i++) {




            mImages.get(i).load(context);
        }

        cleanImages();
    }

    private void cleanImages() {
        if (mImages == null) {
            return;
        }

        List<MultiTouchEntity> entityList = new ArrayList<MultiTouchEntity>();
        for (MultiTouchEntity entity : mImages)
            if (!((ImageEntity) entity).isNull()) {
                entityList.add(entity);
            }

        mImages.clear();
        mImages.addAll(entityList);
    }

    public void addImageEntity(MultiTouchEntity entity) {
        if (mImages == null) {
            return;
        }

        if (mImages.size() > 0) {
            if ((mImages.get(0) instanceof ImageEntity)
                    && (entity instanceof ImageEntity)) {
                ImageEntity img = (ImageEntity) mImages.get(0);
                ((ImageEntity) entity).setBorderColor(img.getBorderColor());
                ((ImageEntity) entity).setDrawImageBorder(img
                        .isDrawImageBorder());
            }
        }

        mImages.add(entity);
        entity.load(getContext(), (getWidth() - entity.getWidth()) / 2,
                (getHeight() - entity.getHeight()) / 2);
        invalidate();
    }

    public void clearAllImageEntities() {
        if (mImages == null) {
            return;
        }

        unloadImages();
        mImages.clear();
        invalidate();
    }

    public void removeImageEntity(MultiTouchEntity entity) {
        if (mImages == null) {
            return;
        }

        mImages.remove(entity);
        invalidate();
    }

    public void setOnDoubleClickListener(OnDoubleClickListener listener) {
        this.mClickListener = listener;
    }

    public void setFrameTouchListener(OnFrameTouchListener frameTouchListener) {
        mFrameTouchListener = frameTouchListener;
    }

    public void setBorderColor(int color) {
        if (mImages == null) {
            return;
        }

        int n = mImages.size();
        for (int i = 0; i < n; i++) {
            if (mImages.get(i) instanceof ImageEntity) {
                ((ImageEntity) mImages.get(i)).setBorderColor(color);
            }
        }

        invalidate();
    }

    public void setBorderSize(float borderSize) {
        if (mImages == null) {
            return;
        }

        int n = mImages.size();
        for (int i = 0; i < n; i++) {
            if (mImages.get(i) instanceof ImageEntity) {
                ((ImageEntity) mImages.get(i)).setBorderSize(borderSize);
            }
        }

        invalidate();
    }

    public void setDrawImageBound(boolean drawImageBorder) {
        if (mImages == null) {
            return;
        }

        int n = mImages.size();
        for (int i = 0; i < n; i++) {
            if (mImages.get(i) instanceof ImageEntity) {
                ((ImageEntity) mImages.get(i))
                        .setDrawImageBorder(drawImageBorder);
            }
        }

        invalidate();
    }

    public void setDrawShadow(boolean drawShadow) {
        if (mImages == null) {
            return;
        }

        int n = mImages.size();
        for (int i = 0; i < n; i++) {
            if (mImages.get(i) instanceof ImageEntity) {
                ((ImageEntity) mImages.get(i)).setDrawShadow(drawShadow);
            }
        }

        invalidate();
    }

    public void setShadowSize(int shadowSize) {
        if (mImages == null) {
            return;
        }

        int n = mImages.size();
        for (int i = 0; i < n; i++) {
            if (mImages.get(i) instanceof ImageEntity) {
                ((ImageEntity) mImages.get(i)).setShadowSize(shadowSize);
            }
        }

        invalidate();
    }

    public void setImageEntities(ArrayList<MultiTouchEntity> images) {
        mImages = images;
    }

    public ArrayList<MultiTouchEntity> getImageEntities() {
        return mImages;
    }

    @SuppressWarnings("deprecation")
    public void setPhotoBackground(Uri photoBackgroundUri) {

        destroyBackground();

        mPhotoBackgroundUri = photoBackgroundUri;
        if (mPhotoBackgroundUri != null) {
            BitmapDrawable d = ImageDecoder.decodeUriToDrawable(getContext(),
                    photoBackgroundUri);
            if (Build.VERSION.SDK_INT >= 16) {
                setBackground(d);
            } else {
                setBackgroundDrawable(d);
            }
        } else {
            if (Build.VERSION.SDK_INT >= 16) {
                setBackground(null);
            } else {
                setBackgroundDrawable(null);
            }
        }

    }

    public Uri getPhotoBackgroundUri() {
        return mPhotoBackgroundUri;
    }

    @SuppressWarnings("deprecation")
    public void destroyBackground() {
        Drawable d = getBackground();
        if (d != null && d instanceof BitmapDrawable) {
            Bitmap bm = ((BitmapDrawable) d).getBitmap();
            if (bm != null && !bm.isRecycled()) {
                bm.recycle();
            }
            bm = null;
        }

        if (Build.VERSION.SDK_INT >= 16) {
            setBackground(null);
        } else {
            setBackgroundDrawable(null);
        }

        mPhotoBackgroundUri = null;
    }


    public void unloadImages() {
        int n = mImages.size();
        for (int i = 0; i < n; i++)
            mImages.get(i).unload();
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mImages == null) {
            return;
        }

        int n = mImages.size();
        for (int i = 0; i < n; i++)
            mImages.get(i).draw(canvas);
        if (mShowDebugInfo)
            drawMultitouchDebugMarks(canvas);
    }


    public Bitmap getImage(float outputScale) {
        if (mImages == null) {
            return null;
        }

        Bitmap result = Bitmap.createBitmap((int) (getWidth() * outputScale), (int) (getHeight() * outputScale),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Drawable bgDrawable = getBackground();
        if (bgDrawable != null && bgDrawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) bgDrawable).getBitmap();
            if(bitmap != null){
                canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), new Rect(0, 0, result.getWidth(), result.getHeight()), new Paint(Paint.ANTI_ALIAS_FLAG));
            }
        }

        int n = mImages.size();
        for (int i = 0; i < n; i++) {
            MultiTouchEntity entity = mImages.get(i);
            if (entity instanceof ImageEntity) {
                ((ImageEntity) entity).draw(canvas, outputScale);
            } else {
                entity.draw(canvas);
            }
        }

        return result;
    }

    public void trackballClicked() {
        mUIMode = (mUIMode + 1) % 3;
        invalidate();
    }

    private void drawMultitouchDebugMarks(Canvas canvas) {
        if (currTouchPoint.isDown()) {
            float[] xs = currTouchPoint.getXs();
            float[] ys = currTouchPoint.getYs();
            float[] pressures = currTouchPoint.getPressures();
            int numPoints = Math.min(currTouchPoint.getNumTouchPoints(), 2);
            for (int i = 0; i < numPoints; i++)
                canvas.drawCircle(xs[i], ys[i], 50 + pressures[i] * 80,
                        mLinePaintTouchPointCircle);
            if (numPoints == 2)
                canvas.drawLine(xs[0], ys[0], xs[1], ys[1],
                        mLinePaintTouchPointCircle);
        }
    }




    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean b = false;
        boolean processed = false;
        if (mFrameTouchListener != null
                && mFrameTouchListener instanceof FrameTouch
                && ((FrameTouch) mFrameTouchListener).isImageFrameMoving()) {
            if (mFrameTouchListener != null && mTouchedObject == null) {
                mFrameTouchListener.onFrameTouch(event);
                processed = true;
            } else {
                b = multiTouchController.onTouchEvent(event);
            }
        } else {
            b = multiTouchController.onTouchEvent(event);
            if (mFrameTouchListener != null && mTouchedObject == null) {
                mFrameTouchListener.onFrameTouch(event);
                processed = true;
            }
        }

        if (!processed && mTouchedObject == null) {
            if (mDoubleClickDetector.doubleClick(event)
                    && mClickListener != null) {
                mClickListener.onBackgroundDoubleClick();
            }
        }
        return b;
    }


    @Override
    public MultiTouchEntity getDraggableObjectAtPoint(MultiTouchController.PointInfo pt) {
        float x = pt.getX(), y = pt.getY();
        int n = mImages.size();
        for (int i = n - 1; i >= 0; i--) {
            ImageEntity im = (ImageEntity) mImages.get(i);
            if (im.contain(x, y))
                return im;
        }
        return null;
    }


    @Override
    public void selectObject(MultiTouchEntity img, MultiTouchController.PointInfo touchPoint) {
        currTouchPoint.set(touchPoint);
        mTouchedObject = img;
        if (img != null) {

            mImages.remove(img);
            mImages.add(img);

            if (!touchPoint.isMultiTouch() && touchPoint.isDown()) {
                long currentTime = System.currentTimeMillis();
                if (mCurrentSelectedObject != img) {
                    mCurrentSelectedObject = img;
                    mSelectedCount = 1;
                    mOldX = touchPoint.getX();
                    mOldY = touchPoint.getY();
                } else {
                    if (currentTime - mSelectedTime < DOUBLE_CLICK_TIME_INTERVAL) {
                        final float x = touchPoint.getX();
                        final float y = touchPoint.getY();
                        if (mOldX + mTouchAreaInterval > x
                                && mOldX - mTouchAreaInterval < x
                                && mOldY + mTouchAreaInterval > y
                                && mOldY - mTouchAreaInterval < y) {
                            mSelectedCount++;
                        } else {
                            mOldX = x;
                            mOldY = y;
                        }
                    } else {
                        mOldX = touchPoint.getX();
                        mOldY = touchPoint.getY();
                    }

                    if (mSelectedCount == 2) {
                        if (mClickListener != null) {
                            mClickListener.onPhotoViewDoubleClick(this, img);
                        }
                        mCurrentSelectedObject = null;
                        mSelectedCount = 0;
                        mOldX = 0;
                        mOldY = 0;
                    }
                }
                mSelectedTime = currentTime;
            }
        } else {

        }
        invalidate();
    }


    @Override
    public void getPositionAndScale(MultiTouchEntity img,
                                    MultiTouchController.PositionAndScale objPosAndScaleOut) {


        objPosAndScaleOut.set(img.getCenterX(), img.getCenterY(),
                (mUIMode & UI_MODE_ANISOTROPIC_SCALE) == 0,
                (img.getScaleX() + img.getScaleY()) / 2,
                (mUIMode & UI_MODE_ANISOTROPIC_SCALE) != 0, img.getScaleX(),
                img.getScaleY(), (mUIMode & UI_MODE_ROTATE) != 0,
                img.getAngle());
    }


    @Override
    public boolean setPositionAndScale(MultiTouchEntity img,
                                       MultiTouchController.PositionAndScale newImgPosAndScale, MultiTouchController.PointInfo touchPoint) {
        currTouchPoint.set(touchPoint);
        boolean ok = ((ImageEntity) img).setPos(newImgPosAndScale);
        if (ok)
            invalidate();
        return ok;
    }

    @Override
    public boolean pointInObjectGrabArea(MultiTouchController.PointInfo pt, MultiTouchEntity img) {
        return false;
    }
}
