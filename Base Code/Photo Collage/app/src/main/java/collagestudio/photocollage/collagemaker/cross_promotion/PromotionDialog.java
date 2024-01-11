package collagestudio.photocollage.collagemaker.cross_promotion;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import collagestudio.photocollage.collagemaker.R;

public class PromotionDialog extends Dialog {

    private Context context;

    public PromotionDialog(@NonNull Context context) {
        super(context, R.style.full_screen_dialog);

        this.context = context;
        this.setContentView(R.layout.promotion_interstitial);
        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);
        this.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
    }

    public void populateInterstitialDialog(final LoadPromotionResponse response) {
//        Toast.makeText(context, "inside", Toast.LENGTH_SHORT).show();
        Log.d("responseddd",response.toString());
        TextView title = this.findViewById(R.id.dialog_title);
        TextView description = this.findViewById(R.id.dialog_description);
        ImageView mainIcon = this.findViewById(R.id.dialog_icon);
        ImageView coverImage = this.findViewById(R.id.main_image);
        ImageView downloadButton = this.findViewById(R.id.dialog_download_button);
        ImageView closeButton = this.findViewById(R.id.dialog_close_button);


        Glide.with(context).load(response.getAppIconStr()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(mainIcon);
        Glide.with(context).load(response.getAppCoverImage()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(coverImage);
    /*    Log.d("responsed", response.getAppCoverImage());
        Log.d("responsed", response.getAppIconStr());
        Log.d("responsed", response.getAppTitle());*/
        title.setText(response.getAppTitle());
        description.setText(response.getAppDescription());

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissPromotionInterstitial();
            }
        });

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(response.getUrl()));
                context.startActivity(intent);
            }
        });
    }

    public void showPromotionInterstitial() {
        if (LoadPromotionData.dataLoaded) {
            this.show();
        }
    }

    public void dismissPromotionInterstitial() {
        if (this.isShowing()) {
            this.dismiss();
        }
    }
}
