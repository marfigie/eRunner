package jpwp.project.pg.erunner;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents Routes menu gui and part of logic
 */
public class RoutesMenu extends ActionBarActivity {
    //
    int checkedItemOnList;
    /**Function that is called when activity is being created*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes_menu);

        //Setting up list view property to toggle of multi selection
        ListView listView = (ListView) findViewById(R.id.RoutesList);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        //ListView aa = (ListView) findViewById(R.id.RoutesList);
        //aa.setEmptyView(findViewById(R.id.emptyElement));

        //Buttons listeners & functions
        CheckButtonGotoMainMenu();
        CheckButtonGotoRouteDesigner();
        CheckButtonDeleteRoute();
        CheckListSelectedItem();
    }
    /**Function that is called when the activity is resumed*/
    @Override
    protected void onResume() {
        super.onResume();
        UpdateListView();
    }
    /**Overridden finish method, called when activity is closing*/
    @Override
    public void finish(){
        RouteManager.getInstance().SaveRoutes(this);
        super.finish();
    }
    /**Called when action bar is created*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_routes_menu, menu);
        return true;
    }
    /**Called when item on action bar is selected*/
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
     * Routes Menu Buttons Listeners Section
     */
    /**Basically button listener*/
    private void CheckButtonGotoRouteDesigner(){
        Button routeDesignerButton = (Button)findViewById(R.id.btnGoToRouteDesigner);

        routeDesignerButton.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        Intent tmpintent = new Intent(v.getContext(),RouteDesigner.class);
                        startActivity(tmpintent);
                    }
                }
        );
    }
    /**Basically button listener*/
    private void CheckButtonGotoMainMenu(){
        Button GoToMainMenuButton = (Button)findViewById(R.id.btnGoToMainMenuFromRoutesMenu);

        GoToMainMenuButton.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        finish();
                    }
                }
        );
    }
    /**Basically button listener*/
    private void CheckButtonDeleteRoute(){
        Button DeleteRouteButton = (Button)findViewById(R.id.btnDeleteRoute);

        DeleteRouteButton.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        //if(checkedItemOnList != null){
                        String tmpDeletedRoute = RouteManager.getInstance().getRoutes().get(checkedItemOnList).getRouteName();
                        RouteManager.getInstance().DeleteRoute(checkedItemOnList);
                        UpdateListView();
                        Toast.makeText(RoutesMenu.this, "Usunięto drogę o nazwie: "+tmpDeletedRoute, Toast.LENGTH_LONG).show();
                        //}
                    }
                }
        );
    }
    /**Basically listView item clicks listener*/
    private void CheckListSelectedItem(){
        final ListView listView = (ListView)findViewById(R.id.RoutesList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Changing background color of all items to Transparent
                for (int j = 0; j < parent.getChildCount(); j++)
                    parent.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);

                //Changing the color of selected item to specific one
                view.setBackgroundColor(Color.argb(255,47,49,64));
                checkedItemOnList = (int)id;    //Changing checked item id to clicked item id
                RouteManager.getInstance().setSelectedRoute(checkedItemOnList); //Sending result to Route manager
            }
        }
    );
    }
    /**Updates listView*/
    private void UpdateListView(){
        ListView listViewRoutes = (ListView)findViewById(R.id.RoutesList);
        List<String> routesList = new ArrayList<>();

        for(Route route : RouteManager.getInstance().getRoutes()){
            routesList.add(route.toString());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.routes_listview_layout, routesList);
        listViewRoutes.setAdapter(arrayAdapter);
    }

}
