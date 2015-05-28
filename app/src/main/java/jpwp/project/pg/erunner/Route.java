package jpwp.project.pg.erunner;

import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Default;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementUnion;
import org.simpleframework.xml.Transient;
import org.simpleframework.xml.convert.Convert;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents Route with its parameters
 * The route object is created when user saves the route thus
 * all params are passed through constructor
 */
@Default
public class Route {

    @Transient
    private static int globalRouteIDCount;

    @Attribute
    private LatLng startPoint;
    @Attribute
    private LatLng endPoint;

    @Element
    @Convert(PolyLineConverter.class)
    private Polyline calculatedRoute;

    @ElementUnion({
            @Element(name="Sprint", type=SprintChallenge.class),
    })
    private Challenge challenge;

    @Attribute
    private String routeName;
    @Attribute
    private int routeID;

    /**
     * First of constructors - this one do not require challenges - ideal for plain route
     * The routeID is calculated automatically
     *
     * @param startPoint Route start point as LatLng object
     * @param endPoint  Route end point as LatLng object
     * @param calculatedRoute Drawable route as poly line
     * @param routeName Name of the route as string
     */
    public Route(LatLng startPoint, LatLng endPoint, Polyline calculatedRoute, String routeName){
        globalRouteIDCount++;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.calculatedRoute = calculatedRoute;
        this.routeName = routeName;

        this.routeID = globalRouteIDCount;
        this.challenge = null;
    }

    /**
     * Second constructor - this one requires to specify challenges as a List
     * The routeID is calculated automatically
     *
     * @param startPoint Route start point as LatLng object
     * @param endPoint Route end point as LatLng object
     * @param calculatedRoute Drawable route as poly line
     * @param routeName Name of the route as string
     * @param challenge Challenge planned on the route
     */
    public Route(LatLng startPoint, LatLng endPoint, Polyline calculatedRoute, String routeName, Challenge challenge){

        globalRouteIDCount++;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.calculatedRoute = calculatedRoute;
        this.routeName = routeName;

        this.routeID = globalRouteIDCount;
        this.challenge = challenge;
    }

    /*
        There are no setters for fields because the routes are meant to be unchangeable;

        No javadoc comment - method names are pretty much self sufficient
     */
    /**Start point getter*/
    public LatLng getStartPoint() {
        return startPoint;
    }
    /**End point getter*/
    public LatLng getEndPoint() {
        return endPoint;
    }
    /**Calculated route getter*/
    public Polyline getCalculatedRoute() {
        return calculatedRoute;
    }
    /*Unused not javadoc commented*/
    public Challenge getChallenge() {
        return challenge;
    }
    /**Route name getter*/
    public String getRouteName() {
        return routeName;
    }
    /**Route id getter*/
    public int getRouteID() {
        return routeID;
    }
    /**Overriden toString method*/
    @Override
    public String toString(){
        if(challenge == null) {
            return routeName;
        }
        else{
            return routeName + " z wyzwaniem typu: " + this.challenge.toString();// -- for some weird reason not working
        }
    }

    /**
     * Method used to calculate the distance in meters, between two LatLng locations
     *
     * @param Point1 First LatLng point
     * @param Point2 Second LatLng point
     * @return Returns distance in meters between the two points
     */
    private double CalculateDistanceBetweenPoints(LatLng Point1, LatLng Point2){

        final double EARTH_RADIUS = 6378.137;

        //Calculating differences between point1 an point2 LatLong
        double deltaLatitude = (Point2.latitude - Point1.latitude) * Math.PI /180;
        double deltaLongitude = (Point2.longitude - Point1.longitude) * Math.PI /180;

        // Avoiding Math.pow(a,2) using a*a is more efficient
        double tmpAuxVar1 = Math.sin(deltaLatitude/2) * Math.sin(deltaLatitude/2) + Math.cos(Point1.latitude * Math.PI/180) * Math.cos(Point2.latitude * Math.PI/180) *
                Math.sin(deltaLongitude/2) * Math.sin(deltaLongitude/2);

        double tmpAuxVar2 = 2 * Math.atan2(Math.sqrt(tmpAuxVar1), Math.sqrt(1-tmpAuxVar1));
        double tmpAuxVar3 = EARTH_RADIUS * tmpAuxVar2;

        return tmpAuxVar3 * 1000; //Returns distance in meters
    }

    /**
     * Method used to calculate the distance of an route a
     *
     * @return Returns route length in meters as double
     */
    final public double getRouteLength(){

        //Calculating the summary route length
        double routeLength = 0;

        if(!calculatedRoute.getPoints().isEmpty()) {
            for (int i = 0; i<calculatedRoute.getPoints().size(); i++){
                if(i==0){
                    //Do nothing
                }
                else{
                    routeLength = routeLength + CalculateDistanceBetweenPoints(calculatedRoute.getPoints().get(i-1),calculatedRoute.getPoints().get(i));
                }
            }
        }
        else {
            /*
                If the List of points is empty, the route is obviously 0.0m long, this piece of code exists to prevent from doing operations
                such as .size() on empty list.
             */
            routeLength = 0.0;
        }
        return routeLength; //Returns route length
    }

}