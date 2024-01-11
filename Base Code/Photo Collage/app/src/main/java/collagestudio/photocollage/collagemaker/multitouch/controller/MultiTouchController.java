package collagestudio.photocollage.collagemaker.multitouch.controller;

import java.lang.reflect.Method;

import android.util.Log;
import android.view.MotionEvent;


public class MultiTouchController<T> {

	
	private static final long EVENT_SETTLE_TIME_INTERVAL = 20;

	
	private static final float MAX_MULTITOUCH_POS_JUMP_SIZE = 30.0f;

	
	private static final float MAX_MULTITOUCH_DIM_JUMP_SIZE = 40.0f;

	
	private static final float MIN_MULTITOUCH_SEPARATION = 30.0f;

	
	private static final float THRESHOLD = 3.0f;

	
	public static final int MAX_TOUCH_POINTS = 20;

	
	public static final boolean DEBUG = false;

	

	MultiTouchObjectCanvas<T> objectCanvas;

	
	private PointInfo mCurrPt;

	
	private PointInfo mPrevPt;

	
	private float mCurrPtX, mCurrPtY, mCurrPtDiam, mCurrPtWidth, mCurrPtHeight,
			mCurrPtAng;

	
	private void extractCurrPtInfo() {
		
		
		
		
		mCurrPtX = mCurrPt.getX();
		mCurrPtY = mCurrPt.getY();
		mCurrPtDiam = Math.max(
				MIN_MULTITOUCH_SEPARATION * .71f,
				!mCurrXform.updateScale ? 0.0f : mCurrPt
						.getMultiTouchDiameter());
		mCurrPtWidth = Math
				.max(MIN_MULTITOUCH_SEPARATION,
						!mCurrXform.updateScaleXY ? 0.0f : mCurrPt
								.getMultiTouchWidth());
		mCurrPtHeight = Math.max(
				MIN_MULTITOUCH_SEPARATION,
				!mCurrXform.updateScaleXY ? 0.0f : mCurrPt
						.getMultiTouchHeight());
		mCurrPtAng = !mCurrXform.updateAngle ? 0.0f : mCurrPt
				.getMultiTouchAngle();
	}

	

	
	private boolean handleSingleTouchEvents;

	
	private T selectedObject = null;

	
	private PositionAndScale mCurrXform = new PositionAndScale();

	
	private long mSettleStartTime, mSettleEndTime;

	
	private float startPosX, startPosY;

	
	private float startScaleOverPinchDiam, startAngleMinusPinchAngle;

	
	private float startScaleXOverPinchWidth, startScaleYOverPinchHeight;

	
	private boolean mDragOccurred = false;

	

	
	public static final int MODE_NOTHING = 0;

	
	public static final int MODE_DRAG = 1;

	
	public static final int MODE_PINCH = 2;

	public static final int MODE_ST_GRAB = 3;

	
	private int mMode = MODE_NOTHING;

	

	
	public MultiTouchController(MultiTouchObjectCanvas<T> objectCanvas) {
		this(objectCanvas, true);
	}

	
	public MultiTouchController(MultiTouchObjectCanvas<T> objectCanvas,
			boolean handleSingleTouchEvents) {
		this.mCurrPt = new PointInfo();
		this.mPrevPt = new PointInfo();
		this.handleSingleTouchEvents = handleSingleTouchEvents;
		this.objectCanvas = objectCanvas;
	}

	

	
	protected void setHandleSingleTouchEvents(boolean handleSingleTouchEvents) {
		this.handleSingleTouchEvents = handleSingleTouchEvents;
	}

	
	protected boolean getHandleSingleTouchEvents() {
		return handleSingleTouchEvents;
	}

	public boolean dragOccurred() {
		return mDragOccurred;
	}

	

	public static final boolean multiTouchSupported;
	private static Method m_getPointerCount;
	private static Method m_getPointerId;
	private static Method m_getPressure;
	private static Method m_getHistoricalX;
	private static Method m_getHistoricalY;
	private static Method m_getHistoricalPressure;
	private static Method m_getX;
	private static Method m_getY;
	private static int ACTION_POINTER_UP = 6;
	private static int ACTION_POINTER_INDEX_SHIFT = 8;

