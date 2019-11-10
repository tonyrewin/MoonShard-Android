package io.moonshard.moonshard;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;

import com.facebook.soloader.SoLoader;
import com.instacart.library.truetime.TrueTime;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

import org.matrix.androidsdk.MXSession;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import de.adorsys.android.securestoragelibrary.SecurePreferences;
import io.moonshard.moonshard.di.components.ApplicationComponent;
import io.moonshard.moonshard.di.components.DaggerApplicationComponent;
import io.moonshard.moonshard.di.modules.ApplicationModule;
import io.moonshard.moonshard.di.modules.WebModule;
import io.moonshard.moonshard.services.P2ChatService;
import io.moonshard.moonshard.services.XMPPConnection;

public class MainApplication extends Application {
    private static ApplicationComponent component;
    public static final String SERVICE_TOPIC = "moonshard";
    public static final String PROTOCOL_ID = "/moonshard/1.0.0";
    public static ServiceConnection serviceConnection = null;
    private static P2ChatService service = null;

    private static Application instance;
    public final static String APP_NAME = "Influence";
    public final static String DEFAULT_NTP_SERVER = "time.apple.com";

    private static String jid;
    // private static RoomHelper chatDB;
    private static SharedPreferences preferences;
    private static XMPPConnection xmppConnection;
    private static LoginCredentials currentLoginCredentials;
    private static Handler mainUIThreadHandler;
    private static boolean isMainActivityDestroyed = true;
    private static String currentChatActivity = "";
    public final static Map<String, byte[]> avatarsCache = new ConcurrentHashMap<>();

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



        instance = this;

        mainUIThreadHandler = new Handler(Looper.getMainLooper());
        //initChatDB();
        preferences = PreferenceManager.getDefaultSharedPreferences(instance);
        initTrueTime();
        loadLoginCredentials();
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

    public static Context getContext() {
        return instance.getApplicationContext();
    }

    public static String getJid() { return jid; }

    public static void setJid(String jid1) { jid = jid1; }

    // public static RoomHelper getChatDB() { return chatDB; }

    public static SharedPreferences getPreferences() {
        return preferences;
    }

    public static XMPPConnection getXmppConnection() {
        return xmppConnection;
    }

    public static void setXmppConnection(XMPPConnection xmppConnection) {
        MainApplication.xmppConnection =  xmppConnection;
    }

    public static void loadLoginCredentials() {
        currentLoginCredentials = new LoginCredentials();
        String jid = SecurePreferences.getStringValue("jid", null);
        String password = SecurePreferences.getStringValue("pass", null);
        if(jid != null && password != null) {
            String username = jid.split("@")[0];
            String jabberHost = jid.split("@")[1];
            currentLoginCredentials.username = username;
            currentLoginCredentials.jabberHost = jabberHost;
            currentLoginCredentials.password = password;
        }
        MainApplication.setJid(currentLoginCredentials.username + "@" + currentLoginCredentials.jabberHost);
    }

    public static void resetLoginCredentials() {
        currentLoginCredentials = null;
        SecurePreferences.removeValue("jid");
        SecurePreferences.removeValue("pass");
        SecurePreferences.removeValue("logged_in");
    }

    private static void initTrueTime() {
        new Thread(() -> {
            boolean isTrueTimeIsOn = false;
            int count = 0;
            while(!isTrueTimeIsOn && count <= 10) {
                try {
                    TrueTime.build().withNtpHost(DEFAULT_NTP_SERVER).initialize();
                    isTrueTimeIsOn = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    count++;
                }
            }
        }).start();
    }

    /*
    private void initChatDB() {
        chatDB = Room.databaseBuilder(getApplicationContext(), RoomHelper.class, "chatDB")
                .fallbackToDestructiveMigration() // FIXME   ONLY FOR TEST ENVIRONMENT! DON'T USE THIS IN PRODUCTION!
                .allowMainThreadQueries()
                .build();
    }

     */

    public static Handler getMainUIThread() {
        return mainUIThreadHandler;
    }

    public static void setServiceConnection(ServiceConnection serviceConnection) {
        MainApplication.serviceConnection = serviceConnection;
    }

    public static ServiceConnection getServiceConnection() {
        return serviceConnection;
    }

    public static boolean isIsMainActivityDestroyed() {
        return isMainActivityDestroyed;
    }

    public static void setIsMainActivityDestroyed(boolean isMainActivityDestroyed) {
        MainApplication.isMainActivityDestroyed = isMainActivityDestroyed;
    }

    public static String getCurrentChatActivity() {
        return currentChatActivity;
    }

    public static void setCurrentChatActivity(String currentChatActivity) {
        MainApplication.currentChatActivity = currentChatActivity;
    }

    public static LoginCredentials getCurrentLoginCredentials() {
        return currentLoginCredentials;
    }
}


