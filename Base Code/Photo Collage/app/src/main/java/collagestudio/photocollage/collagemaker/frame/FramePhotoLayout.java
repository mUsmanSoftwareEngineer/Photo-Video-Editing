package collagestudio.photocollage.collagemaker.frame;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.RelativeLayout;

import collagestudio.photocollage.collagemaker.R;
import collagestudio.photocollage.collagemaker.utils.ALog;
import collagestudio.photocollage.collagemaker.quickaction.QuickAction;
import collagestudio.photocollage.collagemaker.quickaction.QuickActionItem;
import collagestudio.photocollage.collagemaker.utils.PhotoItem;
import collagestudio.photocollage.collagemaker.utils.ImageDecoder;
import collagestudio.photocollage.collagemaker.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;


public class FramePhotoLayout extends RelativeLayout implements FrameImageView.OnImageClickListener {
    private static final String TAG = FramePhotoLayout.class.getSimpleName();
    PhotoTapListner photoTapListner;
    public interface PhotoTapListner {
        public void onPhotoTapListner(FrameImageView view);
        public void onPhotoDoubleTapListner(FrameImageView view);
    }

    public interface OnQuickActionClickListener {
        void onEditActionClick(FrameImageView v);

        void onChangeActionClick(FrameImageView v);
        void onDeleteActionClick(FrameImageView v);

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
                    ALog.i("Drag Event", "Dropped: x=" + event.getX() + ", y=" + event.getY());
                    FrameImageView target = (FrameImageView) v;
                    FrameImageView selectedView = getSelectedFrameImageView(target, event);
                    if (selectedView != null) {
                        target = selectedView;
                        FrameImageView dragged = (FrameImageView) event.getLocalState();
                        if (target.getPhotoItem() != null && dragged.getPhotoItem() != null) {
                            String targetPath = target.getPhotoItem().imagePath;
                            String draggedPath = dragged.getPhotoItem().imagePath;
                            if (targetPath == null) targetPath = "";
                            if (draggedPath == null) draggedPath = "";
                            if (!targetPath.equals(draggedPath))
                                target.swapImage(dragged);
                        }
                    }
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

    private List<PhotoItem> mPhotoItems;
    private List<FrameImageView> mItemImageViews;
    private int mViewWidth, mViewHeight;
    private float mOutputScaleRatio = 1;
    private OnQuickActionClickListener mQuickActionClickListener;

    public FramePhotoLayout(Context context, List<PhotoItem> photoItems,PhotoTapListner listner) {
        super(context);
        mItemImageViews = new ArrayList<>();
        setLayerType(LAYER_TYPE_HARDWARE, null);
        createQuickAction();
        mPhotoItems = photoItems;
        photoTapListner = listner;
    }
    public FramePhotoLayout(Context context, List<PhotoItem> photoItems) {
        super(context);
        mItemImageViews = new ArrayList<>();
        setLayerType(LAYER_TYPE_HARDWARE, null);
        createQuickAction();
        mPhotoItems = photoItems;
    }

    private FrameImageView getSelectedFrameImageView(FrameImageView target, DragEvent event) {
        ALog.d(TAG, "getSelectedFrameImageView");
        FrameImageView dragged = (FrameImageView) event.getLocalState();
        int leftMargin = (int) (mViewWidth * target.getPhotoItem().bound.left);
        int topMargin = (int) (mViewHeight * target.getPhotoItem().bound.top);
        final float globalX = leftMargin + event.getX();
        final float globalY = topMargin + event.getY();
        for (int idx = mItemImageViews.size() - 1; idx >= 0; idx--) {
            FrameImageView view = mItemImageViews.get(idx);
            float x = globalX - mViewWidth * view.getPhotoItem().bound.left;
            float y = globalY - mViewHeight * view.getPhotoItem().bound.top;
            if (view.isSelected(x, y)) {
                if (view == dragged) {
                    return null;
                } else {
                    return view;
                }
            }
        }
        return null;
    }

    public void saveInstanceState(Bundle outState) {
        if (mItemImageViews != null)
            for (FrameImageView view : mItemImageViews)
                view.saveInstanceState(outState);
    }

