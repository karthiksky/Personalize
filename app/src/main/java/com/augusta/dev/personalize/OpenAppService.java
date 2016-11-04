package com.augusta.dev.personalize;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by skarthik on 10/27/2016.
 */

public class OpenAppService extends Service
{

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent intent1 = new Intent(this, ListProfileDialogActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent1);

        return super.onStartCommand(intent, flags, startId);
    }
}
