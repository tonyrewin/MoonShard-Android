package io.moonshard.moonshard.helpers;


import android.app.Application;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;

import androidx.multidex.MultiDexApplication;

import com.instacart.library.truetime.TrueTime;


import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.adorsys.android.securestoragelibrary.SecurePreferences;
import io.moonshard.moonshard.LoginCredentials;
import io.moonshard.moonshard.services.XMPPConnection;

/**
 * Extended Application class which designed for centralized getting various objects from anywhere in the application.
 */
public class AppHelper extends MultiDexApplication {
    private static Application instance;
    public final static String APP_NAME = "Influence";
    public final static String DEFAULT_NTP_SERVER = "time.apple.com";

    private static String jid;
   // private static RoomHelper chatDB;
    private static SharedPreferences preferences;
    private static XMPPConnection xmppConnection;
    private static LoginCredentials currentLoginCredentials;
    private static Handler mainUIThreadHandler;
    private static ServiceConnection serviceConnection;
    private static boolean isMainActivityDestroyed = true;
    private static String currentChatActivity = "";
    public final static Map<String, byte[]> avatarsCache = new ConcurrentHashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        mainUIThreadHandler = new Handler(Looper.getMainLooper());
        //initChatDB();
        preferences = PreferenceManager.getDefaultSharedPreferences(instance);
        initTrueTime();
        loadLoginCredentials();
    }

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
        AppHelper.xmppConnection = xmppConnection;
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
        AppHelper.setJid(currentLoginCredentials.username + "@" + currentLoginCredentials.jabberHost);
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
        AppHelper.serviceConnection = serviceConnection;
    }

    public static ServiceConnection getServiceConnection() {
        return serviceConnection;
    }

    public static boolean isIsMainActivityDestroyed() {
        return isMainActivityDestroyed;
    }

    public static void setIsMainActivityDestroyed(boolean isMainActivityDestroyed) {
        AppHelper.isMainActivityDestroyed = isMainActivityDestroyed;
    }

    public static String getCurrentChatActivity() {
        return currentChatActivity;
    }

    public static void setCurrentChatActivity(String currentChatActivity) {
        AppHelper.currentChatActivity = currentChatActivity;
    }

    public static LoginCredentials getCurrentLoginCredentials() {
        return currentLoginCredentials;
    }
}
