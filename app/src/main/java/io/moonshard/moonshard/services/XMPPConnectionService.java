package io.moonshard.moonshard.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import com.google.android.gms.location.LocationListener;

import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import java.io.IOException;

import io.moonshard.moonshard.EmptyLoginCredentialsException;
import io.moonshard.moonshard.MainApplication;


public class XMPPConnectionService extends Service {
    public static XMPPConnection.ConnectionState CONNECTION_STATE = XMPPConnection.ConnectionState.DISCONNECTED;
    public static XMPPConnection.SessionState SESSION_STATE = XMPPConnection.SessionState.LOGGED_OUT;

    private Thread thread;
    private Handler threadHandler;
    private boolean isThreadAlive = false;
    private XMPPConnection connection;
    private Context context = MainApplication.getContext();
    private XMPPServiceBinder binder = new XMPPServiceBinder();



    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            MainApplication.setCurrentLocation(location);
        }
    };

    public XMPPConnectionService() { }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void onServiceStart() {

        if(!isThreadAlive)
        {
            isThreadAlive = true;
            if(thread == null || !thread.isAlive()) {
                thread = new Thread(() -> {
                    createConnection();
                    Looper.prepare();
                    threadHandler = new Handler();
                    //createConnection();
                    Looper.loop();
                });
                thread.start();
            }
        }
    }

    private void onServiceStop() {
        isThreadAlive = false;
        if(threadHandler != null) {
            threadHandler.post(() -> {
                if(connection != null) {
                    thread.interrupt();
                    thread = null;
                    connection.disconnect();
                    connection = null;
                }
            });
        }
    }

    @SuppressLint("MissingPermission")
    private void createLocationListener(){
        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 1000 * 10, 10,
                (android.location.LocationListener) locationListener);    }

    private void createConnection() {
        if(connection == null) {
            connection = new XMPPConnection(this);
        }
        try {
            connection.connect();
        } catch (IOException | SmackException e) {
           // EventBus.getDefault().post(new AuthenticationStatusEvent(AuthenticationStatusEvent.NETWORK_ERROR));
            e.printStackTrace();
            onServiceStop();
            stopSelf();
        } catch (XMPPException e) {
           // EventBus.getDefault().post(new AuthenticationStatusEvent(AuthenticationStatusEvent.INCORRECT_LOGIN_OR_PASSWORD));
           // e.printStackTrace();
            onServiceStop();
            stopSelf();
        } catch (EmptyLoginCredentialsException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onServiceStart();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        onServiceStop();
    }

    public class XMPPServiceBinder extends Binder {
        public XMPPConnection getConnection() {
            return connection;
        }
    }
}