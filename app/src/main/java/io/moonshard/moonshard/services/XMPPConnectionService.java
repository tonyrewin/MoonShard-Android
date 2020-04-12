package io.moonshard.moonshard.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;


import androidx.core.app.NotificationCompat;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import io.moonshard.moonshard.EmptyLoginCredentialsException;
import io.moonshard.moonshard.MainApplication;
import io.moonshard.moonshard.R;


public class XMPPConnectionService extends Service {

    private static final int RECONNECT_TRY_INTERVAL_MS = 10000; // 10 Seconds
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
            setAddress(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            String test = "";
        }

        @Override
        public void onProviderEnabled(String provider) {
            String test = "";

        }

        @Override
        public void onProviderDisabled(String provider) {
            String test = "";
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(context.getString(R.string.hi))
                    .setContentText(context.getString(R.string.how_are_you)).build();

            startForeground(1, notification);
        }
    }

    void setAddress(Location location){
       Geocoder geocoder;
       List<Address> addresses;
       geocoder = new Geocoder(this, Locale.getDefault());

       try {
           addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); 
           String address = addresses.get(0).getAddressLine(0); 
           String city = addresses.get(0).getLocality();
           String state = addresses.get(0).getAdminArea();
           String country = addresses.get(0).getCountryName();
           String postalCode = addresses.get(0).getPostalCode();
           String knownName = addresses.get(0).getFeatureName(); 
           MainApplication.setAddress(address);
       } catch (IOException e) {
           e.printStackTrace();
       }
   }

    public XMPPConnectionService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void onServiceStart() {
        createLocationListener();

        new XMPPConnectionDelayedCheckAndStartThread().start();

    }

    class XMPPConnectionDelayedCheckAndStartThread extends Thread{
        @Override
        public void run() {
            super.run();
            XMPPConnection connection = new XMPPConnection();
            MainApplication.setXmppConnection(connection);
            try {
                    connection.connect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            
        }
    }

    private void onServiceStop() {
        isThreadAlive = false;
        if (threadHandler != null) {
            threadHandler.post(() -> {
                if (connection != null) {
                    thread.interrupt();
                    thread = null;
                    connection.disconnect();
                    connection = null;
                }
            });
        }
    }

    @SuppressLint("MissingPermission")
    private void createLocationListener() {
       try{
           LocationManager mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
           mLocationManager.requestLocationUpdates(
                   LocationManager.GPS_PROVIDER, 1000 * 10, 10, locationListener);
           mLocationManager.requestLocationUpdates(
                   LocationManager.NETWORK_PROVIDER, 1000 * 10, 10, locationListener);
       }catch (Exception e){
           Log.d("error","Permissions GSP off");
       }
    }

    private void createConnection() {
        if (connection == null) {
            connection = new XMPPConnection();
        }
        try {
            connection.connect();
        } catch (IOException | SmackException e) {
            
            e.printStackTrace();
            onServiceStop();
            stopSelf();
        } catch (XMPPException e) {
            
            
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