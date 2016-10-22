package xyz.kt.senser;

/**
 * Created by Björn Karge on 30/09/2016.
 * Credits Zuhairi Harun
 */

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

class LocationListener implements android.location.LocationListener
{
    static double lastSpeed;

    private static final String TAG = "LocationListener";

    private LocationManager locationManager;
    private Context context;
    private boolean subscribed;

    private Pusher pusher;
    private StringBuilder sb= new StringBuilder(200);
    private long t0= -1;

    private boolean locationAvailable()
    {
        // TODO: Consider calling ActivityCompat#requestPermissions here
        // in order to request the missing permissions, and then overriding
        // public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.

        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    LocationListener(Context context, LocationManager locationManager, Pusher pusher )
    {
        this.locationManager= locationManager;
        this.context= context;
        this.pusher= pusher;

        if (locationAvailable())
        {
            Criteria criteria = new Criteria();
            criteria.setHorizontalAccuracy( Criteria.ACCURACY_HIGH );
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setCostAllowed(false);
            String provider = locationManager.getBestProvider( criteria, true );

            if (provider != null)
            {
                locationManager.requestLocationUpdates(provider, 1000 /*ms*/, 5 /*m*/, this);
                subscribed = true;
            }
        }
    }

    void stop()
    {
        if (subscribed)
        {
            locationManager.removeUpdates(this);
            subscribed= false;
        }
    }

    @Override
    public void onLocationChanged(Location location)
    {
        long t= System.currentTimeMillis();
        if (t0 < 0)
            t0= t;

        sb.append( 'L' ); sb.append( ',' );
        sb.append( t / 1000.0d ); sb.append( ',' );
        sb.append( location.getLatitude() ); sb.append( ',' );
        sb.append( location.getLongitude() ); sb.append( ',' );

        if (location.hasAltitude())
            sb.append( location.getAltitude() );
        sb.append( ',' );

        if (location.hasSpeed())
        {
            sb.append(lastSpeed= location.getSpeed());
        }
        sb.append( ',' );

        if (location.hasBearing())
            sb.append( location.getBearing() );
        sb.append( ',' );

        if (location.hasAccuracy())
            sb.append( location.getAccuracy() );
        sb.append( ',' );

        sb.append( location.getTime() );
        sb.append( ',' );

        sb.append( location.getProvider() );
        sb.append( '\n' );

        if (1000 < t - t0)
        {
            String data= sb.toString();
            pusher.enqueueChunk( data );
            sb.setLength(0);
            t0= t;
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle)
    {
        // TODO: Meaningful?
    }

    @Override
    public void onProviderEnabled(String s)
    {
        // TODO: Meaningful?
    }

    @Override
    public void onProviderDisabled(String s)
    {
        // TODO: Meaningful?
    }
}

abstract class EventListener<Event>
{
    private Pusher pusher;
    private StringBuilder sb= new StringBuilder();
    private long t0= -1;

    EventListener( Pusher pusher )
    {
        this.pusher= pusher;
    }

    abstract void appendEventData(StringBuilder sb, long time, Event event);

    void handleEvent(Event event)
    {
        long t= System.currentTimeMillis();
        if (t0 < 0)
            t0= t;

        appendEventData( sb, t, event );
        sb.append( '\n' );

        if (1000 < t - t0)
        {
            String data= sb.toString();
            pusher.enqueueChunk( data );
            sb.setLength(0);
            t0= t;
        }
    }
}

abstract class SensorListener extends EventListener<SensorEvent> implements SensorEventListener
{
    private static final String TAG = "SensorListener";

    private SensorManager manager;

    SensorListener( SensorManager manager, Sensor sensor, Pusher pusher )
    {
        super(pusher);
        this.manager= manager;
        manager.registerListener( this, sensor, SensorManager.SENSOR_DELAY_NORMAL );
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        handleEvent( event );
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        // TODO: meaningful?
    }

    void stop()
    {
        manager.unregisterListener( this );
    }
}

class AnnotationListener extends EventListener<String>
{
    AnnotationListener(Pusher pusher)
    {
        super(pusher);
    }

    @Override
    void appendEventData(StringBuilder sb, long time, String event)
    {
        sb.append( '#' ); sb.append( ',' );
        sb.append( time / 1000.0d ); sb.append( ',' );
        sb.append( event );
    }
}

class LinearAccelerationListener extends SensorListener
{
    private static final String TAG = "LinearAccelerationListener";

    LinearAccelerationListener(SensorManager manager, Sensor sensor, Pusher pusher)
    {
        super( manager, sensor, pusher );
    }

    @Override
    void appendEventData(StringBuilder sb, long time, SensorEvent event)
    {
        sb.append( 'U' ); sb.append( ',' );
        sb.append( time / 1000.0d ); sb.append( ',' );
        sb.append( event.values[ 0 ] ); sb.append( ',' );
        sb.append( event.values[ 1 ] ); sb.append( ',' );
        sb.append( event.values[ 2 ] );
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        // TODO: meaningful?
    }
}

class RotationVectorListener extends SensorListener
{
    private static final String TAG = "RotationVectorListener";

    RotationVectorListener(SensorManager manager, Sensor sensor, Pusher pusher)
    {
        super( manager, sensor, pusher );
    }

    @Override
    void appendEventData(StringBuilder sb, long time, SensorEvent event)
    {
        sb.append( 'Q' ); sb.append( ',' );
        sb.append( time / 1000.0d ); sb.append( ',' );
        sb.append( event.values[ 0 ] ); sb.append( ',' );
        sb.append( event.values[ 1 ] ); sb.append( ',' );
        sb.append( event.values[ 2 ] ); sb.append( ',' );
        if (3 < event.values.length && 0 <= event.values[ 3 ])
            sb.append(event.values[3]);
    }
}

public class SenserService extends Service
{
    private LocationListener locationListener;
    private LinearAccelerationListener linearAccelerationListener;
    private RotationVectorListener rotationVectorListener;
    AnnotationListener annotationListener;
    private Pusher pusher= new Pusher();

    @Override
    public void onCreate()
    {
        Toast.makeText(this, "Background Motion Sensing Enabled", Toast.LENGTH_SHORT).show();

        annotationListener= new AnnotationListener( pusher );

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener( getApplicationContext(), locationManager, pusher );

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        Sensor linearAcceleration;
        if ((linearAcceleration= sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)) != null)
            linearAccelerationListener= new LinearAccelerationListener( sensorManager, linearAcceleration, pusher );

        Sensor rotationVector;
        if ((rotationVector= sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)) != null)
            rotationVectorListener= new RotationVectorListener( sensorManager, rotationVector, pusher );

        IntentFilter filter = new IntentFilter();
        filter.addAction("Senser.Annotate");
        filter.addAction("Senser.Credentials");
        registerReceiver(receiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Background Motion Sensing Enabled", Toast.LENGTH_SHORT).show();

        //return START_STICKY;
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy()
    {
        unregisterReceiver(receiver);

        Toast.makeText(this, "Background Motion Sensing Disabled", Toast.LENGTH_SHORT).show();
        if (locationListener != null)
            locationListener.stop();

        if (linearAccelerationListener != null)
            linearAccelerationListener.stop();

        if (rotationVectorListener != null)
            rotationVectorListener.stop();
    }

    // http://stackoverflow.com/questions/20594936/communication-between-activity-and-service
    // http://stackoverflow.com/questions/9092134/broadcast-receiver-within-a-service
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals( "Senser.Annotate" ))
            {
                Toast.makeText( SenserService.this, intent.getStringExtra( "comment" ), Toast.LENGTH_SHORT ).show();
                annotationListener.handleEvent( intent.getStringExtra( "comment" ) );
            }
            else
            if (intent.getAction().equals( "Senser.Credentials" ))
            {
                Toast.makeText( SenserService.this, "Authenticating...", Toast.LENGTH_SHORT ).show();
                pusher.setCredentials( intent.getStringExtra( "user" ), intent.getStringExtra( "passwd" ) );
            }
        }
    };
}
