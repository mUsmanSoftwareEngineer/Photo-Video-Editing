package collagestudio.photocollage.collagemaker.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import collagestudio.photocollage.collagemaker.R;
import collagestudio.photocollage.collagemaker.adapter.GalleryAlbumImageAdapter;

import java.util.ArrayList;


public class GalleryAlbumImageFragment extends BaseFragment {
    public interface OnSelectImageListener {
        void onSelectImage(String image);
    }

    public static final String ALBUM_IMAGE_EXTRA = "albumImage";
    public static final String ALBUM_NAME_EXTRA = "albumName";

    private GridView mGridView;
    private ArrayList<String> mImages;
    private OnSelectImageListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof OnSelectImageListener) {
            mListener = (OnSelectImageListener) getActivity();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_gallery_photo, container, false);
        mGridView = (GridView) view.findViewById(R.id.gridView);
        String albumName = getString(R.string.album_image);
        if (getArguments() != null) {
            mImages = getArguments().getStringArrayList(ALBUM_IMAGE_EXTRA);
            albumName = getArguments().getString(ALBUM_NAME_EXTRA);
            if (mImages != null) {
                GalleryAlbumImageAdapter adapter = new GalleryAlbumImageAdapter(getActivity(), mImages);
                mGridView.setAdapter(adapter);
                mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if(mListener != null){
                            mListener.onSelectImage(mImages.get(position));
                            //view.setForeground(getResources().getDrawable(R.drawable.selected_fg));
                        }
                    }
                });
            }
        }
        setTitle(albumName);
        return view;
    }
}
