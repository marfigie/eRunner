package jpwp.project.pg.erunner;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Camera;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import java.util.logging.LogRecord;

public class Hud extends ActionBarActivity implements SensorEventListener {
    //Camera fields
    private Camera camera;
    private HudCamPreview cameraPreview;

    //Activity management fields
    private boolean runStarted;
    private boolean routeChoosen;
    private Handler timeHandler = new Handler();
    private Handler runHandler = new Handler();
    private Handler locHandler = new Handler();
    private Context thisContext;

    //Route and location fields
    private Polyline inRunRoute;
    private LatLng startPoint;
    private GoogleMap mMap;

    //Activity child objects:
    private ImageView directionPointer;
    private TextView speedTxt;
    private TextView timerTxt;
    private TextView bearingTxt;

    //Class-Global data fields:
    private long startTime;
    private long runTimeInMillis;
    private float bearing;

    /*Sensor Fields: - due to limited time unimplemented.
    private SensorManager sensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    private float mCurrentDegree = 0f;*/

    /**
     * Method called on create of an activity.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hud);

        //Control flags
        runStarted = false;
        routeChoosen = false;
        thisContext = this;

        //Get an instance of back facing cam
        camera = getCameraInstance();

        //Linking the hardware cam life-feed to an Activity
        cameraPreview = new HudCamPreview(this, camera);
        FrameLayout prewiev = (FrameLayout) findViewById(R.id.cameraPreview);
        prewiev.addView(cameraPreview);

        //Registering go back button listener
        CheckButtonGoBackToMainMenu();

        //finding and preparing text fields to display run info.
        speedTxt = (TextView) findViewById(R.id.textSpeed);
        timerTxt = (TextView) findViewById(R.id.textTimer);
        //bearingTxt = (TextView) findViewById(R.id.textbearing);
        TextView textView = (TextView)findViewById(R.id.textRouteName);
        TextView textViewRouteLength = (TextView)findViewById(R.id.textRouteLength);

        //Checking if user has actually created any route. Route with number 0 will be choosen.
        if(!RouteManager.getInstance().getRoutes().isEmpty()) {
            textView.setText(RouteManager.getInstance().getRoutes().get(0).getRouteName());
            textViewRouteLength.setText(String.format("%.0f",RouteManager.getInstance().getRoutes().get(0).getRouteLength()));
            inRunRoute = RouteManager.getInstance().getRoutes().get(0).getCalculatedRoute();
            startPoint = inRunRoute.getPoints().get(0);
            routeChoosen = true;
        }
        else {
            Toast.makeText(this,"Lista dróg jest pusta, aby rozpocząć bieg musisz mieć wybraną drogę",Toast.LENGTH_LONG).show();
            textView.setText("Nie wybrano drogi!");
            textViewRouteLength.setText("Nie wybrano drogi!");
            speedTxt.setText("Nie wybrano drogi!");
        }

        //Registering location&speed, time measurement, and run control handlers
        locHandler.post(speedAndLocationInformationUpdater);
        timeHandler.post(updateRunTime);
        if(routeChoosen){
            runHandler.postDelayed(onRouteController,0);
        }
    }

    /**Field operating as method responsible for measuring run time.*/
    private Runnable updateRunTime = new Runnable() {
        @Override
        public void run() {
            if(runStarted) {
                //Current run time is difference between current time and start time
                runTimeInMillis = SystemClock.uptimeMillis() - startTime;

                //Calculating to minutes, seconds and milliseconds
                int seconds = (int) (runTimeInMillis / 1000);
                int mins = seconds / 60;
                seconds = seconds % 60;
                int tenths = (int) ((runTimeInMillis / 10) % 100);
                timerTxt.setText("" + mins + ":"
                        + String.format("%02d", seconds) + ":"
                        + String.format("%02d", tenths));
            }
            //Putting task back into the queue
            timeHandler.postDelayed(this, 0);
        }
    };

    /**Unimplemented Runnable - would be resposible for rotating the direction pointing arrow*/
    private  Runnable updateCompassArrowRotation = new Runnable() {
        @Override
        public void run() {

        }
    };