    public void restoreInstanceState(Bundle savedInstanceState) {
        if (mItemImageViews != null)
            for (FrameImageView view : mItemImageViews)
                view.restoreInstanceState(savedInstanceState);
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
                    //FrameImageView v = (FrameImageView) mQuickAction.getAnchorView();
                    if (mQuickActionClickListener != null) {
                        mQuickActionClickListener.onDeleteActionClick((FrameImageView) mQuickAction.getAnchorView());
                    }
//                    v.clearMainImage();
                } else if (actionId == ID_EDIT) {
                    if (mQuickActionClickListener != null) {
                        mQuickActionClickListener.onEditActionClick((FrameImageView) mQuickAction.getAnchorView());
                    }
                } else if (actionId == ID_CHANGE) {
                    if (mQuickActionClickListener != null) {
                        mQuickActionClickListener.onChangeActionClick((FrameImageView) mQuickAction.getAnchorView());
                    }
                }
            }
        });

        
        
        mQuickAction.setOnDismissListener(new QuickAction.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });

    }

    private boolean isNotLargeThan1Gb() {
        ImageUtils.MemoryInfo memoryInfo = ImageUtils.getMemoryInfo(getContext());
        if (memoryInfo.totalMem > 0 && (memoryInfo.totalMem / 1048576.0 <= 1024)) {
            return true;
        } else {
            return false;
        }
    }

    public void build(final int viewWidth, final int viewHeight, final float outputScaleRatio, final float space, final float corner) {
        if (viewWidth < 1 || viewHeight < 1) {
            return;
        }
        
        mViewWidth = viewWidth;
        mViewHeight = viewHeight;
        mOutputScaleRatio = outputScaleRatio;
        mItemImageViews.clear();
        
        if (mPhotoItems.size() > 4 || isNotLargeThan1Gb()) {
            ImageDecoder.SAMPLER_SIZE = 256;
        } else {
            ImageDecoder.SAMPLER_SIZE = 512;
        }
        ALog.d(TAG, "build, SAMPLER_SIZE = " + ImageDecoder.SAMPLER_SIZE);
        for (PhotoItem item : mPhotoItems) {
            FrameImageView imageView = addPhotoItemView(item, mOutputScaleRatio, space, corner);
            mItemImageViews.add(imageView);
        }
    }

    public void build(final int viewWidth, final int viewHeight, final float outputScaleRatio) {
        build(viewWidth, viewHeight, outputScaleRatio, 0, 0);
    }

    public void setSpace(float space, float corner) {
        for (FrameImageView img : mItemImageViews)
            img.setSpace(space, corner);
    }

    private FrameImageView addPhotoItemView(PhotoItem item, float outputScaleRatio, final float space, final float corner) {
        final FrameImageView imageView = new FrameImageView(getContext(), item);
        int leftMargin = (int) (mViewWidth * item.bound.left);
        int topMargin = (int) (mViewHeight * item.bound.top);
        int frameWidth = 0, frameHeight = 0;
        if (item.bound.right == 1) {
            frameWidth = mViewWidth - leftMargin;
        } else {
            frameWidth = (int) (mViewWidth * item.bound.width() + 0.5f);
        }

        if (item.bound.bottom == 1) {
            frameHeight = mViewHeight - topMargin;
        } else {
            frameHeight = (int) (mViewHeight * item.bound.height() + 0.5f);
        }

        imageView.init(frameWidth, frameHeight, outputScaleRatio, space, corner);
        imageView.setOnImageClickListener(this);
        if (mPhotoItems.size() > 1)
            imageView.setOnDragListener(mOnDragListener);

        LayoutParams params = new LayoutParams(frameWidth, frameHeight);
        params.leftMargin = leftMargin;
        params.topMargin = topMargin;
        imageView.setOriginalLayoutParams(params);
        imageView.setForeground(getResources().getDrawable(R.drawable.rec1));
        addView(imageView, params);
        return imageView;
    }

    public Bitmap createImage() throws OutOfMemoryError {
        try {
            Bitmap template = Bitmap.createBitmap((int) (mOutputScaleRatio * mViewWidth), (int) (mOutputScaleRatio * mViewHeight), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(template);
            for (FrameImageView view : mItemImageViews)
                if (view.getImage() != null && !view.getImage().isRecycled()) {
                    final int left = (int) (view.getLeft() * mOutputScaleRatio);
                    final int top = (int) (view.getTop() * mOutputScaleRatio);
                    final int width = (int) (view.getWidth() * mOutputScaleRatio);
                    final int height = (int) (view.getHeight() * mOutputScaleRatio);
                    
                    canvas.saveLayer(left, top, left + width, top + height, new Paint(), Canvas.ALL_SAVE_FLAG);
                    canvas.translate(left, top);
                    canvas.clipRect(0, 0, width, height);
                    view.drawOutputImage(canvas);

                    canvas.restore();
                }

            return template;
        } catch (OutOfMemoryError error) {
            throw error;
        }
    }

    public void recycleImages() {
        ALog.d(TAG, "recycleImages");
        for (FrameImageView view : mItemImageViews) {
            view.recycleImage();
        }
        System.gc();
    }

    @Override
    public void onLongClickImage(FrameImageView v) {
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
    public void onDoubleClickImage(FrameImageView v) {


            photoTapListner.onPhotoDoubleTapListner(v);

           // PointF center = v.getCenterPolygon();
          //  if (center == null) center = new PointF(v.getWidth() / 2.0f, v.getHeight() / 2.0f);
           // mQuickAction.show(v, (int) center.x, (int) center.y);
           //mQuickAction.setAnimStyle(QuickAction.ANIM_REFLECT);

    }

    @Override
    public void onSingleClickImage(FrameImageView v) {
        if ((v.getImage() == null ) && mQuickActionClickListener != null) {
            mQuickActionClickListener.onChangeActionClick((FrameImageView) mQuickAction.getAnchorView());
        } else {
            for (int i = 0; i < mItemImageViews.size(); i++) {
                if (mItemImageViews.get(i).equals(v)) {
                    //mItemImageViews.get(i).setForeground(getResources().getDrawable(R.drawable.rec));
                    try {
                        mItemImageViews.get(i).setForeground(getResources().getDrawable(R.drawable.rec));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        mItemImageViews.get(i).setForeground(getResources().getDrawable(R.drawable.rec1));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            photoTapListner.onPhotoTapListner(v);
        }
    }
}
