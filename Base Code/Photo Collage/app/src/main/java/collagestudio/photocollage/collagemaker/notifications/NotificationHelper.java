package collagestudio.photocollage.collagemaker.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.telephony.SubscriptionPlan;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import collagestudio.photocollage.collagemaker.R;
import collagestudio.photocollage.collagemaker.activity.SplashActivity;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class NotificationHelper {
    private static final String NOTIFICATION_CHANNEL_ID = "1001";
    private static int notifid = 100;
    private final Context mContext;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    int val;
    NotificationHelper(Context context) {
        mContext = context;
    }

    void createNotification() {

        Intent intent = new Intent(mContext, SplashActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext,
                0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID);
        CharSequence message = "Best photo collage maker & photo editor with 100+ grids, filters, stickers, text.";

        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle("Collage Maker")
                   .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))

                .setAutoCancel(false)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        val = getData(mContext);
        if(val<0 || val==0){
            val=100;
            saveData(mContext,val);
        }


        if(val != 0){
            val=val+1;
            saveData(mContext,val);
            Log.d("notifyid", String.valueOf(val));

            if(val == 101)
            {
                message = "Just select several pictures, Photo Collage Maker & Editor instantly remix them into a cool photo collage. You can pick the layout you like best, edit collage with filter, sticker, text and much more. It’s completely free.";
                mBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID);
                mBuilder.setSmallIcon(R.mipmap.ic_launcher);
                mBuilder.setContentTitle("Collage Maker")
                        .setContentText(message)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))

                        .setAutoCancel(false)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setContentIntent(resultPendingIntent);
            }
            if(val == 102)
            {
                message = "Make photo collage with Free style or Grid style.";
                mBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID);
                mBuilder.setSmallIcon(R.mipmap.ic_launcher);
                mBuilder.setContentTitle("Collage Maker")
                        .setContentText(message)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))

                        .setAutoCancel(false)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setContentIntent(resultPendingIntent);
//                saveData(mContext,100);
            }
            if(val == 103)
            {
                message = "Time to Show Some Social Media Presence!!!";
                mBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID);
                mBuilder.setSmallIcon(R.mipmap.ic_launcher);
                mBuilder.setContentTitle("Collage Maker")
                        .setContentText(message)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))

                        .setAutoCancel(false)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setContentIntent(resultPendingIntent);
            }
            if(val == 104)
            {
                message = "Share Your Creativity with your Friends, family and more!!!\n" +
                        "Insta square photo with blur background for Instagram.";
                mBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID);
                mBuilder.setSmallIcon(R.mipmap.ic_launcher);
                mBuilder.setContentTitle("Collage Maker")
                        .setContentText(message)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))

                        .setAutoCancel(false)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setContentIntent(resultPendingIntent);
//                saveData(mContext,100);
            }
            if(val == 105)
            {
                message = "Save photos in high resolution and share pictures to social apps.ِ";
                mBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID);
                mBuilder.setSmallIcon(R.mipmap.ic_launcher);
                mBuilder.setContentTitle("Collage Maker")
                        .setContentText(message)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))

                        .setAutoCancel(false)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setContentIntent(resultPendingIntent);
//                saveData(mContext,100);
            }
            if(val == 106)
            {
                message = "elect beautiful background with a full-screen ratio to create a scrapbook. You can decorate with pictures, stickers, texts, doodles, and share your scrapbook to Instagram Stories and Snapchat Stories.\n";
                mBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID);
                mBuilder.setSmallIcon(R.mipmap.ic_launcher);
                mBuilder.setContentTitle("Collage Maker")
                        .setContentText(message)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))

                        .setAutoCancel(false)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setContentIntent(resultPendingIntent);
//                saveData(mContext,100);
            }
            if(val == 107)
            {
                message = "Create a photo collage with hundreds of layouts in seconds. Custom photo grid size, border and background, you can design layout on your own! So easy to make a beautiful photo collage.";
                mBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID);
                mBuilder.setSmallIcon(R.mipmap.ic_launcher);
                mBuilder.setContentTitle("Collage Maker")
                        .setContentText(message)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))

                        .setAutoCancel(false)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setContentIntent(resultPendingIntent);
//                saveData(mContext,100);
            }
            if(val == 108)
            {
                message = "Best photo collage maker & editor with 100+ layout, grid, filter, sticker, text.\nِ";
                mBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID);
                mBuilder.setSmallIcon(R.mipmap.ic_launcher);
                mBuilder.setContentTitle("Collage Maker")
                        .setContentText(message)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))

                        .setAutoCancel(false)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setContentIntent(resultPendingIntent);
