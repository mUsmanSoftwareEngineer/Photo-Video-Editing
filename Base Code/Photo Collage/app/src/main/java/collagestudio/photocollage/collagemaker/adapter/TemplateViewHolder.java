package collagestudio.photocollage.collagemaker.adapter;



import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import collagestudio.photocollage.collagemaker.R;
import collagestudio.photocollage.collagemaker.model.TemplateItem;
import collagestudio.photocollage.collagemaker.utils.ImageUtils;


public class TemplateViewHolder extends RecyclerView.ViewHolder {
    public interface OnTemplateItemClickListener {
        void onTemplateItemClick(final TemplateItem item);
    }

    private ImageView mImageView;
    private TextView mTextView;
    private int mViewType = TemplateAdapter.VIEW_TYPE_CONTENT;

    TemplateViewHolder(View view, int viewType) {
        super(view);
        mViewType = viewType;
        mTextView = (TextView) view.findViewById(R.id.text);
        mImageView = (ImageView) view.findViewById(R.id.imageView);
    }

    public void bindItem(final TemplateItem item, final OnTemplateItemClickListener listener) {
        if (mViewType == TemplateAdapter.VIEW_TYPE_HEADER && mTextView != null) {
            mTextView.setText(item.getHeader()+"-");
        } else if (mImageView != null) {
            Log.d("employe4747", "bindItem: "+item.getPreview()+" , id: "+item.getId());
            ImageUtils.loadImageWithPicasso(mImageView.getContext(), mImageView, item.getPreview());
            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onTemplateItemClick(item);
                    }
                }
            });
        }
    }
}