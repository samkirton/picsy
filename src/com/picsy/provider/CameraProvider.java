package com.picsy.provider;

import android.hardware.Camera;

/**
 * @author samuelkirton
 */
public class CameraProvider {

	/**
	 * Get the camera if it is available
	 * @param	cameraType	front or back facing?
	 * @return	The camera object
	 */
	public static Camera getCamera(int cameraType) {
	    Camera camera = null;
	    
	    try {	    	
	        camera = Camera.open(cameraType); 
	    } catch (Exception e) { 
	    	System.out.println("TEST");
	    }
	    
	    return camera; 
	}
}
