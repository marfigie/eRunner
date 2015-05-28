package jpwp.project.pg.erunner;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

/**
 * Inherited base class, created as sprint.
 */
public class SprintChallenge extends Challenge {

    public SprintChallenge(int startPoint, int endPoint, LatLng startPointLatLng, LatLng endPointLatLang, Polyline challengeRoute) {
        super(startPoint, endPoint, startPointLatLng, endPointLatLang, challengeRoute);
    }

    /**Method to set score - currently unused*/
    @Override
    public void setScore() {
        this.score = (int)(challengeDistance*1.5);
    }
    /**Overridden toString method*/
    @Override
    public String toString(){
       return "Sprint - " + (int)challengeDistance + " m.";
    }
}
