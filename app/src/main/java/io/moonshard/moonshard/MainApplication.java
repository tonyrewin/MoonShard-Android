package io.moonshard.moonshard;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import com.facebook.soloader.SoLoader;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

import org.matrix.androidsdk.MXSession;

import java.util.HashSet;
import java.util.Set;

import io.moonshard.moonshard.di.components.ApplicationComponent;
import io.moonshard.moonshard.di.components.DaggerApplicationComponent;
import io.moonshard.moonshard.di.modules.ApplicationModule;
import io.moonshard.moonshard.di.modules.WebModule;
import io.moonshard.moonshard.services.P2ChatService;

public class MainApplication extends Application {
    private static ApplicationComponent component;
    public static final String SERVICE_TOPIC = "moonshard";
    public static final String PROTOCOL_ID = "/moonshard/1.0.0";
    public static ServiceConnection serviceConnection = null;
    private static P2ChatService service = null;

    @Override
    public void onCreate() {
        super.onCreate();

        setupLogger();

        component = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(getApplicationContext()))
                .webModule(new WebModule(getApplicationContext()))
                .build();

        SoLoader.init(this, /* native exopackage */ false);

        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                MainApplication.serviceConnection = this;
                MainApplication.service = ((P2ChatService.P2ChatServiceBinder) service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                MainApplication.serviceConnection = null;
                MainApplication.service = null;
            }
        };

        new Handler().postDelayed(() -> {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getApplicationContext().startForegroundService(new Intent(getApplicationContext(), P2ChatService.class));
            } else {
                getApplicationContext().startService(new Intent(getApplicationContext(), P2ChatService.class));
            }

            getApplicationContext().bindService(new Intent(getApplicationContext(), P2ChatService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        }, 2000);
    }

    private static void setupLogger() {
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)
                .tag("MoonShard")
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
    }

    public static ApplicationComponent getComponent() {
        return component;
    }

    //==============================================================================================================
    // Syncing mxSessions
    //==============================================================================================================


    /**
     * syncing sessions
     */
    private static final Set<MXSession> mSyncingSessions = new HashSet<>();

    /**
     * Add a session in the syncing sessions list
     *
     * @param session the session
     */
    public static void addSyncingSession(MXSession session) {
        synchronized (mSyncingSessions) {
            mSyncingSessions.add(session);
        }
    }

    /**
     * Remove a session in the syncing sessions list
     *
     * @param session the session
     */
    public static void removeSyncingSession(MXSession session) {
        if (null != session) {
            synchronized (mSyncingSessions) {
                mSyncingSessions.remove(session);
            }
        }
    }

    /**
     * Clear syncing sessions list
     */
    public static void clearSyncingSessions() {
        synchronized (mSyncingSessions) {
            mSyncingSessions.clear();
        }
    }

    /**
     * Tell if a session is syncing
     *
     * @param session the session
     * @return true if the session is syncing
     */
    public static boolean isSessionSyncing(MXSession session) {
        boolean isSyncing = false;

        if (null != session) {
            synchronized (mSyncingSessions) {
                isSyncing = mSyncingSessions.contains(session);
            }
        }

        return isSyncing;
    }

    /*
    public static P2ChatService getP2ChatService() {
        return service;
    }

     */
}