//                saveData(mContext,100);
            }
            if(val == 109)
            {
                message = "Photo in templates is much trendy nowadays. Tons of photo frames and effects make your moment stunning, like love photo frames, anniversary, holiday & baby photo frames…";
                mBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID);
                mBuilder.setSmallIcon(R.mipmap.ic_launcher);
                mBuilder.setContentTitle("Collage Maker")
                        .setContentText(message)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))

                        .setAutoCancel(false)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setContentIntent(resultPendingIntent);
//                saveData(mContext,100);
            }
            if(val == 110)
            {
                message = "Just select several pictures, in college the best Photo Collage Maker & Editor will instantly remix them into a cool photo collage or a meme. You can pick the layout you like best, edit collage with filter, sticker, frame, text and much more. It's is completely free!ِ";
                mBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID);
                mBuilder.setSmallIcon(R.mipmap.ic_launcher);
                mBuilder.setContentTitle("Collage Maker")
                        .setContentText(message)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))

                        .setAutoCancel(false)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setContentIntent(resultPendingIntent);
//                saveData(mContext,100);
            }
            if(val == 111)
            {
                message = "Pic Collage Maker & Picture Editor with photo layout, photo grid to make a collage";
                mBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID);
                mBuilder.setSmallIcon(R.mipmap.ic_launcher);
                mBuilder.setContentTitle("Collage Maker")
                        .setContentText(message)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))

                        .setAutoCancel(false)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setContentIntent(resultPendingIntent);
//                saveData(mContext,100);
            }
            if(val == 112)
            {
                message = "Thanks to the selfie filters and picture effects you’ll have dozens of pretty pics to post. Montage all your selfies with the photo collage editor instantly. Photo collage maker creates amazing colleges for special days like Birthdays, Valentine’s day.. Just choose your favourite pics and let the photo college maker do its magic. Many stickers are available in various themes. Make your picture collage and memes super fun with picture stickers. Explore layout and the PhotoGrid, to add a breathtaking collage photo frame :). Blur background or change it with an astonishing background image :).";
                mBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID);
                mBuilder.setSmallIcon(R.mipmap.ic_launcher);
                mBuilder.setContentTitle("Collage Maker")
                        .setContentText(message)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))

                        .setAutoCancel(false)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setContentIntent(resultPendingIntent);
//                saveData(mContext,100);
            }
            if(val == 113)
            {
                message = "Apply filters for pictures to your astonishing collages :). Snap selfie with a golden hour filter with the correct light :). Sketch effects create marvellous neon sketch art. It is so much fun to apply picture filters and effects. Montage pics in one post with the picture collage maker :).";
                mBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID);
                mBuilder.setSmallIcon(R.mipmap.ic_launcher);
                mBuilder.setContentTitle("Collage Maker")
                        .setContentText(message)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))

                        .setAutoCancel(false)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setContentIntent(resultPendingIntent);

            }

            if(val == 114)
            {
                message = "Photo Collage, collage maker and photo grid frame for Instagram!";
                mBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID);
                mBuilder.setSmallIcon(R.mipmap.ic_launcher);
                mBuilder.setContentTitle("Collage Maker")
                        .setContentText(message)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))

                        .setAutoCancel(false)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setContentIntent(resultPendingIntent);

            }
            if(val == 115)
            {
                message = "Photo Collage is packed with templates, stickers, grid and layout options, photo effects, and exciting photo editor tools to make photos extra special.";
                mBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID);
                mBuilder.setSmallIcon(R.mipmap.ic_launcher);
                mBuilder.setContentTitle("Collage Maker")
                        .setContentText(message)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))

                        .setAutoCancel(false)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setContentIntent(resultPendingIntent);
                val=100;
                saveData(mContext, val);
            }


        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }

        Log.d("notifid", String.valueOf(notifid));

        assert mNotificationManager != null;
        mNotificationManager.notify(notifid /* Request Code */, mBuilder.build());
    }


    public void saveData(Context context, int notifid){
        SharedPreferences sharedPref = getDefaultSharedPreferences(context);
        SharedPreferences.Editor spEditor = sharedPref.edit();
        spEditor.putInt("DATA", notifid);
        spEditor.commit();
    }

    public int getData(Context context){
        SharedPreferences sharedPref = getDefaultSharedPreferences(context);
        int sPSavedData= sharedPref.getInt("DATA", 0);
        return sPSavedData;
    }



}
