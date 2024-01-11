package com.google.android.ads.nativetemplates;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.ads.nativetemplates.R;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.gms.ads.nativead.NativeAdView;


/** Base class for a template view. * */
public class TemplateView extends FrameLayout {

  private int templateType;
  private NativeTemplateStyle styles;
  private UnifiedNativeAd nativeAd;
  private UnifiedNativeAdView nativeAdView;

  private LinearLayout primaryParentView;
  private TextView primaryView;
  private LinearLayout secondaryParentView;
  private TextView secondaryView;
  private LinearLayout tertiaryParentView;
  private RatingBar ratingBar;
  private TextView tertiaryView;
  private ImageView iconView;
  private MediaView mediaView;
  private LinearLayout callToActionParentView;
  private Button callToActionView;
  private LinearLayout background;

  private static final String MEDIUM_TEMPLATE = "medium_template";
  private static final String SMALL_TEMPLATE = "small_template";

  public TemplateView(Context context) {
    super(context);
  }

  public TemplateView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    initView(context, attrs);
  }

  public TemplateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initView(context, attrs);
  }

  @SuppressLint("NewApi")
  public TemplateView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    initView(context, attrs);
  }

  public void setStyles(NativeTemplateStyle styles) {
    this.styles = styles;
    this.applyStyles();
  }

  public UnifiedNativeAdView getNativeAdView() {
    return nativeAdView;
  }

  private void applyStyles() {

    Drawable mainBackground = styles.getMainBackgroundColor();
    if (mainBackground != null) {
      background.setBackground(mainBackground);
      primaryView.setBackground(mainBackground);
      secondaryView.setBackground(mainBackground);
      tertiaryView.setBackground(mainBackground);
    }

    Typeface primary = styles.getPrimaryTextTypeface();
    if (primary != null) {
      primaryView.setTypeface(primary);
    }

    Typeface secondary = styles.getSecondaryTextTypeface();
    if (secondary != null) {
      secondaryView.setTypeface(secondary);
    }

    Typeface tertiary = styles.getTertiaryTextTypeface();
    if (tertiary != null) {
      tertiaryView.setTypeface(tertiary);
    }

    Typeface ctaTypeface = styles.getCallToActionTextTypeface();
    if (ctaTypeface != null) {
      callToActionView.setTypeface(ctaTypeface);
    }

    int primaryTypefaceColor = styles.getPrimaryTextTypefaceColor();
    if (primaryTypefaceColor > 0) {
      primaryView.setTextColor(primaryTypefaceColor);
    }

    int secondaryTypefaceColor = styles.getSecondaryTextTypefaceColor();
    if (secondaryTypefaceColor > 0) {
      secondaryView.setTextColor(secondaryTypefaceColor);
    }

    int tertiaryTypefaceColor = styles.getTertiaryTextTypefaceColor();
    if (tertiaryTypefaceColor > 0) {
      tertiaryView.setTextColor(tertiaryTypefaceColor);
    }

    int ctaTypefaceColor = styles.getCallToActionTypefaceColor();
    if (ctaTypefaceColor > 0) {
      callToActionView.setTextColor(ctaTypefaceColor);
    }

    float ctaTextSize = styles.getCallToActionTextSize();
    if (ctaTextSize > 0) {
      callToActionView.setTextSize(ctaTextSize);
    }

    float primaryTextSize = styles.getPrimaryTextSize();
    if (primaryTextSize > 0) {
      primaryView.setTextSize(primaryTextSize);
    }

    float secondaryTextSize = styles.getSecondaryTextSize();
    if (secondaryTextSize > 0) {
      secondaryView.setTextSize(secondaryTextSize);
    }

    float tertiaryTextSize = styles.getTertiaryTextSize();
    if (tertiaryTextSize > 0) {
      tertiaryView.setTextSize(tertiaryTextSize);
    }

    Drawable ctaBackground = styles.getCallToActionBackgroundColor();
    if (ctaBackground != null) {
      callToActionView.setBackground(ctaBackground);
    }

    Drawable primaryBackground = styles.getPrimaryTextBackgroundColor();
    if (primaryBackground != null) {
      primaryView.setBackground(primaryBackground);
    }

    Drawable secondaryBackground = styles.getSecondaryTextBackgroundColor();
    if (secondaryBackground != null) {
      secondaryView.setBackground(secondaryBackground);
    }

    Drawable tertiaryBackground = styles.getTertiaryTextBackgroundColor();
    if (tertiaryBackground != null) {
      tertiaryView.setBackground(tertiaryBackground);
    }

    invalidate();
    requestLayout();
  }

  private boolean adHasOnlyStore(UnifiedNativeAd nativeAd) {
    String store = nativeAd.getStore();
    String advertiser = nativeAd.getAdvertiser();
    return !isNullOrEmpty(store) && isNullOrEmpty(advertiser);
  }

  private boolean adHasOnlyAdvertiser(UnifiedNativeAd nativeAd) {
    String store = nativeAd.getStore();
    String advertiser = nativeAd.getAdvertiser();
    return !isNullOrEmpty(advertiser) && isNullOrEmpty(store);
  }

  private boolean adHasBothStoreAndAdvertiser(UnifiedNativeAd nativeAd) {
    String store = nativeAd.getStore();
    String advertiser = nativeAd.getAdvertiser();
    return (!isNullOrEmpty(advertiser)) && (!isNullOrEmpty(store));
  }

  private boolean isNullOrEmpty(String string) {
    return string == null || string.isEmpty();
  }

  public void setNativeAd(UnifiedNativeAd nativeAd) {
    this.nativeAd = nativeAd;

    String store = nativeAd.getStore();
    String advertiser = nativeAd.getAdvertiser();
    String headline = nativeAd.getHeadline();
    String body = nativeAd.getBody();
    String cta = nativeAd.getCallToAction();
    Double starRating = nativeAd.getStarRating();
    NativeAd.Image icon = nativeAd.getIcon();

    String tertiaryText;

    nativeAdView.setCallToActionView(callToActionParentView);
    nativeAdView.setHeadlineView(primaryParentView);
    nativeAdView.setMediaView(mediaView);

    if (adHasOnlyStore(nativeAd)) {
      nativeAdView.setStoreView(tertiaryView);
      tertiaryParentView.setVisibility(VISIBLE);
      tertiaryText = store;
    } else if (adHasOnlyAdvertiser(nativeAd)) {
      nativeAdView.setAdvertiserView(tertiaryView);
      tertiaryParentView.setVisibility(VISIBLE);
      secondaryView.setLines(1);
      tertiaryText = advertiser;
    } else if (adHasBothStoreAndAdvertiser(nativeAd)) {
      nativeAdView.setAdvertiserView(tertiaryView);
      tertiaryParentView.setVisibility(VISIBLE);
      secondaryView.setLines(1);
      tertiaryText = advertiser;
    } else {
      tertiaryText = "";
      tertiaryParentView.setVisibility(GONE);
      secondaryView.setLines(3);
    }

    primaryView.setText(headline);
    tertiaryView.setText(tertiaryText);
    callToActionView.setText(cta);

    // Set the secondary view to be the star rating if available.
    // Otherwise fall back to the body text.
    if (starRating != null && starRating > 0) {
      secondaryView.setVisibility(GONE);
      ratingBar.setVisibility(VISIBLE);
      ratingBar.setMax(5);
      nativeAdView.setStarRatingView(ratingBar);
    } else {
      secondaryView.setText(body);
      secondaryView.setVisibility(VISIBLE);
      ratingBar.setVisibility(GONE);
      nativeAdView.setBodyView(secondaryView);
    }

    if (icon != null) {
      iconView.setVisibility(VISIBLE);
      iconView.setImageDrawable(icon.getDrawable());
    } else {
      iconView.setVisibility(GONE);
    }

    nativeAdView.setNativeAd(nativeAd);
  }

  /**
   * To prevent memory leaks, make sure to destroy your ad when you don't need it anymore. This
   * method does not destroy the template view.
   * https://developers.google.com/admob/android/native-unified#destroy_ad
   */
  public void destroyNativeAd() {
    nativeAd.destroy();
  }

  public String getTemplateTypeName() {
    if (templateType == R.layout.gnt_medium_template_view) {
      return MEDIUM_TEMPLATE;
    } else if (templateType == R.layout.gnt_small_template_view) {
      return SMALL_TEMPLATE;
    }
    return "";
  }

  private void initView(Context context, AttributeSet attributeSet) {

    TypedArray attributes =
        context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.TemplateView, 0, 0);

    try {
      templateType =
          attributes.getResourceId(
              R.styleable.TemplateView_gnt_template_type, R.layout.gnt_medium_template_view);
    } finally {
      attributes.recycle();
    }
    LayoutInflater inflater =
        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(templateType, this);
  }

  @Override
  public void onFinishInflate() {
    super.onFinishInflate();
    nativeAdView = (UnifiedNativeAdView) findViewById(R.id.native_ad_view);
    primaryView = (TextView) findViewById(R.id.primary);
    secondaryView = (TextView) findViewById(R.id.secondary);
    secondaryParentView = (LinearLayout) findViewById(R.id.body);
    ratingBar = (RatingBar) findViewById(R.id.rating_bar);
    ratingBar.setEnabled(false);
    tertiaryView = (TextView) findViewById(R.id.tertiary);
    tertiaryParentView = (LinearLayout) findViewById(R.id.third_line);
    callToActionView = (Button) findViewById(R.id.cta);
    iconView = (ImageView) findViewById(R.id.icon);
    mediaView = (MediaView) findViewById(R.id.media_view);
    primaryParentView = (LinearLayout) findViewById(R.id.headline);
    callToActionParentView = (LinearLayout) findViewById(R.id.cta_parent);
    background = (LinearLayout) findViewById(R.id.background);
  }
}
