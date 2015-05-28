package jpwp.project.pg.erunner;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

import java.util.List;

/**
 * "Simple XML" converter class. Converting LatLng to XML nodes.
 */
public class PolyLineConverter implements Converter<Polyline> {
    @Override
    public Polyline read(InputNode node) throws Exception {

        return null;
    }
    /**Method that adapts List<LatLng> objects to be serializable*/
    @Override
    public void write(OutputNode node, Polyline value) throws Exception {

        List<LatLng> pointlist = value.getPoints();

        for(LatLng lng : pointlist) {
            OutputNode pointNode = node.getChild("point");
            pointNode.setAttribute("Latitude",String.valueOf(lng.latitude));
            pointNode.setAttribute("Longitude",String.valueOf(lng.longitude));
        }
    }
}
