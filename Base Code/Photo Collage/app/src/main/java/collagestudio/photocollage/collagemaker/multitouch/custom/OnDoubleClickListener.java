package collagestudio.photocollage.collagemaker.multitouch.custom;

import collagestudio.photocollage.collagemaker.multitouch.controller.MultiTouchEntity;

public interface OnDoubleClickListener {
	public void onPhotoViewDoubleClick(PhotoView view, MultiTouchEntity entity);
	public void onBackgroundDoubleClick();
}