	static {
		boolean succeeded = false;
		try {
			
			m_getPointerCount = MotionEvent.class.getMethod("getPointerCount");
			m_getPointerId = MotionEvent.class.getMethod("getPointerId",
					Integer.TYPE);
			m_getPressure = MotionEvent.class.getMethod("getPressure",
					Integer.TYPE);
			m_getHistoricalX = MotionEvent.class.getMethod("getHistoricalX",
					Integer.TYPE, Integer.TYPE);
			m_getHistoricalY = MotionEvent.class.getMethod("getHistoricalY",
					Integer.TYPE, Integer.TYPE);
			m_getHistoricalPressure = MotionEvent.class.getMethod(
					"getHistoricalPressure", Integer.TYPE, Integer.TYPE);
			m_getX = MotionEvent.class.getMethod("getX", Integer.TYPE);
			m_getY = MotionEvent.class.getMethod("getY", Integer.TYPE);
			succeeded = true;
		} catch (Exception e) {
			Log.e("MultiTouchController", "static initializer failed", e);
		}
		multiTouchSupported = succeeded;
		if (multiTouchSupported) {
			
			
			
			try {
				ACTION_POINTER_UP = MotionEvent.class.getField(
						"ACTION_POINTER_UP").getInt(null);
				ACTION_POINTER_INDEX_SHIFT = MotionEvent.class.getField(
						"ACTION_POINTER_INDEX_SHIFT").getInt(null);
			} catch (Exception e) {
			}
		}
	}

	

	private static final float[] xVals = new float[MAX_TOUCH_POINTS];
	private static final float[] yVals = new float[MAX_TOUCH_POINTS];
	private static final float[] pressureVals = new float[MAX_TOUCH_POINTS];
	private static final int[] pointerIds = new int[MAX_TOUCH_POINTS];

	
	public boolean onTouchEvent(MotionEvent event) {
		try {
			int pointerCount = multiTouchSupported ? (Integer) m_getPointerCount
					.invoke(event) : 1;
			if (DEBUG)
				Log.i("MultiTouch", "Got here 1 - " + multiTouchSupported + " "
						+ mMode + " " + handleSingleTouchEvents + " "
						+ pointerCount);
			if (mMode == MODE_NOTHING && !handleSingleTouchEvents
					&& pointerCount == 1)
				
				return false;
			if (DEBUG)
				Log.i("MultiTouch", "Got here 2");

			
			
			int action = event.getAction();
			int histLen = event.getHistorySize() / pointerCount;
			for (int histIdx = 0; histIdx <= histLen; histIdx++) {
				
				
				boolean processingHist = histIdx < histLen;
				if (!multiTouchSupported || pointerCount == 1) {
					
					
					
					
					
					
					
					
					if (DEBUG)
						Log.i("MultiTouch", "Got here 3");
					xVals[0] = processingHist ? event.getHistoricalX(histIdx)
							: event.getX();
					yVals[0] = processingHist ? event.getHistoricalY(histIdx)
							: event.getY();
					pressureVals[0] = processingHist ? event
							.getHistoricalPressure(histIdx) : event
							.getPressure();
				} else {
					
					if (DEBUG)
						Log.i("MultiTouch", "Got here 4");
					int numPointers = Math.min(pointerCount, MAX_TOUCH_POINTS);
					if (DEBUG && pointerCount > MAX_TOUCH_POINTS)
						Log.i("MultiTouch",
								"Got more pointers than MAX_TOUCH_POINTS");
					for (int ptrIdx = 0; ptrIdx < numPointers; ptrIdx++) {
						int ptrId = (Integer) m_getPointerId.invoke(event,
								ptrIdx);
						pointerIds[ptrIdx] = ptrId;
						
						
						
						
						
						
						
						
						
						xVals[ptrIdx] = (Float) (processingHist ? m_getHistoricalX
								.invoke(event, ptrIdx, histIdx) : m_getX
								.invoke(event, ptrIdx));
						yVals[ptrIdx] = (Float) (processingHist ? m_getHistoricalY
								.invoke(event, ptrIdx, histIdx) : m_getY
								.invoke(event, ptrIdx));
						pressureVals[ptrIdx] = (Float) (processingHist ? m_getHistoricalPressure
								.invoke(event, ptrIdx, histIdx) : m_getPressure
								.invoke(event, ptrIdx));
					}
				}
				
				decodeTouchEvent(
						pointerCount,
						xVals,
						yVals,
						pressureVals,
						pointerIds,
						processingHist ? MotionEvent.ACTION_MOVE
								: action,
						processingHist ? true
								: action != MotionEvent.ACTION_UP
										&& (action & ((1 << ACTION_POINTER_INDEX_SHIFT) - 1)) != ACTION_POINTER_UP
										&& action != MotionEvent.ACTION_CANCEL, 
						processingHist ? event.getHistoricalEventTime(histIdx)
								: event.getEventTime());
			}
			if(selectedObject == null){
				return false;
			}else {
				return true;
			}
		} catch (Exception e) {
			
			Log.e("MultiTouchController", "onTouchEvent() failed", e);
			return false;
		}
	}

