package agustin.prototipoapp.util;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import agustin.prototipoapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Ravi on 31/03/15.
 */
public class NotificationUtils {

    private static String TAG = NotificationUtils.class.getSimpleName();

    private Context mContext;

    public NotificationUtils(Context mContext) {
        this.mContext = mContext;
    }


    public void showNotificationMessage(final String asunto, final String descripcion, final String fecha, Intent intent) {
        // Check for empty push message
        if (TextUtils.isEmpty(descripcion))
            return;

        // notification icon
        final int icon = R.mipmap.ic_launcher;

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mContext,
                        0,
                        intent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                mContext);

        final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + mContext.getPackageName() + "/raw/notification");

        Log.e("AGUSTIN","NOTIFICACIÓN MANDADA ANTES||||");
        showSmallNotification(mBuilder, icon, asunto, descripcion, fecha, resultPendingIntent, alarmSound);
        playNotificationSound();
        Log.e("AGUSTIN","NOTIFICACIÓN MANDADA DESPUES||||");

    }


    private void showSmallNotification(NotificationCompat.Builder mBuilder, int icon, String asunto, String descripcion, String fecha, PendingIntent resultPendingIntent, Uri alarmSound) {

        Log.e("AGUSTIN","NOTIFICACIÓN MANDADA INICIO||||");
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        inboxStyle.addLine(descripcion);

        Notification notification;
        notification = mBuilder.setSmallIcon(icon).setTicker(asunto).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(asunto)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .setStyle(inboxStyle)
                //TODO ver
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                .setContentText(descripcion)
                .build();

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Config.NOTIFICATION_ID, notification);
        Log.e("AGUSTIN","NOTIFICACIÓN MANDADA ||||");
    }

    // Playing notification sound
    public void playNotificationSound() {
        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + mContext.getPackageName() + "/raw/notification");
            Ringtone r = RingtoneManager.getRingtone(mContext, alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method checks if the app is in background or not
     */
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    // Clears notification tray messages
    public static void clearNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static long getTimeMilliSec(String fechaDeEnvio) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(fechaDeEnvio);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}