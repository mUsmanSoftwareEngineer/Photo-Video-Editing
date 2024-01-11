package collagestudio.photocollage.collagemaker.notifications;

import android.content.Context;
import android.content.Intent;

import androidx.legacy.content.WakefulBroadcastReceiver;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

        NotificationHelper notificationHelper = new NotificationHelper(context);
        notificationHelper.createNotification();
/*
        Intent startServiceIntent = new Intent(context, NotificationHelper.class);
        startWakefulService(context, startServiceIntent);
*/

    }



}