	private void decodeTouchEvent(int pointerCount, float[] x, float[] y,
			float[] pressure, int[] pointerIds, int action, boolean down,
			long eventTime) {
		if (DEBUG)
			Log.i("MultiTouch", "Got here 5 - " + pointerCount + " " + action
					+ " " + down);

		
		PointInfo tmp = mPrevPt;
		mPrevPt = mCurrPt;
		mCurrPt = tmp;
		
		mCurrPt.set(pointerCount, x, y, pressure, pointerIds, action, down,
				eventTime);
		multiTouchController();
	}

	

	
	private void anchorAtThisPositionAndScale() {
		if (DEBUG)
			Log.i("MulitTouch", "anchorAtThisPositionAndScale()");
		if (selectedObject == null)
			return;

		
		objectCanvas.getPositionAndScale(selectedObject, mCurrXform);

		
		
		
		
		
		
		float currScaleInv = 1.0f / (!mCurrXform.updateScale ? 1.0f
				: mCurrXform.scale == 0.0f ? 1.0f : mCurrXform.scale);
		extractCurrPtInfo();
		startPosX = (mCurrPtX - mCurrXform.xOff) * currScaleInv;
		startPosY = (mCurrPtY - mCurrXform.yOff) * currScaleInv;
		startScaleOverPinchDiam = mCurrXform.scale / mCurrPtDiam;
		startScaleXOverPinchWidth = mCurrXform.scaleX / mCurrPtWidth;
		startScaleYOverPinchHeight = mCurrXform.scaleY / mCurrPtHeight;
		startAngleMinusPinchAngle = mCurrXform.angle - mCurrPtAng;
	}

	
	private void performDragOrPinch() {
		
		if (selectedObject == null)
			return;

		
		float currScale = !mCurrXform.updateScale ? 1.0f
				: mCurrXform.scale == 0.0f ? 1.0f : mCurrXform.scale;
		extractCurrPtInfo();
		float newPosX = mCurrPtX - startPosX * currScale;
		float newPosY = mCurrPtY - startPosY * currScale;

		
		
		
		
		
		
		
		
		

		float deltaX = mCurrPt.getX() - mPrevPt.getX();
		float deltaY = mCurrPt.getY() - mPrevPt.getY();

		
		float newScale = mCurrXform.scale;
		if (mMode == MODE_ST_GRAB) {
			if (deltaX < 0.0f || deltaY < 0.0f) {
				newScale = mCurrXform.scale - 0.04f;
			} else {
				newScale = mCurrXform.scale + 0.04f;
			}
			if (newScale < 0.35f)
				return;
		} else {
			newScale = startScaleOverPinchDiam * mCurrPtDiam;
		}

		if (!mDragOccurred) {
			if (!pastThreshold(Math.abs(deltaX), Math.abs(deltaY), newScale)) {
				if (DEBUG) {
					Log.i("MultiTouch",
							"Change received by performDragOrPinch "
									+ "was below the threshold");
				}
				
				return;
			}
		}

		float newScaleX = startScaleXOverPinchWidth * mCurrPtWidth;
		float newScaleY = startScaleYOverPinchHeight * mCurrPtHeight;
		float newAngle = startAngleMinusPinchAngle + mCurrPtAng;

		
		
		mCurrXform.set(newPosX, newPosY, newScale, newScaleX, newScaleY,
				newAngle);

		boolean success = objectCanvas.setPositionAndScale(selectedObject,
				mCurrXform, mCurrPt);
		if (!success)
			; 
		mDragOccurred = true;
	}

	
	private boolean pastThreshold(float deltaX, float deltaY, float newScale) {
		if (deltaX < THRESHOLD && deltaY < THRESHOLD) {
			if (newScale == mCurrXform.scale) {
				mDragOccurred = false;
				return false;
			}
		}
		mDragOccurred = true;
		return true;
	}

	
	private void multiTouchController() {
		if (DEBUG)
			Log.i("MultiTouch",
					"Got here 6 - " + mMode + " " + mCurrPt.getNumTouchPoints()
							+ " " + mCurrPt.isDown() + mCurrPt.isMultiTouch());

		switch (mMode) {
		case MODE_NOTHING:
			if (DEBUG)
				Log.i("MultiTouch", "MODE_NOTHING");
			
			if (mCurrPt.isDown()) {
				
				selectedObject = objectCanvas
						.getDraggableObjectAtPoint(mCurrPt);
				if (selectedObject != null) {
					if (objectCanvas.pointInObjectGrabArea(mCurrPt,
							selectedObject)) {
						
						mMode = MODE_ST_GRAB;
						objectCanvas.selectObject(selectedObject, mCurrPt);
						anchorAtThisPositionAndScale();
						mSettleStartTime = mSettleEndTime = mCurrPt
								.getEventTime();
					} else {
						
						mMode = MODE_DRAG;
						objectCanvas.selectObject(selectedObject, mCurrPt);
						anchorAtThisPositionAndScale();
						
						
						
						mSettleStartTime = mSettleEndTime = mCurrPt
								.getEventTime();
					}
				}
			}
			break;

		case MODE_ST_GRAB:
			if (DEBUG)
				Log.i("MultiTouch", "MODE_ST_GRAB");
			
			if (!mCurrPt.isDown()) {
				
				mMode = MODE_NOTHING;
				objectCanvas.selectObject((selectedObject = null), mCurrPt);
				mDragOccurred = false;
			} else {
				
				performDragOrPinch();
			}
			break;

		case MODE_DRAG:
			if (DEBUG)
				Log.i("MultiTouch", "MODE_DRAG");
			
			if (!mCurrPt.isDown()) {
				
				mMode = MODE_NOTHING;
				objectCanvas.selectObject((selectedObject = null), mCurrPt);
				mDragOccurred = false;
			} else if (mCurrPt.isMultiTouch()) {
				
				mMode = MODE_PINCH;
				
				
				anchorAtThisPositionAndScale();
				
				
				mSettleStartTime = mCurrPt.getEventTime();
				mSettleEndTime = mSettleStartTime + EVENT_SETTLE_TIME_INTERVAL;

			} else {
				
				
				if (mCurrPt.getEventTime() < mSettleEndTime) {
					
					
					
					
					
					anchorAtThisPositionAndScale();
				} else {
					
					performDragOrPinch();
				}
			}
			break;

		case MODE_PINCH:
			if (DEBUG)
				Log.i("MultiTouch", "MODE_PINCH");
			
			if (!mCurrPt.isMultiTouch() || !mCurrPt.isDown()) {
				

				if (!mCurrPt.isDown()) {
					
					mMode = MODE_NOTHING;
					objectCanvas.selectObject((selectedObject = null), mCurrPt);

				} else {
					
					mMode = MODE_DRAG;
					
					anchorAtThisPositionAndScale();
					
					
					mSettleStartTime = mCurrPt.getEventTime();
					mSettleEndTime = mSettleStartTime
							+ EVENT_SETTLE_TIME_INTERVAL;
				}

			} else {
				
				if (Math.abs(mCurrPt.getX() - mPrevPt.getX()) > MAX_MULTITOUCH_POS_JUMP_SIZE
						|| Math.abs(mCurrPt.getY() - mPrevPt.getY()) > MAX_MULTITOUCH_POS_JUMP_SIZE
						|| Math.abs(mCurrPt.getMultiTouchWidth()
								- mPrevPt.getMultiTouchWidth()) * .5f > MAX_MULTITOUCH_DIM_JUMP_SIZE
						|| Math.abs(mCurrPt.getMultiTouchHeight()
								- mPrevPt.getMultiTouchHeight()) * .5f > MAX_MULTITOUCH_DIM_JUMP_SIZE) {
					
					
					
					anchorAtThisPositionAndScale();
					mSettleStartTime = mCurrPt.getEventTime();
					mSettleEndTime = mSettleStartTime
							+ EVENT_SETTLE_TIME_INTERVAL;

				} else if (mCurrPt.eventTime < mSettleEndTime) {
					
					anchorAtThisPositionAndScale();
				} else {
					
					performDragOrPinch();
				}
			}
			break;
		}
		if (DEBUG)
			Log.i("MultiTouch",
					"Got here 7 - " + mMode + " " + mCurrPt.getNumTouchPoints()
							+ " " + mCurrPt.isDown() + mCurrPt.isMultiTouch());
	}

