package collagestudio.photocollage.collagemaker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import collagestudio.photocollage.collagemaker.R;
import collagestudio.photocollage.collagemaker.model.FontItem;
import collagestudio.photocollage.collagemaker.utils.TextUtils;

import java.util.List;


public class FontAdapter extends ArrayAdapter<FontItem> {
    private LayoutInflater mInflater;

    public FontAdapter(Context context, List<FontItem> objects) {
        super(context, R.layout.item_font, objects);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        final FontItem item = getItem(position);
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_font, parent, false);
            holder.textView = (TextView) convertView.findViewById(R.id.fontView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textView.setText(item.getFontName());
        holder.textView.setTypeface(TextUtils.loadTypeface(getContext(), item.getFontPath()));

        return convertView;
    }

    private class ViewHolder {
        TextView textView;
    }
}
