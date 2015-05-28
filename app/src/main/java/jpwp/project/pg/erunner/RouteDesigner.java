package jpwp.project.pg.erunner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.hardware.GeomagneticField;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.DocumentsContract;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.PendingIntent.getActivity;
import static com.google.android.gms.maps.CameraUpdateFactory.zoomTo;

public class RouteDesigner extends FragmentActivity{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Marker startMarker;
    private Marker endMarker;
    private Marker startChallengeMarker;
    private Marker endChallengeMarker;
    private Polyline polylineRoute;
    private Polyline polylineChallenge;
    private int challengeStartInt;
    private int challengeEndInt;
    private Context thisContext;

    /**
     * Method that is called when activity is created
     *
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_designer);
        setUpMapIfNeeded();

        thisContext = this;

        //Buttons listeners & functions
        CheckButtonGoBackToRoutesMenu();
        CheckButtonSaveRoute();

        //routeManager = new RouteManager();
    }



    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * Method used to set upMap - adding markers poly lines etc.
     *
     * Should be called only once.
     */
    private void setUpMap() {
        UpDateMapToUserLocation();
        CheckLongTapMap();
    }

    /**
     * Updates maps' camera position to the device location.
     */
    private void UpDateMapToUserLocation(){
        mMap.setMyLocationEnabled(true);


        //mMap.moveCamera(CameraUpdateFactory.newLatLng(getCurrentLocation()));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(54.3716528,18.6124871)));
        mMap.animateCamera(zoomTo(19.0f));

    }

