package collagestudio.photocollage.collagemaker.model;

import java.util.ArrayList;
import java.util.List;


public class GalleryAlbum {
    private String mAlbumName;
    private long mAlbumId;
    private String mTakenDate;
    private List<String> mImageList = new ArrayList<>();

    public GalleryAlbum(long albumId, String albumName){
        mAlbumId = albumId;
        mAlbumName = albumName;
    }

    public void setAlbumId(long albumId) {
        mAlbumId = albumId;
    }

    public void setAlbumName(String albumName) {
        mAlbumName = albumName;
    }

    public void setTakenDate(String takenDate) {
        mTakenDate = takenDate;
    }

    public List<String> getImageList() {
        return mImageList;
    }

    public long getAlbumId() {
        return mAlbumId;
    }

    public String getAlbumName() {
        return mAlbumName;
    }

    public String getTakenDate() {
        return mTakenDate;
    }
}
