package collagestudio.photocollage.collagemaker.activity;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import collagestudio.photocollage.collagemaker.R;
import collagestudio.photocollage.collagemaker.adapter.SelectedPhotoAdapter;
import collagestudio.photocollage.collagemaker.fragment.BaseFragment;
import collagestudio.photocollage.collagemaker.fragment.DownloadedPackageFragment;
import collagestudio.photocollage.collagemaker.fragment.GalleryAlbumImageFragment;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.util.ArrayList;

import dauroi.photoeditor.database.table.ItemPackageTable;


public class DownloadedPackageActivity extends AdsFragmentActivity implements SelectedPhotoAdapter.OnDeleteButtonClickListener,
        GalleryAlbumImageFragment.OnSelectImageListener {
    public static final String EXTRA_IMAGE_COUNT = "imageCount";
    public static final String EXTRA_IS_MAX_IMAGE_COUNT = "isMaxImageCount";
    public static final String EXTRA_SELECTED_IMAGES = "selectedImages";

    private RecyclerView mRecyclerView;
    private TextView doneBtn,mImageCountView;

    private ArrayList<String> mSelectedImages = new ArrayList<>();
    private SelectedPhotoAdapter mSelectedPhotoAdapter;
    private int mNeededImageCount = 0;
    private boolean mIsMaxImageCount = false;
    
    private String mFormattedText;
    private String mFormattedWarningText;
    private String mPackageType = ItemPackageTable.BACKGROUND_TYPE;
    TextView title,back;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_photo);
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.app_name);
            //sab ko Allah hi ki taraf loat kr jana hy beshakk
        }

        doneBtn = findViewById(R.id.action_done);

        mNeededImageCount = getIntent().getIntExtra(EXTRA_IMAGE_COUNT, 0);
        mIsMaxImageCount = getIntent().getBooleanExtra(EXTRA_IS_MAX_IMAGE_COUNT, false);
        mPackageType = getIntent().getStringExtra(DownloadedPackageFragment.EXTRA_PACKAGE_TYPE);
        
        if (ItemPackageTable.BACKGROUND_TYPE.equals(mPackageType)) {
            mNeededImageCount = 1;
            mIsMaxImageCount = false;
        } else {
            mNeededImageCount = BaseFragment.MAX_NEEDED_PHOTOS;
            mIsMaxImageCount = true;
        }

        //addNativeAdView();

        mRecyclerView = (RecyclerView) findViewById(R.id.selectedImageRecyclerView);
        mImageCountView = (TextView) findViewById(R.id.imageCountView);
        title = findViewById(R.id.title);
        back = findViewById(R.id.iv_back);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        
        if (!mIsMaxImageCount) {
            mFormattedText = String.format(getString(R.string.please_select_photo), mNeededImageCount);
        } else {
            mFormattedText = getString(R.string.please_select_photo_without_counting);
        }
        mImageCountView.setText(mFormattedText.concat("(0)"));
        mFormattedWarningText = String.format(getString(R.string.you_need_photo), mNeededImageCount);
        
        
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mSelectedPhotoAdapter = new SelectedPhotoAdapter(mSelectedImages, this);
        if (ItemPackageTable.STICKER_TYPE.equals(mPackageType)) {
            mSelectedPhotoAdapter.setImageFitCenter(true);
            title.setText("Choose Stickers");
        } else {
            mSelectedPhotoAdapter.setImageFitCenter(false);
        }

        mRecyclerView.setAdapter(mSelectedPhotoAdapter);

        DownloadedPackageFragment fragment = new DownloadedPackageFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DownloadedPackageFragment.EXTRA_PACKAGE_TYPE, mPackageType);
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.frame_container, fragment).commit();
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectedImages.size() < mNeededImageCount && !mIsMaxImageCount) {
                    Toast.makeText(DownloadedPackageActivity.this, mFormattedText, Toast.LENGTH_SHORT).show();
                } else {
                    Intent data = new Intent();
                    data.putExtra(EXTRA_SELECTED_IMAGES, mSelectedImages);
                    setResult(RESULT_OK, data);

                    if (mSelectedImages != null && mSelectedImages.size() > 0) {
                        for (String str : mSelectedImages) {
                            if (mPackageType != null && str != null && mPackageType.length() > 0 && str.length() > 0) {
                                String msg = mPackageType.concat("/");
                                File file = new File(str);
                                if (file.getParentFile() != null) {
                                    msg = msg.concat(file.getParentFile().getName());
                                }
                                msg = msg.concat("_").concat(file.getName());
                                Bundle bundle = new Bundle();
                                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, msg);
                                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, str);
                                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, str);
                                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                            }
                        }
                    }




                    finish();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gallery, menu);
        return true;
    }
    public void goBack(){

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            if (mSelectedImages.size() < mNeededImageCount && !mIsMaxImageCount) {
                Toast.makeText(this, mFormattedText, Toast.LENGTH_SHORT).show();
            } else {
                Intent data = new Intent();
                data.putExtra(EXTRA_SELECTED_IMAGES, mSelectedImages);
                setResult(RESULT_OK, data);
                
                if (mSelectedImages != null && mSelectedImages.size() > 0) {
                    for (String str : mSelectedImages) {
                        if (mPackageType != null && str != null && mPackageType.length() > 0 && str.length() > 0) {
                            String msg = mPackageType.concat("/");
                            File file = new File(str);
                            if (file.getParentFile() != null) {
                                msg = msg.concat(file.getParentFile().getName());
                            }
                            msg = msg.concat("_").concat(file.getName());
                            Bundle bundle = new Bundle();
                            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, msg);
                            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, str);
                            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, str);
                            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                        }
                    }
                }
                


                
                finish();
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getFragmentManager().findFragmentById(R.id.frame_container);
        if (fragment != null && fragment instanceof DownloadedPackageFragment) {
            super.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void onDeleteButtonClick(String image) {
        mSelectedImages.remove(image);
        mSelectedPhotoAdapter.notifyDataSetChanged();
        mImageCountView.setText(mFormattedText.concat("(" + mSelectedImages.size() + ")"));
    }

    @Override
    public void onSelectImage(String image) {
        if (mSelectedImages.size() == mNeededImageCount) {
            Toast.makeText(this, mFormattedWarningText, Toast.LENGTH_SHORT).show();
        } else {
            mSelectedImages.remove(image);
            mSelectedImages.add(image);
            mSelectedPhotoAdapter.notifyDataSetChanged();
            mImageCountView.setText(mFormattedText.concat("(" + mSelectedImages.size() + ")"));
        }
    }


}
