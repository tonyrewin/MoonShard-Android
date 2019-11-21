package io.moonshard.moonshard.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;


import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

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
            getAddress(location);
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


   void getAddress(Location location){
       Geocoder geocoder;
       List<Address> addresses;
       geocoder = new Geocoder(this, Locale.getDefault());

       try {
           addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
           String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
           String city = addresses.get(0).getLocality();
           String state = addresses.get(0).getAdminArea();
           String country = addresses.get(0).getCountryName();
           String postalCode = addresses.get(0).getPostalCode();
           String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
           MainApplication.setAdress(address);
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

        if (!isThreadAlive) {
            isThreadAlive = true;
            if (thread == null || !thread.isAlive()) {
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