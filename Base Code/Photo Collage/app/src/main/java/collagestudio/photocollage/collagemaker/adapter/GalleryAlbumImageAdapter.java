package collagestudio.photocollage.collagemaker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

import collagestudio.photocollage.collagemaker.R;

import java.util.List;

import dauroi.photoeditor.utils.PhotoUtils;


public class GalleryAlbumImageAdapter extends ArrayAdapter<String> {
    private LayoutInflater mInflater;
    private boolean mImageFitCenter = false;

    public GalleryAlbumImageAdapter(Context context, List<String> objects) {
        super(context, R.layout.item_gallery_photo, objects);
        mInflater = LayoutInflater.from(context);
    }

    public void setImageFitCenter(boolean imageFitCenter) {
        mImageFitCenter = imageFitCenter;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_gallery_photo, parent, false);
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            holder.root = (FrameLayout) convertView.findViewById(R.id.root);
            if(mImageFitCenter){
                holder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PhotoUtils.loadImageWithGlide(getContext(), holder.imageView, getItem(position));
        return convertView;
    }

    private class ViewHolder {
        ImageView imageView;
        FrameLayout root;
    }
}
