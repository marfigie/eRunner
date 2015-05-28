package jpwp.project.pg.erunner;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

/**
 * Application main activity class, app starts on this activity
 */
public class MainMenu extends FragmentActivity {

    /**
     * Method called when activity is created
     *
     * @param savedInstanceState last State of App
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //Buttons listeners & functions
        CheckButtonQuitClick();
        CheckButtonGoToRouteMenu();
        CheckButtonGoToHud();

    }

    /**
     * Method that inflates action bar menu
     *
     * @param menu given menu
     * @return returns true if executed
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
        return true;
    }

    /**
     * Method that is called when item on action bar is clicked
     *
     * @param item clicked item
     * @return inherited superclass method
     */
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
     * Main Menu buttons section:
     */
    /**Basically button listener*/
   private void CheckButtonQuitClick(){
            Button quitButton = (Button)findViewById(R.id.btnMainMenuQuit);

            quitButton.setOnClickListener(
                    new Button.OnClickListener(){
                public void onClick(View v){
                    finish();
                }
            }
        );
    }
    /**Basically button listener*/
    private void CheckButtonGoToRouteMenu(){
        Button routeButton = (Button)findViewById(R.id.btnMainMenuGoToRoutesMenu);

        routeButton.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        Intent tmpIntent = new Intent(v.getContext(),RoutesMenu.class);
                        startActivity(tmpIntent);
                    }
                }
        );
    }
    /**Basically button listener*/
    private void CheckButtonGoToHud(){
        Button routeButton = (Button)findViewById(R.id.btnGoToHudFromMainMenu);

        routeButton.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        Intent tmpIntent = new Intent(v.getContext(),Hud.class);
                        startActivity(tmpIntent);
                    }
                }
        );
    }
}
