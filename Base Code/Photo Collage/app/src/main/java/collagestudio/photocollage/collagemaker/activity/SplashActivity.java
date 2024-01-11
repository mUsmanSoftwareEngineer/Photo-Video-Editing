package collagestudio.photocollage.collagemaker.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

import collagestudio.photocollage.collagemaker.IntroScreens;
import collagestudio.photocollage.collagemaker.R;
import collagestudio.photocollage.collagemaker.cross_promotion.LoadPromotionData;
import collagestudio.photocollage.collagemaker.notifications.AlarmReceiver;

public class SplashActivity extends AppCompatActivity {
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Intent alarmIntent = new Intent(SplashActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(SplashActivity.this, 0, alarmIntent, 0);
        new LoadPromotionData(this, "https://raw.githubusercontent.com/QaisBaig/videodownloader/main/photocollage_crosspromo.json");
//        new LoadPromotionData(this, "https://raw.githubusercontent.com/QaisBaig/videodownloader/main/videodownloader_crosspromo_applinks.json");

//        setting alarm here
//        myAlarm();
        scheduleNotification();
//        AdLoader.
        startMainActivity();
    }

    private void startMainActivity() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean isFirstTime = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this).getBoolean("isFirstTime",true);
                if (isFirstTime){
                    PreferenceManager.getDefaultSharedPreferences(SplashActivity.this).edit().putBoolean("isFirstTime",false).apply();
                    loadActivity(IntroScreens.class);
                }else {
                    loadActivity(MainActivity.class);
                }
                finish();
            }
        }, 1000);
    }

    private void loadActivity(Class startClass) {

        Intent intent = new Intent(SplashActivity.this, startClass);
        startActivity(intent);
        finish();
    }
    public void myAlarm() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        int interval = 1000 * 60 * 20;
        /* Set the alarm to start at 10:30 AM */
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 10);
        calendar.set(Calendar.MINUTE, 30);

/*        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(System.currentTimeMillis());
        calendar2.set(Calendar.HOUR_OF_DAY, 11);
        calendar2.set(Calendar.MINUTE, 24);*/
//         Repeating on every 20 minutes interval
        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                11 * 3600000, pendingIntent);    //11 hours timer which will work on background service
//                1000 * 60 * 2, pendingIntent);    //& 7PM
                1000 * 60 * 510, pendingIntent);    //for testing two minutes
//                calendar2.getTimeInMillis(), pendingIntent);    //for testing two minutes
    /*    manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar2.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);*/

    }
    public void scheduleNotification() {

        Calendar mfiringCal  = Calendar.getInstance();
        Calendar mcurrentCal = Calendar.getInstance();

        mfiringCal.set(Calendar.HOUR_OF_DAY, 10);
        mfiringCal.set(Calendar.MINUTE, 14);
        mfiringCal.set(Calendar.SECOND, 0);

        long intendedTime = mfiringCal.getTimeInMillis();
        long currentTime  = mcurrentCal.getTimeInMillis();

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if(intendedTime >= currentTime) {

            Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
            notificationIntent.addCategory("android.intent.category.DEFAULT");

            PendingIntent broadcast = PendingIntent.getBroadcast(this, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, mfiringCal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }else{

            Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
            notificationIntent.addCategory("android.intent.category.DEFAULT");

            PendingIntent broadcast = PendingIntent.getBroadcast(this, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            mfiringCal.add(Calendar.DAY_OF_MONTH, 1);
            intendedTime = mfiringCal.getTimeInMillis();

            alarmManager.setRepeating(AlarmManager.RTC, intendedTime, AlarmManager.INTERVAL_DAY, pendingIntent);
        }






        /*-------------- 2nd Notification ------------- */
        Calendar nFiringCal  = Calendar.getInstance();
        Calendar nCurrentCal = Calendar.getInstance();

        nFiringCal.set(Calendar.HOUR_OF_DAY, 19);
        nFiringCal.set(Calendar.MINUTE, 30);
        nFiringCal.set(Calendar.SECOND, 0);

        long intendedTime1 = nFiringCal.getTimeInMillis();
        long currentTime1  = nCurrentCal.getTimeInMillis();

        if(intendedTime1 >= currentTime1) {

            Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
            notificationIntent.addCategory("android.intent.category.DEFAULT");

            PendingIntent broadcast = PendingIntent.getBroadcast(this, 101, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, nFiringCal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, broadcast);
        }else{

            Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
            notificationIntent.addCategory("android.intent.category.DEFAULT");

            PendingIntent broadcast = PendingIntent.getBroadcast(this, 101, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            nFiringCal.add(Calendar.DAY_OF_MONTH, 1);
            intendedTime = nFiringCal.getTimeInMillis();

            alarmManager.setRepeating(AlarmManager.RTC, intendedTime, AlarmManager.INTERVAL_DAY, broadcast);
        }
    }
}

