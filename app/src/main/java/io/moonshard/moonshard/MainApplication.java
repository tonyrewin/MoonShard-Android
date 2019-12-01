package io.moonshard.moonshard;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;

import com.instacart.library.truetime.TrueTime;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cat.ereza.customactivityoncrash.config.CaocConfig;
import de.adorsys.android.securestoragelibrary.SecurePreferences;
import io.moonshard.moonshard.di.components.ApplicationComponent;
import io.moonshard.moonshard.di.components.DaggerApplicationComponent;
import io.moonshard.moonshard.di.modules.ApplicationModule;
import io.moonshard.moonshard.di.modules.WebModule;
import io.moonshard.moonshard.services.P2ChatService;
import io.moonshard.moonshard.services.XMPPConnection;
import io.moonshard.moonshard.ui.activities.BaseActivity;

public class MainApplication extends Application {
    private static ApplicationComponent component;
    public static final String SERVICE_TOPIC = "moonshard";
    public static final String PROTOCOL_ID = "/moonshard/1.0.0";
    public static ServiceConnection serviceConnection = null;
    private static P2ChatService service = null;

    private static Application instance;
    public final static String APP_NAME = "MoonShard";
    public final static String DEFAULT_NTP_SERVER = "time.apple.com";
    private static BaseActivity currentActivity;


    private static String jid;
    private static SharedPreferences preferences;
    private static XMPPConnection xmppConnection;
    private static LoginCredentials currentLoginCredentials;
    private static Handler mainUIThreadHandler;
    private static boolean isMainActivityDestroyed = true;
    private static String currentChatActivity = "";
    public final static Map<String, byte[]> avatarsCache = new ConcurrentHashMap<>();
    private static Location currentLocation;

    public static String getAdress() {
        return adress;
    }

    public static void setAdress(String adress) {
        MainApplication.adress = adress;
    }

    private static String adress;


    public static boolean isForeGround() {
        return (currentActivity != null
                && !currentActivity.isFinishing());
    }


    public static BaseActivity getCurrentActivity() {
        return currentActivity;
    }

    public static void setCurrentActivity(BaseActivity activity) {
        currentActivity = activity;
    }

    public static Location getCurrentLocation() {
        return currentLocation;
    }

    public static void setCurrentLocation(Location location) {
        currentLocation = location;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        setupLogger();

        component = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(getApplicationContext()))
                .webModule(new WebModule(getApplicationContext()))
                .build();

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
        ObjectBox.INSTANCE.init(getApplicationContext()); // initialize ObjectBox DB
        mainUIThreadHandler = new Handler(Looper.getMainLooper());
        preferences = PreferenceManager.getDefaultSharedPreferences(instance);
        initTrueTime();
        loadLoginCredentials();

        CaocConfig.Builder.create()
                .logErrorOnRestart(false) //default: true
                .trackActivities(true) //default: false
                .apply();
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

    /*public static P2ChatService getP2ChatService() {
        return service;
    }*/

    public static Context getContext() {
        return instance.getApplicationContext();
    }

    public static String getJid() { return jid; }

    public static void setJid(String jid1) { jid = jid1; }

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

    public static void setCurrentLoginCredentials(LoginCredentials currentLoginCredentials) {
        MainApplication.currentLoginCredentials = currentLoginCredentials;
    }

    public static LoginCredentials getCurrentLoginCredentials() {
        return currentLoginCredentials;
    }
}


