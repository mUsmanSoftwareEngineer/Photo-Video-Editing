package collagestudio.photocollage.collagemaker.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.TextView;

import collagestudio.photocollage.collagemaker.R;
import collagestudio.photocollage.collagemaker.adapter.TemplateAdapter;
import collagestudio.photocollage.collagemaker.adapter.TemplateViewHolder;
import collagestudio.photocollage.collagemaker.model.TemplateItem;
import collagestudio.photocollage.collagemaker.quickaction.QuickAction;
import collagestudio.photocollage.collagemaker.quickaction.QuickActionItem;
import collagestudio.photocollage.collagemaker.utils.PhotoItem;
import collagestudio.photocollage.collagemaker.utils.TemplateImageUtils;
import collagestudio.photocollage.collagemaker.utils.frame.FrameImageUtils;
import com.google.firebase.crash.FirebaseCrash;
import com.tonicartos.superslim.LayoutManager;

import java.util.ArrayList;


public class TemplateActivity extends AdsFragmentActivity implements TemplateViewHolder.OnTemplateItemClickListener {
    private ArrayList<String> permissions;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected;
    private static final int ALL_PERMISSIONS_RESULT = 4545344;

    public void goBack(View view) {
        startActivity( new Intent(TemplateActivity.this,MainActivity.class));
        finish();
    }

    private class ViewHolder {
        private final RecyclerView mRecyclerView;

        public ViewHolder(RecyclerView recyclerView) {
            mRecyclerView = recyclerView;
        }

        public void initViews(LayoutManager lm) {
            mRecyclerView.setLayoutManager(lm);
        }

        public void scrollToPosition(int position) {
            mRecyclerView.scrollToPosition(position);
        }

        public void setAdapter(RecyclerView.Adapter<?> adapter) {
            mRecyclerView.setAdapter(adapter);
        }

        public void smoothScrollToPosition(int position) {
            mRecyclerView.smoothScrollToPosition(position);
        }
    }

    public static final String EXTRA_IMAGE_PATHS = "imagePaths";
    public static final String EXTRA_IMAGE_IN_TEMPLATE_COUNT = "imageInTemplateCount";
    public static final String EXTRA_SELECTED_TEMPLATE_INDEX = "selectedTemplateIndex";
    public static final String EXTRA_IS_FRAME_IMAGE = "frameImage";

    private static final int REQUEST_SELECT_PHOTO = 789;

    private static final String KEY_HEADER_POSITIONING = "key_header_mode";

    private static final String KEY_MARGINS_FIXED = "key_margins_fixed";

    private ViewHolder mViews;

    private TemplateAdapter mAdapter;

    private int mHeaderDisplay;

    private boolean mAreMarginsFixed;

    
    private ArrayList<TemplateItem> mTemplateItemList = new ArrayList<TemplateItem>();
    private ArrayList<TemplateItem> mAllTemplateItemList = new ArrayList<TemplateItem>();
    private boolean mFrameImages = false;
    
