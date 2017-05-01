package com.example.bryan.ipcsharedatatestone.backgroundjobs;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * It's important to remember that a Service, though independent of the foreground component, still runs code
 * in the MainThread; Therefore, i must create a new custom Thread when using a service.
 */

public class PaintFileService extends Service {


    @Override
    public void onCreate() {
        Log.i("test", "Hello i am starting");
        /*each startService(Intent) call will do one/two things
        * It will either create the service via onCreate() AND call onStartCommand()
        * OR it will simply call onStartCommand() IF the service already exists.
        */
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("test", "Hello i'm onStartCommand()");
        /* There are 3 possible return values. Each of them tells the OS how to restart/or not restart
         * The service after the OS shuts the service down due to memory constraints
           START_STICKY : Restart the service (and subsequently call this method, with a null intent)
           START_NOT_STICKY : Don't bother restarting this service
           START_REDELIVER_INTENT : Restart this service WITH the same intent it had before it was killed.
         */


        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //Returns NULL if i don't want to bind the service
        return null;
    }







}
