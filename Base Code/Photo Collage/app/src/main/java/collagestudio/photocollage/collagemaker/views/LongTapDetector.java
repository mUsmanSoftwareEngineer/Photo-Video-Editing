package collagestudio.photocollage.collagemaker.views;

import android.os.Handler;
import android.view.MotionEvent;

import collagestudio.photocollage.collagemaker.utils.ALog;


public class LongTapDetector {
    private static final String TAG = LongTapDetector.class.getSimpleName();

    public static interface OnLongClickDetected {
        void onDetected();
    }

    final Handler longPressHandler = new Handler();
    Runnable longPressedRunnable = new Runnable() {
        public void run() {
            ALog.e(TAG, "Long press detected in long press Handler!");
            isLongPressHandlerActivated = true;
            if (mDetector != null) {
                mDetector.onDetected();
            }
        }
    };

    private OnLongClickDetected mDetector;
    private boolean isLongPressHandlerActivated = false;

    private boolean isActionMoveEventStored = false;
    private float lastActionMoveEventBeforeUpX;
    private float lastActionMoveEventBeforeUpY;
    private float mTouchAreaInterval = 10;

    public LongTapDetector(OnLongClickDetected detector) {
        mDetector = detector;
    }

    public void setTouchAreaInterval(float touchAreaInterval) {
        mTouchAreaInterval = touchAreaInterval;
    }

    public void dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            longPressHandler.postDelayed(longPressedRunnable, android.view.ViewConfiguration.getLongPressTimeout());
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_HOVER_MOVE) {
            if (!isActionMoveEventStored) {
                isActionMoveEventStored = true;
                lastActionMoveEventBeforeUpX = event.getX();
                lastActionMoveEventBeforeUpY = event.getY();
            } else {
                float currentX = event.getX();
                float currentY = event.getY();
                float firstX = lastActionMoveEventBeforeUpX;
                float firstY = lastActionMoveEventBeforeUpY;
                double distance = Math.sqrt(
                        (currentY - firstY) * (currentY - firstY) + ((currentX - firstX) * (currentX - firstX)));
                if (distance > mTouchAreaInterval) {
                    longPressHandler.removeCallbacks(longPressedRunnable);
                }
            }
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            isActionMoveEventStored = false;
            longPressHandler.removeCallbacks(longPressedRunnable);
            if (isLongPressHandlerActivated) {
                ALog.d(TAG, "Long Press detected; halting propagation of motion event");
                isLongPressHandlerActivated = false;
            }
        }
    }

}
