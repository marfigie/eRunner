package jpwp.project.pg.erunner;


import com.google.android.gms.maps.model.LatLng;
import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

/**
 * "Simple XML" converter class. Converting LatLng to XML nodes.
 */
public class LatLngConverter implements Converter<LatLng> {
    @Override
    public LatLng read(InputNode node) throws Exception {
        return null;
    }
    /**Method that adapts LatLng objects to be serializable*/
    @Override
    public void write(OutputNode node, LatLng value) throws Exception {
        node.setAttribute("Latitude",String.valueOf(value.latitude));
        node.setAttribute("Longitude",String.valueOf(value.longitude));
    }
}
