package jpwp.project.pg.erunner;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Class that transforms simple SurfaceView into live feed from device camera
 */
public class HudCamPreview extends SurfaceView implements SurfaceHolder.Callback {

    //Simple log TAG
    private static final String CAM_TAG = "CamPreview";

    @SuppressWarnings("deprecation")
    private Camera cam;
    private SurfaceHolder holder;

    /**
     * HudCamPreview constructor
     *
     * @param context Current activity Context.
     * @param camera Devices' camera.
     */
    public HudCamPreview(Context context, Camera camera) {
        super(context);
        cam = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        holder = getHolder();
        holder.addCallback(this);

    }

    /**
     * Creates the surface that image from camera will be painted on.
     *
     * @param holder a SurfaceHolder
     */
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            cam.setPreviewDisplay(holder);
            cam.startPreview();
        } catch (IOException e) {
            Log.d(CAM_TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    /**
     * Method that is called when surface is destroyed.
     *
     * @param holder a SurfaceHolder
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    /**
     * Method called when surface has to be changed.
     *
     * @param holder a SurfaceHolder
     * @param format
     * @param w width
     * @param h height
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (holder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            cam.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            cam.setPreviewDisplay(holder);
            cam.startPreview();

        } catch (Exception e){
            Log.d(CAM_TAG, "Error starting camera preview: " + e.getMessage());
        }
    }
}