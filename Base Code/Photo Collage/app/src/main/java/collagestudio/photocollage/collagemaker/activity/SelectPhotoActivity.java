package collagestudio.photocollage.collagemaker.activity;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import collagestudio.photocollage.collagemaker.R;
import collagestudio.photocollage.collagemaker.adapter.SelectedPhotoAdapter;
import collagestudio.photocollage.collagemaker.fragment.GalleryAlbumFragment;
import collagestudio.photocollage.collagemaker.fragment.GalleryAlbumImageFragment;

import java.util.ArrayList;


public class SelectPhotoActivity extends AdsFragmentActivity implements SelectedPhotoAdapter.OnDeleteButtonClickListener, GalleryAlbumImageFragment.OnSelectImageListener {
    public static final String EXTRA_IMAGE_COUNT = "imageCount";
    public static final String EXTRA_IS_MAX_IMAGE_COUNT = "isMaxImageCount";
    public static final String EXTRA_SELECTED_IMAGES = "selectedImages";

    private RecyclerView mRecyclerView;
    private TextView mImageCountView;

    private ArrayList<String> mSelectedImages = new ArrayList<>();
    private SelectedPhotoAdapter mSelectedPhotoAdapter;
    private int mNeededImageCount = 0;
    private boolean mIsMaxImageCount = false;
    
    private String mFormattedText;
    private String mFormattedWarningText;
    TextView done;
    TextView goBack;
    private ViewGroup mAdLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_photo);
//        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
//        this.setSupportActionBar(toolbar);
//        ActionBar actionBar = this.getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setTitle(R.string.app_name);
//        }
        mNeededImageCount = getIntent().getIntExtra(EXTRA_IMAGE_COUNT, 0);
        mIsMaxImageCount = getIntent().getBooleanExtra(EXTRA_IS_MAX_IMAGE_COUNT, false);
        

        goBack = findViewById(R.id.iv_back);
        mRecyclerView = (RecyclerView) findViewById(R.id.selectedImageRecyclerView);
        mImageCountView = (TextView) findViewById(R.id.imageCountView);
        done = (TextView) findViewById(R.id.action_done);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectedImages.size() < mNeededImageCount && !mIsMaxImageCount) {
                    Toast.makeText(SelectPhotoActivity.this, mFormattedText, Toast.LENGTH_SHORT).show();
                } else {
                    Intent data = new Intent();
                    data.putExtra(EXTRA_SELECTED_IMAGES, mSelectedImages);
                    setResult(RESULT_OK, data);
                    finish();
                }
            }
        });
        if (!mIsMaxImageCount) {
            mFormattedText = String.format(getString(R.string.please_select_photo), mNeededImageCount);
        } else {
            mFormattedText = getString(R.string.please_select_photo_without_counting);
        }
        mImageCountView.setText(mFormattedText.concat("  Selected (0)"));
        mFormattedWarningText = String.format(getString(R.string.you_need_photo), mNeededImageCount);
        
        
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mSelectedPhotoAdapter = new SelectedPhotoAdapter(mSelectedImages, this);
        mRecyclerView.setAdapter(mSelectedPhotoAdapter);
        getFragmentManager().beginTransaction().replace(R.id.frame_container, new GalleryAlbumFragment()).commit();
        mAdLayout = (ViewGroup) findViewById(R.id.adsLayout);



        if (getAdsHelper() != null)
            getAdsHelper().addAdsBannerView(mAdLayout);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_gallery, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.action_done) {
//            if (mSelectedImages.size() < mNeededImageCount && !mIsMaxImageCount) {
//                Toast.makeText(this, mFormattedText, Toast.LENGTH_SHORT).show();
//            } else {
//                Intent data = new Intent();
//                data.putExtra(EXTRA_SELECTED_IMAGES, mSelectedImages);
//                setResult(RESULT_OK, data);
//                finish();
//            }
//            return true;
//        } else {
//            return super.onOptionsItemSelected(item);
//        }
//    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getFragmentManager().findFragmentById(R.id.frame_container);
        if (fragment != null && fragment instanceof GalleryAlbumFragment) {
            super.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void onDeleteButtonClick(String image) {
        mSelectedImages.remove(image);
        mSelectedPhotoAdapter.notifyDataSetChanged();
        mImageCountView.setText(mFormattedText.concat(" Selected (" + mSelectedImages.size() + ")"));
    }

    @Override
    public void onSelectImage(String image) {
        if (mSelectedImages.size() == mNeededImageCount ) {
            Toast.makeText(this, mFormattedWarningText, Toast.LENGTH_SHORT).show();
        } else {

            mSelectedImages.add(image);
            mSelectedPhotoAdapter.notifyDataSetChanged();
            mImageCountView.setText(mFormattedText.concat(" Selected (" + mSelectedImages.size() + ")"));
        }
    }
}