    /**
     * Runnable responsible for control of the run, checks if the user is on the route
     */
    private Runnable onRouteController = new Runnable() {
        @Override
        public void run() {

            double snapDelta = 0.0008; //Roughly 40 meters on LatLng scale (true for distances less than 3km
            double distanceBetweenPoints = 0.0; //Temporary auxiliary variable

            LatLng currentDeviceLocation = new LatLng(getCurrentLocation().getLatitude(),getCurrentLocation().getLongitude()); //Obtaining device loaction

            //If the device location is not null the distance will be calculated, else if device location is null then great distance will be set
            if(currentDeviceLocation != null) {
                distanceBetweenPoints = Math.sqrt((currentDeviceLocation.latitude - inRunRoute.getPoints().get(0).latitude) * (currentDeviceLocation.latitude - inRunRoute.getPoints().get(0).latitude) +
                        (currentDeviceLocation.longitude - inRunRoute.getPoints().get(0).longitude) * (currentDeviceLocation.longitude - inRunRoute.getPoints().get(0).longitude));
            }else
            {
                distanceBetweenPoints = 1.0;
            }

            //Checks if run is not started, checks conditions to start run and starts run
            if(!runStarted){
                if(!inRunRoute.getPoints().get(0).equals(startPoint)){
                    runStarted = true;
                    startTime = SystemClock.uptimeMillis();
                    Toast.makeText(thisContext,"Rozpoczęto bieg!",Toast.LENGTH_LONG).show();
                }
            }

           // Debug option
            if(!runStarted) {
                runStarted = true;
                startTime = SystemClock.uptimeMillis();
            }

            //If the distance between current location and current route point is less than delta, then the current route point will be dropped
            if(distanceBetweenPoints < snapDelta)
            {
                if(!inRunRoute.getPoints().isEmpty()){
                    inRunRoute.getPoints().remove(0);
                }
            }
            //Checks if run has been completed if so displays message and stops Runnable that counts time.
            if(inRunRoute.getPoints().isEmpty()){
                timeHandler.removeCallbacks(updateRunTime);

                AlertDialog.Builder builder = new AlertDialog.Builder(thisContext);

                builder.setMessage("Wybierz czy chesz dodac punkt startu czy mety.");

                builder.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //dismiss the dialog
                            }
                        });

                builder.create().show();

            }
            //Task back into queue
            runHandler.postDelayed(this,0);
        }
    };

    /**Runnable responsible for updating location and speed (can offer bearing between current and next location)*/
    private Runnable speedAndLocationInformationUpdater = new Runnable() {

        @Override
        public void run() {
            Location location = getCurrentLocation(); // Obtaining location

            //Setting up new location as destination to which bearing will be calculated, using current location to initialize;
            Location nextLocationPoint = location;
            nextLocationPoint.setLatitude(inRunRoute.getPoints().get(0).latitude);
            nextLocationPoint.setLongitude(inRunRoute.getPoints().get(0).longitude);

            //This updates the speed of device since its runnable it's constantly updated
            if (location != null) {
                double speed = location.getSpeed();
                speedTxt.setText(String.format("%.0f", speed));
                bearing = location.bearingTo(nextLocationPoint);
                //bearingTxt.setText(String.format("%.1f",bearing)); // commented out in xml
            } else {
                speedTxt.setText("Brak informacji");
            }
            //Runnable back into queue - delayed by 100ms to not overwhelm the location sensors
            timeHandler.postDelayed(this, 100);
        }
    };

    /**Method called when back buton on cation bar is pressed*/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        camera.release();
    }
    /**Method called on action bar menu creation*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hud, menu);
        return true;
    }
    /**Method called on action bar menu item selection*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    Safe way to get camera - exception handling required by Android to work properly, or else app will be terminated.
    Recommended approach by Google Developer Guide:
     */
    /**Method that releases the camera*/
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // try to get back-facing camera
        }
        catch (Exception e){
            // Camera not available (cam does not exist or claimed by other app)
        }
        return c; // returns null if camera is unavailable
    }
    /** Method that was planned to obtain info about rotation of device,
     *  this together with bearing would determine where the direction pointer should be pointing*/
    @Override
    public void onSensorChanged(SensorEvent event) {
        float azimuth_angle = event.values[0];
        float pitch_angle = event.values[1];
        float roll_angle = event.values[2];
        //TODO

    }
    //Not used - required to be overridden
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Nothing to do
    }
    /**Basically button listener*/
    private void CheckButtonGoBackToMainMenu(){
        ImageButton routeButton = (ImageButton)findViewById(R.id.btnGoToMainMenuFromHud);

        routeButton.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        camera.release();
                        finish();
                    }
                }
        );
    }
    /**Method that is called when activity is being closed*/
    @Override
    public void finish(){
        //Removing all callbacks
        locHandler.removeCallbacks(speedAndLocationInformationUpdater);
        timeHandler.removeCallbacks(updateRunTime);
        runHandler.removeCallbacks(onRouteController);
        super.finish();
    }



    /**
     * Method that retrieves current device location.
     *
     * @return returns current device location as Location object.
     */
    public Location getCurrentLocation()
    {
        try
        {
            LocationManager locManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);

            //Acquiring the best localization service provider (GPS or NETWORK)
            String provider = locManager.getBestProvider(new Criteria(), false);
            Location location = locManager.getLastKnownLocation(provider);

            // Checking the status of GPS and Network providers
            boolean isGPSEnabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNWEnabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNWEnabled)
            {
                return null; //No provider found
            }
            else
            {
                // First get location from Network Provider
                if (isNWEnabled)
                    if (locManager != null)
                        location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled)
                    if (location == null)
                        if (locManager != null)
                            location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }

            //This returns current position of device with a slight margin of error
            return location;
        }
        //Exception handling:
        catch (NullPointerException npe)
        {
            return null;
        }
        catch (Exception e)
        {
            //Other exceptions
            e.printStackTrace();
            return null;
        }
    }
}

