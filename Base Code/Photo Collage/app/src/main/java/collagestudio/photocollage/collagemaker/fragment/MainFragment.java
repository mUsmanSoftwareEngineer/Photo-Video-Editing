package collagestudio.photocollage.collagemaker.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import collagestudio.photocollage.collagemaker.Constatnts;
import collagestudio.photocollage.collagemaker.R;
import collagestudio.photocollage.collagemaker.activity.PhotoCollageActivity;
import collagestudio.photocollage.collagemaker.activity.TemplateActivity;
import collagestudio.photocollage.collagemaker.cross_promotion.LoadPromotionData;
import collagestudio.photocollage.collagemaker.cross_promotion.LoadPromotionResponse;
import collagestudio.photocollage.collagemaker.cross_promotion.PromotionDialog;
import collagestudio.photocollage.collagemaker.utils.BigDAdsHelper;
import dauroi.photoeditor.receiver.NetworkStateReceiver;

public class MainFragment extends BaseFragment implements NetworkStateReceiver.NetworkStateReceiverListener {
    TextView title;
    private BigDAdsHelper mBigDAdsHelper;
   /* private int promotionAdCounter = 0;
    private final int promotionAdCounterLimit = 3;*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, null);
        View photoView = rootView.findViewById(R.id.photoButton);
        View title = rootView.findViewById(R.id.title);
        photoView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Constatnts.promotionAdCounter++;
                if (Constatnts.promotionAdCounter >= Constatnts.promotionAdCounterLimit) {
                    Log.d("aaya", "aaa gya");

                    showPromotionalAd();

                }
                else {
                    createFromPhoto();
                    report("home/clicked_create_freely");
                    getActivity().finish();
                }
            }
        });
        View frameView = rootView.findViewById(R.id.frameButton);
        frameView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Constatnts.promotionAdCounter++;
                if (Constatnts.promotionAdCounter >= Constatnts.promotionAdCounterLimit) {
                    Log.d("aaya", "aaa gya");

                    showPromotionalAd();

                }
                else {
                    createFromFrame();
                    report("home/clicked_frame");
                    getActivity().finish();
                }
            }
        });
        View templateView = rootView.findViewById(R.id.imageTemplateButton);
        templateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constatnts.promotionAdCounter++;

                if (Constatnts.promotionAdCounter >= Constatnts.promotionAdCounterLimit) {
                    Log.d("aaya", "aaa gya");

                  showPromotionalAd();

                }
                else {
                    createFromTemplate();
                    report("home/clicked_template");
                    getActivity().finish();
                }
            }
        });

//        if (!DebugOptions.isProVersion()) {
////            final ViewGroup mAdsAppLayout = (ViewGroup) rootView.findViewById(R.id.appLayout);
////            mBigDAdsHelper = new BigDAdsHelper(mActivity);
////            mBigDAdsHelper.showSecondDetailView(false);
////            mBigDAdsHelper.attach(mAdsAppLayout);
////            mBigDAdsHelper.asyncLoadBigDAds();
////            NetworkStateReceiver.addListener(this);
//        }

        return rootView;
    }

    public void showPromotionalAd() {
        PromotionDialog promotionDialog = new PromotionDialog(mActivity);
        LoadPromotionResponse response = LoadPromotionData.getRandomAppData();
        Log.d("random", "" + response);

        if (response != null) {
            Log.d("aaya", "aaa gya");

            promotionDialog.populateInterstitialDialog(response);
            promotionDialog.showPromotionInterstitial();
        }


        Constatnts.promotionAdCounter = 0;
    }

    @Override
    protected void setTitle() {
        String mTitle = getString(R.string.home);
        setTitle(mTitle);
    }

    @Override
    public void onDestroyView() {
        NetworkStateReceiver.removeListener(this);
        super.onDestroyView();
    }

    private void report(String type) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, type);
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "home");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }


    public void createFromPhoto() {
        if (!already()) {
            return;
        }
        Intent intent = new Intent(getActivity(), PhotoCollageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(PhotoCollageActivity.EXTRA_CREATED_METHOD_TYPE, PhotoCollageActivity.PHOTO_TYPE);
        startActivity(intent);
    }

    public void createFromFrame() {
        if (!already()) {
            return;
        }


//        Toast.makeText(mActivity, "aa gya", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), TemplateActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(TemplateActivity.EXTRA_IS_FRAME_IMAGE, true);
        startActivity(intent);
    }

    public void createFromTemplate() {
        if (!already()) {
            return;
        }
        Intent intent = new Intent(getActivity(), TemplateActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(PhotoCollageActivity.EXTRA_CREATED_METHOD_TYPE, PhotoCollageActivity.FRAME_TYPE);
        startActivity(intent);
    }

    @Override
    public void onNetworkAvailable() {
        if (mBigDAdsHelper != null && !mBigDAdsHelper.isVisible()) {
            mBigDAdsHelper.asyncLoadBigDAds();
        }
    }

    @Override
    public void onNetworkUnavailable() {


    }

}
