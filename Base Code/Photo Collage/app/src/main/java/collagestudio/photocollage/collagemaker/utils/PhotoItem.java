package collagestudio.photocollage.collagemaker.utils;

import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.HashMap;


public class PhotoItem {
    public static final int SHRINK_METHOD_DEFAULT = 0;
    public static final int SHRINK_METHOD_3_3 = 1;
    public static final int SHRINK_METHOD_USING_MAP = 2;
    public static final int SHRINK_METHOD_3_6 = 3;
    public static final int SHRINK_METHOD_3_8 = 4;
    public static final int SHRINK_METHOD_COMMON = 5;
    public static final int CORNER_METHOD_DEFAULT = 0;
    public static final int CORNER_METHOD_3_6 = 1;
    public static final int CORNER_METHOD_3_13 = 2;
    
    public float x = 0;
    public float y = 0;
    public int index = 0;
    public String imagePath;
    public String maskPath;
    
    public ArrayList<PointF> pointList = new ArrayList<>();
    public RectF bound = new RectF();
    
    public Path path = null;
    public RectF pathRatioBound = null;
    public boolean pathInCenterHorizontal = false;
    public boolean pathInCenterVertical = false;
    public boolean pathAlignParentRight = false;
    public float pathScaleRatio = 1;
    public boolean fitBound = false;
    
    public boolean hasBackground = false;
    public int shrinkMethod = SHRINK_METHOD_DEFAULT;
    public int cornerMethod = CORNER_METHOD_DEFAULT;
    public boolean disableShrink = false;
    public HashMap<PointF, PointF> shrinkMap;
    
    public ArrayList<PointF> clearAreaPoints;
    
    public Path clearPath = null;
    public RectF clearPathRatioBound = null;
    public boolean clearPathInCenterHorizontal = false;
    public boolean clearPathInCenterVertical = false;
    public boolean clearPathAlignParentRight = false;
    public float clearPathScaleRatio = 1;
    public boolean centerInClearBound = false;





}
