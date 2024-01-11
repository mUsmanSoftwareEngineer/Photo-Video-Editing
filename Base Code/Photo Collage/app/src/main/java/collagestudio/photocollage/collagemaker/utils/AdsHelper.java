package collagestudio.photocollage.collagemaker.utils;

import android.content.Context;
import android.view.ViewGroup;

import collagestudio.photocollage.collagemaker.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;


public class AdsHelper {

//    public static final String NATIVE_AD_ID = "ca-app-pub-3940256099942544/2247696110";
    public static final String NATIVE_AD_ID = String.valueOf(R.string.native_ad_unit);

    public interface OnInterstitialAdListener {
        void onInterstitialAdLoaded();
    }

    private int mClickedPeriod = 15;
    private InterstitialAd mInterstitialAd;
    private int mClickedCount = 0;
    private OnInterstitialAdListener mAdListener;
    
    private AdView mAdView;
    private NativeExpressAdView nadnative;

    public AdsHelper(Context context) {
        this(context, null);
    }

    public AdsHelper(Context context, OnInterstitialAdListener listener) {
        
        mAdListener = listener;
        try {
            MobileAds.initialize(context.getApplicationContext(), context.getResources().getString(R.string.ADMOB_APP_ID));
            mInterstitialAd = new InterstitialAd(context);
            mInterstitialAd.setAdUnitId(context.getResources().getString(R.string.interstitial_ad_unit));

            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    requestNewInterstitial();
                    
                }

                @Override
                public void onAdLoaded() {
                    if (mAdListener != null) {
                        mAdListener.onInterstitialAdLoaded();
                    }
                }
            });

            requestNewInterstitial();
            
            mAdView = new AdView(context);
            mAdView.setAdSize(AdSize.SMART_BANNER);
            mAdView.setAdUnitId(context.getResources().getString(R.string.banner_ad_unit));

            AdRequest adRequest = new AdRequest.Builder().build();
            
//            AdRequest adRequest = new AdRequest.Builder().addTestDevice(
//                    AdRequest.DEVICE_ID_EMULATOR).build();
            
            mAdView.loadAd(adRequest);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void addAdsBannerView(ViewGroup parent) {
        if (mAdView != null) {
            parent.removeView(mAdView);
        }

        parent.addView(mAdView);
    }
   /* public void adnativeaddview(ViewGroup parent) {
        if (mAdView != null) {
            parent.removeView(mAdView);
        }

        parent.addView(mAdView);
    }*/

    public void pauseAdsBanner() {
        if (mAdView != null) {
            mAdView.pause();
        }
    }

    public void resumeAdsBanner() {
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    public void destroyAdsBanner() {
        if (mAdView != null) {
            mAdView.destroy();
        }
    }

    public void setAdListener(OnInterstitialAdListener adListener) {
        mAdListener = adListener;
    }

    public boolean showInterstitialAds() {
        try {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public InterstitialAd getInterstitialAd() {
        return mInterstitialAd;
    }

    public void setClickedPeriod(int clickedPeriod) {
        mClickedPeriod = clickedPeriod;
    }

    public void clickItem() {
        try {
            mClickedCount++;
            if (mClickedCount % mClickedPeriod == 0) {
                showInterstitialAds();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void requestNewInterstitial() {
        try {
            AdRequest adRequest = new AdRequest.Builder().build();
          //  AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
            mInterstitialAd.loadAd(adRequest);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
