package jpwp.project.pg.erunner;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.convert.Convert;

import java.util.List;

/**
 * Represents a Challenge on route map.
 * This class is abstract representation of challenge.
 * To create new challenge type this should be inherited.
 * Labelled with "Simple XML" serialization library TAG's
 */
public abstract class Challenge {

    @Attribute
    protected int endPoint;
    @Attribute
    protected int startPoint;
    @Element
    protected int score;
    @Element
    @Convert(LatLngConverter.class)
    protected LatLng startPointLatLng;
    @Element
    @Convert(LatLngConverter.class)
    protected LatLng endPointLatLang;
    @Element
    @Convert(PolyLineConverter.class)
    protected Polyline challengeRoute;
    @Attribute
    protected double challengeDistance;


    /**
     * Challenge constructor - builds complete Challenge except calculating score.
     *
     * @param startPoint Start point of a challenge as int number - id of specific route marker in Polyline.getPoints() list
     * @param endPoint End point of a challenge as int number - id of specific route marker in Polyline.getPoints() list
     * @param startPointLatLng Start point of a challenge as LatLng position on map
     * @param endPointLatLang End point of a challenge as LatLng position on map
     * @param challengeRoute Drawable route in form of polyline object
     */
    public Challenge (int startPoint, int endPoint, LatLng startPointLatLng, LatLng endPointLatLang, Polyline challengeRoute){

        //Initializing fields
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.startPointLatLng = startPointLatLng;
        this.endPointLatLang = endPointLatLang;
        this.challengeRoute = challengeRoute;

        //Calculate distance
        this.challengeDistance = ChallengeLength();
    }

    /*
        Getters:
     */
    final public int getEndPoint(){
        return this.endPoint;
    }

    final public int getStartPoint(){
        return startPoint;
    }

    final public LatLng getStartPointLatLng(){
        return this.startPointLatLng;
    }

    final public LatLng getEndPointLatLang(){
        return this.startPointLatLng;
    }

    // Default setScore case - returns number of points equal to length of challenge
    public void setScore(){
        this.score = (int) challengeDistance;
    }

    /**
     * Method used to calculate the distance in meters, between two LatLng locations
     *
     * @param Point1 First LatLng point
     * @param Point2 Second LatLng point
     * @return Returns distance in meters between the two points
     */
    final protected double CalculateDistanceBetweenPoints(LatLng Point1, LatLng Point2){

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
     * Method used to calculate the distance of a challenge and
     * setup of challenges' poly line.
     *
     * @return Returns challenge length in meters as double
     */
    final protected double ChallengeLength(){

        List<LatLng> challengeRoutePoints = this.challengeRoute.getPoints();

        //Calculating the summary challenge length
        double challengeLength = 0;

        if(!challengeRoutePoints.isEmpty()) {
            for (int i = 0; i<challengeRoutePoints.size(); i++){
                if(i==0){
                    //Do nothing
                }
                else{
                    challengeLength = challengeLength + CalculateDistanceBetweenPoints(challengeRoutePoints.get(i-1),challengeRoutePoints.get(i));
                }
            }
        }
        else {
            /*
                If the List of points is empty, the route is obviously 0.0m long, this piece of code exists to prevent from doing operations
                such as .size() on empty list.
             */
            challengeLength = 0.0;
        }
        return challengeLength; //Returns challenge route length
    }

    /**
     * Overridden toString method
     *
     * @return returns challenge type as string.
     */
    @Override
    public String toString(){
        return "Nieznany challenge";
    }
}
