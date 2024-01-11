package collagestudio.photocollage.collagemaker.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import collagestudio.photocollage.collagemaker.R;

public class StepActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private FrameLayout[] dots;
    private int[] layouts;
    private Button btnSkip, btnNext;
    private Constant session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        
        session = new Constant(this);
//        if (!session.isFirstTimeLaunch()) {
//            launchHomeScreen();
//            finish();
//        }

        
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_step);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnSkip = (Button) findViewById(R.id.btn_skip);
        btnNext = (Button) findViewById(R.id.btn_next);


        
        
        layouts = new int[]{
                R.layout.welcome_slide1,
                R.layout.welcome_slide2,
                R.layout.welcome_slide3,
 /*       R.layout.welcomeslide4
        ,R.layout.welcome_slide5*/};

        
        addBottomDots(0);

        
        changeStatusBarColor();
        Log.d("5656", "onCreate: "+layouts.length);

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchHomeScreen();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                launchHomeScreen();
//                int current = getItem(+1);
//                if (current < layouts.length) {
//
//                    viewPager.setCurrentItem(current);
//                } else {
//                    launchHomeScreen();
//                }
            }
        });
    }

    private void addBottomDots(int currentPage) {
        dots = new FrameLayout[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new FrameLayout(this);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(15,15);
            params.setMarginEnd(6);
            dots[i].setLayoutParams(params);

//            dots[i].setText(".");
//            dots[i].setTextSize(20);
//            dots[i].setTextColor(getResources().getColor(R.color.textColorSecondary));
            dots[i].setBackground(getResources().getDrawable(R.drawable.circle));
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
          //  dots[currentPage].setTextColor(getResources().getColor(R.color.textColorPrimary));
            dots[currentPage].setBackground(getResources().getDrawable(R.drawable.circle));
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        //session.setFirstTimeLaunch(false);
        loadActivity(MainActivity.class);
       // startActivity(new Intent(this, Login.class));
       // finish();
    }

    private void loadActivity(Class startClass) {
        Intent intent = new Intent(StepActivity.this, startClass);
        startActivity(intent);
        finish();
    }
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            
//            if (position == layouts.length - 1) {
//
//                btnNext.setText(getString(R.string.start));
//                btnSkip.setVisibility(View.GONE);
//            } else {
//
//                btnNext.setText(getString(R.string.next));
//                btnSkip.setVisibility(View.VISIBLE);
//            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}