    private QuickAction mQuickAction;
    private TextView mFilterView;
    ConstraintLayout filterenable;
    private int mImageInTemplateCount = 0;
    private int mSelectedTemplateIndex = 0;
    Dialog dialog;
    @Override
    protected void preCreateAdsHelper() {
        mLoadedData = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template);
//        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
//        this.setSupportActionBar(toolbar);
//        ActionBar actionBar = this.getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setTitle(R.string.app_name);
//        }

        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
        } else {
            mHeaderDisplay = getResources().getInteger(R.integer.default_header_display);
            mAreMarginsFixed = getResources().getBoolean(R.bool.default_margins_fixed);
        }
        
        addAdsView(R.id.adsLayout);

        permissions= new ArrayList<>();
        permissionsRejected= new ArrayList<>();
        permissionsToRequest= new ArrayList<>();
        permissions.add(Manifest.permission.CAMERA);
        permissions.add(Manifest.permission.WAKE_LOCK);
        permissions.add(Manifest.permission.INTERNET);
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.VIBRATE);


        permissionsToRequest = permissionsToRequest(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.
                        toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }
        }

        mViews = new ViewHolder((RecyclerView) findViewById(R.id.recycler_view));
        mViews.initViews(new LayoutManager(this));

        mFilterView = (TextView) findViewById(R.id.frameCountView);
        filterenable = findViewById(R.id.filter_main);
        filterenable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mQuickAction.show(mFilterView);
            }
        });
        mFrameImages = getIntent().getBooleanExtra(EXTRA_IS_FRAME_IMAGE, false);
        if (mFrameImages) {
            loadFrameImages(false);
        } else {
            loadFrameImages(true);
        }
        mAdapter = new TemplateAdapter(this, mHeaderDisplay, mTemplateItemList, this);
        mAdapter.setMarginsFixed(mAreMarginsFixed);
        mAdapter.setHeaderDisplay(mHeaderDisplay);
        mViews.setAdapter(mAdapter);

            createFilterQuickAction();




    }
    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }
    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perm : permissionsToRequest) {
                    if (!hasPermission(perm)) {
                        permissionsRejected.add(perm);
                    }
                }


                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            new AlertDialog.Builder(TemplateActivity.this).
                                    setMessage("These permissions are mandatory to access your gallery photos. You need to allow them to edit.").
                                    setPositiveButton("Got it!", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.
                                                        toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    }) .setCancelable(false).create().show();

                            return;
                        }
                    }

                }

                break;
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_HEADER_POSITIONING, mHeaderDisplay);
        outState.putBoolean(KEY_MARGINS_FIXED, mAreMarginsFixed);
        outState.putBoolean(EXTRA_IS_FRAME_IMAGE, mFrameImages);
        outState.putInt("mImageInTemplateCount", mImageInTemplateCount);
        outState.putInt("mSelectedTemplateIndex", mSelectedTemplateIndex);

    }

    protected void restoreInstanceState(Bundle savedInstanceState) {
        mHeaderDisplay = savedInstanceState
                .getInt(KEY_HEADER_POSITIONING,
                        getResources().getInteger(R.integer.default_header_display));
        mAreMarginsFixed = savedInstanceState
                .getBoolean(KEY_MARGINS_FIXED,
                        getResources().getBoolean(R.bool.default_margins_fixed));
        mFrameImages = savedInstanceState.getBoolean(EXTRA_IS_FRAME_IMAGE, false);
        mImageInTemplateCount = savedInstanceState.getInt("mImageInTemplateCount");
        mSelectedTemplateIndex = savedInstanceState.getInt("mSelectedTemplateIndex");
    }

    private void loadFrameImages(boolean template) {

        Context context = TemplateActivity.this;
//
                mAllTemplateItemList.clear();
                if (template) {
                    mAllTemplateItemList.addAll(TemplateImageUtils.loadTemplates(context));
                } else {
                    mAllTemplateItemList.addAll(FrameImageUtils.loadFrameImages(context));
                }
                mTemplateItemList.clear();
                if (mImageInTemplateCount > 0) {
                    for (TemplateItem item : mAllTemplateItemList)
                        if (item.getPhotoItemList().size() == mImageInTemplateCount) {
                            mTemplateItemList.add(item);
                        }
                } else {
                    mTemplateItemList.addAll(mAllTemplateItemList);
                }
//

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.filter_frame_count, menu);
//        MenuItem item = menu.findItem(R.id.action_filter);
//        mFilterView = (TextView) item.getActionView().findViewById(R.id.frameCountView);
//        mFilterView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mQuickAction.show(mFilterView);
//            }
//        });
//        return true;
//    }

    private void createFilterQuickAction() {
        
        Drawable  x = getResources().getDrawable(R.drawable.action_item_btn);
        mQuickAction = new QuickAction(this, QuickAction.VERTICAL);
        mQuickAction.setPopupBackgroundColor(getResources().getColor(R.color.primaryColor));
        //                mFilterView.setTextColor(getResources().getColor(R.color.gnt_black));

        String[] filterTexts = getResources().getStringArray(R.array.frame_count);

        if (mFrameImages) {
            for (int idx = 0; idx < filterTexts.length; idx++) {
                QuickActionItem item = new QuickActionItem(idx, filterTexts[idx]);
                mQuickAction.addActionItem(item);
            }
        } else {
            for (int idx = 0; idx < 4; idx++) {
                QuickActionItem item = new QuickActionItem(idx, filterTexts[idx]);
                mQuickAction.addActionItem(item);
            }
        }
        
        mQuickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
            @Override
            public void onItemClick(QuickAction source, int pos, int actionId) {
                QuickActionItem quickActionItem = mQuickAction.getActionItem(pos);
                mQuickAction.dismiss();
                
                mFilterView.setText(quickActionItem.getTitle());
                if (quickActionItem.getActionId() == 0) {
                    mTemplateItemList.clear();
                    mTemplateItemList.addAll(mAllTemplateItemList);
                    mImageInTemplateCount = 0;
                } else {
                    mTemplateItemList.clear();
                    mImageInTemplateCount = quickActionItem.getActionId();
                    for (TemplateItem item : mAllTemplateItemList)
                        if (item.getPhotoItemList().size() == quickActionItem.getActionId()) {
                            mTemplateItemList.add(item);
                        }
                }
                mAdapter.setData(mTemplateItemList);
            }
        });

        
        
        mQuickAction.setOnDismissListener(new QuickAction.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });
    }

    @Override
    public void onTemplateItemClick(TemplateItem templateItem) {
        if (!templateItem.isAds()) {
            mSelectedTemplateIndex = mTemplateItemList.indexOf(templateItem);
            Intent data = new Intent(this, SelectPhotoActivity.class);
            data.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            data.putExtra(SelectPhotoActivity.EXTRA_IMAGE_COUNT, templateItem.getPhotoItemList().size());
            startActivityForResult(data, REQUEST_SELECT_PHOTO);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_PHOTO && resultCode == RESULT_OK) {
            try {
                ArrayList<String> mSelectedImages = data.getStringArrayListExtra(SelectPhotoActivity.EXTRA_SELECTED_IMAGES);
                final TemplateItem selectedTemplateItem = mTemplateItemList.get(mSelectedTemplateIndex);
                int itemSize = selectedTemplateItem.getPhotoItemList().size();
                int size = Math.min(itemSize, mSelectedImages.size());
                for (int idx = 0; idx < size; idx++) {
                    selectedTemplateItem.getPhotoItemList().get(idx).imagePath = mSelectedImages.get(idx);
                }
                Intent intent;
                if (mFrameImages) {
                    intent = new Intent(this, FrameDetailActivity.class);
                } else {
                    intent = new Intent(this, TemplateDetailActivity.class);
                }

                intent.putExtra(EXTRA_IMAGE_IN_TEMPLATE_COUNT, selectedTemplateItem.getPhotoItemList().size());
                intent.putExtra(EXTRA_IS_FRAME_IMAGE, mFrameImages);
                if (mImageInTemplateCount == 0) {
                    ArrayList<TemplateItem> tmp = new ArrayList<>();
                    for (TemplateItem item : mTemplateItemList)
                        if (item.getPhotoItemList().size() == selectedTemplateItem.getPhotoItemList().size()) {
                            tmp.add(item);
                        }
                    intent.putExtra(EXTRA_SELECTED_TEMPLATE_INDEX, tmp.indexOf(selectedTemplateItem));
                } else {
                    intent.putExtra(EXTRA_SELECTED_TEMPLATE_INDEX, mSelectedTemplateIndex);
                }
                ArrayList<String> imagePaths = new ArrayList<>();
                for (PhotoItem item : selectedTemplateItem.getPhotoItemList()) {
                    if (item.imagePath == null) item.imagePath = "";
                    imagePaths.add(item.imagePath);
                }
                intent.putExtra(EXTRA_IMAGE_PATHS, imagePaths);
                startActivity(intent);
                finish();
            } catch (Exception ex) {
                ex.printStackTrace();
                FirebaseCrash.report(ex);
            }
        }
    }

    @Override
    public void onBackPressed() {
        startActivity( new Intent(TemplateActivity.this,MainActivity.class));
        finish();
    }
}
