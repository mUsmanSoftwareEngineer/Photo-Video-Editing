package collagestudio.photocollage.collagemaker.cross_promotion;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import collagestudio.photocollage.collagemaker.R;

public class MoreAppsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<LoadPromotionResponse> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_apps);

        recyclerView = findViewById(R.id.more_apps_recyclerview);
        list = LoadPromotionData.allAppsList;
        Log.d("listttt", ""+list);
        if (list.size() <= 0) {
//            Toast.makeText(this, "No more apps found.", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            finish();
        } else {

            //good to go
            recyclerView.hasFixedSize();
            recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
            recyclerView.setAdapter(new moreAppsAdapter());
        }

        Toast.makeText(this, "Press back again to exit.", Toast.LENGTH_SHORT).show();
    }

    class moreAppsAdapter extends RecyclerView.Adapter<MoreAppsViewHolder> {

        @NonNull
        @Override
        public MoreAppsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.more_apps_single_item, parent, false);
            return new MoreAppsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MoreAppsViewHolder holder, int position) {
            holder.populateItem(list.get(position).getAppTitle(), list.get(position).getAppIconStr(), list.get(position).getUrl());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    class MoreAppsViewHolder extends RecyclerView.ViewHolder {

        View view;

        public MoreAppsViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        public void populateItem(String name, String icon, final String url) {

            TextView appName = view.findViewById(R.id.app_name);
            ImageView appIcon = view.findViewById(R.id.app_icon);
            RelativeLayout installButton = view.findViewById(R.id.install_button);

            appName.setText(name);
            appName.setSelected(true);
           /* RequestOptions requestOptions = new RequestOptions();
            requestOptions.diskCacheStrategy(DiskCacheStrategy.SOURCE);*/
            Glide.with(MoreAppsActivity.this).load(icon).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(appIcon);

            installButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }
}