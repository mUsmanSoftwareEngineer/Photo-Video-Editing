package collagestudio.photocollage.collagemaker.frame;

import collagestudio.photocollage.collagemaker.listener.OnFrameTouchListener;

public abstract class FrameTouch implements OnFrameTouchListener {
	private boolean mImageFrameMoving = false;

	public void setImageFrameMoving(boolean imageFrameMoving) {
		mImageFrameMoving = imageFrameMoving;
	}

	public boolean isImageFrameMoving() {
		return mImageFrameMoving;
	}

}
