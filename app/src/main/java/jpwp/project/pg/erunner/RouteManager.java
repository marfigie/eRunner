package jpwp.project.pg.erunner;

import android.content.Context;
import android.util.Log;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.Transient;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton Responsible for management and on app life cycle storage of routes.
 */
@Root(name="RootElement")
public class RouteManager {

    @Transient
    private static RouteManager instance;
    @ElementList
    private List<Route> routesList = new ArrayList<>();
    @Transient
    private int selectedRoute;

    //This is singleton class thus the constructor is private;
    private RouteManager(){};

    /**
     * Method used to retrieve the only instance of class.
     * If there is no instance it generates it - acts as constructor.
     *
     * @return Returns the instance of this singleton class.
     */
    public static synchronized RouteManager getInstance(){
        if (instance == null){
            instance = new RouteManager();
        }
        return instance;
    }

    /**
     * Method used to add new route to routes list
     *
     * @param route The route object that is meant to be added
     */
    public void AddRoute (Route route){

        this.routesList.add(route);

    }

    /**
     * Method used to delete route from the list.
     *
     * @param routeId Id of the route on list (starts from 0)
     */
    public void DeleteRoute (int routeId){
        this.routesList.remove(routeId);
    }

    /**Returns routes list
     *
     * @return routes list.
     */

    public List<Route> getRoutes (){

        return this.routesList;

    }

    /**
     * Method that saves routes to an XML file
     * Using Google "Simple XML" library to serialize objects to json
     *
     * @param context Context of current activity.
     */
    public void SaveRoutes (Context context) {

        Serializer serializer = new Persister();
        File file = new File(context.getFilesDir(),"eRunnerData.xml");

        try {
            serializer.write(RouteManager.getInstance(), file);
            Log.d("Data save","Data saved in: "+ context.getFilesDir());
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Serializing","Exception occurred when serializing");
        }
    }

    /**
     * Method that reads routes from an JSON text file
     * Using Google GSON to deserialize json file to objects
     */
    public void LoadRoutes (){
        //TODO
    }

    /**
     * Determines which route on the list is selected
     *
     * @param selectedRoute Id of the route to be selected
     */
    public void setSelectedRoute(int selectedRoute){
        this.selectedRoute = selectedRoute;
    }

    /**
     * Returns selected route
     *
     * @return Returns Id of selected route;
     */
    public int getSelectedRoute(){
        return this.selectedRoute;
    }
}