	public int getMode() {
		return mMode;
	}

	

	
	public static class PointInfo {
		
		private int numPoints;
		private float[] xs = new float[MAX_TOUCH_POINTS];
		private float[] ys = new float[MAX_TOUCH_POINTS];
		private float[] pressures = new float[MAX_TOUCH_POINTS];
		private int[] pointerIds = new int[MAX_TOUCH_POINTS];

		
		private float xMid, yMid, pressureMid;

		
		private float dx, dy, diameter, diameterSq, angle;

		
		
		private boolean isDown, isMultiTouch;

		
		
		private boolean diameterSqIsCalculated, diameterIsCalculated,
				angleIsCalculated;

		
		private int action;
		private long eventTime;

		

		
		private void set(int numPoints, float[] x, float[] y, float[] pressure,
				int[] pointerIds, int action, boolean isDown, long eventTime) {
			if (DEBUG)
				Log.i("MultiTouch", "Got here 8 - " + +numPoints + " " + x[0]
						+ " " + y[0] + " " + (numPoints > 1 ? x[1] : x[0])
						+ " " + (numPoints > 1 ? y[1] : y[0]) + " " + action
						+ " " + isDown);
			this.eventTime = eventTime;
			this.action = action;
			this.numPoints = numPoints;
			for (int i = 0; i < numPoints; i++) {
				this.xs[i] = x[i];
				this.ys[i] = y[i];
				this.pressures[i] = pressure[i];
				this.pointerIds[i] = pointerIds[i];
			}
			this.isDown = isDown;
			this.isMultiTouch = numPoints >= 2;

			if (isMultiTouch) {
				xMid = (x[0] + x[1]) * .5f;
				yMid = (y[0] + y[1]) * .5f;
				pressureMid = (pressure[0] + pressure[1]) * .5f;
				dx = Math.abs(x[1] - x[0]);
				dy = Math.abs(y[1] - y[0]);

			} else {
				
				xMid = x[0];
				yMid = y[0];
				pressureMid = pressure[0];
				dx = dy = 0.0f;
			}
			
			diameterSqIsCalculated = diameterIsCalculated = angleIsCalculated = false;
		}

		
		public void set(PointInfo other) {
			this.numPoints = other.numPoints;
			for (int i = 0; i < numPoints; i++) {
				this.xs[i] = other.xs[i];
				this.ys[i] = other.ys[i];
				this.pressures[i] = other.pressures[i];
				this.pointerIds[i] = other.pointerIds[i];
			}
			this.xMid = other.xMid;
			this.yMid = other.yMid;
			this.pressureMid = other.pressureMid;
			this.dx = other.dx;
			this.dy = other.dy;
			this.diameter = other.diameter;
			this.diameterSq = other.diameterSq;
			this.angle = other.angle;
			this.isDown = other.isDown;
			this.action = other.action;
			this.isMultiTouch = other.isMultiTouch;
			this.diameterIsCalculated = other.diameterIsCalculated;
			this.diameterSqIsCalculated = other.diameterSqIsCalculated;
			this.angleIsCalculated = other.angleIsCalculated;
			this.eventTime = other.eventTime;
		}

		

		
		public boolean isMultiTouch() {
			return isMultiTouch;
		}

		
		public float getMultiTouchWidth() {
			return isMultiTouch ? dx : 0.0f;
		}

		
		public float getMultiTouchHeight() {
			return isMultiTouch ? dy : 0.0f;
		}

		
		private int julery_isqrt(int val) {
			int temp, g = 0, b = 0x8000, bshft = 15;
			do {
				if (val >= (temp = (((g << 1) + b) << bshft--))) {
					g += b;
					val -= temp;
				}
			} while ((b >>= 1) > 0);
			return g;
		}

		
		public float getMultiTouchDiameterSq() {
			if (!diameterSqIsCalculated) {
				diameterSq = (isMultiTouch ? dx * dx + dy * dy : 0.0f);
				diameterSqIsCalculated = true;
			}
			return diameterSq;
		}

		
		public float getMultiTouchDiameter() {
			if (!diameterIsCalculated) {
				if (!isMultiTouch) {
					diameter = 0.0f;
				} else {
					
					
					
					
					
					float diamSq = getMultiTouchDiameterSq();
					diameter = (diamSq == 0.0f ? 0.0f
							: (float) julery_isqrt((int) (256 * diamSq)) / 16.0f);
					
					
					if (diameter < dx)
						diameter = dx;
					if (diameter < dy)
						diameter = dy;
				}
				diameterIsCalculated = true;
			}
			return diameter;
		}

		
		public float getMultiTouchAngle() {
			if (!angleIsCalculated) {
				if (!isMultiTouch)
					angle = 0.0f;
				else
					angle = (float) Math.atan2(ys[1] - ys[0], xs[1] - xs[0]);
				angleIsCalculated = true;
			}
			return angle;
		}

		

		
		public int getNumTouchPoints() {
			return numPoints;
		}

		
		public float getX() {
			return xMid;
		}

		
		public float[] getXs() {
			return xs;
		}

		
		public float getY() {
			return yMid;
		}

		
		public float[] getYs() {
			return ys;
		}

		
		public int[] getPointerIds() {
			return pointerIds;
		}

		
		public float getPressure() {
			return pressureMid;
		}

		
		public float[] getPressures() {
			return pressures;
		}

		