    /*
      Route Designer buttons section
     */
    /**Basically button listener*/
    private void CheckButtonGoBackToRoutesMenu(){
        Button routeButton = (Button)findViewById(R.id.btnGoToRoutesMenuFromRouteDesigner);

        routeButton.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        RouteManager.getInstance().SaveRoutes(thisContext);
                        finish();
                    }
                }
        );
    }
    /** Basically button listener.
     *
     *  Checks if all fields necessary to create route have been initialized if yes saves route to list
     *  if no, the user is informed about an error.
     */
    private void CheckButtonSaveRoute(){
        Button routeButton = (Button)findViewById(R.id.btnCreateRoute);

        routeButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        //Check if all markers and challenge are present
                        if (startMarker != null && endMarker != null && polylineRoute != null && endChallengeMarker != null && startChallengeMarker !=null & polylineChallenge != null) {
                            EditText editText = (EditText) findViewById(R.id.editTextField);
                            editText.getText();
                            Challenge challenge = new SprintChallenge(challengeStartInt, challengeEndInt, startChallengeMarker.getPosition(), endChallengeMarker.getPosition(), polylineChallenge);
                            Route tmpRoute = new Route(startMarker.getPosition(), endMarker.getPosition(), polylineRoute, editText.getText().toString(), challenge);
                            //Saving route with challenge.
                            RouteManager.getInstance().AddRoute(tmpRoute);
                            //Informing user that route has been saved
                            Toast.makeText(RouteDesigner.this, "Zapisano trase: " + editText.getText().toString() + " - trasa z wyzwaniem", Toast.LENGTH_LONG).show();
                        } else {
                            //Check if start and end markers and their polyline are present
                            if (startMarker != null && endMarker != null && polylineRoute != null) {
                                EditText editText = (EditText) findViewById(R.id.editTextField);
                                editText.getText();
                                Route tmpRoute = new Route(startMarker.getPosition(), endMarker.getPosition(), polylineRoute, editText.getText().toString());
                                //Saving route with challenge.
                                RouteManager.getInstance().AddRoute(tmpRoute);
                                //Informing user that route has been saved
                                Toast.makeText(RouteDesigner.this, "Zapisano trase: " + editText.getText().toString(), Toast.LENGTH_LONG).show();
                            } else {
                                //If conditions are not fulfilled the user is beaing informed about it
                                if (startMarker == null && endMarker == null) {
                                    Toast.makeText(RouteDesigner.this, "Aby zapisac droge dodaj punkt startu oraz mety!", Toast.LENGTH_LONG).show();
                                } else {
                                    if (startMarker == null) {
                                        Toast.makeText(RouteDesigner.this, "Aby zapisac droge dodaj punkt startu!", Toast.LENGTH_LONG).show();
                                    }

                                    if (endMarker == null) {
                                        Toast.makeText(RouteDesigner.this, "Aby zapisac droge dodaj punkt mety!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        }
                    }
                }
        );
    }
    /**Map on long tap listener - user adds markers with long tap on map*/
    private void CheckLongTapMap(){

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                ShowDialogStartFinishChoice(latLng);
            }
        });

    }
    /**Function that checks if both markers are present and draws route between them*/
    private void CheckMarkersAndDrawRoute(){
        //If there are start and end markers provided then the route between two points is being set up;
        if(startMarker != null && endMarker != null){

            //Adding polyline to map.
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(getDirectionsUrl(startMarker.getPosition(),endMarker.getPosition()));

        }
    }

    /**
     * Function that retrieves current location of device
     *
     * @return LatLng position of device
     */
    public LatLng getCurrentLocation()
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
                /*
                    Due to this structure if both providers are present the localization data is automatically compared,
                    to provide better QoS - more accurate position of device

                 */
            }

            //This returns current position of device with a slight margin of error
            return new LatLng(location.getLatitude(), location.getLongitude());
        }
        //Exception handling:
        catch (NullPointerException npe)
        {
            /*
                If data from gps and network is non existent = null then synthetic position of 0,0 is provided to
                prevent from further exception throwing - mostly null pointer errors

             */
            return new LatLng(0, 0);
        }
        catch (Exception e)
        {
            //Other exceptions

            e.printStackTrace();
            return new LatLng(0, 0);
        }
    }


    /**
     * Method responsible for displaying info dialog box to ask user whether add start or finish point on map.
     *
     * The CheckMarkersAndDrawRoute method is called within the dialog box to avoid
     * synchronization issues between dialog box and RouteDesigner activity.
     * (The dialog box and an activity are both separate threads)
     *
     * @param latLng Position of the new finnish/start point as LatLng
     */
   private void ShowDialogStartFinishChoice (final LatLng latLng) {

       String choice[] = {"Punkt Startu", "Punkt Mety","Poczatek wyzwania","Koniec Wyzwania"};

       boolean snapPointFound = false;
       double snapDelta = 0.0005; //Roughly 35 meters on LatLng scale (true for distances less than 3km
       int tmpSnapPointNodeNumber = 0;

       //Checking for possible snap points with grid
       if(!snapPointFound)
       if(polylineRoute != null) {
           for (int i = 0; i < polylineRoute.getPoints().size(); i++) {

               double distanceBetweenPoints = Math.sqrt((latLng.latitude - polylineRoute.getPoints().get(i).latitude) * (latLng.latitude - polylineRoute.getPoints().get(i).latitude) +
                       (latLng.longitude - polylineRoute.getPoints().get(i).longitude) * (latLng.longitude - polylineRoute.getPoints().get(i).longitude));

               if(distanceBetweenPoints < snapDelta) snapPointFound = true;
               if(distanceBetweenPoints < snapDelta) tmpSnapPointNodeNumber = i;
           }
       }

       final int snapPointNodeNumber = tmpSnapPointNodeNumber; //Number of the point on route that matches LatLang+delta
       /*If snap point has been found - offer user option to add a marker there*/
       if(snapPointFound){

           AlertDialog.Builder builder = new AlertDialog.Builder(this);
           builder.setTitle("Wybierz jaki punkt chcesz dodac")
                   .setItems(choice, new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int which) {
                           // The 'which' argument contains the index position
                           // of the selected item

                           switch (which){
                               case 0:
                                   if (startMarker != null) {
                                       startMarker.remove();
                                   }
                                   /* The route has changed and the challenge markers became obsolete thus they have to be deleted
                                   * along with the marker int info an challenges polyline */
                                   if (startChallengeMarker != null){
                                       startChallengeMarker.remove();
                                       challengeStartInt = 0;
                                   }
                                   if (endChallengeMarker != null){
                                       endChallengeMarker.remove();
                                       challengeEndInt = 0;
                                   }
                                   if (polylineChallenge != null){
                                       polylineRoute.remove();
                                   }
                                   startMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Start").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                                   CheckMarkersAndDrawRoute();
                                   break;
                               case 1:
                                   if (endMarker != null) {
                                       endMarker.remove();
                                   }
                                   /* The route has changed and the challenge markers became obsolete thus they have to be deleted
                                   * along with the marker int info an challenges polyline */
                                   if (startChallengeMarker != null){
                                       startChallengeMarker.remove();
                                       challengeStartInt = 0;
                                   }
                                   if (endChallengeMarker != null){
                                       endChallengeMarker.remove();
                                       challengeEndInt = 0;
                                   }
                                   if (polylineChallenge != null){
                                       polylineRoute.remove();
                                   }
                                   endMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Meta").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                   CheckMarkersAndDrawRoute();
                                   break;
                               case 2:
                                   if (startChallengeMarker != null){
                                       startChallengeMarker.remove();
                                   }
                                   challengeStartInt = snapPointNodeNumber;
                                   startChallengeMarker = mMap.addMarker(new MarkerOptions().position(polylineRoute.getPoints().get(snapPointNodeNumber)).title("Start Wyzwania").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                                   CheckMarkersAndDrawRoute();
                                   break;
                               case 3:
                                   if (endChallengeMarker != null){
                                       endChallengeMarker.remove();
                                   }
                                   challengeEndInt = snapPointNodeNumber;
                                   endChallengeMarker = mMap.addMarker(new MarkerOptions().position(polylineRoute.getPoints().get(snapPointNodeNumber)).title("Koniec Wyzwania").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                                   CheckMarkersAndDrawRoute();
                                   break;
                               default:
                                   break;
                           }
                       }
                   });
           Dialog dialog = builder.create();
           dialog.show();
       }
       else {

           AlertDialog.Builder builder = new AlertDialog.Builder(this);

           builder.setMessage("Wybierz czy chesz dodac punkt startu czy mety.");

           builder.setPositiveButton("Meta", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                   if (endMarker != null) {
                       endMarker.remove();
                   }
                   /* The route has changed and the challenge markers became obsolete thus they have to be deleted
                    * along with the marker int info an challenges polyline */
                   if (startChallengeMarker != null){
                       startChallengeMarker.remove();
                       challengeStartInt = 0;
                   }
                   if (endChallengeMarker != null){
                       endChallengeMarker.remove();
                       challengeEndInt = 0;
                   }
                   if (polylineChallenge != null){
                       polylineRoute.remove();
                   }
                   endMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Meta").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                   CheckMarkersAndDrawRoute();
               }
           });

           builder.setNegativeButton("Start", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                   if (startMarker != null) {
                       startMarker.remove();
                   }
                   /* The route has changed and the challenge markers became obsolete thus they have to be deleted
                    * along with the marker int info an challenges polyline */
                   if (startChallengeMarker != null){
                       startChallengeMarker.remove();
                       challengeStartInt = 0;
                   }
                   if (endChallengeMarker != null){
                       endChallengeMarker.remove();
                       challengeEndInt = 0;
                   }
                   if (polylineChallenge != null){
                       polylineRoute.remove();
                   }
                   startMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Start").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                   CheckMarkersAndDrawRoute();
               }
           });

           AlertDialog dialog = builder.create();
           dialog.show();
       }

   }
    /**Method used to set up, and add to map challenge polyline*/
    private void setUpChallengePolyline(Polyline fullRoute){

        PolylineOptions challengePolylineOptions = new PolylineOptions();

        List<LatLng> challengeRoutePoints = fullRoute.getPoints();

        //Deleting unnecessary route points after challenge endpoint
        while (challengeRoutePoints.size()-1 > challengeEndInt){
            challengeRoutePoints.remove(challengeEndInt+1);
        }

        //Deleting unnecessary route points before challenge start point
        for (int i = 0; i < challengeStartInt; i++){
            challengeRoutePoints.remove(0);
        }

        //Setting up poly line - necessary for displaying the challenge route on map
        challengePolylineOptions.addAll(challengeRoutePoints);
        challengePolylineOptions.width(5);
        challengePolylineOptions.color(Color.rgb(227,181,102));

        polylineChallenge = mMap.addPolyline(challengePolylineOptions);
    }

    /*
        Notice:

        This code is responsible solely for decoding the response from google servers on http json request.
        This part of code has been incorporated in to the project, thanks to George Mathew's Google Maps V2 Directions Api tutorial.

        Tutorial written by: George Mathew @ http://wptraficanalyzer.in/blog

        Cosmetic touches by Marcin Figielski
     */

    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false&mode=walking";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String>{

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> > {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration = "";

            if(result.size()<1){
                Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
                return;
            }

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    if(j==0){    // Get distance from the list
                        distance = (String)point.get("distance");
                        continue;
                    }else if(j==1){ // Get duration from the list
                        duration = (String)point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(4);
                lineOptions.color(Color.BLUE);
            }

            //tvDistanceDuration.setText("Distance:"+distance + ", Duration:"+duration);
            //Removing old polyline:
            if (polylineRoute != null){
                polylineRoute.remove();
            }

            // Drawing polyline in the Google Map for the i-th route
            polylineRoute = mMap.addPolyline(lineOptions);

            //Method that draws the challenges' polyline has to be called within this method to prevent desynchronization between threads.
            if(startChallengeMarker != null && endChallengeMarker !=null){
                setUpChallengePolyline(polylineRoute);
            }


        }
    }
}
