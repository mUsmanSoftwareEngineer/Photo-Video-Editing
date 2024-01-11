package collagestudio.photocollage.collagemaker.listener;

import dauroi.photoeditor.model.ItemPackageInfo;


public interface OnDownloadedPackageClickListener {
    void onDeleteButtonClick(int position, ItemPackageInfo info);

    void onItemClick(int position, ItemPackageInfo info);
}
