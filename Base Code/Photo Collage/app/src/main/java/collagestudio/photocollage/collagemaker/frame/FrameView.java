package collagestudio.photocollage.collagemaker.frame;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

public class FrameView extends FrameLayout {
    public FrameView(Context context, View view) {
        super(context);
        addView(view);
    }
}