		public boolean isDown() {
			return isDown;
		}

		public int getAction() {
			return action;
		}

		public long getEventTime() {
			return eventTime;
		}
	}

	

	
	public static class PositionAndScale {
		private float xOff, yOff, scale, scaleX, scaleY, angle;
		private boolean updateScale, updateScaleXY, updateAngle;

		
		public void set(float xOff, float yOff, boolean updateScale,
				float scale, boolean updateScaleXY, float scaleX, float scaleY,
				boolean updateAngle, float angle) {
			this.xOff = xOff;
			this.yOff = yOff;
			this.updateScale = updateScale;
			this.scale = scale == 0.0f ? 1.0f : scale;
			this.updateScaleXY = updateScaleXY;
			this.scaleX = scaleX == 0.0f ? 1.0f : scaleX;
			this.scaleY = scaleY == 0.0f ? 1.0f : scaleY;
			this.updateAngle = updateAngle;
			this.angle = angle;
		}

		
		protected void set(float xOff, float yOff, float scale, float scaleX,
				float scaleY, float angle) {
			this.xOff = xOff;
			this.yOff = yOff;
			this.scale = scale == 0.0f ? 1.0f : scale;
			this.scaleX = scaleX == 0.0f ? 1.0f : scaleX;
			this.scaleY = scaleY == 0.0f ? 1.0f : scaleY;
			this.angle = angle;
		}

		public float getXOff() {
			return xOff;
		}

		public float getYOff() {
			return yOff;
		}

		public float getScale() {
			return !updateScale ? 1.0f : scale;
		}

		
		public float getScaleX() {
			return !updateScaleXY ? 1.0f : scaleX;
		}

		
		public float getScaleY() {
			return !updateScaleXY ? 1.0f : scaleY;
		}

		public float getAngle() {
			return !updateAngle ? 0.0f : angle;
		}
	}

	

	public static interface MultiTouchObjectCanvas<T> {

		
		public T getDraggableObjectAtPoint(PointInfo touchPoint);

		
		public boolean pointInObjectGrabArea(PointInfo touchPoint, T obj);

		
		public void getPositionAndScale(T obj,
				PositionAndScale objPosAndScaleOut);

		
		public boolean setPositionAndScale(T obj,
				PositionAndScale newObjPosAndScale, PointInfo touchPoint);

		
		public void selectObject(T obj, PointInfo touchPoint);
	}
}
